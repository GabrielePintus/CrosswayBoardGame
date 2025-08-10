package org.boardgames.crossway.model;

import java.util.*;

/**
 * Union-Find (Disjoint Set Union) with rollback.
 * - Supports checkpoint() and rollback() to the last checkpoint in O(changes).
 * - Uses union-by-size. Do NOT use path compression (breaks rollback).
 */
public final class DisjointSet<T> {

    private final Map<T, T> parent = new HashMap<>();
    private final Map<T, Integer> size = new HashMap<>();

    private static final class Change<T> {
        final T child;        // the root that got attached (or new node)
        final T oldParent;    // previous parent (== itself if it was a root)
        final T root;         // the root that grew in union (null for new node)
        final Integer oldSize;// previous size of 'root' before union (null for new node)
        final boolean newNode;
        Change(T child, T oldParent, T root, Integer oldSize, boolean newNode) {
            this.child = child; this.oldParent = oldParent; this.root = root;
            this.oldSize = oldSize; this.newNode = newNode;
        }
    }

    private final Deque<Change<T>> changes = new ArrayDeque<>();
    private final Deque<Integer> checkpoints = new ArrayDeque<>();

    public void clear() {
        parent.clear();
        size.clear();
        changes.clear();
        checkpoints.clear();
    }

    public void checkpoint() {
        checkpoints.push(changes.size());
    }

    public void rollback() {
        if (checkpoints.isEmpty()) return;
        int target = checkpoints.pop();
        while (changes.size() > target) {
            Change<T> c = changes.pop();
            if (c.newNode) {
                parent.remove(c.child);
                size.remove(c.child);
            } else {
                // detach the child and restore size of root
                parent.put(c.child, c.oldParent);
                if (c.root != null && c.oldSize != null) {
                    size.put(c.root, c.oldSize);
                }
            }
        }
    }

    public boolean contains(T x) { return parent.containsKey(x); }

    public void makeSet(T x) {
        if (parent.containsKey(x)) return;
        parent.put(x, x);
        size.put(x, 1);
        changes.push(new Change<>(x, null, null, null, true));
    }

    private T findRoot(T x) {
        T p = parent.get(x);
        while (p != null && !p.equals(x)) {
            x = p;
            p = parent.get(x);
        }
        return x;
    }

    public boolean union(T a, T b) {
        if (!parent.containsKey(a) || !parent.containsKey(b)) return false;
        T ra = findRoot(a), rb = findRoot(b);
        if (ra.equals(rb)) return false;

        int sa = size.get(ra), sb = size.get(rb);
        // attach smaller under larger
        if (sa < sb) { T tmp = ra; ra = rb; rb = tmp; int ts = sa; sa = sb; sb = ts; }

        // record change BEFORE mutating
        changes.push(new Change<>(rb, parent.get(rb), ra, sa, false));
        parent.put(rb, ra);
        size.put(ra, sa + sb);
        return true;
    }

    public boolean connected(T a, T b) {
        if (!parent.containsKey(a) || !parent.containsKey(b)) return false;
        return findRoot(a).equals(findRoot(b));
    }
}
