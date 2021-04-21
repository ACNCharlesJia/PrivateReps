package SourceCode.GroovyPlugins;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import com.navis.argo.ContextHelper
import org.apache.log4j.Logger
import com.navis.argo.business.api.GroovyApi
import com.navis.argo.business.services.TieredCalculation
import com.navis.argo.business.services.TieredCalculationEntry
import com.navis.billing.business.model.Invoice
import com.navis.billing.business.model.TariffRate
import com.navis.billing.business.model.TariffRateTier
import com.navis.argo.business.extract.ChargeableUnitEvent
import com.navis.billing.business.model.InvoiceItem

/*
  Author: Edward Ip
  Date: 2017/11/21
  TD111: Container Logic Utility

  This groovy script will be loaded to N4 database via Code Extensions View
  Installation Instructions:
	1. Go to Administration --> System --> Groovy Plug-ins
	2. Click Add (+)
	3. Enter Groovy Name as MTLDEVPrintObjectUtility
	4. Save it.

  Create Groovy Job for the mentioned groovy

 **
 Modifier:
 Date    :
 Purpose :
 */

public class MTLDEVPrintObjectUtility extends GroovyApi {
    /////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void printTieredCalculation(TieredCalculation tieredCalculation)
    {
String FN_NAME = "(printTieredCalculation) " ;
        this.log(FN_NAME + "*** START ***");

        if (tieredCalculation == null)
        {
this.log(FN_NAME + "*** tieredCalculation == null");
            return ;
        }

        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._remainingDeltaQty : " + tieredCalculation._remainingDeltaQty) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._durationTotal     : " + tieredCalculation._durationTotal) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._qtyOwed           : " + tieredCalculation._qtyOwed) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._qtyPaid           : " + tieredCalculation._qtyPaid) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._paidThruDay       : " + tieredCalculation._paidThruDay) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._prevPaidThruDay   : " + tieredCalculation._prevPaidThruDay) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._firstPaidDay      : " + tieredCalculation._firstPaidDay) ;
        LOGGER.debug(FN_NAME + "tieredCalculation._lastFreeDay       : " + tieredCalculation._lastFreeDay) ;
//        LOGGER.debug(FN_NAME + "tieredCalculation._outTime           : " + tieredCalculation._outTime) ;

        Long lTemp = 0 ;
        lTemp = oMTLDEVDateUtility.dateDiff(tieredCalculation._firstPaidDay, tieredCalculation._prevPaidThruDay, 'HOUR') ;
        LOGGER.debug(FN_NAME + "lTemp (firstPaidDay - prevPaidThruDay): " + lTemp + " HOURS") ;
        lTemp = oMTLDEVDateUtility.dateDiff(tieredCalculation._firstPaidDay, tieredCalculation._paidThruDay, 'HOUR') ;
        LOGGER.debug(FN_NAME + "lTemp (firstPaidDay - paidThruDay): " + lTemp + " HOURS") ;
        lTemp = oMTLDEVDateUtility.dateDiff(tieredCalculation._prevPaidThruDay, tieredCalculation._paidThruDay, 'HOUR') ;
        LOGGER.debug(FN_NAME + "lTemp (prevPaidThruDay - paidThruDay): " + lTemp + " HOURS") ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation._entries ;
        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
