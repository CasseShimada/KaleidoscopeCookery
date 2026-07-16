package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MobBucketItem;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MobBucketItem.class)
public class MobBucketItemMixin {
    @Shadow
    @Final
    private EntityType<? extends Mob> type;

    @Inject(method = "checkExtraContent", at = @At("RETURN"))
    private void onCheckExtraContent(LivingEntity entity, Level level, ItemStack containerStack, BlockPos pos, CallbackInfo ci) {
        if (level.isClientSide() || !(entity instanceof Player player)) {
            return;
        }

        if (BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(this.type).is(TagMod.RICE_GROWTH_BOOSTER)
                && hasRiceCropNear(level, pos)) {
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_FISH_IN_RICE_FIELD);
        }
    }

    private static boolean hasRiceCropNear(Level level, BlockPos pos) {
        for (BlockPos candidate : BlockPos.betweenClosed(
                pos.offset(-1, 0, -1), pos.offset(1, 0, 1))) {
            if (level.getBlockState(candidate).is(ModBlocks.RICE_CROP)) {
                return true;
            }
        }
        return false;
    }
}
