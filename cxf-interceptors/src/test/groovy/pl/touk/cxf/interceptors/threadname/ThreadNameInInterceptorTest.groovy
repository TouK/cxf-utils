package pl.touk.cxf.interceptors.threadname

import org.apache.cxf.message.Exchange
import org.apache.cxf.message.Message
import spock.lang.Specification

class ThreadNameInInterceptorTest extends Specification {
    Exchange exchange = Mock()
    Message message = Mock() {
        getExchange() >> exchange
    }
    ThreadNameContext context = new ThreadNameContext()
    ThreadNameInInterceptor sut = new ThreadNameInInterceptor(context)

    def 'should perform actions if request is not for WSDL'() {
        given:
            message.get(Message.QUERY_STRING) >> queryString
        when:
            sut.handleMessage(message)
        then:
            1 * exchange.put(context.getOldNameContextProperty(), _)
        where:
            queryString << ["", "wsdlWithSuffix", "prefixAndWsdl"]
    }

    def 'should not perform actions if request is for WSDL'() {
        given:
            message.get(Message.QUERY_STRING) >> queryString
        when:
            sut.handleMessage(message)
        then:
            0 * exchange.put(context.getOldNameContextProperty(), _)
        where:
            queryString << ["wsdl", "wsdl&other=value"]
    }
}
