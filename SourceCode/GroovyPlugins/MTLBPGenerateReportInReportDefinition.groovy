package SourceCode.GroovyPlugins

import com.navis.argo.ArgoConfig;
import com.navis.argo.ContextHelper;
import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.atoms.ReportOutputTypeEnum;
import com.navis.argo.business.model.Complex;
import com.navis.argo.business.model.Facility;
import com.navis.argo.business.model.GeneralReference;
import com.navis.argo.business.model.Operator;
import com.navis.argo.business.model.Yard;
import com.navis.argo.business.reports.ReportDefinition;
import com.navis.argo.util.FileUtil;
import com.navis.argo.util.XmlUtil;
import com.navis.argo.webservice.types.v1_0.GenericInvokeResponseWsType;
import com.navis.argo.webservice.types.v1_0.QueryResultType;
import com.navis.argo.webservice.types.v1_0.ResponseType;
import com.navis.argo.webservice.types.v1_0.ScopeCoordinateIdsWsType;
import com.navis.framework.business.Roastery;
import com.navis.framework.portal.UserContext;
import com.navis.framework.util.BaseConfigurationProperties;
import com.navis.framework.util.scope.ScopeCoordinates;
import com.navis.www.services.argoservice.ArgoServiceLocator;
import com.navis.www.services.argoservice.ArgoServicePort;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.StringTokenizer;
import javax.xml.rpc.Stub;

import org.jdom.Document;
import org.jdom.Element;

