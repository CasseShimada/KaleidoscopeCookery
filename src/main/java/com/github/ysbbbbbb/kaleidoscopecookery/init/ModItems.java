package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.*;
import net.minecraft.world.item.equipment.ArmorType;
import net.minecraft.world.level.block.Block;

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
    public static final Item TEAPOT = new TeapotItem(blockItemProperties("teapot").stacksTo(1));
    public static final Item EMPTY_CUP = new EmptyCupItem(blockItemProperties("empty_cup").stacksTo(16));
    public static final Item TRASH_CAN = new BlockItem(ModBlocks.TRASH_CAN, blockItemProperties("trash_can"));
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
    public static final Item RECIPE_ITEM = new RecipeItem(itemProperties("recipe_item").stacksTo(1));
    public static final Item KITCHEN_SHOVEL = new KitchenShovelItem(
            itemProperties("kitchen_shovel").component(ModDataComponents.KITCHEN_SHOVEL_HAS_OIL, false));
    public static final Item FRUIT_BASKET = new FruitBasketItem(itemProperties("fruit_basket"));
    public static final Item SCARECROW = new ScarecrowItem(itemProperties("scarecrow"));
    public static final Item STRAW_HAT = new StrawHatItem(false, itemProperties("straw_hat"));
    public static final Item STRAW_HAT_FLOWER = new StrawHatItem(true, itemProperties("straw_hat_flower"));
    public static final Item FARMER_CHEST_PLATE = new ModArmorItem(ModArmorMaterials.FARMER, ArmorType.CHESTPLATE, itemProperties("farmer_chest_plate").stacksTo(1));
    public static final Item FARMER_LEGGINGS = new ModArmorItem(ModArmorMaterials.FARMER, ArmorType.LEGGINGS, itemProperties("farmer_leggings").stacksTo(1));
    public static final Item FARMER_BOOTS = new ModArmorItem(ModArmorMaterials.FARMER, ArmorType.BOOTS, itemProperties("farmer_boots").stacksTo(1));
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
    public static final Item QINGTUAN = new FoodWithEffectsItem(itemProperties("qingtuan"), ModFoods.QINGTUAN);
    public static final Item STICKY_CANDY = new FoodWithEffectsItem(itemProperties("sticky_candy"), ModFoods.STICKY_CANDY);
    public static final Item STICKY_RICE_CAKE = new FoodWithEffectsItem(itemProperties("sticky_rice_cake"), ModFoods.STICKY_RICE_CAKE);
    public static final Item ZONGZI = new FoodWithEffectsItem(itemProperties("zongzi"), ModFoods.ZONGZI);
    public static final Item RAW_ZONGZI = new Item(itemProperties("raw_zongzi"));
    public static final Item RAW_BAMBOO_TUBE_RICE = new Item(ModFoods.applyFood(itemProperties("raw_bamboo_tube_rice"), ModFoods.RAW_BAMBOO_TUBE_RICE));
    public static final Item BAMBOO_TUBE_RICE = new BambooTubeRiceBlockItem(
            ModBlocks.BAMBOO_TUBE_RICE_BLOCK, blockItemProperties("bamboo_tube_rice").usingConvertsTo(Items.BAMBOO), ModFoods.BAMBOO_TUBE_RICE);
    public static final Item RAW_DOUGH = new RawDoughItem(itemProperties("raw_dough"));
    public static final Item FLOUR = new FlourItem(itemProperties("flour"));
    public static final Item RAW_NOODLES = new Item(itemProperties("raw_noodles"));
    public static final Item STUFFED_DOUGH_FOOD = new Item(itemProperties("stuffed_dough_food"));

    // Bowl foods
    public static final Item COOKED_RICE = new BowlFoodOnlyItem(itemProperties("cooked_rice").craftRemainder(Items.BOWL), ModFoods.COOKED_RICE);
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
    public static final Item HOT_DRY_NOODLES = new BowlFoodOnlyItem(itemProperties("hot_dry_noodles"), ModFoods.HOT_DRY_NOODLES);
    public static final Item LABA_CONGEE = new BowlFoodOnlyItem(itemProperties("laba_congee"), ModFoods.LABA_CONGEE);

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

    private ModItems() {
    }

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

    private static void register(String path, Item item) {
        register(id(path), item);
    }

    private static void register(Identifier id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    public static void registerItems() {
        // Block items
        register("stove", STOVE);
        register("pot", POT);
        register("stockpot", STOCKPOT);
        register("stockpot_lid", STOCKPOT_LID);
        register("oil", OIL);
        register("oil_block", OIL_BLOCK);
        register("chopping_board", CHOPPING_BOARD);
        register("enamel_basin", ENAMEL_BASIN);
        register("kitchenware_racks", KITCHENWARE_RACKS);
        register("chili_ristra", CHILI_RISTRA);
        register("strung_mushrooms", STRUNG_MUSHROOMS);
        register("straw_block", STRAW_BLOCK);
        register("shawarma_spit", SHAWARMA_SPIT);
        register("steamer", STEAMER);
        register("teapot", TEAPOT);
        register("empty_cup", EMPTY_CUP);
        register("trash_can", TRASH_CAN);
        register("millstone", MILLSTONE);
        register("oil_pot", OIL_POT);
        register("cold_cut_ham_slices", COLD_CUT_HAM_SLICES);

        TeacupRegistry.forEachData((id, data) -> {
            Block block = BuiltInRegistries.BLOCK.getOptional(id)
                    .orElseThrow(() -> new IllegalStateException("Missing registered teacup block: " + id));
            if (!(block instanceof TeacupBlock teacupBlock)) {
                throw new IllegalStateException("Registered teacup block has unexpected type: " + id);
            }
            register(id, new TeacupItem(teacupBlock,
                    blockItemProperties(id.getPath()).stacksTo(16).usingConvertsTo(EMPTY_CUP), data.getEffects()));
        });


        // Tools
        register("iron_kitchen_knife", IRON_KITCHEN_KNIFE);
        register("gold_kitchen_knife", GOLD_KITCHEN_KNIFE);
        register("diamond_kitchen_knife", DIAMOND_KITCHEN_KNIFE);
        register("netherite_kitchen_knife", NETHERITE_KITCHEN_KNIFE);
        register("sickle", SICKLE);

        // Special items
        register("recipe_item", RECIPE_ITEM);
        register("kitchen_shovel", KITCHEN_SHOVEL);
        register("fruit_basket", FRUIT_BASKET);
        register("scarecrow", SCARECROW);
        register("straw_hat", STRAW_HAT);
        register("straw_hat_flower", STRAW_HAT_FLOWER);
        register("farmer_chest_plate", FARMER_CHEST_PLATE);
        register("farmer_leggings", FARMER_LEGGINGS);
        register("farmer_boots", FARMER_BOOTS);
        register("transmutation_lunch_bag", TRANSMUTATION_LUNCH_BAG);

        // Seeds
        register("tomato_seed", TOMATO_SEED);
        register("chili_seed", CHILI_SEED);
        register("lettuce_seed", LETTUCE_SEED);
        register("rice", RICE_SEED);
        register("wild_rice", WILD_RICE_SEED);

        // Cook stools
        register("cook_stool_oak", COOK_STOOL_OAK);
        register("cook_stool_spruce", COOK_STOOL_SPRUCE);
        register("cook_stool_acacia", COOK_STOOL_ACACIA);
        register("cook_stool_bamboo", COOK_STOOL_BAMBOO);
        register("cook_stool_birch", COOK_STOOL_BIRCH);
        register("cook_stool_cherry", COOK_STOOL_CHERRY);
        register("cook_stool_crimson", COOK_STOOL_CRIMSON);
        register("cook_stool_dark_oak", COOK_STOOL_DARK_OAK);
        register("cook_stool_jungle", COOK_STOOL_JUNGLE);
        register("cook_stool_mangrove", COOK_STOOL_MANGROVE);
        register("cook_stool_warped", COOK_STOOL_WARPED);

        // Chairs
        register("chair_oak", CHAIR_OAK);
        register("chair_spruce", CHAIR_SPRUCE);
        register("chair_acacia", CHAIR_ACACIA);
        register("chair_bamboo", CHAIR_BAMBOO);
        register("chair_birch", CHAIR_BIRCH);
        register("chair_cherry", CHAIR_CHERRY);
        register("chair_crimson", CHAIR_CRIMSON);
        register("chair_dark_oak", CHAIR_DARK_OAK);
        register("chair_jungle", CHAIR_JUNGLE);
        register("chair_mangrove", CHAIR_MANGROVE);
        register("chair_warped", CHAIR_WARPED);

        // Tables
        register("table_oak", TABLE_OAK);
        register("table_spruce", TABLE_SPRUCE);
        register("table_acacia", TABLE_ACACIA);
        register("table_bamboo", TABLE_BAMBOO);
        register("table_birch", TABLE_BIRCH);
        register("table_cherry", TABLE_CHERRY);
        register("table_crimson", TABLE_CRIMSON);
        register("table_dark_oak", TABLE_DARK_OAK);
        register("table_jungle", TABLE_JUNGLE);
        register("table_mangrove", TABLE_MANGROVE);
        register("table_warped", TABLE_WARPED);

        // Food items
        register("tomato", TOMATO);
        register("red_chili", RED_CHILI);
        register("green_chili", GREEN_CHILI);
        register("lettuce", LETTUCE);
        register("rice_panicle", RICE_PANICLE);
        register("caterpillar", CATERPILLAR);
        register("fried_egg", FRIED_EGG);
        register("donkey_burger", DONKEY_BURGER);
        register("baozi", BAOZI);
        register("mantou", MANTOU);
        register("samsa", SAMSA);
        register("dumpling", DUMPLING);
        register("meat_pie", MEAT_PIE);
        register("qingtuan", QINGTUAN);
        register("sticky_candy", STICKY_CANDY);
        register("sticky_rice_cake", STICKY_RICE_CAKE);
        register("zongzi", ZONGZI);
        register("raw_zongzi", RAW_ZONGZI);
        register("raw_bamboo_tube_rice", RAW_BAMBOO_TUBE_RICE);
        register("bamboo_tube_rice", BAMBOO_TUBE_RICE);
        register("raw_dough", RAW_DOUGH);
        register("flour", FLOUR);
        register("raw_noodles", RAW_NOODLES);
        register("stuffed_dough_food", STUFFED_DOUGH_FOOD);

        // Bowl foods
        register("cooked_rice", COOKED_RICE);
        register("scramble_egg_with_tomatoes", SCRAMBLE_EGG_WITH_TOMATOES);
        register("scramble_egg_with_tomatoes_rice_bowl", SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL);
        register("stir_fried_beef_offal", STIR_FRIED_BEEF_OFFAL);
        register("stir_fried_beef_offal_rice_bowl", STIR_FRIED_BEEF_OFFAL_RICE_BOWL);
        register("braised_beef", BRAISED_BEEF);
        register("braised_beef_rice_bowl", BRAISED_BEEF_RICE_BOWL);
        register("stir_fried_pork_with_peppers", STIR_FRIED_PORK_WITH_PEPPERS);
        register("stir_fried_pork_with_peppers_rice_bowl", STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL);
        register("sweet_and_sour_pork", SWEET_AND_SOUR_PORK);
        register("sweet_and_sour_pork_rice_bowl", SWEET_AND_SOUR_PORK_RICE_BOWL);
        register("country_style_mixed_vegetables", COUNTRY_STYLE_MIXED_VEGETABLES);
        register("fish_flavored_shredded_pork", FISH_FLAVORED_SHREDDED_PORK);
        register("fish_flavored_shredded_pork_rice_bowl", FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL);
        register("braised_fish_rice_bowl", BRAISED_FISH_RICE_BOWL);
        register("spicy_chicken_rice_bowl", SPICY_CHICKEN_RICE_BOWL);
        register("suspicious_stir_fry_rice_bowl", SUSPICIOUS_STIR_FRY_RICE_BOWL);
        register("egg_fried_rice", EGG_FRIED_RICE);
        register("delicious_egg_fried_rice", DELICIOUS_EGG_FRIED_RICE);
        register("pork_bone_soup", PORK_BONE_SOUP);
        register("seafood_miso_soup", SEAFOOD_MISO_SOUP);
        register("fearsome_thick_soup", FEARSOME_THICK_SOUP);
        register("lamb_and_radish_soup", LAMB_AND_RADISH_SOUP);
        register("braised_beef_with_potatoes", BRAISED_BEEF_WITH_POTATOES);
        register("wild_mushroom_rabbit_soup", WILD_MUSHROOM_RABBIT_SOUP);
        register("tomato_beef_brisket_soup", TOMATO_BEEF_BRISKET_SOUP);
        register("pufferfish_soup", PUFFERFISH_SOUP);
        register("borscht", BORSCHT);
        register("beef_meatball_soup", BEEF_MEATBALL_SOUP);
        register("chicken_and_mushroom_stew", CHICKEN_AND_MUSHROOM_STEW);
        register("donkey_soup", DONKEY_SOUP);
        register("beef_noodle", BEEF_NOODLE);
        register("hui_noodle", HUI_NOODLE);
        register("udon_noodle", UDON_NOODLE);
        register("hot_dry_noodles", HOT_DRY_NOODLES);
        register("laba_congee", LABA_CONGEE);

        // Raw and cooked foods
        register("sashimi", SASHIMI);
        register("raw_lamb_chops", RAW_LAMB_CHOPS);
        register("raw_cow_offal", RAW_COW_OFFAL);
        register("raw_pork_belly", RAW_PORK_BELLY);
        register("raw_donkey_meat", RAW_DONKEY_MEAT);
        register("raw_cut_small_meats", RAW_CUT_SMALL_MEATS);
        register("raw_meatball", RAW_MEATBALL);
        register("cooked_lamb_chops", COOKED_LAMB_CHOPS);
        register("cooked_cow_offal", COOKED_COW_OFFAL);
        register("cooked_pork_belly", COOKED_PORK_BELLY);
        register("cooked_donkey_meat", COOKED_DONKEY_MEAT);
        register("cooked_cut_small_meats", COOKED_CUT_SMALL_MEATS);
        register("cooked_meatball", COOKED_MEATBALL);
    }
}
