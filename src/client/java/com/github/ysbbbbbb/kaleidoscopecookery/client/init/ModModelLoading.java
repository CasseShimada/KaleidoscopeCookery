package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.model.loading.v1.ExtraModelKey;
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin;
import net.fabricmc.fabric.api.client.model.loading.v1.SimpleUnbakedExtraModel;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.DyeColor;

@Environment(EnvType.CLIENT)
public final class ModModelLoading {
    private static final String MODELS = "models/";
    private static final String MODELS_CHOPPING_BOARD = MODELS + "chopping_board";
    private static final String JSON = ".json";
    private static final String[] TABLE_CARPET_PARTS = {"single", "middle", "left", "right"};

    private ModModelLoading() {
    }

    public static void register() {
        ModelLoadingPlugin.register(context -> {
            ResourceManager resourceManager = Minecraft.getInstance().getResourceManager();
            addResourceModels(context, resourceManager, MODELS_CHOPPING_BOARD);
            addCarpetModels(context);
        });
    }

    private static void addResourceModels(ModelLoadingPlugin.Context context, ResourceManager resourceManager, String path) {
        resourceManager.listResources(path, ModModelLoading::isJsonModel)
                .keySet().stream()
                .map(ModModelLoading::handleModelId)
                .forEach(modelId -> addModel(context, modelId));
    }

    private static void addCarpetModels(ModelLoadingPlugin.Context context) {
        for (DyeColor color : DyeColor.values()) {
            String colorName = color.getName();
            addModel(context, modModel("block/carpet/chair/" + colorName));
            for (String part : TABLE_CARPET_PARTS) {
                addModel(context, modModel("block/carpet/table/" + colorName + "_" + part));
            }
        }
    }

    private static void addModel(ModelLoadingPlugin.Context context, Identifier modelId) {
        context.addModel(ExtraModelKey.create(() -> modelId.toString()), SimpleUnbakedExtraModel.blockStateModel(modelId));
    }

    private static boolean isJsonModel(Identifier id) {
        return id.getPath().endsWith(JSON);
    }

    private static Identifier handleModelId(Identifier input) {
        String namespace = input.getNamespace();
        String path = input.getPath();
        return Identifier.fromNamespaceAndPath(namespace, path.substring(MODELS.length(), path.length() - JSON.length()));
    }

    private static Identifier modModel(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }
}
