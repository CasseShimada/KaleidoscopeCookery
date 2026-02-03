package com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.layer;

import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ScarecrowModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.ScarecrowRenderState;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.animal.parrot.ParrotModel;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.ParrotRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.ParrotRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.parrot.Parrot;

public class ScarecrowParrotOnShoulderLayer extends RenderLayer<ScarecrowRenderState, ScarecrowModel> {
    private final ParrotModel model;

    public ScarecrowParrotOnShoulderLayer(RenderLayerParent<ScarecrowRenderState, ScarecrowModel> renderer, EntityModelSet modelSet) {
        super(renderer);
        this.model = new ParrotModel(modelSet.bakeLayer(ModelLayers.PARROT));
    }

    @Override
    public void submit(PoseStack poseStack, SubmitNodeCollector collector, int packedLight, ScarecrowRenderState state,
                       float limbSwing, float limbSwingAmount) {
        CompoundTag tag = state.shoulderEntity;
        if (tag == null || tag.isEmpty()) {
            return;
        }
        String id = tag.getStringOr("id", "");
        if (id.isEmpty()) {
            return;
        }
        EntityType.byString(id).filter(type -> type == EntityType.PARROT).ifPresent(type -> {
            poseStack.pushPose();
            poseStack.translate(0.625F, -1.675F, 0.0625F);
            Parrot.Variant variant = Parrot.Variant.byId(tag.getIntOr("Variant", 0));
            ParrotRenderState parrotState = new ParrotRenderState();
            parrotState.pose = ParrotModel.Pose.ON_SHOULDER;
            parrotState.variant = variant;
            parrotState.ageInTicks = state.ageInTicks;
            parrotState.walkAnimationPos = state.walkAnimationPos;
            parrotState.walkAnimationSpeed = state.walkAnimationSpeed;
            parrotState.yRot = state.yRot;
            parrotState.xRot = state.xRot;
            collector.submitModel(this.model, parrotState, poseStack, this.model.renderType(ParrotRenderer.getVariantTexture(variant)),
                    packedLight, OverlayTexture.NO_OVERLAY, state.outlineColor, null);
            poseStack.popPose();
        });
    }
}
