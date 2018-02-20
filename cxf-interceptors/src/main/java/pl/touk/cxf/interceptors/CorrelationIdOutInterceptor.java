package pl.touk.cxf.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.MDC;

@Slf4j
public class CorrelationIdOutInterceptor extends AbstractPhaseInterceptor<Message> {
    private final CorrelationContext context;

    public CorrelationIdOutInterceptor(CorrelationContext context) {
        super(Phase.SETUP_ENDING);
        this.context = context;
    }

    public void handleMessage(Message message) throws Fault {
        log.debug("removing {}", context.getMdcCorrelationIdName());
        MDC.remove(context.getMdcCorrelationIdName());
    }
}
