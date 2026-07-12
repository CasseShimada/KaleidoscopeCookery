package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.server.level.ServerLevel;
import org.spongepowered.asm.mixin.Mixin;
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
            self.discard();
            // 换成自己的掉落物
            List<ItemStack> drops = SteamerBlockEntity.dropFallingSteamerAsItem(blockState, self.blockData, self.level());
            if (self.level() instanceof ServerLevel serverLevel) {
                for (ItemStack drop : drops) {
                    self.spawnAtLocation(serverLevel, drop);
                }
            }
        }
    }
}
