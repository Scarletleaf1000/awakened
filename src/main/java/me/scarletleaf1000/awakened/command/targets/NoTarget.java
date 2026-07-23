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
        return Component.translatable("target.awakened.no_target.description");
    }

    @Override
    public boolean select(CommandContext ctx) {
        return true;
    }
}
