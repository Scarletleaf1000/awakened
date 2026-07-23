package me.scarletleaf1000.awakened.command.targets;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Target;
import net.minecraft.network.chat.Component;

public class NoTarget implements Target {
    @Override
    public Component getDisplayName() {
        return Component.empty();
    }

    @Override
    public Component getDescription() {
        return Component.literal("No target is used for this command.");
    }

    @Override
    public boolean select(CommandContext ctx) {
        return true;
    }
}
