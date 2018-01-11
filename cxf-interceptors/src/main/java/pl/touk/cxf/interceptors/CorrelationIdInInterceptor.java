package pl.touk.cxf.interceptors;

import pl.touk.cxf.interceptors.correlation.Correlation;
import lombok.extern.slf4j.Slf4j;
import org.apache.cxf.helpers.CastUtils;
import org.apache.cxf.interceptor.Fault;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;
import org.slf4j.MDC;

import java.util.List;
import java.util.Map;

import static pl.touk.cxf.interceptors.CorrelationIdConstants.CORRELATION_ID;
import static pl.touk.cxf.interceptors.CorrelationIdConstants.CORRELATION_ID_HEADER;

@Slf4j
public class CorrelationIdInInterceptor extends AbstractPhaseInterceptor<Message> {
    public CorrelationIdInInterceptor() {
        super(Phase.RECEIVE);
    }

    public void handleMessage(Message message) throws Fault {
        String correlationIdFromMdc = MDC.get(CORRELATION_ID);
        String correlationIdFromHeaders = getCorrelationIdFromHeaders(message);
        if (correlationIdFromHeaders == null && correlationIdFromMdc == null) {
            addNewCorrelationId();
        } else if (correlationIdFromHeaders != null && correlationIdFromMdc != null) {
            chooseCorrelationIdWhenBothArePresent(correlationIdFromMdc, correlationIdFromHeaders);
        } else if (correlationIdFromMdc != null) {
            log.warn("using {} from MDC: {}", CORRELATION_ID, correlationIdFromMdc);
        } else {
            addCorrelationIdFromHeaders(correlationIdFromHeaders);
        }
    }

    private String getCorrelationIdFromHeaders(Message message) {
        Map<String, List<String>> headers = CastUtils.cast((Map<?, ?>) message.get(Message.PROTOCOL_HEADERS));
        if (headers != null) {
            List<String> correlationIds = headers.get(CORRELATION_ID_HEADER);
            if (correlationIds != null && correlationIds.size() == 1) {
                return correlationIds.get(0);
            }
        }
        log.debug("{} not found in headers", CORRELATION_ID_HEADER);
        return null;
    }

    private void addNewCorrelationId() {
        String correlationId = Correlation.generateNew();
        log.debug("{} not found in MDC nor headers, generated new one: {}", CORRELATION_ID, correlationId);
        MDC.put(CORRELATION_ID, correlationId);
    }

    private void chooseCorrelationIdWhenBothArePresent(String correlationIdFromMdc, String correlationIdFromHeaders) {
        if (!correlationIdFromHeaders.equals(correlationIdFromMdc)) {
            log.warn("{} from headers and MDC mismatch, from header={}, from MDC={}, choosing from headers",
                    CORRELATION_ID, correlationIdFromHeaders, correlationIdFromMdc);

            MDC.put(CORRELATION_ID, correlationIdFromHeaders);
        }
    }

    private void addCorrelationIdFromHeaders(String correlationIdFromHeaders) {
        MDC.put(CORRELATION_ID, correlationIdFromHeaders);
    }
}
