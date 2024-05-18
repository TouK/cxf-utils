package pl.touk.cxf.interceptors.correlation;

import java.util.Optional;

/**
 * Enables pluggable correlation id, useful if you want to reuse existing correlation mechanism.
 */
public interface ExternalCorrelationIdProvider {
    /**
     * Starts correlation id process, optionally using given correlation id.
     *
     * @param correlationId known correlation id.
     */
    void start(String correlationId);

    /**
     * Finishes correlation id process.
     */
    void finish();
}
