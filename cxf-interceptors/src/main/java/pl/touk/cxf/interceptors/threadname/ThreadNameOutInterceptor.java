package pl.touk.cxf.interceptors.threadname;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

@Slf4j
public class ThreadNameOutInterceptor extends AbstractPhaseInterceptor<Message> {
    private final ThreadNameContext context;

    public ThreadNameOutInterceptor(ThreadNameContext context) {
        super(Phase.SETUP_ENDING);
        this.context = context;
    }

    public void handleMessage(Message message) throws Fault {
        String oldThreadName = (String) message.getExchange().get(context.getOldNameContextProperty());
        log.debug("Setting old thread name {} for thread {}", oldThreadName, Thread.currentThread().getName());
        Thread.currentThread().setName(oldThreadName);
    }
}
