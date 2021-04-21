package SourceCode.GroovyPlugins;


import com.navis.argo.ArgoField;
import com.navis.argo.ContextHelper;
import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.Complex;
import com.navis.argo.business.model.Facility;
import com.navis.argo.business.model.Operator;
import com.navis.argo.webservice.BillingWebServiceHandlerImpl;
import com.navis.argo.webservice.types.v1_0.GenericInvokeResponseWsType;
import com.navis.argo.webservice.types.v1_0.MessageType;
import com.navis.argo.webservice.types.v1_0.QueryResultType;
import com.navis.argo.webservice.types.v1_0.ResponseType;
import com.navis.argo.webservice.types.v1_0.ScopeCoordinateIdsWsType;
import com.navis.billing.BillingConfig;
import com.navis.billing.BillingEntity;
import com.navis.billing.BillingField;
import com.navis.billing.business.atoms.ExtractEntityEnum;
import com.navis.edi.business.entity.EdiMailbox;
import com.navis.edi.business.portal.communication.EdiFileSystem;
import com.navis.framework.metafields.MetafieldIdFactory;
import com.navis.framework.metafields.entity.EntityId;
import com.navis.framework.metafields.entity.EntityIdFactory;
import com.navis.framework.persistence.HibernateApi;
import com.navis.framework.portal.FieldChanges;
import com.navis.framework.portal.QueryUtils;
import com.navis.framework.portal.UserContext;
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.JoinType;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.query.common.api.QueryResult;
import com.navis.framework.query.common.impl.processors.DomainQueryProcessor;
import com.navis.framework.util.BizViolation;
import com.navis.framework.util.StringUtil;
import com.navis.www.services.argoservice.ArgoServiceLocator;
import com.navis.www.services.argoservice.ArgoServicePort;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.log4j.Logger;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.rpc.ServiceException;
import javax.xml.rpc.Stub;
/**
 * MTL update Payment details in CME and CUE.
 *

 The XML file to be used should be properly formatted as follows:
 <Invoices>
 <Invoice>
 <N4>103</N4>
 <SAP>103</SAP>
 <ReasonCode>FP</ReasonCode>
 </Invoice>
 <Invoice>
 <N4>102</N4>
 <SAP>102</SAP>
 <ReasonCode>FP</ReasonCode>
 </Invoice>
 </Invoices>
 */
/*-----------------------------------------------------------------------------
 * Modified by:     Henry Cheng
 * Modified Date:   May30, 2018
 * Description:     CGD180068 - Move payment file to SAP Backup folder instead of delete it after FTP to N4B
 *
 */
public class MTLN4BillingUpdatePaymentFromXMLtoCUEorCME extends GroovyApi {
    HibernateApi hbrapi = HibernateApi.getInstance();

    Facility fcyD, fcyI;

    private boolean ftpGetFile(EdiMailbox mbx, String sPrefix) throws IOException {
        FTPClient ftp = new FTPClient();
        FTPClientConfig cfg = new FTPClientConfig(FTPClientConfig.SYST_UNIX);
        ftp.configure(cfg);
        ftp.connect(mbx.getEdimlbxCommAddr());
        if (ftp.login(mbx.getEdimlbxCommUserId(),mbx.getEdimlbxCommPasswd())==false) {
            registerError("FTP login error");
            return false;
        }
        ftp.cwd(mbx.getEdimlbxCommFolder());
        String sDir = mbx.getEdimlbxDirectory();

        String[] names = ftp.listNames();
        for (String sName : names) {
            if (sName.startsWith(sPrefix)==false)
                continue;

            if (ftp.retrieveFile(sName, new FileOutputStream(new File(sDir,sName))))
                ftp.rename(sName,"Backup/" + sName);
        }

        ftp.logout();
        ftp.disconnect();
        
        return true;
    }

    private List<File> dirGetFile(EdiMailbox mbx, String sPrefix) {
        String sDir = mbx.getEdimlbxDirectory();
        File dir = new File(sDir);
        File[] files = dir.listFiles();
        List<File> listFile = new ArrayList<File>();
        for (File file : files) {
            String sFileName = file.getName();
            if (sFileName.startsWith(sPrefix))
                listFile.add(file);
        }
        return listFile;
    }

    private Facility findFacilityBySuffix(Operator opr, String sSuffix) {
        for (Complex cpx : (Collection<Complex>) opr.getChildren()) {
            if (cpx.getCpxId().endsWith(sSuffix)) {
                for (Facility fcy : (Collection<Facility>) cpx.getChildren()) {
                    if (fcy.getFcyId().endsWith(sSuffix)) {
                        return fcy;
                    }
                }
            }
        }
        return null;
    }

