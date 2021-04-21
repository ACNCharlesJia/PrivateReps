package SourceCode.CodeExtensions

import com.navis.argo.ArgoExtractField
import com.navis.argo.ContextHelper
import com.navis.argo.business.atoms.ServiceQuantityUnitEnum
import com.navis.argo.business.extract.ChargeableUnitEvent
import com.navis.argo.business.model.GeneralReference
import com.navis.argo.util.XmlUtil
import com.navis.argo.webservice.types.v1_0.GenericInvokeResponseWsType
import com.navis.argo.webservice.types.v1_0.QueryResultType
import com.navis.argo.webservice.types.v1_0.ResponseType
import com.navis.argo.webservice.types.v1_0.ScopeCoordinateIdsWsType
import com.navis.billing.BillingField
import com.navis.billing.business.api.IInvoiceManager
import com.navis.billing.business.model.*
import com.navis.external.argo.AbstractCustomWSHandler
import com.navis.framework.FrameworkBizMetafield
import com.navis.framework.business.Roastery
import com.navis.framework.metafields.MetafieldEntry
import com.navis.framework.metafields.MetafieldId
import com.navis.framework.persistence.HibernateApi
import com.navis.framework.portal.FieldChanges
import com.navis.framework.portal.Ordering
import com.navis.framework.portal.QueryUtils
import com.navis.framework.portal.UserContext
import com.navis.framework.portal.query.DomainQuery
import com.navis.framework.portal.query.PredicateFactory
import com.navis.framework.query.business.SavedPredicate
import com.navis.framework.util.ValueHolder
import com.navis.framework.util.ValueObject
import com.navis.framework.util.message.MessageCollector
import com.navis.services.business.rules.EventType
import com.navis.www.services.argoservice.ArgoServiceLocator
import com.navis.www.services.argoservice.ArgoServicePort
import oracle.jdbc.pool.OracleConnectionPoolDataSource
import org.apache.axis.utils.StringUtils
import org.apache.commons.lang.builder.ToStringBuilder
import org.jdom.Document
import org.jdom.Element

import javax.xml.rpc.ServiceException
import javax.xml.rpc.Stub
import java.sql.Connection
import java.sql.PreparedStatement
import java.sql.ResultSet
import java.sql.SQLException
import java.text.DecimalFormat
import java.text.SimpleDateFormat

public class DCBDEVIBCashierRequestHandler extends AbstractCustomWSHandler {

	private final String REQUEST_ID_PARAM = "request_id";
	private final String REQUEST_TYPE_PARAM = "request_type";

	private final String REQUEST_TYPE_DRAFT_INVOICE = "DRAFT";
	private final String REQUEST_TYPE_FINAL_INVOICE = "FINAL";
	private final String REQUEST_TYPE_CANCEL_INVOICE = "CANCEL";
	private final String REQUEST_TYPE_MSACODE = "MSACODE";

	private final String REQUEST_PAID_THUR_DATE="cut_off_date_time";
	private final String REQUEST_CHARGE_TYPE="charge_type";
	private final String REQUEST_USER_GROUP="user_group";
	private final String REQUEST_TARIFF_TYPE="tariff_type";
	private final String REQUEST_VESSEL_CODE="vessel_code";
	private final String REQUEST_VESSEL_VOY="customs_voyage";
	private final String REQUEST_SHIPPING_ORDER="shipping_order";
	private final String REQUEST_BILL_OF_LADING="bill_of_lading";
	private final String REQUEST_CONTAINER_NUMBER="container_number";
	private final String REQUEST_CUSTOMS_CLEARANCE_INDICATOR="customs_clearance_indicator";
	//CUE webService name
	private static String CODE_EXTENSION_IN_N4T = "CashierUpdateCUEWebService";
	private final String UPDATE_PAYEE_REQUEST = "<groovy class-name=\"" + CODE_EXTENSION_IN_N4T+ "\" class-location=\"code-extension\">\n" +
			"    <parameters>\n" +
			"        %s" +
			"    </parameters>\n" +
			"</groovy>";

	private String MISSING_REQUEST_TYPE_CODE="request_reject_reason_code";
	private String MISSING_REQUEST_TYPE_MESSAGE="request_reject_reason";

	private String getConGkeyByConIdSql="SELECT IU.ID,IU.GKEY FROM NAVISUSR.INV_UNIT IU WHERE IU.ID IN (";
	private String BOL_FIND_UNITNBR_SQL="SELECT DISTINCT IU.ID unitNbr,IU.GKEY gkey FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG WHERE IU.GOODS = IG.GKEY AND IG.GKEY = BLG.GDS_GKEY AND BLG.BL_GKEY = CRG_BILL.GKEY AND CRG_BILL.NBR IN (";
	private String getMSANumberByBlNbrSql="SELECT BOL.CUSTDFF_BL_01 masNbr FROM NAVISUSR.CRG_BILLS_OF_LADING bol WHERE BOL.NBR = ?";
	private String getVesselIdByVesselCode="SELECT VV.ID from NAVISUSR.VSL_VESSELS vv where vv.NAME= ?";
	private String getUnitByNbr="SELECT iu.OOG_BACK_CM olb, IU.OOG_FRONT_CM olf, IU.OOG_LEFT_CM owl, IU.OOG_RIGHT_CM owr, IU.OOG_TOP_CM oh, IU. ID containerNbr, IU.FLEX_STRING07 spectionLevel, IUFV.TIME_RND giTime, RBS. ID op, CASE WHEN IU.REQUIRES_POWER = 1 THEN 'Y' ELSE 'N' END reefer FROM NAVISUSR.INV_UNIT iu, NAVISUSR.INV_UNIT_FCY_VISIT iufv, NAVISUSR.REF_BIZUNIT_SCOPED rbs WHERE IU.GKEY = IUFV.UNIT_GKEY AND IU.LINE_OP = rbs.GKEY AND ROWNUM = 1 AND IU. ID = ?";
	private String getCUEByGkey="SELECT CUE.BL_NBR blNbr, CASE WHEN CUE. CATEGORY = 'IMPRT' OR CUE. CATEGORY = 'STRGE' THEN CUE.IB_CARRIER_NAME WHEN CUE. CATEGORY = 'EXPRT' OR CUE. CATEGORY = 'TRSHP' THEN CUE.OB_CARRIER_NAME END obCarrierName, CASE WHEN CUE. CATEGORY = 'IMPRT' OR CUE. CATEGORY = 'STRGE' THEN CUE.IB_ID WHEN CUE. CATEGORY = 'EXPRT' OR CUE. CATEGORY = 'TRSHP' THEN CUE.OB_ID END voyage, CUE.ISO_CODE isoCode, SUBSTR (CUE.EQ_LENGTH, 4) eqSize, CUE.ISO_GROUP eqType, SUBSTR (CUE.EQ_HEIGHT, 4) eqHeight, DECODE ( CUE. CATEGORY, 'DMSTC', 'Domestic', 'TRSHP', 'Transship', 'STRGE', 'Storage', 'THRGH', 'Through', 'IMPRT', 'Import', 'EXPRT', 'Export' ) CATEGORY, DECODE ( CUE.FREIGHT_KIND, 'MTY', 'EMPTY', CUE.FREIGHT_KIND ) FREIGHT_KIND, cue.IMDG_CLASS, cue.UFV_TIME_IN timeIn, CUE.UFV_TIME_OUT TIMEOUT, et.DESCRIPTION, CUE.CONSIGNEE_ID consigneeId, CUE.PAYEE_CUSTOMER_ID payeeId, CUE.SHIPPER_ID shipperId, CUE.FLEX_DATE05 INDICATOR, CUE.UNIT_GKEY unitGkey FROM NAVISUSR.ARGO_CHARGEABLE_UNIT_EVENTS cue, NAVISUSR.SRV_EVENT_TYPES et WHERE CUE.EVENT_TYPE = ET. ID AND CUE.STATUS = 'DRAFT' AND CUE.EQUIPMENT_ID = ? AND ET. ID = ?";
	private String getAtbAndAtd="SELECT ACV.ID, ACV.ATA,ACV.ATD from NAVISUSR.ARGO_CARRIER_VISIT acv,NAVISUSR.VSL_VESSEL_VISIT_DETAILS vvvd,NAVISUSR.ARGO_VISIT_DETAILS vvd where VVVD.VVD_GKEY=VVD.GKEY AND ACV.CVCVD_GKEY=VVD.GKEY AND ACV.ID= ?";
	private String getBillNbrByUnitGkey="SELECT DISTINCT CRG_BILL.GKEY,CRG_BILL.NBR,CRG_BILL.CUSTDFF_BL_01 CODE FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG, NAVISUSR.INV_UNIT_FCY_VISIT ufv WHERE ufv.GKEY = IU.ACTIVE_UFV AND IU.GOODS = IG.GKEY AND IG.GKEY = BLG.GDS_GKEY AND BLG.BL_GKEY = CRG_BILL.GKEY AND IU.GKEY IN (:value)";
	private String getUnitByNbrSql="SELECT IU.GKEY FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG WHERE IU.GOODS = IG.GKEY AND IG.GKEY = BLG.GDS_GKEY AND BLG.BL_GKEY = CRG_BILL.GKEY AND CRG_BILL.GKEY = ?";
	private String getMultBolbyUnitGkey="SELECT CRG_BILL.NBR NBR,CRG_BILL.CUSTDFF_BL_01 CODE FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG, NAVISUSR.INV_UNIT_FCY_VISIT ufv WHERE ufv.GKEY = IU.ACTIVE_UFV AND IU.GOODS = IG.GKEY AND IG.GKEY = BLG.GDS_GKEY AND BLG.BL_GKEY = CRG_BILL.GKEY AND IU.GKEY IN (?) ORDER BY IU.GKEY";
	private String getMSACodeByNbrSql="SELECT BOL.NBR,BOL.CUSTDFF_BL_01 CODE FROM NAVISUSR.CRG_BILLS_OF_LADING bol WHERE BOL.NBR IN(:bols) ORDER BY BOL.GKEY DESC";

	private static GeneralReference connectN4TReference=null;
	private GeneralReference eventTypeReference= null;

