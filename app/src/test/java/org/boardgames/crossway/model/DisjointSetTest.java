package org.boardgames.crossway.model;

import org.boardgames.crossway.model.DisjointSet;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

class DisjointSetTest {
    @Test
    void basicConnectivityAndMissingNodes() {
        DisjointSet<String> uf = new DisjointSet<>();
        uf.makeSet("a"); uf.makeSet("b"); uf.makeSet("c");
        uf.union("a","b");
        assertTrue(uf.connected("a","b"));
        assertFalse(uf.connected("a","c"));
        assertFalse(uf.connected("a","z"), "missing node should not be connected");
    }
}
