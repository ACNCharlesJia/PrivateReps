package SourceCode.CodeExtensions;

import com.navis.billing.BillingField;
import com.navis.billing.business.model.InvoiceDiscount;
import org.apache.log4j.Logger

/**
 * Created with IntelliJ IDEA. User: lakshra Date: 1/8/13 Time: 1:58 PM To change this template use File | Settings | File Templates.
 */
public  class InvoiceDiscountInterceptor extends AbstractInvoiceDiscountInterceptor{

  @Override
  public boolean calculateInvoiceDiscount(InvoiceDiscount inInvoiceDiscount) {
    inInvoiceDiscount.setFieldValue(BillingField.INVDIS_AMOUNT,1000);
    return super.calculateInvoiceDiscount(inInvoiceDiscount);
  }

  private final Logger LOGGER = Logger.getLogger(this.class);
}