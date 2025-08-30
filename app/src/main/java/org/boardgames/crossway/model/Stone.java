package org.boardgames.crossway.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Enum representing the two types of stones used in the game, Black and White.
 * This enum also provides utility methods for serialization and for switching turns.
 *
 * @author Gabriele Pintus
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public enum Stone {
    /**
     * Represents the Black and White stone.
     */
    BLACK, WHITE;

    /**
     * Gets the name of the stone as a string.
     * This method is annotated with {@code @JsonProperty} to control how the enum is
     * serialized into a JSON object, ensuring the key is "stoneName".
     *
     * @return The name of the stone (e.g., "BLACK", "WHITE").
     */
    @JsonProperty("stoneName")
    public String getStoneName() {
        return this.name();
    }

    /**
     * Returns the opposite stone color.
     * This is useful for switching turns between players.
     *
     * @return The opposite stone (e.g., if the current stone is BLACK, this returns WHITE).
     */
    public Stone opposite() {
        return this == BLACK ? WHITE : BLACK;
    }
}

