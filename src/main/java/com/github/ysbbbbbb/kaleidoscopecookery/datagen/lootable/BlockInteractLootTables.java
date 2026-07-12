package com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootTables;
import net.minecraft.data.loot.LootTableSubProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;

import java.util.function.BiConsumer;

public final class BlockInteractLootTables implements LootTableSubProvider {
    @Override
    public void generate(BiConsumer<ResourceKey<LootTable>, LootTable.Builder> consumer) {
        consumer.accept(ModLootTables.HARVEST_TOMATO_CROP, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(LootItem.lootTableItem(ModItems.TOMATO))));

        consumer.accept(ModLootTables.HARVEST_CHILI_CROP, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(AlternativesEntry.alternatives(
                                LootItem.lootTableItem(ModItems.GREEN_CHILI)
                                        .when(LootItemRandomChanceCondition.randomChance(0.2F)),
                                LootItem.lootTableItem(ModItems.RED_CHILI)))));
    }
}
