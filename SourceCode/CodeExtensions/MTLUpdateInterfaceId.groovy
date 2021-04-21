package SourceCode.CodeExtensions;

import com.navis.argo.business.api.ArgoUtils;
import com.navis.billing.business.model.Credit;
import com.navis.billing.business.model.Invoice;
import com.navis.external.framework.ui.AbstractTableViewCommand;
import com.navis.framework.metafields.entity.EntityId;
import com.navis.framework.util.message.MessageCollector;
import com.navis.framework.util.message.MessageLevel
import org.apache.log4j.Logger;

class MTLUpdateInterfaceId extends AbstractTableViewCommand {
    @Override
    public void execute(EntityId entityId, List<Serializable> inGkeys, Map<String, Object> params) {
        if (!(Invoice.getSimpleName().equals(entityId.getEntityName()) || Credit.getSimpleName().equals(entityId.getEntityName()))) {
            LOGGER.error(String.format("Should be called only from menu item for entity: %s, hence returning" + Invoice.getSimpleName() + " / " + Credit.getSimpleName()));
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, errorDialogHeading, "Should be called only from menu item for entity: %s, hence returning", null);
            return;
        }
        if (inGkeys.size() == 0) {
            LOGGER.error(String.format("No invoice/credit is selected to initiate ACR, hence returning"));
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, errorDialogHeading, "No invoice/credit is selected to initiate ACR", null);
            return;
        }
        List<Serializable> invoiceGkeyList = new ArrayList<>();
        List<Serializable> creditGkeyList = new ArrayList<>();
        if (Invoice.getSimpleName().equals(entityId.getEntityName())) {
            invoiceGkeyList.addAll(inGkeys);
        } else if (Credit.getSimpleName().equals(entityId.getEntityName())) {
            creditGkeyList.addAll(inGkeys);
        }

        Map inParam = new HashMap();
        inParam.put(INVOICE_GKEYS_NAME, invoiceGkeyList);
        inParam.put(CREDIT_GKEYS_NAME, creditGkeyList);
        Map outParam = new HashMap();
        MessageCollector mc = executeInTransaction(IN_TRANSACTION_GROOVY, inParam, outParam);
        if (mc.hasError()) {
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, errorDialogHeading, "", mc);
        } else {
            getExtensionHelper().showMessageDialog(MessageLevel.SEVERE, "Initiate ACR", outParam.get(RESULT_STRING).toString(), mc);
        }
        LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }

    private final String INVOICE_GKEYS_NAME = "invoiceGKeys";
    private final String CREDIT_GKEYS_NAME = "creditGKeys";
    private final String errorDialogHeading = "Update Interface Id Failed.";
    private final String RESULT_STRING = "RESULT";
    private final String IN_TRANSACTION_GROOVY = "MTLBillingUpdateInterfaceIdTxnBound";
    private final Logger LOGGER = Logger.getLogger(MTLUpdateInterfaceId.class);
}