	private String getBOLSqlByUnit="SELECT DISTINCT CRG_BILL.NBR, IU. ID, CRG_BILL.GKEY, DECODE ( ACV_OB.FCY_GKEY, 2161, 'DCBI', 2169, 'DCBD' ) FACILITY, IU. CATEGORY, VVVD_OB.CUSTDFF_VVD_01 manifest, CRG_BILL.nbr sheet, CRG_BILL.MANIFESTED_QTY qty, CRG_BILL.CUSTDFF_BL_01 bl_freight, CRG_BILL.CUSTDFF_BL_02 bl_flag, IU.FLEX_STRING15 sub_category, IU.FREIGHT_KIND freight_kind FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.ARGO_CARRIER_VISIT ACV_OB, navisusr.ARGO_VISIT_DETAILS AVD_OB, navisusr.VSL_VESSEL_VISIT_DETAILS VVVD_OB, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG, navisusr.INV_UNIT_FCY_VISIT IUFV WHERE IU.GKEY = IUFV.UNIT_GKEY AND IUFV.ACTUAL_IB_CV = ACV_OB.GKEY AND AVD_OB.GKEY (+) = ACV_OB.CVCVD_GKEY AND VVVD_OB.VVD_GKEY (+) = AVD_OB.GKEY AND IU.GOODS (+) = IG.GKEY AND IG.GKEY (+) = BLG.GDS_GKEY AND BLG.BL_GKEY (+) = CRG_BILL.GKEY AND VVVD_OB.CUSTDFF_VVD_01 IS NOT NULL AND CRG_BILL. CATEGORY IN ('IMPRT', 'STRGE') AND IU.VISIT_STATE = '1ACTIVE' AND IU.GKEY IN (:value) UNION SELECT DISTINCT CRG_BILL.NBR, IU. ID, CRG_BILL.GKEY, DECODE ( ACV_OB.FCY_GKEY, 2161, 'DCBI', 2169, 'DCBD' ) FACILITY, IU. CATEGORY, VVVD_OB.CUSTDFF_VVD_02 manifest, CRG_BILL.nbr sheet, CRG_BILL.MANIFESTED_QTY qty, CRG_BILL.CUSTDFF_BL_01 bl_freight, CRG_BILL.CUSTDFF_BL_02 bl_flag, IU.FLEX_STRING15 sub_category, IU.FREIGHT_KIND freight_kind FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.ARGO_CARRIER_VISIT ACV_OB, navisusr.ARGO_VISIT_DETAILS AVD_OB, navisusr.VSL_VESSEL_VISIT_DETAILS VVVD_OB, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG, navisusr.INV_UNIT_FCY_VISIT IUFV WHERE IU.GKEY = IUFV.UNIT_GKEY AND IUFV.ACTUAL_OB_CV = ACV_OB.GKEY AND AVD_OB.GKEY (+) = ACV_OB.CVCVD_GKEY AND VVVD_OB.VVD_GKEY (+) = AVD_OB.GKEY AND IU.GOODS (+) = IG.GKEY AND IG.GKEY (+) = BLG.GDS_GKEY AND BLG.BL_GKEY (+) = CRG_BILL.GKEY AND VVVD_OB.CUSTDFF_VVD_02 IS NOT NULL AND CRG_BILL. CATEGORY IN ('EXPRT', 'TRSHP') AND IU.VISIT_STATE = '1ACTIVE' AND IU.GKEY IN (:value)";
	private static String countUnitSql="SELECT COUNT (IU. ID) CONTA_NO FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG WHERE IU.GOODS = IG.GKEY AND IG.GKEY = BLG.GDS_GKEY AND BLG.BL_GKEY = CRG_BILL.GKEY AND CRG_BILL.GKEY = ?";
	private static String unitSql="SELECT IU. ID CONTA_NO, CASE WHEN IU.FREIGHT_KIND = 'FCL' THEN 'F' ELSE 'E' END CONTA_STATUS, IU.SEAL_NBR1 SEAL_NO, RET. ID ISO_TYPE FROM navisusr.CRG_BILLS_OF_LADING CRG_BILL, navisusr.CRG_BL_GOODS BLG, navisusr.INV_UNIT IU, navisusr.INV_GOODS IG, navisusr.INV_UNIT_EQUIP IUE, navisusr.ref_equipment RE, navisusr.ref_equip_type RET WHERE IU.GOODS = IG.GKEY AND IG.GKEY = BLG.GDS_GKEY AND BLG.BL_GKEY = CRG_BILL.GKEY AND IUE.UNIT_GKEY = IU.GKEY AND RE.GKEY = IUE.EQ_GKEY AND RET.GKEY = RE.EQTYP_GKEY AND CRG_BILL.GKEY =?";
	private static String bOLSql="SELECT DISTINCT crg_bill.GKEY, crg_bill.nbr sheet, RUC_POL.PLACE_NAME LOAD_PORT, RFC_POL.CNTRY_NAME LOAD_COUNTRY, RUC_POD.PLACE_NAME DISCHARGE_PORT, RFC_POD.CNTRY_NAME DISCHARGE_COUNTRY, CASE WHEN IU.FREIGHT_KIND = 'MTY' THEN 6 WHEN ACV_OB.FCY_GKEY = 2161 AND CRG_BILL. CATEGORY = 'EXPRT' AND IU.FLEX_STRING15 = 'FEEDER' THEN 6 WHEN ACV_OB.FCY_GKEY = 2161 AND CRG_BILL. CATEGORY = 'TRSHP' THEN 6 WHEN ACV_OB.FCY_GKEY = 2161 AND CRG_BILL. CATEGORY = 'STRGE' THEN 6 WHEN ACV_OB.FCY_GKEY = 2169 AND CRG_BILL. CATEGORY != 'EXPRT' THEN 6 WHEN ACV_OB.FCY_GKEY = 2169 AND CRG_BILL. CATEGORY = 'EXPRT' AND IU.FLEX_STRING15 = 'FEEDER' THEN 6 ELSE 1 END rebate, CASE WHEN CRG_BILL. CATEGORY = 'TRSHP' THEN 'T' END transit, ACV_OB.FCY_GKEY, IU.FLEX_STRING15, IU.FREIGHT_KIND FROM navisusr.CRG_BILLS_OF_LADING crg_bill, navisusr.INV_UNIT IU, navisusr.INV_UNIT_FCY_VISIT IUFV, navisusr.ARGO_CARRIER_VISIT ACV_OB, navisusr.ARGO_VISIT_DETAILS AVD_OB, navisusr.VSL_VESSEL_VISIT_DETAILS VVVD_OB, NAVISUSR.REF_ROUTING_POINT RRP_POL, NAVISUSR.REF_UNLOC_CODE RUC_POL, NAVISUSR.REF_COUNTRY RFC_POL, NAVISUSR.REF_ROUTING_POINT RRP_POD, NAVISUSR.REF_UNLOC_CODE RUC_POD, NAVISUSR.REF_COUNTRY RFC_POD WHERE crg_bill.cv_gkey = ACV_OB.GKEY AND AVD_OB.GKEY (+) = ACV_OB.CVCVD_GKEY AND VVVD_OB.VVD_GKEY (+) = AVD_OB.GKEY AND IU.GKEY (+) = IUFV.UNIT_GKEY AND IUFV.ACTUAL_IB_CV (+) = ACV_OB.GKEY AND RRP_POL.GKEY (+) = CRG_BILL.POL_GKEY AND RUC_POL.GKEY (+) = RRP_POL.UNLOC_GKEY AND RFC_POL.CNTRY_CODE (+) = RUC_POL.CNTRY_CODE AND RRP_POD.GKEY (+) = CRG_BILL.POD1_GKEY AND RUC_POD.GKEY (+) = RRP_POD.UNLOC_GKEY AND RFC_POD.CNTRY_CODE (+) = RUC_POD.CNTRY_CODE AND CRG_BILL.NBR = ?";
	private static String freightXml="<BILL><BILL_SEQ_NO>[#bill_seq_no]</BILL_SEQ_NO><BILL_NO>[#bill_no]</BILL_NO><SHIPPER>SHIPPER</SHIPPER><CONSIGNEE>*</CONSIGNEE><NOTIFY>*</NOTIFY><LOAD_PORT>[#load_port]</LOAD_PORT><LOAD_COUNTRY>[#load_country]</LOAD_COUNTRY><DISCHARGE_PORT>[#discharge_port]</DISCHARGE_PORT><DISCHARGE_COUNTRY>[#discharge_country]</DISCHARGE_COUNTRY><BILL_GROSS_WT>0</BILL_GROSS_WT><BILL_CONTA_NUM>[#bill_conta_num]</BILL_CONTA_NUM><REBATE>[#rebate]</REBATE><TRANSIT>[#transit]</TRANSIT><REMARK></REMARK><CARGO><CARGO_SEQ_NO>1</CARGO_SEQ_NO><GOODS_NAME>2012 DCB ORIGIN</GOODS_NAME><GOODS_HS>21001001</GOODS_HS><GOODS_JT>18</GOODS_JT><MARK_CONTR>*</MARK_CONTR><GROSS_WT>0</GROSS_WT><VOLUME>0</VOLUME></CARGO> [#CONTAINER]</BILL>";
	private static String containerXml="<CONTAINER><CONTA_SEQ_NO>[#conta_seq_no]</CONTA_SEQ_NO><CONTA_NO>[#conta_no]</CONTA_NO><ISO_TYPE >[#iso_type]</ISO_TYPE><CONTA_STATUS>[#conta_status]</CONTA_STATUS><SEAL_NO>[#seal_no]</SEAL_NO></CONTAINER>";

	SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmm");

	public String execute(UserContext paramUserContext,MessageCollector paramMessageCollector, String paramString,Long paramLong) {


		this.log("------------DCBDEVIBCashierRequestHandler execute start:--------");
		this.log(paramString);
		if(paramString != null){


			Document dRequest = XmlUtil.parse(paramString);
			Element eRequest = dRequest.getRootElement();
			String requestType = eRequest.getChildText(REQUEST_TYPE_PARAM);
			String requestID = eRequest.getChildText(REQUEST_ID_PARAM);
			String userGroup=eRequest.getChildText(REQUEST_USER_GROUP);
			if (StringUtils.isEmpty(requestType)){


				MISSING_REQUEST_TYPE_CODE="011";
				MISSING_REQUEST_TYPE_MESSAGE="Invalid Request Type";
				return getXmlAcknowledge("REJECTED", requestID, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,requestType);
			}
			if(REQUEST_TYPE_DRAFT_INVOICE.equals(requestType)){


				this.log("------user group="+userGroup);
				if(!"Cashier".equals(userGroup)&&!"User".equals(userGroup)&&!"Admin".equals(userGroup)){


					MISSING_REQUEST_TYPE_CODE="012";
					MISSING_REQUEST_TYPE_MESSAGE="Invalid User Group";
					return getXmlAcknowledge("REJECTED", requestID, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,requestType);
				}
				Date paidThurDate = sf.parse(eRequest.getChildText(REQUEST_PAID_THUR_DATE));
				SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");
				Integer ptd=Integer.parseInt(sdf.format(paidThurDate));
				Integer nowInt=Integer.parseInt(sdf.format(new Date()));
				if(ptd==null||ptd<nowInt){


					MISSING_REQUEST_TYPE_CODE="003";
					MISSING_REQUEST_TYPE_MESSAGE="Invalid Cut Off Date/Time";
					return getXmlAcknowledge("REJECTED", requestID, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,requestType);
				}
			}
			if(REQUEST_TYPE_DRAFT_INVOICE.equals(requestType)){


				return handleDraftInvoice(eRequest);
			}else if(REQUEST_TYPE_FINAL_INVOICE.equals(requestType)){


				try{


					return handleFinalInvoice(eRequest);
				}catch(Exception e)
				{


					this.log("handlFinalInvoice error:"+e.getMessage());
					e.printStackTrace();
				}
			}else if(REQUEST_TYPE_CANCEL_INVOICE.equals(requestType)){


				return handleCancelInvoice(eRequest);
			}else if(REQUEST_TYPE_MSACODE.equals(requestType)){


				return getMSACodeByNbr(eRequest);
			}
		}else{


			MISSING_REQUEST_TYPE_CODE="999";
			MISSING_REQUEST_TYPE_MESSAGE="Invalid Request Xml";
			return getXmlAcknowledge("REJECTED", "", MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,"");
		}
	}

	public String getMSACodeByNbr(Element eRequest){


		String requestId = eRequest.getChildText(REQUEST_ID_PARAM);
		String bolString= eRequest.getChildText("bill_of_lading");
		String[] bolArray= bolString.split(";");
		StringBuffer bols= new StringBuffer();
		int temp = 0;
		for(String bol:bolArray){


			if(temp>0){


				bols.append(",");
			}
			temp++;
			bols.append("'"+bol+"'");
		}
		Map<String,String> map= new HashMap<String,String>();
		Connection conn =  JdbcUtils.getConnection();
		PreparedStatement stmt=null;
		ResultSet rs=null;
		String sql=getMSACodeByNbrSql.replace(":bols", bols);
		this.log("--------G1.1------sql="+sql);
		try{


			stmt=conn.prepareStatement(sql);
			rs=stmt.executeQuery();
			while(rs.next()){


				String nbr= rs.getString("NBR");
				String code = rs.getString("CODE");
				if(!StringUtils.isEmpty(code)&&!"null".equals(code)&&!map.containsKey(nbr)){


					map.put(nbr,code);
				}
			}
		}catch(SQLException e){


			this.log("------G2.1-----error,e="+e.getMessage());
			e.printStackTrace();
			return e.getMessage();
		}finally{


			if(rs!=null&&!rs.isClosed()){


				rs.close();
			}
			if(stmt!=null&&!stmt.isClosed()){


				stmt.close();
			}
			conn.close();
		}
		StringWriter strXml = new StringWriter();
		def xml = new groovy.xml.MarkupBuilder(strXml);
		xml."invoice"(){


			"request_type"(REQUEST_TYPE_MSACODE);
			"request_id"(requestId);
			"status"("ACCEPTED");
			Iterator<String> itat=map.keySet().iterator();
			while(itat.hasNext()){


				String key = itat.next();
				"invoice_item"(){


					"invoice_item_bill_of_lading"(key);
					"invoice_item_msa_number"(map.get(key));
				}
			}
		}
		return strXml;
	}

