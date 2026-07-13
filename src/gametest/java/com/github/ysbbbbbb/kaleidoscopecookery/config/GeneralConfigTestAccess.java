package com.github.ysbbbbbb.kaleidoscopecookery.config;

public final class GeneralConfigTestAccess {
    private GeneralConfigTestAccess() {
    }

    public static void setSatiatedShieldAbsorbExcessDamage(GeneralConfig config, boolean absorbExcessDamage) {
        config.setSatiatedShieldAbsorbExcessDamage(absorbExcessDamage);
    }
}
