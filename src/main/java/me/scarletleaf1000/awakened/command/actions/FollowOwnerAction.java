package me.scarletleaf1000.awakened.command.actions;

import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandContext;
import net.minecraft.network.chat.Component;

public class FollowOwnerAction implements Action {
    @Override
    public ActionType getActionType() {
        return ActionType.ENTITY;
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("Follow Target");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Orders the host to follow the selected target.");
    }

    @Override
    public void execute(CommandContext ctx) {
        if (ctx.getTarget() != null) {
            // TODO: pathfind toward ctx.getTarget()
        }
    }
}
