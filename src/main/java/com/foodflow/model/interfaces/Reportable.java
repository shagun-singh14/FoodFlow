package com.foodflow.model.interfaces;

import java.util.Map;

/**
 * Interface contract for entities capable of contributing summary metrics to analytical reports.
 */
public interface Reportable {
    String getReportIdentifier();
    Map<String, Object> generateMetricsMap();
}
