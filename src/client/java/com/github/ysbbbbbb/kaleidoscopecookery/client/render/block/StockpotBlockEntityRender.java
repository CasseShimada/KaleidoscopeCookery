package com.github.ysbbbbbb.kaleidoscopecookery.client.render.block;

import com.github.ysbbbbbb.kaleidoscopecookery.api.client.render.ISoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase.FluidSoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase.MobSoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.soupbase.SimpleSoupBaseRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.ItemRenderReplacerReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotVisuals;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.FluidSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.MobSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SimpleSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.util.Util;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.client.renderer.state.level.CameraRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public class StockpotBlockEntityRender implements BlockEntityRenderer<StockpotBlockEntity, StockpotBlockEntityRender.RenderState> {
    private final ItemModelResolver itemModelResolver;
    private final Function<Identifier, ISoupBaseRender> soupBaseRender;

    public StockpotBlockEntityRender(BlockEntityRendererProvider.Context context) {
        this.itemModelResolver = context.itemModelResolver();
        this.soupBaseRender = Util.memoize(id -> {
            ISoupBase soupBase = SoupBaseManager.getSoupBase(id);
            if (soupBase instanceof MobSoupBase mobSoupBase) {
                return new MobSoupBaseRender(mobSoupBase.getFluid(), mobSoupBase.getEntityType());
            }
            if (soupBase instanceof FluidSoupBase fluidSoupBase) {
                return new FluidSoupBaseRender(fluidSoupBase.getFluid());
            }
            if (soupBase instanceof SimpleSoupBase simpleSoupBase) {
                return new SimpleSoupBaseRender(simpleSoupBase.getSoupBaseTexture());
            }
            return null;
        });
    }

    @Override
    public RenderState createRenderState() {
        return new RenderState();
    }

    @Override
    public void extractRenderState(StockpotBlockEntity stockpot, RenderState state, float partialTick,
                                   net.minecraft.world.phys.Vec3 cameraPos,
                                   net.minecraft.client.renderer.feature.ModelFeatureRenderer.CrumblingOverlay crumblingOverlay) {
        BlockEntityRenderState.extractBase(stockpot, state, crumblingOverlay);
        state.stockpot = stockpot;
        state.partialTick = partialTick;
        state.hasLid = stockpot.getBlockState().getValue(StockpotBlock.HAS_LID);
        state.status = stockpot.getStatus();
        state.soupBase = this.soupBaseRender.apply(stockpot.getSoupBaseId());
        state.cookingTexture = null;
        state.finishedTexture = null;
        state.soupHeight = 0.38f;
        state.renderCount = 0;
        state.randomSeed = stockpot.getBlockPos().asLong();
        StockpotVisuals visuals = stockpot.getVisuals();

        if (state.status == StockpotBlockEntity.FINISHED) {
            int takeoutCount = stockpot.getTakeoutCount();
            int maxCount = Math.min(stockpot.getResult().getCount(), StockpotBlockEntity.MAX_TAKEOUT_COUNT);
            state.soupHeight = 0.065f + 0.315f / maxCount * takeoutCount;
            state.finishedTexture = visuals.finishedTexture();
            return;
        }

        if (state.status == StockpotBlockEntity.COOKING) {
            state.cookingTexture = visuals.cookingTexture();
        }

        NonNullList<ItemStack> items = stockpot.getInputs();
        boolean useFinishedModels = state.status == StockpotBlockEntity.COOKING;
        state.renderCount = Math.min(items.size(), state.items.length);
        for (int i = 0; i < state.renderCount; i++) {
            ItemStack stack = items.get(i);
            state.items[i] = stack;
            state.itemStates[i].clear();
            if (!stack.isEmpty()) {
                if (useFinishedModels) {
                    ItemRenderReplacerReloadListener.updateStockpotFinishedRenderState(itemModelResolver, state.itemStates[i],
                            stack, ItemDisplayContext.FIXED, stockpot.getLevel(), 0);
                } else {
                    ItemRenderReplacerReloadListener.updateStockpotCookingRenderState(itemModelResolver, state.itemStates[i],
                            stack, ItemDisplayContext.FIXED, stockpot.getLevel(), 0);
                }
            }
        }
    }

    @Override
    public void submit(RenderState state, PoseStack poseStack, SubmitNodeCollector collector, CameraRenderState cameraState) {
        if (state.hasLid || state.soupBase == null || state.stockpot == null) {
            return;
        }

        if (state.status == StockpotBlockEntity.PUT_INGREDIENT) {
            state.soupBase.renderWhenPutIngredient(state.stockpot, state.partialTick, poseStack, collector, cameraState, state.lightCoords, OverlayTexture.NO_OVERLAY, state.soupHeight);
            renderItems(state, poseStack, collector);
        } else if (state.status == StockpotBlockEntity.COOKING) {
            state.soupBase.renderWhenCooking(state.stockpot, state.partialTick, poseStack, collector, cameraState, state.lightCoords, OverlayTexture.NO_OVERLAY, state.cookingTexture, state.soupHeight);
            renderItems(state, poseStack, collector);
        } else if (state.status == StockpotBlockEntity.FINISHED) {
            state.soupBase.renderWhenFinished(state.stockpot, state.partialTick, poseStack, collector, cameraState, state.lightCoords, OverlayTexture.NO_OVERLAY, state.finishedTexture, state.soupHeight);
        }
    }

    private void renderItems(RenderState state, PoseStack poseStack, SubmitNodeCollector collector) {
        for (int i = 0; i < state.renderCount; i++) {
            ItemStack stack = state.items[i];
            if (stack.isEmpty()) {
                continue;
            }
            int random = Long.hashCode(state.randomSeed ^ (long) i * 31L ^ ItemStack.hashItemAndComponents(stack));
            long time = random + Util.getMillis();
            float offsetX = (random % 100) * 0.002f;
            float offsetZ = (float) (Math.sin(time * 0.0005) * 0.2);
            float offsetY = random % 50 * 0.004f;
            float yRot = (random % 2 == 0 ? -1 : 1) * 20 + random % 10;

            poseStack.pushPose();
            poseStack.mulPose(Axis.XP.rotationDegrees(85 + random % 10));
            poseStack.scale(0.5f, 0.5f, 0.5f);
            poseStack.translate(0.9 + offsetX, 0.9 + offsetY, -0.5 + offsetZ);
            poseStack.mulPose(Axis.YP.rotationDegrees(yRot));
            poseStack.mulPose(Axis.ZP.rotationDegrees(random % 360));
            state.itemStates[i].submit(poseStack, collector, state.lightCoords, OverlayTexture.NO_OVERLAY, 0);
            poseStack.popPose();
        }
    }

    public static class RenderState extends BlockEntityRenderState {
        final ItemStackRenderState[] itemStates = new ItemStackRenderState[9];
        final ItemStack[] items = new ItemStack[9];
        StockpotBlockEntity stockpot;
        float partialTick;
        boolean hasLid;
        int status;
        ISoupBaseRender soupBase;
        Identifier cookingTexture;
        Identifier finishedTexture;
        float soupHeight;
        int renderCount;
        long randomSeed;

        public RenderState() {
            for (int i = 0; i < itemStates.length; i++) {
                itemStates[i] = new ItemStackRenderState();
                items[i] = ItemStack.EMPTY;
            }
        }
    }
}
