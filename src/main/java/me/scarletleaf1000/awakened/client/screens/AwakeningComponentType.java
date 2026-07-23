package me.scarletleaf1000.awakened.client.screens;

import net.minecraft.network.chat.Component;

public enum AwakeningComponentType {
    TRIGGER("gui.awakened.component_type.trigger"),
    ACTION("gui.awakened.component_type.action"),
    TARGET("gui.awakened.component_type.target");

    private final String key;

    AwakeningComponentType(String key) {
        this.key = key;
    }

    public Component getDisplayName() {
        return Component.translatable(key);
    }

    public Component getDescription() {
        return Component.translatable(key + ".description");
    }
}
