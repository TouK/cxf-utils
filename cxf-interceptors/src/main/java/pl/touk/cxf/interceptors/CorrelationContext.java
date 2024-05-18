package pl.touk.cxf.interceptors;

import lombok.Data;
import org.slf4j.MDC;
import pl.touk.cxf.interceptors.correlation.ExternalCorrelationIdProvider;

import java.util.Optional;

@Data
class CorrelationContext {
    private String mdcCorrelationIdName = "correlationId";
    private String httpHeaderCorrelationIdName = "X-CORRELATION-ID";
    private ExternalCorrelationIdProvider correlationIdProvider;
}
