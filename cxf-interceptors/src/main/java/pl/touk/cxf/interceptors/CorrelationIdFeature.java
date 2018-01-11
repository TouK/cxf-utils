package pl.touk.cxf.interceptors;

import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;

@Provider(value = Provider.Type.Feature)
public class CorrelationIdFeature extends AbstractFeature {
    private CorrelationIdInInterceptor in = new CorrelationIdInInterceptor();
    private CorrelationIdOutInterceptor out = new CorrelationIdOutInterceptor();

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);
        provider.getInInterceptors().add(in);
        provider.getInFaultInterceptors().add(in);
        provider.getOutInterceptors().add(out);
        provider.getOutFaultInterceptors().add(out);
    }
}
