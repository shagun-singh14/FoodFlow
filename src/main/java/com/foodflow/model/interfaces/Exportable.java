package com.foodflow.model.interfaces;

/**
 * Interface contract for domain models/reports that export structured content to byte and character streams.
 */
public interface Exportable {
    String toFormattedText();
    String toCsvRow();
    String getCsvHeader();
}
