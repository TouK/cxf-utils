package pl.touk.cxf.interceptors;

import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.Phase;
import org.slf4j.MDC;
import pl.touk.cxf.interceptors.correlation.Correlation;

import java.util.List;
import java.util.Map;

@Slf4j
public class CorrelationIdInInterceptor extends NonWsdlAbstractPhaseInterceptor {
    private final CorrelationContext context;

    public CorrelationIdInInterceptor(CorrelationContext context) {
        super(Phase.RECEIVE);
        this.context = context;
    }

    @Override
    public void processMessage(Message message) throws Fault {
        String correlationIdFromHeaders = getCorrelationIdFromHeaders(message);
        if (context.getCorrelationIdProvider() != null) {
            context.getCorrelationIdProvider().start(correlationIdFromHeaders);
            return;
        }
        String correlationIdFromMdc = MDC.get(context.getMdcCorrelationIdName());
        if (correlationIdFromHeaders == null && correlationIdFromMdc == null) {
            addNewCorrelationId();
        } else if (correlationIdFromHeaders != null && correlationIdFromMdc != null) {
            chooseCorrelationIdWhenBothArePresent(correlationIdFromMdc, correlationIdFromHeaders);
        } else if (correlationIdFromMdc != null) {
            log.warn("using {} from MDC: {}", context.getMdcCorrelationIdName(), correlationIdFromMdc);
        } else {
            addCorrelationIdFromHeaders(correlationIdFromHeaders);
        }
    }

    private String getCorrelationIdFromHeaders(Message message) {
        Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
        if (headers != null) {
            List<String> correlationIds = headers.get(context.getHttpHeaderCorrelationIdName());
            if (correlationIds != null && correlationIds.size() == 1) {
                return correlationIds.get(0);
            }
        }
        log.debug("{} not found in headers", context.getHttpHeaderCorrelationIdName());
        return null;
    }

    private void addNewCorrelationId() {
        String correlationId = Correlation.generateNew();
        log.debug("{} not found in MDC nor headers, generated new one: {}", context.getMdcCorrelationIdName(), correlationId);
        MDC.put(context.getMdcCorrelationIdName(), correlationId);
    }

    private void chooseCorrelationIdWhenBothArePresent(String correlationIdFromMdc, String correlationIdFromHeaders) {
        if (!correlationIdFromHeaders.equals(correlationIdFromMdc)) {
            log.warn("{} from headers and MDC mismatch, from header={}, from MDC={}, choosing from headers",
                    context.getMdcCorrelationIdName(), correlationIdFromHeaders, correlationIdFromMdc);

            MDC.put(context.getMdcCorrelationIdName(), correlationIdFromHeaders);
        }
    }

    private void addCorrelationIdFromHeaders(String correlationIdFromHeaders) {
        MDC.put(context.getMdcCorrelationIdName(), correlationIdFromHeaders);
    }
}