public class MTLBPGenerateReportInReportDefinition extends GroovyApi {
    public void execute(Map map) {
        String FN_NAME = ".execute " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "End.");
    }

    private String getURLString(String sXMLQuery) {
        String FN_NAME = ".getURLString " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "sXMLQuery : " + sXMLQuery);

        GenericInvokeResponseWsType response = null;
        try {
            response = callGenericWebservice(sXMLQuery);
            if (DEBUG_MODE) log(FN_NAME + "response : " + response.toString());
        } catch (Exception e) {
            localLog("Exception in execute method:" + e.getMessage());
            throw e;
        }

        if (response == null) {
            registerError("Null response ");
            return null;
        }

        if (DEBUG_MODE) log(FN_NAME + "response.getCommonResponse() : " + response.getCommonResponse().toString());
        if (DEBUG_MODE) log(FN_NAME + "response.getResponsePayLoad() : " + response.getResponsePayLoad().toString());
        if (DEBUG_MODE) log(FN_NAME + "response.getStatus() : " + response.getStatus().toString());
        ResponseType commonResponse = response.getCommonResponse();

        if (DEBUG_MODE) log(FN_NAME + "commonResponse.getStatus() : " + commonResponse.getStatus());
      //if (DEBUG_MODE) log(FN_NAME + "commonResponse.getQueryResults(0) : " + commonResponse.getQueryResults(0).toString());
        if (DEBUG_MODE) log(FN_NAME + "commonResponse.getQueryResults() : " + commonResponse.getQueryResults().toString());

        QueryResultType[] queryResultTypes = commonResponse.getQueryResults();
        if (queryResultTypes == null) {
            registerError("queryResultType is null");
            return null;
        }

        if (DEBUG_MODE) log(FN_NAME + "queryResultType.getResult() : " + queryResultTypes[0].getResult().toString());
        String urlString = queryResultTypes[0].getResult();
        localLog("URL:" + urlString);
        if (DEBUG_MODE) log(FN_NAME + "End.");

        return urlString;
    }


    private String getReportDefinitionOutputType(String reportID) {
        String FN_NAME = ".getReportDefinitionOutputType ";
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        String sExtension ="";

        ReportDefinition rd = ReportDefinition.findReportDefinition(reportID) ;
        if (rd != null) {
            ReportOutputTypeEnum rote = rd.getRepdefOutputType() ;
            if (DEBUG_MODE) log(FN_NAME + "rote : " + rote.toString());
            if (rote != null) {
                sExtension = rote.getName() ;
                if (DEBUG_MODE) log(FN_NAME + "sExtension : " + sExtension);
                sExtension = sExtension.toLowerCase() ;
                if (DEBUG_MODE) log(FN_NAME + "sExtension : " + sExtension);
            }
        }
        if (DEBUG_MODE) log(FN_NAME + "End.");
        return sExtension;
    }

    //private static GenericInvokeResponseWsType callGenericWebservice(String inQueryXML) throws Exception {
    private GenericInvokeResponseWsType callGenericWebservice(String inQueryXML) throws Exception {
        String FN_NAME = ".GenericInvokeResponseWsType " ;
        GenericInvokeResponseWsType response = null;
        ScopeCoordinateIdsWsType scope = new ScopeCoordinateIdsWsType();

        UserContext uc = ContextHelper.getThreadUserContext();
        if (DEBUG_MODE) log(FN_NAME + "uc : " + uc.toString());

        ScopeCoordinates scp = uc.getScopeCoordinate();
        if (DEBUG_MODE) log(FN_NAME + "scp : " + scp.toString());

        if (scp.toString().contains(".")) {
            if (DEBUG_MODE) log(FN_NAME + "scp as Number Format");
            StringTokenizer token = new StringTokenizer(scp.toString(), ".");

            String sOperatorId = "" ;
            if (token.hasMoreTokens()) {
                sOperatorId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sOperatorId : " + sOperatorId);
                scope.setOperatorId(sOperatorId);
            }

            String sComplexId = "" ;
            if (token.hasMoreTokens()) {
                sComplexId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sComplexId : " + sComplexId);
                scope.setComplexId(sComplexId);
            }

            String sFacilityId = "" ;
            if (token.hasMoreTokens()) {
                sFacilityId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sFacilityId : " + sFacilityId);
                scope.setFacilityId(sFacilityId);
            }

            String sYardId = "" ;
            if (token.hasMoreTokens()) {
                sYardId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sYardId : " + sYardId);
                scope.setYardId(sYardId);
            }

            Operator operator = null ;
            if (scope.getOperatorId() != null)
               operator = Operator.loadByGkey(scope.getOperatorId().toLong()) ;
            if (DEBUG_MODE) log(FN_NAME + "operator : " + operator.toString());
               
            Complex complex = null ;
            if (scope.getComplexId() != null)
               complex = Complex.loadByGkey(scope.getComplexId().toLong()) ;
            if (DEBUG_MODE) log(FN_NAME + "complex : " + complex.toString());
               
            Facility facility = null ;
            if (scope.getFacilityId() != null)
               facility = Facility.loadByGkey(scope.getFacilityId().toLong()) ;
            if (DEBUG_MODE) this.log(FN_NAME + "facility : " + facility.toString());
               
            Yard yard = null ;
            if (scope.getYardId() != null)
               yard = Yard.loadByGkey(scope.getYardId().toLong()) ;
            if (DEBUG_MODE) log(FN_NAME + "yard : " + yard.toString());

            if (operator != null)
                scope.setOperatorId(operator.getId());
            if (complex != null)
                scope.setComplexId(complex.getId());
            if (facility != null)
                scope.setFacilityId(facility.getId());
            if (yard != null)
                scope.setYardId(yard.getId());
        } else {
            if (DEBUG_MODE) log(FN_NAME + "scp as String Format");
            StringTokenizer token = new StringTokenizer(scp.getBusinessCoords(), "/");
            if (DEBUG_MODE) log(FN_NAME + "token : " + token.toString());

            String sOperatorId = "" ;
            if (token.hasMoreTokens()) {
                sOperatorId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sOperatorId : " + sOperatorId);
                scope.setOperatorId(sOperatorId);
            }

            String sComplexId = "" ;
            if (token.hasMoreTokens()) {
                sComplexId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sComplexId : " + sComplexId);
                scope.setComplexId(sComplexId);
            }

            String sFacilityId = "" ;
            if (token.hasMoreTokens()) {
                sFacilityId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sFacilityId : " + sFacilityId);
                scope.setFacilityId(sFacilityId);
            }

            String sYardId = "" ;
            if (token.hasMoreTokens()) {
                sYardId = token.nextToken() ;
                if (DEBUG_MODE) log(FN_NAME + "sYardId : " + sYardId);
                scope.setYardId(token.nextToken());
            }
        }

        if (DEBUG_MODE) log(FN_NAME + "scope : " + scope.toString());
        if (DEBUG_MODE) log(FN_NAME + "scope.getExternalUserId() : " + scope.getExternalUserId());
        if (DEBUG_MODE) log(FN_NAME + "scope.getComplexId() : " + scope.getComplexId());
        if (DEBUG_MODE) log(FN_NAME + "scope.getOperatorId() : " + scope.getOperatorId());
        if (DEBUG_MODE) log(FN_NAME + "scope.getFacilityId() : " + scope.getFacilityId());
        if (DEBUG_MODE) log(FN_NAME + "scope.getYardId() : " + scope.getYardId());

        ARGO_SERVICE_URL = getServiceURL();
        if (DEBUG_MODE) log(FN_NAME + "ARGO_SERVICE_URL : " + ARGO_SERVICE_URL.toString());

        // Identify the Web Services host
        ArgoServiceLocator service = new ArgoServiceLocator();
        ArgoServicePort port = service.getArgoServicePort(new URL(ARGO_SERVICE_URL));
        Stub stub = (Stub) port;

        // Specify the User ID and the Password
        if (DEBUG_MODE) log(FN_NAME + "ArgoConfig.N4_WS_BILLING_USERID.getSetting(uc) : " + ArgoConfig.N4_WS_BILLING_USERID.getSetting(uc));
        if (DEBUG_MODE) log(FN_NAME + "ArgoConfig.N4_WS_BILLING_PASSWORD.getSetting(uc) : " + ArgoConfig.N4_WS_BILLING_PASSWORD.getSetting(uc));

        stub._setProperty(Stub.USERNAME_PROPERTY, ArgoConfig.N4_WS_BILLING_USERID.getSetting(uc));
        stub._setProperty(Stub.PASSWORD_PROPERTY, ArgoConfig.N4_WS_BILLING_PASSWORD.getSetting(uc));

        response = port.genericInvoke(scope, inQueryXML);
        return response;
    }

    private File getFile(String inDownloadUrl, String inPath, String inFileNameWithExtension) {
        String FN_NAME = ".getFile " ;
        if (DEBUG_MODE) log(FN_NAME + "inDownloadUrl : " + inDownloadUrl);
        if (DEBUG_MODE) log(FN_NAME + "inPath : " + inPath);
        if (DEBUG_MODE) log(FN_NAME + "inFileNameWithExtension : " + inFileNameWithExtension);

        Document responseDoc = XmlUtil.parse(inDownloadUrl);
        Element rootElement = responseDoc.getRootElement();
        String targetUrlStr = rootElement.text;
        if (targetUrlStr != null) {
            URL url = new URL(targetUrlStr);
            URLConnection connection = url.openConnection();
            connection.connect();
            InputStream urlInputStream = connection.getInputStream();

            String classPath = "" ;
            if (DEBUG_MODE) log(FN_NAME + "classPath : " + classPath);
            if (inPath == "")
                classPath = FileUtil.getClassPath();
            else
                classPath = inPath ;

            File classPathDirectory = new File(classPath);
            File file = new File(inFileNameWithExtension, classPathDirectory);
            //file.deleteOnExit();

            BufferedInputStream buffInStream = new BufferedInputStream(urlInputStream);
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file));

            copyStream(buffInStream, outputStream);

            buffInStream.close();
            outputStream.close();
            return file;
        }
    }

    public URL getServiceURL() {
        String sURL = "" ;
        if (N4orN4Billing())
            sURL = (String) ArgoConfig.N4_WS_ARGO_URL.getSetting(ContextHelper.getThreadUserContext());
        else
            sURL = (String) ArgoConfig.N4_WS_BILLING_URL.getSetting(ContextHelper.getThreadUserContext());

        if (sURL == null || sURL.length() == 0) {
            BaseConfigurationProperties bc = (BaseConfigurationProperties) Roastery.getBean(CONFIGURATION_PROPERTIES);
            if (N4orN4Billing())
                sURL = "http://localhost" + bc.getPort() + "/argo/services/argoservice";
            else
                sURL = "http://localhost" + bc.getPort() + "/billing/services/argoservice";

        }
        //LOGGER.info("Billing URL :: " + billingURL);
        return new URL(sURL);
    }

    private void copyStream(InputStream inInputStream, OutputStream inOutputStream) throws IOException {
        byte[] buffer = new byte[256];
        while (true) {
            int bytesRead = inInputStream.read(buffer);
            if (bytesRead == -1) {
                break;
            }
            inOutputStream.write(buffer, 0, bytesRead);
        }
    }

    private void localLog(String inMsg) {
        _gApi.logInfo(inMsg);
    }
    private static String ARGO_SERVICE_URL;
        GroovyApi _gApi = new GroovyApi();

    public Date GetStartDateTimeByYearMonthDay(String sDate) {
        String FN_NAME = "(GetStartDateTimeByYearMonthDay) ";
        String sYear = sDate.subSequence(0, 4).toString();
        if (DEBUG_MODE) log(FN_NAME + "sYear : " + sYear);
        String sMonth = sDate.subSequence(5, 7).toString();
        if (DEBUG_MODE) log(FN_NAME + "sMonth : " + sMonth);
        String sDay = sDate.subSequence(8, 10).toString();
        if (DEBUG_MODE) log(FN_NAME + "sDay : " + sDay);

        return GetStartDateTimeByYearMonthDay(sYear, sMonth, sDay) ;
    }

    // format YYYY/MM/DD
    public Date GetEndDateTimeByYearMonthDay(String sDate) {
        String FN_NAME = "(GetEndDateTimeByYearMonthDay) ";
        String sYear = sDate.subSequence(0, 4).toString();
        if (DEBUG_MODE) log(FN_NAME + "sYear : " + sYear);
        String sMonth = sDate.subSequence(5, 7).toString();
        if (DEBUG_MODE) log(FN_NAME + "sMonth : " + sMonth);
        String sDay = sDate.subSequence(8, 10).toString();
        if (DEBUG_MODE) log(FN_NAME + "sDay : " + sDay);

        return GetEndDateTimeByYearMonthDay(sYear, sMonth, sDay) ;
    }

    public Date GetStartDateTimeByYearMonthDay(String sYear, String sMonth, String sDay) {
        String FN_NAME = "(GetStartDateTimeByYearMonthDay) " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "sYear : " + sYear);
        if (DEBUG_MODE) log(FN_NAME + "sMonth : " + sMonth);
        if (DEBUG_MODE) log(FN_NAME + "sDay : " + sDay);

        Date dStartDate = new Date().parse("yyyy/MM/dd", sYear + "/" + sMonth + "/" + sDay);
        if (DEBUG_MODE) log(FN_NAME + "dStartDate : " + dStartDate);

        dStartDate = dStartDate - 1;
        String sStartDate = dStartDate.format("yyyy/MM/dd");
        sYear = sStartDate.subSequence(0, 4).toString();
        if (DEBUG_MODE) log(FN_NAME + "sYear : " + sYear);
        sMonth = sStartDate.subSequence(5, 7).toString();
        if (DEBUG_MODE) log(FN_NAME + "sMonth : " + sMonth);
        sDay = sStartDate.subSequence(8, 10).toString();
        if (DEBUG_MODE) log(FN_NAME + "sDay : " + sDay);
        dStartDate = new Date().parse("yyyy/MM/dd HH:mm:ss", sYear + "/" + sMonth + "/" + sDay + " 23:59:59") ;
        if (DEBUG_MODE) log(FN_NAME + "dStartDate : " + dStartDate);

        return dStartDate ;
    }

    public Date GetEndDateTimeByYearMonthDay(String sYear, String sMonth, String sDay) {
        String FN_NAME = "(GetEndDateTimeByYearMonthDay) " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "sYear : " + sYear);
        if (DEBUG_MODE) log(FN_NAME + "sMonth : " + sMonth);

        Date dEndDate = new Date().parse("yyyy/MM/dd", sYear + "/" + sMonth + "/" + sDay);
        if (DEBUG_MODE) log(FN_NAME + "dEndDate : " + dEndDate);

        dEndDate = dEndDate + 1;
        String sEndDate = dEndDate.format("yyyy/MM/dd");
        sYear = sEndDate.subSequence(0, 4).toString();
        if (DEBUG_MODE) this.log(FN_NAME + "sYear : " + sYear);
        sMonth = sEndDate.subSequence(5, 7).toString();
        if (DEBUG_MODE) this.log(FN_NAME + "sMonth : " + sMonth);
        sDay = sEndDate.subSequence(8, 10).toString();

        dEndDate = new Date().parse("yyyy/MM/dd HH:mm:ss", sYear + "/" + sMonth + "/" + sDay + " 00:00:00");
        if (DEBUG_MODE) log(FN_NAME + "dEndDate : " + dEndDate);

        return dEndDate ;
    }


    public Date GetStartDateTimeByYearMonth(String sYear, String sMonth) {
        String FN_NAME = "(GetStartDateTimeByYearMonth) " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "sYear : " + sYear);
        if (DEBUG_MODE) log(FN_NAME + "sMonth : " + sMonth);

        String sDay = "01" ;

        return GetStartDateTimeByYearMonthDay(sYear, sMonth, sDay) ;
    }

    public Date GetEndDateTimeByYearMonth(String sYear, String sMonth) {
        String FN_NAME = "(GetEndDateTimeByYearMonth) " ;
        if (DEBUG_MODE) log(FN_NAME + "Start.");
        if (DEBUG_MODE) log(FN_NAME + "sYear : " + sYear);
        if (DEBUG_MODE) log(FN_NAME + "sMonth : " + sMonth);

        String sDay = "01" ;

        Date dStartDate = new Date().parse("yyyy/MM/dd", sYear + "/" + sMonth + "/" + sDay);
        if (DEBUG_MODE) log(FN_NAME + "dStartDate : " + dStartDate);

        Calendar c = Calendar.getInstance();
        c.setTime(dStartDate) ;
        if (DEBUG_MODE) log(FN_NAME + "c.getTime() : " + c.getTime().toString());

        int iLastDay = c.getActualMaximum(Calendar.DAY_OF_MONTH) ;
        if (DEBUG_MODE) log(FN_NAME + "iLastDay : " + iLastDay);

        return GetEndDateTimeByYearMonthDay(sYear, sMonth, iLastDay.toString());
    }

    // N4 true
    // N4 Billing false
    public boolean N4orN4Billing() {
        String FN_NAME = "(N4orN4Billing) " ;
        if (DEBUG_MODE) this.log(FN_NAME + "Start.");

        GeneralReference gfFileSavePath = GeneralReference.findUniqueEntryById("BP_CONFIGURATION", "RPT_N4TEXRPTPATH");
        boolean bReturn = (gfFileSavePath != null) ;

        if (DEBUG_MODE) log(FN_NAME + "bReturn : " + bReturn);

        return bReturn;
    }

    private final boolean DEBUG_MODE = true;
    public final String GROOVY_DATE_TIME_FORMAT = "yyyyMMddHHmm";
    public final String GROOVY_DATE_HOUR_MIN_FORMAT = "yyyy/MM/dd HHmm";
    public final String GROOVY_DATE_FORMAT = "yyyy/MM/dd";
    //public final String GROOVY_DATE_HQL_FORMAT = "dd-MMM-yy";
    public final String GROOVY_DATE_HQL_FORMAT = "dd-MMM-yy hh:mm:ssaa";

    private static String NEXT_LINE = "\n";
}