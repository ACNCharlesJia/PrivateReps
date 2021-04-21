package SourceCode.CodeExtensions

import com.navis.billing.BillingField
import com.navis.external.framework.entity.AbstractEntityLifecycleInterceptor
import com.navis.external.framework.entity.EEntityView
import com.navis.external.framework.util.EFieldChanges
import com.navis.external.framework.util.EFieldChangesView
import com.navis.framework.util.ConversionUtils

import java.math.RoundingMode

/**
 * Sample groovy code to round the the invoice item amount.
 * Copy this groovy code and
 * Murali Raghavachari Aug 05, 2010
 */
public class AmountRoundingForInvoiceItemSample extends AbstractEntityLifecycleInterceptor {

  @Override
  public void onCreate(EEntityView inEntity, EFieldChangesView inOriginalFieldChanges,
                       EFieldChanges inMoreFieldChanges) {
    Double roundedValue = roundInvItemAmount(inEntity);
    // It sets the rounded value and returns it to the caller.
    inMoreFieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, roundedValue);

  }

  public void onUpdate(EEntityView inEntity, EFieldChangesView inOriginalFieldChanges, EFieldChanges inMoreFieldChanges) {
    Double roundedValue = roundInvItemAmount(inEntity);
    // It sets the rounded value and returns it to the caller.
    inMoreFieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, roundedValue);
  }

  private double roundInvItemAmount(EEntityView inEntity) {
    // get the actual amount from the invoice item entity.
    Double itemAmount = (Double) inEntity.getField(BillingField.ITEM_AMOUNT);
    Double roundedValue = itemAmount;
    // Use one of the method and delete the code of other method.
    /**
     * Method - 1
     */
    // Standard rounding feature provided by framework.  (Remove the following code if customer specific rounding is used
    int expectedFractionalDigits = 2;
    // Available rounding modes are UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN and HALF_EVEN
    RoundingMode expectedRoundingMethod = RoundingMode.HALF_UP;
    roundedValue = ConversionUtils.convertNumberToDouble(getUserContext(), itemAmount, expectedFractionalDigits,
            expectedRoundingMethod);

    /**
     * Method - 2
     */
    // Custom rounding - rule provided by HIT
    long amountWithoutDecimal = itemAmount;
    Double amountOnlyDecimal = itemAmount - amountWithoutDecimal;
    if (amountOnlyDecimal != 0D) {
      if (amountOnlyDecimal < 0.24D) {
        amountOnlyDecimal = 0.00D;
      } else if (amountOnlyDecimal > 0.24D && amountOnlyDecimal < 0.75D) {
        amountOnlyDecimal = 0.50D;
      } else if (amountOnlyDecimal >= 0.75D) {
        amountOnlyDecimal = 0.00D;
        amountWithoutDecimal++;
      }
    }
    roundedValue = amountWithoutDecimal + amountOnlyDecimal;

    return (roundedValue);
  }
}