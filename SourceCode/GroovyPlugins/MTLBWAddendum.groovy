package SourceCode.GroovyPlugins

import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.GeneralReference;
import com.navis.extension.model.persistence.DynamicHibernatingEntity;
import com.navis.framework.business.Roastery;
import com.navis.framework.metafields.MetafieldIdFactory;
import com.navis.framework.persistence.HibernateApi;
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.portal.query.QueryFactory;
import org.hibernate.Query;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
 
class MTLBWAddendum extends GroovyApi {
    String fn_name = "MTLBWAddendum";
	 
    public void execute(Map map) {
	String refDateStartPeriod = getRefDate("start");
	String refDateEndPeriod   = getRefDate("end");
		 
	if (refDateStartPeriod == null||refDateEndPeriod == null) {
	    refDateStartPeriod = "TO_CHAR(SYSDATE,'yyyy-MM-dd')||' 000000'";
	    refDateEndPeriod = "TO_CHAR(SYSDATE,'yyyy-MM-dd')||' 235959'";
	}

        DeleteRecordForDate();
		
        String sHQL = "select 2100.0, 'I', inv.invoiceCreated, inv.invoiceDraftNbr, "+
        "tar.tariffId, sum(itm.itemQuantityBilled), "+
        "itm.itemDescription, itm.itemGlCode, "+
        "rte.rateFlexString03  "+
        "from   Invoice as inv, InvoiceItem as itm, "+
        "TariffRate as rte, Tariff as tar "+
        "where inv.invoiceGkey = itm.itemInvoice "+
        "and   itm.itemTariffRate = rte.rateGkey "+
        "and   rte.rateTariff = tar.tariffGkey "+
        "and   inv.invoiceStatus = 'FINAL' "+
        "and   rte.rateContract.contractType like 'AD%' "+
        "and   tar.tariffId not in ('OT_FSP_FREE_DAY_ALLOW') "+
        "group by inv.invoiceDraftNbr, tar.tariffId,  itm.itemDescription, "+
        "rte.rateFlexString03, itm.itemGlCode, inv.invoiceCreated "  +
        "having to_char(inv.invoiceCreated,'yyyy-MM-dd HH24MISS') between "+ refDateStartPeriod +" and "+ refDateEndPeriod ;

	Double ccde, qty;
	String created, acrType, dnum, tarId, sp_ind, tariff_desc, glCode;

	List<Serializable> lHQLResult = GetValuesByRunHQL(sHQL);
	for (Serializable records : lHQLResult) {
	    Object[] col = (Object[]) records;
	    ccde = (Double) col[0];
	    acrType = (String) col[1];
	    created = (String) col[2];
	    dnum = (String) col[3];
	    tarId = (String) col[4];
	    qty = (Double) col[5];
	    tariff_desc = (String) col[6];
	    glCode = (String) col[7];
            sp_ind = (String) col[8];

	    InsertRawData(entityName, ccde, acrType, created, dnum, tarId, qty, tariff_desc, glCode, sp_ind);
	}
		
        sHQL =  "select 2100.0, 'C', crd.creditCreated, crd.creditDraftNbr, "+
        "tar.tariffId, sum(invitm.itemQuantityBilled), "+
        "invitm.itemDescription, invitm.itemGlCode, rte.rateFlexString03 "+
        "from Credit as crd, CreditItem as itm, "+
        "TariffRate as rte, InvoiceItem as invitm, Tariff as tar "+
        "where crd.creditGkey = itm.crditmCredit "+ 
         "and   itm.crditmInvoiceItem = invitm.itemGkey "+
         "and   invitm.itemTariffRate=rte.rateGkey "+ 
         "and   rte.rateTariff = tar.tariffGkey "+ 
         "and   crd.creditStatus = 'FINAL' "+ 
         "and   rte.rateContract.contractType like 'AD%' "+ 
         "and   tar.tariffId not in ('OT_FSP_FREE_DAY_ALLOW') "+ 
        "group by crd.creditDraftNbr, tar.tariffId, "+
        "invitm.itemDescription, rte.rateFlexString03, invitm.itemGlCode, crd.creditCreated " +
        "having to_char(crd.creditCreated,'yyyy-MM-dd HH24MISS') between "+ refDateStartPeriod +" and "+ refDateEndPeriod ;

	lHQLResult = GetValuesByRunHQL(sHQL);

	for (Serializable records : lHQLResult) {
   	    Object[] col = (Object[]) records;
	    ccde = (Double) col[0];
	    acrType = (String) col[1];
	    created = (String) col[2];
	    dnum = (String) col[3];
	    tarId = (String) col[4];
	    qty = (Double) col[5];
	    tariff_desc = (String) col[6];
	    glCode = (String) col[7];
            sp_ind = (String) col[8];

	    InsertRawData(entityName, ccde, acrType, created, dnum, tarId,qty, tariff_desc, glCode, sp_ind);
        }
    }
	 
