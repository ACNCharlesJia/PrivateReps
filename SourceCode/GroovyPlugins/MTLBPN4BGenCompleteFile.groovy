package SourceCode.GroovyPlugins;

import com.navis.argo.ContextHelper;
import com.navis.argo.ArgoEntity;
import com.navis.argo.ArgoField;
import com.navis.argo.business.api.ArgoUtils;
import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.Complex;
import com.navis.argo.business.model.GeneralReference;
import com.navis.argo.util.FileUtil;
import com.navis.argo.util.XmlUtil;
import com.navis.billing.BillingEntity;
import com.navis.billing.BillingField;
import com.navis.billing.business.atoms.InvoiceBatchStatusEnum;
import com.navis.billing.business.atoms.InvoiceStatusEnum;
import com.navis.edi.business.atoms.EdiCommunicationTypeEnum;
import com.navis.edi.business.entity.EdiMailbox;
import com.navis.edi.business.portal.communication.EdiFileSystem;
import com.navis.edi.business.portal.communication.FtpAdaptor;
import com.navis.framework.persistence.HibernateApi;
import com.navis.framework.portal.QueryUtils;
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.query.common.api.QueryResult;

import java.io.File;
import java.io.FileInputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import org.jdom.Element;
/**
 * Author:          Henry Cheng
 * Created Date:    Jan 4, 2018
 * Description:     CGD170150 - Completion File FTP error
 *
 **/
public class MTLBPN4BGenCompleteFile extends GroovyApi {
    private File[] files;
    private String invoiceGkeys;
    final private HibernateApi hbrapi = HibernateApi.getInstance();

    public void execute() {
        execute(new HashMap());
    }

    public void execute(Map map) {
        String FN_NAME = " MTLN4BGenCompleteFile  (execute) " ;
        logInfo(FN_NAME + "Start.");
        Integer executeDate = Integer.parseInt(getReferenceValue("BILLINGMAIN_DATE", "EXECUTE_DATE",null,null,1));
        if (executeDate == null)
            executeDate = -1;

        GregorianCalendar cal = new GregorianCalendar();
        SimpleDateFormat sdfDT = new SimpleDateFormat("yyyyMMddHHmmss");
        String fileName = "N4BINVGEN_" + sdfDT.format(cal.getTime()) + ".cmp";

        cal.add(Calendar.DATE,executeDate);
        Date dtBilling = cal.getTime();

        cal.add(Calendar.DATE,1);
        Date dayStart = ArgoUtils.getLocalTimeStartOfDay(cal.getTime());
        Date dayEnd = ArgoUtils.getLocalTimeEndOfDay(dayStart);

        GeneralReference grInvGenCmp = GeneralReference.findUniqueEntryById("BP_CONFIGURATION","N4B_INVGENCMPPATH",null,null);
        if (grInvGenCmp == null) {
            registerError("GR (BP_CONFIGURATION,N4B_INVGENCMPPATH) not defined");
            return;
        }

        String sCmpPath = grInvGenCmp.getRefValue1();
        if (sCmpPath == null) {
            registerError("CMP PATH not defined");
            return;
        }

        String sValue2 = grInvGenCmp.getRefValue2();
        if ((sValue2 != null) && sValue2.substring(1,18).equals(fileName.substring(1, 18))) {
            return;
        }

        EdiMailbox mbxFTP = EdiMailbox.find(sCmpPath);
        if (mbxFTP == null) {
            registerError("EdiMailbox " + sCmpPath + " not found");
            return;
        }

        try {
            if (checkInvoiceIsDone(dayStart, dayEnd)) {
                logInfo("invoiceGkeys ::" + invoiceGkeys);
                //----Start Version: 1.2-----------
                int ediCount = 0;
                if (invoiceGkeys != null && invoiceGkeys.length() > 0) {
                    ediCount = findInvoiceByGkeys(dayStart,dayEnd);
                }

                String filePath = mbxFTP.getEdimlbxDirectory();
                logInfo("File Name ::"+ fileName);
                File fileCmp = new File(filePath,fileName);
                SimpleDateFormat sdfD = new SimpleDateFormat("yyyyMMdd");
                buildXml(fileCmp, sdfD.format(dtBilling), ediCount);

                String sDirBackup = getReferenceValue("BP_CONFIGURATION","N4B_INVGENCMPBACKUP",null,null,1);

                if(fileCmp.exists()) {
                    if (mbxFTP.getEdimlbxCommType().equals(EdiCommunicationTypeEnum.FTP)) {
                        FtpAdaptor fa = new FtpAdaptor();
                        fa.openConnection(mbxFTP);
                        fa._ftpClient.enterLocalActiveMode()
                        fa.sendDocument(fileCmp);
                        fa.closeConnection();
                        grInvGenCmp.setRefValue2(fileName);
                    }
                }

                if (sDirBackup != null && fileCmp.exists()) {
                    FileUtil.writeToFile(new FileInputStream(fileCmp), new File(sDirBackup,fileName));
                }

                if (fileCmp.exists()) EdiFileSystem.archive(fileName, mbxFTP);
            }

            logInfo(FN_NAME + "End.");
        } catch(Exception e) {
            registerError("Exception: " + e.getMessage());
        } finally {
            files = null;
            invoiceGkeys = null;
        }
    }

