package me.scarletleaf1000.awakened.client;

import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.client.screens.VillagerBreathTradeScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandlers {
    public static void handleBreathSync(int breath) {
        if (Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.getCapability(BreathProvider.BREATH).ifPresent(b -> b.setBreath(breath));
        }
    }

    public static void handleEntityBreathSync(int entityId, int breath) {
        ClientBreathData.set(entityId, breath);
    }

    public static void handleDrabShaderToggle(boolean disabled) {
        DrabShaderManager.setGloballyDisabled(disabled);
    }

    public static void handleNightbloodSync(int crafted, int max) {
        ClientNightbloodData.set(crafted, max);
    }

    public static void handleVillagerTradeResult(boolean success) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.screen instanceof VillagerBreathTradeScreen screen) {
            if (success) {
                mc.setScreen(null);
            } else {
                screen.resetAfterFailedTrade();
            }
        }
    }
}
