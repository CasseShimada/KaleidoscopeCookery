package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;

public final class ModItems {
    // Block items
    public static final Item STOVE = new WithTooltipsBlockItem(ModBlocks.STOVE, blockItemProperties("stove"), "stove");
    public static final Item POT = new WithTooltipsBlockItem(ModBlocks.POT, blockItemProperties("pot"), "pot", "pot.fail");
    public static final Item STOCKPOT = new WithTooltipsBlockItem(ModBlocks.STOCKPOT, blockItemProperties("stockpot"), "stockpot", "stockpot.fail");
    public static final Item STOCKPOT_LID = new StockpotLidItem(itemProperties("stockpot_lid"));
    public static final Item OIL = new WithTooltipsItem(itemProperties("oil"), "oil");
    public static final Item OIL_BLOCK = new BlockItem(ModBlocks.OIL_BLOCK, blockItemProperties("oil_block"));
    public static final Item CHOPPING_BOARD = new WithTooltipsBlockItem(ModBlocks.CHOPPING_BOARD, blockItemProperties("chopping_board"), "chopping_board");
    public static final Item ENAMEL_BASIN = new WithTooltipsBlockItem(ModBlocks.ENAMEL_BASIN, blockItemProperties("enamel_basin"), "enamel_basin");
    public static final Item KITCHENWARE_RACKS = new WithTooltipsBlockItem(ModBlocks.KITCHENWARE_RACKS, blockItemProperties("kitchenware_racks"), "kitchenware_racks");
    public static final Item CHILI_RISTRA = new BlockItem(ModBlocks.CHILI_RISTRA, blockItemProperties("chili_ristra"));
    public static final Item STRUNG_MUSHROOMS = new BlockItem(ModBlocks.STRUNG_MUSHROOMS, blockItemProperties("strung_mushrooms"));
    public static final Item STRAW_BLOCK = new BlockItem(ModBlocks.STRAW_BLOCK, blockItemProperties("straw_block"));
    public static final Item SHAWARMA_SPIT = new WithTooltipsBlockItem(ModBlocks.SHAWARMA_SPIT, blockItemProperties("shawarma_spit"), "shawarma_spit");
    public static final Item MILLSTONE = new WithTooltipsBlockItem(ModBlocks.MILLSTONE, blockItemProperties("millstone"), "millstone");
    public static final Item STEAMER = new SteamerItem(blockItemProperties("steamer"));
    public static final Item OIL_POT = new OilPotItem(blockItemProperties("oil_pot"));
    public static final Item COLD_CUT_HAM_SLICES = new LiftBlockItem(
            ModBlocks.COLD_CUT_HAM_SLICES, blockItemProperties("cold_cut_ham_slices"), "cold_cut_ham_slices");

    // Tools
    public static final Item IRON_KITCHEN_KNIFE = new KitchenKnifeItem(ToolMaterial.IRON, itemProperties("iron_kitchen_knife"));
    public static final Item GOLD_KITCHEN_KNIFE = new KitchenKnifeItem(ToolMaterial.GOLD, itemProperties("gold_kitchen_knife"));
    public static final Item DIAMOND_KITCHEN_KNIFE = new KitchenKnifeItem(ToolMaterial.DIAMOND, itemProperties("diamond_kitchen_knife"));
    public static final Item NETHERITE_KITCHEN_KNIFE = new KitchenKnifeItem(ToolMaterial.NETHERITE, itemProperties("netherite_kitchen_knife"));
    public static final Item SICKLE = new SickleItem(itemProperties("sickle"));

