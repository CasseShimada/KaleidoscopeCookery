package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTrigger;
import net.minecraft.advancements.triggers.DistanceTrigger;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public class ModTrigger {
    public static ModEventTrigger EVENT;
    public static DistanceTrigger FLATULENCE_FLY_HEIGHT;

    public static void init() {
        EVENT = register("mod_event", new ModEventTrigger());
        FLATULENCE_FLY_HEIGHT = register("flatulence_fly_height", new DistanceTrigger());
    }

    private static <T extends net.minecraft.advancements.triggers.CriterionTrigger<?>> T register(String id, T trigger) {
        return Registry.register(BuiltInRegistries.TRIGGER_TYPES, modLoc(id), trigger);
    }

    private static Identifier modLoc(String id) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, id);
    }
}
