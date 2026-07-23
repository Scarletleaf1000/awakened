package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.command.SpecialCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;

public class AwakeningBuildState {
    private final EnumMap<AwakeningComponentType, ResourceLocation> selections = new EnumMap<>(AwakeningComponentType.class);
    private SpecialCommand specialCommand;

    public void set(AwakeningComponentType type, ResourceLocation id) {
        selections.put(type, id);
    }

    public ResourceLocation get(AwakeningComponentType type) {
        return selections.get(type);
    }

    public void setSpecialCommand(SpecialCommand specialCommand) {
        this.specialCommand = specialCommand;
    }

    public SpecialCommand getSpecialCommand() {
        return specialCommand;
    }

    public boolean isComplete() {
        return selections.containsKey(AwakeningComponentType.ACTION);
    }

    public Component getSummary() {
        MutableComponent summary = Component.empty();
        Component separator = Component.translatable("gui.awakened.command_builder.separator");
        boolean first = true;
        for (AwakeningComponentType type : AwakeningComponentType.values()) {
            ResourceLocation id = selections.get(type);
            Component value = id == null
                    ? Component.translatable("gui.awakened.command_builder.none")
                    : Component.translatable("gui.awakened.command_builder.unknown", id.getPath());
            if (!first) {
                summary.append(separator);
            }
            first = false;
            summary.append(Component.translatable("gui.awakened.command_builder.component_label", type.getDisplayName(), value));
        }
        return summary;
    }
}
