package org.boardgames.crossway.model;


/**
 * Represents a violation of a pattern rule on the board.
 *
 * @param ruleName the name of the violated rule
 * @param message a descriptive message about the violation
 * @param at the point on the board where the violation occurred
 */
public record PatternViolation(String ruleName, String message, Point at) { }