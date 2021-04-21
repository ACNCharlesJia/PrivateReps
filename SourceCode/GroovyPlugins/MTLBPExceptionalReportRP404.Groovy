package SourceCode.GroovyPlugins

import com.navis.argo.business.api.GroovyApi
import com.navis.argo.business.model.GeneralReference
import com.navis.framework.persistence.hibernate.PersistenceUtils
import org.hibernate.SQLQuery
import org.hibernate.Session
import org.hibernate.SessionFactory
import org.springframework.jdbc.support.rowset.SqlRowSet;
import com.navis.argo.business.model.report.ReportManagerPea;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;

import jxl.Workbook;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.write.Boolean;
import jxl.write.DateFormats;
import jxl.write.DateTime;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.CellView;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.format.BorderLineStyle;
import jxl.format.VerticalAlignment;

import java.awt.Color;

import java.text.SimpleDateFormat

class MTLBPExceptionalReportRP404 extends GroovyApi {
	
	private final String reportId = "RP404_Billing_Exception_Report_Inv_";
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
	
    public String execute(Map args) {
		this.log("MTLBPExceptionalReportRP404 start");
        createExcel(reportSQL);
		this.log("MTLBPExceptionalReportRP404 end");
    }

	public void createExcel(String sql) throws WriteException,IOException {
		
		String FNAME = " execute "
		String sFileSavePath = "";
        Date now = new Date()
        GeneralReference gfFileSavePath = GeneralReference.findUniqueEntryById('BP_CONFIGURATION', 'RPT_N4TEXRPTPATH')
        if (gfFileSavePath != null) {
            try {
                if (gfFileSavePath.getRefValue1() != null) {
                    sFileSavePath = gfFileSavePath.getRefValue1();
					String fileFullName = sFileSavePath + "/" + reportId + sdf.format(now) + ".xls";
                    this.log("File save path " + fileFullName);
				try{
					OutputStream os = new FileOutputStream(fileFullName);
					WritableWorkbook workbook = Workbook.createWorkbook(os);
					WritableSheet sheet = workbook.createSheet("MTL_TD404 N4 Daily Billing Exce", 0);
					sheet.mergeCells(0, 0, 12, 0);
					
					WritableFont boldNumber = new WritableFont(WritableFont.ARIAL,10,WritableFont.BOLD);
					WritableCellFormat boldNumberFormate = new WritableCellFormat(boldNumber);
					Label create = new Label(0,0,"N4 Billing Exception Report",boldNumberFormate);
					sheet.addCell(create);
					Label currentDate = new Label(0,2,"Date :");
					sheet.addCell(currentDate);
					
					Date nowTime = new Date(); 
					SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
					String cTime = timeFormat.format(nowTime);
					Label dt = new Label(1,2,cTime);
					sheet.addCell(dt);
					
					Date nowDate = new Date(); 
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
					String cDate = dateFormat.format( nowDate ); 
					
					Label from = new Label(0,4,"From :");
					sheet.addCell(from);
					Label fromDate = new Label(1,4, cDate + " 00:00:00");
					sheet.addCell(fromDate);
					Label to = new Label(2,4,"To :");
					sheet.addCell(to);
					Label toDate = new Label(3,4, cDate + " 23:59:59");
					sheet.addCell(toDate);
					
					Color color = Color.decode("#0099FF");
					workbook.setColourRGB(Colour.ORANGE, color.getRed(),color.getGreen(), color.getBlue());
					WritableFont Bwf = new WritableFont(WritableFont.ARIAL, 9,WritableFont.NO_BOLD, false);
					WritableCellFormat CBwcfF = new WritableCellFormat(Bwf);
					CBwcfF.setBorder(Border.ALL, BorderLineStyle.MEDIUM,Colour.ORANGE);
					
					WritableFont underline= new WritableFont(WritableFont.ARIAL, 9,WritableFont.NO_BOLD, false);
					WritableCellFormat greyBackground = new WritableCellFormat(underline);
					greyBackground.setBorder(Border.ALL, BorderLineStyle.MEDIUM);
					
					CellView cellView0 = new CellView();  
					cellView0.setSize(3800);
					CellView cellView = new CellView();  
					cellView.setSize(4600);
					CellView cellView1 = new CellView();  
					cellView1.setAutosize(true);
					sheet.setColumnView(0, cellView0);
					sheet.setColumnView(1, cellView);
					sheet.setColumnView(2, cellView0);
					sheet.setColumnView(3, cellView);
					sheet.setColumnView(4, cellView1);
					sheet.setColumnView(5, cellView1);
					sheet.setColumnView(6, cellView1);
					sheet.setColumnView(7, cellView1);
					sheet.setColumnView(8, cellView0);
					sheet.setColumnView(9, cellView0);
					sheet.setColumnView(10, cellView1);
					sheet.setColumnView(11, cellView1);
					sheet.setColumnView(12, cellView1);
					
					Label created = new Label(0,6,"Created",CBwcfF);
					sheet.addCell(created);
					Label facility = new Label(1,6,"Facility",CBwcfF);
					sheet.addCell(facility);
					Label contractCustomer = new Label(2,6,"Contract Customer",CBwcfF);
					sheet.addCell(contractCustomer);
					Label payeeCustomer = new Label(3,6,"Payee Customer",CBwcfF);
					sheet.addCell(payeeCustomer);
					Label draftNbr = new Label(4,6,"Draft Nbr",CBwcfF);
					sheet.addCell(draftNbr);
					Label finalNbr = new Label(5,6,"Final Nbr",CBwcfF);
					sheet.addCell(finalNbr);
					Label status = new Label(6,6,"Status",CBwcfF);
					sheet.addCell(status);
					Label interfaceId = new Label(7,6,"Interface Id",CBwcfF);
					sheet.addCell(interfaceId);
					Label invType = new Label(8,6,"Inv Type",CBwcfF);
					sheet.addCell(invType);
					Label eventId = new Label(9,6,"Event Id",CBwcfF);
					sheet.addCell(eventId);
					Label eventTypeId = new Label(10,6,"Event Type Id",CBwcfF);
					sheet.addCell(eventTypeId);
					Label severity = new Label(11,6,"Severity",CBwcfF);
					sheet.addCell(severity);
					Label message = new Label(12,6,"Message",greyBackground);
					sheet.addCell(message);
					
					String fromDay = cDate + " 00:00:00";
					String toDay = cDate + " 23:59:59";
					String from_date = "'" + fromDay + "'";
					String to_date = "'" + toDay +"'";
					String newSQL = sql
					newSQL = newSQL.replace("replace_from", from_date)
					newSQL = newSQL.replace("replace_to", to_date)
					
					SqlRowSet Rows = null;
					try{
						ReportManagerPea pea = new ReportManagerPea();
						Rows = pea.processSQLQuery(newSQL,null);
					}catch(Exception e){
						this.log("Cannot execute the SQL script provided: " + e.getMessage());
					}
					
					int i =7;
					while(Rows.next()){
						String aCreated = Rows.getString("Created");
						String aFacility = Rows.getString("Facility");
						String aContractCustomer = Rows.getString("contract_customer");
						String aPayeeCustomer = Rows.getString("payee_customer");
						String aDraftNbr = Rows.getString("Draft_Nbr");
						String aFinalNbr = Rows.getString("final_nbr");
						String aStatus = Rows.getString("status");
						String aInterfaceId = Rows.getString("interface_id");
						String aInvType = Rows.getString("inv_type");
						String aEventId = Rows.getString("event_id");
						String aEventTypeId = Rows.getString("event_type_id");
						String aSeverity = Rows.getString("severity");
						String aMessage = Rows.getString("message");
						
						Label bCreated = new Label(0,i,aCreated,greyBackground);
						sheet.addCell(bCreated);
						Label bFacility = new Label(1,i,aFacility,greyBackground);
						sheet.addCell(bFacility);
						Label bContractCustomer = new Label(2,i,aContractCustomer,greyBackground);
						sheet.addCell(bContractCustomer);
						Label bPayeeCustomer = new Label(3,i,aPayeeCustomer,greyBackground);
						sheet.addCell(bPayeeCustomer);
						Label bDraftNbr = new Label(4,i,aDraftNbr,greyBackground);
						sheet.addCell(bDraftNbr);
						Label bFinalNbr = new Label(5,i,aFinalNbr,greyBackground);
						sheet.addCell(bFinalNbr);
						Label bStatus = new Label(6,i,aStatus,greyBackground);
						sheet.addCell(bStatus);
						Label bInterfaceId = new Label(7,i,aInterfaceId,greyBackground);
						sheet.addCell(bInterfaceId);
						Label bInvType = new Label(8,i,aInvType,greyBackground);
						sheet.addCell(bInvType);
						Label bEventId = new Label(9,i,aEventId,greyBackground);
						sheet.addCell(bEventId);
						Label bEventTypeId = new Label(10,i,aEventTypeId,greyBackground);
						sheet.addCell(bEventTypeId);
						Label bSeverity = new Label(11,i,aSeverity,greyBackground);
						sheet.addCell(bSeverity);
						Label bMessage = new Label(12,i,aMessage,greyBackground);
						sheet.addCell(bMessage); 
						
						i++;
					}
					
					workbook.write();
					workbook.close();
					os.close();
				}catch(Exception e1){
					e1.getMessage();
					this.log("Can not generate the report");
				}
			}
		} catch (Exception e2) {
                log(FNAME + e2.getMessage())
            }
        }	
    }

