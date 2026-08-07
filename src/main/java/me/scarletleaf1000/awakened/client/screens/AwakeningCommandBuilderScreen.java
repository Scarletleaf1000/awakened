package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.client.ClientNightbloodData;
import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.command.SpecialCommand;
import me.scarletleaf1000.awakened.command.TieredEntry;
import me.scarletleaf1000.awakened.command.Trigger;
import me.scarletleaf1000.awakened.heightening.Heightening;
import me.scarletleaf1000.awakened.item.AwakenedItemData;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import me.scarletleaf1000.awakened.network.AwakeningCommandUseC2SPacket;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import me.scarletleaf1000.awakened.network.DestroyEvilCommandUseC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AwakeningCommandBuilderScreen extends AbstractAwakeningScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/awakening_small.png");
    private static final Component DEFAULT_DESCRIPTION = Component.translatable("gui.awakened.command_builder.description");

    private final AwakeningBuildState buildState;
    private SummaryBox summaryBox;
    private Button useCommandButton;

    public AwakeningCommandBuilderScreen(AwakeningBuildState buildState) {
        super(Component.translatable("gui.awakened.command_builder.title"), BACKGROUND, 200, 250, DEFAULT_DESCRIPTION);
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
        Button specialButton = Button.builder(Component.translatable("gui.awakened.command_builder.button.special_commands"), b -> Minecraft.getInstance().setScreen(new SpecialCommandSelectionScreen(this, this.buildState)))
                .bounds(buttonX, specialY, buttonWidth, 20)
                .build();
        specialButton.active = true;
        this.addWidgetWithDescription(specialButton, Component.translatable("gui.awakened.command_builder.button.special_commands.description"));

        int summaryY = specialY + 20 + 20;
        int summaryWidth = this.imageWidth - 30;
        this.summaryBox = new SummaryBox(this.font, this.leftPos + 15, summaryY, summaryWidth, 44, getSummaryComponent());
        this.addWidgetWithDescription(this.summaryBox, Component.translatable("gui.awakened.command_builder.summary.description"));

        int useButtonY = summaryY + 60;
        this.useCommandButton = Button.builder(Component.translatable("gui.awakened.command_builder.button.use_command"), b -> useCommand())
                .bounds(this.leftPos + (this.imageWidth - 100) / 2, useButtonY, 100, 20)
                .build();
        this.useCommandButton.active = (isSpecialCommandReady() || this.buildState.isComplete() && getAvailableHeightening() >= getRequiredHeightening() && isHeldItemValid());
        this.addWidgetWithDescription(this.useCommandButton, getUseCommandDescription());
    }

    private Component getComponentButtonLabel(AwakeningComponentType type) {
        Component value;
        if (type == AwakeningComponentType.TARGET && !isTargetUsed()) {
            value = Component.translatable("gui.awakened.command_builder.not_available");
        } else {
            ResourceLocation id = getEffectiveId(type);
            value = id == null ? Component.translatable("gui.awakened.command_builder.none") : getComponentName(type, id);
        }
        return Component.translatable("gui.awakened.command_builder.component_label", type.getDisplayName(), value);
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
        if (this.buildState.getSpecialCommand() != null) {
            return this.buildState.getSpecialCommand().getDisplayName();
        }
        return Component.translatable("gui.awakened.command_builder.summary", builder.toString(), getTotalCost(), getRequiredHeightening());
    }

    private Component getComponentName(AwakeningComponentType type, ResourceLocation id) {
        if (id == null) {
            return Component.translatable("gui.awakened.command_builder.none");
        }
        TieredEntry entry = switch (type) {
            case TRIGGER -> CommandRegistries.TRIGGER_REGISTRY.get().getValue(id);
            case ACTION -> CommandRegistries.ACTION_REGISTRY.get().getValue(id);
            case TARGET -> CommandRegistries.TARGET_REGISTRY.get().getValue(id);
        };
        if (entry == null) {
            return Component.translatable("gui.awakened.command_builder.unknown", id.getPath());
        }
        Component name = entry.getDisplayName();
        return name.getString().isEmpty() ? Component.translatable("gui.awakened.command_builder.unknown", id.getPath()) : name;
    }

    private Component getComponentButtonDescription(AwakeningComponentType type) {
        if (type == AwakeningComponentType.TARGET && !isTargetUsed()) {
            return Component.translatable("gui.awakened.command_builder.target_not_required");
        }
        ResourceLocation id = getEffectiveId(type);
        if (id == null) {
            return type.getDescription();
        }
        TieredEntry entry = getComponentEntry(type, id);
        if (entry == null) {
            return type.getDescription();
        }
        boolean locked = entry.minHeightening() > getAvailableHeightening();
        return Component.translatable("gui.awakened.command_builder.component_description",
                entry.getDescription(), entry.cost(), entry.minHeightening(),
                locked ? Component.translatable("gui.awakened.command_builder.locked") : Component.empty());
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

    private Component getUseCommandDescription() {
        SpecialCommand special = this.buildState.getSpecialCommand();
        if (special != null) {
            if (special == SpecialCommand.DESTROY_EVIL) {
                return getDestroyEvilDescription();
            }
            return Component.translatable("gui.awakened.special.my_breath_to_yours.use.description");
        }
        Component itemError = getHeldItemError();
        if (itemError != null) {
            return itemError;
        }
        if (getAvailableHeightening() < getRequiredHeightening()) {
            return Component.translatable("gui.awakened.command_builder.use.heightening_required", getRequiredHeightening());
        }
        return Component.translatable("gui.awakened.command_builder.use.activate", getTotalCost(), getRequiredHeightening());
    }

    private Component getDestroyEvilDescription() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return Component.translatable("gui.awakened.command_builder.use.requires_item");
        }
        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || held.getCount() != 1 || !held.is(Items.NETHERITE_SWORD)) {
            return Component.translatable("gui.awakened.command_builder.use.requires_item");
        }
        if (AwakenedItemData.isAwakened(held) || ItemBreathStorage.hasStoredBreath(held)) {
            return Component.translatable("gui.awakened.command_builder.use.invalid_item");
        }
        if (getAvailableHeightening() < Heightening.NINTH.ordinal()) {
            return Component.translatable("gui.awakened.command_builder.use.heightening_required", Heightening.NINTH.ordinal());
        }
        if (ClientNightbloodData.isLimitReached()) {
            return Component.translatable("message.awakened.destroy_evil.limit_reached");
        }
        return Component.translatable("gui.awakened.special.destroy_evil.use.description");
    }

    private boolean isHeldItemValid() {
        return getHeldItemError() == null;
    }

    private Component getHeldItemError() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return Component.translatable("gui.awakened.command_builder.use.requires_item");
        }
        ItemStack held = mc.player.getMainHandItem();
        if (held.isEmpty() || held.getMaxStackSize() != 1) {
            return Component.translatable("gui.awakened.command_builder.use.requires_item");
        }
        if (held.is(Awakened.UNAWAKENABLE_TAG)) {
            return Component.translatable("gui.awakened.command_builder.use.unawakenable");
        }
        if (held.is(Awakened.AWAKENABLE_TAG) || getAvailableHeightening() >= Heightening.NINTH.ordinal()) {
            return null;
        }
        return Component.translatable("gui.awakened.command_builder.use.not_strong_enough");
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
        SpecialCommand special = this.buildState.getSpecialCommand();
        if (special != null) {
            if (special == SpecialCommand.DESTROY_EVIL) {
                BreathNetwork.CHANNEL.sendToServer(new DestroyEvilCommandUseC2SPacket());
                Minecraft.getInstance().setScreen(null);
                return;
            }
            Minecraft.getInstance().setScreen(new BreathTransferTargetScreen(this));
            return;
        }
        if (!this.buildState.isComplete() || !isHeldItemValid()) {
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
    public void tick() {
        super.tick();
        if (this.useCommandButton != null) {
            this.useCommandButton.active = (isSpecialCommandReady() || this.buildState.isComplete() && getAvailableHeightening() >= getRequiredHeightening() && isHeldItemValid());
            this.descriptions.put(this.useCommandButton, getUseCommandDescription());
        }
    }

    private boolean isSpecialCommandReady() {
        SpecialCommand special = this.buildState.getSpecialCommand();
        if (special == null) {
            return false;
        }
        if (special == SpecialCommand.DESTROY_EVIL) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) {
                return false;
            }
            ItemStack held = mc.player.getMainHandItem();
            if (held.isEmpty() || held.getCount() != 1 || !held.is(Items.NETHERITE_SWORD)) {
                return false;
            }
            if (AwakenedItemData.isAwakened(held) || ItemBreathStorage.hasStoredBreath(held)) {
                return false;
            }
            if (ClientNightbloodData.isLimitReached()) {
                return false;
            }
            return getAvailableHeightening() >= Heightening.NINTH.ordinal();
        }
        return true;
    }

    @Override
    public void onClose() {
        super.onClose();
    }
}
