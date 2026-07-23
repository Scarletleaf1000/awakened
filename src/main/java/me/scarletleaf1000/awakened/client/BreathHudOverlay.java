package me.scarletleaf1000.awakened.client;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.client.screens.AbstractAwakeningScreen;
import me.scarletleaf1000.awakened.heightening.Heightening;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BreathHudOverlay {
    private static final int DISPLAY_DURATION_TICKS = 100;
    private static int lastBreath = Integer.MIN_VALUE;
    private static int lastChangeTick;

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui || mc.screen != null) {
            return;
        }
        renderBreath(mc, event.getGuiGraphics());
    }

    @SubscribeEvent
    public static void onScreenRenderPost(ScreenEvent.Render.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }
        if (!(event.getScreen() instanceof AbstractContainerScreen<?>) && !(event.getScreen() instanceof AbstractAwakeningScreen)) {
            return;
        }
        renderBreath(mc, event.getGuiGraphics());
    }

    private static void renderBreath(Minecraft mc, GuiGraphics guiGraphics) {
        mc.player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
            int currentBreath = breath.getBreath();
            if (currentBreath != lastBreath) {
                lastBreath = currentBreath;
                lastChangeTick = mc.player.tickCount;
            }
            if (!shouldDisplay(mc)) {
                return;
            }

            Heightening heightening = Heightening.fromBreath(currentBreath);
            Component text = Component.translatable("gui.awakened.hud.breath", currentBreath, heightening.getDisplayName());
            int x = 5;
            int y = mc.getWindow().getGuiScaledHeight() - 15;
            guiGraphics.drawString(mc.font, text, x, y, 0x0000FF, true);
        });
    }

    private static boolean shouldDisplay(Minecraft mc) {
        return mc.player.tickCount - lastChangeTick <= DISPLAY_DURATION_TICKS
                || mc.screen instanceof AbstractContainerScreen<?>
                || mc.screen instanceof AbstractAwakeningScreen;
    }
}