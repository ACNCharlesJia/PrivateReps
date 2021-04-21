package SourceCode.CodeExtensions;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import com.navis.framework.FrameworkConfig;
import com.navis.billing.BillingPropertyKeys;
import com.navis.framework.util.BizFailure;
import com.navis.argo.business.api.ArgoUtils

import com.navis.argo.ArgoExtractField
import com.navis.argo.ContextHelper
import com.navis.argo.business.api.GroovyApi
import com.navis.argo.business.atoms.ServiceQuantityUnitEnum
import com.navis.argo.business.extract.ChargeableUnitEvent
import com.navis.argo.business.services.IServiceExtract
import com.navis.argo.business.services.TieredCalculation
import com.navis.argo.business.services.TieredCalculationEntry
import com.navis.billing.BillingEntity
import com.navis.billing.BillingField
import com.navis.billing.business.calculators.TariffRateCalculator
import com.navis.billing.presentation.BillingPresentationConstants
import com.navis.external.billing.AbstractTariffRateCalculatorInterceptor
import com.navis.framework.business.Roastery
import com.navis.framework.metafields.MetafieldId
import com.navis.framework.persistence.HibernateApi
import com.navis.framework.portal.FieldChanges
import com.navis.framework.portal.Ordering
import com.navis.framework.portal.QueryUtils
import com.navis.framework.portal.query.DomainQuery
import com.navis.framework.portal.query.PredicateFactory
import com.navis.framework.util.DateUtil
import org.apache.log4j.Logger
import com.navis.billing.business.model.*
import com.navis.billing.business.atoms.RateTypeEnum
import com.navis.billing.BillingPropertyKeys

/**
 * MTL Storage tariff rate calculation
 *
 * Authors: <a href="mailto:Mugunthan.Selvaraj@navis.com">Mugunthan Selvaraj</a>
 * Date: 19 Aug 2015
 * JIRA: CSDV-3170
 * SFDC: NA
 * Called from: Custom Tariff rate calculation for storage
 *
 * S.no   Modified Date      Modified By          Jira Id    SFDC      Change Description
 */
