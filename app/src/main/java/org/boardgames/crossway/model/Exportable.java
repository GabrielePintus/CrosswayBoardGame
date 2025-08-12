package org.boardgames.crossway.model;

/**
 * Interface for objects that can be exported to a string representation.
 */

public interface Exportable {

    /**
     * Encodes this object into its string representation.
     *
     * @return a string representing the encoded state of this object
     */
    public String toJson();
}