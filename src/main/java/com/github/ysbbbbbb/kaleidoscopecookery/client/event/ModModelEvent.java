package com.github.ysbbbbbb.kaleidoscopecookery.client.event;


public class ModModelEvent {
    private static final String MODELS = "models/";
    private static final String MODELS_CHOPPING_BOARD = MODELS + "chopping_board";
    private static final String MODELS_CARPET = MODELS + "block/carpet";
    private static final String JSON = ".json";

//
//    public static void registerModels() {
//        ClientTickEvents.START_CLIENT_TICK.register(client -> {
//            var resourceManager = client.getResourceManager();
//            resourceManager.listResources(MODELS_CHOPPING_BOARD, id -> id.getPath().endsWith(JSON))
//                    .keySet().stream().map(ModModelEvent::handleModelId).forEach(modelIdentifier -> );
//
//            resourceManager.listResources(MODELS_CARPET, id -> id.getPath().endsWith(JSON))
//                    .keySet().stream().map(ModModelEvent::handleModelId).forEach(event::register);
//
//            // 额外添加的模型注册
//            // 蜂蜜瓶，用来替换锅内渲染
//            event.register(ModelIdentifier.standalone(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "item/honey")));
//            // 鸡蛋，用来替换锅内渲染
//            event.register(ModelIdentifier.standalone(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "item/egg")));
//            // 面团
//            event.register(ModelIdentifier.standalone(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "item/raw_dough_in_millstone")));
//            // 油
//            event.register(ModelIdentifier.standalone(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "item/oil_in_millstone")));
//
//            // 重置缓存
//            ItemRenderReplacer.resetCache();
//        });
//
//    }
//
//    private static ModelIdentifier handleModelId(Identifier input) {
//        String namespace = input.getNamespace();
//        String path = input.getPath();
//        String substring = path.substring(MODELS.length(), path.length() - JSON.length());
//        Identifier id = Identifier.fromNamespaceAndPath(namespace, substring);
//        return ModelIdentifier.standalone(id);
//    }
}
