package me.scarletleaf1000.awakened.command;

/**
 * Thrown when a requested Command combination cannot be built, e.g. unknown registry ID
 * or required tier above what is available.
 */
public class CommandBuildException extends RuntimeException {
    public CommandBuildException(String message) {
        super(message);
    }
}
