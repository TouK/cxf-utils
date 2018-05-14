package pl.touk.cxf.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;

import java.util.Arrays;

@Slf4j
public abstract class NonWsdlAbstractPhaseInterceptor extends AbstractPhaseInterceptor<Message> {
    public NonWsdlAbstractPhaseInterceptor(String phase) {
        super(phase);
    }

    public abstract void processMessage(Message message);

    public void handleMessage(Message message) throws Fault {
        if (isWsdl(message)) {
            log.trace("wsdl message received so skipping interceptor");
            return;
        }
        processMessage(message);
    }

    private static boolean isWsdl(Message message) {
        String queryString = (String) message.get(Message.QUERY_STRING);
        return queryString != null && Arrays.stream(queryString.split("&")).anyMatch("wsdl"::equals);
    }
}
