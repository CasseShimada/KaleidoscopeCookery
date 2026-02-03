package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

public class RecipeBlockEntity extends BaseBlockEntity {
    private static final String SHOW_ITEMS = "ShowItems";
    private final ItemStackHandler items = new ItemStackHandler(1);

    public RecipeBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.RECIPE_BLOCK_BE, pos, blockState);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ItemStack stack = this.items.getStackInSlot(0);
        if (!stack.isEmpty()) {
            output.store(SHOW_ITEMS, ItemStack.CODEC, stack);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.items.setStackInSlot(0, ItemStack.EMPTY);
        input.read(SHOW_ITEMS, ItemStack.CODEC).ifPresent(stack -> this.items.setStackInSlot(0, stack));
    }

    public ItemStackHandler getItems() {
        return items;
    }
}
