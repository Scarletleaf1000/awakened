package me.scarletleaf1000.awakened.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import me.scarletleaf1000.awakened.item.ItemBreathStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.IItemDecorator;

public class InvestedGlintRenderer implements IItemDecorator {
    public static final InvestedGlintRenderer INSTANCE = new InvestedGlintRenderer();

    @Override
    public boolean render(GuiGraphics guiGraphics, Font font, ItemStack stack, int xOffset, int yOffset) {
        if (!ItemBreathStorage.hasStoredBreath(stack)) {
            return false;
        }

        Minecraft minecraft = Minecraft.getInstance();
        ItemRenderer itemRenderer = minecraft.getItemRenderer();
        BakedModel model = itemRenderer.getModel(stack, null, null, 0);
        if (model == null) {
            return false;
        }

        // Draw a vanilla enchantment glint over the item by re-rendering the item model
        // with the vanilla glint RenderType. This matches the item shape and draws on top.
        VertexConsumer glintConsumer = guiGraphics.bufferSource().getBuffer(RenderType.glint());
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 0.75f);

        PoseStack pose = new PoseStack();
        pose.pushPose();
        pose.translate(xOffset + 8.0f, yOffset + 8.0f, 100.0f);
        pose.scale(1.0f, -1.0f, 1.0f);
        pose.scale(16.0f, 16.0f, 16.0f);

        itemRenderer.renderModelLists(model, stack, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY, pose, glintConsumer);

        pose.popPose();
        guiGraphics.flush();

        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        return true;
    }
}
