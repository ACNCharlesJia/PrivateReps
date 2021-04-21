package SourceCode.GroovyPlugins

import java.text.SimpleDateFormat

import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.GeneralReference;
import com.navis.billing.BillingField;
import com.navis.billing.business.api.IInvoiceManager;
import com.navis.billing.business.model.Invoice;
import com.navis.billing.business.model.InvoiceItem
import com.navis.framework.business.Roastery;
import com.navis.framework.metafields.MetafieldEntry;
import com.navis.framework.metafields.MetafieldId
import com.navis.framework.persistence.HibernateApi;
import com.navis.framework.portal.FieldChanges;
import com.navis.framework.portal.Ordering;
import com.navis.framework.portal.QueryUtils
import com.navis.framework.portal.query.DomainQuery;
import com.navis.framework.portal.query.PredicateFactory;
import com.navis.framework.query.common.api.QueryResult
import org.apache.axis.utils.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;


public class DoFinalizeInvoice extends GroovyApi {
	private static ServiceSoapProxy proxy=new ServiceSoapProxy();
	private static String reasonCode="";
	private static String reason="";
	private static SimpleDateFormat sf=new SimpleDateFormat("yyyyMMddHHmmssSS");

	public void execute(Map map){
		

		this.log("-----1------doFinalizeInvoice start---");
		QueryResult rs= getInvoiceResult("Y");
		if(rs==null){
			

			this.log("-----2------doFinalizeInvoice,rs=null, end---");
			return ;
		}
		this.log("-----2---------rs="+ToStringBuilder.reflectionToString(rs));
		Iterator it = rs.getIterator();
		int i=0;
//		String currentId=sf.format(new Date());
		List<Invoice> invList=new ArrayList<Invoice>();
//		this.log("-----2.1---------currentId="+currentId);
		while(it.hasNext()){
			

			this.log("-----3.1---------");
			String gkey =(String) rs.getValue(i++, 0);
			it.next();
			this.log("-----3.2---------gkey="+gkey);
			Invoice inv=getInvoice(gkey);
			this.log("-----3.2.1---------inv="+ToStringBuilder.reflectionToString(inv));
			FieldChanges fieldChanges = new FieldChanges();
			fieldChanges.setFieldChange(BillingField.INVOICE_FLEX_STRING03, null);
			inv.applyFieldChanges(fieldChanges);
			invList.add(inv);
			this.log("-----3.2.2---------flexString03="+inv.getInvoiceFlexString03());
			this.log("-----3.2.3---------invList.size="+invList.size());
		}
		
		for(Invoice invoice:invList){
			

			boolean isSuccess=false;
			int count=0;
			while(count<3){
				

				this.log("----3.3.1,count="+count);
				try{
					

					this.log("----3.3.2,isSuccess="+isSuccess);
					isSuccess= finalizeInvoice(invoice);
					reasonCode="";
					reason="";
					if(!isSuccess&&!StringUtils.isEmpty(invoice.getInvoiceFinalNbr())){
						

						reasonCode="022";
						reason="the invoice has already finaled";
					}
					this.log("----3.3.3,isSuccess="+isSuccess);
					break;
				}catch(Exception e){
					

					e.printStackTrace();
					count++;
					reasonCode="999";
					reason=e.getMessage();
					this.log("----3.3.4,count++="+count);
					this.log("----3.3.5,reason="+reason);
				}
			}
			Map<String,String> itemMap=new HashMap<String,String>();
			String status="REJECTED";
			if(isSuccess){
				

				status="ACCEPTED";
				Set<InvoiceItem> itemSet=(Set<InvoiceItem>)invoice.getInvoiceInvoiceItems();
				if(itemSet!=null){
					

					Iterator<InvoiceItem> itat=itemSet.iterator();
					while(itat.hasNext()){
						

						InvoiceItem item=itat.next();
//						String value=",";
						String value=item.getItemNotes();
						this.log("----------3.4------,seq+created="+value);
						itemMap.put(String.valueOf(item.getItemGkey()), value);
					}
				}
			}
			
			this.log("-----3.5---------itemMap="+ToStringBuilder.reflectionToString(itemMap));
			String results=createFinalXmlByInvoice(invoice.getInvoiceFinalNbr(),itemMap,invoice.getInvoiceFlexString04(),status,reasonCode,reason);
			this.log("-----3.6---------before string03="+invoice.getInvoiceFlexString03());
			FieldChanges fieldChanges = new FieldChanges();
//			fieldChanges.setFieldChange(BillingField.INVOICE_FLEX_STRING03, null);
			fieldChanges.setFieldChange(BillingField.INVOICE_FLEX_STRING04, null);
			invoice.applyFieldChanges(fieldChanges);
			this.log("-----3.7---------after string03="+invoice.getInvoiceFlexString03());
			this.log("------3.8--------,xml="+results);
			String ret=proxy.finalInvoiceResponse(results);
			this.log("-----3.9-----response:finalNbr="+invoice.getInvoiceFinalNbr()+",response="+ret);
		}
	}
	