	public String handleFinalInvoice(Element eRequest) {


		this.log("----------step 1 :----------handleFinalInvoice");
		this.log("handleFinalInvoice,--------------start Date="+new Date());
		String requestId=(String) eRequest.getChildText(REQUEST_ID_PARAM);
		String invoiceNbr=(String)eRequest.getChildText("invoice_number");
//		String userGroup=eRequest.getChildText(REQUEST_USER_GROUP);
//		String tariffType=(String) eRequest.getChildText(REQUEST_TARIFF_TYPE);
		this.log("----------step 2 :----------requestId,invoiceNbr="+requestId+","+invoiceNbr);
		Invoice invoice = Invoice.findInvoiceByDraftNbr(invoiceNbr);
		this.log("----------step 3 :----------invoice="+ToStringBuilder.reflectionToString(invoice));
//		Map<Long,Long> cueIdMap=new HashMap<Long,Long>();
		if(invoice==null){


			MISSING_REQUEST_TYPE_CODE="015";
			MISSING_REQUEST_TYPE_MESSAGE="Invalid Draft Invoice";
			return getXmlAcknowledge("REJECTED", requestId, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,REQUEST_TYPE_FINAL_INVOICE);
		}
		//To save the InvoiceItem while need add
		Set<InvoiceItem> addItemSet = new HashSet<InvoiceItem>();
		//To save the InvoiceItemId while need to remove
		Set<String> removeItemSet = new HashSet<String>();
		//To save the itemId and payeeValue while need update payee
		Map<String,String> payeeMap = new HashMap<String,String>();
		//To save the itemId while need update cashier indicator to 0
		Set<String> indiSet=new HashSet<String>();
		//save the itemId and statusValue while need update status
		Map<String,String> statusMap=new HashMap<String,String>();

		Map<String,String> finalItemResultMap=new HashMap<String,String>();

		Map<String,String> allSettled=new HashMap<String,String>();

		Map<String,String> cutOfTimeMap=new HashMap<String,String>();

		Map<String,String> payThroughDateMap=new HashMap<String,String>();

		Set<InvoiceItem> removeItem = new HashSet<InvoiceItem>();

		//the items of accepted
		List<Element> items= eRequest.getChildren("invoice_item");
		this.log("----------step 4 :----------items.size()="+items.size());

		DecimalFormat df=new DecimalFormat("#0.00");
		Double totalAmount=df.parse(eRequest.getChildText("total_amount")==null?"0":eRequest.getChildText("total_amount"));
		if(items==null||items.size()==0){


			IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
			if(!invoice.isFinalized()){


				invoiceManager.delete(invoice);
			}
			String results=createFinalXmlByInvoice(requestId);
			this.log("handleFinalInvoice,--------------end Date="+new Date());
			this.log("---------------Final end :"+results);
			return results;
		}
		for(Element itemElement:items){


			String payee=(String) itemElement.getChildText("invoice_item_payee");
			if(StringUtils.isEmpty(payee)){


				payee=(String) itemElement.getChildText("invoice_item_paid_by");
			}
			if(StringUtils.isEmpty(payee)){


				MISSING_REQUEST_TYPE_CODE="005";
				MISSING_REQUEST_TYPE_MESSAGE="Invalid Final Payee";
				return getXmlAcknowledge("REJECTED", requestId, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,REQUEST_TYPE_FINAL_INVOICE);
			}
			preprocessFinalItem(itemElement,invoice,addItemSet,removeItemSet,payeeMap,indiSet,statusMap,finalItemResultMap,allSettled,payee,cutOfTimeMap,payThroughDateMap);
		}
		Set<InvoiceItem> invoiceItems=(Set<InvoiceItem>)invoice.getInvoiceInvoiceItems();
		this.log("----------step 6 :----------invoiceItems.size="+invoiceItems.size());
		Iterator<InvoiceItem> invIt=invoiceItems.iterator();
		while(invIt.hasNext()){


			InvoiceItem invoiceItem=invIt.next();
			String itemGkey = String.valueOf(invoiceItem.getItemGkey());
			String cueGkey=String.valueOf(invoiceItem.getItemServiceExtractGkey());
			if(payeeMap.containsKey(itemGkey)){


				String payee=payeeMap.get(itemGkey);
				this.log("----------step 7.3 :----------itemGkey="+itemGkey+",payee="+payee);
				payeeMap.put(cueGkey,payeeMap.get(itemGkey));
				payeeMap.remove(itemGkey);
			}
			this.log("----------step 7.5 :----------indiSet="+indiSet);
			if(indiSet.contains(itemGkey)){


				this.log("----------step 7.5.1 :----------invoiceItem.getItemServiceExtractGkey()="+invoiceItem.getItemServiceExtractGkey());
				indiSet.add(cueGkey);
				indiSet.remove(itemGkey);
			}
			this.log("----------step 7.6.0 :----------itemGkey="+itemGkey+"allSettled.size="+allSettled.size());
			if(allSettled.containsKey(itemGkey)){


				this.log("----------step 7.6.1 :----------cueGkey="+cueGkey+",size="+allSettled.size());
				if("CUE".equals(allSettled.get(itemGkey))){


					this.log("----------step 7.6.1.1 :----------cueGkey="+cueGkey);
					allSettled.put(cueGkey,"CUE");
				}else{


					this.log("----------step 7.6.1.2 :----------cmeGkey="+cueGkey);
					allSettled.put(cueGkey,"CME");
				}
				allSettled.remove(itemGkey);
				if(allSettled.containsKey(cueGkey)){


					this.log("----------step 7.6.2 :----------itemGkey="+itemGkey+",size="+allSettled.size());
				}
			}
			if(statusMap.containsKey(itemGkey)){


				this.log("----------step 7.4.1 :----------ItemServiceExtractGkey="+invoiceItem.getItemServiceExtractGkey()+",statusMap.get(invoiceItem.getItemGkey())="+statusMap.get(invoiceItem.getItemGkey()));
				statusMap.put(cueGkey,statusMap.get(itemGkey));
				statusMap.remove(itemGkey);
			}
			if(removeItemSet.size()>0){


				if(removeItemSet.contains(itemGkey)){


					this.log("----------step 7.2 :----------removeItemGkey="+itemGkey);
					removeItemSet.remove(itemGkey);
					removeItem.add(invoiceItem);
					continue;
				}
			}
			String addNotes=finalItemResultMap.get(String.valueOf(invoiceItem.getItemGkey()))+",,";
			this.log("----------step 6.1 :----------itemGkey="+invoiceItem.getItemGkey()+",seq="+finalItemResultMap.get(String.valueOf(invoiceItem.getItemGkey())));
			if(StringUtils.isEmpty(invoiceItem.getItemNotes())){


				invoiceItem.setItemNotes(addNotes);
			}else{


				invoiceItem.setItemNotes(addNotes+invoiceItem.getItemNotes());
			}
			this.log("----------step 6.2 :----------itemNotes="+invoiceItem.getItemNotes());
			this.log("----------step 7.1 :----------cueGkey="+cueGkey);
			this.log("----------step 7.4 :----------statusMap="+statusMap);
			this.log("----------step 7.7 :----------cutOfTimeMap="+cutOfTimeMap);
			if(cutOfTimeMap.containsKey(itemGkey+",0")){


				this.log("----------step 7.7.1 :----------cueGkey="+cueGkey+",size="+cutOfTimeMap.size());
				cutOfTimeMap.put(invoiceItem.getItemEventEntityId()+",0",cutOfTimeMap.get(itemGkey+",0"));
				cutOfTimeMap.remove(itemGkey+",0");
			}
			if(cutOfTimeMap.containsKey(itemGkey+",1")){


				this.log("----------step 7.7.2 :----------cueGkey="+cueGkey+",size="+cutOfTimeMap.size());
				cutOfTimeMap.put(invoiceItem.getItemEventEntityId()+",1",cutOfTimeMap.get(itemGkey+",1"));
				cutOfTimeMap.remove(itemGkey+",1");
			}
			this.log("----------step 7.8 :----------payThroughDateMap="+payThroughDateMap);
			if(payThroughDateMap.containsKey(itemGkey)){


				this.log("----------step 7.8.1 :----------cueGkey="+cueGkey+",size="+payThroughDateMap.size());
				payThroughDateMap.put(invoiceItem.getItemEventEntityId(),null);
				payThroughDateMap.remove(itemGkey);
			}
		}
		if(removeItem.size()>0){


			invoiceItems.removeAll(removeItem);
		}
		if(addItemSet.size()!=0){


			this.log("----------step 7.9 addItemSet:----------");
			invoice.addInvoiceInvoiceItems(addItemSet);
		}

		if(payeeMap.size()>0){


			payeeMap.put("type","finalPayee");
			String respones=invokeN4BWS(payeeMap);
			this.log("----------step 7.6.3 :----------payeeMap.respones="+respones);
		}
		if(statusMap.size()>0){


			statusMap.put("type","status");
			String respones=invokeN4BWS(statusMap);
			this.log("----------step 7.6.2 :----------statusMap.respones="+respones);
		}
		if(allSettled.size()>0){


			allSettled.put("type", "allSettled");
			String respones=invokeN4BWS(allSettled);
			this.log("----------step 7.6.4 :----------allSettled.respones="+respones);
		}
		if(indiSet.size()>0){


			Map<String,String> indiMap=new HashMap<String,String>();
			indiMap.put("type", "indicator");
			Iterator<String> it=indiSet.iterator();
			while(it.hasNext()){


				String key=it.next();
				indiMap.put(key, "");
			}
			String respones=invokeN4BWS(indiMap);
			this.log("----------step 7.6.5 :----------indiSet.respones="+respones);
		}

		if(cutOfTimeMap.size()>0){


			cutOfTimeMap.put("type","cutOffTime");
			String respones=invokeN4BWS(cutOfTimeMap);
			this.log("----------step 7.10.1 :----------cutOffTime.respones="+respones);
		}
		if(payThroughDateMap.size()>0){


			payThroughDateMap.put("type","payThrouthDate");
			String respones=invokeN4BWS(payThroughDateMap);
			this.log("----------step 7.11.1 :----------payThroughDate.respones="+respones);
		}
		this.log("----------step 7.12 :----------itemSize="+invoice.getInvoiceInvoiceItems().size());
		if(invoice.getInvoiceInvoiceItems().size()==0){


			IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
			this.log("----------step 7.12.1 :----------invoiceManager="+ToStringBuilder.reflectionToString(invoiceManager));
			if(!invoice.isFinalized()){


				this.log("----------step 7.12.2 :----------delete invoice");
				invoiceManager.delete(invoice);
			}
			this.log("----------step 7.12.3 :----------requestId="+requestId);
			String results=createFinalXmlByInvoice(requestId);
			this.log("---------------Final end :"+results);
			return results;
		}
//		Invoice inInvoice = Invoice.findInvoiceByDraftNbr(invoiceNbr);
		String paymentType=(String)eRequest.getChildText("payment_type");
		if(!StringUtils.isEmpty(paymentType)){


			invoice.setInvoiceFlexString02(paymentType);
		}
		invoice.setInvoiceFlexString03("Y");
		invoice.setInvoiceFlexString04(requestId);
		this.log("----------step 7.13 :----------string03="+invoice.getInvoiceFlexString03());
//		boolean isSuccess=finalizeInvoice(invoice);
		String results=createFinalXmlByInvoice(requestId);
		this.log("handleFinalInvoice,--------------end Date="+new Date());
		this.log("---------------Final end :"+results);
		return results;
	}

	private String createFinalXmlByInvoice(String requestId){


		StringBuffer result=new StringBuffer("<invoice_status><status>ACCEPTED</status><request_id>"+requestId+"</request_id><request_type>FINAL</request_type><reject_reason_code /><reject_reason /></invoice_status>");
		return result.toString();
	}

	private void preprocessFinalItem(Element itemElement,Invoice invoice,Set<InvoiceItem> addItem,
									 Set<String> removeItem,Map<String,String> payeeMap,Set<String> indiSet,Map<String,String> statusMap,
									 Map<String,String> finalItemResultMap,Map<String,String> allSettled,String payee,
									 Map<String,String> cutOfTimeMap,Map<String,String> payThroughDateMap){


		String itemId=(String)itemElement.getChildText("invoice_item_id");
		String cashierCreated=(String)itemElement.getChildText("invoice_item_cashier_created");
		String status=null;
		String selIndication=(String) itemElement.getChildText("invoice_item_selection_indicator");
		this.log("----------step 5.1 :----------itemId="+itemId+",cashierCreated="+cashierCreated+",selIndication="+selIndication+",payee="+payee);

		if(!"Y".equals(selIndication)){


			//add the item to removeItem and set status = QUEUED
			if(!StringUtils.isEmpty(itemId)){


				removeItem.add(itemId);
			}
			status="QUEUED";
			this.log("----------step 5.2 :----------status="+status);
		}else {


			String upIndication=(String) itemElement.getChildText("invoice_item_update_indicator");
			String monthlySettle=(String) itemElement.getChildText("invoice_item_monthly_settle");
			String shippingLine=(String) itemElement.getChildText("invoice_item_bill_shipping_line");
			if("Y".equals(upIndication)){


				if("Y".equals(monthlySettle)){


					payee=(String)itemElement.getChildText("invoice_item_payee");
				}else if("Y".equals(shippingLine)){


					payee=(String)itemElement.getChildText("invoice_item_operator");
				}
				if(!StringUtils.isEmpty(itemId)){


					removeItem.add(itemId);
				}
				status="QUEUED";
				//update the cashier indicator to '0' (CUE/CME.flexDate05)
				if(!StringUtils.isEmpty(itemId)){


					indiSet.add(itemId);
				}
			}
			//put the itemId and payee into payeeMap and set status = INVOICED
			if(!StringUtils.isEmpty(itemId)){


				payeeMap.put(itemId, payee);
			}
//			if(totalAmount!=0D){
//				status="INVOICED";
//			}
			this.log("----------step 5.3 :----------upIndication="+upIndication+",payee="+payee+",status="+status);
			//update the 'GRANT_PAYMENT_PERMISSION_IND' to 'Y(CUE/CME.flexLong05)
			if(!StringUtils.isEmpty(itemId)){


				String invoiceType=invoice.getInvoiceInvoiceType().getInvtypeId();
				this.log("----------step 5.3.0.1 :----------invoiceType="+invoiceType);
				if(invoiceType.contains("CME")){


					allSettled.put(itemId,"CME");
				}else{


					allSettled.put(itemId,"CUE");
				}
			}
			if(!StringUtils.isEmpty(itemId)&&"Y".equals(shippingLine)){


				payThroughDateMap.put(itemId,null);
			}
			this.log("----------step 5.3.1 :----------allSettled.size="+allSettled.size());
		}
		this.log("----------step 5.3.2 :----------charge type="+itemElement.getChildText("invoice_item_charge_type"));
		String dateTime= itemElement.getChildText("invoice_item_cut_off_date_time").substring(0,8)+"2359";
		this.log("----------step 5.3.3 :----------dateTime="+dateTime);
		if(!StringUtils.isEmpty(itemId)){


			if("STORAGE".equals(itemElement.getChildText("invoice_item_charge_type"))){


				cutOfTimeMap.put(itemId+",0",dateTime);
			}
			if("REEFER".equals(itemElement.getChildText("invoice_item_charge_type"))){


				cutOfTimeMap.put(itemId+",1",dateTime);
			}
		}
		if("Y".equals(cashierCreated)){


			this.log("----------step 5.4.1 :----------tariffId="+itemElement.getChildText("invoice_item_tariff_id"));
			//create new item and put it in addItem while invoice_item_cashier_created is 'Y'
			Tariff tariff = Tariff.findTariff(String.valueOf(itemElement.getChildText("invoice_item_tariff_id")));
			this.log("----------step 5.4.2 :----------tariff="+ToStringBuilder.reflectionToString(tariff));
			Customer cust = Customer.findCustomer(payee, null);
			this.log("----------step 5.4.3 :----------customer="+ToStringBuilder.reflectionToString(cust));
			Date effectDate = new Date();
			CustomerContract custContract = CustomerContract.findEffectiveCustomerContract(effectDate , cust.getCustGkey() ,null);
			this.log("----------step 5.4.4 :----------custContract="+ToStringBuilder.reflectionToString(custContract));
			Contract cnt = custContract.getCusconBaseContract();
//			Contract cnt = invoice.getInvoiceContract();
			this.log("----------step 5.4.5 :----------cnt="+ToStringBuilder.reflectionToString(cnt));
			TariffRate tariffRate = cnt.findCurrentTariffRate(tariff);
			this.log("----------step 5.4.6 :----------tariffRate="+ToStringBuilder.reflectionToString(tariffRate));
			InvoiceItem invoiceItem = new InvoiceItem();
			FieldChanges fieldChanges = new FieldChanges();
			this.log("----------step 5.4.4 :----------invoiceItem="+ToStringBuilder.reflectionToString(invoiceItem));
			fieldChanges.setFieldChange(BillingField.ITEM_DESCRIPTION, tariff.getTariffDescription());//tariff.getTariffDescription()
			this.log("----------step 5.4.5 :----------invoice_item_tariff_amount="+itemElement.getChildText("invoice_item_tariff_amount"));
			fieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, Double.valueOf(itemElement.getChildText("invoice_item_tariff_amount")));
			this.log("----------step 5.4.6 :----------invoice="+ToStringBuilder.reflectionToString(invoice));
			fieldChanges.setFieldChange(BillingField.ITEM_INVOICE, invoice);
			this.log("----------step 5.4.7 :----------invoice_item_rate="+itemElement.getChildText("invoice_item_rate"));
			fieldChanges.setFieldChange(BillingField.ITEM_TARIFF_RATE,tariffRate);//itemElement.getChildText("invoice_item_rate")
			fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY, Double.valueOf(itemElement.getChildText("invoice_item_quantity")));
			fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_BILLED, Double.valueOf(itemElement.getChildText("invoice_item_quantity")));
			fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_UNIT, ServiceQuantityUnitEnum.ITEMS);
//			fieldChanges.setFieldChange(BillingField.ITEM_EVENT_TYPE_ID, (String) itemElement.getChildText("invoice_item_charge_type"));
			fieldChanges.setFieldChange(BillingField.ITEM_NOTES, (String) itemElement.getChildText("cashier_item_seq")+",Y");
			String container=(String)itemElement.getChildText("invoice_item_container_number");
			if(StringUtils.isEmpty(container)){


				container="MISC";
			}
			fieldChanges.setFieldChange(BillingField.ITEM_EVENT_ENTITY_ID, container);
			String startDate=(String) itemElement.getChildText("invoice_item_start_date");
			Date fromDate=new Date();
			if(!StringUtils.isEmpty(startDate)){


				fromDate=sf.parse(startDate);
			}
			fieldChanges.setFieldChange(BillingField.ITEM_FROM_DATE, fromDate);
