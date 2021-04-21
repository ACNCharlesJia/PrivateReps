package SourceCode.CodeExtensions

import com.navis.argo.ArgoExtractEntity
import com.navis.argo.ArgoExtractField
import com.navis.argo.ContextHelper
import com.navis.argo.business.api.ArgoUtils
import com.navis.argo.business.api.GroovyApi
import com.navis.argo.business.atoms.ChargeableUnitEventTypeEnum
import com.navis.argo.business.extract.ChargeableMarineEvent
import com.navis.argo.business.extract.ChargeableUnitEvent
import com.navis.argo.business.model.ArgoSequenceProvider
import com.navis.argo.business.model.Facility
import com.navis.argo.business.services.IServiceExtract
import com.navis.billing.BillingEntity
import com.navis.billing.BillingField
import com.navis.billing.BillingPropertyKeys
import com.navis.billing.business.api.IBatchInvoiceManager
import com.navis.billing.business.api.ICreditManager
import com.navis.billing.business.api.IInvoiceManager
import com.navis.billing.business.atoms.*
import com.navis.billing.business.calculators.TariffRateCalculator
import com.navis.billing.business.model.*
import com.navis.billing.webservice.BillingInventoryClientServices
import com.navis.framework.business.Roastery
import com.navis.framework.metafields.MetafieldId
import com.navis.framework.persistence.HibernateApi
import com.navis.framework.portal.FieldChanges
import com.navis.framework.portal.Ordering
import com.navis.framework.portal.QueryUtils
import com.navis.framework.portal.query.DomainQuery
import com.navis.framework.portal.query.PredicateFactory
import com.navis.framework.query.common.api.QueryResult
import com.navis.framework.util.BizFailure
import com.navis.framework.util.BizViolation
import com.navis.framework.util.DateUtil
import org.apache.log4j.Logger
import org.hibernate.Query
import org.hibernate.classic.Session

import java.text.SimpleDateFormat

/**
 * MTL Auto Credit and Re-bill.
 *
 * Authors: <a href="mailto:Mugunthan.Selvaraj@navis.com">Mugunthan Selvaraj</a>
 * Date: 26 Aug 2015
 * JIRA: CSDV-3190
 * SFDC: NA
 * Called from: The core logic to do ACR with input having an invoice entity and CUE/CME gkeys
 *
 * S.no   Modified Date      Modified By          Jira Id    SFDC      Change Description
 *
 * Modified by:     Henry Cheng
 * Modified Date:   Dec 8, 2017
 * Description:     CDG170142 - if CN final no. missing, use next sequence no. at Complex level
 */
class MTLN4BillingACRHandlerCore extends GroovyApi {

