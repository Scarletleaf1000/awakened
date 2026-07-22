package me.scarletleaf1000.awakened.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;

public class AwakeningBuildState {
    private final EnumMap<AwakeningComponentType, ResourceLocation> selections = new EnumMap<>(AwakeningComponentType.class);

    public void set(AwakeningComponentType type, ResourceLocation id) {
        selections.put(type, id);
    }

    public ResourceLocation get(AwakeningComponentType type) {
        return selections.get(type);
    }

    public boolean isComplete() {
        for (AwakeningComponentType type : AwakeningComponentType.values()) {
            if (!selections.containsKey(type)) {
                return false;
            }
        }
        return true;
    }

    public Component getSummary() {
        StringBuilder builder = new StringBuilder();
        for (AwakeningComponentType type : AwakeningComponentType.values()) {
            ResourceLocation id = selections.get(type);
            String value = id == null ? "None" : id.getPath();
            if (builder.length() > 0) {
                builder.append(" | ");
            }
            builder.append(type.displayName()).append(": ").append(value);
        }
        return Component.literal(builder.toString());
    }
}
