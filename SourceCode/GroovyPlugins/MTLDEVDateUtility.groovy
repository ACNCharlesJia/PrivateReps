package SourceCode.GroovyPlugins;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.FileUtils;
import com.navis.argo.ContextHelper
import org.apache.log4j.Logger
import com.navis.argo.business.api.GroovyApi

/*
  Author: Edward Ip
  Date: 2017/11/21
  TD111: Container Logic Utility

  This groovy script will be loaded to N4 database via Code Extensions View
  Installation Instructions:
	1. Go to Administration --> System --> Groovy Plug-ins
	2. Click Add (+)
	3. Enter Groovy Name as MTLDEVDateUtility
	4. Save it.

  Create Groovy Job for the mentioned groovy

 **
 Modifier:
 Date    :
 Purpose :
 */

/* Run at script runner
import com.navis.argo.business.api.GroovyApi

class GroovyHello extends GroovyApi {
   public String execute()
   {
       Object oMTLDEVDateUtility = new GroovyApi().getGroovyClassInstance("MTLDEVDateUtility");

       String sResult = "" ;

       sResult = sResult + oMTLDEVDateUtility.trim(oMTLDEVDateUtility.stringToDate('2017/11/02 12:34:56')) + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.trimUpToHour(oMTLDEVDateUtility.stringToDate('2017/11/02 12:34:56')) + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.trimUp(oMTLDEVDateUtility.stringToDate('2017/11/02 12:34:56')) + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.minDate(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), oMTLDEVDateUtility.stringToDate('2017/11/01 01:00:00')) + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.maxDate(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), oMTLDEVDateUtility.stringToDate('2017/11/01 01:00:00')) + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.dateDiffInNoOfUnitByNoOfHour(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), oMTLDEVDateUtility.stringToDate('2017/11/02 01:00:00'), 12) + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00') + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.dateDiff('2017/11/01 00:00:00', '2017/11/02 00:00:00', 'DAY') + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.dateDiff('2017/11/01 00:00:00', '2017/11/02 01:00:00', 'HOUR') + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.dateDiff('2017/11/01 00:00:00', '2017/11/02 00:10:00', 'MINUTE')  + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.addValueToDateDay(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), 1, "DATE") ;
       sResult = sResult + oMTLDEVDateUtility.addValueToDateDay(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), 1, "HOUR") ;
       sResult = sResult + oMTLDEVDateUtility.addValueToDateDay(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), 1, "MINUTE") ;
       sResult = sResult + oMTLDEVDateUtility.addDay(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), 2)  + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.addHour(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), 2)  + oMTLDEVDateUtility.NEXT_LINE ;
       sResult = sResult + oMTLDEVDateUtility.addMinute(oMTLDEVDateUtility.stringToDate('2017/11/01 00:00:00'), 2)  + oMTLDEVDateUtility.NEXT_LINE ;

       return sResult ;
   }
}
*/

public class MTLDEVDateUtility extends GroovyApi {
    // get date and change to 00:00:00
    public Date trim(Date date)
    {
        String FN_NAME = "(trim) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "date : " + date) ;

        if (date == null)
        {
            this.log(FN_NAME + "date == null");
            this.log(FN_NAME + "*** END ***");
            return null ;
        }

        Calendar cal = Calendar.getInstance(ContextHelper.getThreadUserTimezone());
        LOGGER.debug(FN_NAME + "cal : " + cal) ;

        cal.clear();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        LOGGER.debug(FN_NAME + "cal.getTime() : " + cal.getTime()) ;
        this.log(FN_NAME + "*** END ***");
        return cal.getTime();
    }

    // get date and change to 23:59:59
    public Date trimUp(Date date)
    {
        String FN_NAME = "(trimUp) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "date : " + date) ;

        if (date == null)
        {
            this.log(FN_NAME + "date == null");
            this.log(FN_NAME + "*** END ***");
            return null ;
        }

        Calendar cal = Calendar.getInstance(ContextHelper.getThreadUserTimezone());
        LOGGER.debug(FN_NAME + "cal : " + cal) ;

        cal.clear();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        LOGGER.debug(FN_NAME + "cal.getTime() : " + cal.getTime()) ;
        this.log(FN_NAME + "*** END ***");
        return cal.getTime();
    }


