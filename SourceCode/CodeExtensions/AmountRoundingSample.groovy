package SourceCode.CodeExtensions

import com.navis.framework.util.ConversionUtils
import java.math.RoundingMode
import com.navis.external.billing.AbstractAmountRoundingInterceptor

public class AmountRoundingSample extends AbstractAmountRoundingInterceptor {
  /**
   * This is a sample groovy code. Please copy the code and modify as per your requirement. Use one of two methods.
   * The input amount is passed as Double and add key = outAmount value = rounded amount to inOutMap
   * Murali Raghavachari Aug 05, 2010
   * @param inOutMap
   */
  public void round(Map inOutMap) {
    Double amount = 0;
    if (!inOutMap.isEmpty()) {
      amount = (Double) inOutMap.get("inAmount");
    }

    //   Sample -1 HIT Customer specific
    /*  special rounding as per customer specification
      <= .24 is rounded down to the nearest dollar
      >=.25 and <=.74 is rounded to .50
      >=.75 is rounded up to the nearest dollar
     */

    long amountWithoutDecimal = amount;
    Double amountOnlyDecimal = amount - amountWithoutDecimal;
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
    amount = amountWithoutDecimal + amountOnlyDecimal;
    inOutMap.put("outAmount", amount);

    // -----------------------------------------------------------------------------------------------
    //   Sample -2 Standard feature to round the amount using framework utility method.
    // Standard rounding feature provided by framework.  (Remove the following code if customer specific rounding is used
    int expectedFractionalDigits = 2;
    // Available rounding modes are UP, DOWN, CEILING, FLOOR, HALF_UP, HALF_DOWN and HALF_EVEN
    RoundingMode expectedRoundingMethod = RoundingMode.HALF_UP;
    Double roundedValue = ConversionUtils.convertNumberToDouble(getUserContext(), amount, expectedFractionalDigits,
            expectedRoundingMethod);
    inOutMap.put("outAmount", roundedValue);
  }
}