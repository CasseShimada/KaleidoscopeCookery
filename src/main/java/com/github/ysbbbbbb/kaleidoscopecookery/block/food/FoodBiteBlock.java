package com.github.ysbbbbbb.kaleidoscopecookery.block.food;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteAnimateTicks;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
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
    public static final IntegerProperty QUALITY = IntegerProperty.create("quality", 0, Quality.values().length);
    public static final int DEFAULT_QUALITY = Quality.values().length;
    protected static final IntegerProperty BITES_1 = IntegerProperty.create("bites", 0, 1);
    protected static final IntegerProperty BITES_2 = IntegerProperty.create("bites", 0, 2);
    protected static final IntegerProperty BITES_3 = IntegerProperty.create("bites", 0, 3);
    protected static final IntegerProperty BITES_4 = IntegerProperty.create("bites", 0, 4);
    protected static final IntegerProperty BITES_5 = IntegerProperty.create("bites", 0, 5);
    protected static final IntegerProperty BITES_6 = IntegerProperty.create("bites", 0, 6);
    protected static final IntegerProperty BITES_8 = IntegerProperty.create("bites", 0, 8);

    private final FoodProperties foodProperties;
    @Nullable
    private final Consumable consumable;
    private final int maxBites;
    private FoodBiteAnimateTicks.AnimateTick animateTick = null;
    private VoxelShape aabb = FoodBlock.AABB;

    public static FoodBiteBlock create(BlockBehaviour.Properties properties, FoodProperties foodProperties, int maxBites,
                                       @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
        return switch (maxBites) {
            case 1 -> new OneBiteFoodBlock(properties, foodProperties, animateTick);
            case 2 -> new TwoBiteFoodBlock(properties, foodProperties, animateTick);
            case 3 -> new ThreeBiteFoodBlock(properties, foodProperties, animateTick);
            case 4 -> new FourBiteFoodBlock(properties, foodProperties, animateTick);
            case 5 -> new FiveBiteFoodBlock(properties, foodProperties, animateTick);
            case 6 -> new SixBiteFoodBlock(properties, foodProperties, animateTick);
            default -> throw new IllegalArgumentException("Unsupported food bite count: " + maxBites);
        };
    }

    public FoodBiteBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties, int maxBites,
                         FoodBiteAnimateTicks.AnimateTick animateTick) {
        super(properties);
        this.maxBites = maxBites;
        this.foodProperties = foodProperties;
        this.consumable = ModFoods.getConsumable(foodProperties);
        if (maxBites != this.getMaxBitesFromProperty()) {
            throw new IllegalArgumentException("Food bite count " + maxBites
                    + " does not match property range " + this.getMaxBitesFromProperty());
        }
        this.registerDefaultState(this.defaultBlockState()
                .setValue(this.getBites(), 0)
                .setValue(FACING, Direction.SOUTH)
                .setValue(QUALITY, DEFAULT_QUALITY));
        this.animateTick = animateTick;
    }

    public FoodBiteBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties) {
        this(properties, foodProperties, 3, null);
    }

    public IntegerProperty getBites() {
        return BITES_3;
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
        if (!player.canEat(foodProperties.canAlwaysEat())) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        IntegerProperty bitesProperty = this.getBites();
        int bites = state.getValue(bitesProperty);
        if (bites >= getMaxBites()) {
            if (!level.destroyBlock(pos, true, player)) {
                return InteractionResult.FAIL;
            }
            level.gameEvent(GameEvent.BLOCK_DESTROY, pos, GameEvent.Context.of(player, state));
            return InteractionResult.CONSUME;
        }
        return eat(level, pos, state, player);
    }

    private InteractionResult eat(Level level, BlockPos pos, BlockState state, Player player) {
        if (!player.canEat(foodProperties.canAlwaysEat())) {
            return InteractionResult.PASS;
        }
        IntegerProperty bitesProperty = this.getBites();
        int bites = state.getValue(bitesProperty);
        if (!level.setBlock(pos, state.setValue(bitesProperty, bites + 1), Block.UPDATE_ALL)) {
            return InteractionResult.FAIL;
        }
        int qualityId = state.getValue(QUALITY);
        double ratio = qualityId == DEFAULT_QUALITY ? 1.0 : Quality.BY_ID.apply(qualityId).getRatio();
        player.getFoodData().eat(
                (int) Math.round(foodProperties.nutrition() * ratio),
                (float) (foodProperties.saturation() * ratio));
        if (consumable != null) {
            for (ConsumeEffect effect : consumable.onConsumeEffects()) {
                if (effect instanceof ApplyStatusEffectsConsumeEffect apply
                        && apply.probability() > 0.0F
                        && level.getRandom().nextFloat() < apply.probability()) {
                    for (MobEffectInstance instance : apply.effects()) {
                        int duration = (int) Math.round(instance.getDuration() * ratio);
                        if (duration > 0) {
                            player.addEffect(new MobEffectInstance(
                                    instance.getEffect(), duration, instance.getAmplifier()));
                        }
                    }
                }
            }
        }
        level.playSound(null, pos, SoundEvents.GENERIC_EAT.value(), SoundSource.PLAYERS,
                0.5F, level.getRandom().nextFloat() * 0.1F + 0.9F);
        level.gameEvent(player, GameEvent.EAT, pos);
        return InteractionResult.CONSUME;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(this.getBites(), FACING, QUALITY);
    }

    @Override
    public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext context) {
        return this.aabb;
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos, Direction direction) {
        int value = state.getValue(this.getBites());
        return Math.max(0, (getMaxBites() - value) * 15 / getMaxBites());
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
        int quality = QualityUtils.hasQuality(context.getItemInHand())
                ? QualityUtils.getQuality(context.getItemInHand()).getId()
                : DEFAULT_QUALITY;
        return this.defaultBlockState()
                .setValue(FACING, context.getHorizontalDirection().getOpposite())
                .setValue(QUALITY, quality);
    }

    @Override
    public BlockState rotate(BlockState state, Rotation rotation) {
        return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(BlockState state, Mirror mirror) {
        return state.rotate(mirror.getRotation(state.getValue(FACING)));
    }

    private int getMaxBitesFromProperty() {
        return this.getBites().getPossibleValues().stream()
                .mapToInt(Integer::intValue)
                .max()
                .orElseThrow();
    }

    private static final class OneBiteFoodBlock extends FoodBiteBlock {
        private OneBiteFoodBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties,
                                 @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
            super(properties, foodProperties, 1, animateTick);
        }

        @Override
        public IntegerProperty getBites() {
            return BITES_1;
        }
    }

    private static final class TwoBiteFoodBlock extends FoodBiteBlock {
        private TwoBiteFoodBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties,
                                 @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
            super(properties, foodProperties, 2, animateTick);
        }

        @Override
        public IntegerProperty getBites() {
            return BITES_2;
        }
    }

    private static final class ThreeBiteFoodBlock extends FoodBiteBlock {
        private ThreeBiteFoodBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties,
                                   @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
            super(properties, foodProperties, 3, animateTick);
        }

        @Override
        public IntegerProperty getBites() {
            return BITES_3;
        }
    }

    private static final class FourBiteFoodBlock extends FoodBiteBlock {
        private FourBiteFoodBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties,
                                  @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
            super(properties, foodProperties, 4, animateTick);
        }

        @Override
        public IntegerProperty getBites() {
            return BITES_4;
        }
    }

    private static final class FiveBiteFoodBlock extends FoodBiteBlock {
        private FiveBiteFoodBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties,
                                  @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
            super(properties, foodProperties, 5, animateTick);
        }

        @Override
        public IntegerProperty getBites() {
            return BITES_5;
        }
    }

    private static final class SixBiteFoodBlock extends FoodBiteBlock {
        private SixBiteFoodBlock(BlockBehaviour.Properties properties, FoodProperties foodProperties,
                                 @Nullable FoodBiteAnimateTicks.AnimateTick animateTick) {
            super(properties, foodProperties, 6, animateTick);
        }

        @Override
        public IntegerProperty getBites() {
            return BITES_6;
        }
    }
}
