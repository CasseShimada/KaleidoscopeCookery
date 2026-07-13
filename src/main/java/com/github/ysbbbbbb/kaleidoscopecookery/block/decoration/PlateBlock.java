package com.github.ysbbbbbb.kaleidoscopecookery.block.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
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
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class PlateBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<PlateBlock> CODEC = simpleCodec(PlateBlock::new);
    public static final VoxelShape AABB = Block.box(1, 0, 1, 15, 2, 15);
    private static final IntegerProperty SERVINGS_1 = IntegerProperty.create("servings", 0, 1);
    private static final IntegerProperty SERVINGS_3 = IntegerProperty.create("servings", 0, 3);
    private static final IntegerProperty SERVINGS_4 = IntegerProperty.create("servings", 0, 4);
    private static final IntegerProperty SERVINGS_5 = IntegerProperty.create("servings", 0, 5);

    private final List<Supplier<Item>> items;
    private final List<Supplier<Item>> plateItems;
    private final int maxCount;
    private VoxelShape aabb = AABB;

    public static PlateBlock create(BlockBehaviour.Properties properties, int maxCount,
                                    List<Supplier<Item>> items, List<Supplier<Item>> plateItems) {
        return switch (maxCount) {
            case 3 -> new ThreeServingPlateBlock(properties, items, plateItems);
            case 4 -> new FourServingPlateBlock(properties, items, plateItems);
            case 5 -> new FiveServingPlateBlock(properties, items, plateItems);
            default -> throw new IllegalArgumentException("Unsupported plate serving count: " + maxCount);
        };
    }

    public PlateBlock(BlockBehaviour.Properties properties, int maxCount,
                      List<Supplier<Item>> items, List<Supplier<Item>> plateItems) {
        super(properties
                .forceSolidOn()
                .instabreak()
                .mapColor(MapColor.WOOD)
                .sound(SoundType.WOOD)
                .pushReaction(PushReaction.DESTROY)
                .noOcclusion());
        this.maxCount = maxCount;
        this.items = items;
        this.plateItems = plateItems;

        this.registerDefaultState(this.defaultBlockState()
                .setValue(FACING, Direction.SOUTH)
                .setValue(this.getServingsProperty(), maxCount));
    }

    private PlateBlock(BlockBehaviour.Properties properties) {
        this(properties, 1, List.of(), List.of());
    }

    @Override
    protected @NotNull MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    public PlateBlock setAABB(VoxelShape aabb) {
        this.aabb = aabb;
        return this;
    }

    public IntegerProperty getServingsProperty() {
        return SERVINGS_1;
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos,
                                                Player player, InteractionHand hand, BlockHitResult hit) {
        if (hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.TRY_WITH_EMPTY_HAND;
        }

        IntegerProperty servings = this.getServingsProperty();
        int count = state.getValue(servings);
        if (!stack.isEmpty() && count < this.maxCount && canRefill(stack)) {
            if (!level.isClientSide()) {
                if (!player.hasInfiniteMaterials()) {
                    stack.consume(1, player);
                }
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlockAndUpdate(pos, state.setValue(servings, count + 1));
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            }
            return InteractionResult.SUCCESS;
        }

        return InteractionResult.TRY_WITH_EMPTY_HAND;
    }

    @Override
    public @NotNull InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        IntegerProperty servings = this.getServingsProperty();
        int count = state.getValue(servings);
        if (!level.isClientSide()) {
            if (count > 0) {
                giveServing(player);
                level.playSound(null, pos, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS, 1.0F, 1.0F);
                level.setBlockAndUpdate(pos, state.setValue(servings, count - 1));
                level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            } else {
                level.destroyBlock(pos, true, player);
            }
        }
        return InteractionResult.SUCCESS;
    }

    private boolean canRefill(ItemStack itemStack) {
        return this.items.size() == 1 && itemStack.is(this.items.getFirst().get());
    }

    private void giveServing(Player player) {
        int preferredSlot = player.getInventory().getSelectedSlot();
        for (Supplier<Item> item : this.items) {
            ItemUtils.getItemToLivingEntity(player, item.get().getDefaultInstance(), preferredSlot);
        }
    }

    @Override
    public @NotNull List<ItemStack> getDrops(BlockState state, LootParams.Builder params) {
        List<ItemStack> drops = new ArrayList<>();
        for (Supplier<Item> item : this.plateItems) {
            drops.add(item.get().getDefaultInstance());
        }
        int count = state.getValue(this.getServingsProperty());
        if (count > 0) {
            for (Supplier<Item> item : this.items) {
                drops.add(item.get().getDefaultInstance().copyWithCount(count));
            }
        }
        return drops;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING, this.getServingsProperty());
    }

    @Override
    public @NotNull VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.aabb;
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType) {
        return false;
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

    private static final class ThreeServingPlateBlock extends PlateBlock {
        private ThreeServingPlateBlock(BlockBehaviour.Properties properties,
                                       List<Supplier<Item>> items, List<Supplier<Item>> plateItems) {
            super(properties, 3, items, plateItems);
        }

        @Override
        public IntegerProperty getServingsProperty() {
            return SERVINGS_3;
        }
    }

    private static final class FourServingPlateBlock extends PlateBlock {
        private FourServingPlateBlock(BlockBehaviour.Properties properties,
                                      List<Supplier<Item>> items, List<Supplier<Item>> plateItems) {
            super(properties, 4, items, plateItems);
        }

        @Override
        public IntegerProperty getServingsProperty() {
            return SERVINGS_4;
        }
    }

    private static final class FiveServingPlateBlock extends PlateBlock {
        private FiveServingPlateBlock(BlockBehaviour.Properties properties,
                                      List<Supplier<Item>> items, List<Supplier<Item>> plateItems) {
            super(properties, 5, items, plateItems);
        }

        @Override
        public IntegerProperty getServingsProperty() {
            return SERVINGS_5;
        }
    }
}
