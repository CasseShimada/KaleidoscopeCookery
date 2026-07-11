package com.github.ysbbbbbb.kaleidoscopecookery.event.server;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;

/**
 * 镰刀收割地狱疣事件特判
 */
public final class SickleHarvestNetherWartEvent {
    private static final int MATURE_AGE = 3;

    private SickleHarvestNetherWartEvent() {
    }

    public static void register() {
        ModEvents.SICKLE_HARVEST.register(SickleHarvestNetherWartEvent::onSickleHarvest);
    }

    private static void onSickleHarvest(SickleHarvestEvent event) {
        BlockState harvestState = event.getHarvestState();
        if (!harvestState.is(Blocks.NETHER_WART)) {
            return;
        }

        if (isMatureNetherWart(harvestState)) {
            harvestMatureNetherWart(event);
        }
        event.setCanceled(true);
    }

    private static boolean isMatureNetherWart(BlockState state) {
        return state.getValue(NetherWartBlock.AGE) >= MATURE_AGE;
    }

    private static void harvestMatureNetherWart(SickleHarvestEvent event) {
        var player = event.getPlayer();
        var pos = event.getHarvestPos();
        if (!(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        if (!serverPlayer.gameMode.destroyBlock(pos)) {
            return;
        }

        var level = serverPlayer.level();
        if (!level.getBlockState(pos).isAir()) {
            return;
        }
        level.setBlock(pos, Blocks.NETHER_WART.defaultBlockState(), Block.UPDATE_ALL);
        event.setCostDurability(true);
    }
}
