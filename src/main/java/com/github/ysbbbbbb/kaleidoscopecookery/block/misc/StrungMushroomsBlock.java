package com.github.ysbbbbbb.kaleidoscopecookery.block.misc;

import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class StrungMushroomsBlock extends Block {
    public static final BooleanProperty IS_HEAD = BooleanProperty.create("is_head");
    public static final BooleanProperty SHEARED = BooleanProperty.create("sheared");

    private static final VoxelShape AABB_HEAD = Block.box(4, 2, 4, 12, 16, 12);
    private static final VoxelShape AABB_BODY = Block.box(3.5, 0, 3.5, 12.5, 16, 12.5);

    public StrungMushroomsBlock(BlockBehaviour.Properties properties) {
        super(properties
                .mapColor(MapColor.COLOR_BROWN)
                .noCollision()
                .instabreak()
                .sound(SoundType.GRASS)
                .pushReaction(PushReaction.DESTROY));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(IS_HEAD, true)
                .setValue(SHEARED, false));
    }

    @Override
    public InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player,
                                       InteractionHand hand, BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND || !stack.is(Items.BROWN_MUSHROOM)) {
            return InteractionResult.PASS;
        }
        return harvest(state, level, pos, player);
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player,
                                            BlockHitResult hitResult) {
        return harvest(state, level, pos, player);
    }

    private static InteractionResult harvest(BlockState state, Level level, BlockPos pos, Player player) {
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        boolean sheared = state.getValue(SHEARED);
        BlockState updatedState = sheared ? Blocks.AIR.defaultBlockState() : state.setValue(SHEARED, true);
        if (!level.setBlock(pos, updatedState, Block.UPDATE_ALL)) {
            return InteractionResult.FAIL;
        }
        level.gameEvent(sheared ? GameEvent.BLOCK_DESTROY : GameEvent.BLOCK_CHANGE,
                pos, GameEvent.Context.of(player, state));
        ItemStack mushrooms = new ItemStack(Items.BROWN_MUSHROOM, 3);
        ItemUtils.giveItemToPlayer(player, mushrooms, player.getInventory().getSelectedSlot());
        level.playSound(null, pos,
                SoundEvents.SWEET_BERRY_BUSH_PICK_BERRIES,
                SoundSource.BLOCKS, 1.0F,
                0.8F + level.getRandom().nextFloat() * 0.4F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    new BlockParticleOption(ParticleTypes.BLOCK, state),
                    pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5,
                    20,
                    0.25, 0.25, 0.25,
                    0.05);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos currentPos,
                                  Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (direction == Direction.DOWN.getOpposite() && !state.canSurvive(levelReader, currentPos)) {
            tickAccess.scheduleTick(currentPos, this, 1);
        }
        if (direction == Direction.DOWN) {
            return state.setValue(IS_HEAD, !neighborState.is(this));
        }
        return super.updateShape(state, levelReader, tickAccess, currentPos, direction, neighborPos, neighborState, random);
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        BlockPos belowPos = pos.relative(Direction.DOWN.getOpposite());
        BlockState belowState = levelReader.getBlockState(belowPos);
        return belowState.is(this) || belowState.isFaceSturdy(levelReader, belowPos, Direction.DOWN);
    }

    @Override
    public void tick(BlockState state, ServerLevel serverLevel, BlockPos pos, RandomSource random) {
        if (!state.canSurvive(serverLevel, pos)) {
            serverLevel.destroyBlock(pos, true);
        }
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(IS_HEAD, SHEARED);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return state.getValue(IS_HEAD) ? AABB_HEAD : AABB_BODY;
    }
}
