package com.github.ysbbbbbb.kaleidoscopecookery.event.server.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceEntityMatchTool;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.minecraft.advancements.predicates.ItemPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.EnchantedCountIncreaseFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

public final class ExtraLootTableDrop {
    private static final OilDrop[] OIL_DROPS = {
            new OilDrop(Identifier.fromNamespaceAndPath("minecraft", "entities/hoglin"), 2),
            new OilDrop(Identifier.fromNamespaceAndPath("minecraft", "entities/pig"), 1),
            new OilDrop(Identifier.fromNamespaceAndPath("minecraft", "entities/piglin"), 2),
            new OilDrop(Identifier.fromNamespaceAndPath("minecraft", "entities/piglin_brute"), 2),
            new OilDrop(Identifier.fromNamespaceAndPath("minecraft", "entities/zoglin"), 2),
            new OilDrop(Identifier.fromNamespaceAndPath("minecraft", "entities/zombified_piglin"), 1)
    };
    private static final Identifier GRASS = Identifier.fromNamespaceAndPath("minecraft", "blocks/short_grass");
    private static final Identifier DONKEY = Identifier.fromNamespaceAndPath("minecraft", "entities/donkey");
    private static final float OIL_MIN_COUNT = 1.0F;
    private static final float OIL_MAX_COUNT = 2.0F;
    private static final float OIL_MIN_LOOTING_BONUS = 0.0F;
    private static final float OIL_MAX_LOOTING_BONUS = 1.0F;
    private static final int DONKEY_MEAT_ROLLS = 2;
    private static final float DONKEY_MEAT_MIN_COUNT = 1.0F;
    private static final float DONKEY_MEAT_MAX_COUNT = 2.0F;
    private static final float DONKEY_MEAT_MIN_LOOTING_BONUS = 0.0F;
    private static final float DONKEY_MEAT_MAX_LOOTING_BONUS = 1.0F;
    private static final int SEED_POOL_ROLLS = 1;
    private static final float COOKERY_SEED_DROP_CHANCE = 0.125F;
    private static final float VANILLA_SEED_DROP_CHANCE = 0.02F;
    private static final int SEED_FORTUNE_BONUS = 2;

    private ExtraLootTableDrop() {
    }

    public static void register() {
        LootTableEvents.MODIFY.register((key, tableBuilder, source, wrapperLookup) -> {
            Identifier id = key.identifier();
            OilDrop oilDrop = getOilDrop(id);
            if (oilDrop != null) {
                addOilDrop(tableBuilder, oilDrop.rolls(), wrapperLookup);
            } else if (id.equals(DONKEY)) {
                addDonkeyMeatDrop(tableBuilder, wrapperLookup);
            } else if (id.equals(GRASS)) {
                addSeedDrop(tableBuilder, wrapperLookup);
            }
        });
    }

    private static OilDrop getOilDrop(Identifier id) {
        for (OilDrop oilDrop : OIL_DROPS) {
            if (oilDrop.lootTableId().equals(id)) {
                return oilDrop;
            }
        }
        return null;
    }

    private static void addOilDrop(LootTable.Builder tableBuilder, int rolls, HolderLookup.Provider registries) {
        // 厨具刀杀猪掉油
        ItemPredicate hasKnife = ItemPredicate.Builder.item()
                .of(registries.lookupOrThrow(Registries.ITEM), TagMod.KITCHEN_KNIFE)
                .build();
        LootItemCondition.Builder toolMatches = AdvanceEntityMatchTool.toolMatches(EquipmentSlot.MAINHAND, hasKnife);
        var count = SetItemCountFunction.setCount(UniformGenerator.between(OIL_MIN_COUNT, OIL_MAX_COUNT));
        var looting = EnchantedCountIncreaseFunction.lootingMultiplier(registries,
                UniformGenerator.between(OIL_MIN_LOOTING_BONUS, OIL_MAX_LOOTING_BONUS));
        var oil = LootItem.lootTableItem(ModItems.OIL).apply(count).apply(looting);

        tableBuilder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(rolls))
                .add(oil).when(toolMatches));
    }

    private static void addDonkeyMeatDrop(LootTable.Builder tableBuilder, HolderLookup.Provider registries) {
        ItemPredicate hasKnife = ItemPredicate.Builder.item()
                .of(registries.lookupOrThrow(Registries.ITEM), TagMod.KITCHEN_KNIFE)
                .build();
        LootItemCondition.Builder toolMatches = AdvanceEntityMatchTool.toolMatches(EquipmentSlot.MAINHAND, hasKnife);
        var count = SetItemCountFunction.setCount(UniformGenerator.between(DONKEY_MEAT_MIN_COUNT, DONKEY_MEAT_MAX_COUNT));
        var looting = EnchantedCountIncreaseFunction.lootingMultiplier(registries,
                UniformGenerator.between(DONKEY_MEAT_MIN_LOOTING_BONUS, DONKEY_MEAT_MAX_LOOTING_BONUS));
        var meat = LootItem.lootTableItem(ModItems.RAW_DONKEY_MEAT).apply(count).apply(looting);

        tableBuilder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(DONKEY_MEAT_ROLLS))
                .add(meat).when(toolMatches));
    }

    private static void addSeedDrop(LootTable.Builder tableBuilder, HolderLookup.Provider registries) {
        HolderLookup.RegistryLookup<Enchantment> enchantment = registries.lookupOrThrow(Registries.ENCHANTMENT);
        // 穿戴草帽掉落番茄辣椒等种子
        var tomato = getSeed(ModItems.TOMATO_SEED, COOKERY_SEED_DROP_CHANCE, registries, enchantment);
        var chili = getSeed(ModItems.CHILI_SEED, COOKERY_SEED_DROP_CHANCE, registries, enchantment);
        var lettuce = getSeed(ModItems.LETTUCE_SEED, COOKERY_SEED_DROP_CHANCE, registries, enchantment);
        var rice = getSeed(ModItems.WILD_RICE_SEED, COOKERY_SEED_DROP_CHANCE, registries, enchantment);
        var beetroot = getSeed(Items.BEETROOT_SEEDS, VANILLA_SEED_DROP_CHANCE, registries, enchantment);
        var pumpkin = getSeed(Items.PUMPKIN_SEEDS, VANILLA_SEED_DROP_CHANCE, registries, enchantment);
        var melon = getSeed(Items.MELON_SEEDS, VANILLA_SEED_DROP_CHANCE, registries, enchantment);
        tableBuilder.withPool(LootPool.lootPool()
                .setRolls(ConstantValue.exactly(SEED_POOL_ROLLS))
                .add(tomato).add(chili)
                .add(lettuce).add(rice)
                .add(beetroot).add(pumpkin).add(melon));
    }

    private static LootPoolSingletonContainer.Builder<?> getSeed(ItemLike item, float chance,
                                                                 HolderLookup.Provider registries,
                                                                 HolderLookup.RegistryLookup<Enchantment> enchantment) {
        ItemPredicate hasHat = ItemPredicate.Builder.item()
                .of(registries.lookupOrThrow(Registries.ITEM), TagMod.STRAW_HAT)
                .build();
        LootItemCondition.Builder hatMatches = AdvanceBlockMatchTool.toolMatches(EquipmentSlot.HEAD, hasHat);
        return LootItem.lootTableItem(item)
                .when(LootItemRandomChanceCondition.randomChance(chance)).when(hatMatches)
                .apply(ApplyBonusCount.addUniformBonusCount(enchantment.getOrThrow(Enchantments.FORTUNE),
                        SEED_FORTUNE_BONUS));
    }

    private record OilDrop(Identifier lootTableId, int rolls) {
    }
}
