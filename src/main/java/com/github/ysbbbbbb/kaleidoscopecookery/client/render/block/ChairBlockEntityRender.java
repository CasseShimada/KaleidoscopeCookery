package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.ChairBlockEntity;
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
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;

import java.util.function.Function;

public class ChairBlockEntityRender implements BlockEntityRenderer<ChairBlockEntity, ChairBlockEntityRender.RenderState> {
    private static final Function<DyeColor, Identifier> CACHE_MODEL = Util.memoize(color ->
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "block/carpet/chair/" + color.getName()));

    private final ItemModelResolver itemModelResolver;

    public ChairBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(ChairBlockEntity chair, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(chair, state, crumblingOverlay);
        state.modelId = CACHE_MODEL.apply(chair.getColor());
        state.modelState.clear();
        if (state.modelId != null) {
            if (state.modelStack.isEmpty() || !state.modelId.equals(state.modelStack.get(DataComponents.ITEM_MODEL))) {
                state.modelStack = new ItemStack(Items.STONE);
                state.modelStack.set(DataComponents.ITEM_MODEL, state.modelId);
            }
            itemModelResolver.updateForTopItem(state.modelState, state.modelStack, ItemDisplayContext.NONE, chair.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.modelId == null) {
            return;
        }

        poseStack.pushPose();
        int rotation = state.blockState.getValue(HorizontalDirectionalBlock.FACING).getOpposite().get2DDataValue();
        poseStack.translate(0.5, 0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotation * 90));
        poseStack.translate(-0.5, 0, -0.5);
        poseStack.translate(0.5, 0.5, 0.5);
        state.modelState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState modelState = new ItemStackRenderState();
        ItemStack modelStack = ItemStack.EMPTY;
        Identifier modelId;
    }
}
