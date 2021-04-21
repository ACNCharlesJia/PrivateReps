package SourceCode.CodeExtensions

import com.navis.argo.ContextHelper
import com.navis.argo.business.api.ArgoUtils
import com.navis.argo.business.model.ArgoSequenceProvider
import com.navis.argo.business.model.Facility
import com.navis.billing.BillingField
import com.navis.billing.BillingPropertyKeys
import com.navis.billing.business.api.ICreditManager
import com.navis.billing.business.api.IInvoiceManager
import com.navis.billing.business.atoms.CreditByEnum
import com.navis.billing.business.atoms.CreditTypeEnum
import com.navis.billing.business.atoms.InvoiceStatusEnum
import com.navis.billing.business.model.*
import com.navis.external.billing.AbstractInvoicePostInterceptor
import com.navis.framework.business.Roastery
import com.navis.framework.metafields.MetafieldIdFactory
import com.navis.framework.persistence.HibernateApi
import com.navis.framework.util.BizFailure
import org.apache.log4j.Logger
import org.hibernate.Query

public class InvoiceGenerationPostInterceptor extends AbstractInvoicePostInterceptor {
    @Override
    public boolean afterGenerateInvoice(Invoice inInvoice) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        if (inInvoice == null) {
            LOGGER.info("Invoice is null. Can not proceed with finalizing invoice");
            return true;
        }
        if (inInvoice.getInvoiceFlexString01() != null && "Y".equals(inInvoice.getInvoiceFlexString01())) {
            LOGGER.info("Manual Invoice generation is enabled . Hence not finalizing invoice");
            return true;
        }
        Customer payeeCustomer = inInvoice.getInvoicePayeeCustomer();
        if (payeeCustomer != null) {
            String custId = payeeCustomer.getCustId();
            if (custId != null && "UNKNOW PAYEE".equals(custId)) {
                LOGGER.info("Payee is UNKNOWN PAYEE. Skipping finalizing invoice.");
                return true;
            }
        }
        if (inInvoice.getInvoiceInvoiceType().getInvtypeId().startsWith("DCB_CASHIER_")) {
            LOGGER.info("Not to finalize DCB_CASHIER invoice");
            return true;
        }

