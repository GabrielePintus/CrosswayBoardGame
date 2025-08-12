package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class HistoryTest {

    @Test
    @DisplayName("Build History from JSON with extra fields")
    void fromJsonValid() {
        // Extra fields ignored; missing lists -> empty
        String json1 = "{\n" +
                "    \"pastMoves\" : [\n" +
                "        {\n" +
                "            \"point\": {\n" +
                "                \"x\": 5,\n" +
                "                \"y\": 3\n" +
                "            },\n" +
                "            \"stone\": \"WHITE\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"point\": {\n" +
                "                \"x\": 2,\n" +
                "                \"y\": 1\n" +
                "            },\n" +
                "            \"stone\": \"BLACK\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"futureMoves\" : [\n" +
                "        {\n" +
                "            \"point\": {\n" +
                "                \"x\": 2,\n" +
                "                \"y\": 2\n" +
                "            },\n" +
                "            \"stone\": \"WHITE\"\n" +
                "        }\n" +
                "    ]\n" +
                "}".strip();
        History history = History.fromJson(json1);
        // Check past moves
        assertEquals(2, history.getPastMoves().size(), "Should have 2 past moves");
        assertEquals(new Move(new Point(5, 3), Stone.WHITE), history.getPastMoves().get(0), "First past move should match");
        assertEquals(new Move(new Point(2, 1), Stone.BLACK), history.getPastMoves().get(1), "Second past move should match");
        // Check future moves
        assertEquals(1, history.getFutureMoves().size(), "Should have 1 future move");
        assertEquals(new Move(new Point(2, 2), Stone.WHITE), history.getFutureMoves().get(0), "Future move should match");
    }

    @Test
    @DisplayName("Build History from JSON with empty lists")
    void fromJsonEmptyLists() {
        // Empty past and future moves
        String json2 = "{\n" +
                "    \"pastMoves\" : [],\n" +
                "    \"futureMoves\" : []\n" +
                "}".strip();
        History history = History.fromJson(json2);
        // Check past moves
        assertTrue(history.getPastMoves().isEmpty(), "Past moves should be empty");
        // Check future moves
        assertTrue(history.getFutureMoves().isEmpty(), "Future moves should be empty");
    }
}
