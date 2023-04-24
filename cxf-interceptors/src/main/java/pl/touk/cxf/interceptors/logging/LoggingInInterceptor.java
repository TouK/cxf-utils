package pl.touk.cxf.interceptors.logging;

import org.apache.cxf.phase.Phase;

import java.util.Optional;
import java.util.logging.Logger;

class LoggingInInterceptor extends org.apache.cxf.interceptor.LoggingInInterceptor {
    private final Logger logger;


    LoggingInInterceptor(int limit) {
        this(limit, null);
    }

    LoggingInInterceptor(int limit, Logger logger) {
        super(LoggingInInterceptor.class.getName(), Phase.PRE_STREAM);
        setLimit(limit);
        this.logger = logger;
    }

    @Override
    protected Logger getLogger() {
        return Optional.ofNullable(logger).orElseGet(() -> super.getLogger());
    }
}
