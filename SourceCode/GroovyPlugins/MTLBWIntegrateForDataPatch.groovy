package SourceCode.GroovyPlugins

import com.navis.framework.persistence.HibernateApi;
import com.navis.argo.ContextHelper;
import com.navis.argo.business.api.ArgoUtils;
import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.GeneralReference;
import com.navis.extension.model.persistence.DynamicHibernatingEntity;
import com.navis.framework.business.Roastery;
import com.navis.framework.metafields.MetafieldIdFactory;
import com.navis.framework.portal.UserContext;
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.portal.query.QueryFactory;

import org.hibernate.Query;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

class MTLBWIntegrateForDataPatch extends GroovyApi {
    private final String FN_NAME = "MTLBWIntegrateForDataPatch" ;
    private final UserContext context = ContextHelper.getThreadUserContext();
    private final Date timeNow = ArgoUtils.convertDateToLocalDateTime(ArgoUtils.timeNow(), context.getTimeZone());
    private final String EntityName="com.navis.bw.Custom.DataExchange";

    public void execute(Map map) {
	log("MTLBWIntegrateForDataPatch.Strart");

	try {
	    while (true) {
		Map allDate = getRefDate();
		if (allDate == null || allDate.size() < 3) {
                    break;
		}

		if (!isValidDate(allDate)) {
		    break;
		}

		log("MTLBWIntegrateForDataPatch.dateMap:" + allDate);
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

		RunAction("'" + format.format((Date) allDate.get("xd")) + "'");

		setRefDate(allDate);
            }
        } catch (Exception any) {
	    log("MTLBWIntegrateForDataPatch.Exception:" + any.getMessage());
	}

	log("MTLBWIntegrateForDataPatch.end");
    }

