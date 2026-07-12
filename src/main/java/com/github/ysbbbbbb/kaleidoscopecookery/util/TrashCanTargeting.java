package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public final class TrashCanTargeting {
    private static final double TARGET_CLEAR_RANGE = 32.0D;

    private TrashCanTargeting() {
    }

    public static void clearTargetsAroundBlock(Level level, BlockPos pos, Player player) {
        clearTargets(level, new AABB(pos).inflate(TARGET_CLEAR_RANGE), player);
    }

    public static void clearTargetsAroundPlayer(Player player) {
        clearTargets(player.level(), player.getBoundingBox().inflate(TARGET_CLEAR_RANGE), player);
    }

    private static void clearTargets(Level level, AABB area, LivingEntity target) {
        level.getEntitiesOfClass(Mob.class, area, mob -> mob.getTarget() == target)
                .forEach(mob -> mob.setTarget(null));
    }
}