LOGGER.debug(FN_NAME + "========== Looping item : " + iIndex + " =========");
            printTieredCalculationEntry(item) ;

            iIndex++ ;
        }

    }

    void printTieredCalculationEntry(TieredCalculationEntry tieredCalculationEntry)
    {
String FN_NAME = "(printTieredCalculationEntry) " ;
        this.log(FN_NAME + "*** START ***");

        if (tieredCalculationEntry == null)
        {
this.log(FN_NAME + "*** tieredCalculationEntry == null");
            return ;
        }

        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._startDate    : " + tieredCalculationEntry._startDate) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._endDate      : " + tieredCalculationEntry._endDate) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._duration     : " + tieredCalculationEntry._duration) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._isChargeable : " + tieredCalculationEntry._isChargeable) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._description  : " + tieredCalculationEntry._description) ;
        this.log(FN_NAME + "*** END ***");
    }


    void printInvoice(Invoice invoice)
    {
String FN_NAME = "(printInvoice) " ;
        this.log(FN_NAME + "*** START ***");

        if (invoice == null)
        {
this.log(FN_NAME + "*** invoice == null");
            return ;
        }

        LOGGER.debug(FN_NAME + "invoice : " + invoice) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceContactName()          : " + invoice.getInvoiceContactName()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceContract()             : " + invoice.getInvoiceContract()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceContractCustomer()     : " + invoice.getInvoiceContractCustomer()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceItemInvoicesTableKey() : " + invoice.getInvoiceItemInvoicesTableKey()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceDraftNbr()             : " + invoice.getInvoiceDraftNbr()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceTotalCharges()         : " + invoice.getInvoiceTotalCharges()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoicePaidThruDay()          : " + invoice.getInvoicePaidThruDay()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceTotalOwed()            : " + invoice.getInvoiceTotalOwed()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceTotalPaid()            : " + invoice.getInvoiceTotalPaid()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoicePayeeCustomer()        : " + invoice.getInvoicePayeeCustomer()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoicePaymentItems()         : " + invoice.getInvoicePaymentItems()) ;
        LOGGER.debug(FN_NAME + "invoice.getInvoiceInvoiceItems()         : " + invoice.getInvoiceInvoiceItems()) ;

        Set setInvoiceItems = invoice.getInvoiceInvoiceItems() ;
        if (setInvoiceItems != null)
            LOGGER.debug(FN_NAME + "setInvoiceItems.size()         : " + setInvoiceItems.size()) ;
        else
            LOGGER.debug(FN_NAME + "setInvoiceItems.size()         : " + "it is null") ;

        Iterator itII = setInvoiceItems.iterator() ;
        if (itII != null)
        {
while(itII.hasNext())
            {
InvoiceItem invoiceItem = (InvoiceItem) itII.next() ;

                LOGGER.debug(FN_NAME + "\ninvoiceItem.getItemGkey()                  : " + invoiceItem.getItemGkey()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemEventTypeId()             : " + invoiceItem.getItemEventTypeId()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemEventEntityId()           : " + invoiceItem.getItemEventEntityId()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemFromDate()                : " + invoiceItem.getItemFromDate()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemToDate()                  : " + invoiceItem.getItemToDate()) ;

                LOGGER.debug(FN_NAME + "invoiceItem.getItemPrevPaidThruDay()         : " + invoiceItem.getItemPrevPaidThruDay()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemPaidThruDay()             : " + invoiceItem.getItemPaidThruDay()) ;

                LOGGER.debug(FN_NAME + "invoiceItem.getItemTariff()                  : " + invoiceItem.getItemTariff()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemDescription()             : " + invoiceItem.getItemDescription()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemQuantity()                : " + invoiceItem.getItemQuantity()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemQuantityBilled()          : " + invoiceItem.getItemQuantityBilled()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemQuantityUnit()            : " + invoiceItem.getItemQuantityUnit()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemQuantityUnitValue()       : " + invoiceItem.getItemQuantityUnitValue()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemRateBilled()              : " + invoiceItem.getItemRateBilled()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemRemainingCreditQuantity() : " + invoiceItem.getItemRemainingCreditQuantity()) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemGlCode()                  : " + invoiceItem.getItemGlCode()) ;
           }
        }


        this.log(FN_NAME + "*** END ***");
    }

    void printTariffRate(TariffRate tariffRate)
    {
String FN_NAME = "(printTariffRate) " ;
        this.log(FN_NAME + "*** START ***");

        if (tariffRate == null)
        {
this.log(FN_NAME + "*** tariffRate == null");
            return ;
        }

        LOGGER.debug(FN_NAME + "tariffRate : " + tariffRate) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateCustomerBaseContractNames() : " + tariffRate.getRateCustomerBaseContractNames()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateCustomerContractNames()     : " + tariffRate.getRateCustomerContractNames()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRatePredicateMetafieldId()      : " + tariffRate.getRatePredicateMetafieldId()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateTierTableKey()              : " + tariffRate.getRateTierTableKey()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateAmount()                    : " + tariffRate.getRateAmount()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateContract()                  : " + tariffRate.getRateContract()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateCurrency()                  : " + tariffRate.getRateCurrency()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateCustomerTariffId()          : " + tariffRate.getRateCustomerTariffId()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateCustomRateExtensionName()   : " + tariffRate.getRateCustomRateExtensionName()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateDescription()               : " + tariffRate.getRateDescription()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateEffectiveDate()             : " + tariffRate.getRateEffectiveDate()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateGkey()                      : " + tariffRate.getRateGkey()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateGlCode()                    : " + tariffRate.getRateGlCode()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateIsFlatRate()                : " + tariffRate.getRateIsFlatRate()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateMaxAmount()                 : " + tariffRate.getRateMaxAmount()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateMinAmount()                 : " + tariffRate.getRateMinAmount()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateStepRules()                 : " + tariffRate.getRateStepRules()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getRateTariff()                    : " + tariffRate.getRateTariff()) ;

        LOGGER.debug(FN_NAME + "tariffRate.getRateTiers()                     : " + tariffRate.getRateTiers()) ;
        Set setRateTiers = tariffRate.getRateTiers() ;
        Iterator itRateTiers = setRateTiers.iterator() ;
        while (itRateTiers.hasNext())
        {
TariffRateTier tariffRateTier = itRateTiers.next() ;
            printTariffRateTier(tariffRateTier) ;
        }

        LOGGER.debug(FN_NAME + "tariffRate.getRateType()                      : " + tariffRate.getRateType()) ;
        LOGGER.debug(FN_NAME + "tariffRate.getTariffRateRuleDto()             : " + tariffRate.getTariffRateRuleDto()) ;
        this.log(FN_NAME + "*** END ***");
    }


    void printTariffRateTier(TariffRateTier tariffRateTier)
    {
String FN_NAME = "(tariffRateTier) " ;
        this.log(FN_NAME + "*** START ***");

        if (tariffRateTier == null)
        {
this.log(FN_NAME + "*** tariffRateTier == null");
            return ;
        }

        LOGGER.debug(FN_NAME + "tariffRateTier : " + tariffRateTier) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierQtyDivisor()       : " + tariffRateTier.getTierQtyDivisor()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierAmountPerUnit()    : " + tariffRateTier.getTierAmountPerUnit()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierDescription()      : " + tariffRateTier.getTierDescription()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierGkey()             : " + tariffRateTier.getTierGkey()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierGlCode()           : " + tariffRateTier.getTierGlCode()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierFlatRateAmount()   : " + tariffRateTier.getTierFlatRateAmount()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierIsOneInvoiceItem() : " + tariffRateTier.getTierIsOneInvoiceItem()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierMinAmount()        : " + tariffRateTier.getTierMinAmount()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierMaxAmount()        : " + tariffRateTier.getTierMaxAmount()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierMinQuantity()      : " + tariffRateTier.getTierMinQuantity()) ;
        LOGGER.debug(FN_NAME + "tariffRateTier.getTierTariffRate()       : " + tariffRateTier.getTierTariffRate()) ;

        this.log(FN_NAME + "*** END ***");
    }


    void printCUE(ChargeableUnitEvent cue)
    {
String FN_NAME = "(printCUE) " ;
        this.log(FN_NAME + "*** START ***");

        if (cue == null)
        {
this.log(FN_NAME + "*** cue == null");
            return ;
        }

        LOGGER.debug(FN_NAME + "cue : " + cue) ;
        LOGGER.debug(FN_NAME + "cue.getCarrierETA()                 : " + cue.getCarrierETA()) ;
        LOGGER.debug(FN_NAME + "cue.getComplexId()                  : " + cue.getComplexId()) ;
        LOGGER.debug(FN_NAME + "cue.getDraftInvNbrField()           : " + cue.getDraftInvNbrField()) ;
        LOGGER.debug(FN_NAME + "cue.getEventEndTime()               : " + cue.getEventEndTime()) ;
        LOGGER.debug(FN_NAME + "cue.getEventGkey()                  : " + cue.getEventGkey()) ;
        LOGGER.debug(FN_NAME + "cue.getEventType()                  : " + cue.getEventType()) ;
        LOGGER.debug(FN_NAME + "cue.getFacility()                   : " + cue.getFacility()) ;
        LOGGER.debug(FN_NAME + "cue.getFacilityId()                 : " + cue.getFacilityId()) ;
        LOGGER.debug(FN_NAME + "cue.getFirstAvailability()          : " + cue.getFirstAvailability()) ;
        LOGGER.debug(FN_NAME + "cue.getOverrideValueType()          : " + cue.getOverrideValueType()) ;
        LOGGER.debug(FN_NAME + "cue.getServiceEntityId()            : " + cue.getServiceEntityId()) ;
        LOGGER.debug(FN_NAME + "cue.getServiceExtractEventEndTime() : " + cue.getServiceExtractEventEndTime()) ;
        LOGGER.debug(FN_NAME + "cue.getServiceExtractGkey()         : " + cue.getServiceExtractGkey()) ;
        LOGGER.debug(FN_NAME + "cue.getServiceId()                  : " + cue.getServiceId()) ;
        LOGGER.debug(FN_NAME + "cue.getStatus()                     : " + cue.getStatus()) ;
        LOGGER.debug(FN_NAME + "cue.getBexuCategory()               : " + cue.getBexuCategory()) ;

        this.log(FN_NAME + "*** END ***");
    }


    void printDayDistribute(List<Map<TariffRateTier, Object>> lm)
    {
String FN_NAME = "(printDayDistribute) " ;
        this.log(FN_NAME + "\n*** START ***");

        if (lm == null)
        {
this.log(FN_NAME + "*** lm == null");
            return ;
        }

        for(itemMap in lm)
        {
LinkedHashMap<TariffRateTier, Object> lhmTiers = (LinkedHashMap<TariffRateTier, Object>) itemMap ;
            for(tier in lhmTiers)
            {
TariffRateTier trtTemp = tier.getKey() ;
                LinkedHashMap<TariffRateTier, Object> lhmDetails = (LinkedHashMap) tier.getValue() ;
                LOGGER.debug(FN_NAME + "trtTemp : " + trtTemp + " lhmDetails : " + lhmDetails) ;
            }
        }

        this.log(FN_NAME + "\n*** END ***\n");
    }


    private static String NEXT_LINE = "\n";
    private Object oMTLDEVDateUtility = new GroovyApi().getGroovyClassInstance("MTLDEVDateUtility");
    private final Logger LOGGER = Logger.getLogger(this.class) ;
}