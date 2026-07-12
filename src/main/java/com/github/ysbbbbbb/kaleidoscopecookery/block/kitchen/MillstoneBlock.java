package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IMillstone;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MillstoneBlock extends HorizontalDirectionalBlock implements EntityBlock {
    public static final MapCodec<MillstoneBlock> CODEC = simpleCodec(MillstoneBlock::new);
    public static final EnumProperty<NinePart> PART = EnumProperty.create("part", NinePart.class);

    private static final VoxelShape CENTER = Block.box(-2, 0, -2, 18, 15, 18);

    private static final VoxelShape LEFT_UP = Shapes.or(
            Block.box(11, 0, 11, 16, 6, 16),
            Block.box(8, 6, 8, 16, 14, 16));

    private static final VoxelShape UP = Shapes.or(
            Block.box(0, 0, 11, 16, 6, 16),
            Block.box(0, 6, 8, 16, 14, 16));

    private static final VoxelShape RIGHT_UP = Shapes.or(
            Block.box(0, 0, 11, 5, 6, 16),
            Block.box(0, 6, 8, 8, 14, 16));

    private static final VoxelShape LEFT_CENTER = Shapes.or(
            Block.box(11, 0, 0, 16, 6, 16),
            Block.box(8, 6, 0, 16, 14, 16));

    private static final VoxelShape RIGHT_CENTER = Shapes.or(
            Block.box(0, 0, 0, 5, 6, 16),
            Block.box(0, 6, 0, 8, 14, 16));

    private static final VoxelShape LEFT_DOWN = Shapes.or(
            Block.box(11, 0, 0, 16, 6, 5),
            Block.box(8, 6, 0, 16, 14, 8));

    private static final VoxelShape DOWN = Shapes.or(
            Block.box(0, 0, 0, 16, 6, 5),
            Block.box(0, 6, 0, 16, 14, 8));

    private static final VoxelShape RIGHT_DOWN = Shapes.or(
            Block.box(0, 0, 0, 5, 6, 5),
            Block.box(0, 6, 0, 8, 14, 8));

    public MillstoneBlock(BlockBehaviour.Properties properties) {
        super(properties
                .mapColor(MapColor.STONE)
                .instrument(NoteBlockInstrument.BASEDRUM)
                .requiresCorrectToolForDrops()
                .strength(1.5F, 6.0F)
                .sound(SoundType.STONE)
                .forceSolidOn()
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(PART, NinePart.CENTER)
                .setValue(FACING, Direction.NORTH));
    }

    private static boolean handleRemove(Level world, BlockPos pos, BlockState state, @Nullable Player player) {
        if (world.isClientSide()) {
            return false;
        }
        if (!state.hasProperty(PART)) {
            return handleRemoveFromNearbyPart(world, pos, player);
        }
        NinePart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        return handleRemoveAtCenter(world, pos, centerPos, player);
    }

    private static boolean handleRemoveFromNearbyPart(Level world, BlockPos pos, @Nullable Player player) {
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = pos.offset(i, 0, j);
                BlockState searchState = world.getBlockState(searchPos);
                if (searchState.is(ModBlocks.MILLSTONE) && searchState.hasProperty(PART)) {
                    NinePart part = searchState.getValue(PART);
                    BlockPos centerPos = searchPos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
                    if (handleRemoveAtCenter(world, pos, centerPos, player)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean handleRemoveAtCenter(Level world, BlockPos dropPos, BlockPos centerPos, @Nullable Player player) {
        BlockEntity te = world.getBlockEntity(centerPos);
        if (!(te instanceof MillstoneBlockEntity millstone)) {
            return false;
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos offsetPos = centerPos.offset(i, 0, j);
                world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
            }
        }
        if (player != null && !player.hasInfiniteMaterials()) {
            Block.popResource(world, dropPos, ModItems.MILLSTONE.getDefaultInstance());
        }
        if (!millstone.getOutput().isEmpty() && millstone.getCarrier().isEmpty()) {
            Block.popResource(world, dropPos, millstone.getOutput());
        }
        if (!millstone.getInput().isEmpty()) {
            Block.popResource(world, dropPos, millstone.getInput());
        }
        return true;
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> blockEntityType) {
        if (level.isClientSide() || blockEntityType != ModBlocks.MILLSTONE_BE) {
            return null;
        }
        return (levelIn, pos, stateIn, blockEntity) -> ((MillstoneBlockEntity) blockEntity).tick(levelIn);
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        NinePart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        BlockEntity te = level.getBlockEntity(centerPos);
        if (!(te instanceof MillstoneBlockEntity millstone)) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        ItemStack mainHandItem = player.getMainHandItem();
        if (level.isClientSide()) {
            return millstone.canTakeItem(mainHandItem) || millstone.canPutItem(level, mainHandItem)
                    ? InteractionResult.SUCCESS
                    : InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        if (millstone.onTakeItem(player, mainHandItem)) {
            return InteractionResult.CONSUME;
        }
        if (millstone.onPutItem(level, player, mainHandItem)) {
            return InteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        NinePart part = state.getValue(PART);
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        BlockEntity blockEntity = level.getBlockEntity(centerPos);
        if (!(blockEntity instanceof MillstoneBlockEntity millstone)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return millstone.canTakeItem(ItemStack.EMPTY) ? InteractionResult.SUCCESS : InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        return millstone.onTakeItem(player, ItemStack.EMPTY) ? InteractionResult.CONSUME : InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public void stepOn(Level pLevel, BlockPos pPos, BlockState pState, Entity pEntity) {
        // 每 5 tick 检查一次
        if (pEntity instanceof Mob mob && !pLevel.isClientSide() && pLevel.getGameTime() % 5 == 4) {
            NinePart part = pState.getValue(PART);
            BlockPos centerPos = pPos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
            BlockEntity blockEntity = pLevel.getBlockEntity(centerPos);
            if (blockEntity instanceof MillstoneBlockEntity millstone && !millstone.hasEntity() && millstone.canBindEntity(mob)) {
                millstone.bindEntity(mob);
                // 检查实体的乘客是不是玩家，如果是，那么给予成就
                if (mob.getFirstPassenger() instanceof ServerPlayer player) {
                    ModTrigger.EVENT.trigger(player, ModEventTriggerType.DRIVE_THE_MILLSTONE);
                }
            }
        }
    }

    @Override
    public @NotNull BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        handleRemove(world, pos, state, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        handleRemove(level, pos, level.getBlockState(pos), null);
        super.wasExploded(level, pos, explosion);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos centerPos = context.getClickedPos();
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = centerPos.offset(i, 0, j);
                if (!context.getLevel().getBlockState(searchPos).canBeReplaced(context)) {
                    return null;
                }
            }
        }
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(worldIn, pos, state, placer, stack);
        if (worldIn.isClientSide()) {
            return;
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos searchPos = pos.offset(i, 0, j);
                NinePart part = NinePart.getPartByPos(i, j);
                if (part != null && !part.isCenter()) {
                    worldIn.setBlock(searchPos, state.setValue(PART, part), Block.UPDATE_ALL);
                }
            }
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> pBuilder) {
        pBuilder.add(PART, FACING);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART).isCenter()) {
            return new MillstoneBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public @NotNull RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
        NinePart value = pState.getValue(PART);
        return switch (value) {
            case LEFT_UP -> LEFT_UP;
            case UP -> UP;
            case RIGHT_UP -> RIGHT_UP;
            case LEFT_CENTER -> LEFT_CENTER;
            case CENTER -> CENTER;
            case RIGHT_CENTER -> RIGHT_CENTER;
            case LEFT_DOWN -> LEFT_DOWN;
            case DOWN -> DOWN;
            case RIGHT_DOWN -> RIGHT_DOWN;
        };
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }
}
