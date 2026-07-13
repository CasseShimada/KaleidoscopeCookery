package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.VoxelShapeUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.function.Supplier;

public class StackableFoodBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<StackableFoodBlock> CODEC = simpleCodec(StackableFoodBlock::new);
    public static final int MAX_COUNT = 4;
    public static final IntegerProperty COUNT = IntegerProperty.create("count", 1, MAX_COUNT);

    private final int maxCount;
    private final Supplier<Item> item;
    private final EnumMap<Direction, VoxelShape>[] shapes;

    @SuppressWarnings("unchecked")
    public StackableFoodBlock(BlockBehaviour.Properties properties, int maxCount, Supplier<Item> item, VoxelShape... shapes) {
        super(properties
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion());
        if (maxCount > MAX_COUNT) {
            throw new IllegalArgumentException("Stackable food max count cannot exceed " + MAX_COUNT);
        }
        this.maxCount = maxCount;
        this.item = item;
        this.shapes = new EnumMap[shapes.length];
        for (int i = 0; i < shapes.length; i++) {
            this.shapes[i] = VoxelShapeUtils.horizontalShapes(shapes[i]);
        }

        this.registerDefaultState(this.defaultBlockState()
                .setValue(COUNT, 1)
                .setValue(FACING, Direction.NORTH));
    }

    private StackableFoodBlock(BlockBehaviour.Properties properties) {
        this(properties, 1, () -> net.minecraft.world.item.Items.AIR);
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public IntegerProperty getCountProperty() {
        return COUNT;
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                Player player, InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND || !stack.is(this.item.get())) {
            return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
        }

        int count = state.getValue(COUNT);
        if (count >= this.maxCount) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (!level.setBlockAndUpdate(pos, state.setValue(COUNT, count + 1))) {
            return InteractionResult.FAIL;
        }
        SoundType soundType = state.getSoundType();
        SoundEvent sound = soundType.getPlaceSound();
        level.playSound(null, pos, sound, SoundSource.BLOCKS,
                (soundType.getVolume() + 1.0F) / 2.0F,
                soundType.getPitch() * 0.8F);
        stack.consume(1, player);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        int count = state.getValue(COUNT);
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (count > 1) {
            if (!level.setBlockAndUpdate(pos, state.setValue(COUNT, count - 1))) {
                return InteractionResult.FAIL;
            }
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
        } else {
            if (!level.removeBlock(pos, false)) {
                return InteractionResult.FAIL;
            }
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
        }
        ItemUtils.getItemToLivingEntity(player, this.item.get().getDefaultInstance(), player.getInventory().getSelectedSlot());
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        drops.add(new ItemStack(this.item.get(), state.getValue(COUNT)));
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, COUNT);
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (this.shapes.length == 0) {
            return super.getShape(state, level, pos, context);
        }
        int index = Math.min(state.getValue(COUNT), this.shapes.length) - 1;
        Direction direction = state.getValue(FACING);
        return this.shapes[index].getOrDefault(direction, super.getShape(state, level, pos, context));
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected @NotNull BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    protected @NotNull BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