    // Special items
    public static final Item RECIPE_ITEM = new RecipeItem(itemProperties("recipe_item"));
    public static final Item KITCHEN_SHOVEL = new KitchenShovelItem(itemProperties("kitchen_shovel"));
    public static final Item FRUIT_BASKET = new FruitBasketItem(itemProperties("fruit_basket"));
    public static final Item SCARECROW = new ScarecrowItem(itemProperties("scarecrow"));
    public static final Item STRAW_HAT = new StrawHatItem(false, itemProperties("straw_hat"));
    public static final Item STRAW_HAT_FLOWER = new StrawHatItem(true, itemProperties("straw_hat_flower"));
    public static final Item FARMER_CHEST_PLATE = new ModArmorItem(ModArmorMaterials.FARMER.value(), ArmorType.CHESTPLATE, itemProperties("farmer_chest_plate").stacksTo(1));
    public static final Item FARMER_LEGGINGS = new ModArmorItem(ModArmorMaterials.FARMER.value(), ArmorType.LEGGINGS, itemProperties("farmer_leggings").stacksTo(1));
    public static final Item FARMER_BOOTS = new ModArmorItem(ModArmorMaterials.FARMER.value(), ArmorType.BOOTS, itemProperties("farmer_boots").stacksTo(1));
    public static final Item TRANSMUTATION_LUNCH_BAG = new TransmutationLunchBagItem(itemProperties("transmutation_lunch_bag"));

    // Seeds
    public static final Item TOMATO_SEED = new ItemNameBlockItem(ModBlocks.TOMATO_CROP, itemProperties("tomato_seed"));
    public static final Item CHILI_SEED = new ItemNameBlockItem(ModBlocks.CHILI_CROP, itemProperties("chili_seed"));
    public static final Item LETTUCE_SEED = new ItemNameBlockItem(ModBlocks.LETTUCE_CROP, itemProperties("lettuce_seed"));
    public static final Item RICE_SEED = new RiceItem(itemProperties("rice"));
    public static final Item WILD_RICE_SEED = new ItemNameBlockItem(ModBlocks.RICE_CROP, itemProperties("wild_rice"));

    // Cook stools
    public static final Item COOK_STOOL_OAK = new BlockItem(ModBlocks.COOK_STOOL_OAK, blockItemProperties("cook_stool_oak"));
    public static final Item COOK_STOOL_SPRUCE = new BlockItem(ModBlocks.COOK_STOOL_SPRUCE, blockItemProperties("cook_stool_spruce"));
    public static final Item COOK_STOOL_ACACIA = new BlockItem(ModBlocks.COOK_STOOL_ACACIA, blockItemProperties("cook_stool_acacia"));
    public static final Item COOK_STOOL_BAMBOO = new BlockItem(ModBlocks.COOK_STOOL_BAMBOO, blockItemProperties("cook_stool_bamboo"));
    public static final Item COOK_STOOL_BIRCH = new BlockItem(ModBlocks.COOK_STOOL_BIRCH, blockItemProperties("cook_stool_birch"));
    public static final Item COOK_STOOL_CHERRY = new BlockItem(ModBlocks.COOK_STOOL_CHERRY, blockItemProperties("cook_stool_cherry"));
    public static final Item COOK_STOOL_CRIMSON = new BlockItem(ModBlocks.COOK_STOOL_CRIMSON, blockItemProperties("cook_stool_crimson"));
    public static final Item COOK_STOOL_DARK_OAK = new BlockItem(ModBlocks.COOK_STOOL_DARK_OAK, blockItemProperties("cook_stool_dark_oak"));
    public static final Item COOK_STOOL_JUNGLE = new BlockItem(ModBlocks.COOK_STOOL_JUNGLE, blockItemProperties("cook_stool_jungle"));
    public static final Item COOK_STOOL_MANGROVE = new BlockItem(ModBlocks.COOK_STOOL_MANGROVE, blockItemProperties("cook_stool_mangrove"));
    public static final Item COOK_STOOL_WARPED = new BlockItem(ModBlocks.COOK_STOOL_WARPED, blockItemProperties("cook_stool_warped"));