        if (inInvoice != null && inInvoice.getInvoiceInvoiceItems() != null && inInvoice.getInvoiceInvoiceItems().size() > 0) {
            LOGGER.info("No of invoice items which are are getting finalized : " + inInvoice.getInvoiceInvoiceItems().size());
            if ((inInvoice.getInvoiceTotalTotal() != null) &&
                    (inInvoice.getInvoiceTotalTotal() == 0   ) &&
                    (inInvoice.getInvoiceInvoiceType().getInvtypeId().startsWith("DCB_CASHIER")==false)){
                LOGGER.info("Invoice total amount is zero.");
                boolean validTariffFound = true;
                Set<InvoiceItem> items = inInvoice.getInvoiceInvoiceItems();
                if ((items == null) || items.isEmpty()) {
                    validTariffFound = false;
                } else {
                    for (InvoiceItem invoiceItem : items) {
                        if (invoiceItem.getItemEventTypeId().equals("STORAGE"))
                            continue;

                        TariffRate tariffRate = invoiceItem.getItemTariffRate();
                        if (tariffRate == null) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.info("Tariff Rate is null. Hence breaking the loop");
                            }
                            validTariffFound = false;
                            break;
                        }
                        Tariff tariff = tariffRate.getRateTariff();
                        if (tariff == null) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.info("Tariff is null. Hence breaking the loop");
                            }
                            validTariffFound = false;
                            break;
                        }
                        /*if (invoiceItem.getItemQuantityBilled() == null) {
                            if (LOGGER.isDebugEnabled()) {
                                LOGGER.info("Quantity Billed is null. Hence breaking the loop");
                            }
                            validTariffFound = false;
                            break;
                        }*/
                    }
                }

                if (validTariffFound == false) {
                    LOGGER.info("Valid Tariff ID not found in invoice. Hence returning!!");
                    return true;
                }
            }

            List discountItemList = new ArrayList<>();
            Set<InvoiceItem> items = inInvoice.getInvoiceInvoiceItems();
            for (InvoiceItem invoiceItem : items) {
                String itemNotes = invoiceItem.getItemNotes();
                if(itemNotes != null && !itemNotes.isEmpty() && itemNotes.startsWith("Discount Rate")){
                    discountItemList.add(invoiceItem)
                }
            }
            if(discountItemList.size() > 0){
                LOGGER.info("discount rate items size(): " + discountItemList.size())
                Credit credit = createPaperRateCredits(inInvoice,discountItemList)
                if (credit?.creditFinalNbr?.length() > 0){
                    try{
                        inInvoice.setFieldValue(MetafieldIdFactory.valueOf("invoiceFlexLong03"),
                                credit.creditFinalNbr.toLong())
                    }catch (Exception e){
                        LOGGER.error("MC credit final nbr update failed")
                        e.printStackTrace()
                    }
                }
            }

            IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
            LOGGER.info("Finalizing the invoice with draft number : " + inInvoice.getInvoiceDraftNbr());
            TimeZone timeZone = ContextHelper.getThreadUserTimezone();
            Calendar calendar = Calendar.getInstance(timeZone);
            Date invoiceExecutionDate = calendar.getTime();
            LOGGER.info(" with finalize date " + invoiceExecutionDate);
            inInvoice.setInvoiceFinalizedDate(new Date());
            invoiceManager.doFinalize(inInvoice);
            LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        } else {
            LOGGER.info("Invoice item is null. Can not proceed with finalizing invoice");
        }
        return false;
    }

    private Credit createPaperRateCredits(Invoice invoice, List<InvoiceItem> discountItemList){
        if(invoice == null) return

        Long invoiceGkey = invoice.getPrimaryKey()

        /*
            1. Create draft credit
            2. Get basis invoice item from discount invoice item
            3. Get discount amount
            4. Create credit item
         */
        if (discountItemList == null || discountItemList.size() == 0) return

        //1
        Credit credit = createDraftCredit(invoice)
        List<CreditItem> creditItemList = new ArrayList<CreditItem>()

        for (InvoiceItem discountItem : discountItemList) {
            try{
                String itemNotes = discountItem.getItemNotes()
                Tariff discountTariff = discountItem.itemTariffRate.rateTariff;
                Long serviceExtractGkey = discountItem.itemServiceExtractGkey;
                String discountTariffId = discountTariff.getTariffId()
                LOGGER.debug("Invoice item CUE/CME gkey: $serviceExtractGkey Tariff: $discountTariffId")
                //2
                InvoiceItem invoiceItem = findBasicInvoiceItem(invoiceGkey,serviceExtractGkey, discountTariffId)
                if (invoiceItem == null) continue
                //3
                Double discount = itemNotes.split(":")[1].trim().toDouble()
                LOGGER.debug("discount : $discount")
                //4
                credit.creditIsOpenCredit = true
                LOGGER.error("creditIsOpenCredit: " + credit.creditIsOpenCredit)
                CreditItem creditItem = createCreditItem(credit, invoiceItem, discount, CreditByEnum.AMOUNT, "Created by DCB Paper rate credit");

                creditItemList.add(creditItem);
            }catch (Exception e){
                LOGGER.error("createPaperRateCredits() extract paper rate item " + discountItem.primaryKey + " failed ")
                e.printStackTrace()
                continue
            }
        }

        if(creditItemList == null || creditItemList.size() == 0){
            credit.purge()
            return null
        }

        //finalize credit
        credit.setFieldValue(BillingField.CREDIT_DATE, new Date())
        LOGGER.debug("Finalizing the credit-Gkey: " + credit.getCreditGkey());
        ICreditManager creditManager = (ICreditManager) Roastery.getBean(ICreditManager.BEAN_ID);
        if (creditManager == null) {
            LOGGER.error("Cannot load CreditManager!");
            return;
        }

        try {
            creditManager.finalizeCredit(credit);
        } catch (Exception ex) {
            LOGGER.error("Cannot Finalize Credit with Draft Nbr: " + credit.getCreditDraftNbr() + " due to .. " + ex, ex);
        }

        if (credit.getCreditFinalNbr() == null) {
            ArgoSequenceProvider provider = new BillingSequenceProvider();
            String finalNbr = provider.getNextSeqValue("CPX_CREDIT_FINAL",credit.getCreditComplex().getCpxGkey()).toString()
            credit.setCreditFinalNbr(finalNbr);
            credit.creditIsOpenCredit = false
        }

        return credit
    }

    static CreditItem createCreditItem(Credit inCredit, InvoiceItem inInvItem, Double inAmount,
                                       CreditByEnum inCreditBy, String inNotes) {
        if (inInvItem == null) {
            LOGGER.error("Invoice item is null. cannot create credit item")
            throw BizFailure.create(BillingPropertyKeys.NULL_INVOICE_ITEM, null);
        }

        CreditItem crditm = new CreditItem();
        crditm.setCrditmCredit(inCredit);
        crditm.setCrditmInvoice(inInvItem.getItemInvoice());
        CurrencyExchangeRate currencyExchangeRate = inInvItem.getItemExchangeRate();
        if (currencyExchangeRate != null) {
            crditm.setCrditmExchangeRate(currencyExchangeRate);
        }
        if (CreditByEnum.AMOUNT.equals(inCreditBy) && inAmount != null) {
            crditm.setCrditmAmount(inAmount);
        }
        crditm.setCrditmCreditBy(inCreditBy);
        crditm.setCrditmInvoiceItem(inInvItem);

        if (inNotes != null) {
            crditm.setCrditmNotes(inNotes);
        }
        HibernateApi.getInstance().save(crditm);

        //now create invoice item taxes as Credit invoice taxes.
        if (!inInvItem.getItemTaxes().isEmpty()) {
            CreditItemTax.createCreditItemTax(crditm, inInvItem);
        }
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

            HibernateApi hbrapi = Roastery.getHibernateApi();
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

    private Credit createDraftCredit(Invoice inInvoice){
        Credit credit
        Customer customer = inInvoice.getInvoicePayeeCustomer();
        LOGGER.debug("The customer for which the credit header is getting created: " + customer.getCustName());
        LOGGER.debug("Creating and saving the credit");

        // Credit Header
        try{
            credit = Credit.createCredit(new Date(), CreditTypeEnum.OAC, customer, inInvoice.getInvoiceCurrency());
            BillingSequenceProvider seqProvider = new BillingSequenceProvider()
            Long draftNbr = seqProvider.getCreditDraftNextSeqValue()

            credit.setCreditDraftNbr(draftNbr);
            credit.setCreditStatus(InvoiceStatusEnum.DRAFT);
            credit.setCreditNotes("DCB credit for paper rate invoice item under the invoice having the draft no.: " + inInvoice.getInvoiceDraftNbr() +
                    " and Final no.: " + inInvoice.getInvoiceFinalNbr())
            Facility fcy = inInvoice.getInvoiceFacility()
            credit.setCreditComplex(fcy.getFcyComplex())
            credit.setCreditFacility(fcy);
            Roastery.getHibernateApi().save(credit)
        }catch (Exception e){
            LOGGER.error("Credit create failed : " + e.getMessage())
        }

        LOGGER.debug("Credit created successfully and saved in the database");
        return credit
    }

    private static final Logger LOGGER = Logger.getLogger(InvoiceGenerationPostInterceptor.class);
}