package SourceCode.CodeExtensions

import com.navis.billing.BillingField
import com.navis.billing.business.model.Invoice
import com.navis.external.billing.AbstractInvoicePostInterceptor

/**
 * @author <a href="mailto:tramakrishnan@navis.com"> T Ramakrishnan</a> Date: 4/23/13, Time: 4:20 PM
 */
public class NavisInvoiceGenerationPostInterceptSample extends AbstractInvoicePostInterceptor {
  @Override
  boolean afterGenerateInvoice(Invoice inInvoice) {
    if (inInvoice != null) {
      String notes = inInvoice.getInvoiceNotes();
      notes = notes != null ? notes + "\n" : inInvoice.getInvoiceInvoiceType().getInvtypeId();
      inInvoice.setFieldValue(BillingField.INVOICE_NOTES, notes + "Updated by " + this.getClass() + " via code extension library!");
    }
    return true;
  }
}