	Invoice getInvoice(String gkey){
		

		DomainQuery dq = QueryUtils.createDomainQuery("Invoice");
		MetafieldId miGK = (new MetafieldEntry("invoiceGkey").getMetafieldId());
		dq.addDqPredicate(PredicateFactory.eq(miGK,gkey));
		dq.addDqOrdering(Ordering.desc(miGK));
		dq.setMaxResults(1);
		HibernateApi hbrapi = Roastery.getHibernateApi();
		return (Invoice) hbrapi.getUniqueEntityByDomainQuery(dq);
	}
	
	QueryResult getInvoiceResult(String value) {
        

        DomainQuery dq = QueryUtils.createDomainQuery("Invoice");
        MetafieldId miID = (new MetafieldEntry("invoiceFlexString03").getMetafieldId());
        MetafieldId miGK = (new MetafieldEntry("invoiceGkey").getMetafieldId());
        dq.addDqPredicate(PredicateFactory.eq(miID,value));
        dq.addDqOrdering(Ordering.desc(miGK));
        dq.setMaxResults(99999);
        HibernateApi hbrapi = Roastery.getHibernateApi();
        return hbrapi.findValuesByDomainQuery(dq);
    }
	
	private boolean finalizeInvoice(Invoice inInvoice){
		

		boolean result = false;
		if (inInvoice == null){
			

			return result;
		}
		if(!inInvoice.isFinalized()){
			

			IInvoiceManager invoiceManager = (IInvoiceManager) Roastery.getBean(IInvoiceManager.BEAN_ID);
			this.log("-----3.4.1---------invoiceManager="+ToStringBuilder.reflectionToString(invoiceManager));
			this.log("----3.4.2----doFinalize start:"+new Date());
			invoiceManager.doFinalize(inInvoice);
			this.log("----3.4.3----doFinalize end:"+new Date());
			inInvoice.setInvoiceFinalizedDate(new Date());
			result =true;
		}

		return result;
	}
	
	private String createFinalXmlByInvoice(String finalNumber,Map<String,String> finalMap,String prevId,String status,String reasonCode,String reason){
		  

		  StringBuffer result=new StringBuffer("<basicInvokeResponse><update_status><request_id>"+createRequestId()+"</request_id><prev_request_id>"+prevId+"</prev_request_id><request_type>FINAL</request_type><status>"+status+"</status><final_invoice_number>"+finalNumber+"</final_invoice_number><reject_reason_code>"+reasonCode+"</reject_reason_code><reject_reason>"+reason+"</reject_reason>");
		  Iterator<String> it=finalMap.keySet().iterator();
		  while(it.hasNext()){
			  

			  String key=it.next();
			  String[] values = finalMap.get(key).split(",");
			  String seq="";
			  String isCreate="";
			  if(values.length>0){
				  

				  seq=values[0];
			  }
			  if(values.length>1){
				  

				  isCreate=values[1];
			  }
			  result.append("<invoice_item><invoice_item_id>"+key+"</invoice_item_id><cashier_item_seq>"+seq+"</cashier_item_seq><invoice_item_cashier_created>"+isCreate+"</invoice_item_cashier_created><invoice_item_status>ACCEPTED</invoice_item_status><invoice_item_reject_reason_code></invoice_item_reject_reason_code><invoice_item_reject_reason></invoice_item_reject_reason></invoice_item>");
		  }
		  result.append("</update_status></basicInvokeResponse>");
		  return result.toString();
	  
	}
	
	private static synchronized String createRequestId(){
		

		return "DCB"+sf.format(new Date());
	}
}



class ServiceSoapProxy implements ServiceSoap {
	  private String _endpoint = null;
	  private ServiceSoap serviceSoap = null;
	  
	  public ServiceSoapProxy() {

	    _initServiceSoapProxy();
	  }
	  
	  public ServiceSoapProxy(String endpoint) {

	    _endpoint = endpoint;
	    _initServiceSoapProxy();
	  }
	  
