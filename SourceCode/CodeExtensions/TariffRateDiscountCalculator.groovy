package SourceCode.CodeExtensions

import com.navis.argo.business.api.ArgoUtils
import com.navis.argo.business.atoms.ServiceQuantityUnitEnum
import com.navis.argo.business.services.IServiceExtract
import com.navis.argo.business.services.TieredCalculation
import com.navis.billing.BillingField
import com.navis.billing.business.model.*
import com.navis.billing.presentation.BillingPresentationConstants
import com.navis.external.billing.AbstractTariffRateCalculatorInterceptor
import com.navis.framework.metafields.MetafieldId
import com.navis.framework.metafields.MetafieldIdFactory
import com.navis.framework.portal.FieldChanges
import org.apache.log4j.Logger

class TariffRateDiscountCalculator extends AbstractTariffRateCalculatorInterceptor {
    @Override
    void calculateRate(Map inOutMap) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        if(inOutMap != null){
            Invoice invoice = (Invoice) inOutMap.get(BillingPresentationConstants.IN_INVOICE);
            TariffRate tariffRate = (TariffRate) inOutMap.get(BillingPresentationConstants.IN_TARIFF_RATE);
            IServiceExtract serviceExtract = (IServiceExtract) inOutMap.get(BillingPresentationConstants.IN_EXTRACT_EVENT);
            Date invPtd = invoice.getInvoicePaidThruDay();
            CurrencyExchangeRate exchangeRate = getCurrencyExchangeRate(invoice, serviceExtract, tariffRate);
            calculateBandedRate(invoice, serviceExtract, tariffRate, exchangeRate, inOutMap);
        }

        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
    }

    private CurrencyExchangeRate getCurrencyExchangeRate(Invoice invoice, IServiceExtract iServiceExtract, TariffRate tariffRate) {
        CurrencyExchangeRate exchangeRate = null;
        if (tariffRate != null) {
            //get Currency conversation rate
            Currency fromCurrency = tariffRate.getRateCurrency();
            Currency toCurrency = invoice.getInvoiceCurrency();
            LOGGER.debug("Getting the exchange rate for From Currency: " + fromCurrency.getCurrencyId() + " To Currency: " + toCurrency.getCurrencyId())

            // Not required to get exchange rate if both the currency ids are same.
            if (!fromCurrency.equals(toCurrency)) {
                // exchange date may differ based on the rule defined for each
                BillingCurrencyExchangeManager bcem = new BillingCurrencyExchangeManager();
                Date exchangeEffectiveDate = bcem.getCurrencyExchangeDate(iServiceExtract, invoice);
                exchangeRate = fromCurrency.findEffectiveExchangeRate(toCurrency, exchangeEffectiveDate);
            }
        }

        return exchangeRate;
    }

    private void calculateBandedRate(Invoice invoice, IServiceExtract iServiceExtract, TariffRate tariffRate,
                                     CurrencyExchangeRate currencyExchangeRate, Map inOutMap) {
        InvoiceMessage.registerDebug(invoice, "Rate type - Banded Rate");
        ServiceType serviceType = ServiceType.findServiceType(iServiceExtract.getServiceId());
        MetafieldId tieredField = serviceType.getPredicateMetafieldId();
        TieredCalculation calcInput = null;
        if (tieredField != null) {
            InvoiceMessage.registerDebug(invoice, "calculateBandedRate: TieredField defined in ServiceType " + tieredField.getFieldId());
            calcInput = iServiceExtract.getTieredCalculation(tieredField);
        } else {
            InvoiceMessage.registerError(invoice, "calculateBandedRate: TieredField is not defined in ServiceType");
        }

        if (calcInput != null) {
            double qtyOwed = iServiceExtract.getNullSafeQuantity();
            if (serviceType.getSrvctypeUseTieredFieldAsQuantity()) {
                qtyOwed = calcInput.getQtyOwed() * qtyOwed;
            }

            InvoiceMessage.registerDebug(invoice, "calculateBandedRate use invoice item quantity as :" + qtyOwed)
            double tierQuantity = calcInput.getQtyOwed()
            TariffRateTier tierForQuantity = tariffRate.findTierForQuantity(new Long((long)Math.ceil(tierQuantity)))
            InvoiceMessage.registerDebug(invoice, "calculateBandedRate tierForQuantity:" + tierForQuantity)
            Double discountRate = tierForQuantity.getField(MetafieldIdFactory.valueOf("customFlexFields.tierCustomDFFDiscountRate"))
            LOGGER.info("discountRate : " + discountRate)

            if(discountRate > 0){
                handleDiscountRate(invoice, discountRate ,inOutMap, tierForQuantity)
            }
        }
    }

    private void handleDiscountRate(Invoice invoice, Double discountRate, Map inOutMap, TariffRateTier tariffRateTier) {
        FieldChanges fieldChanges = new FieldChanges();
        fieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, tariffRateTier.tierFlatRateAmount);
        fieldChanges.setFieldChange(BillingField.ITEM_INVOICE, invoice);
        fieldChanges.setFieldChange(BillingField.ITEM_TARIFF_RATE, tariffRateTier.getTierTariffRate());
        fieldChanges.setFieldChange(BillingField.ITEM_DESCRIPTION, tariffRateTier.getTierDescription());
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_BILLED, 1D);
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_UNIT, ServiceQuantityUnitEnum.DAYS);
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY, 1D);
        String itemNotes = "Discount Rate : " + discountRate;
        fieldChanges.setFieldChange(BillingField.ITEM_NOTES, itemNotes);

        inOutMap.put(BillingPresentationConstants.OUT_AMOUNT, tariffRateTier.tierFlatRateAmount);
        inOutMap.put(BillingPresentationConstants.INV_ITEM_CHANGES, fieldChanges);
        String invoiceNotes = invoice.getInvoiceNotes();
        if(invoiceNotes == null || invoiceNotes.isEmpty()){
            invoice.setFieldValue(BillingField.INVOICE_NOTES,INVOICE_NOTE);
        }
    }

    private final String INVOICE_NOTE = "Paper Rate +  Credit Note";
    private final Logger LOGGER = Logger.getLogger(TariffRateDiscountCalculator.class);
}