package me.scarletleaf1000.awakened.command;

/**
 * A modular trigger that decides when a {@link Command} should fire.
 */
public interface Trigger extends TieredEntry {
    boolean check(CommandContext ctx);
}
