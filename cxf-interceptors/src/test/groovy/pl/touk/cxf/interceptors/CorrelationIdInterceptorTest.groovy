package pl.touk.cxf.interceptors

import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.frontend.ClientProxyFactoryBean
import org.apache.cxf.frontend.ServerFactoryBean
import org.apache.cxf.message.Message
import pl.touk.cxf.interceptors.logging.LoggingFeature
import pl.touk.cxf_interceptors.CorrelationIdResponse
import pl.touk.cxf_interceptors.TestRequest
import pl.touk.cxf_interceptors.TestWebServiceSoap
import spock.lang.Specification

class CorrelationIdInterceptorTest extends Specification {

    static TestRequest testRequest = new TestRequest(request: 'testRequest')
    static String wsAddress = 'http://localhost:9001/test'
    static TestWebServiceSoap webService

    def setupSpec() {
        ServerFactoryBean serverFactoryBean = new ServerFactoryBean()
        serverFactoryBean.setFeatures([new CorrelationIdFeature(), new LoggingFeature()])
        serverFactoryBean.setServiceBean(new TestWebServiceSoapImpl())
        serverFactoryBean.setAddress(wsAddress)
        serverFactoryBean.create()

        ClientProxyFactoryBean clientProxyFactoryBean = new ClientProxyFactoryBean()
        clientProxyFactoryBean.setServiceClass(TestWebServiceSoap.class)
        clientProxyFactoryBean.setAddress(wsAddress)
        clientProxyFactoryBean.setFeatures([new LoggingFeature()])
        webService = clientProxyFactoryBean.create(TestWebServiceSoap.class)
    }

    def 'should add correlation id to log'() {
        when:
            CorrelationIdResponse response = webService.testRequest(testRequest)
        then:
            !response.correlationId.isEmpty()
    }

    def 'should add different correlation id for each ws request'() {
        when:
            CorrelationIdResponse firstResponse = webService.testRequest(testRequest)
            CorrelationIdResponse secondResponse = webService.testRequest(testRequest)
        then:
            firstResponse.correlationId != secondResponse.correlationId
    }

    def 'should use correlation id from X-CORRELATION-ID header if it is present'() {
        given:
            String correlationId = '12345678'

            Client proxy = ClientProxy.getClient(webService)
            Map<String, List<String>> headers = ['X-CORRELATION-ID': [correlationId]]
            proxy.getRequestContext().put(Message.PROTOCOL_HEADERS, headers)
        when:
            CorrelationIdResponse response = (CorrelationIdResponse) proxy.invoke('testRequest', testRequest).first()

        then:
            correlationId == response.correlationId
    }
}
