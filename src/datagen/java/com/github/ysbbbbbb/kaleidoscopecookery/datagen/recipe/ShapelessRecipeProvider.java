package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.level.ItemLike;

public class ShapelessRecipeProvider extends ModRecipeProvider {
    private static final TagKey<Item> RAW_MEATS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "raw_meats"));
    private static final TagKey<Item> VEGETABLES = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "vegetables"));
    private static final TagKey<Item> DOUGH = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("c", "dough"));
    private static final TagKey<Item> FLOWERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "flowers"));

    public ShapelessRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        shapeless(RecipeCategory.DECORATIONS, ModItems.RICE_PANICLE, 9)
                .requires(ModItems.STRAW_BLOCK)
                .unlockedBy("has_rice_panicle", has(ModItems.RICE_PANICLE))
                .save(consumer);

        shapeless(RecipeCategory.DECORATIONS, ModItems.OIL, 9)
                .requires(ModItems.OIL_BLOCK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.RICE_SEED, 3)
                .requires(ModItems.RICE_PANICLE)
                .unlockedBy("has_rice_panicle", has(ModItems.RICE_PANICLE))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.CHILI_SEED, 1)
                .requires(ModItems.GREEN_CHILI)
                .unlockedBy("has_chili", has(ModItems.GREEN_CHILI))
                .save(consumer, "chili_seed_from_green_chili");

        shapeless(RecipeCategory.FOOD, ModItems.CHILI_SEED, 1)
                .requires(ModItems.RED_CHILI)
                .unlockedBy("has_chili", has(ModItems.RED_CHILI))
                .save(consumer, "chili_seed_from_red_chili");

        shapeless(RecipeCategory.FOOD, ModItems.TOMATO_SEED, 1)
                .requires(ModItems.TOMATO)
                .unlockedBy("has_tomato", has(ModItems.TOMATO))
                .save(consumer);

        for (int flourCount = 1; flourCount <= 8; flourCount++) {
            shapeless(RecipeCategory.FOOD, ModItems.RAW_DOUGH, flourCount)
                    .requires(Items.WATER_BUCKET)
                    .requires(ModItems.FLOUR, flourCount)
                    .unlockedBy("has_wheat", has(Items.WHEAT))
                    .save(consumer, "minecraft:flour_from_" + flourCount + "_wheat");
        }

        shapeless(RecipeCategory.MISC, ModItems.RECIPE_ITEM)
                .requires(ModItems.RECIPE_ITEM)
                .unlockedBy("has_recipe_item", has(ModItems.RECIPE_ITEM))
                .save(consumer, "minecraft:reset_recipe_item");

        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.APPLE_PLATTER), Items.APPLE, 4, "has_apple");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.BAOZI_PLATE), ModItems.BAOZI, 5, "has_baozi");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.CHORUS_FRUIT_PLATTER), Items.CHORUS_FRUIT, 5, "has_chorus_fruit");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.QINGTUAN_PLATE), ModItems.QINGTUAN, 4, "has_qingtuan");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.SHENGJIAN_MANTOU_PLATE),
                FoodBiteRegistry.getItem(FoodBiteRegistry.SHENGJIAN_MANTOU), 5, "has_shengjian_mantou");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.STICKY_CANDY_PLATE), ModItems.STICKY_CANDY, 4, "has_sticky_candy");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.STICKY_RICE_CAKE_PLATE), ModItems.STICKY_RICE_CAKE, 5, "has_sticky_rice_cake");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.TOMATO_PLATTER), ModItems.TOMATO, 5, "has_tomato");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.WATERMELON_PLATTER), Items.MELON_SLICE, 3, "has_melon_slice");
        plateRecipe(consumer, PlateRegistry.getItem(PlateRegistry.ZONGZI_PLATE), ModItems.ZONGZI, 4, "has_zongzi");

        shapeless(RecipeCategory.FOOD, PlateRegistry.getItem(PlateRegistry.BERRY_PLATTER))
                .requires(Items.SWEET_BERRIES, 4)
                .requires(Items.GLOW_BERRIES, 4)
                .requires(Items.BOWL)
                .unlockedBy("has_sweet_berries", has(Items.SWEET_BERRIES))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.FRUIT_PLATTER))
                .requires(Items.APPLE, 2)
                .requires(Items.GLOW_BERRIES, 2)
                .requires(Items.SWEET_BERRIES, 2)
                .requires(Items.BOWL)
                .unlockedBy("has_apple", has(Items.APPLE))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.GOLDEN_SALAD))
                .requires(Items.GOLDEN_APPLE, 2)
                .requires(Items.GOLDEN_CARROT, 2)
                .requires(Items.GLISTERING_MELON_SLICE, 2)
                .requires(Items.BOWL)
                .unlockedBy("has_golden_apple", has(Items.GOLDEN_APPLE))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.NETHER_STYLE_SASHIMI))
                .requires(Items.CRIMSON_FUNGUS)
                .requires(Items.WARPED_FUNGUS)
                .requires(ModItems.SASHIMI, 4)
                .requires(Items.BOWL)
                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.DESERT_STYLE_SASHIMI))
                .requires(Items.CACTUS, 2)
                .requires(ModItems.SASHIMI, 4)
                .requires(Items.BOWL)
                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.COLD_STYLE_SASHIMI))
                .requires(Items.SNOWBALL, 3)
                .requires(ModItems.SASHIMI, 4)
                .requires(Items.BOWL)
                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.END_STYLE_SASHIMI))
                .requires(Items.CHORUS_FRUIT, 3)
                .requires(ModItems.SASHIMI, 4)
                .requires(Items.BOWL)
                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.TUNDRA_STYLE_SASHIMI))
                .requires(tag(FLOWERS), 2)
                .requires(ModItems.SASHIMI, 4)
                .requires(Items.BOWL)
                .unlockedBy("has_sashimi", has(ModItems.SASHIMI))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, FoodBiteRegistry.getItem(FoodBiteRegistry.COLD_ROASTED_MEAT))
                .requires(Items.COOKED_BEEF, 3)
                .requires(Items.BOWL)
                .unlockedBy("has_cooked_beef", has(Items.COOKED_BEEF))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.COLD_CUT_HAM_SLICES)
                .requires(ModItems.COOKED_PORK_BELLY, 8)
                .requires(Items.BOWL)
                .unlockedBy("has_cooked_pork_belly", has(ModItems.COOKED_PORK_BELLY))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.RAW_BAMBOO_TUBE_RICE)
                .requires(Items.BAMBOO)
                .requires(TagCommon.GRAIN_RICE)
                .requires(RAW_MEATS)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.RAW_MEATBALL)
                .requires(tag(RAW_MEATS), 2)
                .requires(VEGETABLES)
                .unlockedBy("has_raw_meats", has(RAW_MEATS))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.RAW_ZONGZI)
                .requires(ModItems.RICE_SEED)
                .requires(Items.LILY_PAD)
                .unlockedBy("has_rice", has(ModItems.RICE_SEED))
                .save(consumer);

        shapeless(RecipeCategory.FOOD, ModItems.STUFFED_DOUGH_FOOD)
                .requires(RAW_MEATS)
                .requires(VEGETABLES)
                .requires(DOUGH)
                .unlockedBy("has_raw_meats", has(RAW_MEATS))
                .save(consumer);

        prefilledSteamerRecipe(consumer, ModItems.RAW_DOUGH, "raw_dough_steamer");
        prefilledSteamerRecipe(consumer, ModItems.STUFFED_DOUGH_FOOD, "stuffed_dough_food_steamer");
    }

    private void plateRecipe(RecipeOutput consumer, ItemLike result, ItemLike ingredient, int count, String unlockName) {
        shapeless(RecipeCategory.FOOD, result)
                .requires(ingredient, count)
                .requires(Items.BOWL)
                .unlockedBy(unlockName, has(ingredient))
                .save(consumer);
    }

    private void prefilledSteamerRecipe(RecipeOutput consumer, ItemLike ingredient, String recipeId) {
        shapeless(RecipeCategory.MISC, createPrefilledSteamerTemplate(ingredient))
                .requires(ModItems.STEAMER)
                .requires(ingredient, 4)
                .unlockedBy(getHasName(ingredient), has(ingredient))
                .save(consumer, KaleidoscopeCookery.MOD_ID + ":" + recipeId);
    }

    private ItemStackTemplate createPrefilledSteamerTemplate(ItemLike ingredient) {
        CompoundTag tag = new CompoundTag();
        ListTag items = new ListTag();
        String ingredientId = BuiltInRegistries.ITEM.getKey(ingredient.asItem()).toString();
        for (int i = 0; i < 4; i++) {
            CompoundTag itemTag = new CompoundTag();
            itemTag.putByte("Slot", (byte) i);
            itemTag.putString("id", ingredientId);
            itemTag.putByte("Count", (byte) 1);
            items.add(itemTag);
        }
        tag.put("Items", items);
        tag.putIntArray(SteamerBlockEntity.COOKING_PROGRESS_TAG, new int[]{0, 0, 0, 0});
        tag.putIntArray(SteamerBlockEntity.COOKING_TIME_TAG, new int[]{1200, 1200, 1200, 1200});

        DataComponentPatch components = DataComponentPatch.builder()
                .set(DataComponents.BLOCK_ENTITY_DATA, TypedEntityData.of(ModBlocks.STEAMER_BE, tag))
                .set(DataComponents.MAX_STACK_SIZE, 1)
                .build();
        return new ItemStackTemplate(ModItems.STEAMER.asItem(), components);
    }
}
