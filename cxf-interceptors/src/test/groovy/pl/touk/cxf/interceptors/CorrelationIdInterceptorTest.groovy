package pl.touk.cxf.interceptors

import org.apache.cxf.endpoint.Client
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.frontend.ClientProxyFactoryBean
import org.apache.cxf.frontend.ServerFactoryBean
import org.apache.cxf.message.Message
import org.apache.log4j.BasicConfigurator
import pl.touk.cxf.interceptors.logging.LoggingFeature
import pl.touk.cxf_interceptors.CorrelationIdResponse
import pl.touk.cxf_interceptors.TestRequest
import pl.touk.cxf_interceptors.TestWebServiceSoap
import spock.lang.Shared
import spock.lang.Specification

class CorrelationIdInterceptorTest extends Specification {

    @Shared
    TestRequest testRequest = new TestRequest(request: 'testRequest')
    @Shared
    TestWebServiceSoap webService
    @Shared
    String wsAddress = 'http://localhost:9001/test'

    def setupSpec() {
        BasicConfigurator.configure();
        startServer()
        webService = createClient()
    }

    private void startServer() {
        ServerFactoryBean serverFactoryBean = new ServerFactoryBean()
        serverFactoryBean.features = [new CorrelationIdFeature(), new LoggingFeature()]
        serverFactoryBean.serviceBean = new TestWebServiceSoapImpl()
        serverFactoryBean.address = wsAddress
        serverFactoryBean.create()
    }

    private TestWebServiceSoap createClient() {
        ClientProxyFactoryBean clientProxyFactoryBean = new ClientProxyFactoryBean()
        clientProxyFactoryBean.serviceClass = TestWebServiceSoap
        clientProxyFactoryBean.address = wsAddress
        clientProxyFactoryBean.features = [new LoggingFeature()]
        return clientProxyFactoryBean.create(TestWebServiceSoap)
    }

    def 'should add correlation id to log'() {
        when:
            CorrelationIdResponse response = webService.testRequest(testRequest)
        then:
            !response.correlationId.empty
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
            proxy.requestContext.put(Message.PROTOCOL_HEADERS, headers)
        when:
            CorrelationIdResponse response = (CorrelationIdResponse) proxy.invoke('testRequest', testRequest).first()
        then:
            response.correlationId == correlationId
    }
}
