package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.level.material.Fluid;

import java.util.Map;
import java.util.WeakHashMap;

public class MobSoupBaseRender extends FluidSoupBaseRender {
    private final EntityType<?> mobType;
    private final Map<StockpotBlockEntity, Entity> renderEntities = new WeakHashMap<>();

    public MobSoupBaseRender(Fluid fluid, EntityType<?> mobType) {
        super(fluid);
        this.mobType = mobType;
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                        SubmitNodeCollector collector, CameraRenderState cameraState, int packedLight, int packedOverlay,
                                        float soupHeight) {
        super.renderWhenPutIngredient(stockpot, partialTick, poseStack, collector, cameraState, packedLight, packedOverlay, soupHeight);
        this.renderInputEntity(stockpot, partialTick, poseStack, collector, cameraState);
    }

    @Override
    public void renderWhenCooking(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                  SubmitNodeCollector collector, CameraRenderState cameraState, int packedLight, int packedOverlay,
                                  Identifier cookingTexture, float soupHeight) {
        super.renderWhenCooking(stockpot, partialTick, poseStack, collector, cameraState, packedLight, packedOverlay, cookingTexture, soupHeight);
        this.renderInputEntity(stockpot, partialTick, poseStack, collector, cameraState);
    }

    private void renderInputEntity(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                   SubmitNodeCollector collector, CameraRenderState cameraState) {
        ClientLevel world = Minecraft.getInstance().level;
        if (world == null) {
            return;
        }
        Entity renderEntity = this.renderEntities.get(stockpot);
        if (renderEntity == null) {
            renderEntity = this.mobType.create(world, EntitySpawnReason.COMMAND);
            if (renderEntity == null) {
                return;
            }
            renderEntity.setOnGround(true);
            this.renderEntities.put(stockpot, renderEntity);
        }

        renderEntity.tickCount = (int) world.getGameTime();
        int random = renderEntity.hashCode();
        float entityY = (float) (Math.sin(random + System.currentTimeMillis() * 0.0005) * 0.25);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(random % 360));
        poseStack.translate(-0.5, -0.5, -0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        var renderState = dispatcher.extractEntity(renderEntity, partialTick);
        dispatcher.submit(renderState, cameraState, 1, 0.375f + entityY, 1, poseStack, collector);
        poseStack.popPose();
    }
}
