package com.github.ysbbbbbb.kaleidoscopecookery.api.event;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Allows custom blocks to handle sickle harvesting before the default crop and vegetation logic.
 * The first callback result other than {@link Result#PASS} wins.
 */
@FunctionalInterface
public interface SickleHarvestCallback {
    Event<SickleHarvestCallback> EVENT = EventFactory.createArrayBacked(
            SickleHarvestCallback.class,
            callbacks -> (player, sickle, pos, state) -> {
                for (SickleHarvestCallback callback : callbacks) {
                    Result result = callback.harvest(player, sickle, pos, state);
                    if (result != Result.PASS) {
                        return result;
                    }
                }
                return Result.PASS;
            });

    Result harvest(Player player, ItemStack sickle, BlockPos pos, BlockState state);

    enum Result {
        /** Continue with the next callback and then the sickle's default harvesting logic. */
        PASS(false, false),
        /** Stop harvesting this block without charging durability. */
        SKIP(true, false),
        /** The callback harvested the block and the sickle should lose durability. */
        HARVESTED(true, true);

        private final boolean handled;
        private final boolean costsDurability;

        Result(boolean handled, boolean costsDurability) {
            this.handled = handled;
            this.costsDurability = costsDurability;
        }

        public boolean handled() {
            return handled;
        }

        public boolean costsDurability() {
            return costsDurability;
        }
    }
}
