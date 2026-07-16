package com.github.ysbbbbbb.kaleidoscopecookery.config;

public final class GeneralConfigTestAccess {
    private GeneralConfigTestAccess() {
    }

    public static GeneralConfig fromLegacyToml(String toml) {
        return GeneralConfig.fromLegacyToml(toml);
    }

    public static void setSatiatedShieldAbsorbExcessDamage(GeneralConfig config, boolean absorbExcessDamage) {
        config.setSatiatedShieldAbsorbExcessDamage(absorbExcessDamage);
    }

    public static void setSatiatedShieldMinFoodLevel(GeneralConfig config, int minFoodLevel) {
        config.setSatiatedShieldMinFoodLevel(minFoodLevel);
    }
}
