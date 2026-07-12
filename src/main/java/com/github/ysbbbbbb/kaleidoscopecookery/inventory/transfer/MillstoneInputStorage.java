package com.github.ysbbbbbb.kaleidoscopecookery.inventory.transfer;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.item.ItemStack;

import java.util.Iterator;
import java.util.List;

public class MillstoneInputStorage extends SnapshotParticipant<ItemStack> implements SingleSlotStorage<ItemVariant> {
    private final MillstoneBlockEntity millstone;
    private ItemStack pending = ItemStack.EMPTY;

    public MillstoneInputStorage(MillstoneBlockEntity millstone) {
        this.millstone = millstone;
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0 || this.millstone.getLevel() == null || !this.millstone.canAutomationInsert()) {
            return 0;
        }

        if (!this.pending.isEmpty() && !resource.matches(this.pending)) {
            return 0;
        }

        int capacity = MillstoneBlockEntity.MAX_INPUT_COUNT - this.pending.getCount();
        if (capacity <= 0) {
            return 0;
        }

        int insertAmount = (int) Math.min(maxAmount, capacity);
        ItemStack stack = resource.toStack(insertAmount);
        if (this.pending.isEmpty() && !this.millstone.canAutomationInsert(stack)) {
            return 0;
        }

        this.updateSnapshots(transaction);
        if (this.pending.isEmpty()) {
            this.pending = stack.copy();
        } else {
            this.pending.grow(insertAmount);
        }
        return insertAmount;
    }

    @Override
    public boolean supportsExtraction() {
        return false;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        return List.<StorageView<ItemVariant>>of(this).iterator();
    }

    @Override
    public boolean isResourceBlank() {
        return this.millstone.getInput().isEmpty() && this.pending.isEmpty();
    }

    @Override
    public ItemVariant getResource() {
        ItemStack input = this.millstone.getInput();
        if (!input.isEmpty()) {
            return ItemVariant.of(input);
        }
        return this.pending.isEmpty() ? ItemVariant.blank() : ItemVariant.of(this.pending);
    }

    @Override
    public long getAmount() {
        ItemStack input = this.millstone.getInput();
        return input.isEmpty() ? this.pending.getCount() : input.getCount();
    }

    @Override
    public long getCapacity() {
        return MillstoneBlockEntity.MAX_INPUT_COUNT;
    }

    @Override
    protected ItemStack createSnapshot() {
        return this.pending.copy();
    }

    @Override
    protected void readSnapshot(ItemStack snapshot) {
        this.pending = snapshot.copy();
    }

    @Override
    protected void onFinalCommit() {
        if (!this.pending.isEmpty() && this.millstone.getLevel() != null && this.millstone.canAutomationInsert(this.pending)) {
            this.millstone.onPutItem(this.millstone.getLevel(), this.pending.copy());
        }
        this.pending = ItemStack.EMPTY;
    }
}
