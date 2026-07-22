package me.scarletleaf1000.awakened.command;

/**
 * A modular target selector that supplies the entity an awakening command acts upon.
 */
public interface Target extends TieredEntry {
    boolean select(CommandContext ctx);
}
