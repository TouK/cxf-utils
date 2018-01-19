package pl.touk.cxf.interceptors;

import lombok.Data;

@Data
class CorrelationContext {
    private String mdcCorrelationIdName = "correlationId";
    private String httpHeaderCorrelationIdName = "X-CORRELATION-ID";
}
