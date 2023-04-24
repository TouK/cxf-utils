package pl.touk.cxf.interceptors.logging;

import java.io.PrintWriter;
import java.util.Optional;
import java.util.logging.Logger;

public class LoggingOutInterceptor extends org.apache.cxf.interceptor.LoggingOutInterceptor {
    private final Logger logger;

    public LoggingOutInterceptor(String phase, Logger logger) {
        super(phase);
        this.logger = logger;
    }

    public LoggingOutInterceptor(Logger logger) {
        this.logger = logger;
    }

    public LoggingOutInterceptor(int lim, Logger logger) {
        super(lim);
        this.logger = logger;
    }

    public LoggingOutInterceptor(PrintWriter w, Logger logger) {
        super(w);
        this.logger = logger;
    }

    @Override
    protected Logger getLogger() {
        return Optional.ofNullable(logger).orElseGet(() -> super.getLogger());
    }
}
