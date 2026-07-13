package com.github.ysbbbbbb.kaleidoscopecookery.inventory;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;

public class ItemStackContainer {
    protected NonNullList<ItemStack> stacks;

    public ItemStackContainer() {
        this(1);
    }

    public ItemStackContainer(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public static ItemStackContainer copyOf(NonNullList<ItemStack> stacks) {
        return copyOf(stacks, stacks.size());
    }

    public static ItemStackContainer copyOf(NonNullList<ItemStack> stacks, int size) {
        ItemStackContainer copy = new ItemStackContainer(size);
        for (int i = 0; i < Math.min(stacks.size(), copy.size()); i++) {
            copy.set(i, stacks.get(i));
        }
        return copy;
    }

    public static ItemStackContainer copyOf(ItemStackContainer items, int size) {
        return copyOf(items.stacks, size);
    }

    public void set(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        this.stacks.set(slot, stack.copy());
    }

    public int size() {
        return this.stacks.size();
    }

    public ItemStack get(int slot) {
        this.validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    public ItemStack insertItem(int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            int limit = this.getStackLimit(slot, stack);
            if (!existing.isEmpty()) {
                if (!ItemStack.isSameItemSameComponents(stack, existing)) {
                    return stack;
                }

                limit -= existing.getCount();
            }

            if (limit <= 0) {
                return stack;
            } else {
                boolean reachedLimit = stack.getCount() > limit;
                if (existing.isEmpty()) {
                    this.stacks.set(slot, stack.copyWithCount(reachedLimit ? limit : stack.getCount()));
                } else {
                    existing.grow(reachedLimit ? limit : stack.getCount());
                }

                return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    public ItemStack extractItem(int slot, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = this.stacks.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    this.stacks.set(slot, ItemStack.EMPTY);
                    return existing;
                } else {
                    this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                    return existing.copyWithCount(toExtract);
                }
            }
        }
    }

    private int getSlotLimit(int slot) {
        return 99;
    }

    protected int getStackLimit(int slot, ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            throw new IndexOutOfBoundsException("Slot " + slot + " not in valid range [0, " + this.stacks.size() + ")");
        }
    }

    public NonNullList<ItemStack> copyStacks() {
        NonNullList<ItemStack> copy = NonNullList.withSize(this.stacks.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.stacks.size(); i++) {
            copy.set(i, this.stacks.get(i).copy());
        }
        return copy;
    }
}