    private void findGkeys(Facility fcy, List<String> listNo, List<Long> listInvoiceGkey, List<Long> listCmeGkey, List<Long> listCueGkey) {
        List<Object> listLong = StringUtil.convertToNumericList((String[]) listNo.toArray());

        DomainQuery dqVM = QueryUtils.createDomainQuery(BillingEntity.INVOICE);
        dqVM.setScopingEnabled(false);
        dqVM.addDqPredicate(PredicateFactory.eq(MetafieldIdFactory.getCompoundMetafieldId(BillingField.INVOICE_FACILITY, ArgoField.FCY_GKEY),fcy.getFcyGkey()));
        dqVM.addDqPredicate(PredicateFactory.in(BillingField.INVOICE_DRAFT_NBR,listLong));

        EntityId eiItem = EntityIdFactory.valueOf(BillingEntity.INVOICE_ITEM,"im");
        dqVM.addDqJoin(PredicateFactory.createJoin(JoinType.INNER_JOIN,BillingField.INVOICE_INVOICE_ITEMS,eiItem.getAlias()));
        dqVM.addDqField(MetafieldIdFactory.getEntityAwareMetafieldId(eiItem,BillingField.ITEM_SERVICE_EXTRACT_TYPE));
        dqVM.addDqField(MetafieldIdFactory.getEntityAwareMetafieldId(eiItem,BillingField.ITEM_SERVICE_EXTRACT_GKEY));

        DomainQueryProcessor dqp = new DomainQueryProcessor();
        QueryResult result = dqp.processQuery(dqVM);
        int nCount = result.getTotalResultCount();
        for (int nRow = 0; nRow < nCount; nRow++) {
            ExtractEntityEnum eee = (ExtractEntityEnum) result.getValue(nRow,BillingField.ITEM_SERVICE_EXTRACT_TYPE);
            Long nInvGkey = (Long) result.getValue(nRow,BillingField.INVOICE_GKEY);
            Long nExtGkey = (Long) result.getValue(nRow,BillingField.ITEM_SERVICE_EXTRACT_GKEY);
            listInvoiceGkey.add(nInvGkey);
            if (eee.equals(ExtractEntityEnum.MARINE))
                listCmeGkey.add(nExtGkey);
            else if (eee.equals(ExtractEntityEnum.INV))
                listCueGkey.add(nExtGkey);
        }
    }

    private ArgoServicePort getWsStub() throws ServiceException {
        ArgoServiceLocator locator = new ArgoServiceLocator();
        ArgoServicePort port = locator.getArgoServicePort(BillingWebServiceHandlerImpl.getArgoServiceURL());

        Stub stub = (Stub) port;
        stub._setProperty(Stub.USERNAME_PROPERTY, BillingConfig.N4_WS_USERID.getSetting(ContextHelper.getThreadUserContext()));
        stub._setProperty(Stub.PASSWORD_PROPERTY, BillingConfig.N4_WS_PASSWORD.getSetting(ContextHelper.getThreadUserContext()));
        return port;
    }

    private void updateInvoicePaid(List<Long> listInvoiceGkey) {
        FieldChanges fcInv = new FieldChanges();
        fcInv.setFieldChange(BillingField.INVOICE_FLEX_LONG02,1L);

        int nStart = 0;
        int nSize = listInvoiceGkey.size();
        while (nStart < nSize) {
            int nEnd = nStart + 500;
            if (nEnd > nSize)
                nEnd = nSize;
            hbrapi.batchUpdate(BillingEntity.INVOICE, (Long[]) listInvoiceGkey.subList(nStart,nEnd).toArray(), fcInv);
            nStart = nEnd;
        }
    }

    private ScopeCoordinateIdsWsType getScopeCoordinatesForWs(Facility fcy) throws BizViolation {
        Complex cpx = fcy.getFcyComplex();
        Operator opr = cpx.getCpxOperator();
        //build the scope coordinates for the web service based on the user context;
        ScopeCoordinateIdsWsType scopeCoordinates = new ScopeCoordinateIdsWsType();
        UserContext uContext = ContextHelper.getThreadUserContext();
        scopeCoordinates.setOperatorId(opr.getId());
        scopeCoordinates.setComplexId(cpx.getCpxId());
        scopeCoordinates.setFacilityId(fcy.getFcyId());
        scopeCoordinates.setYardId(fcy.getActiveYard().getYrdId());
        return scopeCoordinates;
    }

