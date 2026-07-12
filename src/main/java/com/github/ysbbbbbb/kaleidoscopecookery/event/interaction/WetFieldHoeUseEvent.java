package com.github.ysbbbbbb.kaleidoscopecookery.event.interaction;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.BlockHitResult;

public final class WetFieldHoeUseEvent {
    private static final int HOE_DURABILITY_COST = 1;

    private WetFieldHoeUseEvent() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register(WetFieldHoeUseEvent::onUseBlock);
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand,
                                                BlockHitResult hitResult) {
        if (player.isSpectator()) {
            return InteractionResult.PASS;
        }

        BlockPos pos = hitResult.getBlockPos();
        ItemStack stack = player.getItemInHand(hand);
        if (!(stack.getItem() instanceof HoeItem) || !isWetFieldTarget(level, pos)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!level.mayInteract(player, pos)
                || !player.mayUseItemAt(pos, hitResult.getDirection(), stack)) {
            return InteractionResult.FAIL;
        }
        return tillWetField(level, pos, player, hand, stack)
                ? InteractionResult.CONSUME
                : InteractionResult.FAIL;
    }

    private static boolean isWetFieldTarget(Level level, BlockPos pos) {
        BlockState state = level.getBlockState(pos);
        return isTillableFieldBlock(state.getBlock()) && hasWaterAbove(level, pos);
    }

    private static boolean isTillableFieldBlock(Block block) {
        return block == Blocks.DIRT || block == Blocks.GRASS_BLOCK || block == Blocks.DIRT_PATH;
    }

    private static boolean hasWaterAbove(Level level, BlockPos pos) {
        FluidState fluidState = level.getFluidState(pos.above());
        return fluidState.is(FluidTags.WATER);
    }

    private static boolean tillWetField(Level level, BlockPos pos, Player player,
                                        InteractionHand hand, ItemStack stack) {
        BlockState farmland = Blocks.FARMLAND.defaultBlockState();
        if (!level.setBlockAndUpdate(pos, farmland)) {
            return false;
        }
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, farmland));
        level.playSound(null, pos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        if (!player.hasInfiniteMaterials()) {
            stack.hurtAndBreak(HOE_DURABILITY_COST, player, hand);
        }
        ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_HOE_ON_WATER_FIELD);
        return true;
    }
}
