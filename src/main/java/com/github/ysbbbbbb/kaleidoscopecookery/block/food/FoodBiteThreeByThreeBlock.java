package com.github.ysbbbbbb.kaleidoscopecookery.block.food;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

public class FoodBiteThreeByThreeBlock extends FoodBiteBlock implements EntityBlock {
    public static final EnumProperty<NinePart> PART = EnumProperty.create("part", NinePart.class);

    // 3x3 plate footprint, 2 blocks tall.
    private static final VoxelShape LEFT_UP = Block.box(4, 0, 4, 16, 2, 16);
    private static final VoxelShape UP = Block.box(0, 0, 4, 16, 2, 16);
    private static final VoxelShape RIGHT_UP = Block.box(0, 0, 4, 12, 2, 16);
    private static final VoxelShape LEFT_CENTER = Block.box(4, 0, 0, 16, 2, 16);
    private static final VoxelShape CENTER = Block.box(0, 0, 0, 16, 2, 16);
    private static final VoxelShape RIGHT_CENTER = Block.box(0, 0, 0, 12, 2, 16);
    private static final VoxelShape LEFT_DOWN = Block.box(4, 0, 0, 16, 2, 12);
    private static final VoxelShape DOWN = Block.box(0, 0, 0, 16, 2, 12);
    private static final VoxelShape RIGHT_DOWN = Block.box(0, 0, 0, 12, 2, 12);

    public FoodBiteThreeByThreeBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties, int maxBites,
                                     @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
        super(properties, foodProperties, maxBites, animateTick);
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(getBites(), 0)
                .setValue(FACING, Direction.SOUTH)
                .setValue(PART, NinePart.CENTER));
    }

    @Override
    public IntegerProperty getBites() {
        return BITES_8;
    }

    private static BlockPos getCenterPos(BlockPos pos, BlockState state) {
        NinePart part = state.getValue(PART);
        return pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
    }

    private static void handleRemove(Level world, BlockPos pos, BlockState state, @Nullable Player player) {
        if (world.isClientSide()) {
            return;
        }
        BlockPos centerPos = getCenterPos(pos, state);
        BlockEntity blockEntity = world.getBlockEntity(centerPos);
        if (!(blockEntity instanceof FoodBiteThreeByThreeBlockEntity)) {
            return;
        }
        for (int i = -1; i < 2; i++) {
            for (int j = -1; j < 2; j++) {
                BlockPos offsetPos = centerPos.offset(i, 0, j);
                if (i == 0 && j == 0) {
                    world.destroyBlock(offsetPos, true, player);
                } else {
                    world.setBlock(offsetPos, Blocks.AIR.defaultBlockState(), Block.UPDATE_SUPPRESS_DROPS | Block.UPDATE_ALL);
                }
            }
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        BlockPos centerPos = getCenterPos(pos, state);
        BlockState centerState = level.getBlockState(centerPos);
        if (!centerState.is(this)) {
            return InteractionResult.PASS;
        }

        int bites = centerState.getValue(getBites());
        if (bites >= getMaxBites()) {
            handleRemove(level, centerPos, centerState, player);
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        return super.useWithoutItem(centerState, level, centerPos, player, hit);
    }

    @Override
    public BlockState playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
        handleRemove(world, pos, state, player);
        return super.playerWillDestroy(world, pos, state, player);
    }

    @Override
    public void wasExploded(ServerLevel level, BlockPos pos, Explosion explosion) {
        BlockState state = level.getBlockState(pos);
        if (state.is(this)) {
            handleRemove(level, pos, state, null);
        }
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
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(getBites(), FACING, PART);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        if (state.getValue(PART).isCenter()) {
            return new FoodBiteThreeByThreeBlockEntity(pos, state);
        }
        return null;
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.MODEL;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return switch (state.getValue(PART)) {
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
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        BlockPos centerPos = getCenterPos(pos, state);
        BlockState centerState = level.getBlockState(centerPos);
        if (!centerState.is(this)) {
            return 0;
        }
        return super.getAnalogOutputSignal(centerState, level, centerPos, direction);
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        if (state.getValue(PART) != NinePart.CENTER) {
            return Collections.emptyList();
        }
        return super.getDrops(state, params);
    }
}