    private void callWebServiceCallToN4ForPaymentUpdate(ArgoServicePort port, Facility fcy, String idGkeys, List listGkeys) throws RemoteException, BizViolation {
        ScopeCoordinateIdsWsType scope = getScopeCoordinatesForWs(fcy);

        int nStart = 0;
        int nSize = listGkeys.size();
        while (nStart < nSize) {
            int nEnd = nStart + 500;
            if (nEnd > nSize)
                nEnd = nSize;
            String requestString = String.format(XML_CONTENT, N4_EXTENSION, idGkeys, listGkeys.subList(nStart,nEnd).join(","));
            nStart = nEnd;

            LOGGER.debug(requestString);
            GenericInvokeResponseWsType invokeResponseWsType = port.genericInvoke(scope, requestString);
            ResponseType response = invokeResponseWsType.getCommonResponse();

            if (response == null) {
                registerError("web service request has null response");
                return;
            }

            QueryResultType[] queryResultTypes = response.getQueryResults();
            if ((queryResultTypes == null) || (queryResultTypes.length != 1)) {
                registerError("web service for update payment details error");
                for (MessageType msgtype : response.getMessageCollector().getMessages()) {
                    if (msgtype != null)
                        registerError(msgtype.getMessage());
                }
                return;
            }

            String responseString = queryResultTypes[0].getResult();
            LOGGER.debug(responseString);
        }
    }

    private boolean processFile(File file) throws JDOMException, IOException, BizViolation {
        LOGGER.debug(file.getName());
        
        if (file.length() == 0)
            return true;

        SAXBuilder builder = new SAXBuilder();
        Document doc = builder.build(file.getAbsolutePath());
        Element root = doc.getRootElement();

        List<String> listDraftNbrD = new ArrayList();
        List<String> listDraftNbrI = new ArrayList();
        List<Element> listInv = root.getChildren("Invoice");
        for (Element xInv : listInv) {
            String sN4 = xInv.getChild("N4").getValue();
            if ((sN4 != null) && (sN4.isEmpty()==false)) {
                String sDraftNbr = sN4.substring(0, sN4.length() -1);
                String sSuffix = sN4.substring(sN4.length() -1);
                if (sSuffix.equals("D"))
                    listDraftNbrD.add(sDraftNbr);
                else if (sSuffix.equals("I"))
                    listDraftNbrI.add(sDraftNbr);
            }
        }

        if (listDraftNbrD.isEmpty() &&
            listDraftNbrI.isEmpty()   )
            return true;

        ArgoServicePort port = getWsStub();

        for (int nIdx = 0; nIdx < 2; nIdx++) {
            List<String> listDraftNbr = (nIdx == 0) ? listDraftNbrD  : listDraftNbrI;
            if (listDraftNbr.isEmpty())
                continue;

            List<Long> listInvoiceGkey = new ArrayList();
            List<Long> listCmeGkey = new ArrayList();
            List<Long> listCueGkey = new ArrayList();

            findGkeys( nIdx == 0 ? fcyD : fcyI, listDraftNbr
                     , listInvoiceGkey, listCmeGkey, listCueGkey);

            if (listInvoiceGkey.isEmpty()==false) {
                LOGGER.debug(listInvoiceGkey.toString());
                updateInvoicePaid(listInvoiceGkey);
            }

            if (listCmeGkey.isEmpty()==false) {
                LOGGER.debug(listCmeGkey.toString());
                callWebServiceCallToN4ForPaymentUpdate(port, fcyD, CME_GKEYS, listCmeGkey);
            }

            if (listCueGkey.isEmpty()==false) {
                LOGGER.debug(listCueGkey.toString());
                callWebServiceCallToN4ForPaymentUpdate(port, fcyI, CUE_GKEYS, listCueGkey);
            }
        }

        return true;
    }

    public void execute(Map map) throws JDOMException, IOException, BizViolation {
        EdiMailbox mbx = EdiMailbox.find(MAILBOX_NAME);
        if (mbx == null) {
            registerError("Mailbox " + MAILBOX_NAME + " not found");
            return;
        }

        File fDir = new File(mbx.getEdimlbxDirectory());
        if (fDir.exists()==false) {
            fDir.mkdirs();
        }

        if (ftpGetFile(mbx, FILE_PREFIX)==false)
            return;

        List<File> files = dirGetFile(mbx, FILE_PREFIX);

        Operator opr = getFacility().getFcyComplex().getCpxOperator();        
        fcyD = findFacilityBySuffix(opr,"D");
        fcyI = findFacilityBySuffix(opr,"I");

        for (File file : files) {
            if (processFile(file))
                EdiFileSystem.archive(file.getName(),mbx);
        }
    }

    public void execute() throws JDOMException, IOException, BizViolation {
        execute(new HashMap());
    }

    private final Logger LOGGER = Logger.getLogger(getClass());

    private String XML_CONTENT = "<groovy class-name=\"%s\" class-location=\"code-extension\">\n" +
                                     "<parameters>\n" +
                                         "<parameter id=\"%s\" value=\"%s\"/>\n" +
                                     "</parameters>\n" +
                                 "</groovy>";

    private final String MAILBOX_NAME = "SAP_IB_PAYMENT";
    private final String FILE_PREFIX = "PAYMENT_ON_";

    private final String N4_EXTENSION = "MTLN4UpdatePaymentDetailsInCUEOrCME";
    private final String CME_GKEYS = "CME_GKEYS";
    private final String CUE_GKEYS = "CUE_GKEYS";
}