//			invoiceItem.setItemNotes((String) itemElement.getChildText("cashier_item_seq")+",Y");
			this.log("----------step 5.4.8 :----------fieldChanges="+ToStringBuilder.reflectionToString(fieldChanges));
			invoiceItem.applyFieldChanges(fieldChanges);
			this.log("----------step 5.4.9 :----------invoiceItem="+ToStringBuilder.reflectionToString(invoiceItem));
			addItem.add(invoiceItem);
			this.log("----------step 5.4.10 :----------addItem="+ToStringBuilder.reflectionToString(addItem));
			return ;
		}
		//update CUE/CME.status
		if(status!=null&&!StringUtils.isEmpty(itemId)){


			statusMap.put(itemId, status);
			this.log("----------step 5.4.11 :----------itemId,status="+itemId+","+status);
		}
		if(!StringUtils.isEmpty(itemId)){


			finalItemResultMap.put(itemId,(String) itemElement.getChildText("cashier_item_seq"));
		}
		this.log("----------step 5.4.13 :----------finalItemResultMap.size="+finalItemResultMap.size());
	}


	public String handleDraftInvoice(Element eRequest) {


		this.log("handleDraftInvoice,start--------------,requestId="+eRequest.getChildText(REQUEST_ID_PARAM));
		this.log("handleDraftInvoice,--------------start Date="+new Date());
		Invoice invoice=null;
		String resultXml=null;

		String requestId=eRequest.getChildText(REQUEST_ID_PARAM);

		String vesselCode = eRequest.getChildText(REQUEST_VESSEL_CODE);
		String vesselVoy= eRequest.getChildText(REQUEST_VESSEL_VOY);

		String shippingOrder=eRequest.getChildText(REQUEST_SHIPPING_ORDER);
		String billOfLading = eRequest.getChildText(REQUEST_BILL_OF_LADING);
		String containerNbr = eRequest.getChildText(REQUEST_CONTAINER_NUMBER);
		String userGroup=eRequest.getChildText(REQUEST_USER_GROUP);
		if(!StringUtils.isEmpty(vesselVoy)&&(!StringUtils.isEmpty(billOfLading)||!StringUtils.isEmpty(containerNbr))){


			MISSING_REQUEST_TYPE_CODE="013";
			MISSING_REQUEST_TYPE_MESSAGE="cue and cme can not exist at the same time";
			return getXmlAcknowledge("REJECTED", requestId, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,REQUEST_TYPE_DRAFT_INVOICE);
		}
		Map<String,String> cnoAndGkey=new HashMap<String,String>();
		Set<String> containerSet = getContainerSet(shippingOrder,billOfLading,containerNbr,cnoAndGkey);
		if(StringUtils.isEmpty(containerNbr)&&!StringUtils.isEmpty(billOfLading)&&(containerSet.size()==0)){


			MISSING_REQUEST_TYPE_CODE="002";
			MISSING_REQUEST_TYPE_MESSAGE="Invalid Shipping Order/Bill or Lading";
			return getXmlAcknowledge("REJECTED", requestId, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,REQUEST_TYPE_DRAFT_INVOICE);
		}
		this.log("-------D1-----,containerSet.size="+containerSet.size());
		String clearanceIndi=(String)eRequest.getChildText("customs_clearance_indicator");
		this.log("-------D2 -------,Cle="+clearanceIndi);
		if(containerSet.size()>0){


			this.log("-------D3-------,Cle="+eRequest.getChildText("customs_clearance_indicator"));
			boolean isClearance="Y".equals(clearanceIndi);
			StringBuffer containers=new StringBuffer();
			Iterator<String> cnoGkeyItat = cnoAndGkey.keySet().iterator();
			while(cnoGkeyItat.hasNext()){


				String cnoId=cnoGkeyItat.next();
				String cnoGkey = cnoAndGkey.get(cnoId);
				if(containers.length()>1){


					containers.append(",");
				}
				containers.append(cnoGkey);
			}
			this.log("-------D4-------,containers="+containers);
			try{


				String modifyFreightValue = doModifyFreight(containers.toString(),isClearance);
				this.log("-------D5-------,modifyFreightValue="+modifyFreightValue);
			}catch(Exception e2){


				this.log("-------D5.1-------,modifyFreightValue.Exception="+e2.getMessage());
				e2.printStackTrace();
			}
			//update pay through date (ufvFlexDate03)
			String cotOffTime=eRequest.getChildText(REQUEST_PAID_THUR_DATE);
			this.log("-------D6-------,cotOffTime="+cotOffTime);
			Map<String,String> payThroughDateMap=new HashMap<String,String>();
			payThroughDateMap.put("type","payThrouthDate");
			for(String con:containerSet){


				payThroughDateMap.put(con,cotOffTime);
			}
			this.log("-------D7-------,payThroughDateMap.size()="+payThroughDateMap.size());
			if(payThroughDateMap.size()>1){


				String result=invokeN4BWS(payThroughDateMap);
				this.log("-------D8-------,result="+result);
			}
		}

		Date paidThurDate = sf.parse(eRequest.getChildText(REQUEST_PAID_THUR_DATE));
		String chargeTypes=eRequest.getChildText(REQUEST_CHARGE_TYPE);
		List<String> eventTypeList = getEventTypeList(chargeTypes);

		String tariffType=eRequest.getChildText(REQUEST_TARIFF_TYPE);
		this.log("-------D9-------,tariffType="+tariffType);
		eventTypeReference = GeneralReference.findUniqueEntryById("CASHIEREVENTTYPE", tariffType, null, null);
		this.log("-------D9.1-------,eventTypeReference="+ToStringBuilder.reflectionToString(eventTypeReference));
		if(eventTypeReference!=null){


			if(!StringUtils.isEmpty(eventTypeReference.getRefValue1())){


				eventTypeList.add(eventTypeReference.getRefValue1());
				chargeTypes=eventTypeReference.getRefValue1();
			}
			if(!StringUtils.isEmpty(eventTypeReference.getRefValue2())){


				eventTypeList.add(eventTypeReference.getRefValue2());
				chargeTypes=eventTypeReference.getRefValue2();
			}
			if(!StringUtils.isEmpty(eventTypeReference.getRefValue3())){


				eventTypeList.add(eventTypeReference.getRefValue3());
				chargeTypes=eventTypeReference.getRefValue3();
			}
			if(!StringUtils.isEmpty(eventTypeReference.getRefValue4())){


				eventTypeList.add(eventTypeReference.getRefValue4());
				chargeTypes=eventTypeReference.getRefValue4();
			}
			if(!StringUtils.isEmpty(eventTypeReference.getRefValue5())){


				eventTypeList.add(eventTypeReference.getRefValue5());
				chargeTypes=eventTypeReference.getRefValue5();
			}
		}
		this.log("-------D10-------,chargeTypes="+chargeTypes);
		String invoiceTypeId=getInvoiceType(chargeTypes,vesselVoy,userGroup);
		this.log("-------D11-------,invoiceTypeId="+invoiceTypeId);
		if(containerSet!=null&&containerSet.size()!=0){


			//FSP
			if(invoiceTypeId.contains("CUE")){


				String containers=containerSet.toString().replace(",", ";");
				if(containers.length()>0&&containerNbr.length()>0){


					containers=containers.substring(1,containers.length()-1);
					containers=containers+";"+containerNbr;
				}
				Map<String,String> payeeMap =new HashMap<String,String>();
				payeeMap.put("type","payee");
				payeeMap.put("payee",tariffType);
				payeeMap.put("containers",containers);
				String updatePayeeResponse=invokeN4BWS(payeeMap);
			}

			//create invoice object and fill value
		}
		this.log("generateInvoice,--------------start Date="+new Date());
		invoice=generateInvoice(containerSet, eventTypeList, paidThurDate, invoiceTypeId, tariffType,vesselVoy,cnoAndGkey);
		this.log("generateInvoice,--------------end Date="+new Date());
		if(invoice==null){


			MISSING_REQUEST_TYPE_CODE="007";
			MISSING_REQUEST_TYPE_MESSAGE="Draft Invoice generated with error";
			return getXmlAcknowledge("REJECTED", requestId, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,REQUEST_TYPE_DRAFT_INVOICE);
		}
		if(!"MISC".equals(containerNbr)){


			if(invoice.getInvoiceInvoiceItems()==null||invoice.getInvoiceInvoiceItems().size()==0){


				IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
				if(!invoice.isFinalized()){


					invoiceManager.delete(invoice);
				}
				MISSING_REQUEST_TYPE_CODE="014";
				MISSING_REQUEST_TYPE_MESSAGE="Draft Invoice Item generated with error";
				return getXmlAcknowledge("REJECTED", requestId, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,REQUEST_TYPE_DRAFT_INVOICE);
			}
		}
		this.log("invoice--------------"+ToStringBuilder.reflectionToString(invoice));

		//get String of xml from invoice
		if(invoice!=null){


			DraftInvoice draftInvoice=processInvoice(invoice,eRequest,invoiceTypeId,cnoAndGkey);
			this.log("DraftInvoice--------------"+ToStringBuilder.reflectionToString(draftInvoice));

			Set<DraftInvoiceItem> draftItemSet = draftInvoice.getInvoiceItemSet();
			if(draftItemSet!=null){


				Iterator<DraftInvoiceItem> draftItemItat = draftItemSet.iterator();
				while(draftItemItat.hasNext()){


					DraftInvoiceItem draftItem = draftItemItat.next();
					if(!"UNIT_POC".equals(draftItem.getChargeType())){


						continue;
					}
					if(StringUtils.isEmpty(draftItem.getmSANumber())){


						IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
						this.log("----------POC=0 1 :----------invoiceManager="+ToStringBuilder.reflectionToString(invoiceManager));
						if(!invoice.isFinalized()){


							this.log("----------POC=0  2 :----------delete invoice");
							invoiceManager.delete(invoice);
						}
						MISSING_REQUEST_TYPE_CODE="023";
						MISSING_REQUEST_TYPE_MESSAGE="can not get POC amount in MSA";
						return getXmlAcknowledge("REJECTED", requestId, MISSING_REQUEST_TYPE_CODE, MISSING_REQUEST_TYPE_MESSAGE,REQUEST_TYPE_DRAFT_INVOICE);
					}
					continue;
				}
			}
			resultXml = createDraftXmlByInvoice(draftInvoice);
		}
		this.log("result Xml----------------");
		this.log(resultXml);
		this.log("handleDraftInvoice,--------------end Date="+new Date());
		return resultXml;
	}

	private String doModifyFreight(String con,boolean isClearance){


		def sr = this.getLibrary("DCBDevMSADataExWebServiceProxy");
		Connection conn =  JdbcUtils.getConnection();
		String sql=getBOLSqlByUnit.replace(":value", con);
		this.log("--------D4.1------sql="+sql);
		PreparedStatement stmt=conn.prepareStatement(sql);
		ResultSet rs=stmt.executeQuery();
		Map<String,String> msaCodeMap=new HashMap<String,String>();
		msaCodeMap.put("type","msaCode");
		Set<String> msaCatchSet=new HashSet<String>();
		try{


			while(rs.next()){


				int flag=rs.getInt("bl_flag");
				String manifest=rs.getString("manifest");
				String code=rs.getString("bl_freight");
				String sheet=rs.getString("sheet");
				if(msaCatchSet.contains(sheet)){


					continue;
				}
				String payer="*";
				String goods="2012 DCB ORIGIN";
				String cargo="18";
				int weight=0;
				String facility=rs.getString("facility");
				String category=rs.getString("category");
				String fk=rs.getString("freight_kind");
				String subCat=rs.getString("sub_category");
				int layer=5;
				if("EXPRT".equals(category)||"TRSHP".equals(category)){


					layer=1;
				}
				int rebate=1;
				this.log("--------D4.2------category="+category+",subCat="+subCat);
				if(isClearance){


					if(!"EXPRT".equals(category)||!"FEEDER".equals(subCat)){


						return "";
					}
					rebate=5;
				}else{


					if("MTY".equals(fk)){


						rebate=6;
					}
					if("DCBI".equals(facility)){


						if("TRSHP".equals(category)||"STRGE".equals(category)){


							rebate=6;
						}
						if("EXPRT".equals(category)&&"FEEDER".equals(subCat)){


							rebate=6;
						}
					}
					if("DCBD".equals(facility)){


						if(!"EXPRT".equals(category)){


							rebate=6;
						}else if("FEEDER".equals(subCat)){


							rebate=6;
						}
					}
				}
				this.log("--------D4.3------rebate="+rebate);
				String transit=null;
				String gkey=rs.getString("GKEY");
				int resultCode=0;
				PreparedStatement unitCountStmt = conn.prepareStatement(countUnitSql);
				unitCountStmt.setString(1,gkey);
				ResultSet unitRs = unitCountStmt.executeQuery();
				int unitCount=0;
				while(unitRs.next()){


					unitCount=unitRs.getInt(1);
				}
				PreparedStatement unitStmt = conn.prepareStatement(unitSql);
				unitStmt.setString(1, gkey);
				unitRs=unitStmt.executeQuery();
				String containers="";
				int containerNo=1;
				while(unitRs.next()){


					containers+=replaceContainer(unitRs,String.valueOf(containerNo));
					containerNo++;
				}
				PreparedStatement bOLStmt = conn.prepareStatement(bOLSql);
				bOLStmt.setString(1, sheet);
				unitRs=bOLStmt.executeQuery();
				String freightXml="";
				while(unitRs.next()){


					freightXml=replaceFreight(unitRs, "1", unitCount, containers);
				}
				if(unitRs!=null&&!unitRs.isClosed()){


					unitRs.close();
				}
				if(unitCountStmt!=null&&!unitCountStmt.isClosed()){


					unitCountStmt.close();
				}
				if(bOLStmt!=null&&!bOLStmt.isClosed()){


					bOLStmt.close();
				}
				this.log("-------modifyFreight------,freightXml="+freightXml);
				def value;
				this.log("---------------D4.3.1------------,code="+code);
				if(StringUtils.isEmpty(code)){


					this.log("---------------D4.3.2------------,appendFreight");
					value=sr.appendFreight("Poc#1001",manifest,sheet,layer,payer,goods,cargo,weight,rebate,transit,freightXml);
					resultCode=value.getFailure();
					if(resultCode==0){


						code=value.getContent();
					}
					if(resultCode==406){


						if(value.getMessage().contains(":")){


							code=value.getMessage().substring(value.getMessage().indexOf(":")+1,value.getMessage().length()-1);
						}
					}
					this.log("---------------D4.3.3------------,code="+code);
					if(resultCode!=0&&resultCode!=406){


						return rs.getString("ID")+","+resultCode+","+value.getMessage();
					}
					msaCodeMap.put(sheet,code);
				}else{


					value=sr.modifyFreight("Poc#1001", code, sheet, layer, payer, goods, cargo, weight, rebate, transit, freightXml);
					resultCode=value.getFailure();
					this.log("\n flag:"+flag+";	gkey:"+gkey+";    message:"+value.getMessage()+";   faliure:"+resultCode);
					if(resultCode!=0){


						return rs.getString("ID")+","+resultCode+","+value.getMessage();
					}
				}
			}
		}catch(Exception e){


			this.log("----doMSAFreight---exception:"+e.getMessage());
			e.printStackTrace();
		}finally{


			if(rs!=null&&!rs.isClosed()){


				rs.close();
			}
			if(stmt!=null&&!stmt.isClosed()){


				stmt.close();
			}
			conn.close();
		}
		String result="";
		if(msaCodeMap.size()>1){


			result=invokeN4BWS(msaCodeMap);
		}
		return result;
	}

	private String replaceContainer(ResultSet rs,String no) throws SQLException{


		return containerXml.replace("[#conta_seq_no]", no)
				.replace("[#conta_no]", rs.getString("CONTA_NO")==null?"":rs.getString("CONTA_NO"))
				.replace("[#iso_type]", rs.getString("ISO_TYPE")==null?"":rs.getString("ISO_TYPE"))
				.replace("[#conta_status]", rs.getString("CONTA_STATUS")==null?"":rs.getString("CONTA_STATUS"))
				.replace("[#seal_no]", rs.getString("SEAL_NO")==null?"":rs.getString("SEAL_NO"));
	}

	private String replaceFreight(ResultSet rs,String no,Integer unitCount,String containers) throws SQLException{


		return freightXml.replace("[#bill_seq_no]", no)
				.replace("[#bill_no]", rs.getString("sheet")==null?"":rs.getString("sheet"))
				.replace("[#load_port]", rs.getString("LOAD_PORT")==null?"":rs.getString("LOAD_PORT"))
				.replace("[#load_country]", rs.getString("LOAD_COUNTRY")==null?"":rs.getString("LOAD_COUNTRY"))
				.replace("[#discharge_port]", rs.getString("DISCHARGE_PORT")==null?"":rs.getString("DISCHARGE_PORT"))
				.replace("[#discharge_country]", rs.getString("DISCHARGE_COUNTRY")==null?"":rs.getString("DISCHARGE_COUNTRY"))
				.replace("[#bill_conta_num]", String.valueOf(unitCount))
				.replace("[#rebate]", rs.getString("rebate")==null?"":rs.getString("rebate"))
				.replace("[#transit]", rs.getString("transit")==null?"":rs.getString("transit"))
				.replace("[#CONTAINER]", containers);
	}

	private String invokeN4BWS(Map<String,String> map){


		this.log("----------invokeN4BWS-9.1----------type="+map.get("type"));
		ArgoServicePort port = getWsStub();
		this.log("----------invokeN4BWS-9.2----------port="+ToStringBuilder.reflectionToString(port));
		ScopeCoordinateIdsWsType scopeCoordinates = getScopeCoordinatesForWs();
		this.log("----------invokeN4BWS-9.3----------scopeCoordinates="+ToStringBuilder.reflectionToString(scopeCoordinates));
		String request=getReqestString(map);
		this.log("----------invokeN4BWS-9.3.1----------request="+request);
		GenericInvokeResponseWsType invokeResponseWsType = port.genericInvoke(scopeCoordinates, request);
		this.log("----------invokeN4BWS-9.4----------invokeResponseWsType="+ToStringBuilder.reflectionToString(invokeResponseWsType));
		ResponseType response = invokeResponseWsType.getCommonResponse();
		this.log("----------invokeN4BWS-9.5----------response="+ToStringBuilder.reflectionToString(response));
		if(response.getQueryResults()!=null&&response.getQueryResults().length>0){


			QueryResultType queryResultType = response.getQueryResults(0);
			this.log("----------invokeN4BWS-9.6----------queryResultType="+queryResultType);
			String responseString = queryResultType.getResult();
			this.log("----------invokeN4BWS-9.7----------responseString="+responseString);
			return responseString;
		}else{


			return "invokeN4BWS error,type="+map.get("type");
		}
	}

	private ArgoServicePort getWsStub() throws ServiceException {


		if(connectN4TReference==null){


			connectN4TReference=GeneralReference.findUniqueEntryById("CONNECTN4T", "config", null, null);
		}
		this.log("-----------------connectN4t="+ToStringBuilder.reflectionToString(connectN4TReference));
		ArgoServiceLocator locator = new ArgoServiceLocator();
		//DEV environment N4T
		String url=connectN4TReference.getRefValue1();
		String userId=connectN4TReference.getRefValue2();
		String password=connectN4TReference.getRefValue3();

		ArgoServicePort port = locator.getArgoServicePort(new URL(url));
		Stub stub = (Stub) port;
		stub._setProperty(Stub.USERNAME_PROPERTY, userId);
		stub._setProperty(Stub.PASSWORD_PROPERTY, password);
		return port;
	}

	private ScopeCoordinateIdsWsType getScopeCoordinatesForWs() {


		//build the scope coordinates for the web service based on the user context;
		ScopeCoordinateIdsWsType scopeCoordinates = new ScopeCoordinateIdsWsType();
		UserContext uContext = ContextHelper.getThreadUserContext();
		scopeCoordinates.setOperatorId(ContextHelper.getThreadOperator() != null ? ContextHelper.getThreadOperator().getId() : "MTL");
		scopeCoordinates.setComplexId(ContextHelper.getThreadComplex() != null ? ContextHelper.getThreadComplex().getCpxId() : "DCBI");
		scopeCoordinates.setFacilityId(ContextHelper.getThreadFacility() != null ? ContextHelper.getThreadFacility().getFcyId() : "DCB1I");
		scopeCoordinates.setYardId(ContextHelper.getThreadYard() != null ? ContextHelper.getThreadYard().getYrdId() : "DCB1I");
		return scopeCoordinates;
	}


	private String getReqestString(Map<String,String> map){


		Iterator<String> it = map.keySet().iterator();
		StringBuilder param=new StringBuilder();
		while(it.hasNext()){


			String key=it.next();
			param.append("<parameter id=\""+key+"\" value=\""+map.get(key)+"\"/>\n");
		}
		String requestString = String.format(UPDATE_PAYEE_REQUEST,param.toString());
		this.log("----------requestString TO N4T----------=" + requestString);
		return requestString;
	}

	public String handleCancelInvoice(Element eRequest){


		String invoiceNbr=(String)eRequest.getChildText("invoice_number");
		IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
		Invoice invoice = Invoice.findInvoiceByDraftNbr(invoiceNbr);
		if (invoice == null){


			return getXmlAcknowledge("ACCEPTED", "0001",null, null,REQUEST_TYPE_CANCEL_INVOICE);
		}
		if(!invoice.isFinalized()){


			invoiceManager.delete(invoice);
		}
		return getXmlAcknowledge("ACCEPTED", "0001",null, null,REQUEST_TYPE_CANCEL_INVOICE);
	}

	/**
	 * Creates xml to be returned in case of errors
	 * @param inErrorCode error code
	 * @param inErrorMessage error message
	 * @return
	 */
	private String getXmlAcknowledge(String status, String requestId, String rejectReasonCode, String rejectReason,String type){


		StringBuffer result=new StringBuffer("<invoice_status><status>"+status+"</status><request_id>"+requestId+"</request_id><request_type>"+type+"</request_type>");
		if(!StringUtils.isEmpty(rejectReasonCode)){


			result.append(" <reject_reason_code>"+rejectReasonCode+"</reject_reason_code><reject_reason>"+rejectReason+"</reject_reason>");
		}
		result.append("</invoice_status>");
		return result.toString();
	}

	/**
	 *
	 * @param shippingOrder
	 * @param billOfLading
	 * @param containerNbr
	 * @return
	 * @throws SQLException
	 */
	private Set<String> getContainerSet(String shippingOrder,String billOfLading,String containerNbr,Map<String,String> cnoAndKey) throws Exception{


		Set<String> containerSet=new HashSet<String>();
		this.log("-----------10.1--------CNO="+containerNbr+",set="+containerSet.toArray());
		Connection conn=JdbcUtils.getConnection();
		if(!StringUtils.isEmpty(containerNbr)){


			String[] containerArray=containerNbr.split(";");
			if(containerArray.length>0){


				containerSet.addAll(Arrays.asList(containerArray));
			}
			getGkeyById(conn,containerArray,cnoAndKey);
		}

		this.log("-----------10.2--------billOfLading="+billOfLading+",set="+containerSet.toArray());
		try{


			if(!StringUtils.isEmpty(billOfLading)){


				String[] billOfLadings=billOfLading.split(";");
				if(billOfLadings.length>0){


					findUnitNbrBySql(conn,BOL_FIND_UNITNBR_SQL,billOfLadings,containerSet,cnoAndKey);
				}
			}
		}catch(Exception e){


			e.printStackTrace();
			this.log("----getContainerSet---exception:"+e.getMessage());
		}finally{


			conn.close();
		}
		this.log("-----------10.3--------set="+containerSet.toArray());
		return containerSet;
	}

	private void getGkeyById(Connection conn,String[] array,Map<String,String> map){


		StringBuffer sql= new StringBuffer(getConGkeyByConIdSql);
		for(int i=0;i<array.length;i++){


			if(i!=0){


				sql.append(",");
			}
			sql.append("'"+array[i]+"'");
		}
		sql.append(") ORDER BY IU.GKEY");
		this.log("-----------13.1--------sql="+sql);
		PreparedStatement stmt=conn.prepareStatement(sql.toString());
		ResultSet rs=stmt.executeQuery();
		try{


			while(rs.next()){


				String id=rs.getString("ID");
				String gkey= rs.getString("GKEY");
				if(!map.containsKey(id)){


					map.put(id,gkey);
				}else if(Long.valueOf(map.get(id))<Long.valueOf(gkey)){


					map.put(id,gkey);
				}
			}
		}catch(Exception e){


			this.log("----getGkeyById----,error="+e.getMessage());
			e.printStackTrace();
		}finally{


			if(rs!=null&&!rs.isClosed()){


				rs.close();
			}
			if(stmt!=null&&!stmt.isClosed()){


				stmt.close();
			}
		}
	}

	/**
	 *
	 * @param chargeTypes
	 * @return
	 */
	private ArrayList<String> getEventTypeList(String chargeTypes){


		ArrayList<String> eventTypeList=new ArrayList<String>();
		if(!StringUtils.isEmpty(chargeTypes)){


			eventTypeList.addAll(Arrays.asList(chargeTypes.split(";")));
		}

		return eventTypeList;
	}

	/**
	 *
	 * @param containerSet
	 * @param eventTypeList
	 * @param paidThurDate
	 * @param InvoiceTypeId
	 * @param lineOperator
	 * @return
	 */
	private Invoice generateInvoice(Set<String> containerSet,List<String> eventTypeList,
									Date paidThurDate,String invoiceTypeId,String tariffType,String vesselVoy,Map<String,String> cnoAndGkey){


		this.log("----invoictTypeId:-------"+invoiceTypeId+",date="+paidThurDate+",tariffType="+tariffType);
		InvoiceType invoiceType=InvoiceType.findInvoiceType(invoiceTypeId);
		//this.log("----invoictType:-------"+ToStringBuilder.reflectionToString(invoiceType));
		Customer customer = Customer.findCustomer(tariffType, null);
		this.log("----customer:-------"+ToStringBuilder.reflectionToString(customer));
		Currency cc = Currency.findCurrency("RMB");
		Invoice invoice=Invoice.createInvoice(new Date(),invoiceType,customer,customer,cc);
		this.log("----invoice:-------"+ToStringBuilder.reflectionToString(invoice));
		invoice.setInvoiceFlexString01("Y");

		Map<String,Object> parmValues =new HashMap<String,Object>();
		List<ValueObject> parmVaos = new ArrayList();
		ValueHolder filterVao = invoiceType.getInvoiceTypeFilterVao();
		SavedPredicate sp = invoiceType.getInvtypeFilter();
		sp.getPredicateParameterFieldVaos(parmVaos);
		for (ValueObject vao : parmVaos) {


			Object internalName = vao.getFieldValue(FrameworkBizMetafield.PREDICATE_PARM_INTERNAL_NAME);
			Object metafieldId = vao.getFieldValue(FrameworkBizMetafield.PREDICATE_PARM_METAFIELD_ID);
			this.log("---------8.8.1-----metafieldId="+metafieldId+",internalName="+internalName);
			//BEXU_EQ_ID => Container number, Chassis number etc.
			if (metafieldId.equals(ArgoExtractField.BEXU_EQ_ID)){


				if(containerSet==null){


					containerSet=new HashSet<String>();
				}
				this.log("---------8.8.2-----"+containerSet.toArray());
				parmValues.put(String.valueOf(internalName), containerSet.toArray());
			}

			if (metafieldId.equals(ArgoExtractField.BEXU_GKEY)){


				this.log("---------8.8.3-----"+cnoAndGkey.values().toArray());
				parmValues.put(String.valueOf(internalName), cnoAndGkey.values().toArray());
			}

			if (metafieldId.equals(ArgoExtractField.BEXU_EVENT_TYPE)||metafieldId.equals(ArgoExtractField.BEXM_EVENT_TYPE_ID)){


				if(eventTypeList==null){


					eventTypeList=new ArrayList<String>();
				}
				parmValues.put(String.valueOf(internalName), eventTypeList.toArray());
			}
			//BEXU_PAID_THRU_DAY => Day thru which demurrage charges have been paid
			if (metafieldId.equals(ArgoExtractField.BEXU_PAID_THRU_DAY)){


				parmValues.put(String.valueOf(internalName), paidThurDate);
			}

			if(metafieldId.equals(ArgoExtractField.BEXM_VV_ID)){


				parmValues.put(String.valueOf(internalName), vesselVoy);
			}
		}

		IInvoiceManager invoiceManager = (IInvoiceManager)Roastery.getBean("invoiceManager");
		this.log("----invoiceManager:-------"+ToStringBuilder.reflectionToString(invoiceManager));
		Iterator<String> it=parmValues.keySet().iterator();
		while(it.hasNext()){


			String key=it.next();
			this.log("---------parmValues.key="+key+",value="+parmValues.get(key));
		}
		try{


			invoiceManager.generate(invoice, parmValues,true);
		}catch(Exception e){


			this.log("generate error,exception="+e.getMessage());
			e.printStackTrace();
			return null;
		}

		return invoice;
	}


	private String createDraftXmlByInvoice(DraftInvoice inv){


		def strXml = new StringWriter();

		def xml = new groovy.xml.MarkupBuilder(strXml)

		xml."invoice_status"(){


			"status"(inv.getRequestStatus())
			"request_type"("DRAFT")
			"request_id"(inv.getPrevRequestId())
			"reject_reason_code"(inv.getRejectReasonCode())
			"reject_reason"(inv.getRejectReason())
			"invoice_number"(inv.getInvoiceNumber())
			"bill_of_lading_feeder_in"(inv.getBillOfLadingFeederIn())
			"total_amount_currency"(inv.getTotalAmountCurrency())
			"total_amount"(inv.getTotalAmount())
			Set<DraftInvoiceItem> invitem=inv.getInvoiceItemSet();

			Iterator<DraftInvoiceItem> iterator=invitem.iterator();
			if(!iterator.hasNext()){


				"invoice_item"("");
			}
			while (iterator.hasNext()) {


				DraftInvoiceItem dItem = (DraftInvoiceItem) iterator.next();
				"invoice_item"(){


					"invoice_item_id"(dItem.getId())//getInvoiceInterfaceId()
					"invoice_item_vessel_code"(dItem.getVesselCode())
					"invoice_item_customs_voyage"(dItem.getCustomVoyage())
					"invoice_item_vessel_atb"(dItem.getVesselAtb())
					"invoice_item_vessel_atd"(dItem.getVesselAtd())
					"invoice_item_shipping_order"(dItem.getShippingOrder())
					"invoice_item_bill_of_lading"(dItem.getBillOfLading())
					"invoice_item_msa_number"(dItem.getmSANumber())
					"invoice_item_cut_off_date_time"(dItem.getCutOffDateTime())
					"invoice_item_operator"(dItem.getOperator())
					"invoice_item_equipment_iso_type"(dItem.getEquipmentISOType())
					"invoice_item_equipment_size"(dItem.getEquipmentSize())
					"invoice_item_equipment_type"(dItem.getEquipmentType())
					"invoice_item_equipment_height"(dItem.getEquipmentHeight())
					"invoice_item_category"(dItem.getCategory())
					"invoice_item_freight"(dItem.getFreight())
					"invoice_item_reefer"(dItem.getReefer())
					"invoice_item_IMDG"(dItem.getiMDG())
					"invoice_item_OL_B"(dItem.getoLB())
					"invoice_item_OL_F"(dItem.getoLF())
					"invoice_item_OW_L"(dItem.getoWL())
					"invoice_item_OW_R"(dItem.getoWR())
					"invoice_item_OH"(dItem.getoH())
					"invoice_item_container_number"(dItem.getContainerNumber())
					"invoice_item_charge_type"(dItem.getChargeType())
					"invoice_item_charge_description"(dItem.getChargeDescription())
					"invoice_item_tariff_id"(dItem.getTariffId())
					"invoice_item_start_date"(dItem.getStartDate())
					"invoice_item_end_date"(dItem.getEndDate())
					"invoice_item_inspection_level"(dItem.getInspectionLevel())
					"invoice_item_quantity"(dItem.getQuantity())
					"invoice_item_unit"(dItem.getUnit())
					"invoice_item_rate"(dItem.getRate())
					"invoice_item_discount_rate"(dItem.getDiscountRate())
					"invoice_item_tariff_currency"(dItem.getTariffCurrency())
					"invoice_item_tariff_amount"(dItem.getTariffAmount())
					"invoice_item_vat"(dItem.getVat())
					"invoice_item_monthly_settle"(dItem.getMonthlySettle())
					"invoice_item_bill_shipping_line"(dItem.getBillShippingLine())
					"invoice_item_consignee"(dItem.getConsignee())
					"invoice_item_payee"(dItem.getItemPayee())
					"invoice_item_paid_by"(inv.getPayee()) //getInvoicePaidInFull()
					"invoice_item_status"(dItem.getStatus())//getInvoiceStatus
					"invoice_item_reject_reason_code"(dItem.getRejectReasonCode())
					"invoice_item_reject_reason"(dItem.getRejectReason())
					"invoice_item_time_in"(dItem.getTimeIn())
					"invoice_item_time_out"(dItem.getTimeOut())
					"invoice_item_gi_time"(dItem.getGiTime())
					"invoice_item_tx_date_time"(dItem.getTxDateTime())
					"invoice_item_container_key"(dItem.getContainerKey())
				}
			}
			Set<RefBill> refBills = inv.getRefBillSet();
			if(refBills!=null&&refBills.size()>0){


				Iterator<RefBill> rbItat=refBills.iterator();
				while(rbItat.hasNext()){


					RefBill refBill = rbItat.next();
					"container_ref_bill"(){


						"container_key"(refBill.getContainerKey())
						"container_ref_bill_of_lading"(refBill.getRefBillOfLading())
						"container_ref_msa_number"(refBill.getRefMSANumber())
					}
				}
			}
		}
		return strXml;
	}


	private void findUnitNbrBySql(Connection conn,String sql,String[] values,Set<String> containerSet,Map<String,String> cnoAndKey) throws SQLException{


		StringBuffer resultSql=new StringBuffer(sql);
		for(int i=0;i<values.length;i++){


			if(i!=0){


				resultSql.append(",");
			}
			resultSql.append("?");
		}
		resultSql.append(")");
		this.log("-----------10.2.1--------sql="+resultSql.toString());
		PreparedStatement stmt=conn.prepareStatement(resultSql.toString());
		int count=1;
		for(String value:values){


			stmt.setString(count, value);
			count++;
		}
		ResultSet rs=stmt.executeQuery();
		try{


			while(rs.next()){


				containerSet.add(rs.getString("unitNbr"));
				cnoAndKey.put(rs.getString("unitNbr"),rs.getString("gkey"));
			}
		}catch(Exception e){


			e.printStackTrace();
			this.log("----findUnitNbrBySql----error:"+e.getMessage());
		}finally{


			if(rs!=null&&!rs.isClosed()){


				rs.close();
			}
			if(stmt!=null&&!stmt.isClosed()){


				stmt.close();
			}
		}
	}

	private String getInvoiceType(String chargeTypes,String vesselVoy,String userGroup){


		String invoiceType="";
		if(StringUtils.isEmpty(vesselVoy)){


			if(StringUtils.isEmpty(chargeTypes)){


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CUE_ALL";
				}else{


					invoiceType="DCB_CASHIER_CUE_ALL";
				}
			}else{


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CUE";
				}else{


					invoiceType="DCB_CASHIER_CUE";
				}
			}
		}else{


			if(StringUtils.isEmpty(chargeTypes)){


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CME_ALL";
				}else{


					invoiceType="DCB_CASHIER_CME_ALL";
				}
			}else{


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CME";
				}else{


					invoiceType="DCB_CASHIER_CME";
				}
			}
		}
		return invoiceType;
	}

	private String getInvoiceTypeGkey(String chargeTypes,String vesselVoy,String userGroup){


		String invoiceType="";
		if(StringUtils.isEmpty(vesselVoy)){


			if(StringUtils.isEmpty(chargeTypes)){


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CUE_GKEY_ALL";
				}else{


					invoiceType="DCB_CASHIER_CUE_GKEY_ALL";
				}
			}else{


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CUE_GKEY";
				}else{


					invoiceType="DCB_CASHIER_CUE_GKEY";
				}
			}
		}else{


			if(StringUtils.isEmpty(chargeTypes)){


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CME_ALL";
				}else{


					invoiceType="DCB_CASHIER_CME_ALL";
				}
			}else{


				if("Cashier".equals(userGroup)){


					invoiceType="DCB_CASHIER_OFF_CME";
				}else{


					invoiceType="DCB_CASHIER_CME";
				}
			}
		}
		return invoiceType;
	}
	public DraftInvoice processInvoice(Invoice invoice,Element eRequest,String invoiceTypeId,Map<String,String> cnoAndGkey){


		if(invoice==null){


			return null;
		}
//		SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmm");
		SimpleDateFormat oldSf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		DecimalFormat df=new DecimalFormat("#0.00");
		Connection conn = JdbcUtils.getConnection();
		PreparedStatement stmt=null;
		ResultSet rs=null;
		DraftInvoice draftInvoice=new DraftInvoice();
//		BeanUtils.copyProperties(draftInvoice,invoice);
		draftInvoice.setInvoiceNumber(String.valueOf(invoice.getInvoiceDraftNbr()));
		Currency currency = invoice.getInvoiceCurrency();
		String currencyId="";
		if(currency!=null){


			currencyId = currency.getCurrencyId();
		}
		draftInvoice.setTotalAmountCurrency(currencyId);
		draftInvoice.setRequestId(createRequestId());
		draftInvoice.setPrevRequestId(eRequest.getChildText(REQUEST_ID_PARAM));
		draftInvoice.setRequestStatus("ACCEPTED");
		draftInvoice.setBillOfLadingFeederIn(eRequest.getChildText(REQUEST_CUSTOMS_CLEARANCE_INDICATOR));
		Customer payeeCustomer=invoice.getInvoicePayeeCustomer();
		String payee="";
		if(payeeCustomer!=null){


			payee=payeeCustomer.getCustId();
		}
		draftInvoice.setPayee(payee);
		Double totalAmount = 0D;
		Set<DraftInvoiceItem> dItemSet=new HashSet<DraftInvoiceItem>();
		Set<InvoiceItem> itemSet=(Set<InvoiceItem>)invoice.getInvoiceInvoiceItems();
		String cutOffDateTime = eRequest.getChildText(REQUEST_PAID_THUR_DATE);
		if(itemSet!=null){


			Iterator<InvoiceItem> itat=itemSet.iterator();
			while(itat.hasNext()){


				InvoiceItem item=itat.next();

				DraftInvoiceItem dItem=new DraftInvoiceItem();
				dItem.setCutOffDateTime(cutOffDateTime);

				Set<InvoiceItemTax> taxes = item.getItemTaxes();
				this.log("--------11.0.1--------,size="+taxes.size()+",taxes="+ToStringBuilder.reflectionToString(taxes));
				String vat=null;
				if(taxes!=null){


					for(InvoiceItemTax tax:taxes){


						this.log("--------11.0.2--------,tax="+ToStringBuilder.reflectionToString(tax));
						this.log("--------11.0.3--------,vatRate="+tax.getItemtaxTaxRate().getTaxrateRate());
						if(true){


							Double vatRate = tax.getItemtaxTaxRate().getTaxrateRate();
							if(vatRate!=null){


								vat=vatRate*100+"%";
								break;
							}
						}
					}
				}
				if(vat!=null){


					dItem.setVat(vat);
				}
				dItem.setId(String.valueOf(item.getItemGkey()));
				dItem.setChargeType(item.getItemEventTypeId());
				dItem.setVesselCode(eRequest.getChildText(REQUEST_VESSEL_CODE));//cuEvent.bexuLineOperatorId
				dItem.setCustomVoyage(eRequest.getChildText(REQUEST_VESSEL_VOY));
				dItem.setTariffAmount(df.format(item.getItemAmount()==null?0D:item.getItemAmount()));
				if(invoiceTypeId.contains("CUE")){


					String unitId=item.getItemEventEntityId();
					Long cueGkey=item.getItemServiceExtractGkey();
					if(StringUtils.isEmpty(dItem.getContainerNumber())){


						dItem.setContainerNumber(unitId);
						dItem.setContainerKey(cnoAndGkey.get(dItem.getContainerNumber()));
					}
					stmt=conn.prepareStatement(getCUEByGkey);
					stmt.setString(1, unitId);
					stmt.setString(2, dItem.getChargeType());
					rs=stmt.executeQuery();
					this.log("-----------11.1--------unitId="+unitId);
					try{


						while(rs.next()){


							this.log("-----------11.2--------blNbr="+rs.getString("blNbr")+",obCarrierName="+rs.getString("obCarrierName")+",voyage="+rs.getString("voyage")+",eqSize="+rs.getString("eqSize")+","+rs.getString("CATEGORY")+","+rs.getString("FREIGHT_KIND")+","+rs.getString("DESCRIPTION"));
							if(StringUtils.isEmpty(dItem.getBillOfLading())){


								String blNbr=rs.getString("blNbr");
								String blNbrCode="";
								this.log("-----------11.2.0.1--------blNbr="+blNbr);
								if(!StringUtils.isEmpty(blNbr)&&blNbr.contains("+")){


									PreparedStatement blNbrStmt=conn.prepareStatement(getMultBolbyUnitGkey);
									this.log("-----------11.2.0.2--------containerKey="+dItem.getContainerKey());
									blNbrStmt.setString(1,dItem.getContainerKey());
									ResultSet blNbrRs=blNbrStmt.executeQuery();
									Long tempCode=99999999L;
									try{


										while(blNbrRs.next()){


											String blNbrRsCode=blNbrRs.getString("CODE");
											this.log("-----------11.2.0.3--------code="+blNbrRsCode);
											if(StringUtils.isEmpty(blNbrRsCode)||!blNbrRsCode.contains("-")){


												continue;
											}
											Long nowCode=Long.valueOf(blNbrRsCode.substring(blNbrRsCode.lastIndexOf("-")+1));
											this.log("-----------11.2.0.4--------tempCode="+tempCode);
											if(tempCode>nowCode){


												tempCode=nowCode;
												blNbrCode=blNbrRsCode;
												blNbr=blNbrRs.getString("NBR");
												this.log("-----------11.2.0.5--------blNbr="+blNbr);
											}
										}
									}catch(Exception e3){


										this.log("-----------11.2.0.9-------error:"+e3.getMessage());
										e3.printStackTrace();
									}finally{


										if(blNbrRs!=null&&!blNbrRs.isClosed()){


											blNbrRs.close();
										}
										if(blNbrStmt!=null&&!blNbrStmt.isClosed()){


											blNbrStmt.close();
										}
									}
								}
								dItem.setBillOfLading(blNbr);
								//POC
								this.log("-----------11.2.1--------ChargeType="+dItem.getChargeType());
								if("UNIT_POC".equals(dItem.getChargeType())){


									if(StringUtils.isEmpty()){


										blNbrCode=getMSANumberByBlNbr(dItem.getBillOfLading());
									}
									if(StringUtils.isEmpty(dItem.getmSANumber())){


										dItem.setmSANumber(blNbrCode);
									}
									this.log("-----------11.2.2--------msaNumber="+dItem.getmSANumber());
									if(!StringUtils.isEmpty(dItem.getmSANumber())){


										Double msaAmount=getMSAItemAmount(dItem.getmSANumber(),unitId);
										this.log("-----------11.2.3--------msaAmount="+msaAmount+",itemAmount="+item.getItemAmount());
										if(msaAmount>=0.00D){


											if(msaAmount!=item.getItemAmount()){


												this.log("-----------11.2.4.1--------item.Amount="+item.getItemAmount());
												item.setItemAmount(msaAmount);
												this.log("-----------11.2.4.2--------item.Amount="+item.getItemAmount());
												dItem.setTariffAmount(df.format(msaAmount));
												this.log("-----------11.2.4.3--------TariffAmount="+dItem.getTariffAmount());
											}
										}else{


											dItem.setTariffAmount(0D);
										}
									}
								}
							}
							if(StringUtils.isEmpty(dItem.getVesselCode())){


								dItem.setVesselCode(rs.getString("obCarrierName"));//cuEvent.bexuLineOperatorId
							}
							if(StringUtils.isEmpty(dItem.getCustomVoyage())){


								dItem.setCustomVoyage(rs.getString("voyage"));
							}
							if(StringUtils.isEmpty(dItem.getEquipmentISOType())){


								dItem.setEquipmentISOType(rs.getString("isoCode"));
							}
							if(StringUtils.isEmpty(dItem.getEquipmentSize())){


								dItem.setEquipmentSize(rs.getString("eqSize"));
							}
							if(StringUtils.isEmpty(dItem.getEquipmentType())){


								dItem.setEquipmentType(rs.getString("eqType"));
							}
							if(StringUtils.isEmpty(dItem.getEquipmentHeight())){


								dItem.setEquipmentHeight(rs.getString("eqHeight"));
							}
							if(StringUtils.isEmpty(dItem.getCategory())){


								dItem.setCategory(rs.getString("CATEGORY"));
							}
							if(StringUtils.isEmpty(dItem.getFreight())){


								dItem.setFreight(rs.getString("FREIGHT_KIND"));
							}
							if(StringUtils.isEmpty(dItem.getiMDG())){


								dItem.setiMDG(rs.getString("IMDG_CLASS"));
							}
							if(StringUtils.isEmpty(dItem.getTimeIn())){


								String timeIn=rs.getString("timeIn");
								if(timeIn!=null&&timeIn.length()>19){


									timeIn=timeIn.substring(0,19);
								}
								this.log("-------------oldSf="+ToStringBuilder.reflectionToString(oldSf)+",timeIn="+timeIn);
								if(timeIn!=null){


									dItem.setTimeIn(sf.format(oldSf.parse(timeIn)));
								}
							}
							if(StringUtils.isEmpty(dItem.getTimeOut())){


								String timeOut=rs.getString("timeOut");
								if(timeOut!=null&&timeOut.length()>19){


									timeOut=timeOut.substring(0,19);
								}
								if(timeOut!=null){


									dItem.setTimeOut(sf.format(oldSf.parse(timeOut)));
								}
							}
							if(StringUtils.isEmpty(dItem.getChargeDescription())){


								dItem.setChargeDescription(rs.getString("DESCRIPTION"));
							}
							this.log("-----------11.2.5--------consignee="+dItem.getConsignee());
							if(StringUtils.isEmpty(dItem.getConsignee())){


								dItem.setConsignee(rs.getString("consigneeId"));
								this.log("-----------11.2.5.1--------consignee="+dItem.getConsignee());
							}
							this.log("-----------11.2.6--------MonthlySettle="+dItem.getMonthlySettle());
							if(StringUtils.isEmpty(dItem.getItemPayee())){


								dItem.setItemPayee(rs.getString("payeeId"));
								this.log("-----------11.2.6.1--------payeeId="+dItem.getItemPayee());
							}
							if(StringUtils.isEmpty(rs.getString("indicator"))){


								dItem.setMonthlySettle("N");
								dItem.setBillShippingLine("N");
								if(!StringUtils.isEmpty(dItem.getItemPayee())&&dItem.getItemPayee().equals(dItem.getConsignee())) {


									dItem.setMonthlySettle("Y");
								}else{


									if(!StringUtils.isEmpty(dItem.getItemPayee())&&dItem.getItemPayee().equals(dItem.getOperator())){


										dItem.setBillShippingLine("Y");
									}
								}
							}
							this.log("-----------11.2.6.3--------MonthlySettle="+dItem.getMonthlySettle());
							this.log("-----------11.2.6.4--------BillShippingLine="+dItem.getBillShippingLine());

						}
						stmt=conn.prepareStatement(getUnitByNbr);
						stmt.setString(1, unitId);
						rs=stmt.executeQuery();
						this.log("-----------11.3--------unitId="+unitId);
						while(rs.next()){


							this.log("-----------11.4--------unit="+rs.getString("giTime")+","+rs.getString("op")+","+rs.getString("olb"));
							if(StringUtils.isEmpty(dItem.getoLB())){


								dItem.setoLB(rs.getString("olb"));
							}
							if(StringUtils.isEmpty(dItem.getoLF())){


								dItem.setoLF(rs.getString("olf"));
							}
							if(StringUtils.isEmpty(dItem.getoWL())){


								dItem.setoWL(rs.getString("owl"));
							}
							if(StringUtils.isEmpty(dItem.getoWR())){


								dItem.setoWR(rs.getString("owr"));
							}
							if(StringUtils.isEmpty(dItem.getoH())){


								dItem.setoH(rs.getString("oh"));
							}
							if(StringUtils.isEmpty(dItem.getInspectionLevel())){


								dItem.setInspectionLevel(rs.getString("spectionLevel"));
							}
							if(StringUtils.isEmpty(dItem.getReefer())){


								dItem.setReefer(rs.getString("reefer"));
							}
							if(StringUtils.isEmpty(dItem.getGiTime())){


								String giTime=rs.getString("giTime");
								if(giTime!=null&&giTime.length()>19){


									giTime=giTime.substring(0,19);
								}
								if(giTime!=null){


									dItem.setGiTime(sf.format(oldSf.parse(giTime)));
								}
							}
							if(StringUtils.isEmpty(dItem.getOperator())){


								dItem.setOperator(rs.getString("op"));
							}
						}
						this.log("-----------11.5--------voyage="+dItem.getCustomVoyage());
						stmt=conn.prepareStatement(getAtbAndAtd);
						stmt.setString(1, dItem.getCustomVoyage());
						rs=stmt.executeQuery();
						this.log("-----------11.6--------rs="+ToStringBuilder.reflectionToString(rs));
						while(rs.next()){


							this.log("-----------11.7--------ata/atd="+rs.getString("ATA")+","+rs.getString("ATD"));
							if(StringUtils.isEmpty(dItem.getVesselAtb())){


								String atb=rs.getString("ATA");//ata in UI is atb ,it`s true.
								this.log("-----------11.7.1--------atb="+atb);
								if(!StringUtils.isEmpty(atb)&&atb.length()>19){


									atb=atb.substring(0,19);
									dItem.setVesselAtb(sf.format(oldSf.parse(atb)));
								}
							}
							if(StringUtils.isEmpty(dItem.getVesselAtd())){


								String atd=rs.getString("ATD");
								this.log("-----------11.7.2--------atd="+atd);
								if(!StringUtils.isEmpty(atd)&&atd.length()>19){


									atd=atd.substring(0,19);
									dItem.setVesselAtd(sf.format(oldSf.parse(atd)));
								}
							}
						}
					}catch(Exception e){


//						conn.close();
						this.log("----processInvoice-----exception:"+e.getMessage());
						e.printStackTrace();
					}finally{


						if(rs!=null&&!rs.isClosed()){


							rs.close();
						}
						if(stmt!=null&&!stmt.isClosed()){


							stmt.close();
						}
					}
					this.log("-----------11.8--------ata/atd="+dItem.getVesselAtb()+","+dItem.getVesselAtd());
				}
				Date startDate=item.getItemFromDate();
				this.log("-----------11.9--------startDate="+startDate);
				if(startDate!=null){


					dItem.setStartDate(sf.format(startDate));
				}
				Date endDate=item.getItemToDate();
				this.log("-----------11.10--------endDate="+endDate);
				if(endDate!=null){


					dItem.setEndDate(sf.format(endDate));
				}
				dItem.setQuantity(String.valueOf(item.getItemQuantity()));
				if("UNIT_POC".equals(dItem.getChargeType())||
						"UNIT_PSC".equals(dItem.getChargeType())||
						"UNIT_PORT_ANCILLARY_CHARGE".equals(dItem.getChargeType())||
						"UNIT_CIQ_INSPECTION_COMPLETE".equals(dItem.getChargeType())||
						"UNIT_CUST_XRAY_INSPECTION_COMPLETE".equals(dItem.getChargeType())||
						"UNIT_SHIFT_FOR_INSPECTION".equals(dItem.getChargeType())||
						"UNIT_CUST_MANUAL_INSPECTION_COMPLETE".equals(dItem.getChargeType()))
				{


					item.setItemPaidThruDay(null);
					item.setItemQuantityUnit(ServiceQuantityUnitEnum.ITEMS);
				}
				if("STORAGE".equals(dItem.getChargeType())||"REEFER".equals(dItem.getChargeType())){


					item.setItemQuantityUnit(ServiceQuantityUnitEnum.DAYS);
				}
				dItem.setUnit(item.getItemQuantityUnitValue());
				Double rate=item.getItemRateBilled()==null?0D:item.getItemRateBilled();
				if("DOCKAGE".equals(dItem.getChargeType())){


					rate=rate/10;
				}
				dItem.setRate(df.format(rate));
				TariffRate tariffRate=item.getItemTariffRate();
				if(tariffRate!=null){


					this.log("-----------11.11--------tariffRate="+ToStringBuilder.reflectionToString(tariffRate));
					if(tariffRate.getRateCurrency()!=null){


						dItem.setTariffCurrency(tariffRate.getRateCurrency().getCurrencyId());
					}
					if(tariffRate.getRateTariff()!=null){


						dItem.setTariffId(tariffRate.getRateTariff().getTariffId());
					}
				}
				dItem.setTxDateTime(sf.format(new Date()));
				dItem.setStatus("ACCEPTED");
				this.log("-----------11.12--------dItem="+ToStringBuilder.reflectionToString(dItem));
				Double itemAmount= item.getItemAmount()==null?0D:item.getItemAmount();
				if(itemAmount<0.01D){


					itemAmount=0D;
				}
				totalAmount+=itemAmount;
				dItemSet.add(dItem);

			}
		}

		Set<RefBill> refBills=new HashSet<RefBill>();
		if(invoiceTypeId.contains("CUE")&&cnoAndGkey.size()>0){


			StringBuffer cnoGkeyValue=new StringBuffer();
			for(String gkey:cnoAndGkey.values()){


				if(cnoGkeyValue.length()>1){


					cnoGkeyValue.append(",");
				}
				cnoGkeyValue.append(gkey);
			}
			String blByUnitGkeySql=getBillNbrByUnitGkey.replace(":value",cnoGkeyValue.toString());
			this.log("-----------11.13--------blByUnitGkeySql="+blByUnitGkeySql);
			PreparedStatement nbrStmt=conn.prepareStatement(blByUnitGkeySql);
			ResultSet nbrRs=nbrStmt.executeQuery();
			PreparedStatement unitByNbrStmt=null;
			ResultSet unitByNbrRs=null;
			try{


				while(nbrRs.next()){


					this.log("-----------11.13.1--------blNbr="+nbrRs.getString("NBR"));
					RefBill refBill=new RefBill();
					refBill.setRefBillOfLading(nbrRs.getString("NBR"));
					refBill.setRefMSANumber(nbrRs.getString("CODE"));

					unitByNbrStmt=conn.prepareStatement(getUnitByNbrSql);
					unitByNbrStmt.setString(1,nbrRs.getString("GKEY"));
					unitByNbrRs=unitByNbrStmt.executeQuery();
					StringBuffer unitGkeys=new StringBuffer();
					while(unitByNbrRs.next()){


						if(unitGkeys.length()>1){


							unitGkeys.append(";");
						}
						unitGkeys.append(unitByNbrRs.getString("GKEY"));
					}
					refBill.setContainerKey(unitGkeys.toString());
					refBills.add(refBill);
				}
			}catch(Exception e){


				this.log("----getRefBills.error----:"+e.getMessage());
				e.printStackTrace();
			}finally{


				if(unitByNbrRs!=null&&!unitByNbrRs.isClosed()){


					unitByNbrRs.close();
				}
				if(unitByNbrStmt!=null&&!unitByNbrStmt.isClosed()){


					unitByNbrStmt.close();
				}
				if(nbrRs!=null&&!nbrRs.isClosed()){


					nbrRs.close();
				}
				if(nbrStmt!=null&&!nbrStmt.isClosed()){


					nbrStmt.close();
				}
			}
		}
		if(!conn.isClosed()){


			conn.close();
		}
		draftInvoice.setRefBillSet(refBills);
		draftInvoice.setTotalAmount(String.valueOf(totalAmount));
		draftInvoice.setInvoiceItemSet(dItemSet);
		return draftInvoice;

	}

	private Double getMSAItemAmount(String msaNumber,String unitId){


		this.log("-----------11.2.2.1--------unitId="+unitId);
		String revenue="-1.00";
		DecimalFormat df=new DecimalFormat("#0.00");
		def sr = this.getLibrary("DCBDevMSADataExWebServiceProxy");
		def value=sr.calculateFreight("Poc#1001",msaNumber);
		String content=value.getContent();
		this.log("-----------11.2.2.3--------content="+content);
		if(StringUtils.isEmpty(content)){


			return df.parse(revenue);
		}
		Document document = XmlUtil.parse(content);
		Element rootElement = document.getRootElement();
		List<Element> invoices= rootElement.getChildren("INVOICE");
		if(invoices==null||invoices.size()==0){


			return 0D;
		}
		List<Element> containers= rootElement.getChildren("CONTAINER");
		for(Element element:containers){


			String container=element.getChildText("CONTA_NO");
			if(container.equals(unitId)){


				revenue=element.getChildText("REVENUE");
				this.log("-----------11.2.2.4--------revenue="+revenue);
				return df.parse(revenue);
			}
		}
		return 0D;
	}

	private EventType getEventTypeById(String id){


		DomainQuery dq = QueryUtils.createDomainQuery("EventType");
		MetafieldId miID = (new MetafieldEntry("evnttypeId").getMetafieldId());
		MetafieldId miGK = (new MetafieldEntry("evnttypeGkey").getMetafieldId());
		dq.addDqPredicate(PredicateFactory.eq(miID,(Object) id));
		dq.addDqOrdering(Ordering.desc(miGK));
		dq.setMaxResults(1);
		HibernateApi hbrapi = Roastery.getHibernateApi();
		return (EventType) hbrapi.getUniqueEntityByDomainQuery(dq);
	}

	private ChargeableUnitEvent getCUEByGkey(Long gkey){


		DomainQuery dq = QueryUtils.createDomainQuery("ChargeableUnitEvent");
		MetafieldId miID = (new MetafieldEntry("bexuBatchId").getMetafieldId());
		MetafieldId miGK = (new MetafieldEntry("bexuGkey").getMetafieldId());
		dq.addDqPredicate(PredicateFactory.eq(miGK,(Object) gkey));
		dq.addDqOrdering(Ordering.desc(miGK));
		dq.setMaxResults(1);
		HibernateApi hbrapi = Roastery.getHibernateApi();
		return (ChargeableUnitEvent) hbrapi.getUniqueEntityByDomainQuery(dq);
	}

	private static synchronized String createRequestId(){


		Date date=new Date();
		SimpleDateFormat onlySf=new SimpleDateFormat("yyyyMMddHHmmssSS");
		return "DCB"+onlySf.format(date);
	}

	private String getMSANumberByBlNbr(String blNbr){


		String nbr=blNbr;
		if(!StringUtils.isEmpty(blNbr)&&blNbr.contains("+")){


			nbr=blNbr.replace("+","");
		}
		Connection conn=JdbcUtils.getConnection();
		PreparedStatement stmt=conn.prepareStatement(getMSANumberByBlNbrSql);
		stmt.setString(1, nbr);
		ResultSet rs=stmt.executeQuery();
		String masNbr="";
		try{


			while(rs.next()){


				masNbr=rs.getString("masNbr");
			}
		}catch(Exception e){


			this.log("----getMSANumberByBlNbr----exception:"+e.getMessage());
			e.printStackTrace();
		}finally{


			rs.close();
			stmt.close();
			conn.close();
		}
		return masNbr;
	}

	private String getVesselIdByVesselCode(String vesselCode){


		Connection conn=JdbcUtils.getConnection();
		PreparedStatement stmt=conn.prepareStatement(getVesselIdByVesselCode);
		stmt.setString(1, vesselCode);
		ResultSet rs=stmt.executeQuery();
		String vesselId="";
		try{


			while(rs.next()){


				vesselId=rs.getString("ID");
			}
		}catch(Exception e){


			this.log("----getVesselIdByVesselCode----exception:"+e.getMessage());
			e.printStackTrace();
		}finally{


			rs.close();
			stmt.close();
			conn.close();
		}
		return vesselId;
	}

}

