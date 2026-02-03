package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.animation.CustomArmPose;
import com.github.ysbbbbbb.kaleidoscopecookery.item.LiftBlockItem;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.entity.HumanoidArm;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
public abstract class HumanoidModelMixin {
    @Shadow
    public ModelPart rightArm;

    @Shadow
    public ModelPart leftArm;

    @Inject(method = "poseRightArm", at = @At("TAIL"))
    private void kaleidoscopeCookery$poseRightArm(HumanoidRenderState state, CallbackInfo ci) {
        if (!state.rightHandItemStack.isEmpty() && state.rightHandItemStack.getItem() instanceof LiftBlockItem) {
            CustomArmPose.applyLiftPose(this.rightArm, HumanoidArm.RIGHT);
        }
    }

    @Inject(method = "poseLeftArm", at = @At("TAIL"))
    private void kaleidoscopeCookery$poseLeftArm(HumanoidRenderState state, CallbackInfo ci) {
        if (!state.leftHandItemStack.isEmpty() && state.leftHandItemStack.getItem() instanceof LiftBlockItem) {
            CustomArmPose.applyLiftPose(this.leftArm, HumanoidArm.LEFT);
        }
    }
}
