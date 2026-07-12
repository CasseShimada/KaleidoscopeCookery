package com.github.ysbbbbbb.kaleidoscopecookery.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModAttachmentType.FLATULENCE_EFFECT_STARTING_POSITION;

public class FlatulenceEffect extends CookeryEffect {
    public FlatulenceEffect(int color) {
        super(color);
    }

    @Override
    public boolean shouldApplyEffectTickThisTick(int duration, int amplifier) {
        // 每 10 tick 检查一次距离
        return duration % 10 == 5;
    }

    @Override
    public boolean applyEffectTick(ServerLevel level, LivingEntity livingEntity, int amplifier) {
        if (!(livingEntity instanceof ServerPlayer serverPlayer)) {
            return true;
        }
        updateStartingPosition(serverPlayer);
        return true;
    }

    private static void updateStartingPosition(ServerPlayer player) {
        if (!player.hasAttached(FLATULENCE_EFFECT_STARTING_POSITION)) {
            player.setAttached(FLATULENCE_EFFECT_STARTING_POSITION, player.position());
            return;
        }
        Vec3 startingPosition = player.getAttached(FLATULENCE_EFFECT_STARTING_POSITION);
        // 检查坐标是否改变，触发触发器
        ModTrigger.FLATULENCE_FLY_HEIGHT.trigger(player, startingPosition);
    }
}
