package com.github.ysbbbbbb.kaleidoscopecookery.event.server;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
        SickleHarvestCallback.EVENT.register(SickleHarvestNetherWartEvent::onSickleHarvest);
    }

    private static SickleHarvestCallback.Result onSickleHarvest(
            Player player, ItemStack ignoredSickle, BlockPos pos, BlockState state) {
        if (!state.is(Blocks.NETHER_WART)) {
            return SickleHarvestCallback.Result.PASS;
        }

        if (!isMatureNetherWart(state) || !(player instanceof ServerPlayer serverPlayer)) {
            return SickleHarvestCallback.Result.SKIP;
        }
        return harvestMatureNetherWart(serverPlayer, pos)
                ? SickleHarvestCallback.Result.HARVESTED
                : SickleHarvestCallback.Result.SKIP;
    }

    private static boolean isMatureNetherWart(BlockState state) {
        return state.getValue(NetherWartBlock.AGE) >= MATURE_AGE;
    }

    private static boolean harvestMatureNetherWart(ServerPlayer player, BlockPos pos) {
        if (!player.gameMode.destroyBlock(pos)) {
            return false;
        }

        var level = player.level();
        if (!level.getBlockState(pos).isAir()) {
            return false;
        }
        level.setBlock(pos, Blocks.NETHER_WART.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }
}
