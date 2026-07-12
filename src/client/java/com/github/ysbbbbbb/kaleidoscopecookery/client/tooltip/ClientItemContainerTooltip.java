package com.github.ysbbbbbb.kaleidoscopecookery.client.tooltip;


import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ClientItemContainerTooltip implements ClientTooltipComponent {
    private static final int GRAY = 0xAAAAAA;
    private final List<ItemStack> items = new ArrayList<>();
    private @Nullable MutableComponent emptyTip = null;

    public ClientItemContainerTooltip(ItemContainerTooltip containerTooltip) {
        for (ItemStack stack : containerTooltip.items()) {
            if (!stack.isEmpty()) {
                this.items.add(stack.copy());
            }
        }
        if (items.isEmpty()) {
            this.emptyTip = Component.translatable("tooltip.kaleidoscope_cookery.item_container.empty");
        }
    }

    @Override
    public int getHeight(Font font) {
        if (emptyTip != null) {
            return 10;
        }
        return 20;
    }

    @Override
    public int getWidth(Font font) {
        if (emptyTip != null) {
            return font.width(emptyTip);
        }
        return items.size() * 20;
    }

    @Override
    public void extractImage(Font font, int pX, int pY, int width, int height, GuiGraphicsExtractor guiGraphics) {
        if (emptyTip != null) {
            guiGraphics.text(font, emptyTip, pX, pY, GRAY);
        } else {
            int i = 0;
            for (ItemStack stack : this.items) {
                int xOffset = pX + i * 20;
                guiGraphics.fakeItem(stack, xOffset, pY);
                guiGraphics.itemDecorations(font, stack, xOffset, pY);
                i++;
            }
        }
    }
}
