package SourceCode.CodeExtensions;

import org.apache.axis.utils.StringUtils;

import com.navis.argo.business.api.GroovyApi;
import com.navis.argo.business.model.GeneralReference;

public class ServiceReceiveSoapProxy extends GroovyApi implements ServiceReceiveSoap {
  private String _endpoint = null;
  private ServiceReceiveSoap serviceReceiveSoap = null;
  
  public ServiceReceiveSoapProxy() {
    

    _initServiceReceiveSoapProxy();
  }
  
  public ServiceReceiveSoapProxy(String endpoint) {
    

    _endpoint = endpoint;
    _initServiceReceiveSoapProxy();
  }
  
  private void _initServiceReceiveSoapProxy() {
    

    try {
      

      serviceReceiveSoap = (new ServiceReceiveLocator()).getServiceReceiveSoap();
      if (serviceReceiveSoap != null) {
        

        if (_endpoint != null)
          ((javax.xml.rpc.Stub)serviceReceiveSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
        else
          _endpoint =((javax.xml.rpc.Stub)serviceReceiveSoap)._getProperty("javax.xml.rpc.service.endpoint.address");
      }
      
    }
    catch (javax.xml.rpc.ServiceException serviceException) {}
  }
  
  public String getEndpoint() {
    

    return _endpoint;
  }
  
  public void setEndpoint(String endpoint) {
    

    _endpoint = endpoint;
    if (serviceReceiveSoap != null)
      ((javax.xml.rpc.Stub)serviceReceiveSoap)._setProperty("javax.xml.rpc.service.endpoint.address", _endpoint);
    
  }
  
  public ServiceReceiveSoap getServiceReceiveSoap() {
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap;
  }
  
  public PackageOfString importManifest(java.lang.String cipher, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir, int layer, java.lang.String content) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.importManifest(cipher, intent, wharf, course, signal, vessel, voyage, moment, harbor, devoir, layer, content);
  }
  
  public PackageOfString appendManifest(java.lang.String cipher, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.appendManifest(cipher, intent, wharf, course, signal, vessel, voyage, moment, harbor, devoir);
  }
  
  public GeneralPackage modifyManifest(java.lang.String cipher, java.lang.String code, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.modifyManifest(cipher, code, intent, wharf, course, signal, vessel, voyage, moment, harbor, devoir);
  }
  
  public GeneralPackage deleteManifest(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.deleteManifest(cipher, code);
  }
  
  public PackageOfString importFreight(java.lang.String cipher, java.lang.String manifest, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.importFreight(cipher, manifest, sheet, layer, payer, goods, cargo, weight, rebate, transit, content);
  }
  
  public PackageOfString appendFreight(java.lang.String cipher, java.lang.String manifest, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.appendFreight(cipher, manifest, sheet, layer, payer, goods, cargo, weight, rebate, transit, content);
  }
  
  public GeneralPackage modifyFreight(java.lang.String cipher, java.lang.String code, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.modifyFreight(cipher, code, sheet, layer, payer, goods, cargo, weight, rebate, transit, content);
  }
  
