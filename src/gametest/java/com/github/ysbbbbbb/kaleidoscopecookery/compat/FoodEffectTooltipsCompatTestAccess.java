package com.github.ysbbbbbb.kaleidoscopecookery.compat;

public final class FoodEffectTooltipsCompatTestAccess {
    private FoodEffectTooltipsCompatTestAccess() {
    }

    public static boolean shouldShowCookeryEffectTooltips(
            boolean configured, boolean externalTooltipModLoaded) {
        return FoodEffectTooltipsCompat.shouldShowCookeryEffectTooltips(configured, externalTooltipModLoaded);
    }
}
