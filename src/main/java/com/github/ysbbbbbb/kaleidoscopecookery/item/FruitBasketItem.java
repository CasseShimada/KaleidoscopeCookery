package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;


public class FruitBasketItem extends CookeryTooltipBlockItem {

    private static final int MAX_SLOTS = 8;

    public FruitBasketItem(Properties properties) {
        super(ModBlocks.FRUIT_BASKET, properties.stacksTo(1));
    }

    public static ItemStackContainer getItems(ItemStack stack) {
        ItemContainer container = stack.get(ModDataComponents.FRUIT_BASKET_ITEMS);
        if (container != null) {
            return container.toContainer();
        }
        return new ItemStackContainer(MAX_SLOTS);
    }

    public static void saveItems(ItemStack stack, ItemStackContainer items) {
        stack.set(ModDataComponents.FRUIT_BASKET_ITEMS, ItemContainer.of(items));
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (stack.has(ModDataComponents.FRUIT_BASKET_ITEMS)) {
            ItemContainer handler = stack.get(ModDataComponents.FRUIT_BASKET_ITEMS);
            assert handler != null;
            return Optional.of(new ItemContainerTooltip(handler.toContainer().copyStacks()));
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

    public record ItemContainer(NonNullList<ItemStack> items) {
        public ItemContainer {
            items = copyItems(items);
        }

        public static ItemContainer of(ItemStackContainer items) {
            return new ItemContainer(items.copyStacks());
        }

        public static ItemContainer of(NonNullList<ItemStack> items) {
            return new ItemContainer(items);
        }

        @Override
        public NonNullList<ItemStack> items() {
            return copyItems(this.items);
        }

        public ItemStackContainer toContainer() {
            return ItemStackContainer.copyOf(this.items);
        }

        private static NonNullList<ItemStack> copyItems(NonNullList<ItemStack> items) {
            NonNullList<ItemStack> copy = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
            for (int i = 0; i < Math.min(items.size(), copy.size()); i++) {
                copy.set(i, items.get(i).copy());
            }
            return copy;
        }

        private static ItemContainer fromList(List<ItemStack> list) {
            NonNullList<ItemStack> handler = NonNullList.withSize(MAX_SLOTS, ItemStack.EMPTY);
            for (int i = 0; i < Math.min(list.size(), handler.size()); i++) {
                handler.set(i, list.get(i).copy());
            }
            return new ItemContainer(handler);
        }

        private List<ItemStack> itemsForCodec() {
            return this.items();
        }

        public static final Codec<ItemContainer> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(
                ItemContainer::fromList,
                ItemContainer::itemsForCodec
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainer> STREAM_CODEC =
                ItemStack.OPTIONAL_LIST_STREAM_CODEC.map(ItemContainer::fromList, ItemContainer::itemsForCodec);
    }
}