class MTLN4BillingStorageTariffRateCalculation extends AbstractTariffRateCalculatorInterceptor {
    @Override
    public void calculateRate(Map inOutMap)
    {
        LOGGER.debug("\n\n");
        String FN_NAME = "(calculateRate) " ;
        this.log(FN_NAME + "*** START ***");

        LOGGER.debug(FN_NAME + "inOutMap : " + inOutMap);

        if (inOutMap == null)
        {
            this.log(FN_NAME + "*** Input parameter Map is null");
            this.log(FN_NAME + "*** END ***");
            return ;
        }

        // get infomration from input parameter Map
        Invoice invoice = (Invoice) inOutMap.get(BillingPresentationConstants.IN_INVOICE);
        oMTLDEVPrintObjectUtility.printInvoice(invoice) ;

        TariffRate tariffRate = (TariffRate) inOutMap.get(BillingPresentationConstants.IN_TARIFF_RATE);
        oMTLDEVPrintObjectUtility.printTariffRate(tariffRate) ;
        //LOGGER.debug(FN_NAME + "tariffRate : " + tariffRate);

        IServiceExtract serviceExtract = (IServiceExtract) inOutMap.get(BillingPresentationConstants.IN_EXTRACT_EVENT);
        LOGGER.debug(FN_NAME + "serviceExtract : " + serviceExtract);

        CurrencyExchangeRate exchangeRate = getCurrencyExchangeRate(invoice, serviceExtract, tariffRate);
        LOGGER.debug(FN_NAME + "exchangeRate : " + exchangeRate);

        /////////////////////////////
        // Change Time 00:00 to 23:59
        //Date invPtd = invoice.getInvoicePaidThruDay();

        Date invPtd = extractInvoicePaidThruDay(invoice) ;
        LOGGER.debug(FN_NAME + "invPtd : " + invPtd);

        if (serviceExtract.getEventType() != "REEFER")
        {
            invPtd = oMTLDEVDateUtility.trimUp(invPtd) ;
            LOGGER.debug(FN_NAME + "invPtd : " + invPtd);
        }
        /////////////////////////////

        TieredCalculation tieredCalculation = TariffRateCalculator.getTieredCalculation(invoice, serviceExtract, invPtd, null);
        oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        // use first paid day + 12 and the paid through day date for Paid Through Day
        if (serviceExtract.getEventType() == "REEFER")
        {
            //2017-12-20 15:45:02,122 DEBUG [MTLDEVPrintObjectUtility:?] (673228930) (printTieredCalculation) tieredCalculation._firstPaidDay      : Fri Dec 15 13:46:50 HKT 2017
            //invPtd = oMTLDEVDateUtility.combine1Datetand2Time(invPtd, tieredCalculation._firstPaidDay) ;
            Date invPtdTemp = oMTLDEVDateUtility.combine1Datetand2Time(invPtd, tieredCalculation._lastFreeDay) ;
            invPtdTemp = oMTLDEVDateUtility.addHour(invPtdTemp, 12) ;
            invPtdTemp = oMTLDEVDateUtility.addMinute(invPtdTemp, -1) ;
            // Start from previous day
            invPtdTemp = oMTLDEVDateUtility.addDay(invPtdTemp, -1) ;
            LOGGER.debug(FN_NAME + "invPtdTemp : " + invPtdTemp);

            // get the paid datetime's current slot's end time.
            while(invPtdTemp < invPtd)
            {
                invPtdTemp = oMTLDEVDateUtility.addHour(invPtdTemp, 12) ;
                LOGGER.debug(FN_NAME + "invPtdTemp : " + invPtdTemp);
            }
            invPtd = invPtdTemp ;

            tieredCalculation = null ;
            tieredCalculation = TariffRateCalculator.getTieredCalculation(invoice, serviceExtract, invPtd, null);
            oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

            //invoice.setInvoicePaidThruDay(invPtd) ;
            //invoice.setFieldValue(BillingField.)
        }

        tieredCalculation = handleFurturePaidThroughDay(tieredCalculation, tariffRate, serviceExtract, invPtd) ;
        oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        if (tieredCalculation == null)
        {
            String msg = "The no tiered calculation found for the service extract.";
            LOGGER.error(msg);
            InvoiceMessage.registerError(invoice, msg);
        }


        //tieredCalculation._qtyPaid = oMTLDEVDateUtility.dateDiff(tieredCalculation._firstPaidDay, tieredCalculation._prevPaidThruDay, 'DAY') ;
        //LOGGER.debug(FN_NAME + "tieredCalculation._qtyPaid : " + tieredCalculation._qtyPaid);
        //2017-11-22 17:31:46,068 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._remainingDeltaQty : 0
        //2017-11-22 17:31:46,068 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._durationTotal     : 0
        //2017-11-22 17:31:46,069 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._qtyOwed           : 5.0
        //2017-11-22 17:31:46,069 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._qtyPaid           : 0.0
        //2017-11-22 17:31:46,069 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._paidThruDay       : Sat Nov 25 23:59:59 HKT 2017
        //2017-11-22 17:31:46,069 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._prevPaidThruDay   : Tue Nov 21 00:00:00 HKT 2017
        //2017-11-22 17:31:46,070 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._firstPaidDay      : Sat Nov 18 20:00:00 HKT 2017
        //2017-11-22 17:31:46,070 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._lastFreeDay       : Wed Nov 22 20:00:00 HKT 2017
        //2017-11-22 17:31:46,070 DEBUG [MTLN4BillingStorageTariffRateCalculation:?] (1697094232) (printTieredCalculation) tieredCalculation._outTime           : Mon Nov 20 23:59:00 HKT 2017

        //TieredCalculation[ Qty owed:5.0 Qty paid:0.0 PaidThruDate:Sat Nov 25 23:59:59 HKT 2017 PrevPaidThruDate:Tue Nov 21 00:00:00 HKT 2017
        //Calendar Entries: Description:  Start Date: Tue Nov 21 00:00:00 HKT 2017 End Date: Sat Nov 25 23:59:59 HKT 2017 Duration: 4 IsChargeable?: true]

        //setTieredCalculationEntryStartDateToLastFreeDate(tieredCalculation) ;
        //oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        setTieredCalculationEntryStartDateToFirstPaidDay(tieredCalculation) ;


        double dTotalFSPDays = getTotalFSPDays(serviceExtract) ;
        double dTotalVIPDays = getTotalVIPDays(serviceExtract) ;
        double dTotalFreeDays = dTotalFSPDays + dTotalVIPDays ;

        //No of Unit Paid : Unit may be day or half day to be calculate
        double dNoOfUnitPaid = 0 ;
        double dNoOfUnitOwed = 0 ;

        // Create a Map for Total Days to be distribute vs Tired Tariff Rate
        // For Import / TS : FSP ,-> VIP -> Tired Rate
        // For EXPORT      : Tired Rate -> VIP -> FSP
        // Map<Ordering, List -> TariffRateTier, days, Start Date, End Date>


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        List<LinkedHashMap<TariffRateTier, Object>> lmDayDistribute = [] ;
        LOGGER.debug(FN_NAME + "lmDayDistribute : " + lmDayDistribute);


        Tariff fspTariff = Tariff.findTariff("OT_FSP_FREE_DAY_ALLOW");
        TariffRate fspTariffRate = null ;
        LOGGER.debug(FN_NAME + "fspTariff : " + fspTariff);
        if (fspTariff != null)
        {
            fspTariffRate = findTariffRate(tariffRate.getRateContract(), fspTariff);
            LOGGER.debug(FN_NAME + "fspTariffRate : " + fspTariffRate);
            if (fspTariffRate == null)
            {
                LOGGER.info("FSP tariff rate is null. Hence returning.");
                InvoiceMessage.registerInfo(invoice, "No FSP Tariff Rate");
            }
        }

        String vipStorageTariff = tariffRate.getFieldString(BillingField.RATE_FLEX_STRING02);
        LOGGER.debug(FN_NAME + "vipStorageTariff : " + vipStorageTariff);
        InvoiceMessage.registerInfo(invoice, "No VIP Storage Tariff");

        Tariff vipTariff = null ;
        try
        {
            vipTariff = HibernateApi.getInstance().load(Tariff.class, vipStorageTariff);
            LOGGER.debug(FN_NAME + "vipTariff : " + vipTariff);
        }
        catch (any)
        {
            LOGGER.warn("Exception thrown while finding VIP Tariff !")
            LOGGER.error(any.getMessage(), any)
            vipTariff = null;
            LOGGER.debug(FN_NAME + "vipTariff : " + vipTariff);
        }

        TariffRate vipTariffRate = null ;
        if (vipTariff == null)
        {
            //String msg = "Tariff not found for the viptariffId: " + vipStorageTariff;
            //LOGGER.error(msg);
            //InvoiceMessage.registerWarning(invoice, msg);
            InvoiceMessage.registerInfo(invoice, "No VIP Tariff");
        }
        else
        {
            vipTariffRate = findTariffRate(tariffRate.getRateContract(), vipTariff);
            LOGGER.debug(FN_NAME + "vipTariffRate : " + vipTariffRate);

            if (vipTariffRate == null)
            {
                String msg = "The VIP tariff:" + vipTariff.getTariffId() + " configured does not have any tariff rate ";
                LOGGER.error(msg);
                InvoiceMessage.registerWarning(invoice, msg);
                LOGGER.error("cannot create invoice item as there is no rate configured for VIP tariff:" + vipTariff.getTariffId());
            }
        }

        if (serviceExtract.getEventType() != "REEFER")
        {
            lmDayDistribute = lmDayDistribute + createDayDistributeList(fspTariffRate, serviceExtract) ;
            oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;
        }
        lmDayDistribute = lmDayDistribute + createDayDistributeList(vipTariffRate, serviceExtract) ;
        oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;
        lmDayDistribute = lmDayDistribute + createDayDistributeList(tariffRate, serviceExtract) ;
        oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;

        LOGGER.debug(FN_NAME + "lmDayDistribute CREATE");
        oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Calculate DAY distribute
        Date dTieredCalculationFirstPaidDay = oMTLDEVDateUtility.trim(tieredCalculation._firstPaidDay) ;
        LOGGER.debug(FN_NAME + "dTieredCalculationFirstPaidDay : " + dTieredCalculationFirstPaidDay);

        Date dTieredCalculationPaidThurDay = oMTLDEVDateUtility.trim(tieredCalculation._paidThruDay) ;
        LOGGER.debug(FN_NAME + "dTieredCalculationPaidThurDay : " + dTieredCalculationPaidThurDay);

        Date dTieredCalculationPrevPaidThurDay = oMTLDEVDateUtility.trim(tieredCalculation._prevPaidThruDay) ;
        LOGGER.debug(FN_NAME + "dTieredCalculationPrevPaidThurDay : " + dTieredCalculationPrevPaidThurDay);


        Long lTotalDay = oMTLDEVDateUtility.dateDiff(dTieredCalculationFirstPaidDay, dTieredCalculationPaidThurDay, 'DAY')  + 1 ;
        LOGGER.debug(FN_NAME + "lTotalDay : " + lTotalDay);

        Long lPaidDay = calTieredCalculationPaidBy(tieredCalculation, 'DAY') ;
        LOGGER.debug(FN_NAME + "lPaidDay : " + lPaidDay);

        Long lTotalDayRemain = lTotalDay ;
        Long lTotalPaidDayRemain = lPaidDay ;

        Iterator itDayDistribute = lmDayDistribute.iterator() ;
        Date dTempStartDate = dTieredCalculationFirstPaidDay ;
        if (lTotalDay <=  0)
        {
            // Create a dummy FSP record for invoice item
            while(itDayDistribute.hasNext())
            {
                LinkedHashMap<TariffRateTier, Object> lhm = itDayDistribute.next() ;
                for(itemMap in lhm)
                {
                    TariffRateTier trtTemp = itemMap.getKey() ;
                    LinkedHashMap<TariffRateTier, Object> lhmDetails = (LinkedHashMap) itemMap.getValue() ;

                    LOGGER.debug(FN_NAME + "Temp.getTierDescription() : " + trtTemp.getTierDescription());

                    lhmDetails.put("DayUsed", 0) ;
                    lhmDetails.put("DayStartDate", dTieredCalculationFirstPaidDay) ;
                    lhmDetails.put("DayEndDate", dTieredCalculationPaidThurDay) ;
                    lhmDetails.put("BeforeFirstPaidDay", true) ;

                    break ;
                }
                break ;
            }
        }
        else
        {
            while(itDayDistribute.hasNext() && lTotalDayRemain > 0)
            {
                LOGGER.debug(FN_NAME + "dTempStartDate : " + dTempStartDate);
                LinkedHashMap<TariffRateTier, Object> lhm = itDayDistribute.next() ;
                for(itemMap in lhm)
                {
                    TariffRateTier trtTemp = itemMap.getKey() ;
                    LinkedHashMap<TariffRateTier, Object> lhmDetails = (LinkedHashMap) itemMap.getValue() ;

                    LOGGER.debug(FN_NAME + "Temp.getTierDescription() : " + trtTemp.getTierDescription());

                    Long lDayCanUse = (Long) lhmDetails.get("DayAllowed") ;
                    LOGGER.debug(FN_NAME + "lDayCanUse : " + lDayCanUse);

                    if (lTotalDayRemain >= lDayCanUse)
                    {
                        lhmDetails.put("DayUsed", lDayCanUse) ;
                    }
                    else
                    {
                        lhmDetails.put("DayUsed", lTotalDayRemain) ;
                    }
                    lTotalDayRemain = lTotalDayRemain - lDayCanUse ;
                    LOGGER.debug(FN_NAME + "lTotalDayRemain : " + lTotalDayRemain);


                    if (lTotalPaidDayRemain >= lDayCanUse)
                    {
                        lhmDetails.put("PaidDay", lDayCanUse) ;
                    }
                    else
                    {
                        lhmDetails.put("PaidDay", lTotalPaidDayRemain) ;
                    }

                    lTotalPaidDayRemain = lTotalPaidDayRemain - lDayCanUse ;
                    if (lTotalPaidDayRemain <=0)
                        lTotalPaidDayRemain = 0 ;

                    LOGGER.debug(FN_NAME + "lTotalPaidDayRemain : " + lTotalPaidDayRemain);

                    LOGGER.debug(FN_NAME + "lhmDetails : " + lhmDetails);


                    Integer iDayUsed = lhmDetails.get("DayUsed") ;
                    Integer iPaidDay = lhmDetails.get("PaidDay") ;

                    //Date dStartDate = dTempStartDate + iPaidDay ;
                    Date dStartDate = oMTLDEVDateUtility.addDay(dTempStartDate, iPaidDay) ;
                    Date dEndDate = getTieredCalculationEndDate(tieredCalculation) ;
                    Date dCalEndDate = null ;
                    //dCalEndDate = dStartDate + iDayUsed - iPaidDay - 1 ;
                    dCalEndDate = oMTLDEVDateUtility.addDay(dStartDate, iDayUsed - iPaidDay - 1) ;
                    dEndDate = oMTLDEVDateUtility.minDate(tieredCalculation._paidThruDay, dCalEndDate) ;

                    lhmDetails.put("DayStartDate", dStartDate) ;
                    lhmDetails.put("DayEndDate", dEndDate) ;

                    if (dTieredCalculationPaidThurDay != null && dTieredCalculationPrevPaidThurDay != null)
                    {
                    	if (dTieredCalculationPaidThurDay <= dTieredCalculationPrevPaidThurDay)
                    	{
                             lhmDetails.put("BeforeFirstPaidDay", true) ;
                             break ;
                        }
                             
                    }

                    LOGGER.debug(FN_NAME + "lhmDetails : " + lhmDetails);

                    dTempStartDate = oMTLDEVDateUtility.addDay(dEndDate, 1) ;
                    break ;
                }
            }
        }
        LOGGER.debug(FN_NAME + "lmDayDistribute Calculate DAY distribute");
        oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Calculate HOUR distribute

        //dTieredCalculationFirstPaidDay = oMTLDEVDateUtility.addHour(tieredCalculation._firstPaidDay, -1) ;
        //dTieredCalculationFirstPaidDay = oMTLDEVDateUtility.trimUpToHour(dTieredCalculationFirstPaidDay) ;
        //LOGGER.debug(FN_NAME + "dTieredCalculationFirstPaidDay : " + dTieredCalculationFirstPaidDay);

        dTieredCalculationFirstPaidDay = tieredCalculation._lastFreeDay ;
        LOGGER.debug(FN_NAME + "dTieredCalculationFirstPaidDay : " + dTieredCalculationFirstPaidDay);

        dTieredCalculationPaidThurDay = oMTLDEVDateUtility.addHour(tieredCalculation._paidThruDay, 0) ;
        LOGGER.debug(FN_NAME + "dTieredCalculationPaidThurDay : " + dTieredCalculationPaidThurDay);

        Long lTotalHour = oMTLDEVDateUtility.dateDiff(dTieredCalculationFirstPaidDay, dTieredCalculationPaidThurDay, 'HOUR')  + 1 ;
        LOGGER.debug(FN_NAME + "lTotalHour : " + lTotalHour);

        Long lPaidHour = calTieredCalculationPaidBy(tieredCalculation, 'HOUR') ;
        LOGGER.debug(FN_NAME + "lPaidHour : " + lPaidHour);

        Long lTotalHourRemain = lTotalHour ;
        Long lTotalPaidHourRemain = lPaidHour ;

        Iterator itHourDistribute = lmDayDistribute.iterator() ;
        dTempStartDate = dTieredCalculationFirstPaidDay ;
        while(itHourDistribute.hasNext() && lTotalHourRemain > 0)
        {
            LinkedHashMap<TariffRateTier, Object> lhm = itHourDistribute.next() ;
            for(itemMap in lhm)
            {
                TariffRateTier trtTemp = itemMap.getKey() ;
                LinkedHashMap<TariffRateTier, Object> lhmDetails = (LinkedHashMap) itemMap.getValue() ;

                LOGGER.debug(FN_NAME + "Temp.getTierDescription() : " + trtTemp.getTierDescription());

                Long lHourCanUse = (Long) lhmDetails.get("HourAllowed") ;
                LOGGER.debug(FN_NAME + "lHourCanUse : " + lHourCanUse);

                if (lTotalHourRemain >= lHourCanUse)
                {
                    lhmDetails.put("HourUsed", lHourCanUse) ;
                }
                else
                {
                    lhmDetails.put("HourUsed", lTotalHourRemain) ;
                }
                lTotalHourRemain = lTotalHourRemain - lHourCanUse ;
                LOGGER.debug(FN_NAME + "lTotalHourRemain : " + lTotalHourRemain);


                if (lTotalPaidHourRemain >= lHourCanUse)
                {
                    lhmDetails.put("PaidHour", lHourCanUse) ;
                }
                else
                {
                    lhmDetails.put("PaidHour", lTotalPaidHourRemain) ;
                }

                lTotalPaidHourRemain = lTotalPaidHourRemain - lHourCanUse ;
                if (lTotalPaidHourRemain <=0)
                    lTotalPaidHourRemain = 0 ;

                LOGGER.debug(FN_NAME + "lhmDetails : " + lhmDetails);

                Integer iHourUsed = lhmDetails.get("HourUsed") ;
                Integer iPaidHour = lhmDetails.get("PaidHour") ;

                Date dStartDate = oMTLDEVDateUtility.addHour(dTempStartDate, iPaidHour) ;
                LOGGER.debug(FN_NAME + "dStartDate : " + dStartDate);
                Date dEndDate = getTieredCalculationEndDate(tieredCalculation) ;
                Date dCalEndDate = null ;
                //dCalEndDate = oMTLDEVDateUtility.addHour(dStartDate, iHourUsed - iPaidHour - 1) ;
				dCalEndDate = oMTLDEVDateUtility.addHour(dStartDate, iHourUsed - iPaidHour) ;
				dCalEndDate = oMTLDEVDateUtility.addMinute(dCalEndDate, -1) ;
                //dCalEndDate = oMTLDEVDateUtility.addHour(dTempStartDate, iHourUsed - iPaidHour) ;
                dEndDate = oMTLDEVDateUtility.minDate(tieredCalculation._paidThruDay, dCalEndDate) ;
                LOGGER.debug(FN_NAME + "dEndDate : " + dEndDate);

                if (dTieredCalculationPaidThurDay != null && dTieredCalculationPrevPaidThurDay != null)
                {
                    if (dTieredCalculationPaidThurDay <= dTieredCalculationPrevPaidThurDay)
                    {
                        lhmDetails.put("BeforeFirstPaidDay", true) ;
                        break ;
                    }
                             
                }

                lhmDetails.put("HourStartDate", dStartDate) ;
                lhmDetails.put("HourEndDate", dEndDate) ;

                //dTempStartDate = oMTLDEVDateUtility.addHour(dEndDate, 12) ;
				dTempStartDate = oMTLDEVDateUtility.addMinute(dEndDate, 1) ;
                break ;
            }
        }
        LOGGER.debug(FN_NAME + "lmDayDistribute Calculate HOUR distribute");
        oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;
        oMTLDEVPrintObjectUtility.printInvoice(invoice) ;


        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        // Calculate HOUR distribute to Day Variable
        LOGGER.debug(FN_NAME + "serviceExtract.getEventType() : " + serviceExtract.getEventType());
        if (serviceExtract.getEventType() == "REEFER")
        {
            Iterator itHourToDayDistribute = lmDayDistribute.iterator() ;
            while(itHourToDayDistribute.hasNext())
            {
                LinkedHashMap<TariffRateTier, Object> lhm = itHourToDayDistribute.next() ;
                for(itemMap in lhm)
                {
                    TariffRateTier trtTemp = itemMap.getKey() ;
                    LinkedHashMap<TariffRateTier, Object> lhmDetails = (LinkedHashMap) itemMap.getValue() ;

                    Long lTempHourUsed = lhmDetails.get("HourUsed") ;
                    Long lTempPaidHour = lhmDetails.get("PaidHour") ;

                    Long lTempDayUsed = hourToNumberOfUnit(lTempHourUsed, 12) ;
                    Long lTempPaidDay = hourToNumberOfUnit(lTempPaidHour, 12) ;

                    LOGGER.debug(FN_NAME + "lTempDayUsed : " + lTempDayUsed);
                    LOGGER.debug(FN_NAME + "lTempPaidDay : " + lTempPaidDay);

                    lhmDetails.put("DayUsed", lTempDayUsed) ;
                    lhmDetails.put("PaidDay", lTempPaidDay) ;

                    Date dStartDate = (Date) lhmDetails.get("HourStartDate") ;
                    Date dEndDate = (Date) lhmDetails.get("HourEndDate") ;

                    lhmDetails.put("DayStartDate", dStartDate) ;
                    lhmDetails.put("DayEndDate", dEndDate) ;

                    break ;
                }
            }
            LOGGER.debug(FN_NAME + "lmDayDistribute Calculate HOUR distribute to Day Variable");
            oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;
            oMTLDEVPrintObjectUtility.printInvoice(invoice) ;
        }

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        /*
        boolean bAnyInvoiceItemWillCreate = isAnyInvoiceItemWillCreate(lmDayDistribute) ;
        // if bAnyInvoiceItemWillCreate is false, create a dummy record with correct value for FSP
        // since system created a wrong value record with billed quqntity is null

        if (!bAnyInvoiceItemWillCreate && serviceExtract.getEventType() == "STORAGE")
        {
            // set dummy FSP record in the list (DayUsed) to 1
            createDummyFSP(lmDayDistribute) ;
            LOGGER.debug(FN_NAME + "lmDayDistribute CreateDummyFSP record");
            oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;
        }
        */

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////

        boolean bHandledFSPVIP = false ;
        // Loop the distribute map to INVOICE
        LOGGER.debug(FN_NAME + "\n\n");
        itDayDistribute = lmDayDistribute.iterator() ;
        Long lStartQty = 0 ;
        Long lAnyUpdatedOnInvoiceItem = 0 ;

        while(itDayDistribute.hasNext())
        {
            LinkedHashMap<TariffRateTier, Object> lhm = itDayDistribute.next() ;
            for(itemMap in lhm)
            {
                TariffRateTier trtTemp = itemMap.getKey() ;
                LinkedHashMap<String, Object> lhmDetails = (LinkedHashMap) itemMap.getValue() ;

                LOGGER.debug("\n\n" + FN_NAME + "trtTemp.getTierDescription() : " + trtTemp.getTierDescription());

                Long lDayAllowed = (Long) lhmDetails.get("DayAllowed") ;
                Long lDayUsed = (Long) lhmDetails.get("DayUsed") ;
                Long lDayPaid = (Long) lhmDetails.get("PaidDay") ;
                Long lTierUsed = lDayUsed - lDayPaid ;
                boolean bBeforeFirstPaidDay = (boolean) lhmDetails.get("BeforeFirstPaidDay") ;

                LOGGER.debug(FN_NAME + "lTierUsed                        : " + lTierUsed);
                LOGGER.debug(FN_NAME + "bBeforeFirstPaidDay              : " + bBeforeFirstPaidDay);

                if ((lDayAllowed > 0 &&  lTierUsed > 0 && lDayUsed > 0) ||
                    (bBeforeFirstPaidDay)
                   )
                {
                    if (trtTemp.getTierGkey() == null)
                    {
                        handleNullTariffRateTier(invoice, trtTemp, exchangeRate, lhmDetails, tieredCalculation, inOutMap) ;
                        bHandledFSPVIP = true ;
                    }
                    else
                    {
                        Date dStartDate = (Date) lhmDetails.get("DayStartDate") ;
                        Date dEndDate = (Date) lhmDetails.get("DayEndDate") ;

                        if (serviceExtract.getEventType() == "REEFER")
                        {
                            dStartDate = (Date) lhmDetails.get("HourStartDate") ;
                            dEndDate = (Date) lhmDetails.get("HourEndDate") ;
                        }
						
                        LOGGER.debug(FN_NAME + "dStartDate : " + dStartDate);
                        LOGGER.debug(FN_NAME + "dEndDate   : " + dEndDate);
						
                        setTieredCalculationEntryStartDate(tieredCalculation, dStartDate) ;
                        setTieredCalculationEntryEndDate(tieredCalculation, dEndDate) ;
						
                        //InvoiceMessage.registerDebug(invoice, "Tier is not previously paid. Min Quantity: " + tierMinQty + " Tier Max Quantity: " +
                        //        (tierMaxQty == maxValue ? "[No Limit]" : tierMaxQty) + " Quantity unpaid: " +
                        //        (qtyUnpaidInTier == maxValue ? "[No Limit]" : qtyUnpaidInTier) + " Quantity Owed: " + qtyOwedInTier);

                        LOGGER.debug(FN_NAME + "********* before TariffRateCalculator.invoiceTier *********");
                        LOGGER.debug(FN_NAME + "invoice                        : " + invoice);
                        LOGGER.debug(FN_NAME + "trtTemp                        : " + trtTemp);
                        LOGGER.debug(FN_NAME + "serviceExtract                 : " + serviceExtract);
                        LOGGER.debug(FN_NAME + "tieredCalculation              : " + tieredCalculation);
                        LOGGER.debug(FN_NAME + "tieredCalculation.getEntries() : " + tieredCalculation.getEntries());
						oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;
						

                        TariffRateCalculator.invoiceTier(invoice, trtTemp, serviceExtract, exchangeRate, 0, lTierUsed, tieredCalculation);

                        LOGGER.debug(FN_NAME + "********* after TariffRateCalculator.invoiceTier *********");
                    }
                    lAnyUpdatedOnInvoiceItem = 1 ;
                }

                lStartQty = lStartQty + lDayUsed + 1 ;

                oMTLDEVPrintObjectUtility.printInvoice(invoice) ;
            }
        }

        // patch Invoice Item which Paid Through Day < Previous Paid Through Day
        LOGGER.debug(FN_NAME + "lAnyUpdatedOnInvoiceItem : " + lAnyUpdatedOnInvoiceItem);
        if (lAnyUpdatedOnInvoiceItem == 0)
        {
            itDayDistribute = lmDayDistribute.iterator() ;
            // get last item
            LinkedHashMap<TariffRateTier, Object> lhm = null ;
            while(itDayDistribute.hasNext())
            {
                lhm = itDayDistribute.next() ;
            }

            LOGGER.debug(FN_NAME + "lhm : " + lhm);
            if (lhm != null)
            {
                for(itemMap in lhm)
                {
                    TariffRateTier trtTemp = itemMap.getKey() ;
                    LinkedHashMap<String, Object> lhmDetails = (LinkedHashMap) itemMap.getValue() ;

                    LOGGER.debug("\n\n" + FN_NAME + "trtTemp.getTierDescription() : " + trtTemp.getTierDescription());

                    Long lDayAllowed = (Long) lhmDetails.get("DayAllowed") ;
                    Long lDayUsed = (Long) lhmDetails.get("DayUsed") ;
                    Long lDayPaid = (Long) lhmDetails.get("PaidDay") ;
                    Long lTierUsed = lDayUsed - lDayPaid ;
                    boolean bBeforeFirstPaidDay = (boolean) lhmDetails.get("BeforeFirstPaidDay") ;

                    LOGGER.debug(FN_NAME + "lTierUsed                        : " + lTierUsed);
                    LOGGER.debug(FN_NAME + "bBeforeFirstPaidDay              : " + bBeforeFirstPaidDay);

                    handleNullTariffRateTier(invoice, trtTemp, exchangeRate, lhmDetails, tieredCalculation, inOutMap) ;

                    break ;
                }
            }
        }
        LOGGER.debug(FN_NAME + "lmDayDistribute create invoice");
        oMTLDEVPrintObjectUtility.printDayDistribute(lmDayDistribute) ;
        oMTLDEVPrintObjectUtility.printInvoice(invoice) ;

        ////////////////////////////////////////////////////////////////////////////////////////////////////////////
        //if (!bHandledFSPVIP)
        //{
            Set invoiceItems = invoice.getInvoiceInvoiceItems();
            LOGGER.debug(FN_NAME + "invoiceItems : " + invoiceItems);

            if (invoiceItems != null && invoiceItems.isEmpty())
            {
                String msg = "No Invoice items created as the size of the invoice items is 0";
                LOGGER.warn(msg);
                InvoiceMessage.registerWarning(invoice, msg);
            }

            if (invoiceItems != null && !invoiceItems.isEmpty())
            {
                LOGGER.info(FN_NAME + "Invoice item size is greater than zero. So removing one invoice item.");
                removeInvoiceItem(invoice, invPtd, inOutMap);
            }

        oMTLDEVPrintObjectUtility.printInvoice(invoice) ;
        //}


        LOGGER.debug(FN_NAME + "fixItemunitproblem(invoice)") ;
        fixItemunitproblem(invoice) ;
        oMTLDEVPrintObjectUtility.printInvoice(invoice) ;

        this.log(FN_NAME + "*** END ***");
    }