    // get date and change to HH:59:59
    public Date trimUpToHour(Date date)
    {
        String FN_NAME = "(trimUpToHour) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "date : " + date) ;

        if (date == null)
        {
            this.log(FN_NAME + "date == null");
            this.log(FN_NAME + "*** END ***");
            return null ;
        }

        Calendar cal = Calendar.getInstance(ContextHelper.getThreadUserTimezone());
        LOGGER.debug(FN_NAME + "cal : " + cal) ;

        cal.clear();
        cal.setTime(date);
        //cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 59);

        LOGGER.debug(FN_NAME + "cal.getTime() : " + cal.getTime()) ;
        this.log(FN_NAME + "*** END ***");
        return cal.getTime();
    }


    public Date minDate(Date date1, Date date2)
    {
        String FN_NAME = "(minDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "date1 : " + date1) ;
        LOGGER.debug(FN_NAME + "date2 : " + date2) ;

        Date dResult = null ;
        if (date1 == null)
        {
            this.log(FN_NAME + "*** END ***");
            return date2 ;
        }
        if (date2 == null)
        {
            this.log(FN_NAME + "*** END ***");
            return date1 ;
        }

        if (date1 > date2)
            dResult = date2 ;
        else
            dResult = date1 ;

        LOGGER.debug(FN_NAME + "dResult : " + dResult) ;
        this.log(FN_NAME + "*** END ***");
        return dResult;
    }

    public Date maxDate(Date date1, Date date2)
    {
        String FN_NAME = "(maxDate) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "date1 : " + date1) ;
        LOGGER.debug(FN_NAME + "date2 : " + date2) ;

        Date dResult = null ;
        if (date1 == null)
        {
            this.log(FN_NAME + "*** END ***");
            return date2 ;
        }
        if (date2 == null)
        {
            this.log(FN_NAME + "*** END ***");
            return date1 ;
        }

        if (date1 < date2)
            dResult = date2 ;
        else
            dResult = date1 ;

        LOGGER.debug(FN_NAME + "dResult : " + dResult) ;
        this.log(FN_NAME + "*** END ***");
        return dResult;
    }


    // String sDate format : YYYY/MM/DD HH:mm:ss
    public Date stringToDate(String sDate)
    {
        String FN_NAME = "(dateDiff) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "sDate : " + sDate);

        Date dResult = new Date().parse("yyyy/MM/dd HH:mm:ss", sDate) ;
        LOGGER.debug(FN_NAME + "dResult : " + dResult);

        this.log(FN_NAME + "*** END ***");
        return dResult ;
    }

    // String sStart format : YYYY/MM/DD HH:mm:ss
    public Long dateDiff(String sStart, String sEnd, String sType)
    {
        String FN_NAME = "(dateDiff) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "sStart : " + sStart);
        LOGGER.debug(FN_NAME + "sEnd   : " + sEnd);
        LOGGER.debug(FN_NAME + "sType  : " + sType);

        Date dStart = stringToDate(sStart) ;
        Date dEnd = stringToDate(sEnd) ;

        Long lResult = dateDiff(dStart, dEnd, sType) ;
        //LOGGER.debug(FN_NAME + "lResult : " + lResult + " " + sType);

        this.log(FN_NAME + "*** END ***");
        return lResult ;
    }


    public Long dateDiff(Date dStart, Date dEnd, String sType)
    {
        String FN_NAME = "(dateDiff) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dStart : " + dStart);
        LOGGER.debug(FN_NAME + "dEnd   : " + dEnd);

        Long lResult = null ;
        List<String> lsTypes = [] ;
        lsTypes.add("DAY") ;
        lsTypes.add("HOUR") ;
        lsTypes.add("MINUTE") ;
        lsTypes.add("SECOND") ;

        if (dStart == null)
        {
            this.log(FN_NAME + "dStart is null.");
            return lResult ;
        }

        if (dEnd == null)
        {
            this.log(FN_NAME + "dEnd is null.");
            return lResult ;
        }

        if (!(lsTypes.contains(sType)))
        {
            this.log(FN_NAME + "sType is not in the list " + lsTypes);
            return lResult ;
        }


        Double dTimeDiffInSecond = (dEnd.getTime() - dStart.getTime()) / 1000  ;

