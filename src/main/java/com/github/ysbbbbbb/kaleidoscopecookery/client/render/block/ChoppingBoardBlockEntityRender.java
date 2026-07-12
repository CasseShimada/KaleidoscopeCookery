package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ChoppingBoardBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

public class ChoppingBoardBlockEntityRender implements BlockEntityRenderer<ChoppingBoardBlockEntity, ChoppingBoardBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public ChoppingBoardBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(ChoppingBoardBlockEntity choppingBoard, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(choppingBoard, state, crumblingOverlay);
        state.blockState = choppingBoard.getBlockState();
        Identifier modelId = choppingBoard.getModelId();
        if (modelId == null) {
            state.modelId = null;
            return;
        }
        if (!modelId.equals(choppingBoard.previousModel)) {
            choppingBoard.previousModel = modelId;
            choppingBoard.cacheModels = new Identifier[choppingBoard.getMaxCutCount() + 1];
            for (int i = 0; i <= choppingBoard.getMaxCutCount(); i++) {
                choppingBoard.cacheModels[i] = Identifier.fromNamespaceAndPath(modelId.getNamespace(), "chopping_board/" + modelId.getPath() + "/" + i);
            }
        }
        if (choppingBoard.cacheModels == null) {
            state.modelId = null;
            return;
        }
        int index = Math.min(choppingBoard.getCurrentCutCount(), choppingBoard.cacheModels.length - 1);
        Identifier cacheModel = choppingBoard.cacheModels[index];
        state.modelId = cacheModel;
        state.modelState.clear();
        if (cacheModel != null) {
            if (state.modelStack.isEmpty() || !cacheModel.equals(state.modelStack.get(DataComponents.ITEM_MODEL))) {
                state.modelStack = new ItemStack(Items.STONE);
                state.modelStack.set(DataComponents.ITEM_MODEL, cacheModel);
            }
            itemModelResolver.updateForTopItem(state.modelState, state.modelStack, ItemDisplayContext.FIXED, choppingBoard.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.modelId == null || state.blockState == null) {
            return;
        }

        poseStack.pushPose();
        int rotation = state.blockState.getValue(ChoppingBoardBlock.FACING).get2DDataValue();
        poseStack.translate(0.5D, 0, 0.5D);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation * 90));
        poseStack.translate(-0.5D, 0.125, -0.5D);
        state.modelState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState modelState = new ItemStackRenderState();
        ItemStack modelStack = ItemStack.EMPTY;
        Identifier modelId;
        BlockState blockState;
    }
}