    /**
     * The parameters should be having following values:
     *  1. invoice to do ACR
     *  2. corresponding CME or CUE gkeys
     *  3. output result string.
     * @param parameters
     */
    public void execute(Map parameters) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));

        Invoice parentInvoice = (Invoice) parameters.get("INVOICE");
        boolean rebillReqd = (Boolean) parameters.get("rebillReqd");
        String triggerDateString = (String) parameters.get("triggerDate");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date triggerDate = ArgoUtils.timeNow();
        if (triggerDateString != null && !triggerDateString.isEmpty()) {
            triggerDate = sdf.parse(triggerDateString);
        }
        StringBuilder result = new StringBuilder();
        Set<InvoiceItem> invoiceItems = parentInvoice.getInvoiceInvoiceItems();
        List invoiceItemsToBeCredited = new ArrayList<>();
        Long invoiceDraftNo = parentInvoice.getInvoiceDraftNbr();

        List itemEventGkeys = (List) parameters.get("EVENT_GKEYS");
        String eventEntity;

        ExtractEntityEnum extractType = parentInvoice.getInvoiceInvoiceType().getInvtypeAppliedToClass();

        if (extractType.equals(ExtractEntityEnum.INV)) {
            LOGGER.debug("Parameters belong to ChargeableUnitEvents");
            eventEntity = ArgoExtractEntity.CHARGEABLE_UNIT_EVENT;
        } else {
            LOGGER.debug("Parameters does not belong to ChargeableUnitEvents");
        }

        if (extractType.equals(ExtractEntityEnum.MARINE)) {
            LOGGER.debug("Parameters belong to ChargeableMarineEvents");
            eventEntity = ArgoExtractEntity.CHARGEABLE_MARINE_EVENT;
        } else {
            LOGGER.debug("Parameters does not belong to ChargeableMarineEvents");
        }

        if (eventEntity == null) {
            LOGGER.error("Cannot find suitable event entity for the JSON data");
            parameters.put(RESULT_STRING, "Event type unknown");
            return;
        }

        Map<Long, String> storageEqIdAndExtractGkey = new HashMap<>();
        Map<Long, String> reeferEqIdAndExtractGkey = new HashMap<>();
        List storageExtractGkeyList = new ArrayList();
        List<InvoiceItem> timeBasedInvoiceItemList = new ArrayList<>();
        //Check whether the gkeys of this invoice items matches with the input gkeys
        for (InvoiceItem invoiceItem : invoiceItems) {
            if (itemEventGkeys.contains(invoiceItem.getItemServiceExtractGkey())) {
                //Special Handling for Storage
                if (invoiceItem.getItemEventTypeId() != null
                        && (("STORAGE").equals(invoiceItem.getItemEventTypeId()) || ("REEFER").equals(invoiceItem.getItemEventTypeId()))) {
                    Long extractGkey = invoiceItem.getItemServiceExtractGkey();
                    //In Case of multiple tier, loading one time is enough!!
                    if (("REEFER").equals(invoiceItem.getItemEventTypeId())) {
                        if (reeferEqIdAndExtractGkey != null && reeferEqIdAndExtractGkey.get(extractGkey) == null) {
                            List<InvoiceItem> invoiceItemListForExtractGkey = getInvoiceItemListForExtractGkey(extractGkey);
                            reeferEqIdAndExtractGkey.put(extractGkey, invoiceItem.getItemEventEntityId());
                            timeBasedInvoiceItemList.addAll(invoiceItemListForExtractGkey);
                        }
                    } else {
                        if (storageEqIdAndExtractGkey != null && storageEqIdAndExtractGkey.get(extractGkey) == null) {
                            List<InvoiceItem> invoiceItemListForExtractGkey = getInvoiceItemListForExtractGkey(extractGkey);
                            storageEqIdAndExtractGkey.put(extractGkey, invoiceItem.getItemEventEntityId());
                            timeBasedInvoiceItemList.addAll(invoiceItemListForExtractGkey);
                        }
                    }
                } else {
                    invoiceItemsToBeCredited.add(invoiceItem);
                }
            }
        }

        /**=== CORE logic===
         * 1. Create a credit for each invoice item
         * 2. finalize it
         * 3. update the event status in N4 to QUEUED
         * 4. create batch invoices
         * 5. execute batch
         * 6. finalize batch invoice
         * //TODO update to SAP
         * if executed for huge data multiple invoice
         * if fails for the one invoice data,  leave it continue with the remaining
         */
        if (invoiceItemsToBeCredited.size() > 0) {
            LOGGER.debug("No. of invoice items to be credited:" + invoiceItemsToBeCredited.size());
            if (parentInvoice.getInvoiceStatus().equals(InvoiceStatusEnum.FINAL)) {
                processAutoCreditAndRebill(parentInvoice, invoiceItemsToBeCredited, eventEntity, itemEventGkeys, false, result, rebillReqd, triggerDate);
            } else {
                String msg = "Invoice cannot be credited as it is not finalised: Draft No:" +
                        parentInvoice.getInvoiceDraftNbr() + " with status " + parentInvoice.getInvoiceStatus().getKey();
                LOGGER.error(msg);
                result.append("\n" + msg);
            }
        }

        /**
         * STORAGE ACR IMPLEMENTATION
         * Each invoice can have multiple storage invoice item tier
         * Each invoice can have multiple storage invoice item belongs to multiple UFV's
         * Each invoice can have both REEFER and STORAGE
         * Paid through day should be the latest one
         */

        if (timeBasedInvoiceItemList != null & !timeBasedInvoiceItemList.isEmpty() && timeBasedInvoiceItemList.size() > 0) {
            LOGGER.debug("No. of STORAGE/REEFER invoice items to be credited:" + timeBasedInvoiceItemList.size());
            processACRForStorageAndReefer(storageEqIdAndExtractGkey, reeferEqIdAndExtractGkey, timeBasedInvoiceItemList, result, rebillReqd, triggerDate);
        }

        if (invoiceItemsToBeCredited.size() == 0 && timeBasedInvoiceItemList.size() == 0) {
            String msg = "No invoice items to be credited for the invoice draft no.:" + parentInvoice.getInvoiceDraftNbr();
            LOGGER.warn(msg);
            result.append(msg);
        }

        StringBuilder resultString = (StringBuilder) parameters.get(RESULT_STRING);
        resultString.append(result);
        parameters.put(RESULT_STRING, resultString);
        LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }

    private void processACRForStorageAndReefer(Map storageEqIdAndExtractGkey, Map reeferEqIdAndExtractGkey, List<InvoiceItem> invoiceItemList, StringBuilder result, boolean rebillReqd, Date triggerDate) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        List extractGkeys = new ArrayList();
        if (storageEqIdAndExtractGkey != null && storageEqIdAndExtractGkey.keySet() != null && storageEqIdAndExtractGkey.keySet().size() > 0) {
            LOGGER.debug("No. of storage invoice items to be credited:" + storageEqIdAndExtractGkey.keySet().size());
            extractGkeys.addAll(storageEqIdAndExtractGkey.keySet());
        }
        if (reeferEqIdAndExtractGkey != null && reeferEqIdAndExtractGkey.keySet() != null && reeferEqIdAndExtractGkey.keySet().size() > 0) {
            LOGGER.debug("No. of reefer invoice items to be credited:" + storageEqIdAndExtractGkey.keySet().size());
            extractGkeys.addAll(reeferEqIdAndExtractGkey.keySet());
        }

        //Add invoice Map in its created order.
        Map<Long, List<InvoiceItem>> invoiceListMap = new TreeMap<>();
        List list = new ArrayList();
        for (InvoiceItem invoiceItem1 : invoiceItemList) {
            Invoice invoice = invoiceItem1.getItemInvoice();
            list = invoiceListMap.get(invoice.getInvoiceGkey());
            if (list == null) {
                list = new ArrayList<InvoiceItem>();
                invoiceListMap.put(invoice.getInvoiceGkey(), list);
            }
            list.add(invoiceItem1);
        }

        //update paid through day to 1970/01/01
        for (eqAndItsExtractGkey in storageEqIdAndExtractGkey) {
            LOGGER.debug("STORAGE :: Updating Paid through day for Equipment :: " + eqAndItsExtractGkey.key + ", and its Extract Gkey ::" + eqAndItsExtractGkey.value);
            ChargeableUnitEventTypeEnum eventType = ChargeableUnitEventTypeEnum.getEnum("STORAGE");
            updatePaidThroughDay(eqAndItsExtractGkey.key, eqAndItsExtractGkey.value, eventType);
        }

        //update power paid through day to 1970/01/01
        for (eqAndItsExtractGkey in reeferEqIdAndExtractGkey) {
            LOGGER.debug("REEFER :: Updating Paid through day for Equipment :: " + eqAndItsExtractGkey.key + ", and its Extract Gkey ::" + eqAndItsExtractGkey.value);
            ChargeableUnitEventTypeEnum eventType = ChargeableUnitEventTypeEnum.getEnum("REEFER");
            updatePaidThroughDay(eqAndItsExtractGkey.key, eqAndItsExtractGkey.value, eventType);
        }

        //Update CUE to QUEUED
        updateServiceEvents(extractGkeys, ArgoExtractEntity.CHARGEABLE_UNIT_EVENT, "QUEUED");

        //NavigableMap<Long, List<InvoiceItem>> navigableMap = invoiceListMap.descendingMap();
        for (Long gkey : invoiceListMap.keySet()) {
            Invoice invoice = Invoice.hydrate(gkey);
            if (invoice != null && InvoiceStatusEnum.FINAL.equals(invoice.getInvoiceStatus())) {
                LOGGER.debug("Processing ACR for STORAGE/REEFER Invoice Draft Number:: " + invoice.getInvoiceDraftNbr());
                List<InvoiceItem> invoiceItems1 = invoiceListMap.get(gkey);
                if (invoiceItems1 != null && invoiceItems1.size() > 0) {
                    LOGGER.debug("Processing ACR for STORAGE/REEFER and the invoice item size:: " + invoiceItems1.size());
                    processAutoCreditAndRebill(invoice, invoiceItems1, ArgoExtractEntity.CHARGEABLE_UNIT_EVENT, extractGkeys, true, result, rebillReqd, triggerDate);
                }
            }
        }
        LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }

    private void processAutoCreditAndRebill(Invoice parentInvoice, List<InvoiceItem> invoiceItemsToBeCredited, String eventEntity, List itemEventGkeys, boolean isStorage, StringBuilder result, boolean rebillReqd, Date triggerDate) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        try {
            /*1*/
            Credit credit = createCredits(parentInvoice, invoiceItemsToBeCredited);
            credit.setFieldValue(BillingField.CREDIT_DATE, triggerDate);
            result.append(" Successfully auto-credited");

            /*2*/
            finalizeCredit(credit);
            result.append(" and finalized.");

            LOGGER.info("update the existing status to:s " + "QUEUED");

            if (isStorage==false) {
                /*3*/
                updateServiceEvents(itemEventGkeys, eventEntity, "QUEUED");
            }

            if (rebillReqd==false) {
                LOGGER.info("Rebill not required. Hence returning!!");
                result.append(" Rebill Invoice is not required. ");
                return;
            }

            /*4*/
            BatchInvoice batchInvoice = BatchInvoice.getBatchInvoiceByInvoiceGkey(parentInvoice.getInvoiceGkey());
            if (batchInvoice == null) {
                LOGGER.warn("Batch Invoice not existing for the invoice with draft no: " + parentInvoice.getInvoiceDraftNbr());
                result.append(" But no batch found for the invoice draft no: " + parentInvoice.getInvoiceDraftNbr());
            }
            BatchInvoice currentBatch = createBatchInvoice(batchInvoice, parentInvoice.getInvoiceDraftNbr());
            result.append(" The new batch invoice for these are created with ID: ").append(currentBatch.getBatchId());
        } catch (BizFailure e) {
            String failedMsg = "\n. But cannot complete auto credit and re-bill for Invoice(Draft) " + parentInvoice.getInvoiceDraftNbr() + " due to " + e.getMessage();
            LOGGER.warn(failedMsg);
            LOGGER.debug(e, e);
            result.append(failedMsg);
            //To revert the status to INVOICED
            if (isStorage) {
                updateServiceEvents(itemEventGkeys, eventEntity, "PARTIAL");
            } else {
                updateServiceEvents(itemEventGkeys, eventEntity, "INVOICED");
            }

        } catch (Exception e) {
            String failedMsg = "\n. But cannot complete auto credit and re-bill for Invoice(Draft) " + parentInvoice.getInvoiceDraftNbr() + " due to " + e.getMessage();
            LOGGER.warn(failedMsg);
            LOGGER.debug(e, e);
            result.append(failedMsg);
            //To revert the status to INVOICED
            if (isStorage) {
                updateServiceEvents(itemEventGkeys, eventEntity, "PARTIAL");
            } else {
                updateServiceEvents(itemEventGkeys, eventEntity, "INVOICED");
            }
        }
        LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }

    /**
     * Update paid through day to 1970/01/01
     * @param extractGkey
     * @param equipmentId
     * @param eventType
     */
    private void updatePaidThroughDay(Long extractGkey, String equipmentId, ChargeableUnitEventTypeEnum eventType) {
        try {
            BillingInventoryClientServices wsService = new BillingInventoryClientServices();
            wsService.updatePaidThroughDay(equipmentId, XML_DATE_TIME_ZONE_SIMPLE_DATE_FORMAT.format(getMockDate()), eventType, extractGkey);
        } catch (Exception e) {
            LOGGER.error("IGNORABLE EXCEPTION(IF Event Type!=STORAGE) : Update updatePaidThroughDay Throws exception in Webservice : for Event Type :: " + eventType.getKey() + " ::", e);
        }
    }

    private static Date getMockDate() {
        TimeZone tz = ContextHelper.getThreadUserTimezone();
        Calendar c = Calendar.getInstance(tz);
        c.set(Calendar.DATE, 1);
        c.set(Calendar.MONTH, Calendar.JANUARY);
        c.set(Calendar.YEAR, 1970);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 1);
        return c.getTime();
    }

    /**
     * Method to create the credits for the invoice items
     * @param inInvoice
     * @param invoiceItems
     * @return
     */
    private Credit createCredits(Invoice inInvoice, List<InvoiceItem> invoiceItems) {
        LOGGER.info("creating credits for invoice items found in the data request");
        LOGGER.debug("Parent invoice which is getting credited: Draft No: " + inInvoice.getInvoiceDraftNbr());
        LOGGER.debug("No of invoice items which are are getting credited: " + invoiceItems.size());

        HibernateApi api = Roastery.getHibernateApi();
        Credit credit = null;
        Set<CreditItem> creditItemList = new HashSet<CreditItem>()
        ArrayList<InvoiceItem> debitCreditItemList = new ArrayList<InvoiceItem>()
        for (InvoiceItem invoiceItem : invoiceItems) {
            try{
                if (invoiceItem != null) {
                    if (credit == null) {
                        credit = createCreditHeader(inInvoice);
                        credit.creditIsOpenCredit = true
                    }
                    // create partial credit items if the invoice items are already having partial credit items
                    if (hasCredits(invoiceItem)) {
                        LOGGER.debug("Invoice items have existing credits\nSo creating partial credits");
                        CreditItem creditItem = createPartialCredit(credit, invoiceItem)
                        creditItemList.add(creditItem)
                    } else {
                        // create full credit for the invoice items.
                        LOGGER.debug("Creating complete new credits");
                        CreditItem creditItem = createCreditItem(credit, invoiceItem, invoiceItem.getItemQuantity(), invoiceItem.getItemTariffRate().getRateAmount(),
                                invoiceItem.getItemAmount(), CreditByEnum.AMOUNT, "Created by MTL AutoCredit and Re-bill");
                        creditItemList.add(creditItem);
                    }

                    if(invoiceItem.getItemNotes() != null && invoiceItem.getItemNotes().startsWith("Discount Rate")){
                        debitCreditItemList.add(invoiceItem)
                    }
                }
            }catch (Exception e){
                LOGGER.error("Credit item created failed: " + e.getMessage())
                continue
            }
        }

        if(debitCreditItemList.size() > 0){
            Credit mcCredit = findMcCredit(inInvoice)
            debitCredit(inInvoice,debitCreditItemList,mcCredit)
        }

        return credit;
    }

    Credit findMcCredit(Invoice inInvoice){
        Credit credit = null
        try{
            Long invoiceGkey = inInvoice.getInvoiceGkey()
            String hql = "select itm.crditmCredit from CreditItem itm " +
                    "where itm.crditmInvoice.invoiceGkey = $invoiceGkey " +
                    "and itm.crditmCredit.creditNotes like 'DCB credit for paper rate invoice%'"
            LOGGER.debug("hql : $hql")

            HibernateApi hbrapi = Roastery.getHibernateApi()
            Query query = hbrapi.createQuery(hql)
            String sql = query.getQueryString()
            LOGGER.debug("sql : " + sql)

            List<Credit> creditList = query.list()
            if(creditList != null && creditList.size()>0){
                credit = creditList.get(0)
            }
        }catch (Exception e){
            LOGGER.error("Couldn't find MC Credit : " + e.getMessage())
        }finally{
            return credit
        }
    }

    void debitCredit(Invoice inInvoice, List<InvoiceItem> debitCreditItemList, Credit credit){
        LOGGER.info("Decbit credit start()")

        //1. Create Invoice Header
        Invoice invoice = null
        try{
            Customer payeeCustomer = inInvoice.getInvoicePayeeCustomer()
            Date effectiveDate = inInvoice.getInvoiceEffectiveDate()
            Currency currency = inInvoice.getInvoiceCurrency()
            InvoiceType invoiceType = InvoiceType.findInvoiceType("DCB_DEBIT_CREDITS")
            if (invoiceType != null && payeeCustomer != null && currency != null) {
                invoice = Invoice.createInvoice(effectiveDate, invoiceType, payeeCustomer, payeeCustomer, currency)
                invoice.setInvoiceFacility(inInvoice.getInvoiceFacility())
                invoice.setInvoiceContractCustomer(inInvoice.getInvoiceContractCustomer())
                Set<InvoiceParmValue> inParms = inInvoice.getInvoiceParmValues()
                inParms.each {
                    InvoiceParmValue.createInvoiceParmValue(invoice, it.getInvparmMetafield(), it.getInvparmReportableFieldValue(), it.getInvparmValue())
                }

                InvoiceMessage.registerInfo(invoice, "Debit Credit created by auto ACR for invoice Draft Nbr : "
                        + inInvoice.getInvoiceDraftNbr())
            }
        }catch (Exception e){
            LOGGER.error("Invoice Header create failed : " + e.getMessage())
            return
        }

        //2. Create Invoice Item
        if(invoice != null && debitCreditItemList.size() > 0){
            Set invoiceItems = invoice.getInvoiceInvoiceItems()
            if (invoiceItems == null) {
                invoiceItems = new HashSet()
            }

            debitCreditItemList.each { debitCreditItem ->
                try{
                    InvoiceItem invoiceItem = new InvoiceItem()
                    String inKey = inInvoice.getInvoiceInvoiceType().getInvtypeAppliedToClass().getKey()
                    FieldChanges fieldChanges = new FieldChanges()

                    Long invoiceGkey = inInvoice.getInvoiceGkey()
                    Long serviceExtractGkey = debitCreditItem.itemServiceExtractGkey
                    String discountTariffId = debitCreditItem.itemTariffRate.rateTariff.getTariffId()
                    InvoiceItem invoiceBasicItem = findBasicInvoiceItem(invoiceGkey,serviceExtractGkey,discountTariffId)
                    CurrencyExchangeRate currencyExchangeRate = debitCreditItem.getItemExchangeRate()
                    String itemNotes = debitCreditItem.getItemNotes()
                    Double discount = itemNotes.split(":")[1].trim().toDouble()

                    fieldChanges.setFieldChange(BillingField.ITEM_SERVICE_EXTRACT_REF_ID, invoiceBasicItem.getItemServiceExtractRefId())
                    fieldChanges.setFieldChange(BillingField.ITEM_SERVICE_EXTRACT_TYPE, ExtractEntityEnum.getEnum(inKey))
                    fieldChanges.setFieldChange(BillingField.ITEM_EVENT_TYPE_ID, invoiceBasicItem.getItemEventTypeId())
                    fieldChanges.setFieldChange(BillingField.ITEM_EVENT_ENTITY_ID, invoiceBasicItem.getItemEventEntityId())
                    fieldChanges.setFieldChange(BillingField.ITEM_DESCRIPTION, invoiceBasicItem.getItemDescription())

                    fieldChanges.setFieldChange(BillingField.ITEM_EXCHANGE_RATE, currencyExchangeRate)
                    fieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, discount)
                    fieldChanges.setFieldChange(BillingField.ITEM_INVOICE, invoice)
                    fieldChanges.setFieldChange(BillingField.ITEM_TARIFF_RATE, invoiceBasicItem.getItemTariffRate())
                    fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY, invoiceBasicItem.getItemQuantity())
                    fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_UNIT, invoiceBasicItem.getItemQuantityUnit())
                    fieldChanges.setFieldChange(BillingField.ITEM_SERVICE_EXTRACT_GKEY, invoiceBasicItem.getItemServiceExtractGkey())
                    fieldChanges.setFieldChange(BillingField.ITEM_PAID_THRU_DAY, invoiceBasicItem.getItemPaidThruDay())
                    fieldChanges.setFieldChange(BillingField.ITEM_PREV_PAID_THRU_DAY, invoiceBasicItem.getItemPrevPaidThruDay())
                    fieldChanges.setFieldChange(BillingField.ITEM_NOTES, "")
                    fieldChanges.setFieldChange(BillingField.ITEM_FROM_DATE, invoiceBasicItem.getItemFromDate())

                    TariffRate tariffRate =  invoiceBasicItem.itemTariffRate
                    if (tariffRate.getRateCustomerTariffId() != null) {
                        fieldChanges.setFieldChange(BillingField.ITEM_CUSTOMER_TARIFF_ID, tariffRate.getRateCustomerTariffId())
                    } else {
                        fieldChanges.setFieldChange(BillingField.ITEM_CUSTOMER_TARIFF_ID, tariffRate.getRateTariff().getTariffCustomerTariffId())
                    }

                    if (RateTypeEnum.BAND.equals(tariffRate.getRateType()) || RateTypeEnum.CUSTOM_RATE.equals(tariffRate.getRateType())) {
                        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_BILLED, invoiceBasicItem.getItemQuantityBilled());
                        fieldChanges.setFieldChange(BillingField.ITEM_RATE_BILLED, discount);
                        fieldChanges.setFieldChange(BillingField.ITEM_RATE_MAX_AMOUNT, 0D);
                        fieldChanges.setFieldChange(BillingField.ITEM_RATE_MIN_AMOUNT, 0D);
                        fieldChanges.setFieldChange(BillingField.ITEM_RATE_IS_FLAT_RATE, true);
                        if (tariffRate.getRateIsFlatRate()) {
                            fieldChanges.setFieldChange(BillingField.ITEM_FLAT_RATE_AMOUNT, discount)
                        }

                        if (tariffRate.getRateGlCode() != null) {
                            fieldChanges.setFieldChange(BillingField.ITEM_GL_CODE, tariffRate.getRateGlCode());
                        } else {
                            fieldChanges.setFieldChange(BillingField.ITEM_GL_CODE, tariffRate.getRateTariff().getTariffGlCode());
                        }
                    }

                    if (currencyExchangeRate != null) {
                        fieldChanges.setFieldChange(BillingField.ITEM_X_RATE_RATE, currencyExchangeRate.getXrateRate());
                    }

                    invoiceItem.applyFieldChanges(fieldChanges)
                    HibernateApi.getInstance().save(invoiceItem)
                    invoiceItems.add(invoiceItem)

                }catch (Exception e){
                    LOGGER.error("Debit credit item created failed() " + e.getMessage())
                }
            }

            if(invoiceItems.size() > 0){
                invoice.setInvoiceInvoiceItems(invoiceItems)
            }
        }

        //3. Finalize Invoice
        try{
            if(invoice != null && invoice.invoiceInvoiceItems != null && invoice.invoiceInvoiceItems.size() > 0){
                ArgoSequenceProvider provider = new BillingSequenceProvider();
                String finalNbr = provider.getNextSeqValue("INVOICE_FINAL", invoice.getLogEntityComplex().getCpxGkey()).toString()
                invoice.setInvoiceStatus(InvoiceStatusEnum.FINAL)
                invoice.setInvoiceFinalNbr(finalNbr)
                if(credit != null){
                    invoice.setFieldValue(BillingField.INVOICE_FLEX_LONG01, credit.getCreditDraftNbr())
                }
                HibernateApi.getInstance().save(invoice)
            }
        }catch (Exception e){
            LOGGER.error("Debit credit failed to finalize : " + e.getMessage())
        }

        LOGGER.info("Debit credit end()")
    }

    private InvoiceItem findBasicInvoiceItem(Long invoiceGkey, Long serviceExtractGkey, String discountTariffId){
        try{
            if(discountTariffId.endsWith("_D")){
                discountTariffId = discountTariffId[0..-3]
            }
            String hql = "select itm from InvoiceItem itm " +
                    "where 1=1 and itm.itemInvoice.invoiceGkey = $invoiceGkey " +
                    "and itm.itemServiceExtractGkey = $serviceExtractGkey " +
                    "and itm.itemTariffRate.rateTariff.tariffId = '$discountTariffId'"
            LOGGER.debug("hql : $hql")

            HibernateApi hbrapi = Roastery.getHibernateApi()
            Query query = hbrapi.createQuery(hql)
            String sql = query.getQueryString()
            LOGGER.debug("sql : " + sql)

            List<InvoiceItem> invoiceItemList = query.list()
            if(invoiceItemList != null && invoiceItemList.size()>0){
                return invoiceItemList.get(0)
            }
        }catch (Exception e){
            LOGGER.error("Couldn't find basic invoice item")
            e.printStackTrace()
        }
        return null
    }

    /**
     * Method to create a credit header
     * @param inInvoice
     * @return
     */
    private Credit createCreditHeader(Invoice inInvoice) {
        Customer customer = inInvoice.getInvoicePayeeCustomer();
        LOGGER.debug("The customer for which the credit header is getting created: " + customer.getCustName());
        List<InvoiceItem> invoiceItemList = inInvoice.getInvoiceInvoiceItems().toList();
        // check if the invoice has items
        // Create the header only if it has existing items
        if ((invoiceItemList == null) || (invoiceItemList.size() == 0)) {
            LOGGER.warn("Cannot create credit header. items are null or zero for the input invoice");
            return null;
        }

        LOGGER.debug("Creating and saving the credit");
        // Credit Header
        Credit credit = Credit.createCredit(new Date(), CreditTypeEnum.OAC, customer, inInvoice.getInvoiceCurrency());
        credit.setCreditDraftNbr(getCreditDraftNbr());
        credit.setCreditStatus(InvoiceStatusEnum.DRAFT);
        credit.setCreditNotes("MTL Auto credit and re-bill created for invoice item under the invoice having the draft no.: " + inInvoice.getInvoiceDraftNbr() +
                              " and Final no.: " + inInvoice.getInvoiceFinalNbr());
        Facility fcy = inInvoice.getInvoiceFacility();
        credit.setCreditComplex(fcy.getFcyComplex());
        credit.setCreditFacility(fcy);

        LOGGER.debug("Credit created successfully and saved in the data base");
        return credit;
    }

    boolean hasCredits(InvoiceItem inInvoiceItem) {
        DomainQuery dq = QueryUtils.createDomainQuery(BillingEntity.CREDIT_ITEM)
                .addDqPredicate(PredicateFactory.eq(BillingField.CRDITM_INVOICE_ITEM, inInvoiceItem.getItemGkey()))
                .addDqPredicate(PredicateFactory.eq(BillingField.CRDITM_INVOICE, inInvoiceItem.getItemInvoice().getInvoiceGkey()));
        int count = HibernateApi.getInstance().findCountByDomainQuery(dq);
        return count > 0;
    }

    Long getCreditDraftNbr() {
        BillingSequenceProvider seqProvider = new BillingSequenceProvider();
        return seqProvider.getCreditDraftNextSeqValue();
    }

    Long getCreditFinalNbr(Long nGkey) {
        ArgoSequenceProvider provider = new BillingSequenceProvider();
        return provider.getNextSeqValue("CPX_CREDIT_FINAL",nGkey);
    }

    public static CreditItem createCreditItem(Credit inCredit, InvoiceItem inInvItem, Double inQuantity, Double inRate, Double inAmount,
                                       CreditByEnum inCreditBy, String inNotes) {
        if (inInvItem == null) {
            LOGGER.error("Invoice item is null. cannot create credit item");
            throw BizFailure.create(BillingPropertyKeys.NULL_INVOICE_ITEM, null);
        }
        // validate and throw exception if the user tries to do mixed way of crediting for one invoice item.
        // Only one type of credit is allowed for one invoice item.
        validateCreditByTypeWithExistingCredits(inInvItem, inCreditBy);

        CurrencyExchangeRate currencyExchangeRate = inInvItem.getItemExchangeRate();

        Double dQuantity, dRate, dAmount;
        if (CreditByEnum.AMOUNT.equals(inCreditBy) && inAmount != null) {
            //    crditm.setCrditmQuantity(0.0);
            dQuantity = inQuantity;
            dRate = null;
            dAmount = null;
        } else if (inQuantity != null) {
            dQuantity = inQuantity;
            dRate = CreditByEnum.QUANTITY.equals(inCreditBy) ? null : inInvItem.getItemRateBilled();
            CreditManagerImpl cmgr = new CreditManagerImpl();
            dAmount = cmgr.getCreditAmount(inInvItem, dQuantity);
        } else if (inRate != null) {
            dQuantity = inInvItem.getItemQuantity();
            dRate = inRate;
            dAmount = dRate * dQuantity;
            if (currencyExchangeRate != null) {
                dAmount = TariffRateCalculator.applyExchangeRateToAmount(currencyExchangeRate, dAmount);
            }
        } else {
            dQuantity = inInvItem.getItemQuantity();
            dRate = inInvItem.getItemRateBilled();
            dAmount = inInvItem.getItemAmount();
        }

        CreditItem crditm = CreditItem.createCreditItem(inCredit, inInvItem, dQuantity, dRate, dAmount, inCreditBy, inNotes);

        crditm.setCrditmExchangeRate(currencyExchangeRate);
        //now create invoice item taxes as Credit invoice taxes.
        if (!inInvItem.getItemTaxes().isEmpty()) {
            CreditItemTax.createCreditItemTax(crditm, inInvItem);
        }

        return crditm;
    }

    private static void validateCreditByTypeWithExistingCredits(InvoiceItem inInvItem, CreditByEnum inCreditBy) throws BizFailure {
        DomainQuery dq = QueryUtils.createDomainQuery(BillingEntity.CREDIT_ITEM)
                .addDqPredicate(PredicateFactory.eq(BillingField.CRDITM_INVOICE_ITEM, inInvItem.getItemGkey()))
                .addDqPredicate(PredicateFactory.ne(BillingField.CRDITM_CREDIT_BY, inCreditBy));
        if (Roastery.getHibernateApi().existsByDomainQuery(dq)) {
            dq.addDqField(BillingField.CRDITM_CREDIT_BY);
            QueryResult queryResult = Roastery.getHibernateApi().findValuesByDomainQuery(dq);
            String prevCreditBy = ((CreditByEnum) queryResult.getValue(0, 0)).getKey();
            String creditBy = inCreditBy == null ? null : inCreditBy.getKey();
            throw BizFailure.create(BillingPropertyKeys.PARTIAL_CREDIT_SHOULD_USE_SAME_CREDIT_BY_METHOD, null, creditBy, prevCreditBy);
        }
    }

    /**
     * Helper method to create partial credit
     * @param inCredit
     * @param inInvoiceItem
     * @return
     */
    private CreditItem createPartialCredit(Credit inCredit, InvoiceItem inInvoiceItem) {
        CreditItem creditItem = null;
        if (inInvoiceItem == null) {
            LOGGER.warn("inInvoiceItem is null !");
            return null;
        }

        CreditItem previousCreditItem = getLatestCreditItem(inInvoiceItem);

        if (previousCreditItem != null) {
            CreditByEnum previousCreditBy = previousCreditItem.getCrditmCreditBy();
            LOGGER.info("Previously Credited By: " + previousCreditBy.getKey() + " for container ID: " + inInvoiceItem.getItemEventEntityId());
            if (CreditByEnum.QUANTITY.equals(previousCreditBy)) {
                LOGGER.debug("Auto credit by quantity");
                Double remainingQuantity = inInvoiceItem.getItemQuantityBilled() - previousCreditItem.getCrditmQuantity();
                if (remainingQuantity > 0) {
                    creditItem = createCreditItem(inCredit, inInvoiceItem, remainingQuantity, null, null, CreditByEnum.QUANTITY,
                            "Created by (QTY) MTL Auto credit and re-bill");
                    creditItem.setFieldValue(BillingField.CRDITM_RATE, null);
                }
            } else if (CreditByEnum.AMOUNT.equals(previousCreditBy)) {
                LOGGER.debug("Auto credit by amount");
                Double remainingAmount = inInvoiceItem.getItemAmount() - previousCreditItem.getCrditmAmount();
                if (remainingAmount > 0) {
                    creditItem = createCreditItem(inCredit, inInvoiceItem, null, null, remainingAmount, CreditByEnum.AMOUNT,
                            "Created by (AMT)MTL Auto credit and re-bill");
                }
            } else if (CreditByEnum.RATE.equals(previousCreditBy)) {
                LOGGER.debug("Auto credit by rate");
                Double remainingRate = inInvoiceItem.getItemRateBilled() - previousCreditItem.getCrditmRate();
                if (remainingRate > 0) {
                    creditItem = createCreditItem(inCredit, inInvoiceItem, null, remainingRate, null, CreditByEnum.RATE,
                            "Created by (RATE) MTL Auto credit and re-bill");
                }
            }
        }
        return creditItem;
    }

    CreditItem getLatestCreditItem(InvoiceItem inInvoiceItem) {
        DomainQuery dq = QueryUtils.createDomainQuery(BillingEntity.CREDIT_ITEM)
                .addDqPredicate(PredicateFactory.eq(BillingField.CRDITM_INVOICE_ITEM, inInvoiceItem.getItemGkey()))
                .addDqPredicate(PredicateFactory.eq(BillingField.CRDITM_INVOICE, inInvoiceItem.getItemInvoice().getInvoiceGkey()))
                .addDqOrdering(Ordering.desc(BillingField.CRDITM_CREATED));

        Serializable[] creditItemGkeys = Roastery.getHibernateApi().findPrimaryKeysByDomainQuery(dq);
        if (creditItemGkeys.length > 0) {
            return (CreditItem) Roastery.getHibernateApi().load(CreditItem.class, creditItemGkeys[0]);
        }
        LOGGER.warn("No latest credit items exists for invoice item with Gkey:" + inInvoiceItem.getItemGkey());
        return null;
    }

    /*************************finalize credit *************************/
    /**
     * Method to finalize credit
     * @param inCredit
     */
    private void finalizeCredit(Credit inCredit) {
        LOGGER.debug("Finalizing the credit-Gkey: " + inCredit.getCreditGkey());
        ICreditManager creditManager = (ICreditManager) Roastery.getBean(ICreditManager.BEAN_ID);
        if (creditManager == null) {
            LOGGER.error("Cannot load CreditManager!");
            return;
        }

        try {
            creditManager.finalizeCredit(inCredit);
        } catch (Exception ex) {
            LOGGER.error("Cannot Finalize Credit with Draft Nbr: " + inCredit.getCreditDraftNbr() + " due to .. " + ex, ex);
        }

        if (inCredit.getCreditFinalNbr() == null) {
            inCredit.creditIsOpenCredit = false
            inCredit.setCreditFinalNbr(getCreditFinalNbr(inCredit.getCreditComplex().getCpxGkey()).toString());
        }
    }

    /*************************Change the service events status *************************/
    /**
     * To update the event status
     * @param eventGkeys
     * @param eventEntity
     */
    private void updateServiceEvents(List eventGkeys, String eventEntity, String status) {
        LOGGER.info("Updating the event status for the entity: " + eventEntity);

        if (status == null) {
            LOGGER.error("The input status to update cannot be null");
            return;
        }
        LOGGER.debug("Setting status as " + status + " for gkeys ::" + eventGkeys);
        Session extractSession = null;
        try {
            extractSession = ExtractHibernateApi.getInstance().beginExtractSession();

            String extractClassName;
            MetafieldId metafieldId;
            if (eventEntity == ArgoExtractEntity.CHARGEABLE_MARINE_EVENT) {
                extractClassName = "com.navis.argo.business.extract.ChargeableMarineEvent";
                metafieldId = ArgoExtractField.BEXM_GKEY;
            } else if (eventEntity == ArgoExtractEntity.CHARGEABLE_UNIT_EVENT) {
                extractClassName = "com.navis.argo.business.extract.ChargeableUnitEvent";
                metafieldId = ArgoExtractField.BEXU_GKEY;
            }
            Class<?> extractClass = Class.forName(extractClassName);
            String dc = extractClass.getName();
            String extractEntity = dc.substring(dc.lastIndexOf('.') + 1);

            IServiceExtract templateEvent = (IServiceExtract) extractClass.newInstance();
            DomainQuery dq = QueryUtils.createDomainQuery(extractEntity)
                    .addDqPredicate(PredicateFactory.in(metafieldId, eventGkeys));
            List<IServiceExtract> eventList = ExtractHibernateApi.getInstance().executeExtractDomainQuery(dq);

            for (Object o : eventList) {
                IServiceExtract nextEvent = (IServiceExtract) o;
                if (nextEvent instanceof ChargeableUnitEvent) {
                    ((ChargeableUnitEvent) nextEvent).setBexuStatus(status);
                } else if (nextEvent instanceof ChargeableMarineEvent) {
                    ((ChargeableMarineEvent) nextEvent).setBexmStatus(status);
                }
            }
            extractSession.getTransaction().commit();
        } catch (Throwable t) {
            LOGGER.error("Problem when updating the extract class", t);
            ExtractHibernateApi.getInstance().rollbackTransaction(extractSession, t);
        } finally {
            ExtractHibernateApi.getInstance().endExtractSession(extractSession);
        }
    }
    /**
     * method to create batch invoice. The id, description are generated from the parent invoice.
     * param values and other parameters are considered from old batch invoice
     * @param batchInvoice
     * @param parentInvoiceDraftNo
     * @return
     */
    private BatchInvoice createBatchInvoice(BatchInvoice batchInvoice, Long parentInvoiceDraftNo) {
        LOGGER.info("Creating batch invoice for item under parent invoice with draft no.:" + parentInvoiceDraftNo);
        if (null == batchInvoice) {
            return null;
        }

        String batchInvoiceId = "ACR_INVDR_" + parentInvoiceDraftNo;
        String batchDescription = batchInvoiceId + " created after credit";

        InvoiceType invoiceType = batchInvoice.getBatchInvoiceType();

        BatchInvoice currentBatch = BatchInvoice.findOrCreateBatchInvoice(batchInvoiceId, batchDescription,
                ContextHelper.getThreadComplex(), invoiceType, batchInvoice.getBatchPayeeCustomer()
                , batchInvoice.getBatchContractCustomer(), batchInvoice.getBatchCurrency(), batchInvoice.getBatchEffectiveDate());
        currentBatch.setFieldValue(BillingField.BATCH_NOTES, batchDescription + "added automatically");
        currentBatch.setFieldValue(BillingField.BATCH_FILTER, batchInvoice.getBatchFilter());
        currentBatch.setFieldValue(BillingField.BATCH_STATUS, "QUEUED");
        Serializable gkey = HibernateApi.getInstance().save(currentBatch);
        HibernateApi.getInstance().flush();
        LOGGER.info("Batch invoice created successfully with gkey: " + gkey);
        return (BatchInvoice) HibernateApi.getInstance().load(BatchInvoice.class, gkey);
    }

    /**
     * Standalone method to execute the batch invoice created
     * @param currentBatch
     * @return
     */
    private Invoice executeBatchInvoice(BatchInvoice currentBatch) {
        LOGGER.info("Executing the new batch invoice created: Batch ID" + currentBatch.getBatchId());

        LOGGER.debug("Setting the batch invoice status to COMPLETE as it will be triggered by current code and it should not be executed by application");
        currentBatch.setBatchStatus(InvoiceBatchStatusEnum.COMPLETE);
        HibernateApi.getInstance().refresh(currentBatch);
        IBatchInvoiceManager batchManager = (IBatchInvoiceManager) Roastery.getBean(IBatchInvoiceManager.BEAN_ID);
        LOGGER.debug("Calling N4 Billing API to generate invoice");
        Invoice currentBatchInvoice = batchManager.generateBatchInvoice(currentBatch);
        LOGGER.info("New batch invoice created with draft no.: " + currentBatchInvoice.getInvoiceDraftNbr());
        return currentBatchInvoice;
    }

    /**
     * To finalize the invoice
     * @param invoice
     * @param inFinalizeDate
     * @throws BizViolation
     */
    private void finalizeInvoice(Invoice invoice, Date inFinalizeDate) throws BizViolation {
        IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
        invoice.setInvoiceFinalizedDate(inFinalizeDate);
        invoiceManager.doFinalize(invoice);
    }

    private List<InvoiceItem> getInvoiceItemListForExtractGkey(Long extractGkey) {
        List<InvoiceItem> returnList = new ArrayList<>();
        DomainQuery dq = QueryUtils.createDomainQuery(BillingEntity.INVOICE_ITEM)
                .addDqPredicate(PredicateFactory.eq(BillingField.ITEM_SERVICE_EXTRACT_GKEY, extractGkey))
                .addDqPredicate(PredicateFactory.gt(BillingField.ITEM_AMOUNT, 0D));
        //.addDqPredicate(PredicateFactory.isNull(BillingField.INVOICE_CREDITS));
        List<InvoiceItem> allInvoiceItems = HibernateApi.getInstance().findEntitiesByDomainQuery(dq);
        for (InvoiceItem invoiceItem : allInvoiceItems) {
            if (!hasCredits(invoiceItem)) {
                returnList.add(invoiceItem);
            }
        }
        return returnList;
    }

    private static final SimpleDateFormat XML_DATE_TIME_ZONE_SIMPLE_DATE_FORMAT = new SimpleDateFormat(DateUtil.XML_DATE_TIME_ZONE_FORMAT);
    private static final Logger LOGGER = Logger.getLogger(MTLN4BillingACRHandlerCore.class);
    String RESULT_STRING = "RESULT";
}