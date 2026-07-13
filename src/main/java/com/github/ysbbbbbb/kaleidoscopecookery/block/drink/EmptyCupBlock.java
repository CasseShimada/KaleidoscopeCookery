package com.github.ysbbbbbb.kaleidoscopecookery.block.drink;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeacupItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeapotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EmptyCupBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<EmptyCupBlock> CODEC = simpleCodec(EmptyCupBlock::new);
    public static final VoxelShape AABB = Block.box(1, 0, 1, 15, 2, 15);
    public static final int MAX_COUNT = 4;
    public static final IntegerProperty CUP_COUNT = IntegerProperty.create("cup_count", 1, MAX_COUNT);

    public EmptyCupBlock(BlockBehaviour.Properties properties) {
        super(properties
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion());
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(CUP_COUNT, 1)
                .setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                               Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(ModItems.TEAPOT)) {
            ItemStack pourOut = TeapotItem.getPourOut(itemInHand, level);
            if (!(pourOut.getItem() instanceof TeacupItem teacupItem) || !(teacupItem.getBlock() instanceof TeacupBlock teacupBlock)) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            int currentCount = state.getValue(CUP_COUNT);
            if (!level.isClientSide()) {
                BlockState filledState = teacupBlock.defaultBlockState()
                        .setValue(teacupBlock.getCupCountProperty(), Math.min(currentCount, teacupBlock.getMaxCount()))
                        .setValue(teacupBlock.getTeaCountProperty(), 1)
                        .setValue(FACING, state.getValue(FACING));
                if (!level.setBlockAndUpdate(pos, filledState)) {
                    return InteractionResult.FAIL;
                }
                if (currentCount > teacupBlock.getMaxCount()) {
                    ItemUtils.getItemToLivingEntity(player, new ItemStack(ModItems.EMPTY_CUP, currentCount - teacupBlock.getMaxCount()));
                }
                level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                TeapotItem.pourOut(itemInHand, level, player);
                spawnPourParticles(level, pos);
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        if (itemInHand.is(ModItems.EMPTY_CUP)) {
            int count = state.getValue(CUP_COUNT);
            if (count < MAX_COUNT) {
                if (!level.isClientSide()) {
                    if (!level.setBlockAndUpdate(pos, state.setValue(CUP_COUNT, count + 1))) {
                        return InteractionResult.FAIL;
                    }
                    level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.hasInfiniteMaterials()) {
                        itemInHand.consume(1, player);
                    }
                    level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
                }
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
            return InteractionResult.CONSUME;
        }
        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    protected @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                                       Player player, BlockHitResult hitResult) {
        int count = state.getValue(CUP_COUNT);
        if (!level.isClientSide()) {
            BlockState updatedState = count == 1 ? Blocks.AIR.defaultBlockState() : state.setValue(CUP_COUNT, count - 1);
            if (!level.setBlockAndUpdate(pos, updatedState)) {
                return InteractionResult.FAIL;
            }
            ItemUtils.getItemToLivingEntity(player, new ItemStack(ModItems.EMPTY_CUP));
            level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(count == 1 ? GameEvent.BLOCK_DESTROY : GameEvent.BLOCK_CHANGE,
                    pos, GameEvent.Context.of(player, state));
        }
        return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
    }

    private static void spawnPourParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            RandomSource random = level.getRandom();
            serverLevel.sendParticles(ModParticles.COOKING,
                    pos.getX() + 0.5, pos.getY() + 0.35, pos.getZ() + 0.5,
                    4, 0.12 + random.nextDouble() * 0.04, 0.08, 0.12 + random.nextDouble() * 0.04, 0.02);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CUP_COUNT, FACING);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return AABB;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        return List.of(new ItemStack(ModItems.EMPTY_CUP, state.getValue(CUP_COUNT)));
    }
}
