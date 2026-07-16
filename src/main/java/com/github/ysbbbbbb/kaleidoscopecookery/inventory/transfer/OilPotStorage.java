package com.github.ysbbbbbb.kaleidoscopecookery.inventory.transfer;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleSlotStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;

public class OilPotStorage extends SnapshotParticipant<Integer> implements SingleSlotStorage<ItemVariant> {
    private static final ItemVariant OIL = ItemVariant.of(ModItems.OIL);

    private final OilPotBlockEntity oilPot;
    private int pendingCount = -1;

    public OilPotStorage(OilPotBlockEntity oilPot) {
        this.oilPot = oilPot;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || resource.getItem() != ModItems.OIL || maxAmount <= 0) {
            return 0;
        }
        int currentCount = currentCount();
        int inserted = (int) Math.min(maxAmount, OilPotBlockEntity.MAX_OIL_COUNT - currentCount);
        if (inserted <= 0) {
            return 0;
        }
        this.updateSnapshots(transaction);
        this.pendingCount = currentCount + inserted;
        return inserted;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || resource.getItem() != ModItems.OIL || maxAmount <= 0) {
            return 0;
        }
        int currentCount = currentCount();
        int extracted = (int) Math.min(maxAmount, currentCount);
        if (extracted <= 0) {
            return 0;
        }
        this.updateSnapshots(transaction);
        this.pendingCount = currentCount - extracted;
        return extracted;
    }

    @Override
    public boolean isResourceBlank() {
        return currentCount() == 0;
    }

    @Override
    public ItemVariant getResource() {
        return isResourceBlank() ? ItemVariant.blank() : OIL;
    }

    @Override
    public long getAmount() {
        return currentCount();
    }

    @Override
    public long getCapacity() {
        return OilPotBlockEntity.MAX_OIL_COUNT;
    }

    @Override
    protected Integer createSnapshot() {
        return this.pendingCount;
    }

    @Override
    protected void readSnapshot(Integer snapshot) {
        this.pendingCount = snapshot;
    }

    @Override
    protected void onFinalCommit() {
        if (this.pendingCount >= 0) {
            this.oilPot.setOilCount(this.pendingCount);
            this.pendingCount = -1;
        }
    }

    private int currentCount() {
        return this.pendingCount >= 0 ? this.pendingCount : this.oilPot.getOilCount();
    }
}