  public GeneralPackage deleteFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.deleteFreight(cipher, code);
  }
  
  public PackageOfString choiceManifest(java.lang.String cipher, int intent, java.lang.String wharf, java.lang.String signal, java.lang.String vessel, java.lang.String voyage) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.choiceManifest(cipher, intent, wharf, signal, vessel, voyage);
  }
  
  public PackageOfString choiceFreight(java.lang.String cipher, int intent, java.lang.String wharf, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.lang.String sheet, int layer) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.choiceFreight(cipher, intent, wharf, signal, vessel, voyage, sheet, layer);
  }
  
  public PackageOfString calculateFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.calculateFreight(cipher, code);
  }
  
  public GeneralPackage chargeFreight(java.lang.String cipher, java.lang.String code, java.math.BigDecimal revenue) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.chargeFreight(cipher, code, revenue);
  }
  
  public GeneralPackage cancelChargeFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.cancelChargeFreight(cipher, code);
  }
  
  public PackageOfString calculateCheckCode(java.lang.String cipher, java.lang.String species, int numeral, java.lang.String payer, java.lang.String vessel, java.lang.String voyage, java.math.BigDecimal revenue, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.calculateCheckCode(cipher, species, numeral, payer, vessel, voyage, revenue, createCode, createName, createTime);
  }
  
  public GeneralPackage issueChargeFreight1(java.lang.String cipher, java.lang.String code, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.issueChargeFreight1(cipher, code, species, numeral, payer, balance, createCode, createName, createTime, revenue);
  }
  
  public GeneralPackage issueChargeFreight2(java.lang.String cipher, java.lang.String[] codes, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.issueChargeFreight2(cipher, codes, species, numeral, payer, balance, createCode, createName, createTime, revenue);
  }
  
  public GeneralPackage updateInvoice(java.lang.String cipher, java.lang.String code, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.updateInvoice(cipher, code, species, numeral, payer, balance, createCode, createName, createTime, revenue);
  }
  
  public GeneralPackage cancelInvoice(java.lang.String cipher, java.lang.String species, int numeral) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.cancelInvoice(cipher, species, numeral);
  }
  
  public PackageOfString recreateInvoice(java.lang.String cipher, java.lang.String oldSpecies, int oldNumeral, java.lang.String newSpecies, int newNumeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.recreateInvoice(cipher, oldSpecies, oldNumeral, newSpecies, newNumeral, payer, balance, createCode, createName, createTime, revenue);
  }
  
  public GeneralPackage censorManifest(java.lang.String cipher, java.lang.String code, java.lang.String censorCode, java.lang.String censorName) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.censorManifest(cipher, code, censorCode, censorName);
  }
  
  public GeneralPackage affirmManifest(java.lang.String cipher, java.lang.String code, java.lang.String affirmCode, java.lang.String affirmName) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.affirmManifest(cipher, code, affirmCode, affirmName);
  }
  
  public PackageOfString changeFreight(java.lang.String cipher, java.lang.String freight, java.lang.String manifest, java.lang.String sheet) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.changeFreight(cipher, freight, manifest, sheet);
  }
  
  public PackageOfDecimal createDeposit(java.lang.String cipher, java.util.Calendar moment, java.lang.String createCode, java.lang.String createName, java.lang.String remark) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.createDeposit(cipher, moment, createCode, createName, remark);
  }
  
  public PackageOfString queryInvoice1(java.lang.String cipher, java.util.Calendar moment1, java.util.Calendar moment2) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.queryInvoice1(cipher, moment1, moment2);
  }
  
  public PackageOfString queryInvoice2(java.lang.String cipher, java.lang.String species, int start, int _final) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.queryInvoice2(cipher, species, start, _final);
  }
  
  public PackageOfString queryInvoice3(java.lang.String cipher, java.util.Calendar moment1, java.util.Calendar moment2, java.lang.String mark) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.queryInvoice3(cipher, moment1, moment2, mark);
  }
  
  public PackageOfString queryInvoice4(java.lang.String cipher, java.lang.String species, int start, int _final, java.lang.String mark) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.queryInvoice4(cipher, species, start, _final, mark);
  }
  
  public PackageOfInt32 getManifestStatus(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException{
    

    if (serviceReceiveSoap == null)
      _initServiceReceiveSoapProxy();
    return serviceReceiveSoap.getManifestStatus(cipher, code);
  }
  
  
}

interface ServiceReceive extends javax.xml.rpc.Service {
    public java.lang.String getServiceReceiveSoapAddress();

    public ServiceReceiveSoap getServiceReceiveSoap() throws javax.xml.rpc.ServiceException;

    public ServiceReceiveSoap getServiceReceiveSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException;
}

class ServiceReceiveLocator extends org.apache.axis.client.Service implements ServiceReceive {
	
	private GeneralReference ref = GeneralReference.findUniqueEntryById("MSA_SOAP_ADDRESS", "config", null, null);

    public ServiceReceiveLocator() {
    

    }