class JdbcUtils {
	static OracleConnectionPoolDataSource dataSource;
	static GeneralReference jdbcReference=null;
	static{


		instances();
	}


	static void instances(){


		if(jdbcReference==null){


			jdbcReference=GeneralReference.findUniqueEntryById("JDBC_CONNECT_DATABAS", "config", null, null);

		}
		try {


			dataSource = new OracleConnectionPoolDataSource();
			dataSource.setURL("jdbc:oracle:thin:@(DESCRIPTION =(ADDRESS = (PROTOCOL = TCP)(HOST ="+jdbcReference.getRefValue1()+")(PORT = "+jdbcReference.getRefValue2()+"))(CONNECT_DATA =(SERVER = "+jdbcReference.getRefValue3()+")(SERVICE_NAME ="+jdbcReference.getRefValue4()+")))");
			dataSource.setUser(jdbcReference.getRefValue5());
			dataSource.setPassword(jdbcReference.getRefValue6());
		}catch(SQLException e){


			e.printStackTrace();
		}finally{


		}
	}


	public static synchronized Connection getConnection(){


		return dataSource.getConnection();
	}

}

class DraftInvoice {

	//eg:DCByyyymmddnnnnnnnn
	private String requestId;
	//eg:SZEDIyyyymmddnnnnnnnn
	private String prevRequestId;
	private String requestStatus;
	private String rejectReasonCode;
	private String rejectReason;
	private String billOfLadingFeederIn;
	private String totalAmount;
	private Set<DraftInvoiceItem> invoiceItemSet;
	private Set<RefBill> refBillSet;

