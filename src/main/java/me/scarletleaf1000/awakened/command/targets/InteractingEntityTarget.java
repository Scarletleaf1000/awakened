package me.scarletleaf1000.awakened.command.targets;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Target;
import net.minecraft.network.chat.Component;

public class InteractingEntityTarget implements Target {
    @Override
    public Component getDisplayName() {
        return Component.literal("Interacting Entity");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Targets the entity that right-clicked the host.");
    }

    @Override
    public boolean select(CommandContext ctx) {
        if (ctx.getInteractedBy() == null) {
            return false;
        }
        ctx.setTarget(ctx.getInteractedBy());
        return true;
    }
}
