package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.client.event.FlatulenceEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.PotOverlayEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ColdCutHamSlicesModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.MillstoneModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.*;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.item.StrawHatArmorRenderer;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.ItemRenderReplacerReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.renderer.rendertype.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.packs.PackType;

public class ClientRegistry {
    public static void init() {
        // 注册盔甲渲染器
        ArmorRenderer.register(new StrawHatArmorRenderer(), ModItems.STRAW_HAT, ModItems.STRAW_HAT_FLOWER);

        registerClientEvents();
        registerBlockEntityRenderers();
        registerResourceReloadListeners();
    }

    private static void registerClientEvents() {
        FlatulenceEvent.register();
        PotOverlayEvent.register();
    }

    private static void registerBlockEntityRenderers() {
        BlockEntityRenderers.register(ModBlocks.POT_BE, PotBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.FRUIT_BASKET_BE, FruitBasketBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.CHOPPING_BOARD_BE, ChoppingBoardBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.STOCKPOT_BE, StockpotBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.KITCHENWARE_RACKS_BE, KitchenwareRacksBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.CHAIR_BE, ChairBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.TABLE_BE, TableBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.SHAWARMA_SPIT_BE, ShawarmaSpitBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.MILLSTONE_BE, MillstoneBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.RECIPE_BLOCK_BE, RecipeBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.STEAMER_BE, SteamerBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.FOOD_BITE_THREE_BY_THREE_BE, FoodBiteThreeByThreeBlockEntityRender::new);

        EntityModelLayerRegistry.registerModelLayer(MillstoneModel.LAYER_LOCATION, MillstoneModel::createBodyLayer);
        EntityModelLayerRegistry.registerModelLayer(ColdCutHamSlicesModel.LAYER_LOCATION, ColdCutHamSlicesModel::createBodyLayer);
    }

    private static void registerResourceReloadListeners() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
                .registerReloader(ItemRenderReplacerReloadListener.ID, new ItemRenderReplacerReloadListener());
    }
}
