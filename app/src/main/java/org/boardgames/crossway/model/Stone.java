package org.boardgames.crossway.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;

/**
 * Enum representing the two types of stones used in the game,
 * along with a helper for switching turns.
 */
@JsonIgnoreProperties(ignoreUnknown = true)          // forward compatibility
@JsonInclude(JsonInclude.Include.NON_NULL)           // cleaner output
public enum Stone implements Exportable {
    BLACK,
    WHITE;

    private static final ObjectMapper MAPPER =
            new ObjectMapper()
                    .configure(FAIL_ON_UNKNOWN_PROPERTIES, false)
                    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS, true);


    @JsonProperty("stoneName")
    public String getStoneName() {
        return this.name();
    }

    public Stone opposite() {
        return this == BLACK ? WHITE : BLACK;
    }

    public String toJson() {
        try {
            return MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Failed to serialize Stone", e);
        }
    }

    public static Stone fromJson(String json) {
        try {
            return MAPPER.readValue(json, Stone.class);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Invalid JSON for Stone", e);
        }
    }
}