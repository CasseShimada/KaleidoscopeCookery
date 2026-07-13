package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

public class ShawarmaSpitBlockEntityRender implements BlockEntityRenderer<ShawarmaSpitBlockEntity, ShawarmaSpitBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public ShawarmaSpitBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(ShawarmaSpitBlockEntity shawarmaSpit, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(shawarmaSpit, state, crumblingOverlay);
        ItemStack renderItem = shawarmaSpit.getStoredItem();
        if (renderItem.isEmpty()) {
            state.itemCount = 0;
            return;
        }
        BlockState blockState = shawarmaSpit.getBlockState();
        state.powered = blockState.getValue(BlockStateProperties.POWERED);
        state.half = blockState.getValue(ShawarmaSpitBlock.HALF);
        state.itemCount = renderItem.getCount();
        state.renderItem = renderItem;
        state.itemState.clear();
        if (!renderItem.isEmpty()) {
            itemModelResolver.updateForTopItem(state.itemState, renderItem, ItemDisplayContext.FIXED, shawarmaSpit.getLevel(), null, 0);
        }

        // 如果是充能状态，那么一直旋转
        state.spin = state.powered ? (System.currentTimeMillis() % 3600L) / 10f : 0f;
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.itemCount <= 0) {
            return;
        }
        if (state.powered) {
            poseStack.rotateAround(Axis.YP.rotationDegrees(state.spin), 0.5f, 0, 0.5f);
        }

        // 如果是上半部分
        if (state.half == DoubleBlockHalf.UPPER) {
            poseStack.translate(0.25, 0.5, 0.25);
            this.renderItems(state, poseStack, collector);
        }

        // 如果是下半部分
        else if (state.half == DoubleBlockHalf.LOWER) {
            poseStack.translate(0.25, 0.875, 0.25);
            this.renderItems(state, poseStack, collector);
        }
    }

    private void renderItems(RenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        for (int i = 0; i < state.itemCount; i++) {
            poseStack.pushPose();
            poseStack.rotateAround(Axis.YP.rotationDegrees(i * 45), 0.25f, 0, 0.25f);
            poseStack.scale(0.65F, 0.65F, 0.65F);
            state.itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState itemState = new ItemStackRenderState();
        ItemStack renderItem = ItemStack.EMPTY;
        int itemCount;
        boolean powered;
        float spin;
        DoubleBlockHalf half = DoubleBlockHalf.UPPER;
    }
}
