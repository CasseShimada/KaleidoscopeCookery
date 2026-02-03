package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiFunction;

public class TableBlockEntityRender implements BlockEntityRenderer<TableBlockEntity, TableBlockEntityRender.RenderState> {
    private static final BiFunction<DyeColor, Integer, Identifier> CACHE_MODEL = Util.memoize((color, position) -> {
        String name = color.getName();
        if (position == TableBlock.SINGLE) {
            return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "block/carpet/table/" + name + "_single");
        }
        if (position == TableBlock.MIDDLE) {
            return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "block/carpet/table/" + name + "_middle");
        }
        if (position == TableBlock.LEFT) {
            return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "block/carpet/table/" + name + "_left");
        }
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "block/carpet/table/" + name + "_right");
    });

    private final ItemModelResolver itemModelResolver;

    public TableBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(TableBlockEntity table, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(table, state, crumblingOverlay);
        BlockState blockState = table.getBlockState();
        state.axis = blockState.getValue(TableBlock.AXIS);
        state.hasCarpet = blockState.getValue(TableBlock.HAS_CARPET);
        if (state.hasCarpet) {
            int position = blockState.getValue(TableBlock.POSITION);
            state.carpetModelId = CACHE_MODEL.apply(table.getColor(), position);
            state.carpetState.clear();
            if (state.carpetModelId != null) {
                if (state.carpetStack.isEmpty() || !state.carpetModelId.equals(state.carpetStack.get(DataComponents.ITEM_MODEL))) {
                    state.carpetStack = new ItemStack(Items.STONE);
                    state.carpetStack.set(DataComponents.ITEM_MODEL, state.carpetModelId);
                }
                itemModelResolver.updateForTopItem(state.carpetState, state.carpetStack, ItemDisplayContext.NONE, table.getLevel(), null, 0);
            }
        }
        NonNullList<ItemStack> items = table.getItems();
        for (int i = 0; i < state.items.length; i++) {
            ItemStack stack = items.get(i);
            state.items[i] = stack;
            state.itemStates[i].clear();
            if (!stack.isEmpty()) {
                itemModelResolver.updateForTopItem(state.itemStates[i], stack, ItemDisplayContext.FIXED, table.getLevel(), null, 0);
            }
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.hasCarpet && state.carpetModelId != null) {
            int rotation = state.axis == Direction.Axis.X ? 180 : 270;
            poseStack.pushPose();
            poseStack.translate(0.5, 0, 0.5);
            poseStack.mulPose(Axis.YP.rotationDegrees(-rotation));
            poseStack.translate(-0.5, 0, -0.5);
            poseStack.translate(0.5, 0.5, 0.5);
            state.carpetState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }

        int count = 0;
        for (ItemStack stack : state.items) {
            if (!stack.isEmpty()) {
                count++;
            }
        }

        if (count == 0) {
            return;
        }

        poseStack.pushPose();
        poseStack.translate(0.5, 1.3125, 0.5);
        poseStack.scale(0.65F, 0.65F, 0.65F);

        if (count == 1) {
            this.rotation(poseStack, state.axis);
            submitItem(state.itemStates[0], poseStack, collector, state.lightCoords);
        } else if (count == 2) {
            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(-0.25, 0, 0.1);
            submitItem(state.itemStates[0], poseStack, collector, state.lightCoords);
            poseStack.popPose();

            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(0.25, 0.01, -0.1);
            submitItem(state.itemStates[1], poseStack, collector, state.lightCoords);
            poseStack.popPose();
        } else if (count == 3) {
            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(0.25, 0, -0.2);
            submitItem(state.itemStates[0], poseStack, collector, state.lightCoords);
            poseStack.popPose();

            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(-0.25, 0.01, 0);
            submitItem(state.itemStates[1], poseStack, collector, state.lightCoords);
            poseStack.popPose();

            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(0.24, 0.02, 0.2);
            submitItem(state.itemStates[2], poseStack, collector, state.lightCoords);
            poseStack.popPose();
        } else {
            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(0.25, 0, -0.3);
            submitItem(state.itemStates[0], poseStack, collector, state.lightCoords);
            poseStack.popPose();

            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(-0.24, 0.01, -0.1);
            submitItem(state.itemStates[1], poseStack, collector, state.lightCoords);
            poseStack.popPose();

            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(0.24, 0.02, 0.1);
            submitItem(state.itemStates[2], poseStack, collector, state.lightCoords);
            poseStack.popPose();

            poseStack.pushPose();
            this.rotation(poseStack, state.axis);
            poseStack.translate(-0.25, 0.03, 0.3);
            submitItem(state.itemStates[3], poseStack, collector, state.lightCoords);
            poseStack.popPose();
        }

        poseStack.popPose();
    }

    private void rotation(PoseStack poseStack, Direction.Axis axis) {
        if (axis == Direction.Axis.X) {
            poseStack.mulPose(Axis.YP.rotationDegrees(180));
        } else {
            poseStack.mulPose(Axis.YP.rotationDegrees(90));
        }
    }

    private static void submitItem(ItemStackRenderState itemState, PoseStack poseStack, SubmitNodeCollector collector, int packedLight) {
        if (!itemState.isEmpty()) {
            itemState.submit(poseStack, collector, packedLight, OverlayTexture.NO_OVERLAY, 0);
        }
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState carpetState = new ItemStackRenderState();
        final ItemStackRenderState[] itemStates = new ItemStackRenderState[4];
        final ItemStack[] items = new ItemStack[4];
        ItemStack carpetStack = ItemStack.EMPTY;
        Identifier carpetModelId;
        Direction.Axis axis = Direction.Axis.X;
        boolean hasCarpet;

        public RenderState() {
            for (int i = 0; i < itemStates.length; i++) {
                itemStates[i] = new ItemStackRenderState();
                items[i] = ItemStack.EMPTY;
            }
        }
    }
}
