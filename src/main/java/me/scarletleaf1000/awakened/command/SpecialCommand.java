package me.scarletleaf1000.awakened.command;

import net.minecraft.network.chat.Component;

public enum SpecialCommand {
    MY_BREATH_TO_YOURS("gui.awakened.special.my_breath_to_yours"),
    DESTROY_EVIL("gui.awakened.special.destroy_evil");

    private final String translationKey;

    SpecialCommand(String translationKey) {
        this.translationKey = translationKey;
    }

    public Component getDisplayName() {
        return Component.translatable(translationKey);
    }
}
