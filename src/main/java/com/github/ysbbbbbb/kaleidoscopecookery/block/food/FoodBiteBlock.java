package com.github.ysbbbbbb.kaleidoscopecookery.block.food;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class FoodBiteBlock extends FoodBlock {
    public static final EnumProperty<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;
    private final FoodProperties foodProperties;
    @Nullable
    private final Consumable consumable;
    private final IntegerProperty bites;
    private final int maxBites;
    private FoodBiteAnimateTicks.AnimateTick animateTick = null;
    private VoxelShape aabb = FoodBlock.AABB;

    public FoodBiteBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties, int maxBites,
                         FoodBiteAnimateTicks.AnimateTick animateTick) {
        super(properties);
        this.maxBites = maxBites;
        this.foodProperties = foodProperties;
        this.consumable = ModFoods.getConsumable(foodProperties);
        this.bites = IntegerProperty.create("bites", 0, maxBites);
        // 重置一遍 BlockState，因为在父类 FoodBlock 中已经创建了一个默认的 BlockStateDefinition
        StateDefinition.Builder<Block, BlockState> builder = new StateDefinition.Builder<>(this);
        this.createBitesBlockStateDefinition(builder);
        this.stateDefinition = builder.create(Block::defaultBlockState, BlockState::new);
        this.registerDefaultState(this.stateDefinition.any().setValue(bites, 0).setValue(FACING, Direction.SOUTH));
        this.animateTick = animateTick;
    }

    public FoodBiteBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties) {
        this(properties, foodProperties, 3, null);
    }

    public IntegerProperty getBites() {
        return bites;
    }

    public int getMaxBites() {
        return maxBites;
    }

    public FoodBiteBlock setAABB(VoxelShape aabb) {
        this.aabb = aabb;
        return this;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (animateTick != null) {
            animateTick.animateTick(state, level, pos, random);
        }
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hit) {
        int bites = state.getValue(this.bites);
        if (bites >= getMaxBites()) {
            level.destroyBlock(pos, true, player);
            return InteractionResult.SUCCESS;
        }
        if (level.isClientSide()) {
            if (eat(level, pos, state, player).consumesAction()) {
                return InteractionResult.SUCCESS;
            }
        }
        return eat(level, pos, state, player);
    }

    private InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(foodProperties.canAlwaysEat())) {
            return InteractionResult.PASS;
        }
        player.getFoodData().eat(foodProperties);
        if (!level.isClientSide() && consumable != null) {
            for (ConsumeEffect effect : consumable.onConsumeEffects()) {
                if (effect instanceof ApplyStatusEffectsConsumeEffect apply
                        && apply.probability() > 0.0F
                        && level.random.nextFloat() < apply.probability()) {
                    for (MobEffectInstance instance : apply.effects()) {
                        player.addEffect(new MobEffectInstance(instance));
                    }
                }
            }
        }
        level.playSound(null, pos, SoundEvents.GENERIC_EAT.value(), SoundSource.PLAYERS,
                0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        int bites = state.getValue(this.bites);
        level.gameEvent(player, GameEvent.EAT, pos);
        if (bites < getMaxBites()) {
            level.setBlock(pos, state.setValue(this.bites, bites + 1), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    protected void createBitesBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(bites, FACING);
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.aabb;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        int value = state.getValue(bites);
        return (3 - value) * 5;
    }

    @Override
    public boolean hasAnalogOutputSignal(BlockState state) {
        return true;
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
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }
}