    // Chairs
    public static final Item CHAIR_OAK = new BlockItem(ModBlocks.CHAIR_OAK, blockItemProperties("chair_oak"));
    public static final Item CHAIR_SPRUCE = new BlockItem(ModBlocks.CHAIR_SPRUCE, blockItemProperties("chair_spruce"));
    public static final Item CHAIR_ACACIA = new BlockItem(ModBlocks.CHAIR_ACACIA, blockItemProperties("chair_acacia"));
    public static final Item CHAIR_BAMBOO = new BlockItem(ModBlocks.CHAIR_BAMBOO, blockItemProperties("chair_bamboo"));
    public static final Item CHAIR_BIRCH = new BlockItem(ModBlocks.CHAIR_BIRCH, blockItemProperties("chair_birch"));
    public static final Item CHAIR_CHERRY = new BlockItem(ModBlocks.CHAIR_CHERRY, blockItemProperties("chair_cherry"));
    public static final Item CHAIR_CRIMSON = new BlockItem(ModBlocks.CHAIR_CRIMSON, blockItemProperties("chair_crimson"));
    public static final Item CHAIR_DARK_OAK = new BlockItem(ModBlocks.CHAIR_DARK_OAK, blockItemProperties("chair_dark_oak"));
    public static final Item CHAIR_JUNGLE = new BlockItem(ModBlocks.CHAIR_JUNGLE, blockItemProperties("chair_jungle"));
    public static final Item CHAIR_MANGROVE = new BlockItem(ModBlocks.CHAIR_MANGROVE, blockItemProperties("chair_mangrove"));
    public static final Item CHAIR_WARPED = new BlockItem(ModBlocks.CHAIR_WARPED, blockItemProperties("chair_warped"));

    // Tables
    public static final Item TABLE_OAK = new BlockItem(ModBlocks.TABLE_OAK, blockItemProperties("table_oak"));
    public static final Item TABLE_SPRUCE = new BlockItem(ModBlocks.TABLE_SPRUCE, blockItemProperties("table_spruce"));
    public static final Item TABLE_ACACIA = new BlockItem(ModBlocks.TABLE_ACACIA, blockItemProperties("table_acacia"));
    public static final Item TABLE_BAMBOO = new BlockItem(ModBlocks.TABLE_BAMBOO, blockItemProperties("table_bamboo"));
    public static final Item TABLE_BIRCH = new BlockItem(ModBlocks.TABLE_BIRCH, blockItemProperties("table_birch"));
    public static final Item TABLE_CHERRY = new BlockItem(ModBlocks.TABLE_CHERRY, blockItemProperties("table_cherry"));
    public static final Item TABLE_CRIMSON = new BlockItem(ModBlocks.TABLE_CRIMSON, blockItemProperties("table_crimson"));
    public static final Item TABLE_DARK_OAK = new BlockItem(ModBlocks.TABLE_DARK_OAK, blockItemProperties("table_dark_oak"));
    public static final Item TABLE_JUNGLE = new BlockItem(ModBlocks.TABLE_JUNGLE, blockItemProperties("table_jungle"));
    public static final Item TABLE_MANGROVE = new BlockItem(ModBlocks.TABLE_MANGROVE, blockItemProperties("table_mangrove"));
    public static final Item TABLE_WARPED = new BlockItem(ModBlocks.TABLE_WARPED, blockItemProperties("table_warped"));

