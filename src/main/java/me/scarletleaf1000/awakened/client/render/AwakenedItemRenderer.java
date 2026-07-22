package me.scarletleaf1000.awakened.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import me.scarletleaf1000.awakened.entity.AwakenedItemEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;

/**
 * Renders an awakened item entity as the item it was created from.
 */
public class AwakenedItemRenderer extends EntityRenderer<AwakenedItemEntity> {

    public AwakenedItemRenderer(EntityRendererProvider.Context context) {
        super(context);
        this.shadowRadius = 0.25f;
        this.shadowStrength = 0.75f;
    }

    @Override
    public void render(AwakenedItemEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        poseStack.pushPose();
        poseStack.translate(0.0F, entity.getBbHeight() / 2.0F + 0.1F, 0.0F);

        boolean block = entity.getItem().getItem() instanceof net.minecraft.world.item.BlockItem;
        float scale = block ? 2.5F : 2.0F;
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                entity.getItem(),
                ItemDisplayContext.GROUND,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                buffer,
                entity.level(),
                entity.getId()
        );
        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, buffer, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(AwakenedItemEntity entity) {
        return TextureAtlas.LOCATION_BLOCKS;
    }
}
