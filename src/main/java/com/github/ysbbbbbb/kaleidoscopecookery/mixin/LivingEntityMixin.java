package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.effect.TundraStriderEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.FarmerArmorEffectEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect.PreservationEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {
    @Unique
    private ItemStack cookery$finishingItem = ItemStack.EMPTY;

    protected LivingEntityMixin(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }

    @Inject(method = "getBlockSpeedFactor", at = @At("HEAD"), cancellable = true)
    private void onGetBlockSpeedFactor(CallbackInfoReturnable<Float> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        TundraStriderEffect.getBlockSpeedFactor(entity).ifPresent(cir::setReturnValue);
    }

    @Inject(method = "completeUsingItem", at = @At("HEAD"))
    private void captureFinishingItem(CallbackInfo ci) {
        this.cookery$finishingItem = ((LivingEntity) (Object) this).getUseItem().copy();
    }

    @Inject(method = "completeUsingItem", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/LivingEntity;stopUsingItem()V"
    ))
    private void onItemUseFinished(CallbackInfo ci) {
        PreservationEvent.onItemUseFinished((LivingEntity) (Object) this, this.cookery$finishingItem);
    }

    @Inject(method = "completeUsingItem", at = @At("TAIL"))
    private void clearFinishingItem(CallbackInfo ci) {
        this.cookery$finishingItem = ItemStack.EMPTY;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void onLivingTick(CallbackInfo ci) {
        FarmerArmorEffectEvent.onLivingTick((LivingEntity) (Object) this);
    }
}
