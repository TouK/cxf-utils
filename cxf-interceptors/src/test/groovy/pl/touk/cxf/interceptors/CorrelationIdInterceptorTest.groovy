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
        new ServerFactoryBean(
            features: [
                new CorrelationIdFeature(),
                new LoggingFeature()
            ],
            serviceBean: new TestWebServiceSoapImpl(),
            address: wsAddress
        ).create()
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
        return new ClientProxyFactoryBean(
            serviceClass: TestWebServiceSoap,
            address: wsAddress,
            features: [new LoggingFeature()]
        ).create(TestWebServiceSoap)
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
            proxy.requestContext.put(Message.PROTOCOL_HEADERS, headers)
        when:
            CorrelationIdResponse response = (CorrelationIdResponse) proxy.invoke('testRequest', testRequest).first()

        then:
            correlationId == response.correlationId
    }

    def 'should use custom correlation id name'() {
        given:
            CorrelationIdFeature correlationIdFeature = new CorrelationIdFeature(
                httpHeaderCorrelationIdName: 'X-CUSTOM-CORRELATION-ID',
                mdcCorrelationIdName: 'customCorrelationId'
            )

            new ServerFactoryBean(
                features: [
                    correlationIdFeature,
                    new LoggingFeature()
                ],
                serviceBean: new CustomCorrelationIdTestWebServiceSoapImpl(),
                address: 'http://localhost:9002/test'
            ).create()

            TestWebServiceSoap webService = new ClientProxyFactoryBean(
                serviceClass: TestWebServiceSoap,
                address: 'http://localhost:9002/test',
                features: [new LoggingFeature()]
            ).create(TestWebServiceSoap)

            String correlationId = 'abc123'

            Client proxy = ClientProxy.getClient(webService)
            Map<String, List<String>> headers = ['X-CUSTOM-CORRELATION-ID': [correlationId]]
            proxy.requestContext.put(Message.PROTOCOL_HEADERS, headers)
        when:
            CorrelationIdResponse response = (CorrelationIdResponse) proxy.invoke('testRequest', testRequest).first()
        then:
            response.correlationId == correlationId
    }
}
