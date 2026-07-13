package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ISteamer;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.SteamerItem;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class SteamerBlock extends FallingBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<SteamerBlock> CODEC = simpleCodec(SteamerBlock::new);
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    public static final BooleanProperty HALF = BooleanProperty.create("half");
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");
    public static final BooleanProperty HAS_BASE = BooleanProperty.create("has_base");
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    private static final VoxelShape HALF_AABB = Block.box(1, 0, 1, 15, 8, 15);
    private static final VoxelShape FULL_AABB = Block.box(1, 0, 1, 15, 16, 15);

    public SteamerBlock(BlockBehaviour.Properties properties) {
        super(properties
                .mapColor(MapColor.WOOD)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .instabreak()
                .noOcclusion()
                .pushReaction(PushReaction.DESTROY)
                .sound(SoundType.BAMBOO));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.NORTH)
                .setValue(HALF, true)
                .setValue(HAS_LID, false)
                .setValue(HAS_BASE, false)
                .setValue(WATERLOGGED, false));
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlocks.STEAMER_BE) {
            return null;
        }
        return (levelIn, blockPos, blockState, blockEntity) -> ((SteamerBlockEntity) blockEntity).tick(levelIn);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        // 如果蒸笼正下方是热源，则触发成就
        if (placer instanceof ServerPlayer player && level.getBlockEntity(pos) instanceof ISteamer steamer && steamer.hasHeatSource(level)) {
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_STEAMER);
        }
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        BlockState below = level.getBlockState(pos.below());
        if (isFree(below) && pos.getY() >= level.getMinY()) {
            CompoundTag blockEntityTag = null;
            if (level.getBlockEntity(pos) instanceof SteamerBlockEntity steamer) {
                blockEntityTag = steamer.saveWithoutMetadata(level.registryAccess());
            }
            FallingBlockEntity fall = FallingBlockEntity.fall(level, pos, state.setValue(HAS_BASE, false));
            fall.blockData = blockEntityTag;
            this.falling(fall);
        }
    }

    @Override
    protected @NotNull MapCodec<? extends FallingBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos,
                                           Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }
        tickAccess.scheduleTick(pos, this, this.getDelayAfterPlace());
        // 如果下方是不完整方块，则添加基座
        if (direction == Direction.DOWN) {
            // 如果下方完全是空气，那么反而不需要添加基座了，让它自然掉落
            if (isFree(neighborState)) {
                state = state.setValue(HAS_BASE, false);
            } else {
                state = state.setValue(HAS_BASE, shouldHasBase(levelReader, pos));
            }
        }
        // 如果是上方方块是蒸笼，那么把盖子去掉
        if (direction == Direction.UP && neighborState.is(this)) {
            state = state.setValue(HAS_LID, false);
        }
        return state;
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.isEmpty()) {
            return this.useWithoutItem(state, level, pos, player, hitResult);
        }

        // 手持蒸笼，右击可以沿着整叠往上摞。
        if (itemInHand.getItem() instanceof SteamerItem steamerItem) {
            BlockPos placePos = hitResult.getBlockPos();
            BlockState placeState = state;
            while (placeState.is(this) && !placeState.getValue(HAS_LID) && !placeState.getValue(HALF)) {
                placePos = placePos.above();
                placeState = level.getBlockState(placePos);
            }

            ItemStack toUse = player.hasInfiniteMaterials() ? itemInHand.copy() : itemInHand;
            BlockHitResult placeHit = hitResult.withDirection(Direction.UP).withPosition(placePos);
            BlockPlaceContext placeContext = new BlockPlaceContext(player, hand, toUse, placeHit);
            if (placeState.canBeReplaced() || placeState.is(this) && placeState.getValue(HALF) && !placeState.getValue(HAS_LID)) {
                InteractionResult place = steamerItem.place(placeContext);
                if (place.consumesAction()) {
                    return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
                }
            }
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        // 其他情况交给 BlockEntity 处理
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (blockEntity instanceof ISteamer steamer) {
            if (level.isClientSide()) {
                return blockEntity instanceof SteamerBlockEntity steamerBlockEntity
                        && steamerBlockEntity.canPlaceFood(level, itemInHand)
                        ? InteractionResult.SUCCESS
                        : InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            // 先尝试放入物品
            if (steamer.placeFood(level, player, itemInHand)) {
                return InteractionResult.CONSUME;
            }
        }

        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        boolean hasLid = state.getValue(HAS_LID);
        if (player.isSecondaryUseActive() && (hasLid || !level.getBlockState(pos.above()).is(this))) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (!level.setBlockAndUpdate(pos, state.setValue(HAS_LID, !hasLid))) {
                return InteractionResult.FAIL;
            }
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            return InteractionResult.CONSUME;
        }

        if (!(level.getBlockEntity(pos) instanceof ISteamer steamer)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return canInteractWithOpenLayer(state, level, pos) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return steamer.takeFood(level, player, InteractionHand.MAIN_HAND)
                ? InteractionResult.CONSUME
                : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    private static boolean canInteractWithOpenLayer(BlockState state, Level level, BlockPos pos) {
        if (state.getValue(HAS_LID)) {
            return false;
        }
        BlockPos above = pos.above();
        return !level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos clickedPos = context.getClickedPos();
        BlockState blockState = context.getLevel().getBlockState(clickedPos);
        FluidState fluidState = context.getLevel().getFluidState(clickedPos);
        BlockState resultState;
        if (blockState.is(this)) {
            resultState = blockState.setValue(HALF, false);
        } else {
            resultState = this.defaultBlockState()
                    .setValue(FACING, context.getHorizontalDirection().getOpposite())
                    .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);
        }

        // 如果下方完全是空气，那么反而不需要添加基座了，让它自然掉落
        if (isFree(context.getLevel().getBlockState(clickedPos.below()))) {
            return resultState.setValue(HAS_BASE, false);
        }
        // 如果下方是不完整方块，则添加基座
        return resultState.setValue(HAS_BASE, shouldHasBase(context.getLevel(), clickedPos));
    }

    private boolean shouldHasBase(LevelReader level, BlockPos pos) {
        BlockState belowState = level.getBlockState(pos.below());
        // 如果还是蒸笼，那么不添加基座
        if (belowState.is(this)) {
            return false;
        }
        // 如果是热源，强制添加基座
        if (belowState.hasProperty(BlockStateProperties.LIT)) {
            return true;
        }
        if (belowState.is(TagMod.HEAT_SOURCE_BLOCKS_WITHOUT_LIT)) {
            return true;
        }
        // 如果下方是完整方块或者是不可替换方块，则添加基座
        return !belowState.isFaceSturdy(level, pos.below(), Direction.UP);
    }

    @Override
    public void onLand(Level level, BlockPos pos, BlockState state, BlockState replaceableState, FallingBlockEntity fallingBlock) {
        // 落地时如果下方是不完整方块，则添加基座
        if (shouldHasBase(level, pos)) {
            level.setBlock(pos, state.setValue(HAS_BASE, true), Block.UPDATE_ALL);
        }
    }

    @Override
    public boolean canBeReplaced(BlockState state, BlockPlaceContext context) {
        ItemStack itemInHand = context.getItemInHand();
        // 如果是半块，那么可以摞上去
        return state.getValue(HALF) && itemInHand.is(this.asItem());
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    public int getDustColor(BlockState state, BlockGetter level, BlockPos pos) {
        return state.getMapColor(level, pos).col;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, HALF, HAS_LID, HAS_BASE, WATERLOGGED);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return state.getValue(HALF) ? HALF_AABB : FULL_AABB;
    }

    @Override
    public @NotNull BlockState rotate(BlockState pState, Rotation pRot) {
        return pState.setValue(FACING, pRot.rotate(pState.getValue(FACING)));
    }

    @Override
    public @NotNull BlockState mirror(BlockState pState, Mirror pMirror) {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)));
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SteamerBlockEntity(pos, state);
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (parameter instanceof SteamerBlockEntity steamer && steamer.getLevel() != null) {
            return steamer.dropAsItem(steamer.getLevel());
        }

        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, lootParamsBuilder));
        if (drops.isEmpty()) {
            drops.add(ModItems.STEAMER.getDefaultInstance());
            if (!state.getValue(HALF)) {
                drops.add(ModItems.STEAMER.getDefaultInstance());
            }
        }
        return drops;
    }
}
