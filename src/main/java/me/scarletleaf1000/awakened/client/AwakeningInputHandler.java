package me.scarletleaf1000.awakened.client;

import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.client.screens.AwakeningBuildState;
import me.scarletleaf1000.awakened.client.screens.AwakeningCommandBuilderScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class AwakeningInputHandler {
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen != null || mc.player == null) {
            return;
        }
        if (AwakenedKeyMappings.OPEN_AWAKENING.consumeClick()) {
            mc.setScreen(new AwakeningCommandBuilderScreen(new AwakeningBuildState()));
        }
    }
}