    private void InsertRawData(String ENTITY_NAME, Double code, String cusid, String draft, String create, String cname, String etgkey, Double rategk, Double rateat, String crency, String ratebl, Double quanty, Double flxdb1, Double flxdb2 ,String bildte) {
	DynamicHibernatingEntity dhe = new DynamicHibernatingEntity(ENTITY_NAME);
        dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdataccde"), code);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdataccid"), cusid);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatadnum"), draft);
	if ((create != null) && (create.isEmpty()==false))
	    dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatacrtdte"), Timestamp.valueOf(create));
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdataoptcde"), cname);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatatarcde"), etgkey);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatatarrate"), rategk);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatabilitmamt"), rateat);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatatarcur"), crency);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatasvccde"), ratebl);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdataqty"), quanty);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatarteu"), flxdb2);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdataateu"), flxdb1);
	if (bildte != null && (!bildte.isEmpty())) {
       	    bildte = bildte.replaceAll("[a-zA-Z]+", "");
	    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
	    try {
		dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwdatabildte"), dateFormat.parse(bildte));
	    } catch (Exception any) {}
	}
	HibernateApi.getInstance().save(dhe);
	HibernateApi.getInstance().flush();
    }

    public List<Serializable> GetValuesByRunHQL(String sHQL) {
	String FN_NAME = "(GetValuesByRunHQL) " ;

        List<Serializable> lResult = [];
	HibernateApi hbrapi = Roastery.getHibernateApi();
	Query qry = hbrapi.createQuery(sHQL) ;
	Iterator it = qry.iterate();
	while (it.hasNext()) {
	    Object[] col = (Object[]) it.next();
	    //log(FN_NAME + "col :" + col.toString());
	    if (col != null)
		lResult.add(col);
	}

	log(FN_NAME + "lResult.size() : " + lResult.size());
	log(FN_NAME + "End.");
	return lResult ;
    }

    public List<GeneralReference> getRefDate(String type) {
	DomainQuery dq = QueryFactory.createDomainQuery("GeneralReference")
			.addDqPredicate(PredicateFactory.eq(MetafieldIdFactory.valueOf("refType"), type));
	return Roastery.getHibernateApi().findEntitiesByDomainQuery(dq);
    }

    public void setRefDate(Map allDate) {
	Date exeDate = (Date)allDate.get("xd");
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	GregorianCalendar cal = new GregorianCalendar();
	cal.setTime(exeDate);
	cal.add(5,1);
	Date newExeDate =cal.getTime();

	GeneralReference fg = GeneralReference.findUniqueEntryById("MTL_BW_DATA_PATCH","EXECUTE_DATE");
	fg.setRefValue3(format.format(newExeDate));
      /*
	HibernateApi.getInstance().update(fg);
	HibernateApi.getInstance().flush()
      */
    }

    public Map getRefDate() {
	HashMap<String,Date> allDate = new HashMap<String,Date>();
	GeneralReference fg = GeneralReference.findUniqueEntryById("MTL_BW_DATA_PATCH","EXECUTE_DATE");

	if (fg != null) {
	    String startDateStr = fg.getRefValue1();
	    String endDateStr= fg.getRefValue2();
	    String exeDateStr= fg.getRefValue3();

	    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
	    try {
		if (startDateStr != null && !startDateStr.isEmpty() && endDateStr != null && !endDateStr.isEmpty() && exeDateStr != null && !exeDateStr.isEmpty()) {
   		    allDate.put("sd",format.parse(startDateStr));
		    allDate.put("ed",format.parse(endDateStr));
		    allDate.put("xd",format.parse(exeDateStr));
		}
            } catch(Exception any) {
		log("getRefDate.Exception:" + any.getMessage());
	    }
	}

	return allDate;
    }

    public boolean isValidDate(Map allDate) {
	Date startDate = (Date)allDate.get("sd");
	Date endDate = (Date)allDate.get("ed");
	Date exeDate = (Date)allDate.get("xd");

	return (exeDate.compareTo(startDate) >= 0) && (exeDate.compareTo(  endDate) <= 0);
    }

    public void DeleteRecordForDate() {
	try {
            String createStart=null,createEnd=null;

	    Date xd = (Date)getRefDate().get("xd");
            createStart = format.format(xd);
	    if (createStart == null) {
		createStart = format.format(new Date()) + " 00:00:00";
		createEnd = format.format(new Date()) + " 23:59:59";
            } else {
		String tmpDate = createStart.replace("'","");
		createStart = tmpDate + " 00:00:00";
		createEnd = tmpDate  + " 23:59:59";
	    }
	    log("createStart:" + createStart + ",createEnd:"+createEnd);
	    DomainQuery dq = QueryFactory.createDomainQuery(EntityName)
		.addDqPredicate(PredicateFactory.between(MetafieldIdFactory.valueOf("customEntityFields.custombwdatacrtdte"),Timestamp.valueOf(createStart),Timestamp.valueOf(createEnd)));
            Roastery.getHibernateApi().deleteByDomainQuery(dq);
	    HibernateApi.getInstance().flush();
        } catch (Exception err) {
	}
    }

    private void RunAction(String executeDate) {
	String refDate = executeDate;
	if (refDate == null)
	    refDate="TO_CHAR(CURRENT_DATE,'YYYY-MM-DD')";

	String sHQL = "SELECT 2100.0,cus.custId,inv.invoiceDraftNbr,inv.invoiceCreated,cus2.custContacttName,tar.tariffId,itm.itemRateBilled," +
		"itm.itemAmount,'HKD',itm.itemRateBilled,itm.itemQuantity,tar.tariffFlexDouble01,tar.tariffFlexDouble02, " +
		"(select prdctValue from SavedPredicate where prdctParentPredicate =(select batchFilter from BatchInvoice where batchInvoiceGkey=inv.invoiceGkey) and prdctParameterLabel='StartTime') "+
		"FROM Invoice as inv, Customer as cus, Customer as cus2, InvoiceItem as itm, TariffRate as rte, Tariff as tar " +
		"WHERE cus.custGkey=inv.invoiceContractCustomer AND cus2.custGkey=inv.invoicePayeeCustomer AND itm.itemInvoice=inv.invoiceGkey " +
		"AND itm.itemTariffRate=rte.rateGkey AND tar.tariffGkey=rte.rateTariff AND TO_CHAR(inv.invoiceCreated,'YYYY-MM-DD')=" + refDate;

	DeleteRecordForDate();

	List<Serializable> lHQLResult = GetValuesByRunHQL(sHQL);

	Double code, rateat, rategk, flxdb1, flxdb2 , quanty;
	String  cusid, draft,create, bildte, cname, etgkey,  crency, ratebl ;
	for (Serializable aRecord : lHQLResult) {
	    Object[] col = (Object[]) aRecord;
	    code = (Double) col[0];
	    cusid = (String) col[1];
            draft = (String) col[2];
	    create = (String) col[3];
	    cname = (String) col[4];
	    etgkey = (String) col[5];
	    rategk = (Double) col[6];
	    rateat = (Double) col[7];
	    crency = (String) col[8];

	    ratebl = "I";

            quanty = (Double) col[10];
	    flxdb1 = (Double) col[11];
	    flxdb2 = (Double) col[12];
	    bildte = (String) col[13];

	    InsertRawData(EntityName, code, cusid, draft, create, cname, etgkey, rategk, rateat, crency, ratebl, quanty, flxdb1, flxdb2, bildte);
	}

	sHQL = 	"SELECT 2100.0,cus.custId,crd.creditDraftNbr,crd.creditCreated,cus.custContacttName,tar.tariffId,-itm.crditmRate, -itm.crditmAmount," +
		"'HKD',invitm.itemRateBilled,invitm.itemQuantity,tar.tariffFlexDouble01,tar.tariffFlexDouble02, TO_CHAR(crd.creditCreated,'yyyyMMddhhmmss') " +
		"FROM Credit as crd, Customer as cus, CreditItem as itm, TariffRate as rte, InvoiceItem as invitm, Tariff as tar " +
		"WHERE cus.custGkey=crd.creditCustomer AND itm.crditmCredit=crd.creditGkey AND itm.crditmInvoiceItem=invitm.itemGkey " +
		"AND invitm.itemTariffRate=rte.rateGkey AND tar.tariffGkey=rte.rateTariff AND TO_CHAR(crd.creditCreated,'YYYY-MM-DD')=" + refDate;
	lHQLResult = GetValuesByRunHQL(sHQL );
	for (Serializable aRecord : lHQLResult) {
	    Object[] col = (Object[]) aRecord;
	    code = (Double) col[0];
	    cusid = (String) col[1];
	    draft = (String) col[2];
	    create = (String) col[3];
	    cname = (String) col[4];
	    etgkey = (String) col[5];
	    rategk = (Double) col[6];
	    rateat = (Double) col[7];
	    crency = (String) col[8];

	    ratebl = "C";

            quanty = (Double) col[10];
	    flxdb1 = (Double) col[11];
	    flxdb2 = (Double) col[12];
	    bildte = (String) col[13];

	    InsertRawData(EntityName, code, cusid, draft, create, cname, etgkey, rategk, rateat, crency, ratebl, quanty, flxdb1, flxdb2, bildte);
	}
    }

    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
}