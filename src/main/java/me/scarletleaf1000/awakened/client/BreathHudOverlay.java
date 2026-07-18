package me.scarletleaf1000.awakened.client;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.heightening.Heightening;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BreathHudOverlay {
    @SubscribeEvent
    public static void onRenderGui(RenderGuiOverlayEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        mc.player.getCapability(BreathProvider.BREATH).ifPresent(breath -> {
            Heightening heightening = Heightening.fromBreath(breath.getBreath());
            String text = "Breath: " + breath.getBreath() + " (" + heightening.getDisplayName() + ")";
            int x = 5;
            int y = mc.getWindow().getGuiScaledHeight() - 15;
            event.getGuiGraphics().drawString(mc.font, text, x, y, 0x0000FF, true);
        });
    }
}
