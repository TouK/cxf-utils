package pl.touk.cxf.interceptors.threadname.policy;

import lombok.AllArgsConstructor;
import pl.touk.cxf.interceptors.threadname.ThreadNamePolicy;

@AllArgsConstructor
public class ThreadNamePrefixPolicy implements ThreadNamePolicy {

    private final String threadNamePrefix;

    public ThreadNamePrefixPolicy() {
        this("cxf-");
    }

    public String apply(String oldName) {
        return threadNamePrefix + oldName;
    }
}