    void fixItemunitproblem(Invoice invoice)
    {
        String FN_NAME = "(printInvoice) " ;
        this.log(FN_NAME + "*** START ***");

        if (invoice == null)
        {
            this.log(FN_NAME + "*** invoice == null");
            return ;
        }

        LOGGER.debug(FN_NAME + "invoice : " + invoice) ;

        Set setInvoiceItems = invoice.getInvoiceInvoiceItems() ;
        if (setInvoiceItems != null)
            LOGGER.debug(FN_NAME + "setInvoiceItems.size()         : " + setInvoiceItems.size()) ;
        else
            LOGGER.debug(FN_NAME + "setInvoiceItems.size()         : " + "it is null") ;

        LinkedHashMap<String, ServiceQuantityUnitEnum> lhmItemunitMapping = new LinkedHashMap<String, ServiceQuantityUnitEnum>() ;
        lhmItemunitMapping.put("STORAGE", ServiceQuantityUnitEnum.DAYS) ;
        lhmItemunitMapping.put("REEFER", ServiceQuantityUnitEnum.DAYS) ;
        lhmItemunitMapping.put("UNIT_PORT_ANCILLARY_CHARGE", ServiceQuantityUnitEnum.ITEMS) ;
        lhmItemunitMapping.put("UNIT_PSC", ServiceQuantityUnitEnum.ITEMS) ;
        lhmItemunitMapping.put("UNIT_POC", ServiceQuantityUnitEnum.ITEMS) ;

        Iterator itII = setInvoiceItems.iterator() ;
        if (itII != null)
        {
            while(itII.hasNext())
            {
                InvoiceItem invoiceItem = (InvoiceItem) itII.next() ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemEventTypeId()  : " + invoiceItem.getItemEventTypeId()) ;

                ServiceQuantityUnitEnum sque = lhmItemunitMapping.get(invoiceItem.getItemEventTypeId()) ;
                LOGGER.debug(FN_NAME + "sque : " + sque) ;
                LOGGER.debug(FN_NAME + "invoiceItem.getItemQuantityUnit()  : " + invoiceItem.getItemQuantityUnit()) ;

                if (sque != null)
                {
                    if (sque != invoiceItem.getItemQuantityUnit())
                    {
                        LOGGER.debug(FN_NAME + "UNIT is Wrong") ;
                        invoiceItem.setFieldValue(BillingField.ITEM_QUANTITY_UNIT, sque) ;
                        LOGGER.debug(FN_NAME + "UNIT updated to sque : " + sque) ;
                    }

                }
            }
        }

        this.log(FN_NAME + "*** END ***");
    }



