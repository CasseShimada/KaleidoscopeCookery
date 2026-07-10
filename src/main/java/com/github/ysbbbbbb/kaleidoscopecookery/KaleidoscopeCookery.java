package com.github.ysbbbbbb.kaleidoscopecookery;

import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModCreativeTabs;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootTypes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModPoi;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModVillager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.CommonRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.network.NetworkHandler;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public final class KaleidoscopeCookery implements ModInitializer {
    public static final String MOD_ID = "kaleidoscope_cookery";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        GeneralConfig.init();
        // 药水效果优先注册
        ModEffects.registerEffects();
        // Data components must exist before item static initialization.
        ModDataComponents.registerDataComponents();

        CommonRegistry.registerContent();
        NetworkHandler.init();

        ModTrigger.init();
        ModBlocks.registerBlocks();
        ModItems.registerItems();
        CommonRegistry.registerIntegrations();
        ModEntities.registerEntities();
        ModPoi.registerPoiTypes();
        ModVillager.registerVillagerProfessions();
        ModCreativeTabs.registerTabs();
        ModSounds.registerSounds();
        ModParticles.registerParticles();
        ModRecipes.registerRecipes();
        ModLootTypes.registerLootTypes();
        ModSoupBases.registerSoupBases();
        // 事件
        ModEvents.init();
    }
}
