package SourceCode.CodeExtensions;

import com.navis.argo.ArgoExtractEntity;
import com.navis.argo.business.api.ArgoUtils;
import com.navis.billing.business.model.Invoice;
import com.navis.external.argo.AbstractGroovyWSCodeExtension;

import groovy.json.JsonSlurper
import org.apache.log4j.Logger;

/**
 * MTL Auto Credit and Re-bill.
 *
 * Authors: <a href="mailto:Mugunthan.Selvaraj@navis.com">Mugunthan Selvaraj</a>
 * Date: 03 Aug 2015
 * JIRA: CSDV-3129
 * SFDC: NA
 * Called from: Called through the web service request from N4
 *
 * S.no   Modified Date      Modified By          Jira Id    SFDC      Change Description
 */

/**
 * NOTE: This code will work for CUE and CME only
 *
 * Send the following format of JSON data for processing{'rebillRequest':{'invoices':{'invoice':[{'cueGkey':[<<array of comma separated gkeys>>],'draftNbr':'draft no'},{'cmeGkey':[<<array of comma separated gkeys>>],'draftNbr':'draft no'}]}}}1. Example with both CUE and CME{"rebillRequest":{"invoices":{"invoice":[{"cueGkey":["51","53"],"draftNbr":"402"},{"cmeGkey":["13","23"],"draftNbr":"402"}]}}}2. Example with only CUE{"rebillRequest":{"invoices":{"invoice":{"cueGkey":["51","53"],"draftNbr":"402"}}}}* */

class MTLN4BillingACRHandler extends AbstractGroovyWSCodeExtension {
    @Override
    public String execute(Map<String, String> inParams) {
        LOGGER.info(String.format("At start of %s.execute at %s", getClass().getName(), ArgoUtils.timeNow()));
        String dataString = (String) inParams.get("rebillRequest");
        LOGGER.debug("The data string:\n" + dataString);
        if (dataString == null) {
            LOGGER.error("the input json string is null");
            return "The data string cannot be null";
        }

        //This will not have any impact when we call from the code extension of N4.
        //This will be helpful when we test the code through the web service with JSON string
        // having single quotes replaced with double quotes
        dataString = dataString.replaceAll("'", "\"");

        def jsonSlurper = new JsonSlurper();
        def jsonData = jsonSlurper.parseText(dataString);

        def invoiceDraftNoAndEventGkeysArray = jsonData.rebillRequest.invoices;

        if (invoiceDraftNoAndEventGkeysArray == null) {
            LOGGER.error("Invalid data");
            return "Cannot process as the input data is invalid";
        }
        StringBuilder result = new StringBuilder();
        Map params;
        for (def invoiceDraftNoAndEventGkeys : invoiceDraftNoAndEventGkeysArray.invoice) {
            String invoiceDraftNo = invoiceDraftNoAndEventGkeys.draftNbr; //Invoice draft number
            Invoice parentInvoice;
            String eventEntity;
            String forEvent;
            boolean rebillReqd = false;
            parentInvoice = Invoice.findInvoiceByDraftNbr(invoiceDraftNo);
            if (parentInvoice == null) {
                def msg = "Could not load the invoice with draft no.:" + invoiceDraftNo;
                LOGGER.error(msg);
                result.append(msg);
            } else {
                String triggerDate = invoiceDraftNoAndEventGkeys.triggerDate;
                if (invoiceDraftNoAndEventGkeys.cmeGkey == null) {
                    LOGGER.debug("Parameters does not belong to ChargeableMarineEvents");
                } else {
                    LOGGER.debug("Parameters belong to ChargeableMarineEvents");
                    eventEntity = ArgoExtractEntity.CHARGEABLE_MARINE_EVENT;
                    forEvent = FOR_EVENT_CME;
                }
                if (invoiceDraftNoAndEventGkeys.cueGkey == null) {
                    LOGGER.debug("Parameters does not belong to ChargeableUnitEvents");
                } else {
                    LOGGER.debug("Parameters belong to ChargeableUnitEvents");
                    eventEntity = ArgoExtractEntity.CHARGEABLE_UNIT_EVENT;
                    forEvent = FOR_EVENT_CUE;
                }
                if (eventEntity == null) {
                    LOGGER.error("Cannot find suitable event entity for the JSON data");
                    return "Not proper event entity found for the JSON data. Please check your data";
                }

                if (invoiceDraftNoAndEventGkeys.rebillReqd != null && "false".equals(invoiceDraftNoAndEventGkeys.rebillReqd)) {
                    LOGGER.debug("Rebill is not required. So setting rebill Required property as false");
                    rebillReqd = false;
                }else if(invoiceDraftNoAndEventGkeys.rebillReqd != null && "true".equals(invoiceDraftNoAndEventGkeys.rebillReqd)) {
                    rebillReqd = true;
                }

                List itemEventGkeys = convertEventArrayIntoList(invoiceDraftNoAndEventGkeys, forEvent);
                params = new HashMap();
                params.put(invoiceName, parentInvoice);
                params.put(resultName, result);
                params.put(rebillReqdName, rebillReqd);
                params.put(invoiceGkeyName, itemEventGkeys);
                params.put(triggerDateName, triggerDate);
                if (itemEventGkeys.size() == 0) {
                    String msg = "\nNo invoice items and corresponding extract gkeys found for invoice(draft no): " + parentInvoice.getInvoiceDraftNbr();
                    LOGGER.error(msg);
                    result.append(msg);
                } else {
                    Object codeExtLibrary = getLibrary(ACR_HANDLER_GROOVY_LIB);
                    codeExtLibrary.execute(params);
                }
            }
        }
        if (params != null) {
            return params.get(resultName).toString();
        } else {
            return "ERROR HAPPENED. PLEASE CONTACT ADMINSTRATOR TO SEE N4 BILLING LOGS FOR ERROR DETAILS";
        }
    }

    /**
     * Helper method to convert the array of input event gkeys to list so that it will be easy to test using contains
     * method
     * @param invoiceDraftNoAndEventGkeys
     * @return
     */
    private List convertEventArrayIntoList(def invoiceDraftNoAndEventGkeys, String forEvent) {
        List result = new ArrayList();

        if (forEvent == FOR_EVENT_CUE) {
            invoiceDraftNoAndEventGkeys.cueGkey.each { eventGkey ->
                if (null != eventGkey)
                    result.add(Long.parseLong(eventGkey))
            }
        }
        if (forEvent == FOR_EVENT_CME) {
            invoiceDraftNoAndEventGkeys.cmeGkey.each { eventGkey ->
                if (null != eventGkey)
                    result.add(Long.parseLong(eventGkey))
            }
        }

        LOGGER.debug("List size of event gkeys input in json data string: " + result.size());
        return result;
    }

    private static final Logger LOGGER = Logger.getLogger(MTLN4BillingACRHandler.class);
    private final String FOR_EVENT_CUE = "CUE";
    private final String FOR_EVENT_CME = "CME";
    private final String invoiceName = "INVOICE";
    private final String invoiceGkeyName = "EVENT_GKEYS";
    private final String rebillReqdName = "rebillReqd";
    private final String resultName = "RESULT";
    private final String triggerDateName = "triggerDate";
    private final String ACR_HANDLER_GROOVY_LIB = "MTLN4BillingACRHandlerCore";
}