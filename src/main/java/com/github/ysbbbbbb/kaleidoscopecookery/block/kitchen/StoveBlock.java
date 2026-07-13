package com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.mojang.serialization.MapCodec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.ScheduledTickAccess;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.pathfinder.PathType;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem.hasOil;
import static com.github.ysbbbbbb.kaleidoscopecookery.item.KitchenShovelItem.setHasOil;

public class StoveBlock extends HorizontalDirectionalBlock {
    public static final MapCodec<StoveBlock> CODEC = simpleCodec(StoveBlock::new);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public StoveBlock(BlockBehaviour.Properties properties) {
        super(properties
                .mapColor(MapColor.STONE)
                .sound(SoundType.STONE)
                .requiresCorrectToolForDrops()
                .lightLevel(state -> state.getValue(LIT) ? 13 : 0)
                .randomTicks()
                .strength(1.5F, 6.0F));
        this.registerDefaultState(this.stateDefinition.any()
                .setValue(FACING, Direction.SOUTH)
                .setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends HorizontalDirectionalBlock> codec() {
        return CODEC;
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        if (state.getValue(LIT)) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 0.5;
            double z = pos.getZ() + 0.5;

            if (random.nextInt(10) == 0) {
                level.playLocalSound(x, y, z,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        0.5F + random.nextFloat(),
                        random.nextFloat() * 0.7F + 0.6F, false);
            }

            level.addParticle(ParticleTypes.SMOKE,
                    x + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    y + 0.5 + random.nextDouble() / 3,
                    z + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    0, 0.02, 0);

            Direction direction = state.getValue(FACING);
            Direction.Axis axis = direction.getAxis();
            double offsetRandom = random.nextDouble() * 0.6 - 0.3;
            double xOffset = axis == Direction.Axis.X ? (double) direction.getStepX() * 0.52 : offsetRandom;
            double yOffset = 0.25 + random.nextDouble() * 6.0 / 16.0;
            double zOffset = axis == Direction.Axis.Z ? (double) direction.getStepZ() * 0.52 : offsetRandom;
            level.addParticle(ParticleTypes.FLAME,
                    x + xOffset,
                    pos.getY() + yOffset,
                    z + zOffset,
                    0, 0, 0);
        }
    }

    @Override
    public void randomTick(BlockState blockState, ServerLevel level, BlockPos pos, RandomSource random) {
        if (blockState.getValue(LIT) && level.isRainingAt(pos.above())) {
            level.setBlockAndUpdate(pos, blockState.setValue(LIT, false));
            level.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(blockState));
        }
    }

    @Override
    public void stepOn(Level level, BlockPos pos, BlockState state, Entity entity) {
        if (state.getValue(LIT)
            && level instanceof ServerLevel serverLevel
            && entity instanceof LivingEntity livingEntity
            && !livingEntity.isSteppingCarefully()
            && !livingEntity.isInvulnerable()
            && livingEntity.invulnerableTime <= 10) {
            // 排除创造模式玩家
            if (livingEntity instanceof Player player && player.hasInfiniteMaterials()) {
                return;
            }
            livingEntity.invulnerableTime = 20;
            serverLevel.broadcastDamageEvent(livingEntity, livingEntity.damageSources().hotFloor());
        }
        super.stepOn(level, pos, state, entity);
    }

    @Override
    public @NotNull BlockState updateShape(BlockState state, LevelReader levelReader, ScheduledTickAccess tickAccess, BlockPos pos,
                                           Direction direction, BlockPos neighborPos, BlockState neighborState, RandomSource random) {
        if (state.getValue(LIT) && levelReader.getFluidState(pos.above()).is(FluidTags.WATER) && levelReader instanceof ServerLevel serverLevel) {
            serverLevel.setBlockAndUpdate(pos, state.setValue(LIT, false));
            serverLevel.playSound(null, pos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 1.0F, 1.0F);
            serverLevel.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(state));
        }
        return super.updateShape(state, levelReader, tickAccess, pos, direction, neighborPos, neighborState, random);
    }

    @Override
    public @NotNull InteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult) {
        ItemStack itemInHand = player.getItemInHand(hand);
        // 点燃炉灶
        if (!state.getValue(LIT) && itemInHand.is(TagMod.LIT_STOVE)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (!level.setBlockAndUpdate(pos, state.setValue(LIT, true))) {
                return InteractionResult.FAIL;
            }
            if (itemInHand.is(Items.FIRE_CHARGE)) {
                level.playSound(null, pos,
                        SoundEvents.FIRECHARGE_USE,
                        SoundSource.BLOCKS, 1.0F,
                        level.getRandom().nextFloat() * 0.4F + 0.8F);
                itemInHand.consume(1, player);
            } else {
                level.playSound(null, pos,
                        SoundEvents.FLINTANDSTEEL_USE,
                        SoundSource.BLOCKS, 1.0F,
                        level.getRandom().nextFloat() * 0.4F + 0.8F);
                if (!player.hasInfiniteMaterials()) {
                    itemInHand.hurtAndBreak(1, player, hand);
                }
            }
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.LIT_THE_STOVE);
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            return InteractionResult.CONSUME;
        }
        // 熄灭
        if (state.getValue(LIT) && itemInHand.is(TagMod.EXTINGUISH_STOVE)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (!level.setBlockAndUpdate(pos, state.setValue(LIT, false))) {
                return InteractionResult.FAIL;
            }
            if (itemInHand.is(ModItems.KITCHEN_SHOVEL) && hasOil(itemInHand)) {
                setHasOil(itemInHand, false);
            }
            level.playSound(null, pos,
                    SoundEvents.FIRE_EXTINGUISH,
                    SoundSource.BLOCKS, 0.5F,
                    2.6F + (level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.8F);
            if (!player.hasInfiniteMaterials()) {
                itemInHand.hurtAndBreak(1, player, hand);
            }
            level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, state));
            return InteractionResult.CONSUME;
        }
        return super.useItemOn(stack, state, level, pos, player, hand, hitResult);
    }

    @Override
    public void onProjectileHit(Level level, BlockState state, BlockHitResult hitResult, Projectile projectile) {
        BlockPos hitBlockPos = hitResult.getBlockPos();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel && projectile.isOnFire()
                && projectile.mayInteract(serverLevel, hitBlockPos) && !state.getValue(LIT)) {
            if (!level.setBlock(hitBlockPos, state.setValue(BlockStateProperties.LIT, true), Block.UPDATE_ALL_IMMEDIATE)) {
                return;
            }
            level.gameEvent(GameEvent.BLOCK_CHANGE, hitBlockPos, GameEvent.Context.of(projectile, state));
            if (projectile.getOwner() instanceof Player player) {
                ModTrigger.EVENT.trigger(player, ModEventTriggerType.LIT_THE_STOVE);
            }
        }
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT, FACING);
    }

}
