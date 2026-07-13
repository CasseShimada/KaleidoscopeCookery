package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Consumer;


public class FruitBasketItem extends CookeryTooltipBlockItem {

    private static final int MAX_SLOTS = 8;

    public FruitBasketItem(Properties properties) {
        super(ModBlocks.FRUIT_BASKET, properties.stacksTo(1));
    }

    public static ItemStackContainer getItems(ItemStack stack) {
        ItemContainerContents contents = stack.get(ModDataComponents.FRUIT_BASKET_ITEMS);
        if (contents != null) {
            return ItemStackContainer.fromContents(contents, MAX_SLOTS);
        }
        return new ItemStackContainer(MAX_SLOTS);
    }

    public static void saveItems(ItemStack stack, ItemStackContainer items) {
        stack.set(ModDataComponents.FRUIT_BASKET_ITEMS, items.toContents());
    }

    public static void saveItems(ItemStack stack, NonNullList<ItemStack> items) {
        stack.set(ModDataComponents.FRUIT_BASKET_ITEMS, ItemContainerContents.fromItems(items));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.has(ModDataComponents.FRUIT_BASKET_ITEMS)) {
            return Optional.of(new ItemContainerTooltip(getItems(stack).copyStacks()));
        }
        return Optional.empty();
    }

    @Override
    public boolean canFitInsideContainerItems() {
        return false;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.fruit_basket").withStyle(ChatFormatting.GRAY));
    }

}
