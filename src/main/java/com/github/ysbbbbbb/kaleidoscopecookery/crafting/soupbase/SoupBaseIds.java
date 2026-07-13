package com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase;

import net.minecraft.resources.Identifier;

public final class SoupBaseIds {
    public static final Identifier WATER = vanilla("water");
    public static final Identifier LAVA = vanilla("lava");

    private static final Identifier WATER_BUCKET_ALIAS = vanilla("water_bucket");
    private static final Identifier LAVA_BUCKET_ALIAS = vanilla("lava_bucket");

    private SoupBaseIds() {
    }

    public static Identifier normalize(Identifier id) {
        if (id.equals(WATER_BUCKET_ALIAS)) {
            return WATER;
        }
        if (id.equals(LAVA_BUCKET_ALIAS)) {
            return LAVA;
        }
        return id;
    }

    private static Identifier vanilla(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }
}