    private String reportSQL = """
    SELECT distinct to_char(inv.Created, 'YYYY-MM-DD HH24:MI') Created,
                f.id Facility,
                cus.Id contract_customer,
                cus2.id payee_customer,
                inv.Draft_Nbr,
                inv.final_nbr,
                inv.status,
                inv.interface_id,
                it.id inv_type,
                ent.event_id,
                ent.event_type_id,
                msg.severity,
                decode(substr(msg.message,1,100), 'Exception: key=EXTRACT_FIELD_NOT_NUMERIC parms=[class com.navis.argo.business.extract.ChargeableMari', 'Please check if NRT is missing for current vessel', substr(msg.message,1,100)) message
                FROM
                navisbill.Bil_Invoices inv,
                navisbill.Bil_Customer  cus,
                navisbill.Bil_Customer  cus2,
                navisbill.Bil_Invoice_message  msg,
                navisbill.Bil_invoice_type it,
                navisbill.argo_facility f,
                navisbill.Bil_Invoice_items itm,
                (select rownum, decode(instr(message,'ChargeableUnitEvent'), 0,
                               substr(message, instr(message, 'Visit Id')+9,7),
                               substr(message, instr(message, 'Equipment Id')+13,11)) event_id,
                        decode(instr(message,'ChargeableUnitEvent'), 0,
                               substr(message, instr(message, 'EventType')+11,  instr(message, 'Vessel Id')-instr(message, 'EventType')-12),
                               substr(message, instr(message, 'Event Type Id')+14,  instr(message, 'Equipment Id')-instr(message, 'Event Type Id')-15)) event_type_id,
                        invoice_gkey
                  from navisbill.Bil_Invoice_message
                  where message like 'Event: Chargeable%'
                  ) ent
                WHERE cus.Gkey=inv.Contract_Customer_Gkey
                AND cus2.Gkey=inv.Payee_Customer_Gkey
                AND msg.Invoice_Gkey=inv.Gkey
                and inv.INVTYPE_GKEY = it.gkey
                and inv.fcy_gkey = f.gkey
                and msg.invoice_gkey = ent.invoice_gkey
                and ent.invoice_gkey = itm.invoice_gkey (+)
                and ent.event_id = itm.event_id (+)
                and ent.event_type_id = itm.event_type_id (+)
                and itm.event_id is null
                and it.id not like '%CASH%'
                and msg.severity not in ( 'INFO', 'TRACE')
                and msg.message not like '%No qualified extract record found for billing%'
                and msg.message not like '%Runtime Exception: key=errkey.invoice_no_data parms%'
                and msg.message not like '%Exception: key=errkey.no_primary_contract_defined parms%'
                and msg.message != 'Tariff Rate is not found. Event is not billed'
                and not (it.id like '%DOCK%' and msg.message like '%Runtime Exception: key=EXTRACT_FIELD_NOT_NUMERIC%')
                and inv.Created between to_date(replace_from,'yyyy-mm-dd hh24:mi:ss') and to_date(replace_to,'yyyy-mm-dd hh24:mi:ss')
                and not exists(select 1 from navisbill.Bil_Invoices inv2, navisbill.Bil_Invoice_message  msg2
                where inv.gkey = inv2.gkey
                AND msg2.Invoice_Gkey=inv2.Gkey
                and (msg2.message like '%UNIT_PSC%THRGH%' or msg2.message like '%UNIT_POC%%THRGH%'or msg2.message like '%UNIT_PORT_ANC%%THRGH%' )
                )
    union all
    SELECT distinct to_char(inv.Created, 'YYYY-MM-DD HH24:MI') Created,
                f.id Facility,
                cus.Id contract_customer,
                cus2.id payee_customer,
                inv.Draft_Nbr,
                inv.final_nbr,
                inv.status,
                inv.interface_id,
                it.id inv_type,
                null event_id,
                null event_type_id,
                msg.severity,
                decode(substr(msg.message,1,100), 'Exception: key=EXTRACT_FIELD_NOT_NUMERIC parms=[class com.navis.argo.business.extract.ChargeableMari', 'Please check if NRT is missing for current vessel', substr(msg.message,1,100)) message
                FROM
                navisbill.Bil_Invoices inv,
                navisbill.Bil_Customer  cus,
                navisbill.Bil_Customer  cus2,
                navisbill.Bil_Invoice_message  msg,
                navisbill.Bil_invoice_type it,
                navisbill.argo_facility f
                WHERE cus.Gkey=inv.Contract_Customer_Gkey
                AND cus2.Gkey=inv.Payee_Customer_Gkey
                AND msg.Invoice_Gkey=inv.Gkey
                and inv.INVTYPE_GKEY = it.gkey
                and inv.fcy_gkey = f.gkey
                and not exists (select 1
                                from (select decode(instr(message,'ChargeableUnitEvent'), 0,
                                      substr(message, instr(message, 'Visit Id')+9,7),
                                      substr(message, instr(message, 'Equipment Id')+13,11)) event_id,
                                      decode(instr(message,'ChargeableUnitEvent'), 0,
                                      substr(message, instr(message, 'EventType')+11,  instr(message, 'Vessel Id')-instr(message, 'EventType')-11),
                                      substr(message, instr(message, 'Event Type Id')+14,  instr(message, 'Equipment Id')-instr(message, 'Event Type Id')-15)) event_type_id,
                                      invoice_gkey
                                      from navisbill.Bil_Invoice_message
                                      where message like 'Event: Chargeable%'
                                     ) ent,
                                     navisbill.Bil_Invoice_items itm
                                 where ent.invoice_gkey = itm.invoice_gkey (+)
                                 and ent.event_id = itm.event_id (+)
                                 and ent.event_type_id = itm.event_type_id (+)
                                 and itm.event_id is null
                                 and msg.invoice_gkey = ent.invoice_gkey
                )
                and it.id not like '%CASH%'
                and msg.severity not in ( 'INFO', 'TRACE')
                and msg.message not like '%No qualified extract record found for billing%'
                and msg.message not like '%Runtime Exception: key=errkey.invoice_no_data parms%'
                and msg.message not like '%Exception: key=errkey.no_primary_contract_defined parms%'
                and msg.message != 'Tariff Rate is not found. Event is not billed'
                and not (it.id like '%DOCK%' and msg.message like '%Runtime Exception: key=EXTRACT_FIELD_NOT_NUMERIC%')
                and inv.Created between to_date(replace_from,'yyyy-mm-dd hh24:mi:ss') and to_date(replace_to,'yyyy-mm-dd hh24:mi:ss')
                and not exists(select 1 from navisbill.Bil_Invoices inv2, navisbill.Bil_Invoice_message  msg2
                where inv.gkey = inv2.gkey
                AND msg2.Invoice_Gkey=inv2.Gkey
                and (msg2.message like '%UNIT_PSC%THRGH%' or msg2.message like '%UNIT_POC%%THRGH%'or msg2.message like '%UNIT_PORT_ANC%%THRGH%' )
                )
    union all
    select to_char(inv.Created, 'YYYY-MM-DD HH24:MI') Created,
                f.id Facility,
                cus.Id contract_customer,
                cus2.id payee_customer,
                inv.Draft_Nbr,
                inv.final_nbr,
                inv.status,
                inv.interface_id,
                it.id inv_type,
                null entity_id,
                null event_type_id,
                null severity,
                'Invoice with charge amount > 0 cannnot be finalized' message
                FROM
                navisbill.Bil_Invoices inv,
                navisbill.Bil_Customer  cus,
                navisbill.Bil_Customer  cus2,
                navisbill.Bil_invoice_type it,
                navisbill.argo_facility f
                where cus.Gkey=inv.Contract_Customer_Gkey
                AND cus2.Gkey=inv.Payee_Customer_Gkey
                and inv.INVTYPE_GKEY = it.gkey
                and inv.fcy_gkey = f.gkey
                and it.id not like '%CASH%'
                and inv.final_nbr is null
                and exists (select 1 from navisbill.Bil_Invoice_items itm
                            where itm.invoice_gkey = inv.gkey
                            and itm.amount > 0)
                and inv.Created between to_date(replace_from,'yyyy-mm-dd hh24:mi:ss') and to_date(replace_to,'yyyy-mm-dd hh24:mi:ss')
                order by 1,2,3,4,5,10
    """
}