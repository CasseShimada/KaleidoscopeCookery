package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum FoodBiteBlockComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
        BlockState state = accessor.getBlockState();
        int quality = state.getValue(FoodBiteBlock.QUALITY);
        if (quality != FoodBiteBlock.DEFAULT_QUALITY) {
            tooltip.add(Quality.BY_ID.apply(quality).getTooltip());
        }
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.FOOD_BITE_BLOCK;
    }
}
