package SourceCode.CodeExtensions

import com.navis.argo.ContextHelper;
import com.navis.argo.business.atoms.ServiceQuantityUnitEnum;
import com.navis.billing.business.model.*;
import com.navis.billing.BillingField;
import com.navis.billing.business.api.IInvoiceManager;
import com.navis.billing.business.model.Invoice;
import com.navis.external.argo.AbstractGroovyWSCodeExtension;
import com.navis.framework.business.Roastery;
import com.navis.framework.portal.FieldChanges;

import groovy.json.JsonSlurper;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

class DCBInvoiceWsHandler extends AbstractGroovyWSCodeExtension {
	//todo: need to handle the xml structure.
	// add item by tariff code (OK)
	// remove draft invoice  (OK)
	// finalize draft invoice (OK)
	// remove item by gkey
	// adjust item
    public String execute(Map<String, Object> inParams) {
	String taskId = inParams.keySet();
	String dataString = inParams.values();
	String draftInv = removeChar(dataString);
	String respond = "SUCCESSFUL";

	taskId = removeChar(taskId);
	log("DW: 1.1" + taskId);

	switch (taskId){
	    case "createInvoice":
		addInvoiceItem(draftInv);
		break;
	    case "removeInvoiceItem":
		removeInvoiceItem(draftInv,"DW_TEST_1");
		break;
	    case "deleteInvoice":
		dataString = (String) inParams.get("deleteInvoce");
		if (!DeleteDraftInvoice(draftInv)) {
		    respond = "Delete Draft Inboice Failed";
		    log("DW: 1.3 : " + respond);
		}
		break;
	    case "finalInvoce":
		dataString = (String) inParams.get("finalInvoce");
		if (!FinalizeInvoice(draftInv)) {
		    respond = "Finalize Invoice Failed";
		    log("DW: 1.4 : " + respond);
		}
	}

	return respond;
    }

    private String removeChar(String str){
	String retStr = str.substring(1,str.length()-1);
	log("DW : 3.1 " + retStr);
	return retStr;
    }

    private boolean removeInvoiceItem(String draftInv, String _tariffID){
	//remove item by tariff ID , tariff rate
	log("DW : 5.1");
	String tariffID = _tariffID;
	String contractID = "SERVICE CATEGORY";
	boolean result = false;

	Invoice inInvoice = Invoice.findInvoiceByDraftNbr(draftInv);
	Set<InvoiceItem> invItems = inInvoice.getInvoiceInvoiceItems();
	InvoiceItem removeItem = null;

	log("DW : 5.2  Invoice items : " + invItems.size());
	for (InvoiceItem item : invItems) {
	    String _tariff = item.getItemTariff();
	    Long _tariffGkey = item.getItemGkey();
	    log("DW : 5.4" + _tariff.toString() + " Gkey : " + _tariffGkey);
	    if (_tariff.equals(tariffID)) {
		removeItem = item;
		result = true;
		log("DW : 5.5  remove item is matched: " + _tariff);
		break;
	    }
	}

	if (removeItem != null) {
	    invItems.remove(removeItem);
	}

	log("DW : 5.10");
    }

    private boolean addInvoiceItem(String draftInv) {
	log("DW : 4.1 ");
	String tariffID = "DW_TEST_1";
	String contractID = "SERVICE CATEGORY";
	long qty = 2L;
	boolean result = false;

	Invoice inInvoice = Invoice.findInvoiceByDraftNbr(draftInv);

	if (inInvoice == null)
	    return result;

	Tariff _tariff = Tariff.findTariff(tariffID);

	if (_tariff == null)
	    return result;

	log("DW : 4.2 ");

	if (inInvoice.isFinalized()==false) {
	    Contract cnt = Contract.findContract(contractID);
	    TariffRate _tariffRate = cnt.findCurrentTariffRate(_tariff);  // ? not sure whether it will also check addendum contract rate or just use the current contract directly.
	    InvoiceItem invoiceItem = new InvoiceItem();
	    FieldChanges fieldChanges = new FieldChanges();
	    fieldChanges.setFieldChange(BillingField.ITEM_DESCRIPTION, _tariffRate.getRateTariff().getTariffDescription());
	    fieldChanges.setFieldChange(BillingField.ITEM_AMOUNT, _tariffRate.getRateAmount() * qty);
	    fieldChanges.setFieldChange(BillingField.ITEM_INVOICE, inInvoice);
	    fieldChanges.setFieldChange(BillingField.ITEM_TARIFF_RATE, _tariffRate);
	    fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY, 1.0d);
	    fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_BILLED, 1.0d);
	    fieldChanges.setFieldChange(BillingField.ITEM_QUANTITY_UNIT, ServiceQuantityUnitEnum.ITEMS);
	    invoiceItem.applyFieldChanges(fieldChanges);

	    log("DW : 4.9 " );
	    Set hSet = new HashSet();
	    hSet.add(invoiceItem);
	    inInvoice.addInvoiceInvoiceItems(hSet);

	    log("DW : 4.6 ");
	    result = true;
	}
	log("DW : 4.7");
	return result;
    }

    private boolean DeleteDraftInvoice(String draftInv) {
	boolean result = false;

	IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);

	Invoice inInvoice = Invoice.findInvoiceByDraftNbr(draftInv);
	log("DW: 2.1");

	if (inInvoice == null)
	    return result;

	log("DW: 2.1");
	if (inInvoice.isFinalized()==false) {
	    invoiceManager.delete(inInvoice);
	    result =true;
	    log("DW: 2.2");
	}

	log("DW: 2.3");
	return result;
    }

    private boolean FinalizeInvoice(String draftInv) {
	boolean result = false;

	Invoice inInvoice = Invoice.findInvoiceByDraftNbr(draftInv);

	if (inInvoice == null)
	    return result;

	if (inInvoice.isFinalized()==false) {
	    IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
	    TimeZone timeZone = ContextHelper.getThreadUserTimezone();
	    Calendar calendar = Calendar.getInstance(timeZone);
	    Date invoiceExecutionDate = calendar.getTime();
	    inInvoice.setInvoiceFinalizedDate(new Date());
	    invoiceManager.doFinalize(inInvoice);
	    result =true;
	}

	return result;
    }
}