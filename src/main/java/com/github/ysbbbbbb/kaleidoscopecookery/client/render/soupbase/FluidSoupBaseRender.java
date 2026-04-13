package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.material.Fluid;

public class FluidSoupBaseRender implements ISoupBaseRender {
    private final Fluid fluid;

    public FluidSoupBaseRender(Fluid fluid) {
        this.fluid = fluid;
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                        SubmitNodeCollector collector, CameraRenderState cameraState, int packedLight, int packedOverlay,
                                        float soupHeight) {
        ISoupBaseRender.renderSurface(getStillFluidSprite(stockpot), getFluidColor(stockpot), poseStack, collector, packedLight, soupHeight);
    }

    @Override
    public void renderWhenCooking(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                  SubmitNodeCollector collector, CameraRenderState cameraState, int packedLight, int packedOverlay,
                                  Identifier cookingTexture, float soupHeight) {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        TextureAtlasSprite sprite = atlas.getSprite(cookingTexture);
        ISoupBaseRender.renderSurface(sprite, 0xFFFFFFFF, poseStack, collector, packedLight, soupHeight);
    }

    @Override
    public void renderWhenFinished(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                   SubmitNodeCollector collector, CameraRenderState cameraState, int packedLight, int packedOverlay,
                                   Identifier finishedTexture, float soupHeight) {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        TextureAtlasSprite sprite = atlas.getSprite(finishedTexture);
        ISoupBaseRender.renderSurface(sprite, 0xFFFFFFFF, poseStack, collector, packedLight, soupHeight);
    }

    private TextureAtlasSprite getStillFluidSprite(StockpotBlockEntity stockpot) {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        return atlas.getSprite(Identifier.fromNamespaceAndPath("minecraft", "block/water_still"));
    }

    private int getFluidColor(StockpotBlockEntity stockpot) {
        return 0xFFFFFFFF;
    }
}
