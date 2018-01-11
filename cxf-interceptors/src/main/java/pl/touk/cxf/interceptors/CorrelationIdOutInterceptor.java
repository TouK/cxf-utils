package pl.touk.cxf.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.MDC;

import static pl.touk.cxf.interceptors.CorrelationIdConstants.CORRELATION_ID;

@Slf4j
public class CorrelationIdOutInterceptor extends AbstractPhaseInterceptor<Message> {
    public CorrelationIdOutInterceptor() {
        super(Phase.PRE_PROTOCOL);
    }

    public void handleMessage(Message message) throws Fault {
        log.debug("removing {}", CORRELATION_ID);
        MDC.remove(CORRELATION_ID);
    }
}
