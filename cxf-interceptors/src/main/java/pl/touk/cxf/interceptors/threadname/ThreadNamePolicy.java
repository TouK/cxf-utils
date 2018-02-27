package pl.touk.cxf.interceptors.threadname;

public interface ThreadNamePolicy {
    String apply(String oldName);
}
