package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.ai.CatLieOnBlockGoal;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.monster.Creeper;

public class EntityJoinWorldEvent {
    private static final String CAT_LIE_GOAL_TAG = "kaleidoscope_cookery.cat_lie_goal";
    private static final String CREEPER_MUSTARD_AVOID_TAG = "kaleidoscope_cookery.creeper_mustard_avoid_goal";

    public static void register() {
        ServerEntityEvents.ENTITY_LOAD.register(EntityJoinWorldEvent::onEntityJoinWorld);
    }

    private static void onEntityJoinWorld(Entity entity, ServerLevel level) {
        if (entity instanceof Cat cat) {
            if (!entity.addTag(CAT_LIE_GOAL_TAG)) {
                return;
            }
            cat.goalSelector.addGoal(5, new CatLieOnBlockGoal(cat, 1.1, 8));
        } else if (entity instanceof Creeper creeper) {
            if (!entity.addTag(CREEPER_MUSTARD_AVOID_TAG)) {
                return;
            }
            creeper.goalSelector.addGoal(3, new AvoidEntityGoal<>(creeper, LivingEntity.class, 6,
                    1, 1.2, e -> e.hasEffect(ModEffects.MUSTARD)));
        }
    }
}
