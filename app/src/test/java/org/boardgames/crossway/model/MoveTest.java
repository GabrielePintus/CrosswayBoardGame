package org.boardgames.crossway.model;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
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
}