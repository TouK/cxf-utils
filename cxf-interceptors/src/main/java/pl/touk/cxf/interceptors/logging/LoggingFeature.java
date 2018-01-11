package pl.touk.cxf.interceptors.logging;

import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Logging;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.common.injection.NoJSR250Annotations;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.AbstractLoggingInterceptor;
import org.apache.cxf.interceptor.InterceptorProvider;
import org.apache.cxf.interceptor.LoggingOutInterceptor;

/**
 * Class is based on {@link org.apache.cxf.feature.LoggingFeature}
 * (https://github.com/apache/cxf/blob/master/core/src/main/java/org/apache/cxf/feature/LoggingFeature.java)
 */
@NoJSR250Annotations
@Provider(value = Provider.Type.Feature)
public class LoggingFeature extends AbstractFeature {
    private static final int DEFAULT_LIMIT = AbstractLoggingInterceptor.DEFAULT_LIMIT;
    private final LoggingInInterceptor IN = new LoggingInInterceptor(-1);
    private final LoggingOutInterceptor OUT = new LoggingOutInterceptor(-1);

    private String inLocation;
    private String outLocation;
    private boolean prettyLogging;
    private boolean showBinary;

    private int limit = DEFAULT_LIMIT;

    public LoggingFeature() {

    }
    public LoggingFeature(int lim) {
        limit = lim;
    }
    public LoggingFeature(String in, String out) {
        inLocation = in;
        outLocation = out;
    }
    public LoggingFeature(String in, String out, int lim) {
        inLocation = in;
        outLocation = out;
        limit = lim;
    }

    public LoggingFeature(String in, String out, int lim, boolean p) {
        inLocation = in;
        outLocation = out;
        limit = lim;
        prettyLogging = p;
    }

    public LoggingFeature(String in, String out, int lim, boolean p, boolean showBinary) {
        this(in, out, lim, p);
        this.showBinary = showBinary;
    }

    public LoggingFeature(Logging annotation) {
        inLocation = annotation.inLocation();
        outLocation = annotation.outLocation();
        limit = annotation.limit();
        prettyLogging = annotation.pretty();
        showBinary = annotation.showBinary();
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
            LoggingInInterceptor in = new LoggingInInterceptor(limit);
            in.setOutputLocation(inLocation);
            in.setPrettyLogging(prettyLogging);
            in.setShowBinaryContent(showBinary);
            LoggingOutInterceptor out = new LoggingOutInterceptor(limit);
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