    // Food items
    public static final Item TOMATO = new Item(ModFoods.applyFood(itemProperties("tomato"), ModFoods.TOMATO));
    public static final Item RED_CHILI = new ChiliItem(2, itemProperties("red_chili"));
    public static final Item GREEN_CHILI = new ChiliItem(1, itemProperties("green_chili"));
    public static final Item LETTUCE = new Item(ModFoods.applyFood(itemProperties("lettuce"), ModFoods.LETTUCE));
    public static final Item RICE_PANICLE = new Item(itemProperties("rice_panicle"));
    public static final Item CATERPILLAR = new WithTooltipsItem(ModFoods.applyFood(itemProperties("caterpillar"), ModFoods.CATERPILLAR), "caterpillar");
    public static final Item FRIED_EGG = new Item(ModFoods.applyFood(itemProperties("fried_egg"), ModFoods.FRIED_EGG));
    public static final Item DONKEY_BURGER = new FoodWithEffectsItem(itemProperties("donkey_burger"), ModFoods.DONKEY_BURGER);
    public static final Item MANTOU = new FoodWithEffectsItem(itemProperties("mantou"), ModFoods.MANTOU);
    public static final Item BAOZI = new FoodWithEffectsItem(itemProperties("baozi"), ModFoods.BAOZI);
    public static final Item SAMSA = new FoodWithEffectsItem(itemProperties("samsa"), ModFoods.SAMSA);
    public static final Item MEAT_PIE = new FoodWithEffectsItem(itemProperties("meat_pie"), ModFoods.MEAT_PIE);
    public static final Item DUMPLING = new FoodWithEffectsItem(itemProperties("dumpling"), ModFoods.DUMPLING);
    public static final Item RAW_DOUGH = new RawDoughItem(itemProperties("raw_dough"));
    public static final Item FLOUR = new FlourItem(itemProperties("flour"));
    public static final Item RAW_NOODLES = new Item(itemProperties("raw_noodles"));
    public static final Item STUFFED_DOUGH_FOOD = new Item(itemProperties("stuffed_dough_food"));

