package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
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
        ContainerHelper.loadAllItems(input.childOrEmpty(SHOW_ITEMS), items);
    }

    public DyeColor getColor() {
        return this.color;
    }

    public void setColor(DyeColor color) {
        this.color = color;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }
}
