package pl.touk.cxf.interceptors.threadname;

import lombok.experimental.Delegate;
import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;

@Provider(value = Provider.Type.Feature)
public class ThreadNameFeature extends AbstractFeature {
    @Delegate
    private final ThreadNameContext context;
    private final ThreadNameInInterceptor in;
    private final ThreadNameOutInterceptor out;

    public ThreadNameFeature(ThreadNameContext context) {
        this.context = context;
        in = new ThreadNameInInterceptor(this.context);
        out = new ThreadNameOutInterceptor(this.context);
    }

    public ThreadNameFeature() {
        this(new ThreadNameContext());
    }

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);
        provider.getInInterceptors().add(in);
        provider.getInFaultInterceptors().add(in);
        provider.getOutInterceptors().add(out);
        provider.getOutFaultInterceptors().add(out);
    }
}
