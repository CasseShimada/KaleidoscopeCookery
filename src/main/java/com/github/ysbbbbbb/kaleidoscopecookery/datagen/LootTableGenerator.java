package com.github.ysbbbbbb.kaleidoscopecookery.datagen;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable.BlockLootTables;
import com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable.BlockInteractLootTables;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class LootTableGenerator extends LootTableProvider {
    public LootTableGenerator(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, Set.of(), List.of(
                new LootTableProvider.SubProviderEntry(provider -> new BlockLootTables(output, registries), LootContextParamSets.BLOCK),
                new LootTableProvider.SubProviderEntry(provider -> new BlockInteractLootTables(), LootContextParamSets.BLOCK_INTERACT)
        ), registries);
    }
}
