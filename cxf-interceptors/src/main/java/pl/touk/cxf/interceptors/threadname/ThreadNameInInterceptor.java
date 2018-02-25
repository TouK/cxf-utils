package pl.touk.cxf.interceptors.threadname;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

@Slf4j
public class ThreadNameInInterceptor extends AbstractPhaseInterceptor<Message> {

    private final ThreadNameContext context;

    public ThreadNameInInterceptor(ThreadNameContext context) {
        super(Phase.RECEIVE);
        this.context = context;
    }

    public void handleMessage(Message message) throws Fault {
        String oldThreadName = Thread.currentThread().getName();
        String newThreadName = context.getThreadNamePolicy().apply(oldThreadName);
        log.debug("Setting new thread name {}", newThreadName);
        Thread.currentThread().setName(newThreadName);
        message.getExchange().put(context.getOldNameContextProperty(), oldThreadName);
    }
}
