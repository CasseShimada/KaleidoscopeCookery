package com.github.ysbbbbbb.kaleidoscopecookery.compat;

import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfig;
import net.fabricmc.loader.api.FabricLoader;

public final class FoodEffectTooltipsCompat {
    public static final String MOD_ID = "foodeffecttooltips";

    private FoodEffectTooltipsCompat() {
    }

    public static boolean shouldShowCookeryEffectTooltips() {
        return shouldShowCookeryEffectTooltips(
                ClientConfig.get().showFoodEffectTooltips(),
                FabricLoader.getInstance().isModLoaded(MOD_ID));
    }

    static boolean shouldShowCookeryEffectTooltips(boolean configured, boolean externalTooltipModLoaded) {
        return configured && !externalTooltipModLoaded;
    }
}