	  private void _initServiceSoapProxy() {

	    try {

	      serviceSoap = (new ServiceLocator()).getServiceSoap();
	      if (serviceSoap != null) {

	        if (_endpoint != null)
	          ((javax.xml.rpc.Stub)serviceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
	        else
	          _endpoint = (String)((javax.xml.rpc.Stub)serviceSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
	      }
	      
	    }
	    catch (javax.xml.rpc.ServiceException serviceException) {}
	  }
	  
	  public String getEndpoint() {

	    return _endpoint;
	  }
	  
	  public void setEndpoint(String endpoint) {

	    _endpoint = endpoint;
	    if (serviceSoap != null)
	      ((javax.xml.rpc.Stub)serviceSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
	    
	  }
	  
	  public ServiceSoap getServiceSoap() {

	    if (serviceSoap == null)
	      _initServiceSoapProxy();
	    return serviceSoap;
	  }
	  
	  public java.lang.String finalInvoiceResponse(java.lang.String inputParm) throws java.rmi.RemoteException{

	    if (serviceSoap == null)
	      _initServiceSoapProxy();
	    return serviceSoap.finalInvoiceResponse(inputParm);
	  }
	  
	  
	}

	class ServiceLocator extends org.apache.axis.client.Service implements Service {
		private static GeneralReference ediServiceReference=GeneralReference.findUniqueEntryById("EDISERVICE", "config", null, null);
	    public ServiceLocator() {

	    }


	    public ServiceLocator(org.apache.axis.EngineConfiguration config) {
	        super(config);
	    }

	    public ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
	        super(wsdlLoc, sName);
	    }
	    
	    // Use to get a proxy class for ServiceSoap
	    private java.lang.String ServiceSoap_address = ediServiceReference.getRefValue1();

	    public java.lang.String getServiceSoapAddress() {

	        return ServiceSoap_address;
	    }

	    // The WSDD service name defaults to the port name.
	    private java.lang.String ServiceSoapWSDDServiceName = "ServiceSoap";

	    public java.lang.String getServiceSoapWSDDServiceName() {

	        return ServiceSoapWSDDServiceName;
	    }

	    public void setServiceSoapWSDDServiceName(java.lang.String name) {

	        ServiceSoapWSDDServiceName = name;
	    }

	    public ServiceSoap getServiceSoap() throws javax.xml.rpc.ServiceException {

	       java.net.URL endpoint;
	        try {

	            endpoint = new java.net.URL(ServiceSoap_address);
	        }
	        catch (java.net.MalformedURLException e) {

	            throw new javax.xml.rpc.ServiceException(e);
	        }
	        return getServiceSoap(endpoint);
	    }

	    public ServiceSoap getServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {

	        try {

	            ServiceSoapStub _stub = new ServiceSoapStub(portAddress, this);
	            _stub.setPortName(getServiceSoapWSDDServiceName());
	            return _stub;
	        }
	        catch (org.apache.axis.AxisFault e) {

	            return null;
	        }
	    }

	    public void setServiceSoapEndpointAddress(java.lang.String address) {

	        ServiceSoap_address = address;
	    }

	    /**
	     * For the given interface, get the stub implementation.
	     * If this service has no port for the given interface,
	     * then ServiceException is thrown.
	     */
	    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {

	        try {

	            if (ServiceSoap.class.isAssignableFrom(serviceEndpointInterface)) {

	                ServiceSoapStub _stub = new ServiceSoapStub(new java.net.URL(ServiceSoap_address), this);
	                _stub.setPortName(getServiceSoapWSDDServiceName());
	                return _stub;
	            }
	        }
	        catch (java.lang.Throwable t) {

	            throw new javax.xml.rpc.ServiceException(t);
	        }
	        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
	    }

	    /**
	     * For the given interface, get the stub implementation.
	     * If this service has no port for the given interface,
	     * then ServiceException is thrown.
	     */
	    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {

	        if (portName == null) {

	            return getPort(serviceEndpointInterface);
	        }
	        java.lang.String inputPortName = portName.getLocalPart();
	        if ("ServiceSoap".equals(inputPortName)) {

	            return getServiceSoap();
	        }
	        else  {

	            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
	            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
	            return _stub;
	        }
	    }

	    public javax.xml.namespace.QName getServiceName() {

	        return new javax.xml.namespace.QName("http://tempuri.org/", "Service");
	    }

	    private java.util.HashSet ports = null;

	    public java.util.Iterator getPorts() {

	        if (ports == null) {

	            ports = new java.util.HashSet();
	            ports.add(new javax.xml.namespace.QName("http://tempuri.org/", "ServiceSoap"));
	        }
	        return ports.iterator();
	    }

	    /**
	    * Set the endpoint address for the specified port name.
	    */
	    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

	        
	if ("ServiceSoap".equals(portName)) {

	            setServiceSoapEndpointAddress(address);
	        }
	        else 
	{ // Unknown Port Name

	            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
	        }
	    }

	    /**
	    * Set the endpoint address for the specified port name.
	    */
	    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {

	        setEndpointAddress(portName.getLocalPart(), address);
	    }

	}

