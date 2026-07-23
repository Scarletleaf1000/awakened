package me.scarletleaf1000.awakened.command;

import java.util.Set;

/**
 * A modular trigger that decides when a {@link Command} should fire.
 */
public interface Trigger extends TieredEntry {
    boolean check(CommandContext ctx);

    /**
     * Returns which action types this trigger can be paired with.
     */
    Set<ActionType> getSupportedActionTypes();
}
