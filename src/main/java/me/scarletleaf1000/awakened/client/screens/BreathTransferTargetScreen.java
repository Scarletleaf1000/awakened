package me.scarletleaf1000.awakened.client.screens;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.network.BreathTransferC2SPacket;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.Comparator;
import java.util.List;

public class BreathTransferTargetScreen extends AbstractAwakeningScreen {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/awakening_large.png");
    private static final double TARGET_RANGE_SQUARED = 100.0D;

    private final Screen parent;

    public BreathTransferTargetScreen(Screen parent) {
        super(Component.translatable("gui.awakened.special.my_breath_to_yours"), BACKGROUND, 176, 166,
                Component.translatable("gui.awakened.special.my_breath_to_yours.target.description"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        super.init();
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.player == null || minecraft.level == null) {
            return;
        }

        int buttonX = this.leftPos + 18;
        int buttonY = this.topPos + 24;
        List<? extends Player> players = minecraft.level.players().stream()
                .filter(player -> player != minecraft.player && player.isAlive() && player.distanceToSqr(minecraft.player) <= TARGET_RANGE_SQUARED)
                .sorted(Comparator.comparing(player -> player.getName().getString()))
                .toList();
        for (Player player : players) {
            Button button = Button.builder(player.getName(), b -> selectPlayer(player))
                    .bounds(buttonX, buttonY, 140, 20)
                    .build();
            this.addWidgetWithDescription(button, Component.translatable("gui.awakened.special.my_breath_to_yours.player.description", player.getName()));
            buttonY += 22;
        }

        ItemStack held = minecraft.player.getMainHandItem();
        Button heldItemButton = Button.builder(Component.translatable("gui.awakened.special.my_breath_to_yours.held_item"), b -> selectHeldItem())
                .bounds(buttonX, buttonY, 140, 20)
                .build();
        heldItemButton.active = !held.isEmpty() && held.getCount() == 1;
        this.addWidgetWithDescription(heldItemButton, Component.translatable("gui.awakened.special.my_breath_to_yours.held_item.description"));

        Button cancelButton = Button.builder(Component.translatable("gui.awakened.button.cancel"), b -> Minecraft.getInstance().setScreen(this.parent))
                .bounds(this.leftPos + 48, this.topPos + this.imageHeight - 30, 80, 20)
                .build();
        this.addWidgetWithDescription(cancelButton, Component.translatable("gui.awakened.special.my_breath_to_yours.cancel.description"));
    }

    private void selectPlayer(Player player) {
        BreathNetwork.CHANNEL.sendToServer(BreathTransferC2SPacket.forPlayer(player.getUUID()));
        Minecraft.getInstance().setScreen(null);
    }

    private void selectHeldItem() {
        BreathNetwork.CHANNEL.sendToServer(BreathTransferC2SPacket.forHeldItem());
        Minecraft.getInstance().setScreen(null);
    }
}
