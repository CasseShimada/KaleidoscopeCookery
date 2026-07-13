package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Allows special millstone outputs to handle extraction before the default carrier logic.
 * The first callback result other than {@link Result#PASS} wins.
 */
@FunctionalInterface
public interface MillstoneTakeItemCallback {
    Event<MillstoneTakeItemCallback> EVENT = EventFactory.createArrayBacked(
            MillstoneTakeItemCallback.class,
            callbacks -> (user, heldItem, millstone) -> {
                for (MillstoneTakeItemCallback callback : callbacks) {
                    Result result = callback.takeItem(user, heldItem, millstone);
                    if (result != Result.PASS) {
                        return result;
                    }
                }
                return Result.PASS;
            });

    Result takeItem(LivingEntity user, ItemStack heldItem, MillstoneBlockEntity millstone);

    enum Result {
        /** Continue with the next callback and then the millstone's default extraction logic. */
        PASS(false, false),
        /** Stop extraction and report that the interaction failed. */
        FAILURE(true, false),
        /** The callback extracted the output successfully. */
        SUCCESS(true, true);

        private final boolean handled;
        private final boolean succeeds;

        Result(boolean handled, boolean succeeds) {
            this.handled = handled;
            this.succeeds = succeeds;
        }

        public boolean handled() {
            return handled;
        }

        public boolean succeeds() {
            return succeeds;
        }
    }
}
