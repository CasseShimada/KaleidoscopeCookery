package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import com.github.ysbbbbbb.kaleidoscopecookery.util.TrashCanTargeting;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Camera.class)
public abstract class CameraMixin {
    @Shadow
    @Final
    private Minecraft minecraft;

    @Shadow
    public abstract float yRot();

    @Shadow
    protected abstract void setRotation(float yaw, float pitch);

    @Inject(method = "update", at = @At("TAIL"))
    private void lockTrashCanPitch(DeltaTracker deltaTracker, CallbackInfo ci) {
        LocalPlayer player = this.minecraft.player;
        if (player != null
                && this.minecraft.options.getCameraType().isFirstPerson()
                && TrashCanTargeting.isHidingInTrashCan(player)) {
            this.setRotation(this.yRot(), 0.0F);
        }
    }
}
