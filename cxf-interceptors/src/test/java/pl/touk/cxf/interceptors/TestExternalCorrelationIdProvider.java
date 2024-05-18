package pl.touk.cxf.interceptors;

import lombok.Setter;
import org.slf4j.MDC;
import pl.touk.cxf.interceptors.correlation.ExternalCorrelationIdProvider;

public class TestExternalCorrelationIdProvider implements ExternalCorrelationIdProvider {
    public static final String CORRELATION_ID = "EXTERNAL_CORRELATION_ID";
    @Setter
    private String currentCorrelationId = "abc";

    @Override
    public void start(String correlationId) {
        if (correlationId == null) {
            MDC.put("EXTERNAL_CORRELATION_ID", currentCorrelationId);
        } else {
            MDC.put("EXTERNAL_CORRELATION_ID", correlationId);
        }
    }

    @Override
    public void finish() {
        MDC.remove(CORRELATION_ID);
    }
}
