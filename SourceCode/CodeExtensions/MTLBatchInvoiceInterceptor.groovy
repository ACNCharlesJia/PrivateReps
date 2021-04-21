package SourceCode.CodeExtensions;

import com.navis.billing.BillingField;
import com.navis.billing.business.model.BatchInvoice;
import com.navis.billing.business.model.Invoice;
import com.navis.external.framework.entity.AbstractEntityLifecycleInterceptor;
import com.navis.external.framework.entity.EEntityView;
import com.navis.external.framework.util.EFieldChanges;
import com.navis.external.framework.util.EFieldChangesView;
import com.navis.framework.persistence.HibernateApi;
import org.apache.log4j.Logger;

public class MTLBatchInvoiceInterceptor extends AbstractEntityLifecycleInterceptor {
    @Override
    public void onCreate(EEntityView inEntity, EFieldChangesView inOriginalFieldChanges, EFieldChanges inMoreFieldChanges) {
        LOGGER.info("Setting BATCH_IS_TRACING_REQUIRED always as true.");
        inMoreFieldChanges.setFieldChange(BillingField.BATCH_IS_TRACING_REQUIRED, Boolean.TRUE);
    }

    @Override
    public void onUpdate(EEntityView inEntity, EFieldChangesView inOriginalFieldChanges, EFieldChanges inMoreFieldChanges) {
        //Do Nothing
        if (!(inEntity._entity instanceof BatchInvoice)) {
            LOGGER.error("Invoice Extension trigger for BatchInvoice entity");
            return;
        }
        BatchInvoice batchInvoice = inEntity._entity;
        if (batchInvoice == null) {
            LOGGER.error("Batch Invoice is null. Hence returning");
            return;
        }
        if (batchInvoice.getBatchId() != null && batchInvoice.getBatchId().startsWith("ACR_INVDR_")
                && inOriginalFieldChanges.hasFieldChange(BillingField.BATCH_INVOICE_GKEY)) {
            try {
                String[] values = batchInvoice.getBatchId().split("_");
                Long draftNbr = Long.valueOf(values[2]);
                String invoiceGkey = (String) inOriginalFieldChanges.findFieldChange(BillingField.BATCH_INVOICE_GKEY).getNewValue();
                if (invoiceGkey != null && draftNbr != null) {
                    Invoice invoice = Invoice.hydrate(Long.parseLong(invoiceGkey));
                    if (invoice != null) {
                        LOGGER.info("Setting previous invoice number as " + draftNbr + " for invoice : " + invoice.getInvoiceDraftNbr());
                        invoice.setFieldValue(BillingField.INVOICE_FLEX_LONG01, draftNbr);
                        HibernateApi.getInstance().save(invoice);
                        HibernateApi.getInstance().flush();
                    }
                }
            } catch (Exception any) {
                LOGGER.error("Exception thrown while populating previous invoice number.");
                LOGGER.error(any.getMessage());
            }
        }
    }
    private static final Logger LOGGER = Logger.getLogger(MTLBatchInvoiceInterceptor.class);
}