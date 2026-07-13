package com.github.ysbbbbbb.kaleidoscopecookery.inventory;
import com.mojang.serialization.Codec;
import net.minecraft.core.NonNullList;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;

public final class ItemStackContainer {
    public static final Codec<ItemContainerContents> CONTENTS_CODEC = Codec.withAlternative(
            ItemContainerContents.CODEC,
            ItemStack.OPTIONAL_CODEC.listOf(),
            ItemContainerContents::fromItems
    );

    private final SimpleContainer container;

    public ItemStackContainer(int size) {
        this.container = new SimpleContainer(size);
    }

    public static ItemStackContainer copyOf(NonNullList<ItemStack> stacks) {
        return copyOf(stacks, stacks.size());
    }

    private static ItemStackContainer copyOf(NonNullList<ItemStack> stacks, int size) {
        ItemStackContainer copy = new ItemStackContainer(size);
        for (int i = 0; i < Math.min(stacks.size(), copy.size()); i++) {
            copy.set(i, stacks.get(i));
        }
        return copy;
    }

    public static ItemStackContainer copyOf(ItemStackContainer items, int size) {
        return copyOf(items.copyStacks(), size);
    }

    public static ItemStackContainer fromContents(ItemContainerContents contents, int size) {
        NonNullList<ItemStack> stacks = NonNullList.withSize(size, ItemStack.EMPTY);
        contents.copyInto(stacks);
        return copyOf(stacks, size);
    }

    public void set(int slot, ItemStack stack) {
        this.validateSlotIndex(slot);
        // Preserve serialized contents as-is; vanilla insertion still enforces stack limits.
        this.container.getItems().set(slot, stack.copy());
    }

    public int size() {
        return this.container.getContainerSize();
    }

    public ItemStack get(int slot) {
        this.validateSlotIndex(slot);
        return this.container.getItem(slot).copy();
    }

    public ItemStack addItem(ItemStack stack) {
        return this.container.addItem(stack);
    }

    public ItemStack extractItem(int slot, int amount) {
        if (amount <= 0) {
            return ItemStack.EMPTY;
        }
        this.validateSlotIndex(slot);
        return this.container.removeItem(slot, amount);
    }

    private void validateSlotIndex(int slot) {
        if (slot < 0 || slot >= this.size()) {
            throw new IndexOutOfBoundsException("Slot " + slot + " not in valid range [0, " + this.size() + ")");
        }
    }

    public NonNullList<ItemStack> copyStacks() {
        NonNullList<ItemStack> copy = NonNullList.withSize(this.size(), ItemStack.EMPTY);
        for (int i = 0; i < this.size(); i++) {
            copy.set(i, this.container.getItem(i).copy());
        }
        return copy;
    }

    public ItemContainerContents toContents() {
        return ItemContainerContents.fromItems(this.copyStacks());
    }
}
