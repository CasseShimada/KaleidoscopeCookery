package com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ScarecrowModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.layer.ScarecrowHandLayer;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.entity.layer.ScarecrowParrotOnShoulderLayer;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.entity.state.ArmedEntityRenderState;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.CustomHeadLayer;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;

public class ScarecrowRender extends LivingEntityRenderer<ScarecrowEntity, ScarecrowRenderState, ScarecrowModel> {
    public static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/entity/scarecrow.png");

    public ScarecrowRender(EntityRendererProvider.Context context) {
        super(context, new ScarecrowModel(context.bakeLayer(ScarecrowModel.LAYER_LOCATION)), 0);
        this.addLayer(new ScarecrowHandLayer(this));
        this.addLayer(new CustomHeadLayer<>(this, context.getModelSet(), context.getPlayerSkinRenderCache()));
        this.addLayer(new ScarecrowParrotOnShoulderLayer(this, context.getModelSet()));
    }

    @Override
    public ScarecrowRenderState createRenderState() {
        return new ScarecrowRenderState();
    }

    @Override
    public void extractRenderState(ScarecrowEntity scarecrow, ScarecrowRenderState state, float partialTick) {
        super.extractRenderState(scarecrow, state, partialTick);
        ArmedEntityRenderState.extractArmedEntityRenderState(scarecrow, state, this.itemModelResolver, partialTick);
        state.timeSinceHit = (float) (scarecrow.level().getGameTime() - scarecrow.lastHit) + partialTick;
        state.shoulderEntity = scarecrow.getShoulderEntity().copy();
    }

    @Override
    protected void setupRotations(ScarecrowRenderState state, PoseStack poseStack, float bodyRot, float scale) {
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - bodyRot));
        float time = state.timeSinceHit;
        if (time < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(time / 1.5F * Mth.PI) * 3.0F));
        }
    }

    @Override
    protected boolean shouldShowName(ScarecrowEntity scarecrow, double distance) {
        return distance < 4096 && scarecrow.isCustomNameVisible();
    }

    @Override
    public Identifier getTextureLocation(ScarecrowRenderState state) {
        return TEXTURE;
    }
}
