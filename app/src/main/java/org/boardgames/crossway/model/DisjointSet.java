package org.boardgames.crossway.model;

import java.util.*;

/**
 * An implementation of the Union-Find (also known as Disjoint Set Union) data structure
 * with transactional rollback capabilities.
 *
 * <p>This implementation is designed to support undo operations by recording every
 * structural change to the set. When a {@code rollback()} is performed, the recorded
 * changes are reversed in LIFO (last-in, first-out) order to restore the state to
 * the last {@code checkpoint()}.</p>
 *
 * <p>Key features:</p>
 * <ul>
 * <li><b>Union-by-size:</b> Merges the smaller tree into the larger one to keep trees
 * relatively balanced, which improves performance.</li>
 * <li><b>No Path Compression:</b> Path compression is intentionally omitted because it
 * modifies parent pointers in a way that is difficult and inefficient to roll back.
 * This ensures the transactional integrity of the data structure.</li>
 * <li><b>Checkpointing:</b> Allows the user to save a specific state and later revert
 * to it. Multiple checkpoints can be created and undone in sequence.</li>
 * </ul>
 *
 * @param <T> The type of elements stored in the disjoint set.
 * @author Gabriele Pintus
 */
public final class DisjointSet<T> {

    /**
     * Maps each element to its parent in the set.
     */
    private final Map<T, T> parent = new HashMap<>();

    /**
     * Maps each root element to the size of its set.
     */
    private final Map<T, Integer> size = new HashMap<>();

    /**
     * A private static nested class representing a single change to the data structure.
     * Each change records the information needed to undo a mutation.
     *
     * @param <T> The type of elements in the set.
     */
    private static final class Change<T> {
        final T child;
        final T oldParent;
        final T root;
        final Integer oldSize;
        final boolean newNode;

        /**
         * Constructs a new {@code Change} instance.
         *
         * @param child The element whose parent is being modified.
         * @param oldParent The previous parent of the child.
         * @param root The root of the set that grew in size during a union.
         * @param oldSize The size of the root before the union.
         * @param newNode A flag indicating if this change was the creation of a new set.
         */
        Change(T child, T oldParent, T root, Integer oldSize, boolean newNode) {
            this.child = child;
            this.oldParent = oldParent;
            this.root = root;
            this.oldSize = oldSize;
            this.newNode = newNode;
        }
    }

    /**
     * A stack to store all changes made to the data structure.
     */
    private final Deque<Change<T>> changes = new ArrayDeque<>();

    /**
     * A stack to store the number of changes at each checkpoint.
     */
    private final Deque<Integer> checkpoints = new ArrayDeque<>();

    /**
     * Clears all elements and resets the disjoint set to an empty state.
     */
    public void clear() {
        parent.clear();
        size.clear();
        changes.clear();
        checkpoints.clear();
    }

    /**
     * Saves the current state of the disjoint set.
     * A checkpoint is essentially a marker that allows for a later rollback to this point.
     * The number of changes made so far is pushed onto the checkpoints stack.
     */
    public void checkpoint() {
        checkpoints.push(changes.size());
    }

    /**
     * Restores the disjoint set to the state of the last saved checkpoint.
     * All changes that occurred after the last checkpoint are undone by
     * iterating through the {@code changes} stack and reversing each mutation.
     * If no checkpoint exists, this method does nothing.
     */
    public void rollback() {
        if (checkpoints.isEmpty()) {
            return;
        }

        int targetChangeCount = checkpoints.pop();
        while (changes.size() > targetChangeCount) {
            Change<T> change = changes.pop();
            if (change.newNode) {
                // If the change was adding a new node, remove it.
                parent.remove(change.child);
                size.remove(change.child);
            } else {
                // If the change was a union, restore the parent and root size.
                parent.put(change.child, change.oldParent);
                if (change.root != null && change.oldSize != null) {
                    size.put(change.root, change.oldSize);
                }
            }
        }
    }

    /**
     * Checks whether the disjoint set contains the specified element.
     *
     * @param x The element to check for.
     * @return {@code true} if the element exists in the set, {@code false} otherwise.
     */
    public boolean contains(T x) {
        return parent.containsKey(x);
    }

    /**
     * Creates a new singleton set containing the specified element.
     * If the element already exists, no action is taken.
     *
     * @param x The element to add.
     */
    public void makeSet(T x) {
        if (parent.containsKey(x)) {
            return;
        }
        parent.put(x, x);
        size.put(x, 1);
        changes.push(new Change<>(x, null, null, null, true));
    }

    /**
     * Finds the root representative of the set containing the specified element.
     * This method does not perform path compression to preserve the rollback capability.
     *
     * @param x The element to find the root for.
     * @return The root element of the set containing {@code x}.
     */
    private T findRoot(T x) {
        T p = parent.get(x);
        while (p != null && !p.equals(x)) {
            x = p;
            p = parent.get(x);
        }
        return x;
    }

    /**
     * Unites the sets containing the two specified elements.
     * This operation uses the union-by-size heuristic. It attaches the root
     * of the smaller set to the root of the larger set.
     *
     * @param a The first element.
     * @param b The second element.
     * @return {@code true} if a union occurred (i.e., the elements were in different sets),
     * {@code false} if they were already connected.
     */
    public boolean union(T a, T b) {
        if (!parent.containsKey(a) || !parent.containsKey(b)) {
            return false;
        }
        T rootA = findRoot(a);
        T rootB = findRoot(b);
        if (rootA.equals(rootB)) {
            return false;
        }

        int sizeA = size.get(rootA);
        int sizeB = size.get(rootB);

        // Ensure rootA is the larger set for consistent union-by-size.
        if (sizeA < sizeB) {
            T tempRoot = rootA;
            rootA = rootB;
            rootB = tempRoot;

            int tempSize = sizeA;
            sizeA = sizeB;
            sizeB = tempSize;
        }

        // Record the change BEFORE performing the mutation.
        changes.push(new Change<>(rootB, parent.get(rootB), rootA, sizeA, false));

        // Perform the union.
        parent.put(rootB, rootA);
        size.put(rootA, sizeA + sizeB);

        return true;
    }

    /**
     * Checks whether two elements belong to the same set.
     *
     * @param a The first element.
     * @param b The second element.
     * @return {@code true} if both elements share the same root, {@code false} otherwise.
     */
    public boolean connected(T a, T b) {
        if (!parent.containsKey(a) || !parent.containsKey(b)) {
            return false;
        }
        return findRoot(a).equals(findRoot(b));
    }
}

