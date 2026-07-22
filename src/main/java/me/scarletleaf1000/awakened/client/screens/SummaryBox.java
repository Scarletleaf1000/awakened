package me.scarletleaf1000.awakened.client.screens;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class SummaryBox extends AbstractWidget {
    private final Font font;
    private Component text;

    public SummaryBox(Font font, int x, int y, int width, int height, Component text) {
        super(x, y, width, height, Component.empty());
        this.font = font;
        this.text = text;
    }

    public void setText(Component text) {
        this.text = text;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.fill(this.getX(), this.getY(), this.getX() + this.width, this.getY() + this.height, 0xF0101010);
        guiGraphics.renderOutline(this.getX(), this.getY(), this.width, this.height, 0xFFAAAAAA);

        List<FormattedCharSequence> lines = this.font.split(this.text, this.width - 10);
        int textY = this.getY() + 5;
        for (FormattedCharSequence line : lines) {
            int textX = this.getX() + (this.width - this.font.width(line)) / 2;
            guiGraphics.drawString(this.font, line, textX, textY, 0xFFFFFFFF, false);
            textY += this.font.lineHeight;
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
    }
}
