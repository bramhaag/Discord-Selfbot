package me.bramhaag.discordselfbot.util;

/**
 * Thrown when breaking forEach loop, should be ignored.
 */
public class BreakException extends RuntimeException {

    public BreakException() {
        super("Broke loop!");
    }

}
