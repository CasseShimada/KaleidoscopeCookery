package com.github.ysbbbbbb.kaleidoscopecookery.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class WarmthEffect extends CookeryEffect {
    public WarmthEffect(int color) {
        super(color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 为了避免卡顿，每秒检查一次
        return duration % 25 == 0;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        if (livingEntity.getHealth() >= livingEntity.getMaxHealth()) {
            return true;
        }
        // 当玩家周围 5x5x3 范围内有热源时，恢复玩家生命值
        BlockPos center = livingEntity.blockPosition();
        for (BlockPos candidate : BlockPos.betweenClosed(
                center.offset(-2, -1, -2), center.offset(2, 1, 2))) {
            BlockState blockState = level.getBlockState(candidate);
            boolean hasLit = blockState.hasProperty(BlockStateProperties.LIT) && blockState.getValue(BlockStateProperties.LIT);
            if (hasLit || blockState.is(TagMod.WARMTH_HEAT_SOURCE_BLOCKS)) {
                livingEntity.heal(1);
                // 找到热源后立即返回，避免重复恢复
                return true;
            }
        }
        // 如果玩家在下界，那么缓慢恢复
        if (level.dimension().equals(Level.NETHER)) {
            // 缓慢的话。那就概率恢复
            if (livingEntity.getRandom().nextInt(4) == 0) {
                livingEntity.heal(0.5F);
            }
        }
        return true;
    }
}
