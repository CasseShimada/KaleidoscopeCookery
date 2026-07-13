package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
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
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.properties.AttachFace;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class RecipeBlockEntityRender implements BlockEntityRenderer<RecipeBlockEntity, RecipeBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;

    public RecipeBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(RecipeBlockEntity recipeBlock, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(recipeBlock, state, crumblingOverlay);
        ItemStack stack = recipeBlock.getItem();
        if (stack.isEmpty()) {
            state.output = ItemStack.EMPTY;
            return;
        }
        RecipeItem.RecipeRecord record = stack.get(ModDataComponents.RECIPE_RECORD);
        if (record == null) {
            state.output = ItemStack.EMPTY;
            return;
        }

        state.output = record.output();
        state.facing = recipeBlock.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        state.attachFace = recipeBlock.getBlockState().getValue(BlockStateProperties.ATTACH_FACE);
        state.outputState.clear();
        if (!state.output.isEmpty()) {
            itemModelResolver.updateForTopItem(state.outputState, state.output, ItemDisplayContext.FIXED, recipeBlock.getLevel(), null, 0);
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.output.isEmpty()) {
            return;
        }

        int rotationX = state.attachFace.ordinal();
        int rotationY = state.facing.get2DDataValue() + (state.attachFace == AttachFace.CEILING ? 2 : 0);

        poseStack.pushPose();
        poseStack.translate(0.5, 0.5, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(-rotationY * 90));
        poseStack.mulPose(Axis.XP.rotationDegrees(90 - rotationX * 90));
        poseStack.translate(-0.5, -0.5, -0.5);
        poseStack.scale(0.5f, 0.5f, 0.5f);

        if (state.attachFace == AttachFace.WALL) {
            poseStack.translate(1, 1.25, 0);
        } else {
            poseStack.translate(1, 0.75, 2);
        }

        state.outputState.submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
        poseStack.popPose();
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState outputState = new ItemStackRenderState();
        ItemStack output = ItemStack.EMPTY;
        Direction facing = Direction.NORTH;
        AttachFace attachFace = AttachFace.WALL;
    }
}
