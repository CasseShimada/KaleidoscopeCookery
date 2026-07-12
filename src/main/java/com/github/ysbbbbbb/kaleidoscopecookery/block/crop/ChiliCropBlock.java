package com.github.ysbbbbbb.kaleidoscopecookery.block.crop;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootTables;

public class ChiliCropBlock extends BaseCropBlock {
    public ChiliCropBlock(Properties properties) {
        super(properties, () -> ModItems.CHILI_SEED, ModLootTables.HARVEST_CHILI_CROP);
    }
}
