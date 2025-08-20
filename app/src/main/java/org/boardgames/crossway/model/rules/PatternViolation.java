package org.boardgames.crossway.model.rules;

import org.boardgames.crossway.model.Point;

/**
 * An immutable record representing a violation of a game rule.
 *
 * <p>This record encapsulates all the necessary information about a rule violation,
 * making it a clear and concise way to communicate why a move is illegal.
 * It is used by the {@link PatternRule} implementations to describe an
 * infringement and is caught by the game's controller to provide feedback to the player.</p>
 *
 * @param ruleName The programmatic name of the violated rule (e.g., "DiagonalXRule").
 * @param message  A descriptive, human-readable message about the violation (e.g., "A 2x2 diagonal X pattern is forbidden").
 * @param at       The {@link Point} on the board where the violation occurred. This is typically the location of the illegal move.
 * @author Gabriele Pintus
 */
public record PatternViolation(String ruleName, String message, Point at) { }