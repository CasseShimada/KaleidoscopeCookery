package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.google.common.collect.Lists;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(FallingBlockEntity.class)
public class FallingBlockEntityMixin {
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/item/FallingBlockEntity;spawnAtLocation(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/ItemLike;)Lnet/minecraft/world/entity/item/ItemEntity;"
            ),
            cancellable = true
    )
    private void onSpawnAtLocation(CallbackInfo ci) {
        FallingBlockEntity self = (FallingBlockEntity) (Object) this;
        BlockState blockState = self.getBlockState();
        // 如果是蒸笼
        if (blockState.is(ModBlocks.STEAMER)) {
            ci.cancel();
            // 换成自己的掉落物
            List<ItemStack> drops = dropAsItem(blockState, self.blockData, self.level());
            if (self.level() instanceof ServerLevel serverLevel) {
                for (ItemStack drop : drops) {
                    self.spawnAtLocation(serverLevel, drop);
                }
            }
        }
    }

    @Unique
    public List<ItemStack> dropAsItem(BlockState blockState, @Nullable CompoundTag steamerTag, Level level) {
        NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
        int[] cookingProgress = new int[8];
        int[] cookingTime = new int[8];
        if (steamerTag != null) {
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), steamerTag);
            ContainerHelper.loadAllItems(input, items);
            input.getIntArray(SteamerBlockEntity.COOKING_PROGRESS_TAG).ifPresent(value -> {
                int length = Math.min(cookingProgress.length, value.length);
                System.arraycopy(value, 0, cookingProgress, 0, length);
            });
            input.getIntArray(SteamerBlockEntity.COOKING_TIME_TAG).ifPresent(value -> {
                int length = Math.min(cookingTime.length, value.length);
                System.arraycopy(value, 0, cookingTime, 0, length);
            });
        }

        List<ItemStack> drops = Lists.newArrayList();
        // 先看看是单层还是双层
        boolean half = blockState.getValue(SteamerBlock.HALF);
        // 全为空？那么直接返回
        ItemStack first = ModItems.STEAMER.getDefaultInstance();
        if (items.stream().allMatch(ItemStack::isEmpty)) {
            drops.add(first);
            if (!half) {
                drops.add(ModItems.STEAMER.getDefaultInstance());
            }
            return drops;
        }

        // 只需要保存物品和进度即可
        TagValueOutput tag1 = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        TagValueOutput tag2 = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        SteamerBlockEntity.saveSplit(tag1, tag2, level, items, cookingProgress, cookingTime);

        BlockItem.setBlockEntityData(first, ModBlocks.STEAMER_BE, tag1);
        drops.add(first);

        if (!half) {
            ItemStack second = ModItems.STEAMER.getDefaultInstance();
            BlockItem.setBlockEntityData(second, ModBlocks.STEAMER_BE, tag2);
            drops.add(second);
        }
        return drops;
    }
}
