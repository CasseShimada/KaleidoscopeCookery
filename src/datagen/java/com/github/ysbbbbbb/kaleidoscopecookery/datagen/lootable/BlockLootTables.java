package com.github.ysbbbbbb.kaleidoscopecookery.datagen.lootable;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.EnamelBasinBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.ChiliRistraBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.StrungMushroomsBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricBlockLootSubProvider;
import net.minecraft.advancements.predicates.StatePropertiesPredicate;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.ExplosionCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemBlockStatePropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class BlockLootTables extends FabricBlockLootSubProvider {
    public final HolderLookup.RegistryLookup<Enchantment> enchantment;

    public BlockLootTables(FabricPackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        this.enchantment = this.registries.lookupOrThrow(Registries.ENCHANTMENT);
    }

    @Override
    public void generate() {
        dropSelf(ModBlocks.STOVE);
        dropSelf(ModBlocks.POT);
        dropSelf(ModBlocks.CHOPPING_BOARD);

        dropSelf(ModBlocks.COOK_STOOL_OAK);
        dropSelf(ModBlocks.COOK_STOOL_SPRUCE);
        dropSelf(ModBlocks.COOK_STOOL_ACACIA);
        dropSelf(ModBlocks.COOK_STOOL_BAMBOO);
        dropSelf(ModBlocks.COOK_STOOL_BIRCH);
        dropSelf(ModBlocks.COOK_STOOL_CHERRY);
        dropSelf(ModBlocks.COOK_STOOL_CRIMSON);
        dropSelf(ModBlocks.COOK_STOOL_DARK_OAK);
        dropSelf(ModBlocks.COOK_STOOL_JUNGLE);
        dropSelf(ModBlocks.COOK_STOOL_MANGROVE);
        dropSelf(ModBlocks.COOK_STOOL_WARPED);

        dropSelf(ModBlocks.CHAIR_OAK);
        dropSelf(ModBlocks.CHAIR_SPRUCE);
        dropSelf(ModBlocks.CHAIR_ACACIA);
        dropSelf(ModBlocks.CHAIR_BAMBOO);
        dropSelf(ModBlocks.CHAIR_BIRCH);
        dropSelf(ModBlocks.CHAIR_CHERRY);
        dropSelf(ModBlocks.CHAIR_CRIMSON);
        dropSelf(ModBlocks.CHAIR_DARK_OAK);
        dropSelf(ModBlocks.CHAIR_JUNGLE);
        dropSelf(ModBlocks.CHAIR_MANGROVE);
        dropSelf(ModBlocks.CHAIR_WARPED);

        dropSelf(ModBlocks.TABLE_OAK);
        dropSelf(ModBlocks.TABLE_SPRUCE);
        dropSelf(ModBlocks.TABLE_ACACIA);
        dropSelf(ModBlocks.TABLE_BAMBOO);
        dropSelf(ModBlocks.TABLE_BIRCH);
        dropSelf(ModBlocks.TABLE_CHERRY);
        dropSelf(ModBlocks.TABLE_CRIMSON);
        dropSelf(ModBlocks.TABLE_DARK_OAK);
        dropSelf(ModBlocks.TABLE_JUNGLE);
        dropSelf(ModBlocks.TABLE_MANGROVE);
        dropSelf(ModBlocks.TABLE_WARPED);

        dropSelf(ModBlocks.STOCKPOT);
        dropSelf(ModBlocks.FRUIT_BASKET);
        dropSelf(ModBlocks.KITCHENWARE_RACKS);
        dropSelf(ModBlocks.STRAW_BLOCK);
        dropSelf(ModBlocks.SHAWARMA_SPIT);
        dropSelf(ModBlocks.TRASH_CAN);
        dropSelf(ModBlocks.OIL_BLOCK);
        dropSelf(ModBlocks.OIL_POT);

        this.add(ModBlocks.TOMATO_CROP, createCropDrops(ModBlocks.TOMATO_CROP, ModItems.TOMATO,
                ModItems.TOMATO_SEED, createCropBuilder(ModBlocks.TOMATO_CROP)));

        var chiliBuilder = createCropBuilder(ModBlocks.CHILI_CROP);
        LootPoolSingletonContainer.Builder<?> greenChili = LootItem.lootTableItem(ModItems.GREEN_CHILI)
                .when(LootItemRandomChanceCondition.randomChance(0.2F));
        this.add(ModBlocks.CHILI_CROP, createCropDrops(ModBlocks.CHILI_CROP, ModItems.RED_CHILI, ModItems.CHILI_SEED, chiliBuilder)
                .withPool(LootPool.lootPool().when(chiliBuilder).add(greenChili)));

        var lettuceBuilder = createCropBuilder(ModBlocks.LETTUCE_CROP);
        LootPoolSingletonContainer.Builder<?> caterpillar = LootItem.lootTableItem(ModItems.CATERPILLAR)
                .when(LootItemRandomChanceCondition.randomChance(0.1F));
        this.add(ModBlocks.LETTUCE_CROP, createCropDrops(ModBlocks.LETTUCE_CROP, ModItems.LETTUCE, ModItems.LETTUCE_SEED, lettuceBuilder)
                .withPool(LootPool.lootPool().when(lettuceBuilder).add(caterpillar)));

        Item riceSeed = ModItems.WILD_RICE_SEED;
        LootItemCondition.Builder riceCropBuilder = createRiceCropBuilder();
        var countFunction = SetItemCountFunction.setCount(UniformGenerator.between(2, 4));
        LootPool.Builder ricePanicle = LootPool.lootPool().add(LootItem.lootTableItem(ModItems.RICE_PANICLE)
                .when(riceCropBuilder).apply(countFunction).otherwise(LootItem.lootTableItem(riceSeed)));
        LootPool.Builder extraRiceSeeds = LootPool.lootPool().add(LootItem.lootTableItem(riceSeed))
                .when(riceCropBuilder).apply(ApplyBonusCount.addBonusBinomialDistributionCount(enchantment.getOrThrow(Enchantments.FORTUNE), 0.5714286F, 3));

        this.add(ModBlocks.RICE_CROP, this.applyExplosionDecay(ModBlocks.RICE_CROP,
                LootTable.lootTable().withPool(ricePanicle).withPool(extraRiceSeeds)));

        FoodBiteRegistry.forEachData(this::dropFoodBite);
        PlateRegistry.forEachData(this::dropPlate);

        this.add(ModBlocks.ENAMEL_BASIN, createEnamelBasinLootTable());
        this.add(ModBlocks.CHILI_RISTRA, createChiliRistraLootTable());
        this.add(ModBlocks.STRUNG_MUSHROOMS, createStrungMushroomsLootTable());
        this.add(ModBlocks.COLD_CUT_HAM_SLICES, createColdCutHamSlicesLootTable());
    }

    private LootTable.Builder createChiliRistraLootTable() {
        LootPool.Builder builder = LootPool.lootPool();

        StatePropertiesPredicate.Builder isSheared = StatePropertiesPredicate.Builder.properties().hasProperty(ChiliRistraBlock.SHEARED, true);
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.CHILI_RISTRA).setProperties(isSheared);

        LootItemConditionalFunction.Builder<?> normalDrop = SetItemCountFunction.setCount(ConstantValue.exactly(6));
        LootItemConditionalFunction.Builder<?> shearedDrop = SetItemCountFunction.setCount(ConstantValue.exactly(3));

        LootPoolSingletonContainer.Builder<?> normalLoot = LootItem.lootTableItem(ModItems.RED_CHILI).apply(normalDrop);
        LootPoolSingletonContainer.Builder<?> shearedLoot = LootItem.lootTableItem(ModItems.RED_CHILI).apply(shearedDrop);

        builder.add(shearedLoot.when(condition).otherwise(normalLoot));

        return LootTable.lootTable().withPool(builder.when(ExplosionCondition.survivesExplosion()));
    }

    private LootTable.Builder createStrungMushroomsLootTable() {
        LootPool.Builder builder = LootPool.lootPool();

        StatePropertiesPredicate.Builder isSheared = StatePropertiesPredicate.Builder.properties().hasProperty(StrungMushroomsBlock.SHEARED, true);
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(ModBlocks.STRUNG_MUSHROOMS).setProperties(isSheared);

        LootItemConditionalFunction.Builder<?> normalDrop = SetItemCountFunction.setCount(ConstantValue.exactly(6));
        LootItemConditionalFunction.Builder<?> shearedDrop = SetItemCountFunction.setCount(ConstantValue.exactly(3));

        LootPoolSingletonContainer.Builder<?> normalLoot = LootItem.lootTableItem(Items.BROWN_MUSHROOM).apply(normalDrop);
        LootPoolSingletonContainer.Builder<?> shearedLoot = LootItem.lootTableItem(Items.BROWN_MUSHROOM).apply(shearedDrop);

        builder.add(shearedLoot.when(condition).otherwise(normalLoot));

        return LootTable.lootTable().withPool(builder.when(ExplosionCondition.survivesExplosion()));
    }

    private LootTable.Builder createColdCutHamSlicesLootTable() {
        if (!(ModBlocks.COLD_CUT_HAM_SLICES instanceof FoodBiteBlock foodBiteBlock)) {
            throw new IllegalStateException("Cold cut ham slices block has unexpected type");
        }
        StatePropertiesPredicate.Builder notBite = StatePropertiesPredicate.Builder.properties().hasProperty(foodBiteBlock.getBites(), 0);
        LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition.hasBlockStateProperties(foodBiteBlock).setProperties(notBite);

        LootPool.Builder builder = LootPool.lootPool()
                .add(LootItem.lootTableItem(ModItems.COLD_CUT_HAM_SLICES)
                        .when(condition)
                        .otherwise(LootItem.lootTableItem(Items.BOWL)));

        return LootTable.lootTable().withPool(builder.when(ExplosionCondition.survivesExplosion()));
    }

    private LootTable.Builder createEnamelBasinLootTable() {
        LootPool.Builder oilDrop = LootPool.lootPool();
        for (int i = 1; i <= EnamelBasinBlock.MAX_OIL_COUNT; i++) {
            StatePropertiesPredicate.Builder property = StatePropertiesPredicate.Builder.properties().hasProperty(EnamelBasinBlock.OIL_COUNT, i);
            LootItemCondition.Builder condition = LootItemBlockStatePropertyCondition
                    .hasBlockStateProperties(ModBlocks.ENAMEL_BASIN)
                    .setProperties(property);
            LootItemConditionalFunction.Builder<?> count = SetItemCountFunction.setCount(ConstantValue.exactly(i));
            oilDrop.add(LootItem.lootTableItem(ModItems.OIL).when(condition).apply(count));
        }
        LootPool.Builder basinDrop = LootPool.lootPool().add(LootItem.lootTableItem(ModItems.ENAMEL_BASIN));
        return LootTable.lootTable().withPool(oilDrop)
                .withPool(basinDrop.when(ExplosionCondition.survivesExplosion()));
    }

    private LootItemCondition.Builder createCropBuilder(Block cropBlock) {
        StatePropertiesPredicate.Builder property = StatePropertiesPredicate.Builder
                .properties().hasProperty(CropBlock.AGE, 7);
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(cropBlock)
                .setProperties(property);
    }

    private LootItemCondition.Builder createRiceCropBuilder() {
        StatePropertiesPredicate.Builder property = StatePropertiesPredicate.Builder
                .properties().hasProperty(CropBlock.AGE, 7)
                .hasProperty(RiceCropBlock.LOCATION, RiceCropBlock.DOWN);
        return LootItemBlockStatePropertyCondition
                .hasBlockStateProperties(ModBlocks.RICE_CROP)
                .setProperties(property);
    }

    private void dropPlate(Identifier id, PlateRegistry.PlateData data) {
        Block block = PlateRegistry.getBlock(id);
        LootTable.Builder lootTable = LootTable.lootTable();
        for (Supplier<Item> lootItem : data.lootItems()) {
            lootTable.withPool(LootPool.lootPool()
                    .when(ExplosionCondition.survivesExplosion())
                    .add(LootItem.lootTableItem(lootItem.get())));
        }
        this.add(block, lootTable);
    }

    private void dropFoodBite(Identifier id, FoodBiteRegistry.FoodData data) {
        Block block = BuiltInRegistries.BLOCK.getOptional(id)
                .orElseThrow(() -> new IllegalStateException("Missing registered food bite block: " + id));
        Item food = BuiltInRegistries.ITEM.getOptional(id)
                .orElseThrow(() -> new IllegalStateException("Missing registered food bite item: " + id));
        if (!(block instanceof FoodBiteBlock foodBiteBlock)) {
            throw new IllegalStateException("Registered food bite block has unexpected type: " + id);
        }
        ConstantValue exactly = ConstantValue.exactly(1);
        StatePropertiesPredicate.Builder notBite = StatePropertiesPredicate.Builder.properties().hasProperty(foodBiteBlock.getBites(), 0);
        LootItemCondition.Builder builder = LootItemBlockStatePropertyCondition.hasBlockStateProperties(foodBiteBlock).setProperties(notBite);

        LootTable.Builder lootTable = LootTable.lootTable();
        List<ItemLike> lootItems = data.getLootItems();
        for (int i = 0; i < lootItems.size(); i++) {
            ItemLike itemLike = lootItems.get(i);
            LootPool.Builder rolls = LootPool.lootPool().setRolls(exactly).when(ExplosionCondition.survivesExplosion());
            if (i == 0) {
                LootPoolSingletonContainer.Builder<?> foodDrop = LootItem.lootTableItem(food).when(builder);
                for (Quality quality : Quality.values()) {
                    StatePropertiesPredicate.Builder qualityState = StatePropertiesPredicate.Builder.properties()
                            .hasProperty(FoodBiteBlock.QUALITY, quality.getId());
                    foodDrop.apply(SetComponentsFunction.setComponent(ModDataComponents.QUALITY, quality)
                            .when(LootItemBlockStatePropertyCondition.hasBlockStateProperties(foodBiteBlock)
                                    .setProperties(qualityState)));
                }
                rolls.add(foodDrop.otherwise(LootItem.lootTableItem(itemLike)));
            } else {
                rolls.add(EmptyLootItem.emptyItem().when(builder).otherwise(LootItem.lootTableItem(itemLike)));
            }
            lootTable.withPool(rolls);
        }
        this.add(block, lootTable);
    }

    public Identifier modLoc(String name) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name);
    }
}
