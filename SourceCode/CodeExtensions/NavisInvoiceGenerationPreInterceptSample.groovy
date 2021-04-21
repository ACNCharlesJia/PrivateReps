package SourceCode.CodeExtensions

import com.navis.billing.BillingField
import com.navis.billing.business.model.Invoice
import com.navis.external.billing.AbstractInvoicePreInterceptor

/**
 *
 * @author <a href="mailto:tramakrishnan@navis.com"> T Ramakrishnan</a> Date: 4/23/13, Time: 4:20 PM
 */

public class NavisInvoiceGenerationPreInterceptSample extends AbstractInvoicePreInterceptor {

  @Override
  boolean beforeGenerateInvoice(Invoice inInvoice) {
    return false  //To change body of implemented methods use File | Settings | File Templates. if (inInvoice != null) {
    String notes = inInvoice.getInvoiceNotes();
    notes = notes != null ? notes + "\n" : inInvoice.getInvoiceInvoiceType().getInvtypeId();
    inInvoice.setFieldValue(BillingField.INVOICE_NOTES, notes + "Updated by " + this.getClass() + " via code extension library!");
  }
}
