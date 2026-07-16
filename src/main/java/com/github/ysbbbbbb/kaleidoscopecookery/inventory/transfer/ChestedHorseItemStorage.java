package com.github.ysbbbbbb.kaleidoscopecookery.inventory.transfer;

import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public final class ChestedHorseItemStorage extends SnapshotParticipant<List<ItemStack>>
        implements Storage<ItemVariant> {
    private final AbstractChestedHorse horse;

    public ChestedHorseItemStorage(AbstractChestedHorse horse) {
        this.horse = horse;
    }

    @Override
    public boolean supportsInsertion() {
        return false;
    }

    @Override
    public long insert(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        return 0;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
        if (resource.isBlank() || maxAmount <= 0) {
            return 0;
        }

        long extracted = 0;
        for (int slot = 0; slot < this.horse.getInventorySize() && extracted < maxAmount; slot++) {
            extracted += this.extractFromSlot(slot, resource, maxAmount - extracted, transaction);
        }
        return extracted;
    }

    @Override
    public Iterator<StorageView<ItemVariant>> iterator() {
        List<StorageView<ItemVariant>> views = new ArrayList<>(this.horse.getInventorySize());
        for (int slot = 0; slot < this.horse.getInventorySize(); slot++) {
            views.add(new HorseSlotView(slot));
        }
        return views.iterator();
    }

    private long extractFromSlot(int slotIndex, ItemVariant resource, long maxAmount,
                                 TransactionContext transaction) {
        SlotAccess slot = this.slot(slotIndex);
        ItemStack stack = slot.get();
        if (stack.isEmpty() || !resource.matches(stack) || maxAmount <= 0) {
            return 0;
        }

        int extracted = (int) Math.min(maxAmount, stack.getCount());
        this.updateSnapshots(transaction);
        int remaining = stack.getCount() - extracted;
        return slot.set(remaining > 0 ? stack.copyWithCount(remaining) : ItemStack.EMPTY)
                ? extracted
                : 0;
    }

    @Override
    protected List<ItemStack> createSnapshot() {
        List<ItemStack> snapshot = new ArrayList<>(this.horse.getInventorySize());
        for (int slot = 0; slot < this.horse.getInventorySize(); slot++) {
            snapshot.add(this.slot(slot).get().copy());
        }
        return snapshot;
    }

    @Override
    protected void readSnapshot(List<ItemStack> snapshot) {
        int slotCount = Math.min(this.horse.getInventorySize(), snapshot.size());
        for (int slot = 0; slot < slotCount; slot++) {
            this.slot(slot).set(snapshot.get(slot).copy());
        }
    }

    private SlotAccess slot(int inventoryIndex) {
        return this.horse.getSlot(AbstractHorse.INVENTORY_SLOT_OFFSET + inventoryIndex);
    }

    private final class HorseSlotView implements StorageView<ItemVariant> {
        private final int slotIndex;

        private HorseSlotView(int slotIndex) {
            this.slotIndex = slotIndex;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, TransactionContext transaction) {
            return ChestedHorseItemStorage.this.extractFromSlot(this.slotIndex, resource, maxAmount, transaction);
        }

        @Override
        public boolean isResourceBlank() {
            return this.stack().isEmpty();
        }

        @Override
        public ItemVariant getResource() {
            ItemStack stack = this.stack();
            return stack.isEmpty() ? ItemVariant.blank() : ItemVariant.of(stack);
        }

        @Override
        public long getAmount() {
            return this.stack().getCount();
        }

        @Override
        public long getCapacity() {
            ItemStack stack = this.stack();
            return stack.isEmpty() ? 0 : stack.getMaxStackSize();
        }

        private ItemStack stack() {
            return ChestedHorseItemStorage.this.slot(this.slotIndex).get();
        }
    }
}
