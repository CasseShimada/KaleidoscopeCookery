package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class FruitBasketBlockEntityRender implements BlockEntityRenderer<FruitBasketBlockEntity, FruitBasketBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public FruitBasketBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(FruitBasketBlockEntity basket, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(basket, state, crumblingOverlay);
        state.rotation = basket.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90;
        NonNullList<ItemStack> items = basket.getItems();
        for (int i = 0; i < state.items.length; i++) {
            ItemStack stack = items.get(i);
            state.items[i] = stack;
            state.itemStates[i].clear();
            if (!stack.isEmpty()) {
                itemModelResolver.updateForTopItem(state.itemStates[i], stack, ItemDisplayContext.FIXED, basket.getLevel(), null, 0);
            }
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.pushPose();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(state.rotation));
        poseStack.translate(-0.5, 0, -0.5);
        poseStack.translate(0.1, 0.3, 0.35);
        for (int i = 0; i < 2; i++) {
            poseStack.pushPose();
            for (int j = 0; j < 4; j++) {
                int index = i * 4 + j;
                ItemStack itemStack = state.items[index];
                if (!itemStack.isEmpty()) {
                    poseStack.translate(0.15, 0, 0);
                    poseStack.pushPose();
                    poseStack.translate(0, 0, index % 2 == 0 ? -0.01f : 0.01f);
                    poseStack.mulPose(Axis.YN.rotationDegrees(90));
                    poseStack.mulPose(Axis.XN.rotationDegrees(-30));
                    poseStack.scale(0.375f, 0.375f, 0.375f);
                    state.itemStates[index].submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
                    poseStack.popPose();
                }
            }
            poseStack.popPose();
            poseStack.translate(0, 0, 0.32);
        }
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState[] itemStates = new ItemStackRenderState[8];
        final ItemStack[] items = new ItemStack[8];
        int rotation;

        public RenderState() {
            for (int i = 0; i < itemStates.length; i++) {
                itemStates[i] = new ItemStackRenderState();
                items[i] = ItemStack.EMPTY;
            }
        }
    }
}
