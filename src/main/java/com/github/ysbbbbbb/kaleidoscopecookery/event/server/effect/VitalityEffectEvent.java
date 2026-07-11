package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.fabricmc.fabric.api.entity.event.v1.ServerEntityCombatEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.phys.Vec3;

public final class VitalityEffectEvent {
    private static final int ZOMBIE_BABY_VILLAGER_CHANCE = 20;

    private VitalityEffectEvent() {
    }

    public static void register() {
        ServerEntityCombatEvents.AFTER_KILLED_OTHER_ENTITY.register(VitalityEffectEvent::onAfterKilledOtherEntity);
    }

    private static void onAfterKilledOtherEntity(ServerLevel level, Entity killer, LivingEntity killed,
                                                 DamageSource source) {
        if (!(killer instanceof LivingEntity living) || !living.hasEffect(ModEffects.VITALITY)) {
            return;
        }

        Vec3 pos = killed.position();
        if (trySpawnAgeableBaby(level, killed, pos)) {
            return;
        }

        if (killed instanceof Zombie zombie && !zombie.isBaby()) {
            spawnZombieBabyOrVillager(level, killed.getType(), pos);
        }
    }

    private static boolean trySpawnAgeableBaby(ServerLevel level, LivingEntity killed, Vec3 pos) {
        if (!(killed instanceof AgeableMob mob) || mob.isBaby()) {
            return false;
        }
        if (killed.getType().create(level, EntitySpawnReason.TRIGGERED) instanceof AgeableMob child) {
            spawnAgeableBaby(level, child, pos);
        }
        return true;
    }

    private static void spawnZombieBabyOrVillager(ServerLevel level, EntityType<?> type, Vec3 pos) {
        if (level.getRandom().nextInt(ZOMBIE_BABY_VILLAGER_CHANCE) == 0) {
            spawnAgeableBaby(level, new Villager(EntityTypes.VILLAGER, level), pos);
            return;
        }
        if (type.create(level, EntitySpawnReason.TRIGGERED) instanceof Zombie zombie) {
            spawnZombieBaby(level, zombie, pos);
        }
    }

    private static void spawnAgeableBaby(ServerLevel level, AgeableMob child, Vec3 pos) {
        child.setBaby(true);
        child.setPos(pos);
        level.addFreshEntity(child);
    }

    private static void spawnZombieBaby(ServerLevel level, Zombie zombie, Vec3 pos) {
        zombie.setBaby(true);
        zombie.setPos(pos);
        level.addFreshEntity(zombie);
    }
}
