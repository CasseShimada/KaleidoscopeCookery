package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.*;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.redstone.Orientation;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class ShawarmaSpitBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<ShawarmaSpitBlock> CODEC = simpleCodec(ShawarmaSpitBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;

    public static final VoxelShape UPPER_AABB = Shapes.or(
            Block.box(0, 14, 0, 16, 16, 16),
            Block.box(6, 0, 6, 10, 14, 10)
    );
    public static final VoxelShape LOWER_AABB = Shapes.or(
            Block.box(0, 0, 0, 16, 7, 16),
            Block.box(6, 7, 6, 10, 16, 10)
    );

    public ShawarmaSpitBlock(BlockBehaviour.Properties properties) {
        super(properties
                .mapColor(MapColor.METAL)
                .noOcclusion()
                .instrument(NoteBlockInstrument.BASS)
                .strength(2.0F, 3.0F)
                .lightLevel(state -> state.getValue(POWERED) ? 8 : 0)
                .sound(SoundType.METAL));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, DoubleBlockHalf.LOWER)
                .setValue(WATERLOGGED, false)
                .setValue(POWERED, false));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (player.isShiftKeyDown()) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        BlockPos storagePos = getStoragePos(pos, state);
        BlockEntity blockEntity = level.getBlockEntity(storagePos);
        if (blockEntity instanceof ShawarmaSpitBlockEntity shawarmaSpit) {
            if (level.isClientSide()) {
                return shawarmaSpit.canPutCookingItem(level, stack) || shawarmaSpit.canTakeCookedItem()
                        ? InteractionResult.SUCCESS
                        : InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            if (shawarmaSpit.onPutCookingItem(level, stack)) {
                return InteractionResult.CONSUME;
            } else if (shawarmaSpit.onTakeCookedItem(level, player)) {
                return InteractionResult.CONSUME;
            }
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                                      BlockHitResult hitResult) {
        if (player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        BlockPos storagePos = getStoragePos(pos, state);
        if (!(level.getBlockEntity(storagePos) instanceof ShawarmaSpitBlockEntity shawarmaSpit)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return shawarmaSpit.canTakeCookedItem() ? InteractionResult.SUCCESS : InteractionResult.PASS;
        }
        return shawarmaSpit.onTakeCookedItem(level, player) ? InteractionResult.CONSUME : InteractionResult.PASS;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlocks.SHAWARMA_SPIT_BE) {
            return null;
        }
        return (levelIn, blockPos, blockState, blockEntity) -> {
            if (blockState.getValue(POWERED)) {
                ((ShawarmaSpitBlockEntity) blockEntity).tick();
            }
        };
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return state.getValue(HALF) == DoubleBlockHalf.LOWER ? LOWER_AABB : UPPER_AABB;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos currentPos,
                                           Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        DoubleBlockHalf half = state.getValue(HALF);
        boolean isLowerHalf = half == DoubleBlockHalf.LOWER && direction == Direction.UP;
        boolean isUpperHalf = half == DoubleBlockHalf.UPPER && direction == Direction.DOWN;
        if (direction.getAxis() == Direction.Axis.Y && (isLowerHalf || isUpperHalf)) {
            if (neighborState.is(this) && neighborState.getValue(HALF) != half) {
                return state.setValue(FACING, neighborState.getValue(FACING))
                        .setValue(POWERED, neighborState.getValue(POWERED));
            }
            return Blocks.AIR.defaultBlockState();
        }
        return super.updateShape(state, levelReader, tickAccess, currentPos, direction, neighborPos, neighborState, random);
    }

    @Override
    public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, Orientation orientation, boolean isMoving) {
        Direction direction = state.getValue(HALF) == DoubleBlockHalf.LOWER ? Direction.UP : Direction.DOWN;
        boolean powered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.relative(direction));
        if (!state.is(block) && powered != state.getValue(POWERED)) {
            level.setBlock(pos, state.setValue(POWERED, powered), Block.UPDATE_CLIENTS);
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide()) {
            if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
                BlockPos below = pos.below();
                BlockState belowState = level.getBlockState(below);
                if (belowState.is(state.getBlock()) && belowState.getValue(HALF) == DoubleBlockHalf.LOWER) {
                    if (player.isCreative()) {
                        ItemStack storedItem = getStoredItem(level, below);
                        BlockState airBlockState = belowState.getFluidState().is(Fluids.WATER) ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState();
                        if (level.setBlock(below, airBlockState, Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL)) {
                            if (!storedItem.isEmpty()) {
                                popResource(level, below, storedItem);
                            }
                            level.levelEvent(player, LevelEvent.PARTICLES_DESTROY_BLOCK, below, Block.getId(belowState));
                        }
                    } else {
                        level.destroyBlock(below, true, player);
                    }
                }
            } else if (player.isCreative()) {
                dropCookItems(level, pos);
            }
        }
        return super.playerWillDestroy(level, pos, state, player);
    }

    private static BlockPos getStoragePos(BlockPos pos, BlockState state) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    private void dropCookItems(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ShawarmaSpitBlockEntity shawarmaSpit) {
            ItemStack storedItem = shawarmaSpit.removeStoredItem();
            if (!storedItem.isEmpty()) {
                popResource(level, pos, storedItem);
            }
        }
    }

    private static ItemStack getStoredItem(Level level, BlockPos pos) {
        if (level.getBlockEntity(pos) instanceof ShawarmaSpitBlockEntity shawarmaSpit) {
            return shawarmaSpit.getStoredItem();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        FluidState fluidState = context.getLevel().getFluidState(pos);
        if (pos.getY() < level.getMaxY() - 1 && level.getBlockState(pos.above()).canBeReplaced(context)) {
            boolean isPowered = level.hasNeighborSignal(pos) || level.hasNeighborSignal(pos.above());
            return this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection())
                    .setValue(POWERED, isPowered)
                    .setValue(HALF, DoubleBlockHalf.LOWER)
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }
        return null;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockPos upperPos = pos.above();
        FluidState fluidState = level.getFluidState(upperPos);
        BlockState blockState = state.setValue(HALF, DoubleBlockHalf.UPPER)
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        level.setBlockAndUpdate(upperPos, blockState);
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, WATERLOGGED, POWERED);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new ShawarmaSpitBlockEntity(pos, state);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        List<ItemStack> drops;
        if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            drops = new ArrayList<>(super.getDrops(state, lootParamsBuilder));
        } else {
            drops = new ArrayList<>();
        }
        BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof ShawarmaSpitBlockEntity shawarmaSpit) {
            ItemStack storedItem = shawarmaSpit.getStoredItem();
            if (!storedItem.isEmpty()) {
                drops.add(storedItem);
            }
        }
        return drops;
    }

}
