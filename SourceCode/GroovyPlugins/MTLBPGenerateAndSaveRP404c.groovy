package SourceCode.GroovyPlugins

import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.GeneralReference;
import java.io.File;
import java.util.Date;
import java.util.Map;

public class MTLBPGenerateAndSaveRP404c extends GroovyApi {
    public void execute(Map map) {
        String FN_NAME = ".execute " ;
        if (DEBUG_MODE) this.log(FN_NAME + "Start.");

        GenerateAndSaveRP404cFile();

        if (DEBUG_MODE) this.log(FN_NAME + "End.");
    }
    // RP404 Type C*************************************************** //
    public String GenerateAndSaveRP404cFile() {
        String FN_NAME = ".GenerateAndSaveRP404cFile " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "No Parameter");

        Date dStartDate = new Date() ;
        String sStartDate = dStartDate.format(GROOVY_DATE_FORMAT);
        if (DEBUG_MODE) log(FN_NAME + "sStartDate : " + sStartDate);
        dStartDate = MTLBPGenerateReportInReportDefinition.GetStartDateTimeByYearMonthDay(sStartDate);

        Date dEndDate = new Date() ;
        String sEndDate = dEndDate.format(GROOVY_DATE_FORMAT) ;
        if (DEBUG_MODE) log(FN_NAME + "sEndDate : " + sEndDate);
        dEndDate = MTLBPGenerateReportInReportDefinition.GetEndDateTimeByYearMonthDay(sEndDate);

        String sNewFile = GenerateAndSaveRP404cFile(dStartDate, dEndDate);

        return sNewFile;
    }

    public String GenerateAndSaveRP404cFile(String sDate) {
        String FN_NAME = ".GenerateAndSaveRP404cFile " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "sDate : " + sDate);

        Date dStartDate = MTLBPGenerateReportInReportDefinition.GetStartDateTimeByYearMonthDay(sDate) ;
        if (DEBUG_MODE) log(FN_NAME + "dStartDate : " + dStartDate.toString());

        Date dEndDate = MTLBPGenerateReportInReportDefinition.GetEndDateTimeByYearMonthDay(sDate) ;
        if (DEBUG_MODE) log(FN_NAME + "dEndDate : " + dEndDate.toString());

        String sNewFile = GenerateAndSaveRP404cFile(dStartDate, dEndDate) ;

        return sNewFile ;
    }

    public String GenerateAndSaveRP404cFile(Date dStartDate, Date dEndDate) {
        String FN_NAME = ".GenerateAndSaveRP404cFile " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "dStartDate : " + dStartDate.toString());
        if (DEBUG_MODE) log(FN_NAME + "dEndDate : " + dEndDate.toString());

        File fRP404c = GenerateRP404cFile(dStartDate, dEndDate) ;
        if (DEBUG_MODE) log(FN_NAME + "fRP404c : " + fRP404c.toString());

        return fRP404c.getAbsolutePath() ;
    }

    public String GenerateAndSaveRP404cFile(String sStartDate, String sEndDate) {
        String FN_NAME = ".GenerateAndSaveRP404cFileByString " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "sStartDate : " + sStartDate);
        if (DEBUG_MODE) log(FN_NAME + "sEndDate : " + sEndDate);

        Date dStartDate = new Date().parse("yyyy/MM/dd", sStartDate);
        Date dEndDate = new Date().parse("yyyy/MM/dd", sEndDate);

        String sNewFile = GenerateAndSaveRP404cFile(dStartDate, dEndDate);

        return sNewFile;
    }

    private File GenerateRP404cFile(Date dStartDate, Date dEndDate) {
        String FN_NAME = ".GenerateRP404cFile ";
        if (DEBUG_MODE) log(FN_NAME + "Start.");

        File fRP404c = null;
        String sReportId = "RP404 N4 DAILY BILLING EXCEPTION REPORT";//Report Id

        fRP404c = getRP404cFile(sReportId, dStartDate, dEndDate, "");
        return fRP404c ;
    }

    private File getRP404cFile(String reportID, Date dStartDate, Date dEndDate, String mimeType) {
        String FN_NAME = ".getRP404cFile ";
        if (DEBUG_MODE) this.log(FN_NAME + "Start.");

        String xmlQuery = "<report-def>" +
                "        <get-report-url report-name=" + '"' + reportID + '"' + ">" +
                "            <parameters>" +
                "                 <parameter name=" + '"' + "Start Date" + '"' + " value=" + '"' + dStartDate.format(GROOVY_DATE_HQL_FORMAT) + '"' + "/>" +
                "                 <parameter name=" + '"' + "End Date" + '"' + " value=" + '"' + dEndDate.format(GROOVY_DATE_HQL_FORMAT) + '"' + "/>" +
                "            </parameters>" +
                "        </get-report-url>" +
                "</report-def>";

        String sURL = MTLBPGenerateReportInReportDefinition.getURLString(xmlQuery) ;
        if (DEBUG_MODE) this.log(FN_NAME + "sURL : " + sURL);

        GeneralReference gfFileSavePath = GeneralReference.findUniqueEntryById("BP_CONFIGURATION", "RPT_N4BEXRPTPATH");
        String sFileSavePath = "/tmp" ;
        if (gfFileSavePath != null)
            if (gfFileSavePath.getRefValue1() != null)
                sFileSavePath = gfFileSavePath.getRefValue1() ;
        if (DEBUG_MODE) log(FN_NAME + "sFileSavePath : " + sFileSavePath);

        String sExtension = mimeType;
        if (mimeType == "") {
            sExtension = MTLBPGenerateReportInReportDefinition.getReportDefinitionOutputType(reportID) ;
        }

        Date dCurrent = new Date();
        String sFilename = "RP404_Billing_Exception_Report_Inv_" + dCurrent.format(GROOVY_DATE_HOUR_MIN_FORMAT) + "." + sExtension;
        sFilename = sFilename.replace("/", "").replace(" ", "") ;
        File outFile = MTLBPGenerateReportInReportDefinition.getFile(sURL, sFileSavePath, sFilename);

        if (DEBUG_MODE) log(FN_NAME + "outFile.getAbsoluteFile() : " + outFile.getAbsoluteFile().toString());
        if (DEBUG_MODE) log(FN_NAME + "End.");
        return outFile;
    }

    private final boolean DEBUG_MODE = true;
    public final String GROOVY_DATE_TIME_FORMAT = "yyyyMMddHHmm";
    public final String GROOVY_DATE_HOUR_MIN_FORMAT = "yyyy/MM/dd HHmm";
    public final String GROOVY_DATE_FORMAT = "yyyy/MM/dd";
    public final String GROOVY_DATE_HQL_FORMAT = "dd-MMM-yy hh:mm:ssaa";

    private static String NEXT_LINE = "\n";
    private GroovyApi MTLBPGenerateReportInReportDefinition = (GroovyApi) (new GroovyApi()).getGroovyClassInstance("MTLBPGenerateReportInReportDefinition");
}