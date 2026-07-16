package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.client.render.TrashCanRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.FabricRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntityRenderer.class)
public abstract class LivingEntityRendererMixin {
    @Inject(
            method = "submit(Lnet/minecraft/client/renderer/entity/state/LivingEntityRenderState;Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/SubmitNodeCollector;Lnet/minecraft/client/renderer/state/level/CameraRenderState;)V",
            at = @At("HEAD"),
            cancellable = true
    )
    private void hideTrashCanPassenger(LivingEntityRenderState state, PoseStack poseStack,
                                        SubmitNodeCollector collector, CameraRenderState cameraState,
                                        CallbackInfo ci) {
        if (((FabricRenderState) state).getDataOrDefault(TrashCanRenderState.HIDDEN, false)) {
            ci.cancel();
        }
    }
}
