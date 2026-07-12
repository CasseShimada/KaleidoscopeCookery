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
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TeacupBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<TeacupBlock> CODEC = simpleCodec(TeacupBlock::new);
    public static final VoxelShape AABB = Block.box(1, 0, 1, 15, 2, 15);
    public static final int MAX_CUP_COUNT = 4;
    public static final IntegerProperty CUP_COUNT = IntegerProperty.create("cup_count", 1, MAX_CUP_COUNT);
    public static final IntegerProperty TEA_COUNT = IntegerProperty.create("tea_count", 1, MAX_CUP_COUNT);

    protected final int maxCount;
    protected VoxelShape aabb = AABB;

    public TeacupBlock(BlockBehaviour.Properties properties) {
        this(properties, 4);
    }

    public TeacupBlock(BlockBehaviour.Properties properties, int maxCount) {
        super(properties
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion());
        if (maxCount > MAX_CUP_COUNT) {
            throw new IllegalArgumentException("Teacup max count cannot exceed " + MAX_CUP_COUNT);
        }
        this.maxCount = maxCount;
        this.registerDefaultState(this.defaultBlockState()
                .setValue(CUP_COUNT, 1)
                .setValue(TEA_COUNT, 1)
                .setValue(FACING, Direction.SOUTH));
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public TeacupBlock setAABB(VoxelShape aabb) {
        this.aabb = aabb;
        return this;
    }

    public int getMaxCount() {
        return maxCount;
    }

    public IntegerProperty getCupCountProperty() {
        return CUP_COUNT;
    }

    public IntegerProperty getTeaCountProperty() {
        return TEA_COUNT;
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
            if (pourOut.isEmpty() || pourOut.getItem() != this.asItem()) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            int count = state.getValue(TEA_COUNT);
            if (count < state.getValue(CUP_COUNT)) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(pos, state.setValue(TEA_COUNT, count + 1));
                    level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                    TeapotItem.pourOut(itemInHand, level, player);
                    spawnPourParticles(level, pos);
                }
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
            return InteractionResult.CONSUME;
        }
        if (itemInHand.is(ModItems.EMPTY_CUP)) {
            int count = state.getValue(CUP_COUNT);
            if (count < this.maxCount) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(pos, state.setValue(CUP_COUNT, count + 1));
                    level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.hasInfiniteMaterials()) {
                        itemInHand.shrink(1);
                    }
                }
                return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
            }
            return InteractionResult.CONSUME;
        }
        if (itemInHand.getItem() instanceof TeacupItem teacupItem) {
            if (teacupItem.getBlock() != this) {
                return InteractionResult.TRY_WITH_EMPTY_HAND;
            }
            int cupCountNum = state.getValue(CUP_COUNT);
            int teaCountNum = state.getValue(TEA_COUNT);
            if (cupCountNum < this.maxCount) {
                if (!level.isClientSide()) {
                    level.setBlockAndUpdate(pos, state
                            .setValue(CUP_COUNT, cupCountNum + 1)
                            .setValue(TEA_COUNT, teaCountNum + 1));
                    level.playSound(null, pos, state.getSoundType().getPlaceSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
                    if (!player.hasInfiniteMaterials()) {
                        itemInHand.shrink(1);
                    }
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
        int cupCountNum = state.getValue(CUP_COUNT);
        int teaCountNum = state.getValue(TEA_COUNT);
        int emptyCountNum = cupCountNum - teaCountNum;
        if (emptyCountNum > 0) {
            if (!level.isClientSide()) {
                ItemUtils.getItemToLivingEntity(player, new ItemStack(ModItems.EMPTY_CUP));
                level.setBlockAndUpdate(pos, cupCountNum == 1 ? Blocks.AIR.defaultBlockState() : state.setValue(CUP_COUNT, cupCountNum - 1));
                level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }
        if (!level.isClientSide()) {
            ItemUtils.getItemToLivingEntity(player, new ItemStack(this));
            level.setBlockAndUpdate(pos, cupCountNum == 1 ? Blocks.AIR.defaultBlockState()
                    : state.setValue(TEA_COUNT, teaCountNum - 1).setValue(CUP_COUNT, cupCountNum - 1));
            level.playSound(null, pos, state.getSoundType().getBreakSound(), SoundSource.BLOCKS, 1.0F, 1.0F);
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
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (random.nextInt(20) == 0) {
            level.addParticle(ModParticles.COOKING,
                    pos.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + 0.5 + random.nextDouble() / 3,
                    pos.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    0.3, 0.1, 0.3);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(CUP_COUNT, TEA_COUNT, FACING);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.aabb;
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        int teaCountNum = state.getValue(TEA_COUNT);
        int cupCountNum = state.getValue(CUP_COUNT);
        int emptyCountNum = cupCountNum - teaCountNum;
        if (emptyCountNum > 0) {
            drops.add(new ItemStack(ModItems.EMPTY_CUP, emptyCountNum));
        }
        if (teaCountNum > 0) {
            drops.add(new ItemStack(this, teaCountNum));
        }
        return drops;
    }
}
