package me.scarletleaf1000.awakened.client.screens;

import com.mojang.blaze3d.systems.RenderSystem;
import me.scarletleaf1000.awakened.Awakened;
import me.scarletleaf1000.awakened.network.BreathNetwork;
import me.scarletleaf1000.awakened.network.VillagerTradeConfirmC2SPacket;
import me.scarletleaf1000.awakened.trade.VillagerBreathTradeMenu;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.List;

public class VillagerBreathTradeScreen extends AbstractContainerScreen<VillagerBreathTradeMenu> {
    private static final ResourceLocation BACKGROUND = new ResourceLocation(Awakened.MOD_ID, "textures/gui/container/breath_trade.png");
    private static final int BUBBLE_MAX_WIDTH = 160;
    private static final int PADDING = 5;

    private Button tradeButton;
    private boolean confirmSent;

    public VillagerBreathTradeScreen(VillagerBreathTradeMenu menu, net.minecraft.world.entity.player.Inventory playerInventory,
                                     Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 166;
    }

    @Override
    protected void init() {
        super.init();
        this.titleLabelX = (this.imageWidth - this.font.width(title)) / 2;
        this.inventoryLabelY = 1000;
        this.tradeButton = Button.builder(Component.translatable("gui.awakened.button.trade"), b -> confirm())
                .bounds(leftPos + 14, topPos + 56, 72, 20)
                .build();
        this.addRenderableWidget(this.tradeButton);

        this.addRenderableWidget(Button.builder(Component.translatable("gui.awakened.button.cancel"), b -> onClose())
                .bounds(leftPos + 90, topPos + 56, 72, 20)
                .build());
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(font, title, titleLabelX, titleLabelY, 0x404040, false);
    }

    private void confirm() {
        if (confirmSent) {
            return;
        }
        confirmSent = true;
        BreathNetwork.CHANNEL.sendToServer(new VillagerTradeConfirmC2SPacket(menu.getVillagerId()));
        setTradeButtonActive(false);
    }

    public void setTradeButtonActive(boolean active) {
        if (this.tradeButton != null) {
            this.tradeButton.active = active;
        }
    }

    public void resetAfterFailedTrade() {
        confirmSent = false;
        if (minecraft != null && minecraft.player != null) {
            setTradeButtonActive(hasEnoughEmeralds());
        }
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        if (confirmSent || this.tradeButton == null || minecraft == null || minecraft.player == null) {
            return;
        }
        this.tradeButton.active = hasEnoughEmeralds();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        guiGraphics.blit(BACKGROUND, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        drawSpeechBubble(guiGraphics, leftPos + imageWidth / 2, topPos + 40);
    }

    private void drawSpeechBubble(GuiGraphics guiGraphics, int centerX, int centerY) {
        Component message = buildMessage();
        List<FormattedCharSequence> lines = font.split(message, BUBBLE_MAX_WIDTH);
        int lineHeight = font.lineHeight;

        int bubbleWidth = 0;
        for (FormattedCharSequence line : lines) {
            bubbleWidth = Math.max(bubbleWidth, font.width(line));
        }
        bubbleWidth += PADDING * 2;
        int bubbleHeight = lines.size() * lineHeight + PADDING * 2;

        int left = centerX - bubbleWidth / 2;
        int top = centerY - bubbleHeight / 2;

        guiGraphics.fill(left, top, left + bubbleWidth, top + bubbleHeight, 0xF0101010);
        guiGraphics.renderOutline(left, top, bubbleWidth, bubbleHeight, 0xFFAAAAAA);

        int textY = top + PADDING;
        for (FormattedCharSequence line : lines) {
            int textX = centerX - font.width(line) / 2;
            guiGraphics.drawString(font, line, textX, textY, 0xFFFFFFFF, false);
            textY += lineHeight;
        }
    }

    private Component buildMessage() {
        int breath = menu.getBreathCount();
        int cost = menu.getCost();
        Component breathText = Component.translatable(
                breath == 1 ? "gui.awakened.trader.breath.singular" : "gui.awakened.trader.breath.plural", breath)
                .withStyle(ChatFormatting.BLUE);
        Component emeraldText = formatCost(cost);
        return Component.translatable("gui.awakened.trader.message", breathText, emeraldText);
    }

    private Component formatCost(int emeralds) {
        if (emeralds > 1000) {
            int blocks = emeralds / 9;
            return Component.translatable(
                    blocks == 1 ? "gui.awakened.trader.cost.block.singular" : "gui.awakened.trader.cost.block.plural", blocks)
                    .withStyle(ChatFormatting.GREEN);
        }
        if (emeralds < 64) {
            return Component.translatable("gui.awakened.trader.cost", emeralds)
                    .withStyle(ChatFormatting.GREEN);
        }
        int stacks = emeralds / 64;
        int remainder = emeralds % 64;
        if (remainder == 0) {
            return Component.translatable("gui.awakened.trader.cost.stacks", stacks)
                    .withStyle(ChatFormatting.GREEN);
        }
        return Component.translatable("gui.awakened.trader.cost.stacks_plus", stacks, remainder)
                .withStyle(ChatFormatting.GREEN);
    }

    private boolean hasEnoughEmeralds() {
        if (minecraft == null || minecraft.player == null) {
            return false;
        }
        Player player = minecraft.player;
        if (player.isCreative()) {
            return true;
        }
        int cost = menu.getCost();
        int emeralds = 0;
        int blocks = 0;
        for (ItemStack stack : player.getInventory().items) {
            if (stack.is(Items.EMERALD)) {
                emeralds += stack.getCount();
            } else if (cost > 1000 && stack.is(Items.EMERALD_BLOCK)) {
                blocks += stack.getCount();
            }
        }
        ItemStack offhand = player.getOffhandItem();
        if (offhand.is(Items.EMERALD)) {
            emeralds += offhand.getCount();
        } else if (cost > 1000 && offhand.is(Items.EMERALD_BLOCK)) {
            blocks += offhand.getCount();
        }
        return emeralds + blocks * 9 >= cost;
    }

    @Override
    public void onClose() {
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
