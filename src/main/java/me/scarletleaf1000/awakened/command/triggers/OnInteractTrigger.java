package me.scarletleaf1000.awakened.command.triggers;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Trigger;
import net.minecraft.network.chat.Component;

public class OnInteractTrigger implements Trigger {
    @Override
    public Component getDisplayName() {
        return Component.literal("On Interact");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Activates when the host is right-clicked by an entity.");
    }

    @Override
    public boolean check(CommandContext ctx) {
        return ctx.getInteractedBy() != null;
    }
}