        if (sType == "DAY")
        {
            lResult = (dTimeDiffInSecond / 60 / 60 / 24).toLong() ;
        }
        else if (sType == "HOUR")
        {
            lResult = (dTimeDiffInSecond / 60 / 60).toLong() ;
        }
        else if (sType == "MINUTE")
        {
            lResult = (dTimeDiffInSecond / 60).toLong() ;
        }
        else if (sType == "SECOND")
        {
            lResult = dTimeDiffInSecond.toLong() ;
        }

        LOGGER.debug(FN_NAME + "lResult : " + lResult + " " + sType);

        this.log(FN_NAME + "*** END ***");
        return lResult ;
    }

    public Long dateDiffInNoOfUnitByNoOfHour(Date dStart, Date dEnd, Long lNoOfHours)
    {
        String FN_NAME = "(dateDiffInNoOfUnitByNoOfHour) " ;
        this.log(FN_NAME + "*** START ***");

        LOGGER.debug(FN_NAME + "dStart : " + dStart) ;
        LOGGER.debug(FN_NAME + "dEnd : " + dEnd) ;
        LOGGER.debug(FN_NAME + "lNoOfHours : " + lNoOfHours) ;

        Long lResult = 0 ;

        if (lNoOfHours < 0)
        {
            this.log(FN_NAME + "*** lNoOfHours < 0 *** ");
            return lResult ;
        }

        if (dStart >= dEnd)
        {
            this.log(FN_NAME + "*** dStart >= dEnd *** ");
            return lResult ;
        }

        Long ldateDiff = dateDiff(dStart, dEnd, "HOUR") ;
        LOGGER.debug(FN_NAME + "ldateDiff : " + ldateDiff) ;

        lResult = (ldateDiff / lNoOfHours).toLong() ;

        if ((ldateDiff % lNoOfHours) > 0 )
            lResult = lResult + 1 ;

        LOGGER.debug(FN_NAME + "lResult : " + lResult) ;

        this.log(FN_NAME + "*** END ***");
        return lResult ;
    }

    public Date changeTimeFromValueToValue(Date dPaidThroughDate)
    {
        String FN_NAME = "(changeTimeFromValueToValue) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dPaidThroughDate : " + dPaidThroughDate);

        Date dPTD = changeTimeFromValueToValue(dPaidThroughDate, "0000", "2359") ;
        LOGGER.debug(FN_NAME + "dPTD : " + dPTD);
        return dPTD ;
    }

    public Date changeTimeFromValueToValue(Date dPaidThroughDate, String sFromTime, String sToTime)
    {
        String FN_NAME = "(changeTimeFromValueToValue) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dPaidThroughDate : " + dPaidThroughDate);

        Date dPTD = null ;

        String sPaidThroughDay = dPaidThroughDate.format(GROOVY_DATE_FORMAT) ;
        LOGGER.debug(FN_NAME + "sPaidThroughDay : " + sPaidThroughDay);
        String sPaidThroughTime = dPaidThroughDate.format(GROOVY_HM_FORMAT) ;
        LOGGER.debug(FN_NAME + "sPaidThroughTime : " + sPaidThroughTime);
        if (sPaidThroughTime == sFromTime)
            sPaidThroughTime = sToTime ;
        LOGGER.debug(FN_NAME + "sPaidThroughTime : " + sPaidThroughTime);

        dPTD = new Date().parse("yyyy/MM/dd HHmm", sPaidThroughDay + " " + sPaidThroughTime) ;

        LOGGER.debug(FN_NAME + "dPTD : " + dPTD);
        return dPTD ;
    }

    // sType possible value : DATE, HOUR, MINUTE
    public Date addValueToDateDay(Date dateInstance, Integer number, String sType)
    {
        String FN_NAME = "(addValueToDateDay) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dateInstance : " + dateInstance) ;
        LOGGER.debug(FN_NAME + "number : " + number) ;
        LOGGER.debug(FN_NAME + "sType : " + sType) ;

        Date newCalculatedDate = null ;

        if (dateInstance != null)
        {
            if(sType == "DATE")
            {
                newCalculatedDate = addDay(dateInstance, number) ;
            }
            else if(sType == "HOUR")
            {
                newCalculatedDate = addHour(dateInstance, number) ;
            }
            else if(sType == "MINUTE")
            {
                newCalculatedDate = addMinute(dateInstance, number) ;
            }
        }

        LOGGER.debug(FN_NAME + "newCalculatedDate : " + newCalculatedDate) ;
        this.log(FN_NAME + "*** END ***");
        return newCalculatedDate;
    }


    public Date addDay(Date dateInstance, Integer number)
    {
        String FN_NAME = "(addDay) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dateInstance : " + dateInstance) ;
        LOGGER.debug(FN_NAME + "number : " + number) ;

        Date newCalculatedDate = null ;

        if (dateInstance != null && number != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateInstance);
            cal.add(Calendar.DATE, number);
            newCalculatedDate = cal.getTime();
        }

        LOGGER.debug(FN_NAME + "newCalculatedDate : " + newCalculatedDate) ;
        this.log(FN_NAME + "*** END ***");
        return newCalculatedDate;
    }

    public Date addHour(Date dateInstance, Integer iHour)
    {
        String FN_NAME = "(addHour) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dateInstance : " + dateInstance) ;
        LOGGER.debug(FN_NAME + "iHour : " + iHour) ;

        Date newCalculatedDate = null ;

        if (dateInstance != null && iHour != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateInstance);
            cal.add(Calendar.HOUR, iHour);
            newCalculatedDate = cal.getTime();
        }

        LOGGER.debug(FN_NAME + "newCalculatedDate : " + newCalculatedDate) ;
        this.log(FN_NAME + "*** END ***");
        return newCalculatedDate;
    }

    public Date addMinute(Date dateInstance, Integer iMinutes)
    {
        String FN_NAME = "(addMinute) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "dateInstance : " + dateInstance) ;
        LOGGER.debug(FN_NAME + "iMinutes : " + iMinutes) ;

        Date newCalculatedDate = null ;

        if (dateInstance != null && iMinutes != null)
        {
            Calendar cal = Calendar.getInstance();
            cal.setTime(dateInstance);
            cal.add(Calendar.MINUTE, iMinutes);
            newCalculatedDate = cal.getTime();
        }

        LOGGER.debug(FN_NAME + "newCalculatedDate : " + newCalculatedDate) ;
        this.log(FN_NAME + "*** END ***");
        return newCalculatedDate;
    }

    public Date combine1Datetand2Time(Date date1, Date date2)
    {
        String FN_NAME = "(combine1Datetand2Time) " ;
        this.log(FN_NAME + "*** START ***");
        LOGGER.debug(FN_NAME + "date1 : " + date1) ;
        LOGGER.debug(FN_NAME + "date2 : " + date2) ;

        if (date1 == null)
        {
            this.log(FN_NAME + "date1 == null");
            this.log(FN_NAME + "*** END ***");
            return null ;
        }

        if (date2 == null)
        {
            this.log(FN_NAME + "date2 == null");
            this.log(FN_NAME + "*** END ***");
            return null ;
        }

        Calendar cal = Calendar.getInstance(ContextHelper.getThreadUserTimezone());
        LOGGER.debug(FN_NAME + "cal : " + cal) ;

        cal.clear();
        cal.setTime(date1);

        Calendar cal2 = Calendar.getInstance(ContextHelper.getThreadUserTimezone());
        LOGGER.debug(FN_NAME + "cal2 : " + cal2) ;

        cal2.clear();
        cal2.setTime(date2);

        cal.set(Calendar.HOUR_OF_DAY, cal2.get(Calendar.HOUR_OF_DAY));
        cal.set(Calendar.MINUTE, cal2.get(Calendar.MINUTE));
        cal.set(Calendar.SECOND, cal2.get(Calendar.SECOND));

        LOGGER.debug(FN_NAME + "cal.getTime() : " + cal.getTime()) ;
        this.log(FN_NAME + "*** END ***");
        return cal.getTime();
    }


    private static String NEXT_LINE = "\n";
    public final String GROOVY_YMDHMS_FORMAT = "yyyy/MM/dd HH:mm:ss";
    public final String GROOVY_YMDHM_FORMAT = "yyyy/MM/dd HHmm";
    public final String GROOVY_YMDH_FORMAT = "yyyy/MM/dd HH";
    public final String GROOVY_YMD_FORMAT = "yyyy/MM/dd";
    public final String GROOVY_HM_FORMAT = "HHmm";
    public final String GROOVY_DATE_FORMAT = "yyyy/MM/dd";
    public final String GROOVY_TIME_FORMAT = "HH:mm";


    private final Logger LOGGER = Logger.getLogger(this.class) ;
}