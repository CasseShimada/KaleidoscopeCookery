package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyItemStackCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

public class TableBlockEntity extends BaseBlockEntity {
    private static final String COLOR_TAG = "CarpetColor";
    private static final String SHOW_ITEMS = "ShowItems";

    private DyeColor color = DyeColor.WHITE;
    private NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);

    public TableBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.TABLE_BE, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt(COLOR_TAG, this.color.getId());
        ContainerHelper.saveAllItems(output.child(SHOW_ITEMS), items);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.color = DyeColor.byId(input.getIntOr(COLOR_TAG, this.color.getId()));
        LegacyItemStackCompat.loadAllItems(input.childOrEmpty(SHOW_ITEMS), items);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
        this.setChangedAndSync();
    }

    public NonNullList<ItemStack> getItems() {
        return copyStacks(this.items);
    }

    public ItemStack getLastItem() {
        int index = this.getLastItemIndex();
        return index < 0 ? ItemStack.EMPTY : this.items.get(index).copy();
    }

    public boolean canAddItem() {
        return this.getLastItemIndex() < this.items.size() - 1;
    }

    public boolean addItem(ItemStack stack) {
        if (this.level == null || this.level.isClientSide() || stack.isEmpty()) {
            return false;
        }
        int index = this.getLastItemIndex() + 1;
        if (index >= this.items.size()) {
            return false;
        }
        this.items.set(index, stack.copyWithCount(1));
        this.setChangedAndSync();
        return true;
    }

    public ItemStack removeLastItem() {
        if (this.level == null || this.level.isClientSide()) {
            return ItemStack.EMPTY;
        }
        int index = this.getLastItemIndex();
        if (index < 0) {
            return ItemStack.EMPTY;
        }
        ItemStack removed = this.items.get(index).copy();
        this.items.set(index, ItemStack.EMPTY);
        this.setChangedAndSync();
        return removed;
    }

    private int getLastItemIndex() {
        for (int i = this.items.size() - 1; i >= 0; i--) {
            if (!this.items.get(i).isEmpty()) {
                return i;
            }
        }
        return -1;
    }
}
