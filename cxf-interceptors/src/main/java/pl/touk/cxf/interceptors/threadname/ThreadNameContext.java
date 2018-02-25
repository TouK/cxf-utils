package pl.touk.cxf.interceptors.threadname;

import lombok.Data;

@Data
public class ThreadNameContext {
    private String threadNamePrefix = "cxf-";
    private String oldNameContextProperty = "oldThreadName";
}