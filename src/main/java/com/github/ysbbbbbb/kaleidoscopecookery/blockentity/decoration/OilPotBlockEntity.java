package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.OilPotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class OilPotBlockEntity extends BaseBlockEntity {
    public static final int MAX_OIL_COUNT = 256;
    private static final String OIL_COUNT = "OilCount";

    private int oilCount = 0;

    public OilPotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.OIL_POT_BE, pos, state);
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (oilCount > 0) {
            output.store(OIL_COUNT, Codec.INT, oilCount);
        }
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        int loaded = input.read(OIL_COUNT, Codec.INT).orElse(0);
        this.oilCount = Mth.clamp(loaded, 0, MAX_OIL_COUNT);
        if (this.level != null) {
            BlockState state = this.getBlockState();
            boolean shouldHaveOil = this.oilCount > 0;
            if (state.getValue(OilPotBlock.HAS_OIL) != shouldHaveOil) {
                this.level.setBlock(this.worldPosition, state.setValue(OilPotBlock.HAS_OIL, shouldHaveOil), Block.UPDATE_ALL);
            }
        }
    }

    public int getOilCount() {
        return oilCount;
    }

    public void setOilCount(int count) {
        this.oilCount = Mth.clamp(count, 0, MAX_OIL_COUNT);
        this.setChangedAndSync();

        if (this.level == null || this.level.isClientSide()) {
            return;
        }

        BlockState state = this.getBlockState();
        boolean hasOil = state.getValue(OilPotBlock.HAS_OIL);
        boolean shouldHaveOil = this.oilCount > 0;
        if (hasOil != shouldHaveOil) {
            this.level.setBlock(this.worldPosition, state.setValue(OilPotBlock.HAS_OIL, shouldHaveOil), Block.UPDATE_ALL);
        }
        this.level.updateNeighbourForOutputSignal(this.worldPosition, state.getBlock());
    }
}