    public ServiceReceiveLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
		
    }

    public ServiceReceiveLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
		
    }

    // Use to get a proxy class for ServiceReceiveSoap
	private java.lang.String ServiceReceiveSoap_address = "";

    public java.lang.String getServiceReceiveSoapAddress() {
    	

    	if(StringUtils.isEmpty(ServiceReceiveSoap_address)){
    		

    		if(ref==null){
    			

    			ref=GeneralReference.findUniqueEntryById("MSA_SOAP_ADDRESS", "config", null, null);
    		}
    		ServiceReceiveSoap_address=ref.getRefValue1();
    	}
        return ServiceReceiveSoap_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ServiceReceiveSoapWSDDServiceName = "ServiceReceiveSoap";

    public java.lang.String getServiceReceiveSoapWSDDServiceName() {
        

        return ServiceReceiveSoapWSDDServiceName;
    }

    public void setServiceReceiveSoapWSDDServiceName(java.lang.String name) {
        

        ServiceReceiveSoapWSDDServiceName = name;
    }

    public ServiceReceiveSoap getServiceReceiveSoap() throws javax.xml.rpc.ServiceException {
       

       java.net.URL endpoint;
        try {
        	

        	if(StringUtils.isEmpty(ServiceReceiveSoap_address)){
        		

        		if(ref==null){
        			

        			ref=GeneralReference.findUniqueEntryById("MSA_SOAP_ADDRESS", "config", null, null);
        		}
        		ServiceReceiveSoap_address=ref.getRefValue1();
        	}
            endpoint = new java.net.URL(ServiceReceiveSoap_address);
        }
        catch (java.net.MalformedURLException e) {
            

            throw new javax.xml.rpc.ServiceException(e);
        }
        return getServiceReceiveSoap(endpoint);
    }

    public ServiceReceiveSoap getServiceReceiveSoap(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        

        try {
            

            ServiceReceiveSoapStub _stub = new ServiceReceiveSoapStub(portAddress, this);
            _stub.setPortName(getServiceReceiveSoapWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            

            return null;
        }
    }

    public void setServiceReceiveSoapEndpointAddress(java.lang.String address) {
        

        ServiceReceiveSoap_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        

        try {
            

            if (ServiceReceiveSoap.class.isAssignableFrom(serviceEndpointInterface)) {
            	

            	if(StringUtils.isEmpty(ServiceReceiveSoap_address)){
            		

            		if(ref==null){
            			

            			ref=GeneralReference.findUniqueEntryById("MSA_SOAP_ADDRESS", "config", null, null);
            		}
            		ServiceReceiveSoap_address=ref.getRefValue1()
            	}
                ServiceReceiveSoapStub _stub = new ServiceReceiveSoapStub(new java.net.URL(ServiceReceiveSoap_address), this);
                _stub.setPortName(getServiceReceiveSoapWSDDServiceName());
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
        if ("ServiceReceiveSoap".equals(inputPortName)) {
            

            return getServiceReceiveSoap();
        }
        else  {
            

            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        

        return new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ServiceReceive");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        

        if (ports == null) {
            

            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ServiceReceiveSoap"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {


        
if ("ServiceReceiveSoap".equals(portName)) {
            

            setServiceReceiveSoapEndpointAddress(address);
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

interface ServiceReceiveSoap extends java.rmi.Remote {
    public PackageOfString importManifest(java.lang.String cipher, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir, int layer, java.lang.String content) throws java.rmi.RemoteException;
    public PackageOfString appendManifest(java.lang.String cipher, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir) throws java.rmi.RemoteException;
    public GeneralPackage modifyManifest(java.lang.String cipher, java.lang.String code, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir) throws java.rmi.RemoteException;
    public GeneralPackage deleteManifest(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException;
    public PackageOfString importFreight(java.lang.String cipher, java.lang.String manifest, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException;
    public PackageOfString appendFreight(java.lang.String cipher, java.lang.String manifest, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException;
    public GeneralPackage modifyFreight(java.lang.String cipher, java.lang.String code, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException;
    public GeneralPackage deleteFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException;
    public PackageOfString choiceManifest(java.lang.String cipher, int intent, java.lang.String wharf, java.lang.String signal, java.lang.String vessel, java.lang.String voyage) throws java.rmi.RemoteException;
    public PackageOfString choiceFreight(java.lang.String cipher, int intent, java.lang.String wharf, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.lang.String sheet, int layer) throws java.rmi.RemoteException;
    public PackageOfString calculateFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException;
    public GeneralPackage chargeFreight(java.lang.String cipher, java.lang.String code, java.math.BigDecimal revenue) throws java.rmi.RemoteException;
    public GeneralPackage cancelChargeFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException;
    public PackageOfString calculateCheckCode(java.lang.String cipher, java.lang.String species, int numeral, java.lang.String payer, java.lang.String vessel, java.lang.String voyage, java.math.BigDecimal revenue, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime) throws java.rmi.RemoteException;
    public GeneralPackage issueChargeFreight1(java.lang.String cipher, java.lang.String code, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException;
    public GeneralPackage issueChargeFreight2(java.lang.String cipher, java.lang.String[] codes, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException;
    public GeneralPackage updateInvoice(java.lang.String cipher, java.lang.String code, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException;
    public GeneralPackage cancelInvoice(java.lang.String cipher, java.lang.String species, int numeral) throws java.rmi.RemoteException;
    public PackageOfString recreateInvoice(java.lang.String cipher, java.lang.String oldSpecies, int oldNumeral, java.lang.String newSpecies, int newNumeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException;
    public GeneralPackage censorManifest(java.lang.String cipher, java.lang.String code, java.lang.String censorCode, java.lang.String censorName) throws java.rmi.RemoteException;
    public GeneralPackage affirmManifest(java.lang.String cipher, java.lang.String code, java.lang.String affirmCode, java.lang.String affirmName) throws java.rmi.RemoteException;
    public PackageOfString changeFreight(java.lang.String cipher, java.lang.String freight, java.lang.String manifest, java.lang.String sheet) throws java.rmi.RemoteException;
    public PackageOfDecimal createDeposit(java.lang.String cipher, java.util.Calendar moment, java.lang.String createCode, java.lang.String createName, java.lang.String remark) throws java.rmi.RemoteException;
    public PackageOfString queryInvoice1(java.lang.String cipher, java.util.Calendar moment1, java.util.Calendar moment2) throws java.rmi.RemoteException;
    public PackageOfString queryInvoice2(java.lang.String cipher, java.lang.String species, int start, int _final) throws java.rmi.RemoteException;
    public PackageOfString queryInvoice3(java.lang.String cipher, java.util.Calendar moment1, java.util.Calendar moment2, java.lang.String mark) throws java.rmi.RemoteException;
    public PackageOfString queryInvoice4(java.lang.String cipher, java.lang.String species, int start, int _final, java.lang.String mark) throws java.rmi.RemoteException;
    public PackageOfInt32 getManifestStatus(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException;
}

class ServiceReceiveSoapStub extends org.apache.axis.client.Stub implements ServiceReceiveSoap {
    private java.util.Vector cachedSerClasses = new java.util.Vector();
    private java.util.Vector cachedSerQNames = new java.util.Vector();
    private java.util.Vector cachedSerFactories = new java.util.Vector();
    private java.util.Vector cachedDeserFactories = new java.util.Vector();

    static org.apache.axis.description.OperationDesc [] _operations;

    static {
        

        _operations = new org.apache.axis.description.OperationDesc[28];
        _initOperationDesc1();
        _initOperationDesc2();
        _initOperationDesc3();
    }

    private static void _initOperationDesc1(){
        

        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ImportManifest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "intent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "wharf"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "course"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "signal"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "vessel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "voyage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "harbor"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "devoir"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "layer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "content"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ImportManifestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[0] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AppendManifest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "intent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "wharf"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "course"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "signal"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "vessel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "voyage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "harbor"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "devoir"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "AppendManifestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[1] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyManifest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "intent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "wharf"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "course"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "boolean"), boolean.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "signal"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "vessel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "voyage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "harbor"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "devoir"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ModifyManifestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[2] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeleteManifest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "DeleteManifestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[3] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ImportFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "manifest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "sheet"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "layer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "goods"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cargo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "weight"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "rebate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "transit"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "content"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ImportFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[4] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AppendFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "manifest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "sheet"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "layer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "goods"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cargo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "weight"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "rebate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "transit"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "content"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "AppendFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[5] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ModifyFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "sheet"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "layer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "goods"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cargo"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "weight"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "rebate"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "transit"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "content"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ModifyFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[6] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("DeleteFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "DeleteFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[7] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ChoiceManifest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "intent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "wharf"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "signal"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "vessel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "voyage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChoiceManifestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[8] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ChoiceFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "intent"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "wharf"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "signal"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "vessel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "voyage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "sheet"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "layer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChoiceFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[9] = oper;

    }

    private static void _initOperationDesc2(){
        

        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CalculateFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CalculateFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[10] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ChargeFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "revenue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChargeFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[11] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CancelChargeFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CancelChargeFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[12] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CalculateCheckCode");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "species"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "numeral"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "vessel"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "voyage"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "revenue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CalculateCheckCodeResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[13] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("IssueChargeFreight1");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "species"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "numeral"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "balance"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "revenue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "IssueChargeFreight1Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[14] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("IssueChargeFreight2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "codes"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ArrayOfString"), java.lang.String[].class, false, false);
        param.setItemQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "string"));
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "species"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "numeral"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "balance"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "revenue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "IssueChargeFreight2Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[15] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("UpdateInvoice");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "species"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "numeral"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "balance"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "revenue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "UpdateInvoiceResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[16] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CancelInvoice");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "species"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "numeral"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CancelInvoiceResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[17] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("RecreateInvoice");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "oldSpecies"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "oldNumeral"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "newSpecies"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "newNumeral"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "payer"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "balance"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createTime"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "revenue"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"), java.math.BigDecimal.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "RecreateInvoiceResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[18] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CensorManifest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "censorCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "censorName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CensorManifestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[19] = oper;

    }

    private static void _initOperationDesc3(){
        

        org.apache.axis.description.OperationDesc oper;
        org.apache.axis.description.ParameterDesc param;
        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("AffirmManifest");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "affirmCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "affirmName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        oper.setReturnClass(GeneralPackage.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "AffirmManifestResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[20] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("ChangeFreight");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "freight"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "manifest"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "sheet"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChangeFreightResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[21] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("CreateDeposit");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createCode"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "createName"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "remark"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfDecimal"));
        oper.setReturnClass(PackageOfDecimal.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CreateDepositResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[22] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("QueryInvoice1");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice1Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[23] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("QueryInvoice2");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "species"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "start"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "final"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice2Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[24] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("QueryInvoice3");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment1"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "moment2"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "dateTime"), java.util.Calendar.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "mark"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice3Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[25] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("QueryInvoice4");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "species"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "start"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "final"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"), int.class, false, false);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "mark"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        oper.setReturnClass(PackageOfString.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice4Result"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[26] = oper;

        oper = new org.apache.axis.description.OperationDesc();
        oper.setName("GetManifestStatus");
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "cipher"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        param = new org.apache.axis.description.ParameterDesc(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "code"), org.apache.axis.description.ParameterDesc.IN, new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"), java.lang.String.class, false, false);
        //param.setOmittable(true);
        oper.addParameter(param);
        oper.setReturnType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfInt32"));
        oper.setReturnClass(PackageOfInt32.class);
        oper.setReturnQName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "GetManifestStatusResult"));
        oper.setStyle(org.apache.axis.constants.Style.WRAPPED);
        oper.setUse(org.apache.axis.constants.Use.LITERAL);
        _operations[27] = oper;

    }

    public ServiceReceiveSoapStub() throws org.apache.axis.AxisFault {
         this(null);
		 
    }

    public ServiceReceiveSoapStub(java.net.URL endpointURL, javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
         this(service);
		 
         super.cachedEndpoint = endpointURL;
    }

    public ServiceReceiveSoapStub(javax.xml.rpc.Service service) throws org.apache.axis.AxisFault {
        

        if (service == null) {
            

            super.service = new org.apache.axis.client.Service();
        } else {
            

            super.service = service;
        }
        ((org.apache.axis.client.Service)super.service).setTypeMappingVersion("1.2");
            java.lang.Class cls;
            javax.xml.namespace.QName qName;
            javax.xml.namespace.QName qName2;
            java.lang.Class beansf = org.apache.axis.encoding.ser.BeanSerializerFactory.class;
            java.lang.Class beandf = org.apache.axis.encoding.ser.BeanDeserializerFactory.class;
            java.lang.Class enumsf = org.apache.axis.encoding.ser.EnumSerializerFactory.class;
            java.lang.Class enumdf = org.apache.axis.encoding.ser.EnumDeserializerFactory.class;
            java.lang.Class arraysf = org.apache.axis.encoding.ser.ArraySerializerFactory.class;
            java.lang.Class arraydf = org.apache.axis.encoding.ser.ArrayDeserializerFactory.class;
            java.lang.Class simplesf = org.apache.axis.encoding.ser.SimpleSerializerFactory.class;
            java.lang.Class simpledf = org.apache.axis.encoding.ser.SimpleDeserializerFactory.class;
            java.lang.Class simplelistsf = org.apache.axis.encoding.ser.SimpleListSerializerFactory.class;
            java.lang.Class simplelistdf = org.apache.axis.encoding.ser.SimpleListDeserializerFactory.class;
            qName = new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ArrayOfString");
            cachedSerQNames.add(qName);
            cls = java.lang.String[].class;
            cachedSerClasses.add(cls);
            qName = new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string");
            qName2 = new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "string");
            cachedSerFactories.add(new org.apache.axis.encoding.ser.ArraySerializerFactory(qName, qName2));
            cachedDeserFactories.add(new org.apache.axis.encoding.ser.ArrayDeserializerFactory());

            qName = new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package");
            cachedSerQNames.add(qName);
            cls = GeneralPackage.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfDecimal");
            cachedSerQNames.add(qName);
            cls = PackageOfDecimal.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfInt32");
            cachedSerQNames.add(qName);
            cls = PackageOfInt32.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

            qName = new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString");
            cachedSerQNames.add(qName);
            cls = PackageOfString.class;
            cachedSerClasses.add(cls);
            cachedSerFactories.add(beansf);
            cachedDeserFactories.add(beandf);

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
                

                java.lang.String key = keys.nextElement();
                _call.setProperty(key, super.cachedProperties.get(key));
            }
            // All the type mapping information is registered
            // when the first call is made.
            // The type mapping information is actually registered in
            // the TypeMappingRegistry of the service, which
            // is the reason why registration is only needed for the first call.
            synchronized (this) {
                

                if (firstCall()) {
                    

                    // must set encoding style before registering serializers
                    _call.setEncodingStyle(null);
                    for (int i = 0; i < cachedSerFactories.size(); ++i) {
                        

                        java.lang.Class cls = cachedSerClasses.get(i);
                        javax.xml.namespace.QName qName =
                                (javax.xml.namespace.QName) cachedSerQNames.get(i);
                        java.lang.Object x = cachedSerFactories.get(i);
                        if (x instanceof Class) {
                            

                            java.lang.Class sf = cachedSerFactories.get(i);
                            java.lang.Class df = cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                        else if (x instanceof javax.xml.rpc.encoding.SerializerFactory) {
                            

                            org.apache.axis.encoding.SerializerFactory sf = cachedSerFactories.get(i);
                            org.apache.axis.encoding.DeserializerFactory df = cachedDeserFactories.get(i);
                            _call.registerTypeMapping(cls, qName, sf, df, false);
                        }
                    }
                }
            }
            return _call;
        }
        catch (java.lang.Throwable _t) {
            

            throw new org.apache.axis.AxisFault("Failure trying to get the Call object", _t);
        }
    }

    public PackageOfString importManifest(java.lang.String cipher, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir, int layer, java.lang.String content) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[0]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ImportManifest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ImportManifest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, new java.lang.Integer(intent), wharf, new java.lang.Boolean(course), signal, vessel, voyage, moment, harbor, devoir, new java.lang.Integer(layer), content] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString appendManifest(java.lang.String cipher, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[1]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/AppendManifest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "AppendManifest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, new java.lang.Integer(intent), wharf, new java.lang.Boolean(course), signal, vessel, voyage, moment, harbor, devoir] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage modifyManifest(java.lang.String cipher, java.lang.String code, int intent, java.lang.String wharf, boolean course, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.util.Calendar moment, java.lang.String harbor, java.lang.String devoir) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[2]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ModifyManifest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ModifyManifest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code, new java.lang.Integer(intent), wharf, new java.lang.Boolean(course), signal, vessel, voyage, moment, harbor, devoir] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage deleteManifest(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[3]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/DeleteManifest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "DeleteManifest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString importFreight(java.lang.String cipher, java.lang.String manifest, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[4]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ImportFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ImportFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, manifest, sheet, new java.lang.Integer(layer), payer, goods, cargo, new java.lang.Integer(weight), new java.lang.Integer(rebate), transit, content] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString appendFreight(java.lang.String cipher, java.lang.String manifest, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[5]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/AppendFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "AppendFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, manifest, sheet, new java.lang.Integer(layer), payer, goods, cargo, new java.lang.Integer(weight), new java.lang.Integer(rebate), transit, content] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage modifyFreight(java.lang.String cipher, java.lang.String code, java.lang.String sheet, int layer, java.lang.String payer, java.lang.String goods, java.lang.String cargo, int weight, int rebate, java.lang.String transit, java.lang.String content) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[6]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ModifyFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ModifyFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code, sheet, new java.lang.Integer(layer), payer, goods, cargo, new java.lang.Integer(weight), new java.lang.Integer(rebate), transit, content] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage deleteFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[7]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/DeleteFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "DeleteFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString choiceManifest(java.lang.String cipher, int intent, java.lang.String wharf, java.lang.String signal, java.lang.String vessel, java.lang.String voyage) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[8]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ChoiceManifest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChoiceManifest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, new java.lang.Integer(intent), wharf, signal, vessel, voyage] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString choiceFreight(java.lang.String cipher, int intent, java.lang.String wharf, java.lang.String signal, java.lang.String vessel, java.lang.String voyage, java.lang.String sheet, int layer) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[9]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ChoiceFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChoiceFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, new java.lang.Integer(intent), wharf, signal, vessel, voyage, sheet, new java.lang.Integer(layer)] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString calculateFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[10]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/CalculateFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CalculateFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage chargeFreight(java.lang.String cipher, java.lang.String code, java.math.BigDecimal revenue) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[11]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ChargeFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChargeFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code, revenue] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage cancelChargeFreight(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[12]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/CancelChargeFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CancelChargeFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString calculateCheckCode(java.lang.String cipher, java.lang.String species, int numeral, java.lang.String payer, java.lang.String vessel, java.lang.String voyage, java.math.BigDecimal revenue, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[13]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/CalculateCheckCode");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CalculateCheckCode"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, species, new java.lang.Integer(numeral), payer, vessel, voyage, revenue, createCode, createName, createTime] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage issueChargeFreight1(java.lang.String cipher, java.lang.String code, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[14]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/IssueChargeFreight1");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "IssueChargeFreight1"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code, species, new java.lang.Integer(numeral), payer, balance, createCode, createName, createTime, revenue] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage issueChargeFreight2(java.lang.String cipher, java.lang.String[] codes, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[15]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/IssueChargeFreight2");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "IssueChargeFreight2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, codes, species, new java.lang.Integer(numeral), payer, balance, createCode, createName, createTime, revenue] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage updateInvoice(java.lang.String cipher, java.lang.String code, java.lang.String species, int numeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[16]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/UpdateInvoice");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "UpdateInvoice"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code, species, new java.lang.Integer(numeral), payer, balance, createCode, createName, createTime, revenue] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage cancelInvoice(java.lang.String cipher, java.lang.String species, int numeral) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[17]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/CancelInvoice");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CancelInvoice"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, species, new java.lang.Integer(numeral)] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString recreateInvoice(java.lang.String cipher, java.lang.String oldSpecies, int oldNumeral, java.lang.String newSpecies, int newNumeral, java.lang.String payer, java.lang.String balance, java.lang.String createCode, java.lang.String createName, java.util.Calendar createTime, java.math.BigDecimal revenue) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[18]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/RecreateInvoice");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "RecreateInvoice"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, oldSpecies, new java.lang.Integer(oldNumeral), newSpecies, new java.lang.Integer(newNumeral), payer, balance, createCode, createName, createTime, revenue] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage censorManifest(java.lang.String cipher, java.lang.String code, java.lang.String censorCode, java.lang.String censorName) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[19]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/CensorManifest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CensorManifest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code, censorCode, censorName] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public GeneralPackage affirmManifest(java.lang.String cipher, java.lang.String code, java.lang.String affirmCode, java.lang.String affirmName) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[20]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/AffirmManifest");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "AffirmManifest"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code, affirmCode, affirmName] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (GeneralPackage) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (GeneralPackage) org.apache.axis.utils.JavaUtils.convert(_resp, GeneralPackage.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString changeFreight(java.lang.String cipher, java.lang.String freight, java.lang.String manifest, java.lang.String sheet) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[21]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/ChangeFreight");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "ChangeFreight"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, freight, manifest, sheet] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfDecimal createDeposit(java.lang.String cipher, java.util.Calendar moment, java.lang.String createCode, java.lang.String createName, java.lang.String remark) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[22]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/CreateDeposit");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "CreateDeposit"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, moment, createCode, createName, remark] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfDecimal) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfDecimal) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfDecimal.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString queryInvoice1(java.lang.String cipher, java.util.Calendar moment1, java.util.Calendar moment2) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[23]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/QueryInvoice1");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice1"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, moment1, moment2] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString queryInvoice2(java.lang.String cipher, java.lang.String species, int start, int _final) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[24]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/QueryInvoice2");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice2"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, species, new java.lang.Integer(start), new java.lang.Integer(_final)] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString queryInvoice3(java.lang.String cipher, java.util.Calendar moment1, java.util.Calendar moment2, java.lang.String mark) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[25]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/QueryInvoice3");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice3"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, moment1, moment2, mark] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfString queryInvoice4(java.lang.String cipher, java.lang.String species, int start, int _final, java.lang.String mark) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[26]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/QueryInvoice4");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "QueryInvoice4"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, species, new java.lang.Integer(start), new java.lang.Integer(_final), mark] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfString) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfString) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfString.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

    public PackageOfInt32 getManifestStatus(java.lang.String cipher, java.lang.String code) throws java.rmi.RemoteException {
        

        if (super.cachedEndpoint == null) {
            

            throw new org.apache.axis.NoEndPointException();
        }
        org.apache.axis.client.Call _call = createCall();
        _call.setOperation(_operations[27]);
        _call.setUseSOAPAction(true);
        _call.setSOAPActionURI("http://www.mainsoft.com.cn/pcd/harbor/receive/GetManifestStatus");
        _call.setEncodingStyle(null);
        _call.setProperty(org.apache.axis.client.Call.SEND_TYPE_ATTR, Boolean.FALSE);
        _call.setProperty(org.apache.axis.AxisEngine.PROP_DOMULTIREFS, Boolean.FALSE);
        _call.setSOAPVersion(org.apache.axis.soap.SOAPConstants.SOAP11_CONSTANTS);
        _call.setOperationName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "GetManifestStatus"));

        setRequestHeaders(_call);
        setAttachments(_call);
 try {
        
        java.lang.Object _resp = _call.invoke([cipher, code] as java.lang.Object[]);

        if (_resp instanceof java.rmi.RemoteException) {
            

            throw (java.rmi.RemoteException)_resp;
        }
        else {
            

            extractAttachments(_call);
            try {
                

                return (PackageOfInt32) _resp;
            } catch (java.lang.Exception _exception) {
                

                return (PackageOfInt32) org.apache.axis.utils.JavaUtils.convert(_resp, PackageOfInt32.class);
            }
        }
  } catch (org.apache.axis.AxisFault axisFaultException) {
  

  throw axisFaultException;
}
    }

}