    public String getRefDate(String sPeriod){
	GeneralReference fg = GeneralReference.findOrCreate("MTL_BW_DATE_ADDENDUM", "DATE", null, null, null, 
			null, null, null, null, null);
	String refDate = null;
		
	if (sPeriod == "start") {
            refDate= fg.getRefValue1();
        } else if (sPeriod == "end") {
	    refDate= fg.getRefValue2();
	}

	if (refDate == null)
	    return null;
	if (isValidDate(refDate)) {
	    return "'"+format.format( format.parse(refDate))+"'";
	} else {
	    fg.setRefValue1(null);
	    HibernateApi.getInstance().update(fg);
	    return null;
	}
    }

    public void setRefDate(String getrefValue1) {
	DynamicHibernatingEntity dhe = new DynamicHibernatingEntity("GeneralReference");
	dhe.setFieldValue(MetafieldIdFactory.valueOf("refType"), "MTL_BW_DATE_ADDENDUM");
	dhe.setFieldValue(MetafieldIdFactory.valueOf("refId1"), "DATE");
	dhe.setFieldValue(MetafieldIdFactory.valueOf("refValue1"), getrefValue1);
	HibernateApi.getInstance().save(dhe);
	HibernateApi.getInstance().flush();
    }
	 
    public void DeleteRecordForDate() {
	try {
	    String createStart=null,createEnd=null;

	    createStart = getRefDate("start");
	    createEnd   = getRefDate("end");

	    if (createStart == null||createEnd == null) {
		createStart = format.format(new Date()) + " 00:00:00";
		createEnd = format.format(new Date()) + " 23:59:59";
	    } else {
		String tmpSdate = createStart.replace("'","");
		String tmpEdate = createEnd.replace("'","");
		createStart = tmpSdate + " 00:00:00";
		createEnd = tmpEdate  + " 23:59:59";
	    }
	    log("createStart:" + createStart + ",createEnd:"+createEnd);
	    DomainQuery dq = QueryFactory.createDomainQuery(entityName)
			.addDqPredicate(PredicateFactory.between(MetafieldIdFactory.valueOf("customEntityFields.custombwadcrtdte"),
	    Timestamp.valueOf(createStart),Timestamp.valueOf(createEnd)));
	    Roastery.getHibernateApi().deleteByDomainQuery(dq);
	    HibernateApi.getInstance().flush();
	} catch (Exception e) {
	}
    }

    //PCR-MNB-000002 begin
    public void InsertRawData(String entityName, Double ccde, String acrType, String created, String dnum, 
				String tarId, Double qty, String tariff_desc, String glCode,String sp_ind) {
        DynamicHibernatingEntity dhe = new DynamicHibernatingEntity(entityName);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadccde"),ccde);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadinvcr"),acrType);
	if (created != null && (!created.isEmpty()))
	    dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadcrtdte"), Timestamp.valueOf(created));
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwaddnum"),dnum);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadtarcde"),tarId);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadqty"),qty);

	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadtardesc"),tariff_desc);
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadglcode"),glCode);//PCR-MNB-000006 add glCode
	dhe.setFieldValue(MetafieldIdFactory.valueOf("customEntityFields.custombwadspind"),sp_ind);
                                                               
	HibernateApi.getInstance().save(dhe);
	HibernateApi.getInstance().flush();
    }
 
    public List<Serializable> GetValuesByRunHQL(String sHQL) {
	String FN_NAME = "(GetValuesByRunHQL) ";
	//log(FN_NAME + "Start.");
	List<Serializable> lResult = [];
	HibernateApi hbrapi = Roastery.getHibernateApi();
	Query qry = hbrapi.createQuery(sHQL) ;
	Iterator it = qry.iterate();
	while (it.hasNext()) {
	    Object[] col = (Object[]) it.next();
	    if (col != null)
		lResult.add(col);
	}

	log(FN_NAME + "lResult.size() : " + lResult.size());
	log(FN_NAME + "End.");
	return lResult;
    }

    public boolean isValidDate(String str) {
	boolean convertSuccess=true;
	try {
	    format.setLenient(false);
	    format.parse(str);
	} catch (ParseException e) {
	    convertSuccess=false;
	}
	return convertSuccess;
    }

    final private String entityName = "com.navis.bw.Custom.Addenndum";
    final private SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
}