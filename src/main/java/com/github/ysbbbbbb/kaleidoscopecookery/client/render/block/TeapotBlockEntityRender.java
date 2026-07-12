package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.TeapotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.animation.TeapotAnimation;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TeapotModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.client.animation.KeyframeAnimation;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.item.ItemStack;

import java.util.Arrays;

public class TeapotBlockEntityRender implements BlockEntityRenderer<TeapotBlockEntity, TeapotBlockEntityRender.RenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/block/teapot.png");
    private static final String EMPTY_TEXT = "tooltip.kaleidoscope_cookery.empty";

    private final TeapotModel model;
    private final KeyframeAnimation boilingAnimation;

    public TeapotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.model = new TeapotModel(context.bakeLayer(TeapotModel.LAYER_LOCATION));
        this.boilingAnimation = TeapotAnimation.BOILING.bake(this.model.root());
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(TeapotBlockEntity teapot, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(teapot, state, crumblingOverlay);
        Direction facing = teapot.getBlockState().getValue(TeapotBlock.FACING);
        state.facingDeg = facing.get2DDataValue() * 90;
        state.variant = teapot.getBlockState().getValue(TeapotBlock.VARIANT);
        state.ageInTicks = teapot.getLevel() == null ? partialTick : teapot.getLevel().getGameTime() + partialTick;
        state.boilingState.copyFrom(teapot.boilingState);
        state.statusText = teapot.getStatusText();
        state.status = teapot.getStatus();
        state.input = teapot.getInput().copy();
        state.result = teapot.getResult().copy();
        state.teaFluidId = teapot.getTeaFluidId();
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        this.model.resetPose();
        this.model.updateVariant(state.variant);
        this.boilingAnimation.apply(state.boilingState, state.ageInTicks);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facingDeg));
        collector.submitModel(this.model, null, poseStack, this.model.renderType(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        final AnimationState boilingState = new AnimationState();
        int facingDeg;
        int variant;
        float ageInTicks;
        int status;
        Component statusText = Component.empty();
        ItemStack input = ItemStack.EMPTY;
        ItemStack result = ItemStack.EMPTY;
        Identifier teaFluidId;

        public Component getIngredientText() {
            if (this.input.isEmpty()) {
                return Component.translatable(EMPTY_TEXT);
            }
            return ComponentUtils.formatList(Arrays.asList(
                    this.input.getHoverName(),
                    Component.literal("x%d".formatted(this.input.getCount()))
            ), CommonComponents.space());
        }

        public Component getResultText() {
            if (this.result.isEmpty()) {
                return Component.translatable(EMPTY_TEXT);
            }
            return ComponentUtils.formatList(Arrays.asList(
                    this.result.getHoverName(),
                    Component.literal("x%d".formatted(this.result.getCount()))
            ), CommonComponents.space());
        }
    }
}
