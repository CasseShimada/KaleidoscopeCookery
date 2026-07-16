package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyItemStackCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

public class RecipeBlockEntity extends BaseBlockEntity {
    private static final String SHOW_ITEMS = "ShowItems";
    private ItemStack item = ItemStack.EMPTY;

    public RecipeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.RECIPE_BLOCK_BE, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.item.isEmpty()) {
            output.store(SHOW_ITEMS, ItemStack.CODEC, this.item);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.item = LegacyItemStackCompat.readItemStackOrHandler(input, SHOW_ITEMS, 1);
    }

    public ItemStack getItem() {
        return this.item.copy();
    }

    public void setItem(ItemStack stack) {
        this.item = stack.copyWithCount(1);
        this.setChangedAndSync();
    }
}
