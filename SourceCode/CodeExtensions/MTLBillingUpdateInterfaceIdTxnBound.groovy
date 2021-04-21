package SourceCode.CodeExtensions;

import com.navis.argo.business.api.ArgoUtils;
import com.navis.billing.BillingField;
import com.navis.billing.business.model.Credit;
import com.navis.billing.business.model.Invoice;
import com.navis.external.framework.persistence.AbstractExtensionPersistenceCallback;
import com.navis.framework.persistence.HibernateApi
import org.apache.log4j.Logger;

public class MTLBillingUpdateInterfaceIdTxnBound extends AbstractExtensionPersistenceCallback {
    @Override
    public void execute(Map inParms, Map inOutResults) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        List<Serializable> invoiceGkeys = (List<Serializable>) inParms.get(INVOICE_GKEYS_NAME);
        List<Serializable> creditGkeys = (List<Serializable>) inParms.get(CREDIT_GKEYS_NAME);
        for (Serializable invoiceGkey : invoiceGkeys) {
            Invoice invoice = Invoice.hydrate(invoiceGkey);
            if (invoice != null && invoice.getInvoiceInterfaceId() != null) {
                LOGGER.debug("Updating Invoice Interface Id as null for invoice draft no::" + invoice.invoiceDraftNbr);
                invoice.setFieldValue(BillingField.INVOICE_INTERFACE_ID, null);
                HibernateApi.getInstance().save(invoice);
                HibernateApi.getInstance().flush();
                inOutResults.put(RESULT_STRING, "Invoice Interface Id is nullified for draft no::" + invoice.invoiceDraftNbr);
            }
        }
        for (Serializable creditGkey : creditGkeys) {
            Credit credit = Credit.hydrate(creditGkey);
            if (credit != null && credit.getCreditInterfaceId() != null) {
                LOGGER.debug("Updating Credit Interface Id as null for Credit draft no::" + credit.getCreditDraftNbr());
                credit.setFieldValue(BillingField.CREDIT_INTERFACE_ID, null);
                HibernateApi.getInstance().save(credit);
                HibernateApi.getInstance().flush();
                inOutResults.put(RESULT_STRING, "Credit Interface Id is nullified for draft no::" + credit.getCreditDraftNbr());
            }
        }

        if (inOutResults.isEmpty()) {
            inOutResults.put(RESULT_STRING, "Interface Id is null");
        }
        LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }
    private final String INVOICE_GKEYS_NAME = "invoiceGKeys";
    private final String CREDIT_GKEYS_NAME = "creditGKeys";
    private final String RESULT_STRING = "RESULT";
    private final Logger LOGGER = Logger.getLogger(MTLBillingUpdateInterfaceIdTxnBound.class);
}