	private String invoiceNumber;
	private String totalAmountCurrency;
	private String payee;

	public String getRequestId() {

		return requestId;
	}
	public void setRequestId(String requestId) {

		this.requestId = requestId;
	}
	public String getPrevRequestId() {

		return prevRequestId;
	}
	public void setPrevRequestId(String prevRequestId) {

		this.prevRequestId = prevRequestId;
	}
	public String getRequestStatus() {

		return requestStatus;
	}
	public void setRequestStatus(String requestStatus) {

		this.requestStatus = requestStatus;
	}
	public String getRejectReasonCode() {

		return rejectReasonCode;
	}
	public void setRejectReasonCode(String rejectReasonCode) {

		this.rejectReasonCode = rejectReasonCode;
	}
	public String getRejectReason() {

		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {

		this.rejectReason = rejectReason;
	}
	public String getBillOfLadingFeederIn() {

		return billOfLadingFeederIn;
	}
	public void setBillOfLadingFeederIn(String billOfLadingFeederIn) {

		this.billOfLadingFeederIn = billOfLadingFeederIn;
	}
	public String getTotalAmount() {

		return totalAmount;
	}
	public void setTotalAmount(String totalAmount) {

		this.totalAmount = totalAmount;
	}
	public Set<DraftInvoiceItem> getInvoiceItemSet() {

		return invoiceItemSet;
	}
	public void setInvoiceItemSet(Set<DraftInvoiceItem> invoiceItemSet) {

		this.invoiceItemSet = invoiceItemSet;
	}
	public String getInvoiceNumber() {

		return invoiceNumber;
	}
	public void setInvoiceNumber(String invoiceNumber) {

		this.invoiceNumber = invoiceNumber;
	}
	public String getTotalAmountCurrency() {

		return totalAmountCurrency;
	}
	public void setTotalAmountCurrency(String totalAmountCurrency) {

		this.totalAmountCurrency = totalAmountCurrency;
	}
	public String getPayee() {

		return payee;
	}
	public void setPayee(String payee) {

		this.payee = payee;
	}
	public Set<RefBill> getRefBillSet() {

		return refBillSet;
	}
	public void setRefBillSet(Set<RefBill> refBillSet) {

		this.refBillSet = refBillSet;
	}

}

class DraftInvoiceItem {
	private String id;
	private String vesselCode;
	private String customVoyage;

