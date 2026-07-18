package me.scarletleaf1000.awakened.command.actions;

import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.CommandContext;

/**
 * Starter action: pathfind toward the entity set as the command's target.
 * Full owner-tracking and AI wiring is out of scope for this chunk.
 */
public class FollowOwnerAction implements Action {
    @Override
    public void execute(CommandContext ctx) {
        if (ctx.getTarget() != null) {
            // TODO: pathfind toward ctx.getTarget()
        }
    }
}
