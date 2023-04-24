package pl.touk.cxf.interceptors.logging;

import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Logging;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.InterceptorProvider;

import java.util.logging.Logger;

/**
 * Class is based on {@link org.apache.cxf.feature.LoggingFeature}
 * (https://github.com/apache/cxf/blob/master/core/src/main/java/org/apache/cxf/feature/LoggingFeature.java)
 */
@NoJSR250Annotations
@Provider(value = Provider.Type.Feature)
public class LoggingFeature extends AbstractFeature {
    private static final int DEFAULT_LIMIT = AbstractLoggingInterceptor.DEFAULT_LIMIT;
    private final LoggingInInterceptor IN;
    private final LoggingOutInterceptor OUT;

    private String inLocation;
    private String outLocation;
    private boolean prettyLogging;
    private boolean showBinary;
    private final Logger logger;

    private int limit = DEFAULT_LIMIT;

    public LoggingFeature() {
        this((String) null);
    }

    public LoggingFeature(String loggerName) {
        this(loggerName, -1);
    }

    public LoggingFeature(String loggerName, int lim) {
        this.logger = Logger.getLogger(loggerName);
        limit = lim;
        this.IN = new LoggingInInterceptor(lim, logger);
        this.OUT = new LoggingOutInterceptor(lim, logger);
    }

    public LoggingFeature(Logging annotation) {
        inLocation = annotation.inLocation();
        outLocation = annotation.outLocation();
        limit = annotation.limit();
        prettyLogging = annotation.pretty();
        showBinary = annotation.showBinary();
        this.logger = null;
        this.IN = new LoggingInInterceptor(-1);
        this.OUT = new LoggingOutInterceptor(-1, null);
    }

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        if (limit == DEFAULT_LIMIT && inLocation == null
                && outLocation == null && !prettyLogging) {
            provider.getInInterceptors().add(IN);
            provider.getInFaultInterceptors().add(IN);
            provider.getOutInterceptors().add(OUT);
            provider.getOutFaultInterceptors().add(OUT);
        } else {
            LoggingInInterceptor in = new LoggingInInterceptor(limit, logger);
            in.setOutputLocation(inLocation);
            in.setPrettyLogging(prettyLogging);
            in.setShowBinaryContent(showBinary);
            LoggingOutInterceptor out = new LoggingOutInterceptor(limit, logger);
            out.setOutputLocation(outLocation);
            out.setPrettyLogging(prettyLogging);
            out.setShowBinaryContent(showBinary);

            provider.getInInterceptors().add(in);
            provider.getInFaultInterceptors().add(in);
            provider.getOutInterceptors().add(out);
            provider.getOutFaultInterceptors().add(out);
        }
    }

    /**
     * This function has no effect at this time.
     * @param lim
     */
    public void setLimit(int lim) {
        limit = lim;
    }

    /**
     * Retrieve the value set with {@link #setLimit(int)}.
     */
    public int getLimit() {
        return limit;
    }

    public boolean isPrettyLogging() {
        return prettyLogging;
    }
    /**
     * Turn pretty logging of XML content on/off
     * @param prettyLogging
     */
    public void setPrettyLogging(boolean prettyLogging) {
        this.prettyLogging = prettyLogging;
    }
}
