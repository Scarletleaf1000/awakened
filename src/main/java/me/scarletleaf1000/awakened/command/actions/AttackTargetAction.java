package me.scarletleaf1000.awakened.command.actions;

import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.CommandContext;

/**
 * Starter action: set the host's attack target and perform a melee attack.
 * Full AI/goal wiring is out of scope for this chunk.
 */
public class AttackTargetAction implements Action {
    @Override
    public void execute(CommandContext ctx) {
        if (ctx.getTarget() != null) {
            // TODO: set attack target on host and swing
        }
    }
}
