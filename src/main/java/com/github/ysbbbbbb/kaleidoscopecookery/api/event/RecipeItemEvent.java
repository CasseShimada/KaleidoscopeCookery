package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

/**
 * 当菜谱进行物品放入时触发
 */
public abstract class RecipeItemEvent {
    private final ItemStack stack;
    @Nullable
    private final ContainerItemContext containerContext;

    public RecipeItemEvent(ItemStack stack) {
        this(stack, null);
    }

    public RecipeItemEvent(ItemStack stack, @Nullable ContainerItemContext containerContext) {
        this.stack = stack;
        this.containerContext = containerContext;
    }

    public ItemStack getStack() {
        return stack;
    }

    @Nullable
    public ContainerItemContext getContainerContext() {
        return containerContext;
    }

    public static class CheckItem extends RecipeItemEvent {
        private final Reference2IntMap<Item> supply;

        public CheckItem(ItemStack stack, Reference2IntMap<Item> supply) {
            super(stack);
            this.supply = supply;
        }

        public CheckItem(ItemStack stack, Reference2IntMap<Item> supply,
                         ContainerItemContext containerContext) {
            super(stack, containerContext);
            this.supply = supply;
        }

        public void addItem(Item item, int count) {
            this.supply.mergeInt(item, count, Integer::sum);
        }
    }

    public static class DeductItem extends RecipeItemEvent {
        private final Item needItem;
        private int needCount;

        public DeductItem(ItemStack stack, Item needItem, int needCount) {
            super(stack);
            this.needItem = needItem;
            this.needCount = needCount;
        }

        public DeductItem(ItemStack stack, Item needItem, int needCount,
                          ContainerItemContext containerContext) {
            super(stack, containerContext);
            this.needItem = needItem;
            this.needCount = needCount;
        }

        public Item getNeedItem() {
            return needItem;
        }

        public int getNeedCount() {
            return this.needCount;
        }

        public void deduct(int count) {
            this.needCount = Math.max(this.needCount - count, 0);
        }
    }
}
