package com.github.ysbbbbbb.kaleidoscopecookery.block.food;

import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.util.RandomSource;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class FoodBiteOneByTwoBlock extends FoodBiteBlock {
    public static final IntegerProperty POSITION = IntegerProperty.create("position", 0, 1);
    public static final int LEFT = 0;
    public static final int RIGHT = 1;

    public FoodBiteOneByTwoBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties, int maxBites,
                                 @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
        super(properties, foodProperties, maxBites, animateTick);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(getBites(), 0)
                .setValue(FACING, Direction.SOUTH)
                .setValue(POSITION, RIGHT));
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos,
                                  Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        int position = state.getValue(POSITION);
        Direction facing = state.getValue(FACING);

        if ((position == LEFT && direction == facing.getCounterClockWise())
                || (position == RIGHT && direction == facing.getClockWise())) {
            if (!neighborState.is(this) || neighborState.getValue(FACING) != facing
                    || neighborState.getValue(POSITION) == position) {
                return Blocks.AIR.defaultBlockState();
            }
            int neighborBites = neighborState.getValue(getBites());
            if (neighborBites != state.getValue(getBites())) {
                return state.setValue(getBites(), neighborBites);
            }
        }

        return super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide() && player.isCreative() && state.getValue(POSITION) == LEFT) {
            BlockPos right = pos.relative(state.getValue(FACING).getCounterClockWise());
            BlockState rightState = level.getBlockState(right);
            if (rightState.is(state.getBlock()) && rightState.getValue(POSITION) == RIGHT) {
                BlockState airBlockState = rightState.getFluidState().is(Fluids.WATER)
                        ? Blocks.WATER.defaultBlockState()
                        : Blocks.AIR.defaultBlockState();
                level.setBlock(right, airBlockState, Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
                level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, right, Block.getId(rightState));
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos rightPos = context.getClickedPos();
        Direction facing = context.getHorizontalDirection().getOpposite();
        BlockPos leftPos = rightPos.relative(facing.getClockWise());
        if (context.getLevel().getBlockState(leftPos).canBeReplaced(context)) {
            return super.getStateForPlacement(context);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        Direction facing = state.getValue(FACING);
        BlockPos leftPos = pos.relative(facing.getClockWise());
        BlockState leftState = state.setValue(POSITION, LEFT);
        level.setBlock(leftPos, leftState, Block.UPDATE_ALL);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, POSITION);
    }

    @Override
    protected void createBitesBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getBites(), FACING, POSITION);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(POSITION) == LEFT) {
            return Collections.emptyList();
        }
        return super.getDrops(state, params);
    }
}
