package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ColdCutHamSlicesModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.state.BlockState;

public class FoodBiteThreeByThreeBlockEntityRender
        implements BlockEntityRenderer<FoodBiteThreeByThreeBlockEntity, FoodBiteThreeByThreeBlockEntityRender.RenderState> {
    private static final Identifier COLD_CUT_HAM_SLICES_TEXTURE = Identifier.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "textures/entity/cold_cut_ham_slices.png");

    private final ColdCutHamSlicesModel coldCutHamSlicesModel;

    public FoodBiteThreeByThreeBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.coldCutHamSlicesModel = new ColdCutHamSlicesModel(context.bakeLayer(ColdCutHamSlicesModel.LAYER_LOCATION));
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(FoodBiteThreeByThreeBlockEntity blockEntity, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(blockEntity, state, crumblingOverlay);
        BlockState blockState = blockEntity.getBlockState();
        if (!(blockState.getBlock() instanceof FoodBiteThreeByThreeBlock block)) {
            state.valid = false;
            return;
        }
        state.valid = true;
        state.facingDeg = blockState.getValue(FoodBiteBlock.FACING).get2DDataValue() * 90;
        state.bites = blockState.getValue(block.getBites());
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (!state.valid) {
            return;
        }
        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facingDeg));
        collector.submitModel(coldCutHamSlicesModel, state, poseStack,
                RenderTypes.entityCutout(COLD_CUT_HAM_SLICES_TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }

    public static class RenderState extends BlockEntityRenderState implements ColdCutHamSlicesModel.BitesState {
        int facingDeg;
        int bites;
        boolean valid;

        @Override
        public int getBites() {
            return bites;
        }
    }
}
