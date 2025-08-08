package org.boardgames.crossway.model;

import java.util.HashMap;
import java.util.Map;

/**
 * Union-Find (Disjoint Set Union) structure with path compression.
 */
public class DisjointSet<T> {
    private final Map<T, T> parent = new HashMap<>();

    /**
     * Ensures the item is registered in the structure.
     */
    public void makeSet(T item) {
        parent.putIfAbsent(item, item);
    }

    /**
     * Finds the representative of the set containing item.
     */
    public T find(T item) {
        T p = parent.get(item);
        if (!p.equals(item)) {
            T root = find(p);
            parent.put(item, root);
            return root;
        }
        return p;
    }

    /**
     * Unions the sets containing a and b.
     */
    public void union(T a, T b) {
        makeSet(a);
        makeSet(b);
        T rootA = find(a);
        T rootB = find(b);
        if (!rootA.equals(rootB)) {
            parent.put(rootA, rootB);
        }
    }

    /**
     * Checks if two items are in the same set.
     */
    public boolean connected(T a, T b) {
        if (!parent.containsKey(a) || !parent.containsKey(b)) return false;
        return find(a).equals(find(b));
    }
}