	private String shippingOrder;
	private String billOfLading;
	private String mSANumber;
	private String cutOffDateTime;
	private String operator;
	private String equipmentISOType;
	private String equipmentSize;
	private String equipmentType;
	private String equipmentHeight;
	private String category;
	//freightKind
	private String freight;
	//flexDouble05
	private String reefer;
	//The IMDG code of the container, separated by �;�
	private String iMDG;
	//Over length Back
	private String oLB;
	//Over length Front
	private String oLF;
	//Over width Left
	private String oWL;
	//Over width Right
	private String oWR;
	//Over height
	private String oH;
	private String containerNumber;
	private String chargeDescription;
	private String inspectionLevel;
	private String discountRate;
	private String vat;
	private String monthlySettle;
	private String billShippingLine;
	private String consignee;
	private String itemPayee;
//	private String paidBy;
	private String rejectReasonCode;
	private String rejectReason;
	private String timeIn;
	private String timeOut;
	//The gate-in date/time of the container
	private String giTime;

	private String chargeType;
	private String startDate;
	private String endDate;
	private String quantity;
	private String unit;
	private String rate;
	private String tariffCurrency;
	private String tariffAmount;
	private String status;
	private String tariffId;
	private String vesselAtb;
	private String vesselAtd;
	private String txDateTime;
	private String containerKey;

