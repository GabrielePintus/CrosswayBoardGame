package org.boardgames.crossway.model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Point class.
 */
public class PointTest {

    @Test
    @DisplayName("Test if a point is a neighbour of itself")
    public void testIsNeighbourOfItself() {
        Point point1 = new Point(2, 3);
        Point point2 = new Point(2, 3);
        assertTrue(point1.isNeighbourOf(point2), "A point should be a neighbour of itself");
    }
    @Test
    @DisplayName("Test if a point is a neighbour of an adjacent point")
    public void testIsNeighbourOfAdjacentPoint() {
        Point point = new Point(1, 1);
        Point nn = new Point(1, 2); // North
        Point ne = new Point(2, 2); // North-East
        Point ee = new Point(2, 1); // East
        Point se = new Point(2, 0); // South-East
        Point ss = new Point(1, 0); // South
        Point sw = new Point(0, 0); // South-West
        Point ww = new Point(0, 1); // West
        Point nw = new Point(0, 2); // North-West
        assertTrue(point.isNeighbourOf(nn), "Point should be a neighbour of North point");
        assertTrue(point.isNeighbourOf(ne), "Point should be a neighbour of North-East point");
        assertTrue(point.isNeighbourOf(ee), "Point should be a neighbour of East point");
        assertTrue(point.isNeighbourOf(se), "Point should be a neighbour of South-East point");
        assertTrue(point.isNeighbourOf(ss), "Point should be a neighbour of South point");
        assertTrue(point.isNeighbourOf(sw), "Point should be a neighbour of South-West point");
        assertTrue(point.isNeighbourOf(ww), "Point should be a neighbour of West point");
        assertTrue(point.isNeighbourOf(nw), "Point should be a neighbour of North-West point");
    }

    @Test
    @DisplayName("toJson: encodes as (x=?,y=?)")
    void toJsonEncodesCustomFormat() {
        Point p1 = new Point(3, 5);
        Point p2 = new Point(-12, 63214);
        String testString1 = "{\"x\":3,\"y\":5}";
        String testString2 = "{\"x\":-12,\"y\":63214}";
        assertEquals(testString1, p1.toJson());
        assertEquals(testString2, p2.toJson());
        assertNotEquals(testString2, p1.toJson());
    }

    @Test
    @DisplayName("Round-trip: toJson -> fromString")
    void roundTripCustomFormat() {
        Point p1 = new Point(3, 5);
        Point p2 = new Point(-12, 63214);
        String json1 = p1.toJson();
        String json2 = p2.toJson();
        Point p1FromJson = Point.fromJson(json1);
        Point p2FromJson = Point.fromJson(json2);
        assertEquals(p1, p1FromJson, "Point should match after serialization and deserialization");
        assertEquals(p2, p2FromJson, "Point should match after serialization and deserialization");
    }
}