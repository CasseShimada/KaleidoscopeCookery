package com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.layer;

import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ScarecrowModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.ScarecrowRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.ScarecrowRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.layers.ItemInHandLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ScarecrowHandLayer extends ItemInHandLayer<ScarecrowRenderState, ScarecrowModel> {
    public ScarecrowHandLayer(ScarecrowRender entityRenderer) {
        super(entityRenderer);
    }

    @Override
    protected void submitArmWithItem(ScarecrowRenderState state, ItemStackRenderState itemState, ItemStack stack,
                                     HumanoidArm arm, PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        if (stack.isEmpty() || itemState.isEmpty()) {
            return;
        }
        poseStack.pushPose();
        this.getParentModel().translateToHand(state, arm, poseStack);
        poseStack.mulPose(Axis.XP.rotationDegrees(-90.0F));
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F));
        boolean isLeft = arm == HumanoidArm.LEFT;
        if (isLeft && stack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LanternBlock) {
            poseStack.translate(-0.375, 0.375, -2);
            poseStack.mulPose(Axis.XP.rotationDegrees(90));
            poseStack.scale(0.75F, 0.75F, 0.75F);
            BlockState blockState = blockItem.getBlock().defaultBlockState();
            collector.submitBlock(poseStack, blockState, packedLight, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
            return;
        }
        if (!isLeft) {
            poseStack.translate(0.125, 0, -1.375);
            poseStack.mulPose(Axis.ZP.rotationDegrees(-90));
            poseStack.mulPose(Axis.XP.rotationDegrees(85));
            poseStack.scale(0.75F, 0.75F, 0.75F);
            itemState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
        }
        poseStack.popPose();
    }
}
