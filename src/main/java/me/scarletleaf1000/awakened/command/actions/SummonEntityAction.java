package me.scarletleaf1000.awakened.command.actions;

import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.CommandContext;
import net.minecraft.network.chat.Component;

public class SummonEntityAction implements Action {

    @Override
    public Component getDisplayName() {
        return Component.literal("Summon Entity");
    }

    @Override
    public Component getDescription() {
        return Component.literal("Transforms an item in your inventory into an awakened entity.");
    }

    @Override
    public int cost() {
        return 10;
    }

    @Override
    public boolean transformsToEntity() {
        return true;
    }

    @Override
    public void execute(CommandContext ctx) {
        // The actual entity spawn is handled by the command-use packet handler.
    }
}