	public String getId() {

		return id;
	}
	public void setId(String id) {

		this.id = id;
	}
	public String getVesselCode() {

		return vesselCode;
	}
	public void setVesselCode(String vesselCode) {

		this.vesselCode = vesselCode;
	}
	public String getCustomVoyage() {

		return customVoyage;
	}
	public void setCustomVoyage(String customVoyage) {

		this.customVoyage = customVoyage;
	}
	public String getShippingOrder() {


		return shippingOrder;
	}
	public void setShippingOrder(String shippingOrder) {


		this.shippingOrder = shippingOrder;
	}
	public String getBillOfLading() {


		return billOfLading;
	}
	public void setBillOfLading(String billOfLading) {


		this.billOfLading = billOfLading;
	}
	public String getmSANumber() {


		return mSANumber;
	}
	public void setmSANumber(String mSANumber) {


		this.mSANumber = mSANumber;
	}
	public String getCutOffDateTime() {


		return cutOffDateTime;
	}
	public void setCutOffDateTime(String cutOffDateTime) {


		this.cutOffDateTime = cutOffDateTime;
	}
	public String getOperator() {


		return operator;
	}
	public void setOperator(String operator) {


		this.operator = operator;
	}
	public String getEquipmentISOType() {


		return equipmentISOType;
	}
	public void setEquipmentISOType(String equipmentISOType) {


		this.equipmentISOType = equipmentISOType;
	}
	public String getEquipmentSize() {


		return equipmentSize;
	}
	public void setEquipmentSize(String equipmentSize) {


		this.equipmentSize = equipmentSize;
	}
	public String getEquipmentType() {


		return equipmentType;
	}
	public void setEquipmentType(String equipmentType) {


		this.equipmentType = equipmentType;
	}
	public String getEquipmentHeight() {


		return equipmentHeight;
	}
	public void setEquipmentHeight(String equipmentHeight) {


		this.equipmentHeight = equipmentHeight;
	}
	public String getCategory() {


		return category;
	}
	public void setCategory(String category) {


		this.category = category;
	}
	public String getFreight() {


		return freight;
	}
	public void setFreight(String freight) {


		this.freight = freight;
	}
	public String getReefer() {


		return reefer;
	}
	public void setReefer(String reefer) {


		this.reefer = reefer;
	}
	public String getiMDG() {


		return iMDG;
	}
	public void setiMDG(String iMDG) {


		this.iMDG = iMDG;
	}
	public String getoLB() {


		return oLB;
	}
	public void setoLB(String oLB) {


		this.oLB = oLB;
	}
	public String getoLF() {


		return oLF;
	}
	public void setoLF(String oLF) {


		this.oLF = oLF;
	}
	public String getoWL() {


		return oWL;
	}
	public void setoWL(String oWL) {


		this.oWL = oWL;
	}
	public String getoWR() {


		return oWR;
	}
	public void setoWR(String oWR) {


		this.oWR = oWR;
	}
	public String getoH() {


		return oH;
	}
	public void setoH(String oH) {


		this.oH = oH;
	}
	public String getContainerNumber() {


		return containerNumber;
	}
	public void setContainerNumber(String containerNumber) {


		this.containerNumber = containerNumber;
	}
	public String getChargeDescription() {


		return chargeDescription;
	}
	public void setChargeDescription(String chargeDescription) {


		this.chargeDescription = chargeDescription;
	}
	public String getInspectionLevel() {


		return inspectionLevel;
	}
	public void setInspectionLevel(String inspectionLevel) {


		this.inspectionLevel = inspectionLevel;
	}
	public String getDiscountRate() {


		return discountRate;
	}
	public void setDiscountRate(String discountRate) {


		this.discountRate = discountRate;
	}
	public String getMonthlySettle() {


		return monthlySettle;
	}
	public void setMonthlySettle(String monthlySettle) {


		this.monthlySettle = monthlySettle;
	}
	public String getBillShippingLine() {


		return billShippingLine;
	}
	public void setBillShippingLine(String billShippingLine) {


		this.billShippingLine = billShippingLine;
	}
	public String getRejectReasonCode() {


		return rejectReasonCode;
	}
	public void setRejectReasonCode(String rejectReasonCode) {


		this.rejectReasonCode = rejectReasonCode;
	}
	public String getRejectReason() {


		return rejectReason;
	}
	public void setRejectReason(String rejectReason) {


		this.rejectReason = rejectReason;
	}
	public String getTimeIn() {


		return timeIn;
	}
	public void setTimeIn(String timeIn) {


		this.timeIn = timeIn;
	}
	public String getTimeOut() {


		return timeOut;
	}
	public void setTimeOut(String timeOut) {


		this.timeOut = timeOut;
	}
	public String getGiTime() {


		return giTime;
	}
	public void setGiTime(String giTime) {


		this.giTime = giTime;
	}
	public String getVat() {


		return vat;
	}
	public void setVat(String vat) {


		this.vat = vat;
	}
	public String getChargeType() {


		return chargeType;
	}
	public void setChargeType(String chargeType) {


		this.chargeType = chargeType;
	}
	public String getStartDate() {


		return startDate;
	}
	public void setStartDate(String startDate) {


		this.startDate = startDate;
	}
	public String getEndDate() {


		return endDate;
	}
	public void setEndDate(String endDate) {


		this.endDate = endDate;
	}
	public String getQuantity() {


		return quantity;
	}
	public void setQuantity(String quantity) {


		this.quantity = quantity;
	}
	public String getUnit() {


		return unit;
	}
	public void setUnit(String unit) {


		this.unit = unit;
	}
	public String getRate() {


		return rate;
	}
	public void setRate(String rate) {


		this.rate = rate;
	}
	public String getTariffCurrency() {


		return tariffCurrency;
	}
	public void setTariffCurrency(String tariffCurrency) {


		this.tariffCurrency = tariffCurrency;
	}
	public String getTariffAmount() {


		return tariffAmount;
	}
	public void setTariffAmount(String tariffAmount) {


		this.tariffAmount = tariffAmount;
	}
	public String getStatus() {


		return status;
	}
	public void setStatus(String status) {


		this.status = status;
	}
	public String getTariffId() {


		return tariffId;
	}
	public void setTariffId(String tariffId) {


		this.tariffId = tariffId;
	}
	public String getVesselAtb() {


		return vesselAtb;
	}
	public void setVesselAtb(String vesselAtb) {


		this.vesselAtb = vesselAtb;
	}
	public String getVesselAtd() {


		return vesselAtd;
	}
	public void setVesselAtd(String vesselAtd) {


		this.vesselAtd = vesselAtd;
	}
	public String getTxDateTime() {


		return txDateTime;
	}
	public void setTxDateTime(String txDateTime) {


		this.txDateTime = txDateTime;
	}
	public String getConsignee() {


		return consignee;
	}
	public void setConsignee(String consignee) {


		this.consignee = consignee;
	}
	public String getItemPayee() {


		return itemPayee;
	}
	public void setItemPayee(String itemPayee) {


		this.itemPayee = itemPayee;
	}
	public String getContainerKey() {


		return containerKey;
	}
	public void setContainerKey(String containerKey) {


		this.containerKey = containerKey;
	}

}

class RefBill{
	private String refBillOfLading;
	private String refMSANumber;
	private String containerKey;
	public String getRefBillOfLading() {


		return refBillOfLading;
	}
	public void setRefBillOfLading(String refBillOfLading) {


		this.refBillOfLading = refBillOfLading;
	}
	public String getRefMSANumber() {


		return refMSANumber;
	}
	public void setRefMSANumber(String refMSANumber) {


		this.refMSANumber = refMSANumber;
	}
	public String getContainerKey() {


		return containerKey;
	}
	public void setContainerKey(String containerKey) {


		this.containerKey = containerKey;
	}

}	