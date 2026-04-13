package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.ItemRenderReplacer;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.ItemRenderReplacerReloadListener;
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
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

import java.util.List;
import java.util.Map;

public class PotBlockEntityRender implements BlockEntityRenderer<PotBlockEntity, PotBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public PotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(PotBlockEntity pot, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(pot, state, crumblingOverlay);
        PotBlockEntity.StirFryAnimationData data = pot.animationData;
        long time = System.currentTimeMillis() - data.timestamp;

        if (data.preSeed == -1L) {
            data.preSeed = pot.getSeed();
        }
        if (data.preSeed != pot.getSeed()) {
            data.preSeed = pot.getSeed();
            if (time > 1000) {
                data.timestamp = System.currentTimeMillis();
                data.randomHeights = new float[9];
                RandomSource source = RandomSource.create(pot.getSeed());
                for (int i = 0; i < 9; i++) {
                    data.randomHeights[i] = 0.25f + source.nextFloat() * 1;
                }
            }
        }

        state.seed = pot.getSeed();
        state.time = time;
        state.randomHeights = data.randomHeights;
        state.rotation = pot.getBlockState().getValue(BlockStateProperties.HORIZONTAL_FACING).get2DDataValue() * 90;
        state.showInputs = pot.getStatus() != PotBlockEntity.FINISHED && pot.getStatus() != PotBlockEntity.BURNT;
        state.hasCarrier = pot.hasCarrier();
        state.isBurnt = pot.getStatus() == PotBlockEntity.BURNT;
        state.burntLevel = Mth.clamp(pot.getCurrentTick() / 25, 0, 16);

        Map<net.minecraft.resources.Identifier, net.minecraft.resources.Identifier> modelOverrides = ItemRenderReplacerReloadListener.INSTANCE.pot();
        if (state.showInputs || state.hasCarrier) {
            List<ItemStack> items = pot.getInputs();
            state.renderCount = Math.min(items.size(), state.items.length);
            for (int i = 0; i < state.renderCount; i++) {
                ItemStack item = items.get(i);
                state.items[i] = item;
                state.itemStates[i].clear();
                if (!item.isEmpty()) {
                    ItemRenderReplacer.updateRenderState(itemModelResolver, state.itemStates[i], item, ItemDisplayContext.FIXED, pot.getLevel(), 0, modelOverrides);
                }
            }
        } else {
            state.renderCount = 1;
            ItemStack result = pot.getResult();
            state.items[0] = result;
            state.itemStates[0].clear();
            if (!result.isEmpty()) {
                ItemRenderReplacer.updateRenderState(itemModelResolver, state.itemStates[0], result, ItemDisplayContext.FIXED, pot.getLevel(), 0, modelOverrides);
            }
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        RandomSource source = RandomSource.create(state.seed);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.1, 0.5);
        poseStack.mulPose(Axis.YN.rotationDegrees(state.rotation));
        poseStack.mulPose(Axis.XN.rotationDegrees(90));
        poseStack.scale(0.5f, 0.5f, 0.5f);

        // 炒菜阶段，或者炒完，但是需要碗才能装的菜，只渲染原材料
        for (int i = 0; i < state.renderCount; i++) {
            ItemStack item = state.items[i];
            if (!item.isEmpty()) {
                renderItem(state, poseStack, collector, source, i);
                poseStack.translate(0, 0, 0.025);
            }
        }

        poseStack.popPose();
    }

    private void renderItem(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, RandomSource source, int index) {
        poseStack.pushPose();

        int count = 90 + source.nextInt(90);
        poseStack.mulPose(Axis.ZN.rotationDegrees(index * count));
        if (state.time < 1000 && state.randomHeights != null) {
            poseStack.translate(0, 0, state.randomHeights[index] * Mth.sin(Mth.PI * state.time / 1000f));
            poseStack.mulPose(Axis.XN.rotationDegrees(720f / 1000 * state.time));
        }

        int overlay = OverlayTexture.NO_OVERLAY;
        state.itemStates[index].submit(poseStack, collector, state.lightCoords, overlay, 0);

        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState[] itemStates = new ItemStackRenderState[9];
        final ItemStack[] items = new ItemStack[9];
        long seed;
        long time;
        float[] randomHeights;
        int rotation;
        int renderCount;
        boolean showInputs;
        boolean hasCarrier;
        boolean isBurnt;
        int burntLevel;

        public RenderState() {
            for (int i = 0; i < itemStates.length; i++) {
                itemStates[i] = new ItemStackRenderState();
                items[i] = ItemStack.EMPTY;
            }
        }
    }
}