    public boolean isAnyInvoiceItemWillCreate(List<Map<TariffRateTier, Object>> lm)
    {
        String FN_NAME = "(isAnyInvoiceItemWillCreate) " ;
        this.log(FN_NAME + "*** START ***");

        boolean bIsAnyInvoiceItemWillCreate = false ;

        if (lm == null)
        {
            this.log(FN_NAME + "*** lm == null");
            return bIsAnyInvoiceItemWillCreate ;
        }

        for(itemMap in lm)
        {
            LinkedHashMap<TariffRateTier, Object> lhmTiers = (LinkedHashMap<TariffRateTier, Object>) itemMap ;
            for(tier in lhmTiers)
            {
                TariffRateTier trtTemp = tier.getKey() ;
                LinkedHashMap<TariffRateTier, Object> lhmDetails = (LinkedHashMap) tier.getValue() ;

                if (lhmDetails.get("DayUsed") > 0)
                    bIsAnyInvoiceItemWillCreate = true ;

                LOGGER.debug(FN_NAME + "bIsAnyInvoiceItemWillCreate : " + bIsAnyInvoiceItemWillCreate + " trtTemp : " + trtTemp + " lhmDetails : " + lhmDetails) ;
            }
        }

        this.log(FN_NAME + "*** END ***");
        return bIsAnyInvoiceItemWillCreate ;
    }

    public void createDummyFSP(List<Map<TariffRateTier, Object>> lm)
    {
        String FN_NAME = "(createDummyFSP) " ;
        this.log(FN_NAME + "*** START ***");

        if (lm == null)
        {
            this.log(FN_NAME + "*** lm == null");
            return  ;
        }

        for(itemMap in lm)
        {
            LinkedHashMap<TariffRateTier, Object> lhmTiers = (LinkedHashMap<TariffRateTier, Object>) itemMap ;
            for(tier in lhmTiers)
            {
                TariffRateTier trtTemp = tier.getKey() ;
                LinkedHashMap<TariffRateTier, Object> lhmDetails = (LinkedHashMap) tier.getValue() ;

                LOGGER.debug(FN_NAME + "trtTemp : " + trtTemp ) ;
                // assume the null record is the FSP record
                if (trtTemp.getTierGkey() == null)
                {
                    lhmDetails.put("DayUsed", 1) ;
                    LOGGER.debug(FN_NAME + "Set lhmDetails.put(\"DayUsed\", 1) ") ;
                }

                LOGGER.debug(FN_NAME + "trtTemp : " + trtTemp + " lhmDetails : " + lhmDetails) ;
            }
        }

        this.log(FN_NAME + "*** END ***");
        return  ;
    }


    public double getTotalFreeDays(IServiceExtract serviceExtract)
    {
        String FN_NAME  = "(getTotalFreeDays) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "serviceExtract : " + serviceExtract);

        double dTotalFSPDays = getTotalFSPDays(serviceExtract) + getTotalVIPDays(serviceExtract) ;

        return dTotalFSPDays ;
    }


    public double getTotalFSPDays(IServiceExtract serviceExtract)
    {
        String FN_NAME  = "(getTotalFSPDays) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "serviceExtract : " + serviceExtract);

        ChargeableUnitEvent cue;

        if (serviceExtract instanceof ChargeableUnitEvent)
        {
            cue = (ChargeableUnitEvent) serviceExtract;
            LOGGER.debug(FN_NAME + "cue : " + cue);

            if (cue == null)
            {
                LOGGER.warn("*** Cannot calculate free days for extracts which are NULL");
            }
            else
            {
                Double nfspDays = (Double) cue.getFieldValue(fspDays);
                LOGGER.debug(FN_NAME + "nfspDays : " + nfspDays);

                if (nfspDays == null)
                {
                    nfspDays = 0D;
                }
                return nfspDays;
            }
        }
        else
        {
            LOGGER.warn("*** Cannot calculate free days for extracts which are not the instance of CUE");
        }
        this.log(FN_NAME + "*** END ***");
        return 0d;
    }

    public double getTotalVIPDays(IServiceExtract serviceExtract)
    {
        String FN_NAME  = "(getTotalVIPDays) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "serviceExtract : " + serviceExtract);

        ChargeableUnitEvent cue;

        if (serviceExtract instanceof ChargeableUnitEvent)
        {
            cue = (ChargeableUnitEvent) serviceExtract;
            LOGGER.debug(FN_NAME + "cue : " + cue);

            if (cue == null)
            {
                LOGGER.warn("*** Cannot calculate free days for extracts which are NULL");
            }
            else
            {
                Double vipExtraDays = (Double) cue.getFieldValue(vipDaysMetaField);
                LOGGER.debug(FN_NAME + "vipExtraDays : " + vipExtraDays);

                if (vipExtraDays == null)
                {
                    vipExtraDays = 0D;
                }
                return vipExtraDays ;
            }
        }
        else
        {
            LOGGER.warn("*** Cannot calculate free days for extracts which are not the instance of CUE");
        }
        this.log(FN_NAME + "*** END ***");
        return 0d;

    }

