package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class ShapedRecipeProvider extends ModRecipeProvider {
    private static final TagKey<Item> FLOWERS = TagKey.create(Registries.ITEM, Identifier.fromNamespaceAndPath("minecraft", "flowers"));

    public ShapedRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    protected void buildRecipes(RecipeOutput consumer) {
        shaped(RecipeCategory.REDSTONE, ModItems.STOVE)
                .pattern("###")
                .pattern("#F#")
                .pattern("###")
                .define('#', Items.COBBLESTONE)
                .define('F', Items.CAMPFIRE)
                .unlockedBy("has_campfire", has(Items.CAMPFIRE))
                .save(consumer, "kaleidoscope_cookery:stove_campfire");

        shaped(RecipeCategory.REDSTONE, ModItems.STOVE)
                .pattern("###")
                .pattern("#F#")
                .pattern("###")
                .define('#', Items.COBBLESTONE)
                .define('F', Items.SOUL_CAMPFIRE)
                .unlockedBy("has_soul_campfire", has(Items.SOUL_CAMPFIRE))
                .save(consumer, "kaleidoscope_cookery:stove_soul_campfire");

        shaped(RecipeCategory.DECORATIONS, ModItems.FRUIT_BASKET)
                .pattern(" S ")
                .pattern("#C#")
                .pattern("###")
                .define('S', Items.STICK)
                .define('#', ItemTags.PLANKS)
                .define('C', Items.CHEST)
                .unlockedBy("has_chest", has(Items.CHEST))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.SCARECROW)
                .pattern(" H ")
                .pattern("SPS")
                .pattern(" # ")
                .define('H', TagMod.STRAW_HAT)
                .define('S', Items.STICK)
                .define('P', Items.PUMPKIN)
                .define('#', TagMod.STRAW_BALE)
                .unlockedBy("has_pumpkin", has(Items.PUMPKIN))
                .save(consumer);

        shaped(RecipeCategory.REDSTONE, ModItems.POT)
                .pattern("###")
                .pattern("###")
                .pattern(" # ")
                .define('#', Items.IRON_INGOT)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, ModItems.IRON_KITCHEN_KNIFE)
                .pattern("##")
                .pattern("#S")
                .define('#', Items.IRON_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, ModItems.GOLD_KITCHEN_KNIFE)
                .pattern("##")
                .pattern("#S")
                .define('#', Items.GOLD_INGOT)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, ModItems.DIAMOND_KITCHEN_KNIFE)
                .pattern("##")
                .pattern("#S")
                .define('#', Items.DIAMOND)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, ModItems.KITCHEN_SHOVEL)
                .pattern("I  ")
                .pattern(" N ")
                .pattern("  S")
                .define('I', Items.IRON_INGOT)
                .define('N', Items.IRON_NUGGET)
                .define('S', Items.STICK)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, ModItems.STRAW_HAT)
                .pattern(" W ")
                .pattern(" S ")
                .pattern("WWW")
                .define('W', Items.WHEAT)
                .define('S', Items.STRING)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, ModItems.STRAW_HAT_FLOWER)
                .pattern("FFF")
                .pattern("FHF")
                .pattern("FFF")
                .define('F', FLOWERS)
                .define('H', ModItems.STRAW_HAT)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.OIL_BLOCK)
                .pattern("OOO")
                .pattern("OOO")
                .pattern("OOO")
                .define('O', ModItems.OIL)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.CHOPPING_BOARD)
                .pattern("PPP")
                .pattern("PPP")
                .define('P', ItemTags.WOODEN_PRESSURE_PLATES)
                .unlockedBy("has_wood", has(Items.OAK_PLANKS))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.STOCKPOT)
                .pattern("B B")
                .pattern("I I")
                .pattern("III")
                .define('B', Items.BRICK)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.STOCKPOT_LID)
                .pattern(" B ")
                .pattern("III")
                .define('B', Items.BRICK)
                .define('I', Items.IRON_INGOT)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.ENAMEL_BASIN)
                .pattern("OOO")
                .pattern("OOO")
                .pattern(" B ")
                .define('O', TagMod.OIL)
                .define('B', Items.BUCKET)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.KITCHENWARE_RACKS)
                .pattern("SSS")
                .pattern("INI")
                .define('S', Items.STICK)
                .define('I', Items.IRON_INGOT)
                .define('N', Items.IRON_NUGGET)
                .unlockedBy("has_ingot_iron", has(Items.IRON_INGOT))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.CHILI_RISTRA)
                .pattern("CC ")
                .pattern("CC ")
                .pattern("CC ")
                .define('C', ModItems.RED_CHILI)
                .unlockedBy("has_red_chili", has(ModItems.RED_CHILI))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.STRAW_BLOCK)
                .pattern("RRR")
                .pattern("RRR")
                .pattern("RRR")
                .define('R', ModItems.RICE_PANICLE)
                .unlockedBy("has_rice_panicle", has(ModItems.RICE_PANICLE))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.FARMER_CHEST_PLATE)
                .pattern("I I")
                .pattern("LLL")
                .pattern("LLL")
                .define('I', Items.IRON_INGOT)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.FARMER_LEGGINGS)
                .pattern("LIL")
                .pattern("L L")
                .pattern("L L")
                .define('I', Items.IRON_INGOT)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.FARMER_BOOTS)
                .pattern("I I")
                .pattern("L L")
                .define('I', Items.IRON_INGOT)
                .define('L', Items.LEATHER)
                .unlockedBy("has_leather", has(Items.LEATHER))
                .save(consumer);

        shaped(RecipeCategory.DECORATIONS, ModItems.SHAWARMA_SPIT)
                .pattern("ICI")
                .pattern("ICI")
                .define('I', Items.IRON_CHAIN)
                .define('C', Items.CAMPFIRE)
                .unlockedBy("has_campfire", has(Items.CAMPFIRE))
                .save(consumer);

        shaped(RecipeCategory.TOOLS, ModItems.STEAMER)
                .pattern("TTT")
                .pattern("BBB")
                .define('T', Items.BAMBOO_TRAPDOOR)
                .define('B', Items.BAMBOO_BLOCK)
                .unlockedBy("has_bamboo", has(Items.BAMBOO))
                .save(consumer);
    }
}
