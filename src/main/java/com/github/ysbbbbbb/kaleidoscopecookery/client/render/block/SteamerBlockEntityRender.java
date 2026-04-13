package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
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
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.Map;

public class SteamerBlockEntityRender implements BlockEntityRenderer<SteamerBlockEntity, SteamerBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public SteamerBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(SteamerBlockEntity steamer, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(steamer, state, crumblingOverlay);
        state.hasLid = steamer.getBlockState().getValue(SteamerBlock.HAS_LID);
        NonNullList<ItemStack> items = steamer.getItems();
        Map<Identifier, Identifier> map = ItemRenderReplacerReloadListener.INSTANCE.steamer();
        for (int i = 0; i < state.items.length; i++) {
            ItemStack stack = items.get(i);
            state.items[i] = stack;
            state.customModel[i] = false;
            state.itemStates[i].clear();
            if (!stack.isEmpty()) {
                Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
                state.customModel[i] = map.containsKey(key);
                ItemRenderReplacer.updateRenderState(itemModelResolver, state.itemStates[i], stack, ItemDisplayContext.FIXED, steamer.getLevel(), 0, map);
            }
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        // 盖住了就不渲染
        if (state.hasLid) {
            return;
        }
        renderItems(state, poseStack, collector);
    }

    private void renderItems(RenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        for (int i = 0; i < state.items.length; i++) {
            ItemStack stack = state.items[i];
            if (stack.isEmpty()) {
                continue;
            }
            boolean hasCustom = state.customModel[i];

            double x = (i % 2) * 0.3 + 0.35;
            double y = (i / 4) * 0.5 + 0.25 + (i % 4) * 0.01;
            double z = ((i / 2) % 2) * 0.3 + 0.35;
            poseStack.pushPose();
            poseStack.translate(x, y, z);
            if (!hasCustom) {
                poseStack.mulPose(Axis.XN.rotationDegrees(90));
            } else {
                poseStack.translate(0, 0.4375, 0.4375);
            }
            poseStack.scale(0.5F, 0.5F, 0.5F);
            state.itemStates[i].submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState[] itemStates = new ItemStackRenderState[8];
        final ItemStack[] items = new ItemStack[8];
        final boolean[] customModel = new boolean[8];
        boolean hasLid;

        public RenderState() {
            for (int i = 0; i < itemStates.length; i++) {
                itemStates[i] = new ItemStackRenderState();
                items[i] = ItemStack.EMPTY;
                customModel[i] = false;
            }
        }
    }
}