    private boolean checkInvoiceIsDone(Date dayStart, Date dayEnd) {
        try {
            String ignoreN4TCompFile = GeneralReference.findUniqueEntryById("BILLINGMAIN_DATE", "EXECUTE_DATE").getRefValue2();
            if (ignoreN4TCompFile.equals("IGNORE"))
                return true;

            if (isBatchInvoiceInAllComplex(dayStart,dayEnd)==false) {
                registerError("checkInvoiceIsDone : Batch Invoices not yet in All Complexes");
                return false;
            }

            String[] status = new String[3]; // Version 1.6
            status[0] = InvoiceBatchStatusEnum.COMPLETE.getKey();
            status[1] = InvoiceBatchStatusEnum.ERRORS.getKey();
            status[2] = InvoiceBatchStatusEnum.QUEUED.getKey(); // Version 1.6
            int nCountAll = findBatchInvoiceByStatusNCreated(status, dayStart, dayEnd);
            logWarn("nCountAll: " + nCountAll);
            if (nCountAll > 0) {
                registerError("checkInvoiceIsDone : Batch Invoices without Invoices");
                return false;
            }

            invoiceGkeys = findBatchInvoiceGkeyByCompNCreated(dayStart, dayEnd);
            logInfo("gkeys: " + invoiceGkeys);
            int nInterfaceIdIsNull = 0;
            if (invoiceGkeys ==  null || invoiceGkeys.length() <= 0 ) {
                registerError("checkInvoiceIsDone - invoices not found");
            } else {
                nInterfaceIdIsNull = findInvoiceByGkeysAndInterfaceIdIsNull(dayStart, dayEnd);
            }

            logInfo("nInterfaceIdIsNull: $nInterfaceIdIsNull");

            if (nInterfaceIdIsNull > 0) {
                registerError("checkInvoiceIsDone : invoices with InterfaceId = null");
                return false;
            }
            return true;
        } catch (Exception any) {
            registerError("checkInvoiceIsDone Exception ::"+ any.getMessage());
        }
        return false;
    }

    private boolean isBatchInvoiceInAllComplex(Date startDate, Date endDate) {
        DomainQuery dqGR = QueryUtils.createDomainQuery(ArgoEntity.GENERAL_REFERENCE);
        dqGR.setScopingEnabled(false);
        dqGR.addDqPredicate(PredicateFactory.eq(ArgoField.REF_TYPE,"BILLINGMAIN_DATE"));
        dqGR.addDqPredicate(PredicateFactory.eq(ArgoField.REF_ID1 ,"EXECUTE_DATE"    ));
        List<GeneralReference> listGR = (List<GeneralReference>) hbrapi.findEntitiesByDomainQuery(dqGR);

        boolean bExist = true;
        for (GeneralReference gr :  listGR) {
            Complex cpx = gr.getRefComplex();
            DomainQuery dqByCpx = QueryUtils.createDomainQuery(BillingEntity.BATCH_INVOICE);
            dqByCpx.setScopingEnabled(false);
            dqByCpx.addDqPredicate(PredicateFactory.between(BillingField.BATCH_CREATED, startDate, endDate));
            dqByCpx.addDqPredicate(PredicateFactory.eq(BillingField.BATCH_COMPLEX,cpx.getCpxGkey()));
            if (hbrapi.existsByDomainQuery(dqByCpx)==false) {
                registerError("waiting for Batch Invoice in " + cpx.getCpxId());
                bExist = false;
            }
        }

        return bExist;
    }