    // Bowl foods
    public static final Item COOKED_RICE = new BowlFoodOnlyItem(itemProperties("cooked_rice"), ModFoods.COOKED_RICE);
    public static final Item SCRAMBLE_EGG_WITH_TOMATOES = new BowlFoodOnlyItem(itemProperties("scramble_egg_with_tomatoes"), ModFoods.SCRAMBLE_EGG_WITH_TOMATOES);
    public static final Item SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("scramble_egg_with_tomatoes_rice_bowl"), ModFoods.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL);
    public static final Item STIR_FRIED_BEEF_OFFAL = new BowlFoodOnlyItem(itemProperties("stir_fried_beef_offal"), ModFoods.STIR_FRIED_BEEF_OFFAL);
    public static final Item STIR_FRIED_BEEF_OFFAL_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("stir_fried_beef_offal_rice_bowl"), ModFoods.STIR_FRIED_BEEF_OFFAL_RICE_BOWL);
    public static final Item BRAISED_BEEF = new BowlFoodOnlyItem(itemProperties("braised_beef"), ModFoods.BRAISED_BEEF);
    public static final Item BRAISED_BEEF_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("braised_beef_rice_bowl"), ModFoods.BRAISED_BEEF_RICE_BOWL);
    public static final Item STIR_FRIED_PORK_WITH_PEPPERS = new BowlFoodOnlyItem(itemProperties("stir_fried_pork_with_peppers"), ModFoods.STIR_FRIED_PORK_WITH_PEPPERS);
    public static final Item STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("stir_fried_pork_with_peppers_rice_bowl"), ModFoods.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL);
    public static final Item SWEET_AND_SOUR_PORK = new BowlFoodOnlyItem(itemProperties("sweet_and_sour_pork"), ModFoods.SWEET_AND_SOUR_PORK);
    public static final Item SWEET_AND_SOUR_PORK_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("sweet_and_sour_pork_rice_bowl"), ModFoods.SWEET_AND_SOUR_PORK_RICE_BOWL);
    public static final Item COUNTRY_STYLE_MIXED_VEGETABLES = new BowlFoodOnlyItem(itemProperties("country_style_mixed_vegetables"), ModFoods.COUNTRY_STYLE_MIXED_VEGETABLES);
    public static final Item FISH_FLAVORED_SHREDDED_PORK = new BowlFoodOnlyItem(itemProperties("fish_flavored_shredded_pork"), ModFoods.FISH_FLAVORED_SHREDDED_PORK);
    public static final Item FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("fish_flavored_shredded_pork_rice_bowl"), ModFoods.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL);
    public static final Item BRAISED_FISH_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("braised_fish_rice_bowl"), ModFoods.BRAISED_FISH_RICE_BOWL);
    public static final Item SPICY_CHICKEN_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("spicy_chicken_rice_bowl"), ModFoods.SPICY_CHICKEN_RICE_BOWL);
    public static final Item SUSPICIOUS_STIR_FRY_RICE_BOWL = new BowlFoodOnlyItem(itemProperties("suspicious_stir_fry_rice_bowl"), ModFoods.SUSPICIOUS_STIR_FRY_RICE_BOWL);
    public static final Item EGG_FRIED_RICE = new BowlFoodOnlyItem(itemProperties("egg_fried_rice"), ModFoods.EGG_FRIED_RICE);
    public static final Item DELICIOUS_EGG_FRIED_RICE = new BowlFoodOnlyItem(itemProperties("delicious_egg_fried_rice"), ModFoods.DELICIOUS_EGG_FRIED_RICE);
    public static final Item PORK_BONE_SOUP = new BowlFoodOnlyItem(itemProperties("pork_bone_soup"), ModFoods.PORK_BONE_SOUP);
    public static final Item SEAFOOD_MISO_SOUP = new BowlFoodOnlyItem(itemProperties("seafood_miso_soup"), ModFoods.SEAFOOD_MISO_SOUP);
    public static final Item FEARSOME_THICK_SOUP = new BowlFoodOnlyItem(itemProperties("fearsome_thick_soup"), ModFoods.FEARSOME_THICK_SOUP);
    public static final Item LAMB_AND_RADISH_SOUP = new BowlFoodOnlyItem(itemProperties("lamb_and_radish_soup"), ModFoods.LAMB_AND_RADISH_SOUP);
    public static final Item BRAISED_BEEF_WITH_POTATOES = new BowlFoodOnlyItem(itemProperties("braised_beef_with_potatoes"), ModFoods.BRAISED_BEEF_WITH_POTATOES);
    public static final Item WILD_MUSHROOM_RABBIT_SOUP = new BowlFoodOnlyItem(itemProperties("wild_mushroom_rabbit_soup"), ModFoods.WILD_MUSHROOM_RABBIT_SOUP);
    public static final Item TOMATO_BEEF_BRISKET_SOUP = new BowlFoodOnlyItem(itemProperties("tomato_beef_brisket_soup"), ModFoods.TOMATO_BEEF_BRISKET_SOUP);
    public static final Item PUFFERFISH_SOUP = new BowlFoodOnlyItem(itemProperties("pufferfish_soup"), ModFoods.PUFFERFISH_SOUP);
    public static final Item BORSCHT = new BowlFoodOnlyItem(itemProperties("borscht"), ModFoods.BORSCHT);
    public static final Item BEEF_MEATBALL_SOUP = new BowlFoodOnlyItem(itemProperties("beef_meatball_soup"), ModFoods.BEEF_MEATBALL_SOUP);
    public static final Item CHICKEN_AND_MUSHROOM_STEW = new BowlFoodOnlyItem(itemProperties("chicken_and_mushroom_stew"), ModFoods.CHICKEN_AND_MUSHROOM_STEW);
    public static final Item DONKEY_SOUP = new BowlFoodOnlyItem(itemProperties("donkey_soup"), ModFoods.DONKEY_SOUP);
    public static final Item BEEF_NOODLE = new BowlFoodOnlyItem(itemProperties("beef_noodle"), ModFoods.BEEF_NOODLE);
    public static final Item HUI_NOODLE = new BowlFoodOnlyItem(itemProperties("hui_noodle"), ModFoods.HUI_NOODLE);
    public static final Item UDON_NOODLE = new BowlFoodOnlyItem(itemProperties("udon_noodle"), ModFoods.UDON_NOODLE);

    // Raw and cooked foods
    public static final Item SASHIMI = new Item(ModFoods.applyFood(itemProperties("sashimi"), ModFoods.SASHIMI));
    public static final Item RAW_LAMB_CHOPS = new Item(ModFoods.applyFood(itemProperties("raw_lamb_chops"), ModFoods.RAW_LAMB_CHOPS));
    public static final Item RAW_COW_OFFAL = new Item(ModFoods.applyFood(itemProperties("raw_cow_offal"), ModFoods.RAW_COW_OFFAL));
    public static final Item RAW_PORK_BELLY = new Item(ModFoods.applyFood(itemProperties("raw_pork_belly"), ModFoods.RAW_PORK_BELLY));
    public static final Item RAW_DONKEY_MEAT = new Item(ModFoods.applyFood(itemProperties("raw_donkey_meat"), ModFoods.RAW_DONKEY_MEAT));
    public static final Item RAW_CUT_SMALL_MEATS = new Item(ModFoods.applyFood(itemProperties("raw_cut_small_meats"), ModFoods.RAW_CUT_SMALL_MEATS));
    public static final Item RAW_MEATBALL = new Item(ModFoods.applyFood(itemProperties("raw_meatball"), ModFoods.RAW_MEATBALL));
    public static final Item COOKED_LAMB_CHOPS = new Item(ModFoods.applyFood(itemProperties("cooked_lamb_chops"), ModFoods.COOKED_LAMB_CHOPS));
    public static final Item COOKED_COW_OFFAL = new Item(ModFoods.applyFood(itemProperties("cooked_cow_offal"), ModFoods.COOKED_COW_OFFAL));
    public static final Item COOKED_PORK_BELLY = new Item(ModFoods.applyFood(itemProperties("cooked_pork_belly"), ModFoods.COOKED_PORK_BELLY));
    public static final Item COOKED_DONKEY_MEAT = new Item(ModFoods.applyFood(itemProperties("cooked_donkey_meat"), ModFoods.COOKED_DONKEY_MEAT));
    public static final Item COOKED_CUT_SMALL_MEATS = new Item(ModFoods.applyFood(itemProperties("cooked_cut_small_meats"), ModFoods.COOKED_CUT_SMALL_MEATS));
    public static final Item COOKED_MEATBALL = new Item(ModFoods.applyFood(itemProperties("cooked_meatball"), ModFoods.COOKED_MEATBALL));

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static ResourceKey<Item> itemKey(String path) {
        return ResourceKey.create(Registries.ITEM, id(path));
    }

    private static Item.Properties itemProperties(String path) {
        return new Item.Properties().setId(itemKey(path));
    }

    private static Item.Properties blockItemProperties(String path) {
        return new Item.Properties().setId(itemKey(path)).useBlockDescriptionPrefix();
    }

    public static void registerItems() {
        // Block items
        Registry.register(BuiltInRegistries.ITEM, id("stove"), STOVE);
        Registry.register(BuiltInRegistries.ITEM, id("pot"), POT);
        Registry.register(BuiltInRegistries.ITEM, id("stockpot"), STOCKPOT);
        Registry.register(BuiltInRegistries.ITEM, id("stockpot_lid"), STOCKPOT_LID);
        Registry.register(BuiltInRegistries.ITEM, id("oil"), OIL);
        Registry.register(BuiltInRegistries.ITEM, id("oil_block"), OIL_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id("chopping_board"), CHOPPING_BOARD);
        Registry.register(BuiltInRegistries.ITEM, id("enamel_basin"), ENAMEL_BASIN);
        Registry.register(BuiltInRegistries.ITEM, id("kitchenware_racks"), KITCHENWARE_RACKS);
        Registry.register(BuiltInRegistries.ITEM, id("chili_ristra"), CHILI_RISTRA);
        Registry.register(BuiltInRegistries.ITEM, id("strung_mushrooms"), STRUNG_MUSHROOMS);
        Registry.register(BuiltInRegistries.ITEM, id("straw_block"), STRAW_BLOCK);
        Registry.register(BuiltInRegistries.ITEM, id("shawarma_spit"), SHAWARMA_SPIT);
        Registry.register(BuiltInRegistries.ITEM, id("steamer"), STEAMER);
        Registry.register(BuiltInRegistries.ITEM, id("millstone"), MILLSTONE);
        Registry.register(BuiltInRegistries.ITEM, id("oil_pot"), OIL_POT);
        Registry.register(BuiltInRegistries.ITEM, id("cold_cut_ham_slices"), COLD_CUT_HAM_SLICES);


        // Tools
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "iron_kitchen_knife"), IRON_KITCHEN_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "gold_kitchen_knife"), GOLD_KITCHEN_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "diamond_kitchen_knife"), DIAMOND_KITCHEN_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "netherite_kitchen_knife"), NETHERITE_KITCHEN_KNIFE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "sickle"), SICKLE);

        // Special items
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_item"), RECIPE_ITEM);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchen_shovel"), KITCHEN_SHOVEL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fruit_basket"), FRUIT_BASKET);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "scarecrow"), SCARECROW);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "straw_hat"), STRAW_HAT);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "straw_hat_flower"), STRAW_HAT_FLOWER);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "farmer_chest_plate"), FARMER_CHEST_PLATE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "farmer_leggings"), FARMER_LEGGINGS);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "farmer_boots"), FARMER_BOOTS);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "transmutation_lunch_bag"), TRANSMUTATION_LUNCH_BAG);

        // Seeds
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tomato_seed"), TOMATO_SEED);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chili_seed"), CHILI_SEED);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "lettuce_seed"), LETTUCE_SEED);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "rice"), RICE_SEED);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "wild_rice"), WILD_RICE_SEED);

        // Cook stools
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_oak"), COOK_STOOL_OAK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_spruce"), COOK_STOOL_SPRUCE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_acacia"), COOK_STOOL_ACACIA);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_bamboo"), COOK_STOOL_BAMBOO);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_birch"), COOK_STOOL_BIRCH);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_cherry"), COOK_STOOL_CHERRY);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_crimson"), COOK_STOOL_CRIMSON);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_dark_oak"), COOK_STOOL_DARK_OAK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_jungle"), COOK_STOOL_JUNGLE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_mangrove"), COOK_STOOL_MANGROVE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cook_stool_warped"), COOK_STOOL_WARPED);

        // Chairs
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_oak"), CHAIR_OAK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_spruce"), CHAIR_SPRUCE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_acacia"), CHAIR_ACACIA);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_bamboo"), CHAIR_BAMBOO);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_birch"), CHAIR_BIRCH);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_cherry"), CHAIR_CHERRY);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_crimson"), CHAIR_CRIMSON);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_dark_oak"), CHAIR_DARK_OAK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_jungle"), CHAIR_JUNGLE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_mangrove"), CHAIR_MANGROVE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chair_warped"), CHAIR_WARPED);

        // Tables
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_oak"), TABLE_OAK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_spruce"), TABLE_SPRUCE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_acacia"), TABLE_ACACIA);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_bamboo"), TABLE_BAMBOO);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_birch"), TABLE_BIRCH);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_cherry"), TABLE_CHERRY);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_crimson"), TABLE_CRIMSON);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_dark_oak"), TABLE_DARK_OAK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_jungle"), TABLE_JUNGLE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_mangrove"), TABLE_MANGROVE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table_warped"), TABLE_WARPED);

        // Food items
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tomato"), TOMATO);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "red_chili"), RED_CHILI);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "green_chili"), GREEN_CHILI);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "lettuce"), LETTUCE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "rice_panicle"), RICE_PANICLE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "caterpillar"), CATERPILLAR);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fried_egg"), FRIED_EGG);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "donkey_burger"), DONKEY_BURGER);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "baozi"), BAOZI);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "mantou"), MANTOU);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "samsa"), SAMSA);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "dumpling"), DUMPLING);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "meat_pie"), MEAT_PIE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_dough"), RAW_DOUGH);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flour"), FLOUR);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_noodles"), RAW_NOODLES);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stuffed_dough_food"), STUFFED_DOUGH_FOOD);

        // Bowl foods
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cooked_rice"), COOKED_RICE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "scramble_egg_with_tomatoes"), SCRAMBLE_EGG_WITH_TOMATOES);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "scramble_egg_with_tomatoes_rice_bowl"), SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stir_fried_beef_offal"), STIR_FRIED_BEEF_OFFAL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stir_fried_beef_offal_rice_bowl"), STIR_FRIED_BEEF_OFFAL_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "braised_beef"), BRAISED_BEEF);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "braised_beef_rice_bowl"), BRAISED_BEEF_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stir_fried_pork_with_peppers"), STIR_FRIED_PORK_WITH_PEPPERS);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stir_fried_pork_with_peppers_rice_bowl"), STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "sweet_and_sour_pork"), SWEET_AND_SOUR_PORK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "sweet_and_sour_pork_rice_bowl"), SWEET_AND_SOUR_PORK_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "country_style_mixed_vegetables"), COUNTRY_STYLE_MIXED_VEGETABLES);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fish_flavored_shredded_pork"), FISH_FLAVORED_SHREDDED_PORK);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fish_flavored_shredded_pork_rice_bowl"), FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "braised_fish_rice_bowl"), BRAISED_FISH_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "spicy_chicken_rice_bowl"), SPICY_CHICKEN_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "suspicious_stir_fry_rice_bowl"), SUSPICIOUS_STIR_FRY_RICE_BOWL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "egg_fried_rice"), EGG_FRIED_RICE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "delicious_egg_fried_rice"), DELICIOUS_EGG_FRIED_RICE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pork_bone_soup"), PORK_BONE_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "seafood_miso_soup"), SEAFOOD_MISO_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fearsome_thick_soup"), FEARSOME_THICK_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "lamb_and_radish_soup"), LAMB_AND_RADISH_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "braised_beef_with_potatoes"), BRAISED_BEEF_WITH_POTATOES);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "wild_mushroom_rabbit_soup"), WILD_MUSHROOM_RABBIT_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "tomato_beef_brisket_soup"), TOMATO_BEEF_BRISKET_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pufferfish_soup"), PUFFERFISH_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "borscht"), BORSCHT);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "beef_meatball_soup"), BEEF_MEATBALL_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chicken_and_mushroom_stew"), CHICKEN_AND_MUSHROOM_STEW);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "donkey_soup"), DONKEY_SOUP);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "beef_noodle"), BEEF_NOODLE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "hui_noodle"), HUI_NOODLE);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "udon_noodle"), UDON_NOODLE);

        // Raw and cooked foods
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "sashimi"), SASHIMI);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_lamb_chops"), RAW_LAMB_CHOPS);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_cow_offal"), RAW_COW_OFFAL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_pork_belly"), RAW_PORK_BELLY);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_donkey_meat"), RAW_DONKEY_MEAT);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_cut_small_meats"), RAW_CUT_SMALL_MEATS);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "raw_meatball"), RAW_MEATBALL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cooked_lamb_chops"), COOKED_LAMB_CHOPS);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cooked_cow_offal"), COOKED_COW_OFFAL);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cooked_pork_belly"), COOKED_PORK_BELLY);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cooked_donkey_meat"), COOKED_DONKEY_MEAT);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cooked_cut_small_meats"), COOKED_CUT_SMALL_MEATS);
        Registry.register(BuiltInRegistries.ITEM, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "cooked_meatball"), COOKED_MEATBALL);
    }
}
