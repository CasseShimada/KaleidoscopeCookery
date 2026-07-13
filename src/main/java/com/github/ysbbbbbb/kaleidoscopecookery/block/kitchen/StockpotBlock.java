package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoundType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
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
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.util.RandomSource;
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

public class StockpotBlock extends HorizontalDirectionalBlock implements EntityBlock, SimpleWaterloggedBlock {
    public static final MapCodec<StockpotBlock> CODEC = simpleCodec(StockpotBlock::new);
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
    public static final BooleanProperty HAS_LID = BooleanProperty.create("has_lid");
    public static final BooleanProperty HAS_BASE = BooleanProperty.create("has_base");
    public static final BooleanProperty HAS_CHAINS = BooleanProperty.create("has_chains");

    private static final VoxelShape AABB = Shapes.or(
            Block.box(2, 0, 2, 14, 5, 14),
            Block.box(1, 5, 1, 15, 7, 15));
    private static final VoxelShape AABB_WITH_LID = Shapes.or(
            Block.box(2, 0, 2, 14, 9, 14),
            Block.box(1, 5, 1, 15, 7, 15));

    public StockpotBlock(BlockBehaviour.Properties properties) {
        super(properties
                .mapColor(MapColor.METAL)
                .sound(ModSoundType.POT).noOcclusion()
                .strength(1.5F, 6.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(WATERLOGGED, false)
                .setValue(HAS_LID, false)
                .setValue(HAS_BASE, false)
                .setValue(HAS_CHAINS, false));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        if (placer instanceof Player player && level.getBlockEntity(pos) instanceof IStockpot stockpot && stockpot.hasHeatSource(level)) {
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_STOCKPOT_ON_HEAT_SOURCE);
        }
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos,
                                           Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(WATERLOGGED)) {
            tickAccess.scheduleTick(pos, Fluids.WATER, Fluids.WATER.getTickDelay(levelReader));
        }

        // 上方无法支撑，取消锁链
        // 下方无法支撑，添加基座
        if (direction == Direction.DOWN && !state.getValue(HAS_CHAINS)) {
            return state.setValue(HAS_CHAINS, canSupportCenter(levelReader, pos.above(), Direction.DOWN))
                    .setValue(HAS_BASE, !neighborState.isFaceSturdy(levelReader, neighborPos, Direction.UP));
        }
        if (direction == Direction.UP && !state.getValue(HAS_BASE)) {
            BlockState belowState = levelReader.getBlockState(pos.below());
            return state.setValue(HAS_CHAINS, canSupportCenter(levelReader, neighborPos, Direction.DOWN))
                    .setValue(HAS_BASE, !belowState.isFaceSturdy(levelReader, pos.below(), Direction.UP));
        }

        return super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        BlockEntity blockEntity = level.getBlockEntity(pos);
        if (!(blockEntity instanceof IStockpot stockpot)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        // 先检查盖子
        ItemStack mainHandItem = player.getMainHandItem();
        boolean canLitClick = stockpot.hasLid() || mainHandItem.is(ModItems.STOCKPOT_LID);
        boolean canAddSoupBase = !stockpot.hasLid() && stockpot.getStatus() == IStockpot.PUT_SOUP_BASE && !mainHandItem.isEmpty();
        boolean canRemoveSoupBase = !stockpot.hasLid() && stockpot.getStatus() == IStockpot.PUT_INGREDIENT && !mainHandItem.isEmpty();
        boolean canAddIngredient = !stockpot.hasLid() && stockpot.getStatus() == IStockpot.PUT_INGREDIENT && !mainHandItem.isEmpty();
        boolean canRemoveIngredient = !stockpot.hasLid() && stockpot.getStatus() == IStockpot.PUT_INGREDIENT && mainHandItem.isEmpty();
        boolean canTakeProduct = !stockpot.hasLid() && stockpot.getStatus() == IStockpot.FINISHED;
        if (level.isClientSide() && (canLitClick || canAddSoupBase || canRemoveSoupBase
                || canAddIngredient || canRemoveIngredient || canTakeProduct)) {
            return InteractionResult.SUCCESS;
        }
        if (stockpot.onLitClick(level, player, mainHandItem)) {
            return InteractionResult.CONSUME;
        }
        // 加入汤底
        if (stockpot.addSoupBase(level, player, mainHandItem)) {
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.PUT_SOUP_BASE_IN_STOCKPOT);
            return InteractionResult.CONSUME;
        }
        // 取出汤底
        if (stockpot.removeSoupBase(level, player, mainHandItem)) {
            return InteractionResult.CONSUME;
        }
        // 加入原料
        if (!mainHandItem.isEmpty() && stockpot.addIngredient(level, player, mainHandItem)) {
            return InteractionResult.CONSUME;
        }
        // 取出原料
        if (mainHandItem.isEmpty() && stockpot.removeIngredient(level, player)) {
            return InteractionResult.CONSUME;
        }
        // 取出成品
        if (stockpot.takeOutProduct(level, player, mainHandItem)) {
            return InteractionResult.CONSUME;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    @Nullable
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new StockpotBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlocks.STOCKPOT_BE) {
            return null;
        }
        return (lvl, blockPos, blockState, blockEntity) -> ((StockpotBlockEntity) blockEntity).tick(lvl);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        Level level = context.getLevel();
        FluidState fluidState = level.getFluidState(context.getClickedPos());
        Direction clickFace = context.getClickedFace();
        BlockState blockState = this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(WATERLOGGED, fluidState.getType() == Fluids.WATER);

        // 如果点击的是上方，那么依据是否是可支持方块添加锁链
        BlockPos abovePos = context.getClickedPos().above();
        if (clickFace == Direction.DOWN && canSupportCenter(level, abovePos, Direction.DOWN)) {
            return blockState.setValue(HAS_CHAINS, true);
        }

        // 如果下方是不完整方块，则添加基座
        BlockPos belowPos = context.getClickedPos().below();
        BlockState belowState = level.getBlockState(belowPos);
        if (!belowState.isFaceSturdy(level, belowPos, Direction.UP)) {
            return blockState.setValue(HAS_BASE, true);
        }
        return blockState;
    }

    @Override
    public @NotNull FluidState getFluidState(BlockState state) {
        return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, WATERLOGGED, HAS_LID, HAS_BASE, HAS_CHAINS);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        if (state.getValue(HAS_LID)) {
            return AABB_WITH_LID;
        }
        return AABB;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder lootParamsBuilder) {
        List<ItemStack> drops = new ArrayList<>(super.getDrops(state, lootParamsBuilder));
        BlockEntity parameter = lootParamsBuilder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (state.getValue(HAS_LID)) {
            ItemStack lid = parameter instanceof StockpotBlockEntity stockpot ? stockpot.getLidItem() : ItemStack.EMPTY;
            if (lid.isEmpty()) {
                lid = new ItemStack(ModItems.STOCKPOT_LID);
            }
            drops.add(lid);
        }
        if (parameter instanceof StockpotBlockEntity stockpotBlock && stockpotBlock.getStatus() == StockpotBlockEntity.PUT_INGREDIENT) {
            stockpotBlock.getInputs().forEach(stack -> {
                if (!stack.isEmpty()) {
                    drops.add(stack.copy());
                }
            });
        }
        return drops;
    }

}
