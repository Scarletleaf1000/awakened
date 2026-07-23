package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.command.TieredEntry;
import me.scarletleaf1000.awakened.command.Trigger;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.network.AwakeningCommandUseC2SPacket;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class AwakeningCommandBuilderScreen extends AbstractAwakeningScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/awakening_small.png");
    private static final Component DEFAULT_DESCRIPTION = Component.literal("Build an Awakening command by selecting a Trigger, Action, and Target.");

    private final AwakeningBuildState buildState;
    private SummaryBox summaryBox;
    private Button useCommandButton;

    public AwakeningCommandBuilderScreen(AwakeningBuildState buildState) {
        super(Component.literal("Awaken"), BACKGROUND, 200, 250, DEFAULT_DESCRIPTION);
        this.buildState = buildState;
    }

    @Override
    protected void init() {
        super.init();
        this.descriptions.clear();

        int buttonWidth = 150;
        int buttonX = this.leftPos + (this.imageWidth - buttonWidth) / 2;
        int startY = this.topPos + 20;
        int spacing = 30;

        for (int i = 0; i < AwakeningComponentType.values().length; i++) {
            AwakeningComponentType type = AwakeningComponentType.values()[i];
            Button button = Button.builder(getComponentButtonLabel(type), b -> openSelectionScreen(type))
                    .bounds(buttonX, startY + i * spacing, buttonWidth, 20)
                    .build();
            button.active = type != AwakeningComponentType.TARGET || isTargetUsed();
            this.addWidgetWithDescription(button, getComponentButtonDescription(type));
        }

        int specialY = startY + AwakeningComponentType.values().length * spacing + 4;
        Button specialButton = Button.builder(Component.literal("Special Commands"), b -> {})
                .bounds(buttonX, specialY, buttonWidth, 20)
                .build();
        specialButton.active = false;
        this.addWidgetWithDescription(specialButton, Component.literal("Special commands are not available yet."));

        int summaryY = specialY + 20 + 20;
        int summaryWidth = this.imageWidth - 30;
        this.summaryBox = new SummaryBox(this.font, this.leftPos + 15, summaryY, summaryWidth, 44, getSummaryComponent());
        this.addWidgetWithDescription(this.summaryBox, Component.literal("Current component selections."));

        int useButtonY = summaryY + 60;
        this.useCommandButton = Button.builder(Component.literal("Use Command"), b -> useCommand())
                .bounds(this.leftPos + (this.imageWidth - 100) / 2, useButtonY, 100, 20)
                .build();
        this.useCommandButton.active = this.buildState.isComplete() && getAvailableHeightening() >= getRequiredHeightening();
        this.addWidgetWithDescription(this.useCommandButton, Component.literal("Activate the assembled command.\nCost: " + getTotalCost() + " | Requires Heightening: " + getRequiredHeightening()));
    }

    private Component getComponentButtonLabel(AwakeningComponentType type) {
        if (type == AwakeningComponentType.TARGET && !isTargetUsed()) {
            return Component.literal(type.displayName() + ": N/A");
        }
        ResourceLocation id = getEffectiveId(type);
        String value = id == null ? "None" : getComponentName(type, id).getString();
        return Component.literal(type.displayName() + ": " + value);
    }

    private Component getSummaryComponent() {
        StringBuilder builder = new StringBuilder();
        for (AwakeningComponentType type : AwakeningComponentType.values()) {
            if (type == AwakeningComponentType.TARGET && !isTargetUsed()) {
                continue;
            }
            if (builder.length() > 0) {
                builder.append(" ");
            }
            builder.append(getComponentName(type, getEffectiveId(type)).getString());
        }
        builder.append("\nCost: ").append(getTotalCost());
        builder.append(" | Requires Heightening: ").append(getRequiredHeightening());
        return Component.literal(builder.toString());
    }

    private Component getComponentName(AwakeningComponentType type, ResourceLocation id) {
        if (id == null) {
            return Component.literal("None");
        }
        TieredEntry entry = switch (type) {
            case TRIGGER -> CommandRegistries.TRIGGER_REGISTRY.get().getValue(id);
            case ACTION -> CommandRegistries.ACTION_REGISTRY.get().getValue(id);
            case TARGET -> CommandRegistries.TARGET_REGISTRY.get().getValue(id);
        };
        if (entry == null) {
            return Component.literal(id.getPath());
        }
        Component name = entry.getDisplayName();
        return name.getString().isEmpty() ? Component.literal(id.getPath()) : name;
    }

    private Component getComponentButtonDescription(AwakeningComponentType type) {
        if (type == AwakeningComponentType.TARGET && !isTargetUsed()) {
            return Component.literal("No target is required for this action.");
        }
        ResourceLocation id = getEffectiveId(type);
        StringBuilder builder = new StringBuilder(type.description());
        if (id != null) {
            TieredEntry entry = getComponentEntry(type, id);
            if (entry != null) {
                builder.append("\nCost: ").append(entry.cost())
                        .append("\nRequires Heightening: ").append(entry.minHeightening());
            }
        }
        return Component.literal(builder.toString());
    }

    private int getTotalCost() {
        int cost = 0;
        for (AwakeningComponentType type : AwakeningComponentType.values()) {
            if (type == AwakeningComponentType.TARGET && !isTargetUsed()) {
                continue;
            }
            ResourceLocation id = getEffectiveId(type);
            if (id != null) {
                TieredEntry entry = getComponentEntry(type, id);
                if (entry != null) {
                    cost += entry.cost();
                }
            }
        }
        return cost;
    }

    private int getRequiredHeightening() {
        int required = 0;
        for (AwakeningComponentType type : AwakeningComponentType.values()) {
            if (type == AwakeningComponentType.TARGET && !isTargetUsed()) {
                continue;
            }
            ResourceLocation id = getEffectiveId(type);
            if (id != null) {
                TieredEntry entry = getComponentEntry(type, id);
                if (entry != null) {
                    required = Math.max(required, entry.minHeightening());
                }
            }
        }
        return required;
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

    private TieredEntry getComponentEntry(AwakeningComponentType type, ResourceLocation id) {
        return switch (type) {
            case TRIGGER -> CommandRegistries.TRIGGER_REGISTRY.get().getValue(id);
            case ACTION -> CommandRegistries.ACTION_REGISTRY.get().getValue(id);
            case TARGET -> CommandRegistries.TARGET_REGISTRY.get().getValue(id);
        };
    }

    private void openSelectionScreen(AwakeningComponentType type) {
        Minecraft.getInstance().setScreen(new AwakeningComponentSelectionScreen(this, this.buildState, type));
    }

    private void useCommand() {
        if (!this.buildState.isComplete()) {
            return;
        }
        ResourceLocation actionId = getEffectiveId(AwakeningComponentType.ACTION);
        if (actionId == null) {
            return;
        }
        ResourceLocation triggerId = getEffectiveId(AwakeningComponentType.TRIGGER);
        ResourceLocation targetId = isTargetUsed()
                ? getEffectiveId(AwakeningComponentType.TARGET)
                : CommandRegistries.NO_TARGET_ID;
        BreathNetwork.CHANNEL.sendToServer(new AwakeningCommandUseC2SPacket(triggerId, actionId, targetId));
        Minecraft.getInstance().setScreen(null);
    }

    private boolean isTargetUsed() {
        return getSelectedActionType() == ActionType.ENTITY;
    }

    private ActionType getSelectedActionType() {
        ResourceLocation actionId = this.buildState.get(AwakeningComponentType.ACTION);
        if (actionId == null) {
            return null;
        }
        TieredEntry entry = getComponentEntry(AwakeningComponentType.ACTION, actionId);
        if (!(entry instanceof Action action)) {
            return null;
        }
        return action.getActionType();
    }

    private ResourceLocation getEffectiveId(AwakeningComponentType type) {
        ResourceLocation id = this.buildState.get(type);
        if (id != null) {
            if (type != AwakeningComponentType.TRIGGER) {
                return id;
            }
            return getCompatibleTriggerId(id);
        }
        return switch (type) {
            case TRIGGER -> new ResourceLocation(Awakened.MOD_ID, "passive");
            case TARGET -> new ResourceLocation(Awakened.MOD_ID, "self");
            case ACTION -> null;
        };
    }

    private ResourceLocation getCompatibleTriggerId(ResourceLocation selectedTriggerId) {
        if (selectedTriggerId == null) {
            return new ResourceLocation(Awakened.MOD_ID, "passive");
        }
        ActionType actionType = getSelectedActionType();
        if (actionType == null) {
            return selectedTriggerId;
        }
        Trigger trigger = (Trigger) getComponentEntry(AwakeningComponentType.TRIGGER, selectedTriggerId);
        if (trigger != null && trigger.getSupportedActionTypes().contains(actionType)) {
            return selectedTriggerId;
        }
        return new ResourceLocation(Awakened.MOD_ID, "passive");
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
