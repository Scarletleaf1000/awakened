package me.scarletleaf1000.awakened.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractAwakeningScreen extends Screen {
    private final ResourceLocation background;
    protected final int imageWidth;
    protected final int imageHeight;
    private final Component defaultDescription;
    protected final Map<AbstractWidget, Component> descriptions = new HashMap<>();

    protected int leftPos;
    protected int topPos;

    protected AbstractAwakeningScreen(Component title, ResourceLocation background, int imageWidth, int imageHeight, Component defaultDescription) {
        super(title);
        this.background = background;
        this.imageWidth = imageWidth;
        this.imageHeight = imageHeight;
        this.defaultDescription = defaultDescription;
    }

    @Override
    protected void init() {
        super.init();
        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight - 50) / 2;
    }

    protected void addWidgetWithDescription(AbstractWidget widget, Component description) {
        this.addRenderableWidget(widget);
        this.descriptions.put(widget, description);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
        guiGraphics.fill(this.leftPos, this.topPos, this.leftPos + this.imageWidth, this.topPos + this.imageHeight, 0xFFC0C0C0);
        guiGraphics.renderOutline(this.leftPos, this.topPos, this.imageWidth, this.imageHeight, 0xFF333333);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTitle(guiGraphics);
        this.renderDescription(guiGraphics, mouseX, mouseY);
    }

    private void renderTitle(GuiGraphics guiGraphics) {
        int titleWidth = this.font.width(this.title);
        int titleX = this.leftPos + (this.imageWidth - titleWidth) / 2;
        guiGraphics.drawString(this.font, this.title, titleX, this.topPos + 6, 0x404040, false);
    }

    private void renderDescription(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Component description = this.defaultDescription;
        for (Map.Entry<AbstractWidget, Component> entry : this.descriptions.entrySet()) {
            if (entry.getKey().isMouseOver(mouseX, mouseY)) {
                description = entry.getValue();
                break;
            }
        }

        int boxTop = this.topPos + this.imageHeight + 10;
        int boxHeight = 40;
        guiGraphics.fill(this.leftPos, boxTop, this.leftPos + this.imageWidth, boxTop + boxHeight, 0xF0101010);
        guiGraphics.renderOutline(this.leftPos, boxTop, this.imageWidth, boxHeight, 0xFFAAAAAA);

        List<FormattedCharSequence> lines = this.font.split(description, this.imageWidth - 10);
        int textY = boxTop + 5;
        for (FormattedCharSequence line : lines) {
            int textX = this.leftPos + (this.imageWidth - this.font.width(line)) / 2;
            guiGraphics.drawString(this.font, line, textX, textY, 0xFFFFFFFF, false);
            textY += this.font.lineHeight;
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