	interface ServiceSoap extends java.rmi.Remote {
	    public java.lang.String finalInvoiceResponse(java.lang.String inputParm) throws java.rmi.RemoteException;
	}

	class ServiceSoapStub extends org.apache.axis.client.Stub implements ServiceSoap {
	    private java.util.Vector cachedSerClasses = new java.util.Vector();
	    private java.util.Vector cachedSerQNames = new java.util.Vector();
	    private java.util.Vector cachedSerFactories = new java.util.Vector();
	    private java.util.Vector cachedDeserFactories = new java.util.Vector();

	    static org.apache.axis.description.OperationDesc [] _operations;

	    static {

	        _operations = new org.apache.axis.description.OperationDesc[1];
	        _initOperationDesc1();
	    }

	    private static void _initOperationDesc1(){

	        org.apache.axis.description.OperationDesc oper;
	        org.apache.axis.description.ParameterDesc param;
	        oper = new org.apache.axis.description.OperationDesc();
	        oper.setName("FinalInvoiceResponse");
	        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://tempuri.org/", "inputParm"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
//	        param.setOmittable(true);
	        oper.addParameter(param);
	        oper.setReturnType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
	        oper.setReturnClass(java.lang.String.class);
	        oper.setReturnQName(new javax.xml.namespace.QName("http://tempuri.org/", "FinalInvoiceResponseResult"));
	        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
	        oper.setUse(org.apache.axis.constants.Use.LITERAL);
	        _operations[0] = oper;

	    }

	    public ServiceSoapStub() throws org.apache.axis.AxisFault {
	         this(null);
	    }

	    public ServiceSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
	         this(service);
	         super.cachedEndpoint = endpointURL;
	    }

	    public ServiceSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {

	        if (service == null) {

	            super.service = new org.apache.axis.client.Service();
	        } else {

	            super.service = service;
	        }
	        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
	    }

	    protected org.apache.axis.client.Call createCall() throws java.rmi.RemoteException {

	        try {

	            org.apache.axis.client.Call _call = super._createCall();
	            if (super.maintainSessionSet) {

	                _call.setMaintainSession(super.maintainSession);
	            }
	            if (super.cachedUsername != null) {

	                _call.setUsername(super.cachedUsername);
	            }
	            if (super.cachedPassword != null) {

	                _call.setPassword(super.cachedPassword);
	            }
	            if (super.cachedEndpoint != null) {

	                _call.setTargetEndpointAddress(super.cachedEndpoint);
	            }
	            if (super.cachedTimeout != null) {

	                _call.setTimeout(super.cachedTimeout);
	            }
	            if (super.cachedPortName != null) {

	                _call.setPortName(super.cachedPortName);
	            }
	            java.util.Enumeration keys = super.cachedProperties.keys();
	            while (keys.hasMoreElements()) {

	                java.lang.String key = (java.lang.String) keys.nextElement();
	                _call.setProperty(key, super.cachedProperties.get(key));
	            }
	            return _call;
	        }
	        catch (java.lang.Throwable _t) {

	            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
	        }
	    }

	    public java.lang.String finalInvoiceResponse(java.lang.String inputParm) throws java.rmi.RemoteException {

	        if (super.cachedEndpoint == null) {

	            throw new org.apache.axis.NoEndPointException();
	        }
	        org.apache.axis.client.Call _call = createCall();
	        _call.setOperation(_operations[0]);
	        _call.setUseSOAPAction(true);
	        _call.setSOAPActionURI("http://tempuri.org/FinalInvoiceResponse");
	        _call.setEncodingStyle(null);
	        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
	        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
	        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
	        _call.setOperationName(new javax.xml.namespace.QName("http://tempuri.org/", "FinalInvoiceResponse"));

	        setRequestHeaders(_call);
	        setAttachments(_call);
	 try {
        java.lang.Object _resp = _call.invoke([inputParm] as java.lang.Object[]);

	        if (_resp instanceof java.rmi.RemoteException) {

	            throw (java.rmi.RemoteException)_resp;
	        }
	        else {

	            extractAttachments(_call);
	            try {

	                return (java.lang.String) _resp;
	            } catch (java.lang.Exception _exception) {

	                return (java.lang.String) org.apache.axis.utils.JavaUtils.convert(_resp, java.lang.String.class);
	            }
	        }
	  } catch (org.apache.axis.AxisFault axisFaultException) {

	  throw axisFaultException;
	}
	    }

	}

	interface Service extends javax.xml.rpc.Service {
	    public java.lang.String getServiceSoapAddress();

	    public ServiceSoap getServiceSoap() throws javax.xml.rpc.ServiceException;

	    public ServiceSoap getServiceSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
	}