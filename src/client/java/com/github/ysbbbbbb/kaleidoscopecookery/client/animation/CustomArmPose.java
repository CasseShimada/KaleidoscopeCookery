package com.github.ysbbbbbb.kaleidoscopecookery.client.animation;

import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.HumanoidArm;

/**
 * Client-only custom arm poses.
 */
public final class CustomArmPose {
    private CustomArmPose() {
    }

    public static void applyLiftPose(ModelPart arm, HumanoidArm side) {
        if (side == HumanoidArm.RIGHT) {
            arm.xRot = -Mth.PI;
            arm.zRot = -Mth.PI * 0.025f;
        } else {
            arm.xRot = -Mth.PI * 0.5f;
            arm.zRot = Mth.PI * 0.025f;
        }
    }
}
