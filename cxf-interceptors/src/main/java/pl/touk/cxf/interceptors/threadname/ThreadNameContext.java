package pl.touk.cxf.interceptors.threadname;

import lombok.Data;
import pl.touk.cxf.interceptors.threadname.policy.ThreadNamePrefixPolicy;

@Data
public class ThreadNameContext {
    private ThreadNamePolicy threadNamePolicy = new ThreadNamePrefixPolicy();
    private String oldNameContextProperty = "oldThreadName";
}