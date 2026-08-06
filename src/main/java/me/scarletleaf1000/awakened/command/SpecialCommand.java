package me.scarletleaf1000.awakened.command;

import me.scarletleaf1000.awakened.heightening.Heightening;
import net.minecraft.network.chat.Component;

public enum SpecialCommand {
    MY_BREATH_TO_YOURS("gui.awakened.special.my_breath_to_yours", 0, 0),
    DESTROY_EVIL("gui.awakened.special.destroy_evil", 1000, Heightening.NINTH.ordinal());

    private final String translationKey;
    private final int cost;
    private final int minHeightening;

    SpecialCommand(String translationKey, int cost, int minHeightening) {
        this.translationKey = translationKey;
        this.cost = cost;
        this.minHeightening = minHeightening;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }

    public Component getDescription() {
        return Component.translatable(translationKey + ".description");
    }

    public int cost() {
        return cost;
    }

    public int minHeightening() {
        return minHeightening;
    }
}
