package SourceCode.CodeExtensions;

import com.navis.argo.business.api.ArgoUtils;
import com.navis.billing.BillingEntity;
import com.navis.billing.BillingField;
import com.navis.billing.business.atoms.InvoiceStatusEnum;
import com.navis.billing.business.model.Invoice;
import com.navis.external.framework.persistence.AbstractExtensionPersistenceCallback;
import com.navis.framework.business.Roastery;
import com.navis.framework.persistence.HibernateApi;
import com.navis.framework.portal.QueryUtils;
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.util.BizFailure
import org.apache.log4j.Logger;
import org.hibernate.Query;

import java.text.SimpleDateFormat

/**
 * MTL Initiate Auto Credit and Re-bill from invoice screen.
 *
 * Authors: <a href="mailto:Mugunthan.Selvaraj@navis.com">Mugunthan Selvaraj</a>
 * Date: 26 Aug 2015
 * JIRA: CSDV-3190
 * SFDC: NA
 * Called from: Called from invoice table view command of invoice screen through groovy- MTLInitiateACRForInvoice
 *
 * S.no   Modified Date      Modified By          Jira Id    SFDC      Change Description
 */
class MTLN4BillingInitiateACRForInvoiceTxnBound extends AbstractExtensionPersistenceCallback {
    @Override
    public void execute(Map inParms, Map inOutResults) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        List<Serializable> invoiceGkeys = (List<Serializable>) inParms.get("invoiceGKeys");
        checkForFinalStatus(invoiceGkeys);
        StringBuilder resultString = new StringBuilder();
        boolean isPaymentReceived = false;
        Map params = new HashMap();
        params.put(resultName, resultString);
        List<Invoice> invoiceList = new ArrayList<>();
        StringBuffer paymentReceivedMsg = new StringBuffer();
        paymentReceivedMsg.append("Payment is already received for Invoice draft number :: ");
        for (Serializable invoiceGkey : invoiceGkeys) {
            Invoice invoice = (Invoice) Roastery.getHibernateApi().load(Invoice.class, invoiceGkey);
            Long paymentFlag = invoice.getInvoiceFlexLong02();
            if (paymentFlag != null && paymentFlag == 1) {
                isPaymentReceived = true;
                paymentReceivedMsg.append(invoice.getInvoiceDraftNbr());
                paymentReceivedMsg.append(", ");
            }
            invoiceList.add(invoice);

        }
        if (!isPaymentReceived) {
            for (Invoice invoice : invoiceList) {
                params.put(invoiceName, invoice);
                //Credit Initiated Date
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date currentDate = ArgoUtils.timeNow();
                params.put(triggerDateName, sdf.format(currentDate));
                params.put(REBILL_REQD, true);
                String selectInvExtractGkeys = "select ii.itemServiceExtractGkey from InvoiceItem as ii where ii.itemInvoice.invoiceGkey=:invoiceGkey";
                Query query = HibernateApi.getInstance().createQuery(selectInvExtractGkeys);
                query.setParameter("invoiceGkey", invoice.getInvoiceGkey());
                List invItemsGkeys = query.list();
                LOGGER.debug("Selected the invoice item unique gkeys for the invoice gkey:" + invoice.getInvoiceGkey() + " extract keys:" + invItemsGkeys);
                params.put(invoiceGkeyName, invItemsGkeys);
                if (invItemsGkeys.size() == 0) {
                    String msg = "\nNo invoice items and corresponding extract gkeys found for invoice(draft no): " + invoice.getInvoiceDraftNbr();
                    LOGGER.error(msg);
                    resultString.append(msg);
                } else {
                    LOGGER.debug("Invoking the groovy library for ACR: " + ACR_HANDLER_GROOVY_LIB);
                    Object codeExtLibrary = getLibrary(ACR_HANDLER_GROOVY_LIB);
                    codeExtLibrary.execute(params);
                }
            }
        } else {
            LOGGER.debug("ACR is initiated for Paid invoice.");
            registerError(paymentReceivedMsg.substring(0, paymentReceivedMsg.length() - 2));
        }

        inOutResults.put(resultName, params.get(resultName));
        LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }

    private void checkForFinalStatus(List invoiceGkeys) {
        DomainQuery dq = QueryUtils.createDomainQuery(BillingEntity.INVOICE)
                .addDqPredicate(PredicateFactory.in(BillingField.INVOICE_GKEY, invoiceGkeys))
                .addDqPredicate(PredicateFactory.eq(BillingField.INVOICE_STATUS, InvoiceStatusEnum.FINAL));

        def totalCount = HibernateApi.getInstance().findCountByDomainQuery(dq);
        LOGGER.debug("total count of finalized invoice gkeys selected: " + totalCount);
        if (invoiceGkeys.size() != totalCount) {
            LOGGER.info("one or more Invoices selected are not in Final status");
            throw BizFailure.create("All the invoices selected for ACR should be in FINAL status");
        }

    }
    private final String invoiceName = "INVOICE";
    private final String invoiceGkeyName = "EVENT_GKEYS";
    private final String resultName = "RESULT";
    private final String triggerDateName = "triggerDate";
    private final String ACR_HANDLER_GROOVY_LIB = "MTLN4BillingACRHandlerCore";
    private final String REBILL_REQD = "rebillReqd";
    private final Logger LOGGER = Logger.getLogger(MTLN4BillingInitiateACRForInvoiceTxnBound.class);
}