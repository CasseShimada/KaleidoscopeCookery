package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.core.BlockPos;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import net.minecraft.world.entity.Entity;
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

    public static boolean isHidingInTrashCan(Entity entity) {
        return entity instanceof Player
                && entity.getVehicle() instanceof SitEntity sitEntity
                && sitEntity.getSitType() == SitEntity.TRASH_CAN;
    }

    private static void clearTargets(Level level, AABB area, LivingEntity target) {
        level.getEntitiesOfClass(Mob.class, area, mob -> mob.getTarget() == target)
                .forEach(mob -> mob.setTarget(null));
    }
}
