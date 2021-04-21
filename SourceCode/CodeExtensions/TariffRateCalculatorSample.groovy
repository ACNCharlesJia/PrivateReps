package SourceCode.CodeExtensions

import com.navis.argo.business.atoms.ServiceQuantityUnitEnum
import com.navis.argo.business.extract.ChargeableUnitEvent
import com.navis.argo.business.services.IServiceExtract
import com.navis.billing.BillingField
import com.navis.billing.business.model.Contract
import com.navis.billing.business.model.Invoice
import com.navis.billing.business.model.Tariff
import com.navis.billing.business.model.TariffRate
import com.navis.billing.presentation.BillingPresentationConstants
import com.navis.external.billing.AbstractTariffRateCalculatorInterceptor
import com.navis.framework.portal.FieldChanges
import org.apache.log4j.Level

public class TariffRateCalculatorSample extends AbstractTariffRateCalculatorInterceptor {
  /**
   * This is a sample groovy code. Please copy the code and modify as per your requirement. Customer can have multiple groovy code for each tariff ID
   * or one groovy code to support multiple tariff id by adding appropriate methods based on the tariff id value.
   * The inOutMap contains input of Invoice, Tariff Rate and ExtractEvent objects.
   * Compute the rate and return as "outAmount".  The computed amount is persisted in Invoice item as flat rate amount.
   *
   * Note: The flat rate amount is assumed to be the currency of the Tariff ID. If currency conversion is necessary, it will be computed
   * internally by using the appropriate currency conversion rate.
   * Murali Raghavachari October 10, 2010
   * @param inOutMap
   */
  public void calculateRate(Map inOutMap) {
    // Initialize the values to get it the object value from inOutMap
    log(Level.INFO, "");
    Invoice inv = null;
    TariffRate tariffRate = null;
    IServiceExtract extract = null;
    Contract contract = null;
    Date effectiveDate = null;
    Double outAmount = 0.00D;
    // Get the objects from inOutMap.
    if (!inOutMap.isEmpty()) {
      inv = (Invoice) inOutMap.get("inInvoice");
      tariffRate = (TariffRate) inOutMap.get("inTariffRate");
      Object extractEvent = inOutMap.get("inExtractEvent");
      if (extractEvent instanceof ChargeableUnitEvent) {
        extract = (ChargeableUnitEvent) inOutMap.get("inExtractEvent");
      }
    }
    // get the Contract from tariff rate.
    if (tariffRate != null) {
      contract = tariffRate.getRateContract();
    }
    effectiveDate = inv.getInvoiceEffectiveDate();

    //   ADD YOUR CODE BLOW //
    // Sample code to get Tariff ID of DISCH_20FULL and find 8.5% to return the rate.
    // First get the Tariff object for tariff ID = UNIT_DISC20

    Tariff tariff = Tariff.findTariff("DISCH_20FULL");
    TariffRate rateOfUnitDisch = contract.findEffectiveTariffRate(tariff, effectiveDate);
    if (rateOfUnitDisch != null) {
      outAmount = rateOfUnitDisch.getRateAmount() * 0.085D;
    }
    // Put the computed value in inOutMap.
    inOutMap.put(BillingPresentationConstants.OUT_AMOUNT, outAmount);

    FieldChanges invItemChanges = new FieldChanges();
    invItemChanges.setFieldChange(BillingField.ITEM_AMOUNT, outAmount);
    invItemChanges.setFieldChange(BillingField.ITEM_QUANTITY_UNIT, ServiceQuantityUnitEnum.SHORT_TONS);
    inOutMap.put(BillingPresentationConstants.INV_ITEM_CHANGES, invItemChanges);
  }
}