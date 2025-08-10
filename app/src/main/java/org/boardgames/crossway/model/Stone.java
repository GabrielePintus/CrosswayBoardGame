package org.boardgames.crossway.model;

/**
 * Enum representing the two types of stones used in the game,
 * along with a helper for switching turns.
 */
public enum Stone implements Exportable {
    BLACK,
    WHITE;

    /**
     * Returns the opposite stone type.
     *
     * @return the other stone type
     */
    public Stone opposite() {
        return this == BLACK ? WHITE : BLACK;
    }

    public String encode() {
        return this.name();
    }
}
