package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.MillstoneModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.ItemRenderReplacerReloadListener;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

public class MillstoneBlockEntityRender implements BlockEntityRenderer<MillstoneBlockEntity, MillstoneBlockEntityRender.RenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/block/millstone.png");

    private final MillstoneModel bodyModel;
    private final ItemModelResolver itemModelResolver;

    public MillstoneBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.bodyModel = new MillstoneModel(context.bakeLayer(MillstoneModel.LAYER_LOCATION));
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(MillstoneBlockEntity millstone, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(millstone, state, crumblingOverlay);
        Level level = millstone.getLevel();
        if (level == null) {
            return;
        }
        Direction facing = millstone.getBlockState().getValue(MillstoneBlock.FACING);
        int facingDeg = facing.get2DDataValue() * 90;

        if (millstone.hasEntity()) {
            float rot = facingDeg + millstone.getRotation(level, partialTick);
            state.wheelYRot = -rot * Mth.DEG_TO_RAD;
            state.rollZRot = rot * Mth.DEG_TO_RAD;
            state.rotStickXRot = -millstone.getLiftAngle() * Mth.DEG_TO_RAD;
        } else {
            float rot = facingDeg + millstone.getCacheRot();
            state.wheelYRot = -rot * Mth.DEG_TO_RAD;
            state.rollZRot = 0;
            state.rotStickXRot = 0;
        }
        state.facingDeg = facingDeg;

        ItemStack renderItem = millstone.getOutput().isEmpty() ? millstone.getInput() : millstone.getOutput();
        state.renderItem = renderItem;
        state.renderCount = Math.min(renderItem.getCount(), MillstoneBlockEntity.MAX_INPUT_COUNT);
        state.itemState.clear();
        if (!renderItem.isEmpty()) {
            ItemRenderReplacerReloadListener.updateMillstoneRenderState(itemModelResolver, state.itemState, renderItem,
                    ItemDisplayContext.FIXED, millstone.getLevel(), 0);
        }
        state.randomSeed = millstone.hashCode();
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        renderBody(state, poseStack, collector);
        if (!state.renderItem.isEmpty()) {
            renderItems(state, poseStack, collector);
        }
    }

    private void renderBody(RenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        bodyModel.getWheel().yRot = state.wheelYRot;
        bodyModel.getRoll().zRot = state.rollZRot;
        bodyModel.getRotStick().xRot = state.rotStickXRot;

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facingDeg));
        collector.submitModel(bodyModel, null, poseStack, RenderTypes.entityCutout(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();

        bodyModel.getWheel().yRot = 0;
        bodyModel.getRoll().zRot = 0;
        bodyModel.getRotStick().xRot = 0;
    }

    private void renderItems(RenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        RandomSource source = RandomSource.create(state.randomSeed);
        for (int i = 0; i < state.renderCount; i++) {
            poseStack.pushPose();
            poseStack.translate(0, 0.875, 0);
            poseStack.rotateAround(Axis.YP.rotationDegrees(i * 45 + source.nextInt(15)), 0.5f, 0, 0.5f);
            poseStack.mulPose(Axis.YP.rotationDegrees(source.nextInt(20)));
            poseStack.mulPose(Axis.XN.rotationDegrees(80 + source.nextInt(20)));
            poseStack.scale(0.65F, 0.65F, 0.65F);
            state.itemState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    @Override
    public boolean shouldRenderOffScreen() {
        return true;
    }


    public AABB getRenderBoundingBox(MillstoneBlockEntity blockEntity) {
        BlockPos pos = blockEntity.getBlockPos();
        return getAABB(pos.offset(-3, 0, -3), pos.offset(3, 1, 3));
    }

    private static AABB getAABB(BlockPos pStart, BlockPos pEnd) {
        return new AABB(pStart.getX(), pStart.getY(), pStart.getZ(), pEnd.getX(), pEnd.getY(), pEnd.getZ());
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState itemState = new ItemStackRenderState();
        ItemStack renderItem = ItemStack.EMPTY;
        int renderCount;
        int facingDeg;
        float wheelYRot;
        float rollZRot;
        float rotStickXRot;
        int randomSeed;
    }
}
