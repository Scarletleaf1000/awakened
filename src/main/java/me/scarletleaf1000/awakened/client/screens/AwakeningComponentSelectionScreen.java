package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.command.CommandBuilder;
import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.command.Target;
import me.scarletleaf1000.awakened.command.TieredEntry;
import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.Trigger;
import me.scarletleaf1000.awakened.heightening.Heightening;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

public class AwakeningComponentSelectionScreen extends AbstractAwakeningScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/awakening_large.png");

    private final Screen parent;
    private final AwakeningBuildState buildState;
    private final AwakeningComponentType componentType;

    public AwakeningComponentSelectionScreen(Screen parent, AwakeningBuildState buildState, AwakeningComponentType componentType) {
        super(Component.literal("Select " + componentType.displayName()), BACKGROUND, 176, 166,
                Component.literal("Choose a " + componentType.displayName() + " component."));
        this.parent = parent;
        this.buildState = buildState;
        this.componentType = componentType;
    }

    @Override
    protected void init() {
        super.init();
        this.descriptions.clear();

        int availableHeightening = getAvailableHeightening();
        List<ResourceLocation> ids = getAvailableIds(availableHeightening);
        int buttonWidth = 140;
        int buttonX = this.leftPos + (this.imageWidth - buttonWidth) / 2;
        int startY = this.topPos + 28;
        int spacing = 24;

        for (int i = 0; i < ids.size(); i++) {
            ResourceLocation id = ids.get(i);
            Component name = getComponentName(id);
            Component description = getComponentDescription(id, availableHeightening);
            Button button = Button.builder(name, b -> select(id))
                    .bounds(buttonX, startY + i * spacing, buttonWidth, 20)
                    .build();
            TieredEntry entry = getEntry(id);
            button.active = entry == null || entry.minHeightening() <= availableHeightening;
            this.addWidgetWithDescription(button, description);
        }

        Button backButton = Button.builder(Component.literal("Back"), b -> Minecraft.getInstance().setScreen(this.parent))
                .bounds(this.leftPos + 48, this.topPos + this.imageHeight - 30, 80, 20)
                .build();
        this.addWidgetWithDescription(backButton, Component.literal("Return to the command builder."));
    }

    private void select(ResourceLocation id) {
        this.buildState.set(this.componentType, id);
        Minecraft.getInstance().setScreen(this.parent);
    }

    private List<ResourceLocation> getAvailableIds(int availableTier) {
        return switch (this.componentType) {
            case TRIGGER -> CommandBuilder.availableIds(CommandRegistries.TRIGGER_REGISTRY.get(), availableTier);
            case ACTION -> CommandBuilder.availableIds(CommandRegistries.ACTION_REGISTRY.get(), availableTier);
            case TARGET -> CommandBuilder.availableIds(CommandRegistries.TARGET_REGISTRY.get(), availableTier);
        };
    }

    private Component getComponentName(ResourceLocation id) {
        TieredEntry entry = getEntry(id);
        if (entry == null) {
            return Component.literal(id.getPath());
        }
        Component name = entry.getDisplayName();
        return name.getString().isEmpty() ? Component.literal(id.getPath()) : name;
    }

    private Component getComponentDescription(ResourceLocation id, int availableHeightening) {
        TieredEntry entry = getEntry(id);
        if (entry == null) {
            return Component.empty();
        }
        Component description = entry.getDescription();
        String base = description.getString().isEmpty() ? this.componentType.description() : description.getString();
        boolean locked = entry.minHeightening() > availableHeightening;
        return Component.literal(base
                + "\nCost: " + entry.cost()
                + "\nRequires Heightening: " + entry.minHeightening()
                + (locked ? " (Locked)" : ""));
    }

    @SuppressWarnings("unchecked")
    private TieredEntry getEntry(ResourceLocation id) {
        return switch (this.componentType) {
            case TRIGGER -> CommandRegistries.TRIGGER_REGISTRY.get().getValue(id);
            case ACTION -> CommandRegistries.ACTION_REGISTRY.get().getValue(id);
            case TARGET -> CommandRegistries.TARGET_REGISTRY.get().getValue(id);
        };
    }

    private static int getAvailableHeightening() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return 0;
        }
        return mc.player.getCapability(BreathProvider.BREATH)
                .map(breath -> Heightening.fromBreath(breath.getBreath()).ordinal())
                .orElse(0);
    }
}
