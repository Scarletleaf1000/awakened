package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.command.SpecialCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class SpecialCommandSelectionScreen extends AbstractAwakeningScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/awakening_large.png");

    private final Screen parent;
    private final AwakeningBuildState buildState;

    public SpecialCommandSelectionScreen(Screen parent, AwakeningBuildState buildState) {
        super(Component.translatable("gui.awakened.special.selection.title"), BACKGROUND, 176, 166,
                Component.translatable("gui.awakened.special.selection.description"));
        this.parent = parent;
        this.buildState = buildState;
    }

    @Override
    protected void init() {
        super.init();
        Button commandButton = Button.builder(SpecialCommand.MY_BREATH_TO_YOURS.getDisplayName(), b -> select(SpecialCommand.MY_BREATH_TO_YOURS))
                .bounds(this.leftPos + 18, this.topPos + 36, 140, 20)
                .build();
        this.addWidgetWithDescription(commandButton, Component.translatable("gui.awakened.special.my_breath_to_yours.description"));

        Button destroyEvilButton = Button.builder(SpecialCommand.DESTROY_EVIL.getDisplayName(), b -> select(SpecialCommand.DESTROY_EVIL))
                .bounds(this.leftPos + 18, this.topPos + 62, 140, 20)
                .build();
        this.addWidgetWithDescription(destroyEvilButton, Component.translatable("gui.awakened.special.destroy_evil.description"));

        Button backButton = Button.builder(Component.translatable("gui.awakened.button.back"), b -> Minecraft.getInstance().setScreen(this.parent))
                .bounds(this.leftPos + 48, this.topPos + this.imageHeight - 30, 80, 20)
                .build();
        this.addWidgetWithDescription(backButton, Component.translatable("gui.awakened.selection.back.description"));
    }

    private void select(SpecialCommand command) {
        this.buildState.setSpecialCommand(command);
        Minecraft.getInstance().setScreen(this.parent);
    }
}