    private void removeInvoiceItem(Invoice invoice, Date inPaidThruDay, Map inOutMap)
    {
        String FN_NAME = "(removeInvoiceItem) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "invoice : " + invoice) ;
        LOGGER.debug(FN_NAME + "inPaidThruDay : " + inPaidThruDay) ;
        LOGGER.debug(FN_NAME + "inOutMap : " + inOutMap) ;

        Set invoiceItems = invoice.getInvoiceInvoiceItems();
        LOGGER.debug(FN_NAME + "invoiceItems : " + invoiceItems) ;

        InvoiceItem invoiceItem = (InvoiceItem) invoiceItems.iterator().next();
        LOGGER.debug(FN_NAME + "invoiceItem : " + invoiceItem) ;

        if (invoiceItem == null)
        {
            LOGGER.error("Invoice item is null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return;
        }

        //For custom rate - Return item should be removed and the product will add again.
      //HibernateApi.getInstance().saveOrUpdate(invoiceItem)
        invoiceItems.remove(invoiceItem);
      //HibernateApi.getInstance().saveOrUpdate(invoiceItem)

        FieldChanges fieldChanges = new FieldChanges();

        fieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, invoiceItem.getItemAmount());
        fieldChanges.setFieldChange(BillingField.ITEM_INVOICE, invoice);
        fieldChanges.setFieldChange(BillingField.ITEM_TARIFF_RATE, invoiceItem.getItemTariffRate());
        //fieldChanges.setFieldChange(MetafieldIdFactory.valueOf("itemTariffRate.rateTariff.tariffId"), invoiceItem.getItemTariff());
        fieldChanges.setFieldChange(BillingField.ITEM_DESCRIPTION, invoiceItem.getItemDescription());
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_BILLED, invoiceItem.getItemQuantityBilled());
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_UNIT, invoiceItem.getItemQuantityUnit());
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY, invoiceItem.getItemQuantity());
        fieldChanges.setFieldChange(BillingField.ITEM_RATE_BILLED, invoiceItem.getItemRateBilled());
        fieldChanges.setFieldChange(BillingField.ITEM_FROM_DATE, invoiceItem.getItemFromDate());
        fieldChanges.setFieldChange(BillingField.ITEM_TO_DATE, invoiceItem.getItemToDate());
        fieldChanges.setFieldChange(BillingField.ITEM_SERVICE_EXTRACT_GKEY, invoiceItem.getItemServiceExtractGkey());
        fieldChanges.setFieldChange(BillingField.ITEM_SERVICE_EXTRACT_TYPE, invoiceItem.getItemServiceExtractType());
        fieldChanges.setFieldChange(BillingField.ITEM_FLAT_RATE_AMOUNT, invoiceItem.getItemFlatRateAmount());
        fieldChanges.setFieldChange(BillingField.ITEM_EVENT_TYPE_ID, invoiceItem.getItemEventTypeId());
        fieldChanges.setFieldChange(BillingField.ITEM_EVENT_ENTITY_ID, invoiceItem.getItemEventEntityId());
        fieldChanges.setFieldChange(BillingField.ITEM_PREV_PAID_THRU_DAY, invoiceItem.getItemPrevPaidThruDay());
        fieldChanges.setFieldChange(BillingField.ITEM_PAID_THRU_DAY, invoiceItem.getItemPaidThruDay());
        fieldChanges.setFieldChange(BillingField.ITEM_FLAT_RATE_AMOUNT, invoiceItem.getItemFlatRateAmount());
        fieldChanges.setFieldChange(BillingField.ITEM_RATE_IS_FLAT_RATE, invoiceItem.getItemRateIsFlatRate());

        inOutMap.put(BillingPresentationConstants.OUT_AMOUNT, invoiceItem.getItemAmount());
        inOutMap.put(BillingPresentationConstants.INV_ITEM_CHANGES, fieldChanges);

        //invoiceItem = invoiceItems.iterator().next();
        invoiceItem.setFieldValue(BillingField.ITEM_SERVICE_EXTRACT_GKEY, null);
        HibernateApi.getInstance().delete(invoiceItem);

        this.log(FN_NAME + "*** END ***");
    }

    private CurrencyExchangeRate getCurrencyExchangeRate(Invoice inInvoice, IServiceExtract inService, TariffRate inTariffRate)
    {
        String FN_NAME = "(getCurrencyExchangeRate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "inInvoice : " + inInvoice) ;
        LOGGER.debug(FN_NAME + "inService : " + inService) ;
        LOGGER.debug(FN_NAME + "inTariffRate : " + inTariffRate) ;

        CurrencyExchangeRate exchangeRate = null;
        if (inTariffRate != null)
        {
            //get Currency conversation rate
            Currency fromCurrency = inTariffRate.getRateCurrency();
            LOGGER.debug(FN_NAME + "fromCurrency : " + fromCurrency) ;

            Currency toCurrency = inInvoice.getInvoiceCurrency();
            LOGGER.debug(FN_NAME + "toCurrency : " + toCurrency) ;

            LOGGER.debug("Getting the exchange rate for From Currency: " + fromCurrency.getCurrencyId() + " To Currency: " + toCurrency.getCurrencyId());

            // Not required to get exchange rate if both the currency ids are same.
            if (!fromCurrency.equals(toCurrency))
            {
                // exchange date may differ based on the rule defined for each
                BillingCurrencyExchangeManager bcem = new BillingCurrencyExchangeManager();
                Date exchangeEffectiveDate = bcem.getCurrencyExchangeDate(inService, inInvoice);
                LOGGER.debug(FN_NAME + "exchangeEffectiveDate : " + exchangeEffectiveDate) ;

                exchangeRate = fromCurrency.findEffectiveExchangeRate(toCurrency, exchangeEffectiveDate);
                LOGGER.debug(FN_NAME + "exchangeRate : " + exchangeRate) ;
            }
        }

        LOGGER.debug(FN_NAME + "exchangeRate : " + exchangeRate) ;
        this.log(FN_NAME + "*** END ***");

        return exchangeRate;
    }

    public TariffRate findTariffRate(Contract inContract, Tariff inTariff)
    {
        String FN_NAME = "(findTariffRate) " ;
        this.log(FN_NAME + "*** START ***");

        LOGGER.debug(FN_NAME + "inContract : " + inContract) ;
        LOGGER.debug(FN_NAME + "inTariff : " + inTariff) ;

        //find rule on same date as inEffectiveDate
        // Find exact match
        TariffRate tariffRate = null;
        DomainQuery dq = QueryUtils.createDomainQuery(BillingEntity.TARIFF_RATE)
                .addDqPredicate(PredicateFactory.eq(BillingField.RATE_CONTRACT, inContract.getContractGkey()))
                .addDqPredicate(PredicateFactory.eq(BillingField.RATE_TARIFF, inTariff.getTariffGkey()))
                .addDqOrdering(Ordering.desc(BillingField.RATE_EFFECTIVE_DATE));

        LOGGER.debug(FN_NAME + "dq : " + dq) ;
        LOGGER.debug(FN_NAME + "dq.toHqlSelectString() : " + dq.toHqlSelectString()) ;

        List tariffRateList = Roastery.getHibernateApi().findEntitiesByDomainQuery(dq);
        LOGGER.debug(FN_NAME + "tariffRateList : " + tariffRateList) ;

        if (tariffRateList != null && !tariffRateList.isEmpty())
        {
            tariffRate = (TariffRate) tariffRateList.get(0);
            LOGGER.debug(FN_NAME + "tariffRate : " + tariffRate) ;
        }

        LOGGER.debug(FN_NAME + "tariffRate : " + tariffRate) ;
        this.log(FN_NAME + "*** END ***");
        return tariffRate;
    }



    private TieredCalculation handleFurturePaidThroughDay(TieredCalculation tieredCalculation
                                                          , TariffRate tariffRate
                                                          , IServiceExtract cue
                                                          , Date dPaidThroughDay)
    {
        String FN_NAME = "(handleFurturePaidThroughDay) " ;
        this.log(FN_NAME + "*** START ***");

        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;
        LOGGER.debug(FN_NAME + "tariffRate : " + tariffRate) ;
        LOGGER.debug(FN_NAME + "cue : " + cue) ;
        LOGGER.debug(FN_NAME + "dPaidThroughDay : " + dPaidThroughDay) ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null (no need to handle further day) ***");
            return tieredCalculation  ;
        }
        if (dPaidThroughDay == null)
        {
            this.log(FN_NAME + "*** dPaidThroughDay == null (no need to handle further day) ***");
            return tieredCalculation  ;
        }

        LOGGER.debug(FN_NAME + "tieredCalculation.getPaidThruDay() : " + tieredCalculation.getPaidThruDay()) ;
        if (dPaidThroughDay <= tieredCalculation.getPaidThruDay())
        {
            this.log(FN_NAME + "*** dPaidThroughDay <= tieredCalculation.getPaidThruDay() (no need to handle further day) ***");
            return tieredCalculation  ;
        }

        boolean bAllowFurturePaidThroughDay = false ;
        String sMessage = "" ;

        sMessage = "Base Status is " + cue.getFieldValue(ArgoExtractField.BEXU_FLEX_LONG02) ;
        if (cue.getFieldValue(ArgoExtractField.BEXU_FLEX_LONG02) == 6)
        {
            bAllowFurturePaidThroughDay = true ;
        }
        sMessage = sMessage + "\n BEXU CATEGORY is " + cue.getFieldValue(ArgoExtractField.BEXU_CATEGORY) ;
        if (cue.getFieldValue(ArgoExtractField.BEXU_CATEGORY) == "IMPRT")
        {
            bAllowFurturePaidThroughDay = true ;
        }

        if (!bAllowFurturePaidThroughDay)
        {
            LOGGER.debug(FN_NAME + "*** not apply for : " + sMessage) ;
            return tieredCalculation  ;
        }

        LOGGER.debug(FN_NAME + "sMessage : " + sMessage) ;
        //printCUE(cue) ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation._entries ;
        LOGGER.debug(FN_NAME + "lTieredCalculationEntry : " + lTieredCalculationEntry);
        LOGGER.debug(FN_NAME + "lTieredCalculationEntry.size() : " + lTieredCalculationEntry.size());

        if (lTieredCalculationEntry.size() == 0)
        {
            // CARU1314021
            //Description: null Start Date: Fri Oct 13 00:00:00 HKT 2017 End Date: Fri Oct 13 00:00:00 HKT 2017 Duration: 1 IsChargeable?: true]
            TieredCalculationEntry tce = new TieredCalculationEntry(tieredCalculation.getPaidThruDay(), tieredCalculation.getPaidThruDay(), 0, true, "") ;
            LOGGER.debug(FN_NAME + "tce : " + tce);

            lTieredCalculationEntry.add(tce) ;
            LOGGER.debug(FN_NAME + "lTieredCalculationEntry : " + lTieredCalculationEntry);

            tieredCalculation._entries = lTieredCalculationEntry ;
            LOGGER.debug(FN_NAME + "tieredCalculation._entries : " + tieredCalculation._entries);
        }
        LOGGER.debug(FN_NAME + "lTieredCalculationEntry : " + lTieredCalculationEntry);

        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
            LOGGER.debug(FN_NAME + "========== Looping item : " + iIndex + " =========");
            item = calculateTieredCalculationEntry(tieredCalculation, item, tariffRate, cue, dPaidThroughDay) ;
            iIndex++ ;
        }
        LOGGER.debug(FN_NAME + "====================================");


        //need handle previuos pTD ???????????????????

        //Since Paid Through Day will be extend to further day, so calculate the Day difference in order to add for to be paid
        Long lDateDiff = oMTLDEVDateUtility.dateDiff(tieredCalculation.getPaidThruDay(), dPaidThroughDay, "DAY") ;
        LOGGER.debug(FN_NAME + "lDateDiff : " + lDateDiff);

        //tieredCalculation._remainingDeltaQty = tieredCalculation._remainingDeltaQty + lDateDiff * iFactor ;
        //tieredCalculation._durationTotal;


        String sTariffName = "" ;
        if (tariffRate != null)
        {
            Tariff t = tariffRate.getRateTariff() ;
            if (tariffRate != null)
            {
                sTariffName = t.getTariffId() ;
            }
        }
        LOGGER.debug(FN_NAME + "sTariffName : " + sTariffName);
        LOGGER.debug(FN_NAME + "sTariffName.subSequence(0, 3) : " + sTariffName.subSequence(0, 3));
        LOGGER.debug(FN_NAME + "serviceExtract.getEventType() : " + cue.getEventType());

        if (sTariffName.subSequence(0, 3) ==  "OTE" && cue.getEventType() == "REEFER" && chargeBy(tieredCalculation) == "HOUR")
        {
            tieredCalculation._qtyOwed = getTieredCalculationEntryDuration(tieredCalculation) ;
        }
        else
        {
            tieredCalculation._qtyOwed = tieredCalculation.getQtyOwed() + lDateDiff  ;
        }
        //tieredCalculation._qtyPaid = tieredCalculation.getQtyPaid() + lDateDiff ;
        tieredCalculation._paidThruDay = dPaidThroughDay ;
        //tieredCalculation._prevPaidThruDay;
        //tieredCalculation._firstPaidDay;
        //tieredCalculation._lastFreeDay;
        //tieredCalculation._outTime;
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation);

        tieredCalculation._entries = lTieredCalculationEntry ;
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation);

        this.log(FN_NAME + "*** END ***");
        return tieredCalculation ;
    }


    private TieredCalculationEntry calculateTieredCalculationEntry(TieredCalculation tieredCalculation
                                                                   , TieredCalculationEntry tieredCalculationEntry
                                                                   , TariffRate tariffRate
                                                                   , IServiceExtract cue
                                                                   , Date dPaidThroughDay)
    {
        String FN_NAME = "(calculateTieredCalculationEntry) " ;
        this.log(FN_NAME + "*** START ***");

        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;
        LOGGER.debug(FN_NAME + "dPaidThroughDay : " + dPaidThroughDay) ;

        LOGGER.debug(FN_NAME + "tieredCalculationEntry.getDescription() : " + tieredCalculationEntry.getDescription()) ;
        String sTieredCalculationEntryDescription = tieredCalculationEntry.getDescription() ;
        if (sTieredCalculationEntryDescription == null)
            sTieredCalculationEntryDescription = "" ;

        String sTariffName = "" ;
        if (tariffRate != null)
        {
            Tariff t = tariffRate.getRateTariff() ;
            if (tariffRate != null)
            {
                sTariffName = t.getTariffId() ;
            }
        }
        LOGGER.debug(FN_NAME + "sTariffName : " + sTariffName);
        LOGGER.debug(FN_NAME + "sTariffName.subSequence(0, 3) : " + sTariffName.subSequence(0, 3));
        LOGGER.debug(FN_NAME + "serviceExtract.getEventType() : " + cue.getEventType());


        // set start time
        if (sTariffName.subSequence(0, 3) ==  "OTE" && cue.getEventType() == "REEFER")
        {
            tieredCalculationEntry._startDate = tieredCalculation._lastFreeDay ;
        }

        if (tieredCalculation._prevPaidThruDay != null)
        {
            // add 1 minute
            //Date dTemp = oMTLDEVDateUtility.addMinute(tieredCalculation._prevPaidThruDay, 1) ;
            //LOGGER.debug(FN_NAME + "dTemp : " + dTemp);
            //tieredCalculationEntry._startDate = dTemp ;
            tieredCalculationEntry._startDate = tieredCalculation._prevPaidThruDay ;
        }

        //set End Time
        tieredCalculationEntry._endDate = dPaidThroughDay ;

        //Long lDateDiff = dPaidThroughDay - tieredCalculation.getPaidThruDay() ;

        Date dStartDate = tieredCalculationEntry.getStartDate() ;
        Date dEndDate = tieredCalculationEntry.getEndDate() ;

        if (tieredCalculationEntry._duration == null)
            tieredCalculationEntry._duration = 0 ;

        // Should check the type of charge by HOUR / DAY ?????????????????????????
        if (chargeBy(tieredCalculation) == "HOUR")
        {
            //tieredCalculationEntry._duration = (dEndDate.getTime() - dStartDate.getTime()) / 86400000 * 24 ;
            tieredCalculationEntry._duration = oMTLDEVDateUtility.dateDiff(dStartDate, dEndDate, 'HOUR') ;
        }
        else
        {
            //tieredCalculationEntry._duration = tieredCalculationEntry.getDuration() + lDateDiff ;
            tieredCalculationEntry._duration = oMTLDEVDateUtility.dateDiff(dStartDate, dEndDate, 'DAY') ;
        }

        /*
        if (sTariffName.subSequence(0, 3) ==  "OTE" && cue.getEventType() == "REEFER")
        {
            //tieredCalculationEntry._duration = (dEndDate.getTime() - dStartDate.getTime()) / 86400000 * 24 ;
            tieredCalculationEntry._duration = oMTLDEVDateUtility.dateDiff(dStartDate, dEndDate, 'HOUR') ;
        }
        else
        {
            //tieredCalculationEntry._duration = tieredCalculationEntry.getDuration() + lDateDiff ;
            tieredCalculationEntry._duration = oMTLDEVDateUtility.dateDiff(dStartDate, dEndDate, 'DAY') ;
        }
        */

        this.log(FN_NAME + "*** END ***");
        return tieredCalculationEntry ;
    }

    private String chargeBy(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(chargeBy) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;

        String sResult = "" ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation._entries ;
        LOGGER.debug(FN_NAME + "lTieredCalculationEntry : " + lTieredCalculationEntry);

        if (lTieredCalculationEntry.size() > 1)
        {
            this.log(FN_NAME + "*** PLEASE Check lTieredCalculationEntry.size() : " + lTieredCalculationEntry.size() + " ***");
        }

        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
            String sTieredCalculationEntryDescription = item.getDescription() ;
            if (sTieredCalculationEntryDescription == null)
                sTieredCalculationEntryDescription = "" ;

            if (sTieredCalculationEntryDescription.contains("CHARGE BY HOUR"))
                sResult = "HOUR" ;

            iIndex++ ;
        }

        LOGGER.debug(FN_NAME + "sResult : " + sResult);
        this.log(FN_NAME + "*** END ***");
        return sResult ;
    }


    private int getTieredCalculationEntryDuration(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(getTieredCalculationEntryDuration) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;

        int iResult = 0 ;

        if (tieredCalculation == null)
            return iResult  ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation._entries ;
        LOGGER.debug(FN_NAME + "lTieredCalculationEntry : " + lTieredCalculationEntry);

        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
            LOGGER.debug(FN_NAME + "========== Looping item : " + iIndex + " =========");
            iResult = iResult + item.getDuration() ;
            iIndex++ ;
        }
        LOGGER.debug(FN_NAME + "====================================");

        LOGGER.debug(FN_NAME + "iResult : " + iResult);
        this.log(FN_NAME + "*** END ***");
        return iResult ;
    }



    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //(printTieredCalculation) tieredCalculation._remainingDeltaQty : 0
    //(printTieredCalculation) tieredCalculation._durationTotal     : 0
    //(printTieredCalculation) tieredCalculation._qtyOwed           : 5.0
    //(printTieredCalculation) tieredCalculation._qtyPaid           : 0.0
    //(printTieredCalculation) tieredCalculation._paidThruDay       : Sat Nov 25 23:59:59 HKT 2017
    //(printTieredCalculation) tieredCalculation._prevPaidThruDay   : Tue Nov 21 00:00:00 HKT 2017
    //(printTieredCalculation) tieredCalculation._firstPaidDay      : Sat Nov 18 20:00:00 HKT 2017
    //(printTieredCalculation) tieredCalculation._lastFreeDay       : Wed Nov 22 20:00:00 HKT 2017
    //(printTieredCalculation) tieredCalculation._outTime           : Mon Nov 20 23:59:00 HKT 2017

    public Long setTieredCalculationDurationTotal(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(calTieredCalculationDurationTotal) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculation) ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return ;
        }

        Long lDurationTotal = 0 ;
        Date dFirstPaidDay = oMTLDEVDateUtility.trim(tieredCalculation._firstPaidDay) ;
        LOGGER.debug(FN_NAME + "dFirstPaidDay : " + dFirstPaidDay) ;
        Date dPaidThruDay = oMTLDEVDateUtility.trim(tieredCalculation._paidThruDay) ;
        LOGGER.debug(FN_NAME + "dPaidThruDay : " + dPaidThruDay) ;

        if (dFirstPaidDay != null && dPaidThruDay != null)
        {
            tieredCalculation._durationTotal = oMTLDEVDateUtility.dateDiff(dFirstPaidDay, dPaidThruDay, 'DAY') + 1 ;
            lDurationTotal = tieredCalculation._durationTotal ;
        }
        else
        {
            this.log(FN_NAME + "*** Unexpect date is null, please check program ***");
            this.log(FN_NAME + "***************************************************");
            this.log(FN_NAME + "dFirstPaidDay : " + dFirstPaidDay) ;
            this.log(FN_NAME + "dPaidThruDay : " + dPaidThruDay) ;
            this.log(FN_NAME + "***************************************************");
        }

        LOGGER.debug(FN_NAME + "lDurationTotal : " + lDurationTotal) ;
        this.log(FN_NAME + "*** END ***");
        return lDurationTotal ;
    }

    public Long setTieredCalculationQtyOwed(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(calTieredCalculationQtyOwed) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculation) ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return ;
        }

        Long lQtyOwed = 0 ;
        Date dFirstPaidDay = oMTLDEVDateUtility.trim(tieredCalculation._firstPaidDay) ;
        LOGGER.debug(FN_NAME + "dFirstPaidDay : " + dFirstPaidDay) ;
        Date dPrevPaidThruDay = oMTLDEVDateUtility.trim(tieredCalculation._prevPaidThruDay) ;
        LOGGER.debug(FN_NAME + "dPrevPaidThruDay : " + dPrevPaidThruDay) ;
        Date dPaidThruDay = oMTLDEVDateUtility.trim(tieredCalculation._paidThruDay) ;
        LOGGER.debug(FN_NAME + "dPaidThruDay : " + dPaidThruDay) ;

        if (dPrevPaidThruDay != null && dPaidThruDay != null)
        {
            tieredCalculation._qtyOwed = oMTLDEVDateUtility.dateDiff(dPrevPaidThruDay, dPaidThruDay, 'DAY') + 1 ;
        }
        else if (dFirstPaidDay != null && dPaidThruDay != null)
        {
            tieredCalculation._qtyOwed = oMTLDEVDateUtility.dateDiff(dFirstPaidDay, dPaidThruDay, 'DAY') + 1 ;
        }
        else
        {
            this.log(FN_NAME + "*** Unexpect date is null, please check program ***");
            this.log(FN_NAME + "***************************************************");
            this.log(FN_NAME + "dFirstPaidDay : " + dFirstPaidDay) ;
            this.log(FN_NAME + "dPrevPaidThruDay : " + dPrevPaidThruDay) ;
            this.log(FN_NAME + "dPaidThruDay : " + dPaidThruDay) ;
            this.log(FN_NAME + "***************************************************");
        }

        lQtyOwed = tieredCalculation._qtyOwed ;

        LOGGER.debug(FN_NAME + "lQtyOwed : " + lQtyOwed) ;
        this.log(FN_NAME + "*** END ***");
        return lQtyOwed ;
    }


    public Long setTieredCalculationQtyPaid(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(calTieredCalculationQtyPaid) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculation) ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return ;
        }

        Long lQtyPaid = 0 ;
        Date dFirstPaidDay = oMTLDEVDateUtility.trim(tieredCalculation._firstPaidDay) ;
        Date dPrevPaidThruDay = oMTLDEVDateUtility.trim(tieredCalculation._prevPaidThruDay) ;

        if (dPrevPaidThruDay == null)
        {
            tieredCalculation._qtyPaid = 0 ;
        }
        if (dFirstPaidDay != null && dPrevPaidThruDay != null)
        {
            tieredCalculation._qtyPaid = oMTLDEVDateUtility.dateDiff(dFirstPaidDay, dPrevPaidThruDay, 'DAY') + 1 ;
        }
        else
        {
            this.log(FN_NAME + "*** Unexpect date is null, please check program ***");
            this.log(FN_NAME + "***************************************************");
            this.log(FN_NAME + "dFirstPaidDay : " + dFirstPaidDay) ;
            this.log(FN_NAME + "dPrevPaidThruDay : " + dPrevPaidThruDay) ;
            this.log(FN_NAME + "***************************************************");
        }

        lQtyPaid = tieredCalculation._qtyPaid ;

        LOGGER.debug(FN_NAME + "lQtyPaid : " + lQtyPaid) ;
        this.log(FN_NAME + "*** END ***");
        return lQtyPaid;
    }


    public Long calTieredCalculationPaidBy(TieredCalculation tieredCalculation, String sType)
    {
        String FN_NAME = "(calTieredCalculationPaidBy) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;
        LOGGER.debug(FN_NAME + "sType             : " + sType) ;

        Long lPaidBy = 0 ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return lPaidBy;
        }

        Date dFirstPaidDay = tieredCalculation._firstPaidDay ;
        Date dPrevPaidThruDay = tieredCalculation._prevPaidThruDay ;

        if (dPrevPaidThruDay < dFirstPaidDay)
        {
            return lPaidBy;
        }

        if (sType == 'DAY')
        {
            dFirstPaidDay = oMTLDEVDateUtility.trim(dFirstPaidDay) ;
            dPrevPaidThruDay = oMTLDEVDateUtility.trim(dPrevPaidThruDay) ;
        }

        if (dFirstPaidDay != null && dPrevPaidThruDay != null)
        {
            lPaidBy = oMTLDEVDateUtility.dateDiff(dFirstPaidDay, dPrevPaidThruDay, sType) + 1;
        }

        this.log(FN_NAME + "lPaidBy : " + lPaidBy);
        this.log(FN_NAME + "*** END ***");
        return lPaidBy ;
    }

    public Date getTieredCalculationStartDate(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(getTieredCalculationStartDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;

        Date dStartDate = null ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return dStartDate;
        }

        List<TieredCalculationEntry> tieredCalculationEntry = tieredCalculation.getEntries() ;
        for (item in tieredCalculationEntry)
        {
            dStartDate = getTieredCalculationEntryStartDate(tieredCalculationEntry) ;
        }

        this.log(FN_NAME + "dStartDate : " + dStartDate);
        this.log(FN_NAME + "*** END ***");
        return dStartDate;
    }

    public Date getTieredCalculationEndDate(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(getTieredCalculationEndDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;

        Date dEndDate = null ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return dEndDate;
        }

        List<TieredCalculationEntry> tieredCalculationEntry = tieredCalculation.getEntries() ;
        for (item in tieredCalculationEntry)
        {
            dEndDate = getTieredCalculationEntryEndDate(tieredCalculationEntry) ;
        }

        this.log(FN_NAME + "dEndDate : " + dEndDate);
        this.log(FN_NAME + "*** END ***");
        return dEndDate;
    }
    ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public String setTieredCalculationEntryStartDate(TieredCalculation tieredCalculation, Date dSetDate)
    {
        String FN_NAME = "(setTieredCalculationEntryStartDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return "FAIL" ;
        }
        oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation.getEntries() ;
        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
            LOGGER.debug(FN_NAME + "========== Looping item : " + iIndex + " =========");
            //tieredCalculationEntry._startDate = item._lastFreeDay ;
            item._startDate = dSetDate ;
            setTieredCalculationEntryDuration(tieredCalculation, item) ;

            iIndex++ ;
            LOGGER.debug(FN_NAME + "==================================================");
        }

        this.log(FN_NAME + "*** END ***");
        return  "SUCCESS" ;
    }

    public String setTieredCalculationEntryEndDate(TieredCalculation tieredCalculation, Date dSetDate)
    {
        String FN_NAME = "(setTieredCalculationEntryEndDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;
		LOGGER.debug(FN_NAME + "dSetDate : " + dSetDate) ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return "FAIL" ;
        }
        oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation.getEntries() ;
        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
            LOGGER.debug(FN_NAME + "========== Looping item : " + iIndex + " =========");
            //tieredCalculationEntry._startDate = item._lastFreeDay ;
            item._endDate = dSetDate ;
            setTieredCalculationEntryDuration(tieredCalculation, item) ;

            iIndex++ ;
            LOGGER.debug(FN_NAME + "==================================================");
        }

        this.log(FN_NAME + "*** END ***");
        return  "SUCCESS" ;
    }

    public Date setTieredCalculationEntryStartDateToLastFreeDate(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(setTieredCalculationEntryStartDateToLastFreeDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;
        Date dStartDate = null ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return dStartDate ;
        }
        oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation.getEntries() ;
        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
            LOGGER.debug(FN_NAME + "========== Looping item : " + iIndex + " =========");
            setTieredCalculationEntryStartDate(tieredCalculation, tieredCalculation._lastFreeDay) ;

            dStartDate = item._startDate ;
            this.log(FN_NAME + "dStartDate : " + dStartDate);
            iIndex++ ;
            LOGGER.debug(FN_NAME + "==================================================");
        }

        this.log(FN_NAME + "*** END ***");
        return dStartDate ;
    }

    public Date setTieredCalculationEntryStartDateToFirstPaidDay(TieredCalculation tieredCalculation)
    {
        String FN_NAME = "(setTieredCalculationEntryStartDateToFirstPaidDay) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculation : " + tieredCalculation) ;
        Date dStartDate = null ;

        if (tieredCalculation == null)
        {
            this.log(FN_NAME + "*** tieredCalculation == null");
            this.log(FN_NAME + "*** END ***");
            return dStartDate ;
        }
        //oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        List<TieredCalculationEntry> lTieredCalculationEntry = tieredCalculation.getEntries() ;
        int iIndex = 1 ;
        for(item in lTieredCalculationEntry)
        {
            LOGGER.debug(FN_NAME + "========== Looping item : " + iIndex + " =========");
            setTieredCalculationEntryStartDate(tieredCalculation, tieredCalculation._firstPaidDay) ;

            dStartDate = item._startDate ;
            this.log(FN_NAME + "dStartDate : " + dStartDate);
            iIndex++ ;
            LOGGER.debug(FN_NAME + "==================================================");
        }

        this.log(FN_NAME + "*** END ***");
        return dStartDate ;
    }

    void setTieredCalculationEntryStartDate(TieredCalculationEntry tieredCalculationEntry, Date dSetDate)
    {
        String FN_NAME = "(setTieredCalculationEntryStartDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;
        LOGGER.debug(FN_NAME + "dSetDate               : " + dSetDate) ;

        if (tieredCalculationEntry == null)
        {
            this.log(FN_NAME + "*** tieredCalculationEntry == null");
            this.log(FN_NAME + "*** END ***");
            return ;
        }

        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._startDate    : " + tieredCalculationEntry._startDate) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._endDate      : " + tieredCalculationEntry._endDate) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._duration     : " + tieredCalculationEntry._duration) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._isChargeable : " + tieredCalculationEntry._isChargeable) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._description  : " + tieredCalculationEntry._description) ;

        tieredCalculationEntry._startDate = dSetDate ;
        setTieredCalculationEntryDuration(tieredCalculation, tieredCalculationEntry) ;

        this.log(FN_NAME + "*** END ***");
        return ;
    }

    void setTieredCalculationEntryEndDate(TieredCalculationEntry tieredCalculationEntry, Date dSetDate)
    {
        String FN_NAME = "(setTieredCalculationEntryStartDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;
        LOGGER.debug(FN_NAME + "dSetDate               : " + dSetDate) ;

        if (tieredCalculationEntry == null)
        {
            this.log(FN_NAME + "*** tieredCalculationEntry == null");
            this.log(FN_NAME + "*** END ***");
            return ;
        }

        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._startDate    : " + tieredCalculationEntry._startDate) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._endDate      : " + tieredCalculationEntry._endDate) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._duration     : " + tieredCalculationEntry._duration) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._isChargeable : " + tieredCalculationEntry._isChargeable) ;
        LOGGER.debug(FN_NAME + "tieredCalculationEntry._description  : " + tieredCalculationEntry._description) ;

        tieredCalculationEntry._endDate = dSetDate ;
        setTieredCalculationEntryDuration(tieredCalculation, tieredCalculationEntry) ;

        this.log(FN_NAME + "*** END ***");
        return ;
    }

    public Long setTieredCalculationEntryDuration(TieredCalculation tieredCalculation, TieredCalculationEntry tieredCalculationEntry)
    {
        String FN_NAME = "(setTieredCalculationEntryDuration) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;
		oMTLDEVPrintObjectUtility.printTieredCalculationEntry(tieredCalculationEntry)

        if (tieredCalculationEntry == null)
        {
            this.log(FN_NAME + "*** tieredCalculationEntry == null");
            this.log(FN_NAME + "*** END ***");
            return ;
        }

        Long lDuration = 0 ;
        Date dStartDate = tieredCalculationEntry._startDate ;
        Date dEndDate = tieredCalculationEntry._endDate ;

        if (chargeBy(tieredCalculation) == "HOUR")
        {
            tieredCalculationEntry._duration = oMTLDEVDateUtility.dateDiff(dStartDate, dEndDate, 'HOUR') + 1 ;
            tieredCalculationEntry._duration = hourToNumberOfUnit(tieredCalculationEntry._duration, 12) ;
        }
        else
        {
            dStartDate = oMTLDEVDateUtility.trim(tieredCalculationEntry._startDate) ;
            dEndDate = oMTLDEVDateUtility.trim(tieredCalculationEntry._endDate) ;
            tieredCalculationEntry._duration = oMTLDEVDateUtility.dateDiff(dStartDate, dEndDate, 'DAY') + 1 ;
        }
        lDuration = tieredCalculationEntry._duration ;

        LOGGER.debug(FN_NAME + "lDuration : " + lDuration) ;
        this.log(FN_NAME + "*** END ***");
        return lDuration ;
    }


    public Date getTieredCalculationEntryStartDate(TieredCalculationEntry tieredCalculationEntry)
    {
        String FN_NAME = "(getTieredCalculationEntryStartDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;

        Date dStartDate = null ;

        if (tieredCalculationEntry == null)
        {
            this.log(FN_NAME + "*** tieredCalculationEntry == null");
            this.log(FN_NAME + "*** END ***");
            return dStartDate;
        }

        dStartDate = tieredCalculationEntry.getStartDate() ;

        this.log(FN_NAME + "dStartDate : " + dStartDate);
        this.log(FN_NAME + "*** END ***");
        return dStartDate;
    }

    public Date getTieredCalculationEntryEndDate(TieredCalculationEntry tieredCalculationEntry)
    {
        String FN_NAME = "(getTieredCalculationEntryStartDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "tieredCalculationEntry : " + tieredCalculationEntry) ;

        Date dEndDate = null ;

        if (tieredCalculationEntry == null)
        {
            this.log(FN_NAME + "*** tieredCalculationEntry == null");
            this.log(FN_NAME + "*** END ***");
            return dEndDate;
        }

        dEndDate = tieredCalculationEntry.getEndDate() ;

        this.log(FN_NAME + "dEndDate : " + dEndDate);
        this.log(FN_NAME + "*** END ***");
        return dEndDate;
    }


    private boolean handleNullTariffRateTier(Invoice invoice, TariffRateTier trt, CurrencyExchangeRate exchangeRate, LinkedHashMap<String, Object> lhmDetails, TieredCalculation tieredCalculation, Map inOutMap)
    {
        String FN_NAME = "(handleNullTariffRateTier) " ;
        this.log(FN_NAME + "*** START ***");

        LOGGER.debug(FN_NAME + "invoice : " + invoice);
        LOGGER.debug(FN_NAME + "trt : " + trt);
        LOGGER.debug(FN_NAME + "lhmDetails : " + lhmDetails);
        LOGGER.debug(FN_NAME + "inOutMap : " + inOutMap);

        if (invoice == null)
        {
            LOGGER.info("invoice == null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }

        Date inPaidThruDay = invoice.getInvoicePaidThruDay();
        LOGGER.debug(FN_NAME + "inPaidThruDay : " + inPaidThruDay);
        inPaidThruDay = oMTLDEVDateUtility.trimUp(inPaidThruDay) ;

        if (trt == null)
        {
            LOGGER.info("trt == null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }

        if (lhmDetails == null)
        {
            LOGGER.info("lhmDetails == null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }

        IServiceExtract serviceExtract = (IServiceExtract) inOutMap.get(BillingPresentationConstants.IN_EXTRACT_EVENT);
        ChargeableUnitEvent cue = null;
        if (serviceExtract instanceof ChargeableUnitEvent)
        {
            cue = (ChargeableUnitEvent) serviceExtract;
        }
        else
        {
            LOGGER.info("NOT instanceof ChargeableUnitEvent.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }

        LOGGER.debug(FN_NAME + "cue : " + cue);
        if (cue == null)
        {
            LOGGER.info("CUE is null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }

        /*
        Tariff tariff = Tariff.findTariff(trt.getTierDescription());
        LOGGER.debug(FN_NAME + "fspTariff : " + fspTariff);
        if (tariff == null)
        {
            LOGGER.info("Tariff is null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }

        TariffRate fspTariffRate = findTariffRate(tariffRate.getRateContract(), tariff);
        LOGGER.debug(FN_NAME + "fspTariffRate : " + fspTariffRate);
        if (fspTariffRate == null)
        {
            LOGGER.info("FSP tariff rate is null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }
        */


        TariffRate tariffRate = (TariffRate) inOutMap.get(BillingPresentationConstants.IN_TARIFF_RATE);
        LOGGER.debug(FN_NAME + "tariffRate : " + tariffRate);
        if (tariffRate == null)
        {
            LOGGER.info("Tariff Rate is null. Hence returning.");
            this.log(FN_NAME + "*** END ***");
            return false;
        }

        Tariff tariff = null ;

        LOGGER.debug(FN_NAME + "trt : " + trt);
        LOGGER.debug(FN_NAME + "trt.getTierGkey() : " + trt.getTierGkey());

        if (trt != null && trt.getTierGkey() > 0 )
        {
            this.log(FN_NAME + "*** trt == null");

            tariffRate = trt.getTierTariffRate() ;
            this.log(FN_NAME + "tariffRate : " + tariffRate);

            tariff = tariffRate.getRateTariff() ;
            LOGGER.debug(FN_NAME + "tariff : " + tariff);
        }
        else
        {
            tariff = Tariff.findTariff(trt.getTierDescription());
            LOGGER.debug(FN_NAME + "tariff : " + tariff);

            tariffRate = findTariffRate(tariffRate.getRateContract(), tariff);
            LOGGER.debug(FN_NAME + "trariffRate : " + tariffRate);
            if (tariffRate == null)
            {
                LOGGER.info("trariffRate is null. Hence returning.");
                this.log(FN_NAME + "*** END ***");
                return false;
            }
        }

        InvoiceItem item = InvoiceItem.createInvoiceItem(invoice, tariffRate, serviceExtract, exchangeRate, 0D, null, null);
        LOGGER.debug(FN_NAME + "item : " + item);
        HibernateApi.getInstance().save(item);
        HibernateApi.getInstance().flush();

        FieldChanges fieldChanges = new FieldChanges();
        fieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, 0D);
        fieldChanges.setFieldChange(BillingField.ITEM_INVOICE, invoice);
        fieldChanges.setFieldChange(BillingField.ITEM_TARIFF_RATE, tariffRate);

        if (trt.toString() == 'TariffRateTier:null')
            fieldChanges.setFieldChange(BillingField.ITEM_DESCRIPTION, tariff.getTariffDescription());
        else
            fieldChanges.setFieldChange(BillingField.ITEM_DESCRIPTION, trt.getTierDescription());

        Double dDaydiff = (Double) lhmDetails.get("DayUsed") - (Double) lhmDetails.get("PaidDay")  ;
        if (dDaydiff < 0)
            dDaydiff = 0 ;

        oMTLDEVPrintObjectUtility.printTieredCalculation(tieredCalculation) ;

        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_BILLED, dDaydiff);
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_UNIT, ServiceQuantityUnitEnum.DAYS);
        //fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY, (Double) lhmDetails.get("DayAllowed") - (Double) lhmDetails.get("DayUsed"));
        fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY, dDaydiff);
        fieldChanges.setFieldChange(BillingField.ITEM_RATE_BILLED, tariffRate.getNullSafeRate());
        fieldChanges.setFieldChange(BillingField.ITEM_FROM_DATE, (Date) lhmDetails.get("DayStartDate"));
        fieldChanges.setFieldChange(BillingField.ITEM_TO_DATE, (Date) lhmDetails.get("DayEndDate"));
        fieldChanges.setFieldChange(BillingField.ITEM_FLAT_RATE_AMOUNT, 0D);
        fieldChanges.setFieldChange(BillingField.ITEM_EVENT_TYPE_ID, cue.getServiceId());
        fieldChanges.setFieldChange(BillingField.ITEM_EVENT_ENTITY_ID, cue.getServiceEntityId());
        //fieldChanges.setFieldChange(BillingField.ITEM_PAID_THRU_DAY, inPaidThruDay);

        fieldChanges.setFieldChange(BillingField.ITEM_PREV_PAID_THRU_DAY, tieredCalculation._prevPaidThruDay);
        Date dPaidThroughDay = oMTLDEVDateUtility.maxDate(tieredCalculation._paidThruDay, tieredCalculation._prevPaidThruDay) ;
        fieldChanges.setFieldChange(BillingField.ITEM_PAID_THRU_DAY, dPaidThroughDay);

        inOutMap.put(BillingPresentationConstants.OUT_AMOUNT, 0D);
        inOutMap.put(BillingPresentationConstants.INV_ITEM_CHANGES, fieldChanges);

        item.applyFieldChanges(fieldChanges) ;

        LOGGER.debug(FN_NAME + "fieldChanges : " + fieldChanges);

        this.log(FN_NAME + "*** END ***");
        return true;
    }


    public List createDayDistributeList(TariffRate tariffRate, IServiceExtract serviceExtract)
    {
        String FN_NAME = "(createDayDistributeList) " ;
        this.log(FN_NAME + "*** START ***");

        List<LinkedHashMap<TariffRateTier, Object>> lmDayDistribute = [] ;

        if (tariffRate == null)
        {
            this.log(FN_NAME + "tariffRate == null");
            this.log(FN_NAME + "*** END ***");
            return lmDayDistribute ;
        }

        LOGGER.debug(FN_NAME + "inTariffRate : " + tariffRate);
        List lTariffRateOrderedTiers = tariffRate.findOrderedTiers();
        LOGGER.debug(FN_NAME + "lTariffRateOrderdTiers : " + lTariffRateOrderedTiers);

        Tariff t = tariffRate.getRateTariff() ;
        String sTariffId = "" ;
        if (t != null)
            sTariffId = t.getTariffId() ;

        if (tariffRate.getRateType() == RateTypeEnum.TIER ||
            tariffRate.getRateType() == RateTypeEnum.CUSTOM_RATE)
        {
            LOGGER.debug(FN_NAME + "tariffRate.getRateType() : " + tariffRate.getRateType());
            Iterator it = lTariffRateOrderedTiers.iterator();
            while (it.hasNext())
            {
                LinkedHashMap<TariffRateTier, Object> lhmTiers = new LinkedHashMap<TariffRateTier, Object>() ;
                TariffRateTier tier = (TariffRateTier) it.next();
                LOGGER.debug(FN_NAME + "Adding tier.getTierDescription() : " + tier.getTierDescription());

                LinkedHashMap<String, Object> lhmDetails = new LinkedHashMap<String, Object>() ;
                lhmDetails.put("DayUsed", 0) ;
                lhmDetails.put("HourUsed", 0) ;

                if (tier.findTierMaxQuantity() == null)
                {
                    lhmDetails.put("DayAllowed", (Long) 999999) ;
                    lhmDetails.put("HourAllowed", (Long) 999999) ;
                }
                else
                {
                    LOGGER.debug(FN_NAME + "tier.getTierMinQuantity() : " + tier.getTierMinQuantity());
                    LOGGER.debug(FN_NAME + "tier.findTierMaxQuantity() : " + tier.findTierMaxQuantity());

                    lhmDetails.put("DayAllowed", tier.findTierMaxQuantity() - tier.getTierMinQuantity()) ;
                    lhmDetails.put("HourAllowed", (tier.findTierMaxQuantity() - tier.getTierMinQuantity()) * 12) ;
                }

                lhmDetails.put("PaidDay", 0) ;
                lhmDetails.put("PaidHour", 0) ;

                lhmDetails.put("DayStartDate", null) ;
                lhmDetails.put("DayEndDate", null) ;
                lhmDetails.put("HourStartDate", null) ;
                lhmDetails.put("HourEndDate", null) ;

                lhmDetails.put("BeforeFirstPaidDay", false) ;

                lhmTiers.put(tier, lhmDetails) ;
                lmDayDistribute.add(lhmTiers) ;
            }
        }
        else if (tariffRate.getRateType() == RateTypeEnum.REGULAR ||
                 tariffRate.getRateType() == RateTypeEnum.BAND ||
                 tariffRate.getRateType() == RateTypeEnum.VOLUME )
        {
            LOGGER.debug(FN_NAME + "tariffRate.getRateType() : " + tariffRate.getRateType());
            LinkedHashMap<TariffRateTier, Object> lhmFSP = new LinkedHashMap<TariffRateTier, Object>() ;

            TariffRateTier trtFSPTemp = new TariffRateTier()  ;
            trtFSPTemp.tierDescription = sTariffId ;
            //trtFSPTemp.tierGkey = -2 ;
            trtFSPTemp.tierMinQuantity = 1 ;
            trtFSPTemp.tierMinAmount = 1 ;

            double dTotalFreeDays = 0 ;
            if (sTariffId == "OT_FSP_FREE_DAY_ALLOW")
                dTotalFreeDays = getTotalFSPDays(serviceExtract) ;
            else
                dTotalFreeDays = getTotalVIPDays(serviceExtract) ;

            LinkedHashMap<String, Object> lhmDetailsFSP = new LinkedHashMap<String, Object>() ;
            lhmDetailsFSP.put("DayUsed", 0) ;
            lhmDetailsFSP.put("HourUsed", 0) ;
            lhmDetailsFSP.put("DayAllowed", dTotalFreeDays) ;
            lhmDetailsFSP.put("HourAllowed", dTotalFreeDays * 24) ;
            lhmDetailsFSP.put("PaidDay", 0) ;
            lhmDetailsFSP.put("PaidHour", 0) ;
            lhmDetailsFSP.put("DayStartDate", null) ;
            lhmDetailsFSP.put("HourStartDate", null) ;
            lhmDetailsFSP.put("DayEndDate", null) ;
            lhmDetailsFSP.put("HourEndDate", null) ;
            lhmDetailsFSP.put("BeforeFirstPaidDay", false) ;

            lhmFSP.put(trtFSPTemp, lhmDetailsFSP) ;
            lmDayDistribute.add(lhmFSP) ;
        }

        LOGGER.debug(FN_NAME + "lmDayDistribute : " + lmDayDistribute);
        this.log(FN_NAME + "*** END ***");
        return lmDayDistribute ;
    }


    public Long hourToNumberOfUnit(double dValue, Integer iModValue)
    {
        String FN_NAME = "(hourToNumberOfUnit) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dValue : " + dValue) ;
        LOGGER.debug(FN_NAME + "iModValue : " + iModValue) ;


        Long lResult = 0 ;

        if (dValue == null)
        {
            this.log(FN_NAME + "*** END ***");
            return lResult
        }

        lResult = (dValue / iModValue).toLong()  ;
        if (dValue % iModValue > 0)
            lResult = lResult + 1 ;
        LOGGER.debug(FN_NAME + "lResult : " + lResult);

        this.log(FN_NAME + "lResult : " + lResult);
        this.log(FN_NAME + "*** END ***");
        return lResult;
    }

    // provide by Navis
    private Date extractInvoicePaidThruDay(Invoice inputInvoice)
    {
        String FN_NAME = "extractInvoicePaidThruDay " ;
        this.log(FN_NAME + "*** START ***");
        Date ptd = null;
        Set parms = inputInvoice.getInvoiceParmValues();

        if (parms != null && !parms.isEmpty()) {
            Iterator it = parms.iterator();
            while (it.hasNext()) {
                InvoiceParmValue parm = (InvoiceParmValue) it.next();
                String metafieldId = parm.getInvparmMetafield();
                String parmValue = parm.getInvparmValue();
                LOGGER.debug(FN_NAME + "metafieldId: " + metafieldId + ",parmValue:"+ parmValue);
                if (parmValue != null) {
                    if (ArgoExtractField.BEXU_PAID_THRU_DAY.getFieldId().equals(metafieldId)) {
                        DateFormat sdf;
                        TimeZone timeZone = ContextHelper.getThreadUserTimezone();
                        try {
                            //Orginal
                            LOGGER.debug(FN_NAME + "1 ptd: " + ptd );
                            String pattern = FrameworkConfig.DATE_TIME_DISPLAY_FORMAT.getSetting(ContextHelper.getThreadUserContext());
                            LOGGER.debug(FN_NAME + "1 pattern: " + pattern );
                            LOGGER.debug(FN_NAME + "1 timeZone: " + timeZone );
                            sdf = new SimpleDateFormat(pattern);
                            sdf.setTimeZone(timeZone);
                            ptd = sdf.parse(parmValue);
                            LOGGER.debug(FN_NAME + "1 ptd: " + ptd );
                        } catch (ParseException parseException) {
                            try {
                                //Add for DCB
                                LOGGER.debug(FN_NAME + "2a ptd: " + ptd);
                                LOGGER.debug(FN_NAME + "2a parmValue: " + parmValue);
                                ptd = DateUtil.xmlDateStringToDate(parmValue);
                                LOGGER.debug(FN_NAME + "2b ptd: " + ptd);
                                String stringDate = DateUtil.convertDateToLocalTime(ptd, timeZone);
                                LOGGER.debug(FN_NAME + "2b stringDate: " + stringDate);
                                ptd = DateUtil.dateStringToDate(stringDate);
                                LOGGER.debug(FN_NAME + "2c ptd: " + ptd);
                                break;
                            } catch (ParseException pe) {
                                try {
                                    //Orginial
                                    LOGGER.debug(FN_NAME + "3a ptd: " + ptd);
                                    LOGGER.debug(FN_NAME + "3a parmValue: " + parmValue);
                                    ptd = DateUtil.dateStringToDate(parmValue);
                                    LOGGER.debug(FN_NAME + "3b ptd: " + ptd);
                                    String stringDate = DateUtil.convertDateToLocalTime(ptd, timeZone);
                                    LOGGER.debug(FN_NAME + "3b stringDate: " + stringDate);
                                    ptd = DateUtil.dateStringToDate(stringDate);
                                    LOGGER.debug(FN_NAME + "3 ptd: " + ptd);
                                    break;
                                } catch (ParseException pe1) {
                                    try {
                                        //Orginial
                                        sdf = new SimpleDateFormat(DATE_FORMAT);
                                        sdf.setTimeZone(timeZone);
                                        ptd = sdf.parse(parmValue);
                                        LOGGER.debug(FN_NAME + "4 ptd: " + ptd);
                                        break;
                                    } catch (ParseException e) {
                                        sdf = new SimpleDateFormat(DATE_ACC_HR_MIN_FROMAT);
                                        sdf.setTimeZone(timeZone);
                                        try {
                                            //Orginial
                                            ptd = sdf.parse(parmValue);
                                            LOGGER.debug(FN_NAME + "5 ptd: " + ptd);
                                            break;
                                        } catch (ParseException e1) {
                                            throw BizFailure.create(BillingPropertyKeys.PTD_INVALID_FORMAT, e, parmValue);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        this.log(FN_NAME + "*** END ***" + ptd);
        return ptd;
    }

    private static final String DATE_FORMAT = "E MMM dd HH:mm:ss z yyyy";
    private static final String DATE_ACC_HR_MIN_FROMAT = "yy-MMM-dd HHmm";

    private static String NEXT_LINE = "\n";
    public final String GROOVY_DATE_FORMAT = "yyyy/MM/dd";
    public final String GROOVY_TIME_FORMAT = "HH:mm";

    private final MetafieldId fspDays = ArgoExtractField.BEXU_FLEX_DOUBLE03;
    private final MetafieldId vipDaysMetaField = ArgoExtractField.BEXU_FLEX_DOUBLE04;

    private final MetafieldId fspTariffGKey = BillingField.RATE_FLEX_STRING01;
    private final MetafieldId vipTariffGkey = BillingField.RATE_FLEX_STRING02;

    private final String FSP_TARIFF_NAME = "OT_FSP_FREE_DAY_ALLOW";

    private Object oMTLDEVPrintObjectUtility = new GroovyApi().getGroovyClassInstance("MTLDEVPrintObjectUtility");
    private Object oMTLDEVDateUtility = new GroovyApi().getGroovyClassInstance("MTLDEVDateUtility");

    private final Logger LOGGER = Logger.getLogger(this.class);
}