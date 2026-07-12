package com.github.ysbbbbbb.kaleidoscopecookery.block.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class BaseCropBlock extends CropBlock {
    private static final VoxelShape[] SHAPE_BY_AGE = new VoxelShape[]{
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 3.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 4.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 5.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 6.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 7.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 8.0D, 16.0D),
            Block.box(0.0D, 0.0D, 0.0D, 16.0D, 9.0D, 16.0D)
    };
    protected final Supplier<Item> seed;
    @Nullable
    private final ResourceKey<LootTable> harvestLootTable;

    public BaseCropBlock(Properties properties, Supplier<Item> seed, @Nullable ResourceKey<LootTable> harvestLootTable) {
        super(properties
                .mapColor(MapColor.PLANT)
                .noCollision()
                .randomTicks()
                .instabreak()
                .sound(SoundType.CROP)
                .pushReaction(PushReaction.DESTROY));
        this.seed = seed;
        this.harvestLootTable = harvestLootTable;
    }

    @Override
    public InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult) {
        if (state.getValue(AGE) >= this.getMaxAge() && this.harvestLootTable != null) {
            if (level instanceof ServerLevel serverLevel) {
                dropFromBlockInteractLootTable(
                        serverLevel,
                        this.harvestLootTable,
                        state,
                        level.getBlockEntity(pos),
                        null,
                        player,
                        (dropLevel, stack) -> Block.popResource(dropLevel, pos, stack));
                this.onUseBreakCrop(state, serverLevel, pos, player);
            }
            return InteractionResult.SUCCESS;
        }
        return super.useWithoutItem(state, level, pos, player, hitResult);
    }

    protected void onUseBreakCrop(BlockState state, ServerLevel level, BlockPos pos, Player player) {
        // 默认右键收割后为第五阶段
        int ageAfterUse = 5;
        BlockState harvestedState = state.setValue(AGE, ageAfterUse);
        level.playSound(null,
                pos.getX() + 0.5,
                pos.getY() + 0.5,
                pos.getZ() + 0.5,
                SoundEvents.CROP_BREAK,
                SoundSource.BLOCKS,
                1.0F, 1.0F);
        level.setBlock(pos, harvestedState, Block.UPDATE_CLIENTS);
        level.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(player, harvestedState));
    }

    @Override
    protected ItemLike getBaseSeedId() {
        return this.seed.get();
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext collisionContext) {
        return SHAPE_BY_AGE[this.getAge(state)];
    }
}
