package com.github.ysbbbbbb.kaleidoscopecookery.util.neo;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

//From Neoforge
public class ItemStackHandler implements IItemHandler {
    protected NonNullList<ItemStack> stacks;

    public ItemStackHandler() {
        this(1);
    }

    public ItemStackHandler(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public ItemStackHandler(NonNullList<ItemStack> stacks) {
        this.stacks = stacks;
    }

    public ItemStackHandler(Inventory inventory) {
        this(inventory.getContainerSize());
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            this.stacks.set(i, inventory.getItem(i));
        }
    }

    public void setSize(int size) {
        this.stacks = NonNullList.withSize(size, ItemStack.EMPTY);
    }

    public void setStackInSlot(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        this.stacks.set(slot, stack);
        this.onContentsChanged(slot);
    }

    public int getSlots() {
        return this.stacks.size();
    }

    public ItemStack getStackInSlot(int slot) {
        this.validateSlotIndex(slot);
        return this.stacks.get(slot);
    }

    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        } else if (!this.isItemValid(slot, stack)) {
            return stack;
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
                if (!simulate) {
                    if (existing.isEmpty()) {
                        this.stacks.set(slot, reachedLimit ? stack.copyWithCount(limit) : stack);
                    } else {
                        existing.grow(reachedLimit ? limit : stack.getCount());
                    }

                    this.onContentsChanged(slot);
                }

                return reachedLimit ? stack.copyWithCount(stack.getCount() - limit) : ItemStack.EMPTY;
            }
        }
    }

    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (amount == 0) {
            return ItemStack.EMPTY;
        } else {
            this.validateSlotIndex(slot);
            ItemStack existing = (ItemStack)this.stacks.get(slot);
            if (existing.isEmpty()) {
                return ItemStack.EMPTY;
            } else {
                int toExtract = Math.min(amount, existing.getMaxStackSize());
                if (existing.getCount() <= toExtract) {
                    if (!simulate) {
                        this.stacks.set(slot, ItemStack.EMPTY);
                        this.onContentsChanged(slot);
                        return existing;
                    } else {
                        return existing.copy();
                    }
                } else {
                    if (!simulate) {
                        this.stacks.set(slot, existing.copyWithCount(existing.getCount() - toExtract));
                        this.onContentsChanged(slot);
                    }

                    return existing.copyWithCount(toExtract);
                }
            }
        }
    }
    public int getSlotLimit(int slot) {
        return 99;
    }

    protected int getStackLimit(int slot, ItemStack stack) {
        return Math.min(this.getSlotLimit(slot), stack.getMaxStackSize());
    }

    public boolean isItemValid(int slot, ItemStack stack) {
        return true;
    }

    public CompoundTag serializeNBT(HolderLookup.Provider provider) {
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, provider);
        ContainerHelper.saveAllItems(output, this.stacks);
        output.putInt("Size", this.stacks.size());
        return output.buildResult();
    }

    public void deserializeNBT(HolderLookup.Provider provider, CompoundTag nbt) {
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, provider, nbt);
        this.setSize(input.getIntOr("Size", this.stacks.size()));
        ContainerHelper.loadAllItems(input, this.stacks);

        this.onLoad();
    }

    protected void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.stacks.size()) {
            throw new RuntimeException("Slot " + slot + " not in valid range - [0," + this.stacks.size() + ")");
        }
    }

    protected void onLoad() {
    }

    protected void onContentsChanged(int slot) {
    }

    public NonNullList<ItemStack> getStacks() {
        return stacks;
    }
}
