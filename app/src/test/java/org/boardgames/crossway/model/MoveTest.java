package org.boardgames.crossway.model;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MoveTest {

    @Test
    @DisplayName("Constructor validates non-null fields")
    void constructorGuards() {
        assertThrows(IllegalArgumentException.class, () -> new Move(null, Stone.BLACK));
        assertThrows(IllegalArgumentException.class, () -> new Move(new Point(1,2), null));
    }

    @Test
    @DisplayName("toJson produces expected structure")
    void toJsonStructure() {
        Move m = new Move(new Point(3, 5), Stone.BLACK);
        String json = m.toJson();
        // Minimal structural checks
        assertTrue(json.contains("\"point\""));
        assertTrue(json.contains("\"x\":3"));
        assertTrue(json.contains("\"y\":5"));
        assertTrue(json.contains("\"stone\":\"BLACK\""));
    }

    @Test
    @DisplayName("fromJson parses valid JSON")
    void fromJsonValid() {
        String json = "{\"point\":{\"x\":3,\"y\":5},\"stone\":\"WHITE\"}";
        Move m = Move.fromJson(json);
        assertEquals(new Point(3,5), m.getPoint());
        assertEquals(Stone.WHITE, m.getStone());
    }

    @Test
    @DisplayName("Round-trip: Move -> JSON -> Move")
    void roundTrip() {
        Move original = new Move(new Point(1, 2), Stone.BLACK);
        Move parsed = Move.fromJson(original.toJson());
        assertEquals(original, parsed, "Points should match");
    }

    @Test
    @DisplayName("fromJson rejects invalid payloads")
    void fromJsonInvalid() {
        // Missing stone
        String noStone = "{\"point\":{\"x\":3,\"y\":5}}";
        assertThrows(IllegalArgumentException.class, () -> Move.fromJson(noStone));

        // Missing point
        String noPoint = "{\"stone\":\"BLACK\"}";
        assertThrows(IllegalArgumentException.class, () -> Move.fromJson(noPoint));

        // Bad types
        String badTypes = "{\"point\":\"(x=3,y=5)\",\"stone\":123}";
        assertThrows(IllegalArgumentException.class, () -> Move.fromJson(badTypes));
    }
}