package me.scarletleaf1000.awakened.command.triggers;

import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Trigger;
import net.minecraft.network.chat.Component;

public class PassiveTrigger implements Trigger {
    @Override
    public Component getDisplayName() {
        return Component.literal("Passive");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Always activates when the command is used.");
    }

    @Override
    public boolean check(CommandContext ctx) {
        return true;
    }
}
