package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import it.unimi.dsi.fastutil.objects.Reference2IntMap;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * 当菜谱进行物品放入时触发
 */
public abstract class RecipeItemEvent {
    private final ItemStack stack;

    public RecipeItemEvent(ItemStack stack) {
        this.stack = stack;
    }

    public ItemStack getStack() {
        return stack;
    }

    public static class CheckItem extends RecipeItemEvent {
        private final Reference2IntMap<Item> supply;

        public CheckItem(ItemStack stack, Reference2IntMap<Item> supply) {
            super(stack);
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
