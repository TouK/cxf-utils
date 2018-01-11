package pl.touk.cxf.interceptors.logging;

import org.apache.cxf.phase.Phase;

class LoggingInInterceptor extends org.apache.cxf.interceptor.LoggingInInterceptor {
    LoggingInInterceptor(int limit) {
        super(LoggingInInterceptor.class.getName(), Phase.PRE_STREAM);
        setLimit(limit);
    }
}
