package me.scarletleaf1000.awakened.command.actions;

import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandContext;
import net.minecraft.network.chat.Component;

public class PlaceholderItemAction implements Action {
    private final String translationKey;
    private final int cost;
    private final int minHeightening;

    public PlaceholderItemAction(String translationKey, int cost, int minHeightening) {
        this.translationKey = translationKey;
        this.cost = cost;
        this.minHeightening = minHeightening;
    }

    @Override
    public ActionType getActionType() {
        return ActionType.ITEM;
    }

    @Override
    public boolean appliesToItem() {
        return true;
    }

    @Override
    public int cost() {
        return cost;
    }

    @Override
    public int minHeightening() {
        return minHeightening;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    @Override
    public Component getDescription() {
        return Component.translatable(translationKey + ".description");
    }

    @Override
    public void execute(CommandContext ctx) {
    }
}
