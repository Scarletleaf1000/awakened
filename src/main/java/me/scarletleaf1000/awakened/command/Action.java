package me.scarletleaf1000.awakened.command;

/**
 * A modular action that runs when a {@link Command}'s trigger and condition pass.
 */
public interface Action extends TieredEntry {
    void execute(CommandContext ctx);
}
