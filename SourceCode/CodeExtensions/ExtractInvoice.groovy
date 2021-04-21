package SourceCode.CodeExtensions

import com.navis.argo.ContextHelper
import com.navis.argo.business.api.ArgoUtils
import com.navis.argo.business.model.GeneralReference
import com.navis.argo.util.XmlUtil
import com.navis.billing.business.model.Invoice
import com.navis.edi.EdiEntity
import com.navis.edi.EdiField
import com.navis.edi.business.atoms.EdiCommunicationTypeEnum
import com.navis.edi.business.atoms.EdiStatusEnum
import com.navis.edi.business.entity.EdiMailbox
import com.navis.edi.business.entity.EdiMessageMap
import com.navis.edi.business.entity.EdiSession
import com.navis.edi.business.portal.communication.EdiFileSystem
import com.navis.edi.business.portal.communication.FtpAdaptor
import com.navis.external.edi.entity.AbstractEdiExtractInterceptor
import com.navis.framework.portal.QueryUtils
import com.navis.framework.portal.query.AggregateFunctionType
import com.navis.framework.portal.query.DomainQuery
import com.navis.framework.portal.query.PredicateFactory
import com.navis.framework.query.common.api.QueryResult
import com.navis.framework.query.common.impl.processors.DomainQueryProcessor
import com.navis.framework.util.BizFailure
import org.apache.log4j.Logger
import org.jdom.Element
import org.jdom.Namespace

import java.sql.*
import java.text.SimpleDateFormat
import java.util.Date

/*-----------------------------------------------------------------------------
 * Modified by:     Henry Cheng
 * Modified Date:   Jun 5, 2018
 * Description:     CGD180027 - Revise consolidated invoice grouping
 *
 * Modified by:     Henry Cheng
 * Modified Date:   Jun 21, 2018
 * Description:     CGD180070 - Skip updating Flex String 02 for Cashier Invoice
 */
public class ExtractInvoice extends AbstractEdiExtractInterceptor {
	private Date getBatchCreated(EdiSession edisess) {
		

		DomainQuery dqBatch = QueryUtils.createDomainQuery(EdiEntity.EDI_BATCH);
		dqBatch.addDqAggregateField(AggregateFunctionType.MAX,EdiField.EDIBATCH_CREATED);
		dqBatch.addDqPredicate(PredicateFactory.eq(EdiField.EDIBATCH_SESSION, edisess.getPrimaryKey()));
		dqBatch.addDqPredicate(PredicateFactory.eq(EdiField.EDIBATCH_STATUS, EdiStatusEnum.UNKNOWN));
		Date dtLastRun = edisess.getEdisessLastRunTimestamp();
		if (dtLastRun != null)
			dqBatch.addDqPredicate(PredicateFactory.gt(EdiField.EDIBATCH_CREATED, dtLastRun));

		DomainQueryProcessor dqp = new DomainQueryProcessor();
		QueryResult result = dqp.processQuery(dqBatch);
		if (result.getCurrentResultCount() > 0)
			return (Date) result.getValue(0,0);
		return null;
	}

