package me.scarletleaf1000.awakened.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.logging.LogUtils;
import me.scarletleaf1000.awakened.Awakened;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.IOException;

/**
 * Manages the drab desaturation post shader through GameRenderer's built-in post-effect pipeline.
 */
@OnlyIn(Dist.CLIENT)
public class DrabShaderManager {
    public static ShaderInstance DESATURATED_ENTITY_SHADER;

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final ResourceLocation DRAB_SHADER = new ResourceLocation(Awakened.MOD_ID, "shaders/post/drab.json");

    private static boolean active = false;
    private static long lastErrorTick = Long.MIN_VALUE;

    public static void update(boolean shouldBeActive) {
        Minecraft mc = Minecraft.getInstance();
        if (isShaderModActive()) {
            if (active) {
                mc.gameRenderer.shutdownEffect();
                active = false;
            }
            return;
        }

        boolean loaded = mc.gameRenderer.currentEffect() != null;
        if (active == shouldBeActive && (!shouldBeActive || loaded)) {
            return;
        }

        try {
            if (shouldBeActive) {
                mc.gameRenderer.loadEffect(DRAB_SHADER);
            } else {
                mc.gameRenderer.shutdownEffect();
            }
            active = shouldBeActive;
        } catch (Exception e) {
            long tick = mc.player != null ? mc.player.tickCount : 0;
            if (tick - lastErrorTick > 200) {
                LOGGER.error("Failed to load drab shader {}", DRAB_SHADER, e);
                lastErrorTick = tick;
            }
            active = false;
        }
    }

    public static boolean isShaderModActive() {
        return ModList.get().isLoaded("oculus") || ModList.get().isLoaded("iris");
    }

    @Mod.EventBusSubscriber(modid = Awakened.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ShaderEvents {
        @SubscribeEvent
        public static void registerShaders(RegisterShadersEvent event) throws IOException {
            event.registerShader(
                    new ShaderInstance(event.getResourceProvider(), new ResourceLocation(Awakened.MOD_ID, "desaturated_entity"), DefaultVertexFormat.NEW_ENTITY),
                    shader -> DESATURATED_ENTITY_SHADER = shader
            );
        }
    }
}
