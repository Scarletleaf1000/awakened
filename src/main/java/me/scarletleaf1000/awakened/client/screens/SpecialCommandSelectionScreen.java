package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.client.ClientNightbloodData;
import me.scarletleaf1000.awakened.command.SpecialCommand;
import me.scarletleaf1000.awakened.heightening.Heightening;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpecialCommandSelectionScreen extends AbstractAwakeningScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/awakening_large.png");

    private final Screen parent;
    private final AwakeningBuildState buildState;
    private Button commandButton;
    private Button destroyEvilButton;

    public SpecialCommandSelectionScreen(Screen parent, AwakeningBuildState buildState) {
        super(Component.translatable("gui.awakened.special.selection.title"), BACKGROUND, 176, 166,
                Component.translatable("gui.awakened.special.selection.description"));
        this.parent = parent;
        this.buildState = buildState;
    }

    @Override
    protected void init() {
        super.init();
        this.commandButton = Button.builder(SpecialCommand.MY_BREATH_TO_YOURS.getDisplayName(), b -> select(SpecialCommand.MY_BREATH_TO_YOURS))
                .bounds(this.leftPos + 18, this.topPos + 36, 140, 20)
                .build();
        this.commandButton.active = isUsable(SpecialCommand.MY_BREATH_TO_YOURS);
        this.addWidgetWithDescription(this.commandButton, getSpecialDescription(SpecialCommand.MY_BREATH_TO_YOURS));

        this.destroyEvilButton = Button.builder(SpecialCommand.DESTROY_EVIL.getDisplayName(), b -> select(SpecialCommand.DESTROY_EVIL))
                .bounds(this.leftPos + 18, this.topPos + 62, 140, 20)
                .build();
        this.destroyEvilButton.active = isUsable(SpecialCommand.DESTROY_EVIL);
        this.addWidgetWithDescription(this.destroyEvilButton, getSpecialDescription(SpecialCommand.DESTROY_EVIL));

        Button backButton = Button.builder(Component.translatable("gui.awakened.button.back"), b -> Minecraft.getInstance().setScreen(this.parent))
                .bounds(this.leftPos + 48, this.topPos + this.imageHeight - 30, 80, 20)
                .build();
        this.addWidgetWithDescription(backButton, Component.translatable("gui.awakened.selection.back.description"));
    }

    @Override
    public void tick() {
        super.tick();
        if (this.commandButton != null) {
            this.commandButton.active = isUsable(SpecialCommand.MY_BREATH_TO_YOURS);
            this.descriptions.put(this.commandButton, getSpecialDescription(SpecialCommand.MY_BREATH_TO_YOURS));
        }
        if (this.destroyEvilButton != null) {
            this.destroyEvilButton.active = isUsable(SpecialCommand.DESTROY_EVIL);
            this.descriptions.put(this.destroyEvilButton, getSpecialDescription(SpecialCommand.DESTROY_EVIL));
        }
    }

    private boolean isUsable(SpecialCommand command) {
        if (getAvailableHeightening() < command.minHeightening()) {
            return false;
        }
        if (command == SpecialCommand.DESTROY_EVIL && ClientNightbloodData.isLimitReached()) {
            return false;
        }
        return true;
    }

    private Component getSpecialDescription(SpecialCommand command) {
        Component lockReason = Component.empty();
        if (getAvailableHeightening() < command.minHeightening()) {
            lockReason = Component.translatable("gui.awakened.command_builder.locked");
        } else if (command == SpecialCommand.DESTROY_EVIL && ClientNightbloodData.isLimitReached()) {
            lockReason = Component.translatable("gui.awakened.special.destroy_evil.limit_reached_suffix");
        }
        return Component.translatable("gui.awakened.command_builder.component_description",
                command.getDescription(), command.cost(), command.minHeightening(), lockReason);
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

    private void select(SpecialCommand command) {
        this.buildState.setSpecialCommand(command);
        Minecraft.getInstance().setScreen(this.parent);
    }
}
