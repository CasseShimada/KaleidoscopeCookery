package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ClientRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModClientTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModEntitiesRender;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModModelLoading;
import com.github.ysbbbbbb.kaleidoscopecookery.client.init.ModParticleFactoryRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.client.resources.LegacyResourcePack;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfig;
import net.fabricmc.api.ClientModInitializer;

public class KaleidoscopeCookeryClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientConfig.init();
        ClientRegistry.init();

        ModModelLoading.register();
        ModClientTooltip.register();
        ModEntitiesRender.register();
        ModParticleFactoryRegistry.register();
        LegacyResourcePack.register();
    }
}
