package me.scarletleaf1000.awakened.command;

/**
 * A modular condition that filters whether a trigger should allow its action.
 * No concrete conditions are implemented yet; the slot is wired into {@link Command}
 * and {@link CommandBuilder} so adding one later is non-breaking.
 */
public interface Condition extends TieredEntry {
    boolean test(CommandContext ctx);
}
