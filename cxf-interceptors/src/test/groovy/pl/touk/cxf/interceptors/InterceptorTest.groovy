package pl.touk.cxf.interceptors

import org.apache.cxf.endpoint.Client
import org.apache.cxf.endpoint.Server
import org.apache.cxf.frontend.ClientProxy
import org.apache.cxf.frontend.ClientProxyFactoryBean
import org.apache.cxf.frontend.ServerFactoryBean
import org.apache.cxf.message.Message
import org.apache.log4j.BasicConfigurator
import pl.touk.cxf.interceptors.logging.LoggingFeature
import pl.touk.cxf.interceptors.threadname.ThreadNameFeature
import pl.touk.cxf.interceptors.threadname.ThreadNamePolicy
import pl.touk.cxf_interceptors.TestRequest
import pl.touk.cxf_interceptors.TestResponse
import pl.touk.cxf_interceptors.TestWebServiceSoap
import spock.lang.AutoCleanup
import spock.lang.Shared
import spock.lang.Specification

class InterceptorTest extends Specification {
    private static final String logger = 'test.logger'

    @Shared
    TestRequest testRequest = new TestRequest(request: 'testRequest')
    @Shared
    TestWebServiceSoap defaultClient
    @Shared
    TestWebServiceSoap customClient
    @Shared
    TestWebServiceSoap externalCorrelationClient
    @Shared
    String defaultAddress = 'http://localhost:9001/test'
    @Shared
    String customAddress = 'http://localhost:9002/test'
    @Shared
    String externalCorrelationAddress = 'http://localhost:9003/test'

    @Shared
    TestExternalCorrelationIdProvider correlationIdProvider = new TestExternalCorrelationIdProvider()

    @Shared
    @AutoCleanup(value = 'stop')
    Server defaultServer

    @Shared
    @AutoCleanup(value = 'stop')
    Server customServer

    @Shared
    @AutoCleanup(value = 'stop')
    Server externalCorrelationServer

    def setupSpec() {
        BasicConfigurator.configure();
        defaultServer = startDefaultServer()
        defaultClient = createClient(defaultAddress)
        customServer = startCustomServer()
        customClient = createClient(customAddress)
        externalCorrelationServer = startExternalCorrelationServer()
        externalCorrelationClient = createClient(externalCorrelationAddress)
    }

    private Server startDefaultServer() {
        return new ServerFactoryBean(
            features: [
                new CorrelationIdFeature(),
                new LoggingFeature(logger),
                new ThreadNameFeature()
            ],
            serviceBean: new TestWebServiceSoapImpl(),
            address: defaultAddress,
        ).create()
    }

    private Server startCustomServer() {
        CorrelationIdFeature correlationIdFeature = new CorrelationIdFeature(
            httpHeaderCorrelationIdName: 'X-CUSTOM-CORRELATION-ID',
            mdcCorrelationIdName: 'customCorrelationId'
        )

        ThreadNameFeature threadNameFeature = new ThreadNameFeature(
            threadNamePolicy: new ThreadNamePolicy() {
                @Override
                String apply(String oldName) {
                    return oldName.replace('qtp', 'custom-')
                }
            }
        )
        return new ServerFactoryBean(
            features: [
                correlationIdFeature,
                new LoggingFeature(logger),
                threadNameFeature
            ],
            serviceBean: new CustomCorrelationIdTestWebServiceSoapImpl(),
            address: customAddress
        ).create()
    }

    private Server startExternalCorrelationServer() {
        CorrelationIdFeature correlationIdFeature = new CorrelationIdFeature(
            correlationIdProvider: correlationIdProvider
        )
        return new ServerFactoryBean(
            features: [
                correlationIdFeature
            ],
            serviceBean: new ExternalCorrelationIdTestWebServiceSoapImpl(),
            address: externalCorrelationAddress
        ).create()
    }

    private static TestWebServiceSoap createClient(String address) {
        return new ClientProxyFactoryBean(
            serviceClass: TestWebServiceSoap,
            address: address,
            features: [new LoggingFeature()]
        ).create(TestWebServiceSoap)
    }

    def 'should add correlation id to log'() {
        when:
            TestResponse response = defaultClient.test(testRequest)
        then:
            !response.correlationId.empty
    }

    def 'should add different correlation id for each ws request'() {
        when:
            TestResponse firstResponse = defaultClient.test(testRequest)
            TestResponse secondResponse = defaultClient.test(testRequest)
        then:
            firstResponse.correlationId != secondResponse.correlationId
    }

    def 'should use correlation id from X-CORRELATION-ID header if it is present'() {
        given:
            String correlationId = '12345678'
            Client proxy = ClientProxy.getClient(defaultClient)
            Map<String, List<String>> headers = ['X-CORRELATION-ID': [correlationId]]
            proxy.requestContext.put(Message.PROTOCOL_HEADERS, headers)
        when:
            TestResponse response = (TestResponse) proxy.invoke('test', testRequest).first()
        then:
            correlationId == response.correlationId
    }

    def 'should use custom correlation id name'() {
        given:
            String correlationId = 'abc123'
            Client proxy = ClientProxy.getClient(customClient)
            Map<String, List<String>> headers = ['X-CUSTOM-CORRELATION-ID': [correlationId]]
            proxy.requestContext.put(Message.PROTOCOL_HEADERS, headers)
        when:
            TestResponse response = (TestResponse) proxy.invoke('test', testRequest).first()
        then:
            response.correlationId == correlationId
    }

    def 'should use external correlation without given correlation id'() {
        given:
            correlationIdProvider.currentCorrelationId = 'qwe'
        when:
            TestResponse response = externalCorrelationClient.test(testRequest)
        then:
            response.correlationId == 'qwe'
    }

    def 'should use external correlation with given correlation id'() {
        given:
            Client proxy = ClientProxy.getClient(externalCorrelationClient)
            Map<String, List<String>> headers = ['X-CORRELATION-ID': ['xyz']]
            proxy.requestContext.put(Message.PROTOCOL_HEADERS, headers)
        when:
            TestResponse response = (TestResponse) proxy.invoke('test', testRequest).first()
        then:
            response.correlationId == 'xyz'
    }

    def 'should set thread name prefix for request processing'() {
        when:
            TestResponse response = defaultClient.test(testRequest)
        then:
            response.threadName.matches(/^cxf-qtp\d+.+$/)
    }

    def 'should set thread name with custom policy'() {
        when:
            TestResponse response = customClient.test(testRequest)
        then:
            response.threadName.matches(/^custom-\d+.+$/)
    }
}
