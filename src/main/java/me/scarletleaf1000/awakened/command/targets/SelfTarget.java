package me.scarletleaf1000.awakened.command.targets;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Target;
import net.minecraft.network.chat.Component;

public class SelfTarget implements Target {
    @Override
    public Component getDisplayName() {
        return Component.translatable("target.awakened.self");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("target.awakened.self.description");
    }

    @Override
    public boolean select(CommandContext ctx) {
        ctx.setTarget(ctx.getHost());
        return true;
    }
}
