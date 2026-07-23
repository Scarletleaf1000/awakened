package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.command.Action;
import me.scarletleaf1000.awakened.command.ActionType;
import me.scarletleaf1000.awakened.command.CommandBuilder;
import me.scarletleaf1000.awakened.command.CommandRegistries;
import me.scarletleaf1000.awakened.command.TieredEntry;
import me.scarletleaf1000.awakened.command.Trigger;
import me.scarletleaf1000.awakened.heightening.Heightening;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class AwakeningComponentSelectionScreen extends AbstractAwakeningScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/awakening_large.png");
    private static final ResourceLocation WIDGETS = new ResourceLocation("textures/gui/widgets.png");
    private static final int VISIBLE_BUTTONS = 4;
    private static final int BUTTON_WIDTH = 140;
    private static final int BUTTON_HEIGHT = 20;
    private static final int BUTTON_SPACING = 24;
    private static final int SCROLLBAR_WIDTH = 8;
    private static final int SCROLLBAR_HEIGHT = VISIBLE_BUTTONS * BUTTON_SPACING - BUTTON_SPACING + BUTTON_HEIGHT;
    private static final int SCROLLBAR_HANDLE_HEIGHT = 20;

    private final Screen parent;
    private final AwakeningBuildState buildState;
    private final AwakeningComponentType componentType;
    private List<ResourceLocation> ids = List.of();
    private int availableHeightening;
    private int scrollOffset;
    private boolean draggingScrollbar;

    public AwakeningComponentSelectionScreen(Screen parent, AwakeningBuildState buildState, AwakeningComponentType componentType) {
        super(Component.translatable("gui.awakened.selection.title", componentType.getDisplayName()), BACKGROUND, 176, 166,
                Component.translatable("gui.awakened.selection.description", componentType.getDisplayName()));
        this.parent = parent;
        this.buildState = buildState;
        this.componentType = componentType;
    }

    @Override
    protected void init() {
        super.init();
        this.availableHeightening = getAvailableHeightening();
        this.ids = getAvailableIds(this.availableHeightening);
        this.scrollOffset = Math.min(this.scrollOffset, getMaxScrollOffset());
        rebuildSelectionWidgets();
    }

    private void rebuildSelectionWidgets() {
        clearWidgets();
        this.descriptions.clear();

        int buttonX = this.leftPos + (this.imageWidth - BUTTON_WIDTH) / 2;
        int startY = this.topPos + 28;
        int endIndex = Math.min(this.ids.size(), this.scrollOffset + VISIBLE_BUTTONS);
        for (int index = this.scrollOffset; index < endIndex; index++) {
            ResourceLocation id = this.ids.get(index);
            Component name = getComponentName(id);
            Component description = getComponentDescription(id, this.availableHeightening);
            Button button = Button.builder(name, b -> select(id))
                    .bounds(buttonX, startY + (index - this.scrollOffset) * BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT)
                    .build();
            TieredEntry entry = getEntry(id);
            button.active = entry == null || entry.minHeightening() <= this.availableHeightening;
            this.addWidgetWithDescription(button, description);
        }

        Button backButton = Button.builder(Component.translatable("gui.awakened.button.back"), b -> Minecraft.getInstance().setScreen(this.parent))
                .bounds(this.leftPos + 48, this.topPos + this.imageHeight - 30, 80, 20)
                .build();
        this.addWidgetWithDescription(backButton, Component.translatable("gui.awakened.selection.back.description"));
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        if (isScrollable()) {
            renderScrollbar(guiGraphics, mouseX, mouseY);
        }
    }

    private void renderScrollbar(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        int scrollbarX = this.leftPos + this.imageWidth - 14;
        int scrollbarY = this.topPos + 28;
        guiGraphics.fill(scrollbarX, scrollbarY, scrollbarX + SCROLLBAR_WIDTH, scrollbarY + SCROLLBAR_HEIGHT, 0xFF555555);
        boolean hovered = isMouseOverScrollbar(mouseX, mouseY);
        guiGraphics.blit(WIDGETS, scrollbarX, getScrollbarHandleY(), 0, hovered || this.draggingScrollbar ? 66 : 46, SCROLLBAR_WIDTH, SCROLLBAR_HANDLE_HEIGHT, 256, 256);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (!isScrollable()) {
            return super.mouseScrolled(mouseX, mouseY, delta);
        }
        int previousOffset = this.scrollOffset;
        this.scrollOffset = Math.max(0, Math.min(getMaxScrollOffset(), this.scrollOffset - (int) Math.signum(delta)));
        if (this.scrollOffset != previousOffset) {
            rebuildSelectionWidgets();
        }
        return true;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0 && isScrollable() && isMouseOverScrollbar(mouseX, mouseY)) {
            this.draggingScrollbar = true;
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
        if (this.draggingScrollbar && button == 0) {
            updateScrollFromMouse(mouseY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0 && this.draggingScrollbar) {
            this.draggingScrollbar = false;
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    private boolean isScrollable() {
        return this.ids.size() > VISIBLE_BUTTONS;
    }

    private int getMaxScrollOffset() {
        return Math.max(0, this.ids.size() - VISIBLE_BUTTONS);
    }

    private int getScrollbarHandleY() {
        int trackRange = SCROLLBAR_HEIGHT - SCROLLBAR_HANDLE_HEIGHT;
        return this.topPos + 28 + (int) Math.round(trackRange * (this.scrollOffset / (double) getMaxScrollOffset()));
    }

    private boolean isMouseOverScrollbar(double mouseX, double mouseY) {
        int scrollbarX = this.leftPos + this.imageWidth - 14;
        int scrollbarY = this.topPos + 28;
        return mouseX >= scrollbarX && mouseX < scrollbarX + SCROLLBAR_WIDTH && mouseY >= scrollbarY && mouseY < scrollbarY + SCROLLBAR_HEIGHT;
    }

    private void updateScrollFromMouse(double mouseY) {
        int trackY = this.topPos + 28;
        int trackRange = SCROLLBAR_HEIGHT - SCROLLBAR_HANDLE_HEIGHT;
        double progress = Math.max(0.0D, Math.min(1.0D, (mouseY - trackY - SCROLLBAR_HANDLE_HEIGHT / 2.0D) / trackRange));
        int newOffset = (int) Math.round(progress * getMaxScrollOffset());
        if (newOffset != this.scrollOffset) {
            this.scrollOffset = newOffset;
            rebuildSelectionWidgets();
        }
    }

    private void select(ResourceLocation id) {
        this.buildState.set(this.componentType, id);
        if (this.componentType == AwakeningComponentType.ACTION) {
            updateCompatibilityForAction(id);
        }
        Minecraft.getInstance().setScreen(this.parent);
    }

    private List<ResourceLocation> getAvailableIds(int availableTier) {
        return switch (this.componentType) {
            case TRIGGER -> {
                List<ResourceLocation> availableIds = CommandBuilder.availableIds(CommandRegistries.TRIGGER_REGISTRY.get(), availableTier);
                ActionType actionType = getSelectedActionType();
                if (actionType != null) {
                    availableIds = availableIds.stream()
                            .filter(id -> {
                                Trigger trigger = CommandRegistries.TRIGGER_REGISTRY.get().getValue(id);
                                return trigger != null && trigger.getSupportedActionTypes().contains(actionType);
                            })
                            .toList();
                }
                yield availableIds;
            }
            case ACTION -> CommandBuilder.availableIds(CommandRegistries.ACTION_REGISTRY.get(), availableTier);
            case TARGET -> CommandBuilder.availableIds(CommandRegistries.TARGET_REGISTRY.get(), availableTier).stream()
                    .filter(id -> !id.equals(CommandRegistries.NO_TARGET_ID))
                    .toList();
        };
    }

    private ActionType getSelectedActionType() {
        ResourceLocation actionId = this.buildState.get(AwakeningComponentType.ACTION);
        if (actionId == null) {
            return null;
        }
        Action action = CommandRegistries.ACTION_REGISTRY.get().getValue(actionId);
        return action == null ? null : action.getActionType();
    }

    private void updateCompatibilityForAction(ResourceLocation actionId) {
        Action action = CommandRegistries.ACTION_REGISTRY.get().getValue(actionId);
        if (action == null) {
            return;
        }
        ActionType type = action.getActionType();
        ResourceLocation triggerId = this.buildState.get(AwakeningComponentType.TRIGGER);
        if (triggerId != null) {
            Trigger trigger = CommandRegistries.TRIGGER_REGISTRY.get().getValue(triggerId);
            if (trigger == null || !trigger.getSupportedActionTypes().contains(type)) {
                this.buildState.set(AwakeningComponentType.TRIGGER, null);
            }
        }
        if (type != ActionType.ENTITY) {
            this.buildState.set(AwakeningComponentType.TARGET, null);
        }
    }

    private Component getComponentName(ResourceLocation id) {
        TieredEntry entry = getEntry(id);
        if (entry == null) {
            return Component.translatable("gui.awakened.command_builder.unknown", id.getPath());
        }
        Component name = entry.getDisplayName();
        return name.getString().isEmpty() ? Component.translatable("gui.awakened.command_builder.unknown", id.getPath()) : name;
    }

    private Component getComponentDescription(ResourceLocation id, int availableHeightening) {
        TieredEntry entry = getEntry(id);
        if (entry == null) {
            return Component.empty();
        }
        Component description = entry.getDescription();
        Component base = description.getString().isEmpty() ? this.componentType.getDescription() : description;
        boolean locked = entry.minHeightening() > availableHeightening;
        return Component.translatable("gui.awakened.command_builder.component_description", base, entry.cost(), entry.minHeightening(),
                locked ? Component.translatable("gui.awakened.command_builder.locked") : Component.empty());
    }

    private TieredEntry getEntry(ResourceLocation id) {
        return switch (this.componentType) {
            case TRIGGER -> CommandRegistries.TRIGGER_REGISTRY.get().getValue(id);
            case ACTION -> CommandRegistries.ACTION_REGISTRY.get().getValue(id);
            case TARGET -> CommandRegistries.TARGET_REGISTRY.get().getValue(id);
        };
    }

    private static int getAvailableHeightening() {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null) {
            return 0;
        }
        return minecraft.player.getCapability(BreathProvider.BREATH)
                .map(breath -> Heightening.fromBreath(breath.getBreath()).ordinal())
                .orElse(0);
    }
}