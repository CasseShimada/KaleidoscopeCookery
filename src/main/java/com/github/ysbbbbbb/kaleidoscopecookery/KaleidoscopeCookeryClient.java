package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ClientRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModBlockRenderLayerMap;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModClientTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModEntitiesRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModModelLoading;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModParticleFactoryRegistry;
import net.fabricmc.api.ClientModInitializer;

public class KaleidoscopeCookeryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientRegistry.init();

        ModModelLoading.register();
        ModClientTooltip.register();
        ModEntitiesRender.register();
        ModParticleFactoryRegistry.register();
        ModBlockRenderLayerMap.register();
    }
}
