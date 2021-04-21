package SourceCode.GroovyPlugins

import com.navis.argo.business.api.GroovyApi;
import com.navis.billing.business.model.BatchInvoiceManagerPea;
import com.navis.framework.util.BizViolation;
import java.util.Map;

public class GenerateBatchInvoice extends GroovyApi {
    public void execute(Map map) throws BizViolation {
        BatchInvoiceManagerPea bimp = new BatchInvoiceManagerPea();
        bimp.generateBatchInvoice();
    }
}