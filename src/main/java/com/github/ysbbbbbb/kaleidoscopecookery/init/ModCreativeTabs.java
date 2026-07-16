package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.OilPotItem;
import net.fabricmc.fabric.api.creativetab.v1.FabricCreativeModeTab;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;

public final class ModCreativeTabs {
    private static final Identifier MAIN_ICON_ID = id("iron_kitchen_knife");
    private static final Identifier FOOD_ICON_ID = id("red_chili");

    private static final ResourceKey<CreativeModeTab> COOKERY_MAIN_TAB = tabKey("cookery_main");
    private static final ResourceKey<CreativeModeTab> COOKERY_FOOD_TAB = tabKey("cookery_food");

    private ModCreativeTabs() {
    }

    public static void registerTabs() {
        // Forge placed the food tab immediately before the main tab.
        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, COOKERY_FOOD_TAB, FabricCreativeModeTab.builder()
                .title(Component.translatable("item_group.kaleidoscope_cookery.cookery_food.name"))
                .icon(() -> requiredItem(FOOD_ICON_ID).getDefaultInstance())
                .displayItems((par, output) -> addFoodTabItems(output)).build());

        Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, COOKERY_MAIN_TAB, FabricCreativeModeTab.builder()
                .title(Component.translatable("item_group.kaleidoscope_cookery.cookery_main.name"))
                .icon(() -> requiredItem(MAIN_ICON_ID).getDefaultInstance())
                .displayItems((par, output) -> addMainTabItems(output)).build());
    }

    private static void addMainTabItems(CreativeModeTab.Output output) {
        output.accept(ModItems.STOVE);
        output.accept(ModItems.SHAWARMA_SPIT);
        output.accept(ModItems.STRAW_BLOCK);
        output.accept(ModItems.OIL_BLOCK);
        output.accept(ModItems.POT);
        output.accept(ModItems.STOCKPOT);
        output.accept(ModItems.STOCKPOT_LID);
        output.accept(ModItems.CHOPPING_BOARD);
        output.accept(ModItems.MILLSTONE);
        output.accept(ModItems.STEAMER);
        output.accept(ModItems.TEAPOT);
        output.accept(ModItems.TRASH_CAN);
        output.accept(ModItems.KITCHENWARE_RACKS);
        output.accept(ModItems.FRUIT_BASKET);
        output.accept(ModItems.SCARECROW);
        output.accept(ModItems.ENAMEL_BASIN);
        output.accept(ModItems.OIL_POT);
        output.accept(OilPotItem.getFullOilPot());
        output.accept(ModItems.OIL);
        output.accept(ModItems.RECIPE_ITEM);
        output.accept(ModItems.TRANSMUTATION_LUNCH_BAG);
        output.accept(ModItems.FLOUR);
        output.accept(ModItems.RAW_DOUGH);
        output.accept(ModItems.RAW_NOODLES);
        output.accept(ModItems.STUFFED_DOUGH_FOOD);
        output.accept(ModItems.RAW_ZONGZI);
        output.accept(ModItems.RAW_BAMBOO_TUBE_RICE);
        output.accept(ModItems.CHILI_RISTRA);
        output.accept(ModItems.STRUNG_MUSHROOMS);
        output.accept(ModItems.RICE_SEED);
        output.accept(ModItems.WILD_RICE_SEED);
        output.accept(ModItems.TOMATO_SEED);
        output.accept(ModItems.CHILI_SEED);
        output.accept(ModItems.LETTUCE_SEED);
        output.accept(ModItems.KITCHEN_SHOVEL);
        output.accept(ModItems.SICKLE);
        output.accept(ModItems.GOLD_KITCHEN_KNIFE);
        output.accept(ModItems.IRON_KITCHEN_KNIFE);
        output.accept(ModItems.DIAMOND_KITCHEN_KNIFE);
        output.accept(ModItems.NETHERITE_KITCHEN_KNIFE);
        output.accept(ModItems.STRAW_HAT);
        output.accept(ModItems.STRAW_HAT_FLOWER);
        output.accept(ModItems.FARMER_CHEST_PLATE);
        output.accept(ModItems.FARMER_LEGGINGS);
        output.accept(ModItems.FARMER_BOOTS);
        output.accept(ModItems.COOK_STOOL_OAK);
        output.accept(ModItems.COOK_STOOL_SPRUCE);
        output.accept(ModItems.COOK_STOOL_ACACIA);
        output.accept(ModItems.COOK_STOOL_BAMBOO);
        output.accept(ModItems.COOK_STOOL_BIRCH);
        output.accept(ModItems.COOK_STOOL_CHERRY);
        output.accept(ModItems.COOK_STOOL_CRIMSON);
        output.accept(ModItems.COOK_STOOL_DARK_OAK);
        output.accept(ModItems.COOK_STOOL_JUNGLE);
        output.accept(ModItems.COOK_STOOL_MANGROVE);
        output.accept(ModItems.COOK_STOOL_WARPED);
        output.accept(ModItems.CHAIR_OAK);
        output.accept(ModItems.CHAIR_SPRUCE);
        output.accept(ModItems.CHAIR_ACACIA);
        output.accept(ModItems.CHAIR_BAMBOO);
        output.accept(ModItems.CHAIR_BIRCH);
        output.accept(ModItems.CHAIR_CHERRY);
        output.accept(ModItems.CHAIR_CRIMSON);
        output.accept(ModItems.CHAIR_DARK_OAK);
        output.accept(ModItems.CHAIR_JUNGLE);
        output.accept(ModItems.CHAIR_MANGROVE);
        output.accept(ModItems.CHAIR_WARPED);
        output.accept(ModItems.TABLE_OAK);
        output.accept(ModItems.TABLE_SPRUCE);
        output.accept(ModItems.TABLE_ACACIA);
        output.accept(ModItems.TABLE_BAMBOO);
        output.accept(ModItems.TABLE_BIRCH);
        output.accept(ModItems.TABLE_CHERRY);
        output.accept(ModItems.TABLE_CRIMSON);
        output.accept(ModItems.TABLE_DARK_OAK);
        output.accept(ModItems.TABLE_JUNGLE);
        output.accept(ModItems.TABLE_MANGROVE);
        output.accept(ModItems.TABLE_WARPED);
    }

    private static void addFoodTabItems(CreativeModeTab.Output output) {
        output.accept(ModItems.TOMATO);
        output.accept(ModItems.RED_CHILI);
        output.accept(ModItems.GREEN_CHILI);
        output.accept(ModItems.LETTUCE);
        output.accept(ModItems.RICE_PANICLE);
        output.accept(ModItems.CATERPILLAR);
        output.accept(ModItems.SASHIMI);
        output.accept(ModItems.RAW_LAMB_CHOPS);
        output.accept(ModItems.COOKED_LAMB_CHOPS);
        output.accept(ModItems.RAW_COW_OFFAL);
        output.accept(ModItems.COOKED_COW_OFFAL);
        output.accept(ModItems.RAW_PORK_BELLY);
        output.accept(ModItems.COOKED_PORK_BELLY);
        output.accept(ModItems.RAW_CUT_SMALL_MEATS);
        output.accept(ModItems.COOKED_CUT_SMALL_MEATS);
        output.accept(ModItems.RAW_DONKEY_MEAT);
        output.accept(ModItems.COOKED_DONKEY_MEAT);
        output.accept(ModItems.RAW_MEATBALL);
        output.accept(ModItems.COOKED_MEATBALL);
        output.accept(ModItems.DONKEY_BURGER);
        output.accept(ModItems.MANTOU);
        output.accept(ModItems.BAOZI);
        output.accept(ModItems.SAMSA);
        output.accept(ModItems.MEAT_PIE);
        output.accept(ModItems.DUMPLING);
        output.accept(ModItems.QINGTUAN);
        output.accept(ModItems.STICKY_CANDY);
        output.accept(ModItems.STICKY_RICE_CAKE);
        output.accept(ModItems.ZONGZI);
        output.accept(ModItems.BAMBOO_TUBE_RICE);
        output.accept(ModItems.FRIED_EGG);
        output.accept(ModItems.COOKED_RICE);
        output.accept(ModItems.EGG_FRIED_RICE);
        output.accept(ModItems.DELICIOUS_EGG_FRIED_RICE);
        output.accept(ModItems.SCRAMBLE_EGG_WITH_TOMATOES);
        output.accept(ModItems.SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL);
        output.accept(ModItems.STIR_FRIED_BEEF_OFFAL);
        output.accept(ModItems.STIR_FRIED_BEEF_OFFAL_RICE_BOWL);
        output.accept(ModItems.BRAISED_BEEF);
        output.accept(ModItems.BRAISED_BEEF_RICE_BOWL);
        output.accept(ModItems.STIR_FRIED_PORK_WITH_PEPPERS);
        output.accept(ModItems.STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL);
        output.accept(ModItems.SWEET_AND_SOUR_PORK);
        output.accept(ModItems.SWEET_AND_SOUR_PORK_RICE_BOWL);
        output.accept(ModItems.COUNTRY_STYLE_MIXED_VEGETABLES);
        output.accept(ModItems.FISH_FLAVORED_SHREDDED_PORK);
        output.accept(ModItems.FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL);
        output.accept(ModItems.BRAISED_FISH_RICE_BOWL);
        output.accept(ModItems.SPICY_CHICKEN_RICE_BOWL);
        output.accept(ModItems.SUSPICIOUS_STIR_FRY_RICE_BOWL);
        output.accept(ModItems.PORK_BONE_SOUP);
        output.accept(ModItems.SEAFOOD_MISO_SOUP);
        output.accept(ModItems.FEARSOME_THICK_SOUP);
        output.accept(ModItems.LAMB_AND_RADISH_SOUP);
        output.accept(ModItems.BRAISED_BEEF_WITH_POTATOES);
        output.accept(ModItems.WILD_MUSHROOM_RABBIT_SOUP);
        output.accept(ModItems.TOMATO_BEEF_BRISKET_SOUP);
        output.accept(ModItems.PUFFERFISH_SOUP);
        output.accept(ModItems.BORSCHT);
        output.accept(ModItems.BEEF_MEATBALL_SOUP);
        output.accept(ModItems.CHICKEN_AND_MUSHROOM_STEW);
        output.accept(ModItems.DONKEY_SOUP);
        output.accept(ModItems.BEEF_NOODLE);
        output.accept(ModItems.HUI_NOODLE);
        output.accept(ModItems.UDON_NOODLE);
        output.accept(ModItems.HOT_DRY_NOODLES);
        output.accept(ModItems.LABA_CONGEE);

        addFoodBiteItems(output);
        addRegistryItems(output, PlateRegistry.ids());
        output.accept(ModItems.EMPTY_CUP);
        addRegistryItems(output, TeacupRegistry.ids());
    }

    private static void addFoodBiteItems(CreativeModeTab.Output output) {
        for (Identifier itemId : FoodBiteRegistry.ids()) {
            if (itemId.equals(FoodBiteRegistry.DOUGH_DROP_SOUP)) {
                output.accept(ModItems.COLD_CUT_HAM_SLICES);
            }
            output.accept(requiredItem(itemId));
        }
    }

    private static void addRegistryItems(CreativeModeTab.Output output, Iterable<Identifier> itemIds) {
        for (Identifier itemId : itemIds) {
            output.accept(requiredItem(itemId));
        }
    }

    private static Item requiredItem(Identifier itemId) {
        return BuiltInRegistries.ITEM.getOptional(itemId)
                .orElseThrow(() -> new IllegalStateException("Missing registered item for creative tab: " + itemId));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static ResourceKey<CreativeModeTab> tabKey(String path) {
        return ResourceKey.create(Registries.CREATIVE_MODE_TAB, id(path));
    }
}