    public int findBatchInvoiceByStatusNCreated(String[] status, Date startDate, Date endDate) {
        int nCount = 0;
        try {
            DomainQuery dqCreated = QueryUtils.createDomainQuery(BillingEntity.BATCH_INVOICE);
            dqCreated.setScopingEnabled(false);
            dqCreated.addDqPredicate(PredicateFactory.in(BillingField.BATCH_STATUS, status));
            dqCreated.addDqPredicate(PredicateFactory.between(BillingField.BATCH_CREATED, startDate, endDate));
            dqCreated.addDqPredicate(PredicateFactory.isNull(BillingField.BATCH_INVOICE_GKEY));

            nCount = hbrapi.findCountByDomainQuery(dqCreated);
            logInfo("query findBatchInvoiceByStatusNCreated:" + nCount);
        } catch(Exception any) {
            registerError("query findBatchInvoiceByStatusNCreated exception: " + any.getMessage());
        }
        return nCount;
    }

    public String findBatchInvoiceGkeyByCompNCreated(Date startDate, Date endDate) {
        String keys = null;
        try {
            DomainQuery dqComp = QueryUtils.createDomainQuery(BillingEntity.BATCH_INVOICE);
            dqComp.setScopingEnabled(false);
            dqComp.addDqField(BillingField.BATCH_INVOICE_GKEY);
            dqComp.addDqPredicate(PredicateFactory.eq(BillingField.BATCH_STATUS, InvoiceBatchStatusEnum.COMPLETE));
            dqComp.addDqPredicate(PredicateFactory.between(BillingField.BATCH_CREATED, startDate, endDate));
            dqComp.addDqPredicate(PredicateFactory.isNotNull(BillingField.BATCH_INVOICE_GKEY));

            QueryResult result = hbrapi.findValuesByDomainQuery(dqComp);
            int nTotal = result.getTotalResultCount();
            if (nTotal > 0) {
                for (int nIdx = 0; nIdx < nTotal; nIdx++) {
                    keys = (keys == null ? "" : keys + ",") + result.getValue(nIdx, BillingField.BATCH_INVOICE_GKEY);
                }
            }

            logInfo("query findBatchInvoiceGkeyByCompNCreated:" + keys);
        } catch(Exception any) {
            registerError("query findBatchInvoiceGkeyByCompNCreated exception: " + any.getMessage());
        }
        return keys;
    }

    public int findInvoiceByGkeysAndInterfaceIdIsNull(Date startDate, Date endDate) {
        return findInvoiceByGkeys(startDate, endDate, true);
    }

    public int findInvoiceByGkeys(Date startDate, Date endDate) {
        return findInvoiceByGkeys(startDate, endDate, false);
    }

    public int findInvoiceByGkeys(Date startDate, Date endDate, boolean bInterfaceIdIsNull) {
        int nCount = 0;
        try {
            DomainQuery dqBatInv = QueryUtils.createDomainQuery(BillingEntity.BATCH_INVOICE);
            dqBatInv.setScopingEnabled(false);
            dqBatInv.addDqField(BillingField.BATCH_INVOICE_GKEY);
            dqBatInv.addDqPredicate(PredicateFactory.eq(BillingField.BATCH_STATUS, InvoiceBatchStatusEnum.COMPLETE));
            dqBatInv.addDqPredicate(PredicateFactory.isNotNull(BillingField.BATCH_INVOICE_GKEY));
            dqBatInv.addDqPredicate(PredicateFactory.between(BillingField.BATCH_CREATED, startDate, endDate));

            DomainQuery dqInvoice = QueryUtils.createDomainQuery(BillingEntity.INVOICE);
            dqInvoice.setScopingEnabled(false);
            dqInvoice.addDqPredicate(PredicateFactory.subQueryIn(dqBatInv, BillingField.INVOICE_GKEY));
            dqInvoice.addDqPredicate(PredicateFactory.eq(BillingField.INVOICE_STATUS, InvoiceStatusEnum.FINAL));
            if (bInterfaceIdIsNull) {
                dqInvoice.addDqPredicate(PredicateFactory.isNull(BillingField.INVOICE_INTERFACE_ID));
            }

            nCount = hbrapi.findCountByDomainQuery(dqInvoice);
        } catch(Exception any) {
            registerError("query findInvoiceByGkeys exception: " + any.getMessage());
        }
        return nCount;
    }

    private String buildXml(File fileCmp, String billingDate, int nInvoiceEDI) {
        Element xRoot = new Element("N4TInvoiceGenerationComplete");
        xRoot.addNamespaceDeclaration(XmlUtil.XSI_NAMESPACE);
        Element xCount = new Element("Count");
        xRoot.addContent(xCount);
        xCount.setAttribute("BillingDate", billingDate);
        xCount.setAttribute("InvoiceEDI", ""+ nInvoiceEDI);
        String sXml = XmlUtil.XML_HEADER + XmlUtil.convertToString(xRoot, false);
        FileUtil.writeToFile(sXml,fileCmp);
        logWarn("Generate Complete File SUCCESS!");
        return sXml;
    }
}