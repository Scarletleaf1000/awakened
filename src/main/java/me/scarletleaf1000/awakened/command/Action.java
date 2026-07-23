package me.scarletleaf1000.awakened.command;

/**
 * A modular action that runs when a {@link Command}'s trigger and condition pass.
 */
public interface Action extends TieredEntry {
    void execute(CommandContext ctx);

    /**
     * Returns true if this action should be stored as NBT on the item the player is holding,
     * keeping the item in their inventory.
     */
    default boolean appliesToItem() {
        return false;
    }

    /**
     * Returns true if this action makes use of a target selection.
     */
    default boolean usesTarget() {
        return true;
    }

}
