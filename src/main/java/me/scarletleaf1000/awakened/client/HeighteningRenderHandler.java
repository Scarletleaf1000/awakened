package me.scarletleaf1000.awakened.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.logging.LogUtils;
import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.breath.BreathProvider;
import me.scarletleaf1000.awakened.heightening.Heightening;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.util.IdentityHashMap;
import java.util.Map;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.RenderTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Awakened.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class HeighteningRenderHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private static final RenderType SILHOUETTE = RenderType.create(
            "awakened_entity_silhouette",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new net.minecraft.client.renderer.RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(new net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {}))
                    .setDepthTestState(new net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard(">", GL11.GL_GREATER))
                    .setCullState(new net.minecraft.client.renderer.RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard(true, false))
                    .createCompositeState(false)
    );

    private static final RenderType SILHOUETTE_ALWAYS = RenderType.create(
            "awakened_entity_silhouette_always",
            DefaultVertexFormat.POSITION_COLOR,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder()
                    .setShaderState(new net.minecraft.client.renderer.RenderStateShard.ShaderStateShard(GameRenderer::getPositionColorShader))
                    .setTransparencyState(new net.minecraft.client.renderer.RenderStateShard.TransparencyStateShard("no_transparency", RenderSystem::disableBlend, () -> {}))
                    .setDepthTestState(new net.minecraft.client.renderer.RenderStateShard.DepthTestStateShard("always", GL11.GL_ALWAYS))
                    .setCullState(new net.minecraft.client.renderer.RenderStateShard.CullStateShard(false))
                    .setWriteMaskState(new net.minecraft.client.renderer.RenderStateShard.WriteMaskStateShard(true, false))
                    .createCompositeState(false)
    );

    private static final WhiteBufferSource VISIBLE_SOURCE = createWhiteSource(SILHOUETTE);
    private static final WhiteBufferSource INVISIBLE_SOURCE = createWhiteSource(SILHOUETTE_ALWAYS);

    private static WhiteBufferSource createWhiteSource(RenderType type) {
        MultiBufferSource.BufferSource bufferSource = MultiBufferSource.immediate(new BufferBuilder(256));
        return new WhiteBufferSource(bufferSource, type);
    }

    private static final ThreadLocal<Boolean> DESATURATING = ThreadLocal.withInitial(() -> false);
    private static final BufferBuilder DESATURATED_BUILDER = new BufferBuilder(256);
    private static final MultiBufferSource.BufferSource DESATURATED_IMMEDIATE = MultiBufferSource.immediate(DESATURATED_BUILDER);
    private static final DesaturatedBufferSource DESATURATED_SOURCE = new DesaturatedBufferSource(DESATURATED_IMMEDIATE);

    @SubscribeEvent
    public static void onRenderLivingPre(RenderLivingEvent.Pre<?, ?> event) {
        if (DESATURATING.get()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!shouldDesaturate(entity)) {
            return;
        }

        event.setCanceled(true);
        renderDesaturated(entity, event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
    }

    @SubscribeEvent
    public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
        if (DESATURATING.get()) {
            return;
        }
        LivingEntity entity = event.getEntity();
        if (!shouldDesaturate(entity)) {
            return;
        }

        event.setCanceled(true);
        renderDesaturated(entity, event.getRenderer(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource(), event.getPackedLight());
    }

    @SubscribeEvent
    public static void onRenderLivingPost(RenderLivingEvent.Post event) {
        if (DESATURATING.get()) {
            return;
        }
        if (shouldDesaturate(event.getEntity())) {
            return;
        }
        renderHeighteningLabel(event.getEntity(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource());
    }

    @SubscribeEvent
    public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
        if (DESATURATING.get()) {
            return;
        }
        if (shouldDesaturate(event.getEntity())) {
            return;
        }
        renderHeighteningLabel(event.getEntity(), event.getPartialTick(), event.getPoseStack(), event.getMultiBufferSource());
    }

    private static boolean shouldDesaturate(LivingEntity entity) {
        return Heightening.fromBreath(ClientBreathData.get(entity.getId())) == Heightening.DRAB;
    }

    private static void renderDesaturated(LivingEntity entity, EntityRenderer renderer, float partialTick, PoseStack pose, MultiBufferSource bufferSource, int packedLight) {
        DESATURATING.set(true);
        boolean prevNameVisible = entity.isCustomNameVisible();
        try {
            entity.setCustomNameVisible(false);
            Minecraft.getInstance().getEntityRenderDispatcher().setRenderShadow(false);
            float yaw = Mth.lerp(partialTick, entity.yRotO, entity.getYRot());
            renderer.render(entity, yaw, partialTick, pose, DESATURATED_SOURCE, packedLight);
            DESATURATED_SOURCE.endBatch();
            renderHeighteningLabel(entity, partialTick, pose, bufferSource);
        } finally {
            entity.setCustomNameVisible(prevNameVisible);
            Minecraft.getInstance().getEntityRenderDispatcher().setRenderShadow(true);
            DESATURATING.set(false);
        }
    }

    private static void renderHeighteningLabel(LivingEntity target, float partialTick, PoseStack pose, MultiBufferSource bufferSource) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) {
            return;
        }

        int ownBreath = mc.player.getCapability(BreathProvider.BREATH)
                .map(b -> b.getBreath())
                .orElse(1);
        if (Heightening.fromBreath(ownBreath).ordinal() < Heightening.FIRST.ordinal()) {
            return;
        }
        if (target.isInvisible()) {
            return;
        }

        Vec3 eye = mc.player.getEyePosition(partialTick);
        Vec3 targetCenter = target.getPosition(partialTick).add(0, target.getBbHeight() * 0.5, 0);
        HitResult trace = mc.level.clip(new ClipContext(eye, targetCenter,
                ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
        if (trace.getType() == HitResult.Type.BLOCK
                && eye.distanceToSqr(trace.getLocation()) < eye.distanceToSqr(targetCenter) - 1.0) {
            return;
        }

        int breath = target == mc.player ? ownBreath : ClientBreathData.get(target.getId());
        Heightening heightening = Heightening.fromBreath(breath);
        String text = heightening.getDisplayName();

        pose.pushPose();
        pose.translate(0.0, target.getBbHeight() + 0.75, 0.0);
        pose.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
        pose.scale(-0.025f, -0.025f, 0.025f);

        Minecraft.getInstance().font.drawInBatch(
                text,
                -Minecraft.getInstance().font.width(text) / 2f,
                0,
                0xFFFFFF,
                false,
                pose.last().pose(),
                bufferSource,
                net.minecraft.client.gui.Font.DisplayMode.NORMAL,
                0,
                0xF000F0
        );

        pose.popPose();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        updateDrabShader();
    }

    @SubscribeEvent
    public static void onRenderTick(RenderTickEvent event) {
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        updateDrabShader();
    }

    private static void updateDrabShader() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            DrabShaderManager.update(false);
            return;
        }
        int breath = mc.player.getCapability(BreathProvider.BREATH)
                .map(b -> b.getBreath())
                .orElse(1);
        DrabShaderManager.update(Heightening.fromBreath(breath) == Heightening.DRAB);
    }

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) {
            return;
        }
        int breath = mc.player.getCapability(BreathProvider.BREATH)
                .map(b -> b.getBreath())
                .orElse(1);
        Heightening heightening = Heightening.fromBreath(breath);
        if (heightening.ordinal() >= Heightening.FOURTH.ordinal()) {
            renderEntitySilhouettes(mc, event.getPartialTick(), event.getPoseStack());
        }
    }

    /**
     * Renders nearby living entities as a solid white silhouette, but only where they are
     * occluded by the existing scene. GL_GREATER means a fragment is drawn only when it is
     * behind the world geometry that has already been written to the depth buffer.
     */
    private static void renderEntitySilhouettes(Minecraft mc, float partialTick, PoseStack levelPose) {
        int breath = mc.player.getCapability(BreathProvider.BREATH)
                .map(b -> b.getBreath())
                .orElse(1);
        double range = Math.min(64.0, Math.max(2.0, breath * 0.2));
        Vec3 playerPos = mc.player.getPosition(partialTick);
        Vec3 cameraPos = mc.gameRenderer.getMainCamera().getPosition();
        EntityRenderDispatcher dispatcher = mc.getEntityRenderDispatcher();

        WhiteBufferSource visibleSource = VISIBLE_SOURCE;
        WhiteBufferSource invisibleSource = INVISIBLE_SOURCE;

        dispatcher.setRenderShadow(false);

        int prevDepthFunc = GL11.glGetInteger(GL11.GL_DEPTH_FUNC);
        boolean prevDepthMask = GL11.glGetBoolean(GL11.GL_DEPTH_WRITEMASK);
        boolean prevCull = GL11.glIsEnabled(GL11.GL_CULL_FACE);
        boolean prevBlend = GL11.glIsEnabled(GL11.GL_BLEND);

        RenderSystem.depthMask(false);
        RenderSystem.disableCull();
        RenderSystem.disableBlend();

        try {
            for (Entity entity : mc.level.entitiesForRendering()) {
                if (!(entity instanceof LivingEntity) || entity == mc.player || !entity.isAlive()) {
                    continue;
                }
                if (entity.getPosition(partialTick).distanceToSqr(playerPos) > range * range) {
                    continue;
                }

                if (Heightening.fromBreath(ClientBreathData.get(entity.getId())) == Heightening.DRAB) {
                    continue;
                }

                EntityRenderer renderer = dispatcher.getRenderer(entity);
                if (renderer == null) {
                    continue;
                }

                Vec3 entityPos = entity.getPosition(partialTick);
                WhiteBufferSource source;

                boolean wasInvisible = entity.isInvisible();
                if (wasInvisible) {
                    source = invisibleSource;
                } else {
                    Vec3 entityCenter = entityPos.add(0, entity.getBbHeight() * 0.5, 0);
                    HitResult trace = mc.level.clip(new ClipContext(cameraPos, entityCenter,
                            ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, mc.player));
                    if (trace.getType() != HitResult.Type.BLOCK
                            || trace.getLocation().distanceToSqr(cameraPos) >= entityCenter.distanceToSqr(cameraPos) - 1.0) {
                        continue;
                    }
                    source = visibleSource;
                }

                if (wasInvisible) {
                    entity.setInvisible(false);
                }

                levelPose.pushPose();
                levelPose.translate(entityPos.x - cameraPos.x, entityPos.y - cameraPos.y, entityPos.z - cameraPos.z);
                renderer.render(entity,
                        Mth.lerp(partialTick, entity.yRotO, entity.getYRot()),
                        partialTick,
                        levelPose,
                        source,
                        net.minecraft.client.renderer.LevelRenderer.getLightColor(mc.level, entity.blockPosition()));
                levelPose.popPose();

                if (wasInvisible) {
                    entity.setInvisible(true);
                }
            }

            visibleSource.endBatch();
            invisibleSource.endBatch();
        } finally {
            dispatcher.setRenderShadow(true);
            RenderSystem.depthFunc(prevDepthFunc);
            RenderSystem.depthMask(prevDepthMask);
            if (prevCull) {
                RenderSystem.enableCull();
            } else {
                RenderSystem.disableCull();
            }
            if (prevBlend) {
                RenderSystem.enableBlend();
            } else {
                RenderSystem.disableBlend();
            }
        }
    }

    private static final class WhiteBufferSource implements MultiBufferSource {
        private final MultiBufferSource.BufferSource delegate;
        private final RenderType type;

        WhiteBufferSource(MultiBufferSource.BufferSource delegate, RenderType type) {
            this.delegate = delegate;
            this.type = type;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            return new WhiteVertexConsumer(delegate.getBuffer(type));
        }

        public void endBatch() {
            delegate.endBatch();
        }
    }

    private static final class WhiteVertexConsumer implements VertexConsumer {
        private final VertexConsumer delegate;

        WhiteVertexConsumer(VertexConsumer delegate) {
            this.delegate = delegate;
        }

        @Override
        public VertexConsumer vertex(double x, double y, double z) {
            delegate.vertex(x, y, z);
            return this;
        }

        @Override
        public VertexConsumer color(int r, int g, int b, int a) {
            return this;
        }

        @Override
        public VertexConsumer color(float r, float g, float b, float a) {
            return this;
        }

        @Override
        public VertexConsumer color(int color) {
            return this;
        }

        @Override
        public VertexConsumer uv(float u, float v) {
            return this;
        }

        @Override
        public VertexConsumer overlayCoords(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer uv2(int u, int v) {
            return this;
        }

        @Override
        public VertexConsumer normal(float x, float y, float z) {
            return this;
        }

        @Override
        public void endVertex() {
            delegate.color(255, 255, 255, 255);
            delegate.endVertex();
        }

        @Override
        public void defaultColor(int r, int g, int b, int a) {
        }

        @Override
        public void unsetDefaultColor() {
        }
    }

    private static final Field SORT_ON_UPLOAD_FIELD;

    static {
        Field field = null;
        try {
            field = ObfuscationReflectionHelper.findField(RenderType.class, "sortOnUpload");
        } catch (Exception e) {
            LOGGER.warn("Could not find RenderType.sortOnUpload field; desaturated render types will not sort on upload.");
        }
        SORT_ON_UPLOAD_FIELD = field;
    }

    private static boolean getSortOnUpload(RenderType type) {
        if (SORT_ON_UPLOAD_FIELD == null) {
            return false;
        }
        try {
            return (boolean) SORT_ON_UPLOAD_FIELD.get(type);
        } catch (IllegalAccessException e) {
            return false;
        }
    }

    private static final class DesaturatedRenderType extends RenderType {
        private DesaturatedRenderType(RenderType original) {
            super("awakened_desaturated_" + original,
                    original.format(),
                    original.mode(),
                    original.bufferSize(),
                    original.affectsCrumbling(),
                    getSortOnUpload(original),
                    original::setupRenderState,
                    original::clearRenderState);
        }

        @Override
        public void setupRenderState() {
            super.setupRenderState();
            if (DrabShaderManager.DESATURATED_ENTITY_SHADER != null) {
                RenderSystem.setShader(() -> DrabShaderManager.DESATURATED_ENTITY_SHADER);
            }
        }
    }

    private static final class DesaturatedBufferSource implements MultiBufferSource {
        private final MultiBufferSource.BufferSource delegate;
        private final Map<RenderType, RenderType> cache = new IdentityHashMap<>();

        DesaturatedBufferSource(MultiBufferSource.BufferSource delegate) {
            this.delegate = delegate;
        }

        @Override
        public VertexConsumer getBuffer(RenderType renderType) {
            RenderType desaturated = cache.computeIfAbsent(renderType, DesaturatedRenderType::new);
            return delegate.getBuffer(desaturated);
        }

        public void endBatch() {
            delegate.endBatch();
        }
    }
}
