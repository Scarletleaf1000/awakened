package me.scarletleaf1000.awakened.command.triggers;

import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandContext;
import me.scarletleaf1000.awakened.command.Trigger;

import java.util.EnumSet;
import java.util.Set;
import net.minecraft.network.chat.Component;

public class PassiveTrigger implements Trigger {
    @Override
    public Set<ActionType> getSupportedActionTypes() {
        return EnumSet.allOf(ActionType.class);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("trigger.awakened.passive");
    }

    @Override
    public Component getDescription() {
        return Component.translatable("trigger.awakened.passive.description");
    }

    @Override
    public boolean check(CommandContext ctx) {
        return true;
    }
}
