package org.boardgames.crossway.model;


public record PatternViolation(String ruleName, String message, Point at) { }