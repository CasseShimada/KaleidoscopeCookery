package com.github.ysbbbbbb.kaleidoscopecookery.event.server;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.ai.CatLieOnBlockGoal;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.Creeper;

public final class ServerEntityLoadEvent {
    private static final int CAT_LIE_GOAL_PRIORITY = 5;
    private static final double CAT_LIE_SPEED_MODIFIER = 1.1;
    private static final int CAT_LIE_SEARCH_RANGE = 8;
    private static final int CREEPER_MUSTARD_AVOID_GOAL_PRIORITY = 3;
    private static final float CREEPER_MUSTARD_AVOID_DISTANCE = 6.0F;
    private static final double CREEPER_MUSTARD_WALK_SPEED_MODIFIER = 1.0;
    private static final double CREEPER_MUSTARD_SPRINT_SPEED_MODIFIER = 1.2;

    private ServerEntityLoadEvent() {
    }

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register(ServerEntityLoadEvent::onEntityLoad);
    }

    private static void onEntityLoad(Entity entity, ServerLevel level) {
        if (entity instanceof Cat cat) {
            addCatLieGoal(cat);
            return;
        }
        if (entity instanceof Creeper creeper) {
            addCreeperMustardAvoidGoal(creeper);
        }
    }

    private static void addCatLieGoal(Cat cat) {
        boolean alreadyInstalled = cat.getGoalSelector().getAvailableGoals().stream()
                .anyMatch(wrappedGoal -> wrappedGoal.getGoal() instanceof CatLieOnBlockGoal);
        if (alreadyInstalled) {
            return;
        }
        cat.getGoalSelector().addGoal(CAT_LIE_GOAL_PRIORITY,
                new CatLieOnBlockGoal(cat, CAT_LIE_SPEED_MODIFIER, CAT_LIE_SEARCH_RANGE));
    }

    private static void addCreeperMustardAvoidGoal(Creeper creeper) {
        boolean alreadyInstalled = creeper.getGoalSelector().getAvailableGoals().stream()
                .anyMatch(wrappedGoal -> wrappedGoal.getGoal() instanceof CreeperMustardAvoidGoal);
        if (alreadyInstalled) {
            return;
        }
        creeper.getGoalSelector().addGoal(CREEPER_MUSTARD_AVOID_GOAL_PRIORITY,
                new CreeperMustardAvoidGoal(creeper));
    }

    private static final class CreeperMustardAvoidGoal extends AvoidEntityGoal<LivingEntity> {
        private CreeperMustardAvoidGoal(Creeper creeper) {
            super(creeper, LivingEntity.class, CREEPER_MUSTARD_AVOID_DISTANCE,
                    CREEPER_MUSTARD_WALK_SPEED_MODIFIER, CREEPER_MUSTARD_SPRINT_SPEED_MODIFIER,
                    entity -> entity.hasEffect(ModEffects.MUSTARD));
        }
    }
}
