package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.client.event.BaoziThrowClientEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.FlatulenceClientEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.PotOverlayEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.event.TrashCanOverlayEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.ColdCutHamSlicesModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.MillstoneModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TeapotModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.model.TrashCanModel;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.ChairBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.ChoppingBoardBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.FoodBiteThreeByThreeBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.FruitBasketBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.KitchenwareRacksBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.MillstoneBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.PotBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.RecipeBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.ShawarmaSpitBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.SteamerBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.StockpotBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.TableBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.TeapotBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.block.TrashCanBlockEntityRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.render.item.StrawHatArmorRenderer;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.ItemRenderReplacerReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import net.fabricmc.fabric.api.client.rendering.v1.ArmorRenderer;
import net.fabricmc.fabric.api.client.rendering.v1.ModelLayerRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.server.packs.PackType;

public final class ClientRegistry {
    private ClientRegistry() {
    }

    public static void init() {
        registerArmorRenderers();
        registerClientEvents();
        registerBlockEntityRenderers();
        registerModelLayers();
        registerResourceReloadListeners();
    }

    private static void registerArmorRenderers() {
        ArmorRenderer.register(new StrawHatArmorRenderer(), ModItems.STRAW_HAT, ModItems.STRAW_HAT_FLOWER);
    }

    private static void registerClientEvents() {
        FlatulenceClientEvent.register();
        BaoziThrowClientEvent.register();
        PotOverlayEvent.register();
        TrashCanOverlayEvent.register();
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
        BlockEntityRenderers.register(ModBlocks.TEAPOT_BE, TeapotBlockEntityRender::new);
        BlockEntityRenderers.register(ModBlocks.TRASH_CAN_BE, TrashCanBlockEntityRender::new);
    }

    private static void registerModelLayers() {
        ModelLayerRegistry.registerModelLayer(MillstoneModel.LAYER_LOCATION, MillstoneModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(ColdCutHamSlicesModel.LAYER_LOCATION, ColdCutHamSlicesModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(TeapotModel.LAYER_LOCATION, TeapotModel::createBodyLayer);
        ModelLayerRegistry.registerModelLayer(TrashCanModel.LAYER_LOCATION, TrashCanModel::createBodyLayer);
    }

    private static void registerResourceReloadListeners() {
        ResourceLoader.get(PackType.CLIENT_RESOURCES)
                .registerReloadListener(ItemRenderReplacerReloadListener.ID, new ItemRenderReplacerReloadListener());
    }
}
