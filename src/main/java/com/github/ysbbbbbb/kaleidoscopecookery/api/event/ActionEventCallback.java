package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public interface ActionEventCallback {

    @FunctionalInterface
    interface MillstoneFinish {
        void onMillstoneFinish(MillstoneBlockEntity millstone, @Nullable Mob bindEntity);
    }

    @FunctionalInterface
    interface CheckSpecialItem {
        void onCheckItemEvent(RecipeItemEvent.CheckItem event);
    }

    @FunctionalInterface
    interface DeductSpecialItem {
        void onDeductItemEvent(RecipeItemEvent.DeductItem event);
    }
}
