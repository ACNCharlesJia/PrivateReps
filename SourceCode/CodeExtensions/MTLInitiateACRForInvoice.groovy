package SourceCode.CodeExtensions;

import com.navis.argo.business.api.ArgoUtils;
import com.navis.billing.BillingEntity;
import com.navis.billing.business.model.Invoice;
import com.navis.external.framework.ui.AbstractTableViewCommand;
import com.navis.framework.metafields.entity.EntityId;
import com.navis.framework.util.message.MessageCollector;
import com.navis.framework.util.message.MessageLevel
import org.apache.log4j.Logger;

class MTLInitiateACRForInvoice extends AbstractTableViewCommand {
    @Override
    public void execute(EntityId entityId, List<Serializable> inGkeys, Map<String, Object> params) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        if (entityId.getEntityName().equals(BillingEntity.INVOICE)==false) {
            LOGGER.error(String.format("Should be called only from menu item for entity: %s, hence returning" + Invoice.getSimpleName()));
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, errorDialogHeading, "Should be called only from menu item for entity: %s, hence returning", null);
            return;
        }

        if (inGkeys.size() == 0) {
            LOGGER.error(String.format("No invoice is selected to initiate ACR, hence returning"));
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, errorDialogHeading, "No invoice is selected to initiate ACR", null);
            return;
        }

        LOGGER.debug("Performing the ACR process from the invoice screen for the following invoice gkeys: " + inGkeys);

        Map inParam = new HashMap();
        inParam.put(INVOICE_GKEYS_NAME, inGkeys);
        Map outParam = new HashMap();

        MessageCollector mc = executeInTransaction(IN_TRANSACTION_GROOVY, inParam, outParam);
        if (mc.hasError()) {
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, errorDialogHeading, "", mc);
        } else {
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, "Initiate ACR", outParam.get(RESULT_STRING).toString(), mc);
        }
        LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }
    private final Logger LOGGER = Logger.getLogger(MTLInitiateACRForInvoice.class);
    private final String errorDialogHeading = "Initiate ACR Failed";
    private final String RESULT_STRING = "RESULT";
    private final String IN_TRANSACTION_GROOVY = "MTLN4BillingInitiateACRForInvoiceTxnBound";
    private final String INVOICE_GKEYS_NAME = "invoiceGKeys";
}