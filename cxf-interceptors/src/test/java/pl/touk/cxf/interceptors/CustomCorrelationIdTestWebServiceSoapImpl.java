package pl.touk.cxf.interceptors;

import org.slf4j.MDC;
import pl.touk.cxf_interceptors.ObjectFactory;
import pl.touk.cxf_interceptors.TestRequest;
import pl.touk.cxf_interceptors.TestResponse;
import pl.touk.cxf_interceptors.TestWebServiceSoap;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;

@WebService(targetNamespace = "http://cxf-interceptors.touk.pl", name = "testWebServiceSoap")
@XmlSeeAlso({ObjectFactory.class})
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
public class CustomCorrelationIdTestWebServiceSoapImpl implements TestWebServiceSoap {

    private static final String CORRELATION_ID_KEY = "customCorrelationId";

    @WebMethod(action = "http://cxf-interceptors.touk.pl/test")
    @WebResult(name = "testResponse", targetNamespace = "http://cxf-interceptors.touk.pl", partName = "testResponse")
    public TestResponse test(
            @WebParam(partName = "parameters", name = "testRequest", targetNamespace = "http://cxf-interceptors.touk.pl") TestRequest parameters) {
        TestResponse response = new TestResponse();
        response.setCorrelationId(MDC.get(CORRELATION_ID_KEY));
        response.setThreadName(Thread.currentThread().getName());
        return response;
    }
}
