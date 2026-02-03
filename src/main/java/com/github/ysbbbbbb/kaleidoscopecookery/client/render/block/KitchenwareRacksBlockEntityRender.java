package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenwareRacksBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class KitchenwareRacksBlockEntityRender implements BlockEntityRenderer<KitchenwareRacksBlockEntity, KitchenwareRacksBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public KitchenwareRacksBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(KitchenwareRacksBlockEntity blockEntity, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        state.rotation = blockEntity.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90;
        state.itemLeft = blockEntity.getItemLeft();
        state.itemRight = blockEntity.getItemRight();
        updateItemState(state.leftState, state.itemLeft, blockEntity);
        updateItemState(state.rightState, state.itemRight, blockEntity);
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(state.rotation));

        if (!state.itemLeft.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(-0.2, 0.4375, -0.3);
            renderItem(poseStack, collector, state.leftState, state.lightCoords);
            poseStack.popPose();
        }

        if (!state.itemRight.isEmpty()) {
            poseStack.pushPose();
            poseStack.translate(0.2, 0.4375, -0.3);
            renderItem(poseStack, collector, state.rightState, state.lightCoords);
            poseStack.popPose();
        }
    }

    private void updateItemState(ItemStackRenderState renderState, ItemStack stack, KitchenwareRacksBlockEntity blockEntity) {
        renderState.clear();
        if (!stack.isEmpty()) {
            itemModelResolver.updateForTopItem(renderState, stack, ItemDisplayContext.FIXED, blockEntity.getLevel(), null, 0);
        }
    }

    private void renderItem(PoseStack poseStack, SubmitNodeCollector collector, ItemStackRenderState renderState, int packedLight) {
        poseStack.scale(0.75f, 0.75f, 0.75f);
        poseStack.mulPose(Axis.XN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(-25));
        poseStack.mulPose(Axis.ZN.rotationDegrees(45));
        renderState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState leftState = new ItemStackRenderState();
        final ItemStackRenderState rightState = new ItemStackRenderState();
        ItemStack itemLeft = ItemStack.EMPTY;
        ItemStack itemRight = ItemStack.EMPTY;
        int rotation;
    }
}
