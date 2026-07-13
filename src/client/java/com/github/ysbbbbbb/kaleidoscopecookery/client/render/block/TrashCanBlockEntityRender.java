package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.TrashCanBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.animation.TrashCanAnimation;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TrashCanModel;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
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
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.phys.AABB;

import java.util.Map;
import java.util.WeakHashMap;

public class TrashCanBlockEntityRender implements BlockEntityRenderer<TrashCanBlockEntity, TrashCanBlockEntityRender.RenderState> {
    private static final Identifier TEXTURE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/block/trash_can.png");

    private final TrashCanModel model;
    private final KeyframeAnimation putAnimation;
    private final KeyframeAnimation withdrawAnimation;
    private final KeyframeAnimation player1Animation;
    private final KeyframeAnimation player2Animation;
    private final KeyframeAnimation enterAnimation;
    private final Map<TrashCanBlockEntity, AnimationData> animationStates = new WeakHashMap<>();

    public TrashCanBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.model = new TrashCanModel(context.bakeLayer(TrashCanModel.LAYER_LOCATION));
        this.putAnimation = TrashCanAnimation.PUT.bake(this.model.root());
        this.withdrawAnimation = TrashCanAnimation.WITHDRAW.bake(this.model.root());
        this.player1Animation = TrashCanAnimation.PLAYER1.bake(this.model.root());
        this.player2Animation = TrashCanAnimation.PLAYER2.bake(this.model.root());
        this.enterAnimation = TrashCanAnimation.ENTER.bake(this.model.root());
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(TrashCanBlockEntity trashCan, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(trashCan, state, crumblingOverlay);
        Direction facing = trashCan.getBlockState().getValue(TrashCanBlock.FACING);
        state.facingDeg = facing.get2DDataValue() * 90;
        state.ageInTicks = trashCan.getLevel() == null ? partialTick : trashCan.getLevel().getGameTime() + partialTick;
        AnimationData animations = this.animationStates.computeIfAbsent(trashCan, ignored -> new AnimationData());
        syncEventAnimation(animations.putState, trashCan.getPutAnimationStartTick());
        syncEventAnimation(animations.withdrawState, trashCan.getWithdrawAnimationStartTick());
        syncEventAnimation(animations.enterState, trashCan.getEnterAnimationStartTick());
        animations.updatePlayerAnimation(trashCan);
        state.putState.copyFrom(animations.putState);
        state.withdrawState.copyFrom(animations.withdrawState);
        state.player1State.copyFrom(animations.player1State);
        state.player2State.copyFrom(animations.player2State);
        state.enterState.copyFrom(animations.enterState);
    }

    private static void syncEventAnimation(AnimationState animationState, int startTick) {
        if (startTick == Integer.MIN_VALUE) {
            animationState.stop();
        } else {
            animationState.start(startTick);
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        this.model.resetPose();
        this.putAnimation.apply(state.putState, state.ageInTicks);
        this.withdrawAnimation.apply(state.withdrawState, state.ageInTicks);
        this.player1Animation.apply(state.player1State, state.ageInTicks);
        this.player2Animation.apply(state.player2State, state.ageInTicks);
        this.enterAnimation.apply(state.enterState, state.ageInTicks);

        poseStack.pushPose();
        poseStack.translate(0.5, 1.5, 0.5);
        poseStack.mulPose(Axis.ZN.rotationDegrees(180));
        poseStack.mulPose(Axis.YN.rotationDegrees(180 - state.facingDeg));
        collector.submitModel(this.model, null, poseStack, this.model.renderType(TEXTURE),
                state.lightCoords, OverlayTexture.NO_OVERLAY, 0, state.breakProgress);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        final AnimationState putState = new AnimationState();
        final AnimationState withdrawState = new AnimationState();
        final AnimationState player1State = new AnimationState();
        final AnimationState player2State = new AnimationState();
        final AnimationState enterState = new AnimationState();
        int facingDeg;
        float ageInTicks;
    }

    private static class AnimationData {
        final AnimationState putState = new AnimationState();
        final AnimationState withdrawState = new AnimationState();
        final AnimationState player1State = new AnimationState();
        final AnimationState player2State = new AnimationState();
        final AnimationState enterState = new AnimationState();
        long playerAnimationTick = Long.MIN_VALUE;

        void updatePlayerAnimation(TrashCanBlockEntity trashCan) {
            var level = trashCan.getLevel();
            if (level == null || level.getEntitiesOfClass(SitEntity.class, new AABB(trashCan.getBlockPos())).isEmpty()) {
                this.player1State.stop();
                this.player2State.stop();
                this.playerAnimationTick = Long.MIN_VALUE;
                return;
            }
            long gameTime = level.getGameTime();
            long animationTick = gameTime - Math.floorMod(gameTime + trashCan.getBlockPos().hashCode(), 61);
            if (this.playerAnimationTick == animationTick) {
                return;
            }
            this.playerAnimationTick = animationTick;
            if ((Long.hashCode(trashCan.getBlockPos().asLong() ^ animationTick) & 1) == 0) {
                this.player2State.stop();
                this.player1State.start((int) animationTick);
            } else {
                this.player1State.stop();
                this.player2State.start((int) animationTick);
            }
        }
    }
}