	@Override
	public String afterEdiMap(Map map) {
		

		LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
		String sEDI = (String) map.get("EDI_TRANSACTION");
		EdiSession es = (EdiSession) map.get("SESSION");
		EdiMailbox mbxNONE = es.getEdisessPrimaryMailbox();
		Date dtCreated = getBatchCreated(es);
		if (dtCreated == null) {
			

			dtCreated = new Date();
		}
		splitEDIperInvoice(sEDI,mbxNONE.getEdimlbxDirectory(),dtCreated);

		EdiMailbox mbxFTP = null;
		EdiMailbox mbxFTP008 = null
		if (mbxNONE.getEdimlbxCommType().equals(EdiCommunicationTypeEnum.NONE)) {
			

			mbxFTP = EdiMailbox.find(es.getEdisessName());
			mbxFTP008 = EdiMailbox.find(es.getEdisessName() + "_008")
		}

		if ((mbxFTP != null) && (mbxFTP.getEdimlbxCommType().equals(EdiCommunicationTypeEnum.FTP))) {
			

			FtpAdaptor fa = new FtpAdaptor()
			File[] files = mbxNONE.listMailboxFiles()
			try {
				

				fa.openConnection(mbxFTP);
				fa._ftpClient.enterLocalActiveMode()
				for (File file : files) {
					

					if (file.isFile()) {
						

						String sFileName = file.getName();
						if (sFileName.matches("^INVOIC_.*\\.edi\$")
								&& !sFileName.contains("MC")
								&& !sFileName.contains("DC")){
							

							fa.sendDocument(file)
							
							EdiFileSystem.archive(sFileName, mbxFTP)
						}
					}
				}
				fa.closeConnection();
			} catch (BizFailure ex) {
				

				LOGGER.debug(ex.getStackTraceString());
			}

			if((mbxFTP008 != null) && (mbxFTP008.getEdimlbxCommType().equals(EdiCommunicationTypeEnum.FTP))){
				

				try {
					

					fa.openConnection(mbxFTP008);
					for (File file : files) {
						

						if (file.isFile()) {
							

							String sFileName = file.getName();
							if (sFileName.matches("^INVOIC_.*\\.edi\$")
									&& (sFileName.contains("MC") || sFileName.contains("DC"))){
								

								fa.sendDocument(file)
							}
							EdiFileSystem.archive(sFileName, mbxFTP008)
						}
					}
					fa.closeConnection();
				} catch (BizFailure ex) {
					

					LOGGER.debug(ex.getStackTraceString());
				}
			}
		}

		LOGGER.info(String.format("At end of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
		return sEDI;
	}

	private void splitEDIperInvoice(String sEDI, String sDir, Date dtCreated) {
		

		LOGGER.debug("splitEDIperInvoice::");
		String[] sInvoices = sEDI.split("<billingTransactions");
		String sDTM = new SimpleDateFormat("yyyyMMddHHmmss").format(dtCreated);

		LOGGER.debug("invoice split ::" + sInvoices.length);
		for (int nSeq = 1; nSeq < sInvoices.length; nSeq++) {
			

			LOGGER.debug("loop ::" + nSeq);
			LOGGER.debug("content ::" + sInvoices[nSeq].split("draftNumber=\"")[1].toString());
			String Inv = sInvoices[nSeq].split("draftNumber=\"")[1].split("\"")[0];
			LOGGER.debug("Invoice :: " + Inv);

			String acrType =  sInvoices[nSeq].split("acrType=\"")[1].split("\"")[0];
			LOGGER.debug("Acr type :: $acrType")
			if("REBILL".equals(acrType)) acrType = "RB"
			String sFileName = "INVOIC_" + Inv + "_" + sDTM + "_" + nSeq + "_$acrType" + ".edi";
			if(acrType.equals("CO")){
				

				sFileName = "INVOIC_" + Inv + "_" + sDTM + "_" + nSeq +  ".edi";
			}
			LOGGER.debug("filename ::" + sFileName);
			FileOutputStream of = new FileOutputStream(new File(sDir, sFileName));
			OutputStreamWriter os = new OutputStreamWriter(of);
			os.write(addCR("<?xml version=\"1.0\" encoding=\"UTF-8\"?>") + "<billingTransactions " + sInvoices[nSeq]);
			os.flush();
			os.close();
			of.close();
		}
	}

	private String addCR(String sLFOnly) {
		

		String[] lines = sLFOnly.split("\n");
		StringBuilder sNew = new StringBuilder();
		for (String sEach : lines) {
			

			sNew.append(sEach);
			sNew.append("\r");
			sNew.append("\n");
		}

		return sNew.toString();
	}

	@Override
	public void beforeEdiExtract(Map map) {
		

		EdiSession es = (EdiSession) map.get("SESSION");
		EdiMessageMap emm = es.getEdisessMsgMap();

		LOGGER.debug("CLEExtractInvoice - beforeEdiExtract start");
		LOGGER.debug("map > " + map);
		LOGGER.debug("emm > " + emm);

		LOGGER.debug("CLEExtractInvoice - beforeEdiExtract End");
	}

	private Connection getConnection() throws ClassNotFoundException {
		

		final String oracleDRIVER = "oracle.jdbc.driver.OracleDriver";

		String sType = "SHIPMENT_DETAIL";
		String sId1 = "EXTRACT";
		String sValue1, sValue2, sValue3, sValue4, sValue5, sValue6;
		GeneralReference gf = GeneralReference.findUniqueEntryById(sType, sId1);
		sValue1 = gf.getRefValue1();
		sValue2 = gf.getRefValue2();
		sValue3 = gf.getRefValue3();
		sValue4 = gf.getRefValue4();
		LOGGER.debug("sValue1:" + sValue1);
		LOGGER.debug("sValue2:" + sValue2);
		LOGGER.debug("sValue3:" + sValue3);
		LOGGER.debug("sValue4:" + sValue4);

		Class.forName(oracleDRIVER);

		String url = "jdbc:oracle:thin:@" + sValue1;
		LOGGER.debug("url:" + url);

		try {
			

			return DriverManager.getConnection(url, sValue3, sValue4);
		} catch (Exception e) {
			

			LOGGER.debug("Cannot find Oracle JDBC Driver= " + e.getMessage());
		}
		return null;
	}

	private void addFlexString02(Connection conn, Element xInvoice) throws SQLException {
		

		Namespace ns = xInvoice.getNamespace();
		String sType = xInvoice.getAttributeValue("type", ns);
		if (sType.startsWith("DCB_CASHIER"))
			return;

		String sCvId = null;
		List<Element> paramvalues = xInvoice.getChildren("invoiceParm", ns);
		for (Element paramvalue : paramvalues) {
			

			String sUiValue = paramvalue.getAttributeValue("UiValue",ns);
			if (sUiValue.equals("VesselID")) {
				

				sCvId = paramvalue.getAttributeValue("Value",ns);
				if ((sCvId != null) && (sCvId.isEmpty()==false))
					break;
			}
		}
		if ((sCvId == null) || sCvId.isEmpty())
			return;

		xInvoice.setAttribute("voyageId", sCvId, ns);

		if (conn == null) {
			

			return;
		}

		String sNRT = "", sDay = "", sATA = "", sATC = "";

		if (sType.equals("DCB_DOCKAGE")) {
			

			Element xCharge = xInvoice.getChild("invoiceCharge",ns);
			if (xCharge != null) {
				

				Element xCME = xCharge.getChild("chargeMarineEvent",ns);
				if (xCME != null) {
					

					sNRT = xCME.getAttributeValue("flexDouble03",ns);
					sDay = xCME.getAttributeValue("flexDouble02",ns);
				}
			}
		}

		String sCpxId = xInvoice.getAttributeValue("complexId", ns);
		Statement stVoy = null;
		ResultSet rsVoy = null;
		try {
			

			stVoy = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			String sSqlVoy = "select vvd.flex_date05, vvd.end_work" +
					" from navisusr.argo_carrier_visit cv" +
					" inner join navisusr.vsl_vessel_visit_details vvd on cv.cvcvd_gkey = vvd.vvd_gkey" +
					", navisusr.argo_complex cpx" +
					" where cv.cpx_gkey = cpx.gkey and cpx.id = '" + sCpxId + "'" +
					" and  cv.id = '" + sCvId + "'";
			LOGGER.debug("sSqlVoy = " + sSqlVoy);
			rsVoy = stVoy.executeQuery(sSqlVoy);
			if (rsVoy.next()) {
				

				Date dtATA  = rsVoy.getDate(1);
				Date dtATC  = rsVoy.getDate(2);
				if (dtATA != null) sATA = XmlUtil.format(dtATA);
				if (dtATC != null) sATC = XmlUtil.format(dtATC);
			}
		} finally {
			

			if (rsVoy != null) rsVoy.close();
			if (stVoy != null) stVoy.close();
		}

		Element xFlexFields = xInvoice.getChild("invoiceFlexFields",ns);
		if (xFlexFields == null) {
			

			xFlexFields = new Element("invoiceFlexFields",ns);
			xInvoice.addContent(xFlexFields);
		}

		if (sType.equals("DCB_DOCKAGE")) {
			

			xFlexFields.setAttribute("flexString02",
					"NRT(Ton)="+ sNRT +
							"\tDays="+ sDay +
							"\tATA="+ sATA +
							"\tATC="+ sATC, ns);
		} else
			xFlexFields.setAttribute("flexString02","ATA="+ sATA,ns);
	}

	@Override
	public Element beforeEdiMap(Map map) {
		

		LOGGER.debug("CLEExtractInvoice - beforeEdiMap start");

		Element xRoot = (Element) map.get("XML_TRANSACTION");
		LOGGER.debug("xRoot>" + xRoot);

		if (xRoot != null) {
			

			LOGGER.debug("xRoot.getChildren() > " + xRoot.getChildren());
			LOGGER.debug("xRoot.getChildren() > " + xRoot.getAttributes());
			LOGGER.debug("xRoot.getContent() > " + xRoot.getContent());
			Namespace ns = xRoot.getNamespace();
			LOGGER.debug("ns>" + ns);
			Element eInvoiceOrCredit = xRoot.getChild("invoice", ns);
			List<Element> invChargeList = null;
			boolean isInvoice = true;
			if (eInvoiceOrCredit == null) {
				

				eInvoiceOrCredit = xRoot.getChild("credit", ns);
				if (eInvoiceOrCredit == null) {
					

					return xRoot;
				} else {
					

					String sCpxId = ContextHelper.getThreadComplex().getCpxId();
					String sCpxSuffix = sCpxId.substring(sCpxId.length() -1);
					String sCreditNbr = eInvoiceOrCredit.getAttributeValue("draftNumber", ns);
					eInvoiceOrCredit.setAttribute("draftNumber", sCreditNbr + sCpxSuffix, ns);
					invChargeList = eInvoiceOrCredit.getChildren("creditItem", ns);
					if (invChargeList != null) {
						

						Element firstElement = invChargeList.get(0);
						String draftNbr = firstElement.getAttributeValue("creditItemInvoiceDraftNbr", ns);
						if (draftNbr != null) {
							

							Invoice invoice = Invoice.findInvoiceByDraftNbr(draftNbr);
							if (invoice != null) {
								

								String invType = invoice.getInvoiceInvoiceType().getInvtypeId();
								eInvoiceOrCredit.setAttribute("invoicetypeforcn", invType, ns);
								eInvoiceOrCredit.setAttribute("previousinvoice", draftNbr + sCpxSuffix, ns);
							}
						}
					}

					isInvoice = false;
					LOGGER.debug("isInvoice::" + isInvoice);
				}
			} else {
				

				invChargeList = eInvoiceOrCredit.getChildren("invoiceCharge", ns);
			}

			Connection conn = getConnection();

			if (isInvoice)
				addFlexString02(conn, eInvoiceOrCredit);

			//LOGGER.debug("eInvoice >" + XmlUtil.convertToString(eInvoiceOrCredit, true));
			LOGGER.debug("invChargeList >" + invChargeList + " ListSize > " + invChargeList.size());

			Iterator iterator = invChargeList.iterator();
			List<Element> elementToBeRemoved = new ArrayList<>();

			while (iterator.hasNext()) {
				

				Element eInvoiceCharge = (Element) iterator.next();
				Element cueElement = null;
				//Get CUE CME Gkey
				String cueCmeGkey = null;
				if (isInvoice) {
					

					cueElement = eInvoiceCharge;
					//Validate if element is FSP. If found then remove from extract list
					if (cueElement != null) {
						

						String eventTypeId = cueElement.getAttributeValue("chargeEventTypeId", ns);
						if (eventTypeId != null && ("STORAGE".equals(eventTypeId) || "REEFER".equals(eventTypeId))) {
							

							LOGGER.info("FSP Tariff Remove Validation: Event Type Id: " + eventTypeId);
							Element chargeTariffElement = cueElement.getChild("chargeTariff", ns);

							if (chargeTariffElement != null) {
								

								String tariffId = chargeTariffElement.getAttributeValue("id", ns);
								String tariffDesc = cueElement.getAttributeValue("description", ns);
								LOGGER.debug("tariffDesc ::" + tariffDesc);
								LOGGER.debug("CNY_TARIFF ::" + CNY_TARIFF);

								if (tariffId != null && tariffDesc.startsWith(CNY_TARIFF)) {
									

									LOGGER.info("FSP Tariff Remove Validation: Tariff is identified as "+CNY_TARIFF);
									elementToBeRemoved.add(eInvoiceCharge);
									continue;
								}

								if (tariffId != null && tariffDesc.startsWith(TYPHOON_TARIFF)) {
									

									LOGGER.info("FSP Tariff Remove Validation: Tariff is identified as "+TYPHOON_TARIFF);
									elementToBeRemoved.add(eInvoiceCharge);
									continue;
								}
/*
                                String quantityBilled =  cueElement.getAttributeValue("quantityBilled",ns);
                                LOGGER.debug("quantityBilled ::" + quantityBilled);
                                if ((quantityBilled == null) || (quantityBilled.equals(ZERO_QTY))) {
                                    

                                    LOGGER.info("Zero quantityBilled Remove Validation: Tariff is identified as " + tariffId );
                                    elementToBeRemoved.add(eInvoiceCharge);
                                    continue;
                                }
*/
							}
						}
						//validate if element is discount tariff, remove them if they are.
						if (eventTypeId != null) {
							

							Element chargeTariffElement = cueElement.getChild("chargeTariff", ns)
							if (chargeTariffElement != null) {
								

								String tariffId = chargeTariffElement.getAttributeValue("id", ns)
								String tariffDesc = cueElement.getAttributeValue("description", ns)
								boolean tariffIdEndWithD = tariffId != null && (tariffId.endsWith("_D") || tariffId.endsWith("_d"))
								boolean tariffDescContainsDiscount = tariffDesc != null && (tariffDesc.contains(DISCOUNT_TARIFF) || tariffDesc.contains(DISCOUNT_TARIFF.toLowerCase()))
								if (tariffIdEndWithD && tariffDescContainsDiscount) {
									

									elementToBeRemoved.add(eInvoiceCharge)
								}
							}
						}
					}
				} else {
					

					cueElement = eInvoiceCharge.getChild("creditItemInvoiceItem", ns);
					String eventTypeId = cueElement.getAttributeValue("chargeEventTypeId", ns);
					//validate if element is discount tariff, remove them if they are.
					if (eventTypeId != null) {
						

						Element chargeTariffElement = cueElement.getChild("chargeTariff", ns)
						if (chargeTariffElement != null) {
							

							String tariffId = chargeTariffElement.getAttributeValue("id", ns)
							String tariffDesc = cueElement.getAttributeValue("description", ns)
							boolean tariffIdEndWithD = tariffId != null && (tariffId.endsWith("_D") || tariffId.endsWith("_d"))
							boolean tariffDescContainsDiscount = tariffDesc != null && (tariffDesc.contains(DISCOUNT_TARIFF) || tariffDesc.contains(DISCOUNT_TARIFF.toLowerCase()))
							if (tariffIdEndWithD && tariffDescContainsDiscount) {
								

								elementToBeRemoved.add(eInvoiceCharge)
							}
						}
					}
				}
				cueCmeGkey = cueElement.getAttributeValue("extractGkey", ns);
				LOGGER.debug("gKey = " + cueCmeGkey);

				//Get CUE CME Extract Class (INV/MARINE)
				String cueCmeExtractClass = cueElement.getAttributeValue("extractClass", ns);
				LOGGER.debug("extractClass = " + cueCmeExtractClass);

				Map cueCmeMap = getCUECMEByGkey(conn, cueCmeGkey, cueCmeExtractClass);

				//CUE - DFF01
				String cueDff01Value = cueCmeMap.get(CUSTDFF_BEXU01).toString();
				LOGGER.debug("cueDff01Value = " + cueDff01Value);
				//CUE - DFF02
				String cueDff02Value = cueCmeMap.get(CUSTDFF_BEXU02).toString();
				LOGGER.debug("cueDff02Value = " + cueDff02Value);
				//CUE - DFF03
				String cueDff03Value = cueCmeMap.get(CUSTDFF_BEXU03).toString();
				LOGGER.debug("cueDff03Value = " + cueDff03Value);
				//CUE - DFF04
				String cueDff04Value = cueCmeMap.get(CUSTDFF_BEXU04).toString();
				LOGGER.debug("cueDff04Value = " + cueDff04Value);
				//CUE - DFF05
				String cueDff05Value = cueCmeMap.get(CUSTDFF_BEXU05).toString();
				LOGGER.debug("cueDff05Value = " + cueDff05Value);
				//CUE - DFF06
				String cueDff06Value = cueCmeMap.get(CUSTDFF_BEXU06).toString();
				LOGGER.debug("cueDff06Value = " + cueDff06Value);
				//CUE - DFF07
				String cueDff07Value = cueCmeMap.get(CUSTDFF_BEXU07).toString();
				LOGGER.debug("cueDff07Value = " + cueDff07Value);
				//CUE - DFF08
				String cueDff08Value = cueCmeMap.get(CUSTDFF_BEXU08).toString();
				LOGGER.debug("cueDff08Value = " + cueDff08Value);
				//CUE - DFF09
				String cueDff09Value = cueCmeMap.get(CUSTDFF_BEXU09).toString();
				LOGGER.debug("cueDff09Value = " + cueDff09Value);
				//CUE - DFF10
				String cueDff10Value = cueCmeMap.get(CUSTDFF_BEXU10).toString();
				LOGGER.debug("cueDff10Value = " + cueDff10Value);
				//CUE - DFF11
				String cueDff11Value = cueCmeMap.get(CUSTDFF_BEXU11).toString();
				LOGGER.debug("cueDff11Value = " + cueDff11Value);
				//CUE - DFF12
				String cueDff12Value = cueCmeMap.get(CUSTDFF_BEXU12).toString();
				LOGGER.debug("cueDff12Value = " + cueDff12Value);
				//CUE - DFF13
				String cueDff13Value = cueCmeMap.get(CUSTDFF_BEXU13).toString();
				LOGGER.debug("cueDff13Value = " + cueDff13Value);
				//CUE - DFF14
				String cueDff14Value = cueCmeMap.get(CUSTDFF_BEXU14).toString();
				LOGGER.debug("cueDff14Value = " + cueDff14Value);
				//CUE - DFF15
				String cueDff15Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU15).toString());
				LOGGER.debug("cueDff15Value = " + cueDff15Value);
				//CUE - DFF16
				String cueDff16Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU16).toString());
				LOGGER.debug("cueDff16Value = " + cueDff16Value);
				//CUE - DFF17
				String cueDff17Value = cueCmeMap.get(CUSTDFF_BEXU17).toString();
				LOGGER.debug("cueDff17Value = " + cueDff17Value);
				//CUE - DFF18
				String cueDff18Value = cueCmeMap.get(CUSTDFF_BEXU18).toString();
				LOGGER.debug("cueDff18Value = " + cueDff18Value);
				//CUE - DFF19
				String cueDff19Value = cueCmeMap.get(CUSTDFF_BEXU19).toString();
				LOGGER.debug("cueDff19Value = " + cueDff19Value);
				//CUE - DFF20
				String cueDff20Value = cueCmeMap.get(CUSTDFF_BEXU20).toString();
				LOGGER.debug("cueDff20Value = " + cueDff20Value);
				//CUE - DFF21
				String cueDff21Value = cueCmeMap.get(CUSTDFF_BEXU21).toString();
				LOGGER.debug("cueDff21Value = " + cueDff21Value);
				//CUE - DFF22
				String cueDff22Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU22).toString());
				LOGGER.debug("cueDff22Value = " + cueDff22Value);
				//CUE - DFF23
				String cueDff23Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU23).toString());
				LOGGER.debug("cueDff23Value = " + cueDff23Value);
				//CUE - DFF24
				String cueDff24Value = cueCmeMap.get(CUSTDFF_BEXU24).toString();
				LOGGER.debug("cueDff24Value = " + cueDff24Value);
				//CUE - DFF25
				String cueDff25Value = cueCmeMap.get(CUSTDFF_BEXU25).toString();
				LOGGER.debug("cueDff25Value = " + cueDff25Value);
				//CUE - DFF26
				String cueDff26Value = cueCmeMap.get(CUSTDFF_BEXU26).toString();
				LOGGER.debug("cueDff26Value = " + cueDff26Value);
				//CUE - DFF27
				String cueDff27Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU27).toString());
				LOGGER.debug("cueDff27Value = " + cueDff27Value);
				//CUE - DFF28
				String cueDff28Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU28).toString());
				LOGGER.debug("cueDff28Value = " + cueDff28Value);
				//CUE - DFF29
				String cueDff29Value = cueCmeMap.get(CUSTDFF_BEXU29).toString();
				LOGGER.debug("cueDff29Value = " + cueDff29Value);
				//CUE - DFF30
				String cueDff30Value = cueCmeMap.get(CUSTDFF_BEXU30).toString();
				LOGGER.debug("cueDff30Value = " + cueDff30Value);
				//CUE - DFF31
				String cueDff31Value = cueCmeMap.get(CUSTDFF_BEXU31).toString();
				LOGGER.debug("cueDff31Value = " + cueDff31Value);
				//CUE - DFF32
				String cueDff32Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU32).toString());

				LOGGER.debug("cueDff32Value = " + cueDff32Value);
				//CUE - DFF33
				String cueDff33Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU33).toString());
				LOGGER.debug("cueDff33Value = " + cueDff33Value);
				//CUE - DFF34
				String cueDff34Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU34).toString());
				LOGGER.debug("cueDff34Value = " + cueDff34Value);
				//CUE - DFF35
				String cueDff35Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU35).toString());
				LOGGER.debug("cueDff35Value = " + cueDff35Value);
				//CUE - DFF36
				String cueDff36Value = cueCmeMap.get(CUSTDFF_BEXU36).toString();
				LOGGER.debug("cueDff36Value = " + cueDff36Value);
				//CUE - DFF37
				String cueDff37Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU37).toString());
				LOGGER.debug("cueDff37Value = " + cueDff37Value);
				//CUE - DFF38
				String cueDff38Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU38).toString());
				LOGGER.debug("cueDff38Value = " + cueDff38Value);
				//CUE - DFF39
				String cueDff39Value = cueCmeMap.get(CUSTDFF_BEXU39).toString();
				LOGGER.debug("cueDff39Value = " + cueDff39Value);
				//CUE - DFF40
				String cueDff40Value = cueCmeMap.get(CUSTDFF_BEXU40).toString();
				LOGGER.debug("cueDff40Value = " + cueDff40Value);
				//CUE - DFF41
				String cueDff41Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU41).toString());
				LOGGER.debug("cueDff41Value = " + cueDff41Value);
				//CUE - DFF42
				String cueDff42Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU42).toString());
				LOGGER.debug("cueDff42Value = " + cueDff42Value);
				//CUE - DFF43
				String cueDff43Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU43).toString());
				LOGGER.debug("cueDff43Value = " + cueDff43Value);
				//CUE - DFF44
				String cueDff44Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU44).toString());
				LOGGER.debug("cueDff44Value = " + cueDff44Value);
				//CUE - DFF45
				String cueDff45Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXU45).toString());
				LOGGER.debug("cueDff45Value = " + cueDff45Value);
				//CUE - DFF46
				String cueDff46Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXU46).toString());
				LOGGER.debug("cueDff46Value = " + cueDff46Value);
				//CUE - DFF47
				String cueDff47Value = cueCmeMap.get(CUSTDFF_BEXU47).toString();
				LOGGER.debug("cueDff47Value = " + cueDff47Value);
				//CUE - DFF48
				String cueDff48Value = cueCmeMap.get(CUSTDFF_BEXU48).toString();
				LOGGER.debug("cueDff48Value = " + cueDff48Value);
				//CUE - DFF49
				String cueDff49Value = cueCmeMap.get(CUSTDFF_BEXU49).toString();
				LOGGER.debug("cueDff49Value = " + cueDff49Value);
				//CUE - DFF50
				String cueDff50Value = cueCmeMap.get(CUSTDFF_BEXU50).toString();
				LOGGER.debug("cueDff50Value = " + cueDff50Value);
				//CUE - DFF51
				String cueDff51Value = cueCmeMap.get(CUSTDFF_BEXU51).toString();
				LOGGER.debug("cueDff51Value = " + cueDff51Value);
				//CUE - DFF52
				String cueDff52Value = cueCmeMap.get(CUSTDFF_BEXU52).toString();
				LOGGER.debug("cueDff52Value = " + cueDff52Value);
				//CUE - DFF53
				String cueDff53Value = cueCmeMap.get(CUSTDFF_BEXU53).toString();
				LOGGER.debug("cueDff53Value = " + cueDff53Value);
				//CUE - DFF54
				String cueDff54Value = cueCmeMap.get(CUSTDFF_BEXU54).toString();
				LOGGER.debug("cueDff54Value = " + cueDff54Value);
				//CUE - DFF58
				String cueDff58Value = cueCmeMap.get(CUSTDFF_BEXU58).toString();
				LOGGER.debug("cueDff58Value = " + cueDff58Value);
				//CUE - DFF59
				String cueDff59Value = cueCmeMap.get(CUSTDFF_BEXU59).toString();
				LOGGER.debug("cueDff58Value = " + cueDff59Value);
				//CUE - DFF64
				String cueDff64Value = cueCmeMap.get(CUSTDFF_BEXU64).toString();
				LOGGER.debug("cueDff64Value = " + cueDff64Value);
				//CUE - DFF65
				String cueDff65Value = cueCmeMap.get(CUSTDFF_BEXU65).toString();
				LOGGER.debug("cueDff65Value = " + cueDff65Value);
				//CUE - DFF66
				String cueDff66Value = cueCmeMap.get(CUSTDFF_BEXU66).toString();
				LOGGER.debug("cueDff66Value = " + cueDff66Value);
				//CUE - DFF67
				String cueDff67Value = cueCmeMap.get(CUSTDFF_BEXU67).toString();
				LOGGER.debug("cueDff67Value = " + cueDff67Value);

				//CME - DFF01
				String cmeDff01Value = cueCmeMap.get(CUSTDFF_BEXM01).toString();
				LOGGER.debug("cmeDff01Value = " + cmeDff01Value);
				//CME - DFF02
				String cmeDff02Value = cueCmeMap.get(CUSTDFF_BEXM02).toString();
				LOGGER.debug("cmeDff02Value = " + cmeDff02Value);
				//CME - DFF03
				String cmeDff03Value = cueCmeMap.get(CUSTDFF_BEXM03).toString();
				LOGGER.debug("cmeDff03Value = " + cmeDff03Value);
				//CME - DFF04
				String cmeDff04Value = cueCmeMap.get(CUSTDFF_BEXM04).toString();
				LOGGER.debug("cmeDff04Value = " + cmeDff04Value);
				//CME - DFF05
				String cmeDff05Value = cueCmeMap.get(CUSTDFF_BEXM05).toString();
				LOGGER.debug("cmeDff05Value = " + cmeDff05Value);
				//CME - DFF06
				String cmeDff06Value = cueCmeMap.get(CUSTDFF_BEXM06).toString();
				LOGGER.debug("cmeDff06Value = " + cmeDff06Value);
				//CME - DFF07
				String cmeDff07Value = cueCmeMap.get(CUSTDFF_BEXM07).toString();
				LOGGER.debug("cmeDff07Value = " + cmeDff07Value);
				//CME - DFF08
				String cmeDff08Value = cueCmeMap.get(CUSTDFF_BEXM08).toString();
				LOGGER.debug("cmeDff08Value = " + cmeDff08Value);
				//CME - DFF09
				String cmeDff09Value = cueCmeMap.get(CUSTDFF_BEXM09).toString();
				LOGGER.debug("cmeDff09Value = " + cmeDff09Value);
				//CME - DFF10
				String cmeDff10Value = convertToDiffDateFormat(cueCmeMap.get(CUSTDFF_BEXM10).toString());
				LOGGER.debug("cmeDff10Value = " + cmeDff10Value);
				//CME - DFF11
				String cmeDff11Value = convertToDiffTimeFormat(cueCmeMap.get(CUSTDFF_BEXM11).toString());
				LOGGER.debug("cmeDff11Value = " + cmeDff11Value);
				//CME - DFF12
				String cmeDff12Value = cueCmeMap.get(CUSTDFF_BEXM12).toString();
				LOGGER.debug("cmeDff12Value = " + cmeDff12Value);
				//CME - DFF13
				String cmeDff13Value = cueCmeMap.get(CUSTDFF_BEXM13).toString();
				LOGGER.debug("cmeDff13Value = " + cmeDff13Value);
				//CME - DFF14
				String cmeDff14Value = cueCmeMap.get(CUSTDFF_BEXM14).toString();
				LOGGER.debug("cmeDff14Value = " + cmeDff14Value);

				if (cueCmeExtractClass.equals("INV")) {
					

					//Set CUE DFF values to attributes
					Element eShipment = new Element("shipmentDetailCUE", ns);
					eShipment.setAttribute("power", cueDff01Value);
					eShipment.setAttribute("grossWeight", cueDff02Value);
					eShipment.setAttribute("portOfOrigin", cueDff03Value);
					eShipment.setAttribute("IAOCRateType", cueDff04Value);
					eShipment.setAttribute("requiredTemp", cueDff05Value);
					eShipment.setAttribute("specialGear", cueDff06Value);
					eShipment.setAttribute("damage", cueDff07Value);
					eShipment.setAttribute("overheight", cueDff08Value);
					eShipment.setAttribute("overwidth", cueDff09Value);
					eShipment.setAttribute("overlength", cueDff10Value);
					eShipment.setAttribute("hazardUNCode", cueDff11Value);
					eShipment.setAttribute("eventDescription", cueDff12Value);
					eShipment.setAttribute("changedFrom", cueDff13Value);
					eShipment.setAttribute("changedTo", cueDff14Value);
					eShipment.setAttribute("dateOfDocument", cueDff15Value);
					eShipment.setAttribute("timeOfDocument", cueDff16Value);
					eShipment.setAttribute("documentReference", cueDff17Value);
					eShipment.setAttribute("lineIdPrevious", cueDff18Value);
					eShipment.setAttribute("baseStatus", cueDff19Value);
					eShipment.setAttribute("ibRadioCallsign", cueDff20Value);
					eShipment.setAttribute("obRadioCallsign", cueDff21Value);
					eShipment.setAttribute("dateOfIbATD", cueDff22Value);
					eShipment.setAttribute("timeOfIbATD", cueDff23Value);
					eShipment.setAttribute("ibVesselLineID", cueDff24Value);
					eShipment.setAttribute("ibVesselName", cueDff25Value);
					eShipment.setAttribute("ibVoyageNbr", cueDff26Value);
					eShipment.setAttribute("dateOfObATD", cueDff27Value);
					eShipment.setAttribute("timeOfObATD", cueDff28Value);
					eShipment.setAttribute("obVesselLineID", cueDff29Value);
					eShipment.setAttribute("oBVesselName", cueDff30Value);
					eShipment.setAttribute("oBVoyageNbr", cueDff31Value);
					eShipment.setAttribute("dateOfIbATB", cueDff32Value);
					eShipment.setAttribute("timeOfIbATB", cueDff33Value);
					eShipment.setAttribute("dateOfObATB", cueDff34Value);
					eShipment.setAttribute("timeOfObATB", cueDff35Value);
					eShipment.setAttribute("movement", cueDff36Value);
					eShipment.setAttribute("dateOfCutoff", cueDff37Value);
					eShipment.setAttribute("timeOfCutoff", cueDff38Value);
					eShipment.setAttribute("ibServiceCode", cueDff39Value);
					eShipment.setAttribute("obServiceCode", cueDff40Value);
					eShipment.setAttribute("dateOfCollection", cueDff41Value);
					eShipment.setAttribute("timeOfCollection", cueDff42Value);
					eShipment.setAttribute("dateOfDelivery", cueDff43Value);
					eShipment.setAttribute("timeOfDelivery", cueDff44Value);
					eShipment.setAttribute("dateOfAppointment", cueDff45Value);
					eShipment.setAttribute("timeOfAppointment", cueDff46Value);
					eShipment.setAttribute("gearBox", cueDff47Value);
					eShipment.setAttribute("extractClass", cueDff48Value);
					eShipment.setAttribute("inboundVesselLength", cueDff49Value);
					eShipment.setAttribute("outboundVesselLength", cueDff50Value);
					eShipment.setAttribute("inboundInterruptionCallReason", cueDff51Value);
					eShipment.setAttribute("outboundInterruptionCallReason", cueDff52Value);
					eShipment.setAttribute("imdgHazardFireCode", cueDff53Value);
					eShipment.setAttribute("dutiable", cueDff54Value);
					eShipment.setAttribute("invoiceVesselVisitId", cueDff58Value);
					eShipment.setAttribute("hazardFireCodeInd", cueDff59Value);
					eShipment.setAttribute("ibDclrVvOverflowInd", cueDff64Value);
					eShipment.setAttribute("ibDclrVvOriginalBerthingTerminal", cueDff65Value);
					eShipment.setAttribute("obDclrVvOverflowInd", cueDff66Value);
					eShipment.setAttribute("obDclrVvOriginalBerthingTerminal", cueDff67Value);
					//eInvoiceCharge.addContent(eShipment);
					if (isInvoice) {
						

						eInvoiceCharge.addContent(eShipment);
					} else {
						

						cueElement.addContent(eShipment);
					}
					//if (xInt != null) sInterchange = XmlUtil.convertToString(xInt,true).replaceAll("argo:", "edi:");
				} else {
					

					//Set CUE DFF values to attributes
					Element eShipment = new Element("shipmentDetailCME", ns);
					eShipment.setAttribute("eventDescription", cmeDff01Value);
					eShipment.setAttribute("serviceOrder", cmeDff02Value);
					eShipment.setAttribute("invoiceType", cmeDff03Value);
					eShipment.setAttribute("ibRadioCallSign", cmeDff04Value);
					eShipment.setAttribute("ibServiceCode", cmeDff05Value);
					eShipment.setAttribute("extractClass", cmeDff06Value);
					eShipment.setAttribute("inboundInterruptionCallReason", cmeDff07Value);
					eShipment.setAttribute("notesForServiceOrder", cmeDff08Value);
					eShipment.setAttribute("invoiceVesselVisitId", cmeDff09Value);
					eShipment.setAttribute("dateOfDocument", cmeDff10Value);
					eShipment.setAttribute("timeOfDocument", cmeDff11Value);
					eShipment.setAttribute("documentReference", cmeDff12Value);
					eShipment.setAttribute("ibDclrVvOverflowInd", cmeDff13Value);
					eShipment.setAttribute("ibDclrVvOriginalBerthingTerminal", cmeDff14Value);
					// eInvoiceCharge.addContent(eShipment);
					if (isInvoice) {
						

						eInvoiceCharge.addContent(eShipment);
					} else {
						

						cueElement.addContent(eShipment);
					}
					//if (xInt != null) sInterchange = XmlUtil.convertToString(xInt,true).replaceAll("argo:", "edi:");
				}

				LOGGER.debug("eInvoiceCharge values = " + eInvoiceCharge);
			}
			//Remove the FSP tariff if found
			LOGGER.info("Element to be removed: Size: " + elementToBeRemoved.size());
			for (Element element : elementToBeRemoved) {
				

				invChargeList.remove(element);
			}

			if (conn != null) conn.close();
		}
		return xRoot;
	}

	public String convertToDiffDateFormat(String inputDateString) {
		

		if (inputDateString == null) {
			

			return null;
		}
		String returnString = "";
		try {
			

			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-M-d");
			SimpleDateFormat format2 = new SimpleDateFormat("yyyyMMdd");
			Date inputDate = format1.parse(inputDateString);
			returnString = format2.format(inputDate);
		} catch (Exception e) {
			

			returnString = "";
		}
		return returnString;
	}

	public String convertToDiffTimeFormat(String inputTimeString) {
		

		if (inputTimeString == null) {
			

			return null;
		}
		String returnString = "";
		try {
			

			SimpleDateFormat format1 = new SimpleDateFormat("yyyy-M-d HH.mm.ss");
			SimpleDateFormat format2 = new SimpleDateFormat("HHmmss");
			Date inputDate = format1.parse(inputTimeString);
			returnString = format2.format(inputDate);
		} catch (Exception e) {
			

			returnString = "";
		}
		return returnString;
	}

	private Map getCUECMEByGkey(Connection conn, String gKey, String extractClass) throws SQLException {
		

		if (conn == null) {
			

			return null;
		}

		String sqlCueQuery = "SELECT CUSTDFF_BEXU01, CUSTDFF_BEXU02, CUSTDFF_BEXU03, CUSTDFF_BEXU04, CUSTDFF_BEXU05," +
				"CUSTDFF_BEXU06, CUSTDFF_BEXU07, CUSTDFF_BEXU08, CUSTDFF_BEXU09, CUSTDFF_BEXU10," +
				"CUSTDFF_BEXU11, CUSTDFF_BEXU12, CUSTDFF_BEXU13, CUSTDFF_BEXU14, CUSTDFF_BEXU15," +
				"CUSTDFF_BEXU16, CUSTDFF_BEXU17, CUSTDFF_BEXU18, CUSTDFF_BEXU19, CUSTDFF_BEXU20," +
				"CUSTDFF_BEXU21, CUSTDFF_BEXU22, CUSTDFF_BEXU23, CUSTDFF_BEXU24, CUSTDFF_BEXU25," +
				"CUSTDFF_BEXU26, CUSTDFF_BEXU27, CUSTDFF_BEXU28, CUSTDFF_BEXU29, CUSTDFF_BEXU30," +
				"CUSTDFF_BEXU31, CUSTDFF_BEXU32, CUSTDFF_BEXU33, CUSTDFF_BEXU34, CUSTDFF_BEXU35," +
				"CUSTDFF_BEXU36, CUSTDFF_BEXU37, CUSTDFF_BEXU38, CUSTDFF_BEXU39, CUSTDFF_BEXU40," +
				"CUSTDFF_BEXU41, CUSTDFF_BEXU42, CUSTDFF_BEXU43, CUSTDFF_BEXU44, CUSTDFF_BEXU45," +
				"CUSTDFF_BEXU46, CUSTDFF_BEXU47, CUSTDFF_BEXU48, CUSTDFF_BEXU49, CUSTDFF_BEXU50," +
				"CUSTDFF_BEXU51, CUSTDFF_BEXU52, CUSTDFF_BEXU53, CUSTDFF_BEXU54, CUSTDFF_BEXU58," +
				"CUSTDFF_BEXU59, CUSTDFF_BEXU64, CUSTDFF_BEXU65, CUSTDFF_BEXU66, CUSTDFF_BEXU67" +
				" FROM NAVISUSR.ARGO_CHARGEABLE_UNIT_EVENTS WHERE GKEY = " + gKey;

		String sqlCmeQuery = "SELECT CUSTDFF_BEXM01, CUSTDFF_BEXM02, CUSTDFF_BEXM03, CUSTDFF_BEXM04, CUSTDFF_BEXM05, " +
				"CUSTDFF_BEXM06, CUSTDFF_BEXM07, CUSTDFF_BEXM08, CUSTDFF_BEXM09, CUSTDFF_BEXM10, CUSTDFF_BEXM11, CUSTDFF_BEXM12, "+
				"CUSTDFF_BEXM13, CUSTDFF_BEXM14" +
				" FROM NAVISUSR.ARGO_CHARGEABLE_MARINE_EVENTS WHERE GKEY = " + gKey;

		Map resMap = new HashMap();

		Statement st = null;
		ResultSet rs = null;
		try {
			

			st = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//Determine Unit or Marine
			if (extractClass.equals("INV")) {
				

				rs = st.executeQuery(sqlCueQuery);
				while (rs.next()) {
					

					resMap.put(CUSTDFF_BEXU01, rs.getString(CUSTDFF_BEXU01));
					resMap.put(CUSTDFF_BEXU02, rs.getString(CUSTDFF_BEXU02));
					resMap.put(CUSTDFF_BEXU03, rs.getString(CUSTDFF_BEXU03));
					resMap.put(CUSTDFF_BEXU04, rs.getString(CUSTDFF_BEXU04));
					resMap.put(CUSTDFF_BEXU05, rs.getString(CUSTDFF_BEXU05));
					resMap.put(CUSTDFF_BEXU06, rs.getString(CUSTDFF_BEXU06));
					resMap.put(CUSTDFF_BEXU07, rs.getString(CUSTDFF_BEXU07));
					resMap.put(CUSTDFF_BEXU08, rs.getString(CUSTDFF_BEXU08));
					resMap.put(CUSTDFF_BEXU09, rs.getString(CUSTDFF_BEXU09));
					resMap.put(CUSTDFF_BEXU10, rs.getString(CUSTDFF_BEXU10));
					resMap.put(CUSTDFF_BEXU11, rs.getString(CUSTDFF_BEXU11));
					resMap.put(CUSTDFF_BEXU12, rs.getString(CUSTDFF_BEXU12));
					resMap.put(CUSTDFF_BEXU13, rs.getString(CUSTDFF_BEXU13));
					resMap.put(CUSTDFF_BEXU14, rs.getString(CUSTDFF_BEXU14));
					resMap.put(CUSTDFF_BEXU15, rs.getString(CUSTDFF_BEXU15));
					resMap.put(CUSTDFF_BEXU16, rs.getString(CUSTDFF_BEXU16));
					resMap.put(CUSTDFF_BEXU17, rs.getString(CUSTDFF_BEXU17));
					resMap.put(CUSTDFF_BEXU18, rs.getString(CUSTDFF_BEXU18));
					resMap.put(CUSTDFF_BEXU19, rs.getString(CUSTDFF_BEXU19));
					resMap.put(CUSTDFF_BEXU20, rs.getString(CUSTDFF_BEXU20));
					resMap.put(CUSTDFF_BEXU21, rs.getString(CUSTDFF_BEXU21));
					resMap.put(CUSTDFF_BEXU22, rs.getString(CUSTDFF_BEXU22));
					resMap.put(CUSTDFF_BEXU23, rs.getString(CUSTDFF_BEXU23));
					resMap.put(CUSTDFF_BEXU24, rs.getString(CUSTDFF_BEXU24));
					resMap.put(CUSTDFF_BEXU25, rs.getString(CUSTDFF_BEXU25));
					resMap.put(CUSTDFF_BEXU26, rs.getString(CUSTDFF_BEXU26));
					resMap.put(CUSTDFF_BEXU27, rs.getString(CUSTDFF_BEXU27));
					resMap.put(CUSTDFF_BEXU28, rs.getString(CUSTDFF_BEXU28));
					resMap.put(CUSTDFF_BEXU29, rs.getString(CUSTDFF_BEXU29));
					resMap.put(CUSTDFF_BEXU30, rs.getString(CUSTDFF_BEXU30));
					resMap.put(CUSTDFF_BEXU31, rs.getString(CUSTDFF_BEXU31));
					resMap.put(CUSTDFF_BEXU32, rs.getString(CUSTDFF_BEXU32));
					resMap.put(CUSTDFF_BEXU33, rs.getString(CUSTDFF_BEXU33));
					resMap.put(CUSTDFF_BEXU34, rs.getString(CUSTDFF_BEXU34));
					resMap.put(CUSTDFF_BEXU35, rs.getString(CUSTDFF_BEXU35));
					resMap.put(CUSTDFF_BEXU36, rs.getString(CUSTDFF_BEXU36));
					resMap.put(CUSTDFF_BEXU37, rs.getString(CUSTDFF_BEXU37));
					resMap.put(CUSTDFF_BEXU38, rs.getString(CUSTDFF_BEXU38));
					resMap.put(CUSTDFF_BEXU39, rs.getString(CUSTDFF_BEXU39));
					resMap.put(CUSTDFF_BEXU40, rs.getString(CUSTDFF_BEXU40));
					resMap.put(CUSTDFF_BEXU41, rs.getString(CUSTDFF_BEXU41));
					resMap.put(CUSTDFF_BEXU42, rs.getString(CUSTDFF_BEXU42));
					resMap.put(CUSTDFF_BEXU43, rs.getString(CUSTDFF_BEXU43));
					resMap.put(CUSTDFF_BEXU44, rs.getString(CUSTDFF_BEXU44));
					resMap.put(CUSTDFF_BEXU45, rs.getString(CUSTDFF_BEXU45));
					resMap.put(CUSTDFF_BEXU46, rs.getString(CUSTDFF_BEXU46));
					resMap.put(CUSTDFF_BEXU47, rs.getString(CUSTDFF_BEXU47));
					resMap.put(CUSTDFF_BEXU48, rs.getString(CUSTDFF_BEXU48));
					resMap.put(CUSTDFF_BEXU49, rs.getString(CUSTDFF_BEXU49));
					resMap.put(CUSTDFF_BEXU50, rs.getString(CUSTDFF_BEXU50));
					resMap.put(CUSTDFF_BEXU51, rs.getString(CUSTDFF_BEXU51));
					resMap.put(CUSTDFF_BEXU52, rs.getString(CUSTDFF_BEXU52));
					resMap.put(CUSTDFF_BEXU53, rs.getString(CUSTDFF_BEXU53));
					resMap.put(CUSTDFF_BEXU54, rs.getString(CUSTDFF_BEXU54));
					resMap.put(CUSTDFF_BEXU58, rs.getString(CUSTDFF_BEXU58));
					resMap.put(CUSTDFF_BEXU59, rs.getString(CUSTDFF_BEXU59));
					resMap.put(CUSTDFF_BEXU64, rs.getString(CUSTDFF_BEXU64));
					resMap.put(CUSTDFF_BEXU65, rs.getString(CUSTDFF_BEXU65));
					resMap.put(CUSTDFF_BEXU66, rs.getString(CUSTDFF_BEXU66));
					resMap.put(CUSTDFF_BEXU67, rs.getString(CUSTDFF_BEXU67));
				}
			} else {
				

				rs = st.executeQuery(sqlCmeQuery);
				while (rs.next()) {
					

					resMap.put(CUSTDFF_BEXM01, rs.getString(CUSTDFF_BEXM01));
					resMap.put(CUSTDFF_BEXM02, rs.getString(CUSTDFF_BEXM02));
					resMap.put(CUSTDFF_BEXM03, rs.getString(CUSTDFF_BEXM03));
					resMap.put(CUSTDFF_BEXM04, rs.getString(CUSTDFF_BEXM04));
					resMap.put(CUSTDFF_BEXM05, rs.getString(CUSTDFF_BEXM05));
					resMap.put(CUSTDFF_BEXM06, rs.getString(CUSTDFF_BEXM06));
					resMap.put(CUSTDFF_BEXM07, rs.getString(CUSTDFF_BEXM07));
					resMap.put(CUSTDFF_BEXM08, rs.getString(CUSTDFF_BEXM08));
					resMap.put(CUSTDFF_BEXM09, rs.getString(CUSTDFF_BEXM09));
					resMap.put(CUSTDFF_BEXM10, rs.getString(CUSTDFF_BEXM10));
					resMap.put(CUSTDFF_BEXM11, rs.getString(CUSTDFF_BEXM11));
					resMap.put(CUSTDFF_BEXM12, rs.getString(CUSTDFF_BEXM12));
					resMap.put(CUSTDFF_BEXM13, rs.getString(CUSTDFF_BEXM13));
					resMap.put(CUSTDFF_BEXM14, rs.getString(CUSTDFF_BEXM14));
				}
			}
		} finally {
			

			if (rs != null) rs.close();
			if (st != null) st.close();
		}
		return resMap;
	}
	// constants - CUE fields name
	private final String CUSTDFF_BEXU01 = "CUSTDFF_BEXU01";
	private final String CUSTDFF_BEXU02 = "CUSTDFF_BEXU02";
	private final String CUSTDFF_BEXU03 = "CUSTDFF_BEXU03";
	private final String CUSTDFF_BEXU04 = "CUSTDFF_BEXU04";
	private final String CUSTDFF_BEXU05 = "CUSTDFF_BEXU05";
	private final String CUSTDFF_BEXU06 = "CUSTDFF_BEXU06";
	private final String CUSTDFF_BEXU07 = "CUSTDFF_BEXU07";
	private final String CUSTDFF_BEXU08 = "CUSTDFF_BEXU08";
	private final String CUSTDFF_BEXU09 = "CUSTDFF_BEXU09";
	private final String CUSTDFF_BEXU10 = "CUSTDFF_BEXU10";
	private final String CUSTDFF_BEXU11 = "CUSTDFF_BEXU11";
	private final String CUSTDFF_BEXU12 = "CUSTDFF_BEXU12";
	private final String CUSTDFF_BEXU13 = "CUSTDFF_BEXU13";
	private final String CUSTDFF_BEXU14 = "CUSTDFF_BEXU14";
	private final String CUSTDFF_BEXU15 = "CUSTDFF_BEXU15";
	private final String CUSTDFF_BEXU16 = "CUSTDFF_BEXU16";
	private final String CUSTDFF_BEXU17 = "CUSTDFF_BEXU17";
	private final String CUSTDFF_BEXU18 = "CUSTDFF_BEXU18";
	private final String CUSTDFF_BEXU19 = "CUSTDFF_BEXU19";
	private final String CUSTDFF_BEXU20 = "CUSTDFF_BEXU20";
	private final String CUSTDFF_BEXU21 = "CUSTDFF_BEXU21";
	private final String CUSTDFF_BEXU22 = "CUSTDFF_BEXU22";
	private final String CUSTDFF_BEXU23 = "CUSTDFF_BEXU23";
	private final String CUSTDFF_BEXU24 = "CUSTDFF_BEXU24";
	private final String CUSTDFF_BEXU25 = "CUSTDFF_BEXU25";
	private final String CUSTDFF_BEXU26 = "CUSTDFF_BEXU26";
	private final String CUSTDFF_BEXU27 = "CUSTDFF_BEXU27";
	private final String CUSTDFF_BEXU28 = "CUSTDFF_BEXU28";
	private final String CUSTDFF_BEXU29 = "CUSTDFF_BEXU29";
	private final String CUSTDFF_BEXU30 = "CUSTDFF_BEXU30";
	private final String CUSTDFF_BEXU31 = "CUSTDFF_BEXU31";
	private final String CUSTDFF_BEXU32 = "CUSTDFF_BEXU32";
	private final String CUSTDFF_BEXU33 = "CUSTDFF_BEXU33";
	private final String CUSTDFF_BEXU34 = "CUSTDFF_BEXU34";
	private final String CUSTDFF_BEXU35 = "CUSTDFF_BEXU35";
	private final String CUSTDFF_BEXU36 = "CUSTDFF_BEXU36";
	private final String CUSTDFF_BEXU37 = "CUSTDFF_BEXU37";
	private final String CUSTDFF_BEXU38 = "CUSTDFF_BEXU38";
	private final String CUSTDFF_BEXU39 = "CUSTDFF_BEXU39";
	private final String CUSTDFF_BEXU40 = "CUSTDFF_BEXU40";
	private final String CUSTDFF_BEXU41 = "CUSTDFF_BEXU41";
	private final String CUSTDFF_BEXU42 = "CUSTDFF_BEXU42";
	private final String CUSTDFF_BEXU43 = "CUSTDFF_BEXU43";
	private final String CUSTDFF_BEXU44 = "CUSTDFF_BEXU44";
	private final String CUSTDFF_BEXU45 = "CUSTDFF_BEXU45";
	private final String CUSTDFF_BEXU46 = "CUSTDFF_BEXU46";
	private final String CUSTDFF_BEXU47 = "CUSTDFF_BEXU47";
	private final String CUSTDFF_BEXU48 = "CUSTDFF_BEXU48";
	private final String CUSTDFF_BEXU49 = "CUSTDFF_BEXU49";
	private final String CUSTDFF_BEXU50 = "CUSTDFF_BEXU50";
	private final String CUSTDFF_BEXU51 = "CUSTDFF_BEXU51";
	private final String CUSTDFF_BEXU52 = "CUSTDFF_BEXU52";
	private final String CUSTDFF_BEXU53 = "CUSTDFF_BEXU53";
	private final String CUSTDFF_BEXU54 = "CUSTDFF_BEXU54";
	private final String CUSTDFF_BEXU58 = "CUSTDFF_BEXU58";
	private final String CUSTDFF_BEXU59 = "CUSTDFF_BEXU59";
	private final String CUSTDFF_BEXU64 = "CUSTDFF_BEXU64";
	private final String CUSTDFF_BEXU65 = "CUSTDFF_BEXU65";
	private final String CUSTDFF_BEXU66 = "CUSTDFF_BEXU66";
	private final String CUSTDFF_BEXU67 = "CUSTDFF_BEXU67";
	// constants - CME fields name
	private final String CUSTDFF_BEXM01 = "CUSTDFF_BEXM01";
	private final String CUSTDFF_BEXM02 = "CUSTDFF_BEXM02";
	private final String CUSTDFF_BEXM03 = "CUSTDFF_BEXM03";
	private final String CUSTDFF_BEXM04 = "CUSTDFF_BEXM04";
	private final String CUSTDFF_BEXM05 = "CUSTDFF_BEXM05";
	private final String CUSTDFF_BEXM06 = "CUSTDFF_BEXM06";
	private final String CUSTDFF_BEXM07 = "CUSTDFF_BEXM07";
	private final String CUSTDFF_BEXM08 = "CUSTDFF_BEXM08";
	private final String CUSTDFF_BEXM09 = "CUSTDFF_BEXM09";
	private final String CUSTDFF_BEXM10 = "CUSTDFF_BEXM10";
	private final String CUSTDFF_BEXM11 = "CUSTDFF_BEXM11";
	private final String CUSTDFF_BEXM12 = "CUSTDFF_BEXM12";
	private final String CUSTDFF_BEXM13 = "CUSTDFF_BEXM13";
	private final String CUSTDFF_BEXM14 = "CUSTDFF_BEXM14";

	private final String CNY_TARIFF = "CNY";
	private final String TYPHOON_TARIFF = "TYPHOON";
	private final String ZERO_QTY = "0.0";
	private final String DISCOUNT_TARIFF = "DISCOUNT"

	private final Logger LOGGER = Logger.getLogger(ExtractInvoice.class);
}