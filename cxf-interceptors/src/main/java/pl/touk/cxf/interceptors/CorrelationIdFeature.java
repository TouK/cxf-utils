package pl.touk.cxf.interceptors;

import lombok.Data;
import lombok.experimental.Delegate;
import org.apache.cxf.Bus;
import org.apache.cxf.annotations.Provider;
import org.apache.cxf.feature.AbstractFeature;
import org.apache.cxf.interceptor.InterceptorProvider;

@Provider(value = Provider.Type.Feature)
@Data
public class CorrelationIdFeature extends AbstractFeature {
    @Delegate
    private final CorrelationContext context = new CorrelationContext();

    private final CorrelationIdInInterceptor in = new CorrelationIdInInterceptor(context);
    private final CorrelationIdOutInterceptor out = new CorrelationIdOutInterceptor(context);

    @Override
    protected void initializeProvider(InterceptorProvider provider, Bus bus) {
        super.initializeProvider(provider, bus);
        provider.getInInterceptors().add(in);
        provider.getInFaultInterceptors().add(in);
        provider.getOutInterceptors().add(out);
        provider.getOutFaultInterceptors().add(out);
    }
}
