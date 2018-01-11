package pl.touk.cxf.interceptors.correlation;

import lombok.experimental.UtilityClass;

import java.util.UUID;

@UtilityClass
public class Correlation {
    public static String generateNew() {
        return UUID.randomUUID().toString();
    }
}
