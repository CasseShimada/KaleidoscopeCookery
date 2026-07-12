package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootTable;

public final class ModLootTables {
    public static final ResourceKey<LootTable> HARVEST_TOMATO_CROP = create("harvest/tomato_crop");
    public static final ResourceKey<LootTable> HARVEST_CHILI_CROP = create("harvest/chili_crop");

    private static ResourceKey<LootTable> create(String path) {
        return ResourceKey.create(
                Registries.LOOT_TABLE,
                Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path));
    }

    private ModLootTables() {
    }
}
