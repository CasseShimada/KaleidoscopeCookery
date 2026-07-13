package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class TeapotBlock extends HorizontalDirectionalBlock implements SimpleWaterloggedBlock, EntityBlock {
    public static final MapCodec<TeapotBlock> CODEC = simpleCodec(TeapotBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final IntegerProperty VARIANT = IntegerProperty.create("variant", 0, 2);
    public static final int COMMON = 0;
    public static final int BASED = 1;
    public static final int CHAINED = 2;

    public static final VoxelShape AABB = Shapes.or(
            Block.box(3, 0, 3, 13, 6, 13),
            Block.box(5, 6, 5, 11, 8, 11)
    );

    public TeapotBlock(BlockBehaviour.Properties properties) {
        super(properties
                .sound(SoundType.LANTERN)
                .mapColor(MapColor.COLOR_ORANGE)
                .noOcclusion()
                .instabreak());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(WATERLOGGED, false)
                .setValue(VARIANT, COMMON));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlocks.TEAPOT_BE) {
            return null;
        }
        return (levelIn, blockPos, blockState, blockEntity) -> ((TeapotBlockEntity) blockEntity).tick(levelIn);
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TeapotBlockEntity(pos, state);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess,
                                           BlockPos pos, Direction direction, BlockPos neighborPos,
                                           BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        int variant = state.getValue(VARIANT);
        if (direction == Direction.DOWN && variant != CHAINED) {
            return state.setValue(VARIANT, neighborState.isFaceSturdy(levelReader, neighborPos, Direction.UP) ? COMMON : BASED);
        }
        if (direction == Direction.UP && variant != BASED) {
            return state.setValue(VARIANT, canSupportCenter(levelReader, neighborPos, Direction.DOWN) ? CHAINED : COMMON);
        }
        return super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND || !(level.getBlockEntity(pos) instanceof ITeapot teapot)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (teapot.addTeaFluid(level, player, mainHandItem)) {
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        if (teapot.removeTeaFluid(level, player, mainHandItem)) {
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        if (!mainHandItem.isEmpty()) {
            return teapot.addIngredient(level, player, mainHandItem)
                    ? level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME
                    : InteractionResult.CONSUME;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                       Player player, BlockHitResult hitResult) {
        if (!(level.getBlockEntity(pos) instanceof ITeapot teapot)) {
            return InteractionResult.PASS;
        }
        if (player.isSecondaryUseActive()) {
            return teapot.removeIngredient(level, player)
                    ? level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME
                    : InteractionResult.CONSUME;
        }
        return teapot.takeTeapot(level, player)
                ? level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME
                : InteractionResult.CONSUME;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        FluidState fluidState = level.getFluidState(context.getClickedPos());
        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        BlockPos abovePos = context.getClickedPos().above();
        if (context.getClickedFace() == Direction.DOWN && canSupportCenter(level, abovePos, Direction.DOWN)) {
            return blockState.setValue(VARIANT, CHAINED);
        }
        BlockPos belowPos = context.getClickedPos().below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!belowState.isFaceSturdy(level, belowPos, Direction.UP)) {
            return blockState.setValue(VARIANT, BASED);
        }
        return blockState;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, VARIANT);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return AABB;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof TeapotBlockEntity teapot) {
            return teapot.getDrops();
        }
        List<ItemStack> drops = super.getDrops(state, lootParamsBuilder);
        return drops.isEmpty() ? List.of(ModItems.TEAPOT.getDefaultInstance()) : drops;
    }
}
