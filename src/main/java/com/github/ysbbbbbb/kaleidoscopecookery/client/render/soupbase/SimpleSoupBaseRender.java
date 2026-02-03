package com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.data.AtlasIds;
import net.minecraft.resources.Identifier;

public class SimpleSoupBaseRender implements ISoupBaseRender {
    private final Identifier soupBaseTexture;

    public SimpleSoupBaseRender(Identifier soupBaseTexture) {
        this.soupBaseTexture = soupBaseTexture;
    }

    @Override
    public void renderWhenPutIngredient(StockpotBlockEntity stockpot, float partialTick, PoseStack poseStack,
                                        SubmitNodeCollector collector, CameraRenderState cameraState, int packedLight, int packedOverlay,
                                        float soupHeight) {
        ISoupBaseRender.renderSurface(this.getSprite(), 0xFFFFFFFF, poseStack, collector, packedLight, soupHeight);
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

    private TextureAtlasSprite getSprite() {
        TextureAtlas atlas = Minecraft.getInstance().getAtlasManager().getAtlasOrThrow(AtlasIds.BLOCKS);
        return atlas.getSprite(this.soupBaseTexture);
    }
}
