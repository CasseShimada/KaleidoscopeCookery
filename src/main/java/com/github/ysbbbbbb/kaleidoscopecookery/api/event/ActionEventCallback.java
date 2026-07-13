package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.Mob;
import org.jetbrains.annotations.Nullable;

public interface ActionEventCallback {

    @FunctionalInterface
    interface MillstoneFinish {
        Event<MillstoneFinish> EVENT = EventFactory.createArrayBacked(
                MillstoneFinish.class,
                callbacks -> (millstone, bindEntity) -> {
                    for (MillstoneFinish callback : callbacks) {
                        callback.onMillstoneFinish(millstone, bindEntity);
                    }
                });

        void onMillstoneFinish(MillstoneBlockEntity millstone, @Nullable Mob bindEntity);
    }

    @FunctionalInterface
    interface CheckSpecialItem {
        Event<CheckSpecialItem> EVENT = EventFactory.createArrayBacked(
                CheckSpecialItem.class,
                callbacks -> event -> {
                    for (CheckSpecialItem callback : callbacks) {
                        callback.onCheckItemEvent(event);
                    }
                });

        void onCheckItemEvent(RecipeItemEvent.CheckItem event);
    }

    @FunctionalInterface
    interface DeductSpecialItem {
        Event<DeductSpecialItem> EVENT = EventFactory.createArrayBacked(
                DeductSpecialItem.class,
                callbacks -> event -> {
                    for (DeductSpecialItem callback : callbacks) {
                        callback.onDeductItemEvent(event);
                    }
                });

        void onDeductItemEvent(RecipeItemEvent.DeductItem event);
    }
}