class GeneralPackage  implements java.io.Serializable {
    private int failure;

    private java.lang.String message;

    public GeneralPackage() {
    

    }

    public GeneralPackage(
           int failure,
           java.lang.String message) {
           

           this.failure = failure;
           this.message = message;
    }


    /**
     * Gets the failure value for this GeneralPackage.
     * 
     * @return failure
     */
    public int getFailure() {
        

        return failure;
    }


    /**
     * Sets the failure value for this GeneralPackage.
     * 
     * @param failure
     */
    public void setFailure(int failure) {
        

        this.failure = failure;
    }


    /**
     * Gets the message value for this GeneralPackage.
     * 
     * @return message
     */
    public java.lang.String getMessage() {
        

        return message;
    }


    /**
     * Sets the message value for this GeneralPackage.
     * 
     * @param message
     */
    public void setMessage(java.lang.String message) {
        

        this.message = message;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        

        if (!(obj instanceof GeneralPackage)) return false;
        GeneralPackage other = obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            

            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = true && 
            this.failure == other.getFailure() &&
            ((this.message==null && other.getMessage()==null) || 
             (this.message!=null &&
              this.message.equals(other.getMessage())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        

        if (__hashCodeCalc) {
            

            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = 1;
        _hashCode += getFailure();
        if (getMessage() != null) {
            

            _hashCode += getMessage().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(GeneralPackage.class, true);

    static {
        

        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Package"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("failure");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Failure"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
        elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("message");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Message"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        

        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

class PackageOfDecimal  extends GeneralPackage  implements java.io.Serializable {
    private java.math.BigDecimal content;

    public PackageOfDecimal() {
    

    }

    public PackageOfDecimal(
           int failure,
           java.lang.String message,
           java.math.BigDecimal content) {
        super(
            failure,
            message);
		
        this.content = content;
    }


    /**
     * Gets the content value for this PackageOfDecimal.
     * 
     * @return content
     */
    public java.math.BigDecimal getContent() {
        

        return content;
    }


    /**
     * Sets the content value for this PackageOfDecimal.
     * 
     * @param content
     */
    public void setContent(java.math.BigDecimal content) {
        

        this.content = content;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        

        if (!(obj instanceof PackageOfDecimal)) return false;
        PackageOfDecimal other = obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            

            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.content==null && other.getContent()==null) || 
             (this.content!=null &&
              this.content.equals(other.getContent())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        

        if (__hashCodeCalc) {
            

            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getContent() != null) {
            

            _hashCode += getContent().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PackageOfDecimal.class, true);

    static {
        

        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfDecimal"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("content");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Content"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "decimal"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        

        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

class PackageOfInt32  extends GeneralPackage  implements java.io.Serializable {
    private int content;

    public PackageOfInt32() {
    

    }

    public PackageOfInt32(
           int failure,
           java.lang.String message,
           int content) {
        super(
            failure,
            message);
		
        this.content = content;
    }


    /**
     * Gets the content value for this PackageOfInt32.
     * 
     * @return content
     */
    public int getContent() {
        

        return content;
    }


    /**
     * Sets the content value for this PackageOfInt32.
     * 
     * @param content
     */
    public void setContent(int content) {
        

        this.content = content;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        

        if (!(obj instanceof PackageOfInt32)) return false;
        PackageOfInt32 other = obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            

            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            this.content == other.getContent();
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        

        if (__hashCodeCalc) {
            

            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        _hashCode += getContent();
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PackageOfInt32.class, true);

    static {
        

        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfInt32"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("content");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Content"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "int"));
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        

        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}

class PackageOfString  extends GeneralPackage  implements java.io.Serializable {
    private java.lang.String content;

    public PackageOfString() {
    

    }

    public PackageOfString(
           int failure,
           java.lang.String message,
           java.lang.String content) {
        super(
            failure,
            message);
		
        this.content = content;
    }


    /**
     * Gets the content value for this PackageOfString.
     * 
     * @return content
     */
    public java.lang.String getContent() {
        

        return content;
    }


    /**
     * Sets the content value for this PackageOfString.
     * 
     * @param content
     */
    public void setContent(java.lang.String content) {
        

        this.content = content;
    }

    private java.lang.Object __equalsCalc = null;
    public synchronized boolean equals(java.lang.Object obj) {
        

        if (!(obj instanceof PackageOfString)) return false;
        PackageOfString other = obj;
        if (obj == null) return false;
        if (this == obj) return true;
        if (__equalsCalc != null) {
            

            return (__equalsCalc == obj);
        }
        __equalsCalc = obj;
        boolean _equals;
        _equals = super.equals(obj) && 
            ((this.content==null && other.getContent()==null) || 
             (this.content!=null &&
              this.content.equals(other.getContent())));
        __equalsCalc = null;
        return _equals;
    }

    private boolean __hashCodeCalc = false;
    public synchronized int hashCode() {
        

        if (__hashCodeCalc) {
            

            return 0;
        }
        __hashCodeCalc = true;
        int _hashCode = super.hashCode();
        if (getContent() != null) {
            

            _hashCode += getContent().hashCode();
        }
        __hashCodeCalc = false;
        return _hashCode;
    }

    // Type metadata
    private static org.apache.axis.description.TypeDesc typeDesc =
        new org.apache.axis.description.TypeDesc(PackageOfString.class, true);

    static {
        

        typeDesc.setXmlType(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "PackageOfString"));
        org.apache.axis.description.ElementDesc elemField = new org.apache.axis.description.ElementDesc();
        elemField.setFieldName("content");
        elemField.setXmlName(new javax.xml.namespace.QName("http://www.mainsoft.com.cn/pcd/harbor/receive/", "Content"));
        elemField.setXmlType(new javax.xml.namespace.QName("http://www.w3.org/2001/XMLSchema", "string"));
        elemField.setMinOccurs(0);
        elemField.setNillable(false);
        typeDesc.addFieldDesc(elemField);
    }

    /**
     * Return type metadata object
     */
    public static org.apache.axis.description.TypeDesc getTypeDesc() {
        

        return typeDesc;
    }

    /**
     * Get Custom Serializer
     */
    public static org.apache.axis.encoding.Serializer getSerializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanSerializer(
            _javaType, _xmlType, typeDesc);
    }

    /**
     * Get Custom Deserializer
     */
    public static org.apache.axis.encoding.Deserializer getDeserializer(
           java.lang.String mechType, 
           java.lang.Class _javaType,  
           javax.xml.namespace.QName _xmlType) {
        

        return 
          new  org.apache.axis.encoding.ser.BeanDeserializer(
            _javaType, _xmlType, typeDesc);
    }

}