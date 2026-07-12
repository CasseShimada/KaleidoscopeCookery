package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder.PotRecipeBuilder;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagCommon;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;

public class FoodBiteRecipeProvider extends ModRecipeProvider {
    private static final TagKey<Item> FLOWERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "flowers"));

    public FoodBiteRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        potRecipeBuilder()
                .addInput(Items.HONEY_BOTTLE, Items.HONEY_BOTTLE,
                        Items.SUGAR, Items.SUGAR, Items.PUMPKIN_PIE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.FONDANT_PIE)
                .save(consumer);

        potRecipeBuilder()
                .addInput(Items.BAMBOO, Items.BAMBOO,
                        TagCommon.RAW_PORK, TagCommon.RAW_PORK, TagCommon.RAW_PORK)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.DONGPO_PORK)
                .save(consumer);

        potRecipeBuilder()
                .addInput(Items.SUGAR, Items.SUGAR, Items.SUGAR,
                        Items.SPIDER_EYE, Items.SPIDER_EYE, Items.SPIDER_EYE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.FONDANT_SPIDER_EYE)
                .save(consumer);

        potRecipeBuilder()
                .addInput(Items.CHORUS_FRUIT, Items.CHORUS_FRUIT, Items.CHORUS_FRUIT,
                        TagCommon.COOKED_EGGS, TagCommon.COOKED_EGGS, TagCommon.COOKED_EGGS)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.CHORUS_FRIED_EGG, 3)
                .save(consumer);

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_COD, TagCommon.RAW_FISHES_COD)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.BRAISED_FISH)
                .save(consumer, "braised_fish_cod");

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_SALMON, TagCommon.RAW_FISHES_SALMON)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.BRAISED_FISH)
                .save(consumer, "braised_fish_salmon");

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_COD, TagCommon.RAW_FISHES_SALMON)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.BRAISED_FISH)
                .save(consumer, "braised_fish_cod_salmon");

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_COD, TagCommon.CROPS_CHILI_PEPPER)
                .setCarrier(ModItems.COOKED_RICE)
                .setResult(ModItems.BRAISED_FISH_RICE_BOWL)
                .save(consumer, "braised_fish_cod_with_rice");

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_SALMON, TagCommon.CROPS_CHILI_PEPPER)
                .setCarrier(ModItems.COOKED_RICE)
                .setResult(ModItems.BRAISED_FISH_RICE_BOWL)
                .save(consumer, "braised_fish_salmon_with_rice");

        potRecipeBuilder()
                .addInput(Items.GOLDEN_APPLE, Items.GOLDEN_APPLE,
                        Items.GOLDEN_CARROT, Items.GOLDEN_CARROT,
                        Items.GLISTERING_MELON_SLICE, Items.GLISTERING_MELON_SLICE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.GOLDEN_SALAD)
                .save(consumer, "golden_salad_golden_apple");

        potRecipeBuilder()
                .addInput(Items.ENCHANTED_GOLDEN_APPLE, Items.ENCHANTED_GOLDEN_APPLE,
                        Items.GOLDEN_CARROT, Items.GOLDEN_CARROT,
                        Items.GLISTERING_MELON_SLICE, Items.GLISTERING_MELON_SLICE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.GOLDEN_SALAD)
                .save(consumer, "golden_salad_enchanted_golden_apple");

        potRecipeBuilder()
                .addInput(Items.AMETHYST_SHARD, Items.AMETHYST_SHARD, Items.AMETHYST_SHARD, TagCommon.RAW_MUTTON)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.CRYSTAL_LAMB_CHOP)
                .save(consumer);

        potRecipeBuilder()
                .addInput(Items.CRIMSON_FUNGUS, Items.CRIMSON_FUNGUS,
                        Items.WARPED_FUNGUS, Items.WARPED_FUNGUS,
                        TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.NETHER_STYLE_SASHIMI)
                .save(consumer);

        potRecipeBuilder().addInput(Items.BONE, Items.BONE, Items.BONE,
                        Items.SWEET_BERRIES, Items.SWEET_BERRIES, Items.SWEET_BERRIES,
                        TagCommon.RAW_BEEF, TagCommon.RAW_BEEF)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.PAN_SEARED_KNIGHT_STEAK)
                .save(consumer);

        potRecipeBuilder()
                .addInput(Items.PUMPKIN_PIE, TagCommon.RAW_FISHES_COD, TagCommon.RAW_FISHES_COD,
                        TagCommon.RAW_FISHES_COD, TagCommon.RAW_FISHES_COD, TagCommon.RAW_FISHES_COD)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.STARGAZY_PIE)
                .save(consumer, "stargazy_pie_cod");

        potRecipeBuilder()
                .addInput(Items.PUMPKIN_PIE, TagCommon.RAW_FISHES_SALMON, TagCommon.RAW_FISHES_SALMON,
                        TagCommon.RAW_FISHES_SALMON, TagCommon.RAW_FISHES_SALMON, TagCommon.RAW_FISHES_SALMON)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.STARGAZY_PIE)
                .save(consumer, "stargazy_pie_salmon");

        potRecipeBuilder()
                .addInput(Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_EYE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.SWEET_AND_SOUR_ENDER_PEARLS)
                .save(consumer, "sweet_and_sour_ender_pearls_1");

        potRecipeBuilder()
                .addInput(Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL,
                        Items.ENDER_PEARL, Items.ENDER_EYE, Items.ENDER_EYE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.SWEET_AND_SOUR_ENDER_PEARLS, 2)
                .save(consumer, "sweet_and_sour_ender_pearls_2");

        potRecipeBuilder()
                .addInput(Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL,
                        Items.ENDER_PEARL, Items.ENDER_PEARL, Items.ENDER_PEARL,
                        Items.ENDER_EYE, Items.ENDER_EYE, Items.ENDER_EYE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.SWEET_AND_SOUR_ENDER_PEARLS, 3)
                .save(consumer, "sweet_and_sour_ender_pearls_3");

        potRecipeBuilder()
                .addInput(TagCommon.CROPS_CHILI_PEPPER, TagCommon.CROPS_CHILI_PEPPER,
                        TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN,
                        TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN,
                        Items.BLAZE_POWDER)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.SPICY_CHICKEN)
                .save(consumer, "spicy_chicken_blaze_powder");

        potRecipeBuilder()
                .addInput(TagCommon.CROPS_CHILI_PEPPER, TagCommon.CROPS_CHILI_PEPPER,
                        TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN,
                        Items.BLAZE_POWDER)
                .setCarrier(ModItems.COOKED_RICE)
                .setResult(ModItems.SPICY_CHICKEN_RICE_BOWL)
                .save(consumer, "spicy_chicken_rice_bowl_blaze_powder");

        potRecipeBuilder()
                .addInput(TagCommon.CROPS_CHILI_PEPPER, TagCommon.CROPS_CHILI_PEPPER,
                        TagCommon.CROPS_CHILI_PEPPER, TagCommon.RAW_CHICKEN,
                        TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.SPICY_CHICKEN)
                .save(consumer, "spicy_chicken");

        potRecipeBuilder()
                .addInput(TagCommon.CROPS_CHILI_PEPPER, TagCommon.CROPS_CHILI_PEPPER,
                        TagCommon.CROPS_CHILI_PEPPER, TagCommon.RAW_CHICKEN,
                        TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN)
                .setCarrier(ModItems.COOKED_RICE)
                .setResult(ModItems.SPICY_CHICKEN_RICE_BOWL)
                .save(consumer, "spicy_chicken_rice_bowl");

        potRecipeBuilder()
                .addInput(TagCommon.CROPS_CHILI_PEPPER, TagCommon.CROPS_CHILI_PEPPER,
                        TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN,
                        TagCommon.RAW_CHICKEN, TagCommon.RAW_CHICKEN)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.YAKITORI)
                .save(consumer);

        potRecipeBuilder()
                .addInput(TagCommon.RAW_MUTTON, TagCommon.RAW_MUTTON, TagCommon.RAW_MUTTON,
                        Items.BLAZE_ROD, Items.BLAZE_ROD, Items.BLAZE_ROD, Items.BLAZE_ROD,
                        Blocks.MAGMA_BLOCK)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.BLAZE_LAMB_CHOP).save(consumer);

        potRecipeBuilder()
                .addInput(TagCommon.RAW_MUTTON, TagCommon.RAW_MUTTON, TagCommon.RAW_MUTTON,
                        Items.BLUE_ICE, Items.BLUE_ICE, Items.BLUE_ICE)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.FROST_LAMB_CHOP)
                .save(consumer);

        potRecipeBuilder().addInput(TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL,
                        TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL)
                .addInput(Items.CHORUS_FRUIT, Items.CHORUS_FRUIT, Items.CHORUS_FRUIT)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.END_STYLE_SASHIMI)
                .save(consumer);

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL,
                        TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL,
                        Items.CACTUS, Items.CACTUS, Items.CACTUS)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.DESERT_STYLE_SASHIMI)
                .save(consumer);

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL,
                        TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL,
                        FLOWERS, FLOWERS, FLOWERS,
                        FLOWERS, FLOWERS)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.TUNDRA_STYLE_SASHIMI)
                .save(consumer);

        potRecipeBuilder()
                .addInput(TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL,
                        TagCommon.RAW_FISHES_TROPICAL, TagCommon.RAW_FISHES_TROPICAL,
                        Items.SNOWBALL, Items.SNOWBALL, Items.SNOWBALL,
                        Items.SNOWBALL, Blocks.SPRUCE_SAPLING)
                .setBowlCarrier()
                .setResult(FoodBiteRegistry.COLD_STYLE_SASHIMI)
                .save(consumer);

        Item slimeBallMeal = FoodBiteRegistry.getItem(FoodBiteRegistry.SLIME_BALL_MEAL);
        addSameItemRecipe(Items.SLIME_BALL, 4, slimeBallMeal, 1, Items.BOWL, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 5, slimeBallMeal, 1, Items.BOWL, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 6, slimeBallMeal, 1, Items.BOWL, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 7, slimeBallMeal, 1, Items.BOWL, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 8, slimeBallMeal, 2, Items.BOWL, consumer);
        addSameItemRecipe(Items.SLIME_BALL, 9, slimeBallMeal, 2, Items.BOWL, consumer);
    }

    public void addSameItemRecipe(Item inputItem, int count, Item outputItem, int outputCount,
                                  ItemLike carrier, RecipeOutput consumer) {
        addSameItemRecipe(inputItem, count, outputItem, outputCount, Ingredient.of(carrier), consumer);
    }

    public void addSameItemRecipe(Item inputItem, int count, Item outputItem, int outputCount,
                                  Ingredient carrier, RecipeOutput consumer) {
        ItemLike[] inputs = this.getItemsWithCount(inputItem, count);
        String idInput = this.getRecipeIdWithCount(inputItem, count);
        String idOutput = this.getRecipeIdWithCount(outputItem, outputCount);
        String id = String.format("%s_to_%s", idInput, idOutput);
        potRecipeBuilder().addInput((Object) inputs).setResult(outputItem, outputCount).setCarrier(carrier).save(consumer, id);
    }
}
