package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.ChiliCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.LettuceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.ChairBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.CookStoolBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.FruitBasketBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.ChiliRistraBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.OilBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.RecipeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.StrawBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.StrungMushroomsBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.ChairBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.*;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntityType;

public class ModBlocks {
    // Kitchen blocks
    public static final Block STOVE = new StoveBlock(blockProperties("stove"));
    public static final Block POT = new PotBlock(blockProperties("pot"));
    public static final Block STOCKPOT = new StockpotBlock(blockProperties("stockpot"));
    public static final Block FRUIT_BASKET = new FruitBasketBlock(blockProperties("fruit_basket"));
    public static final Block CHOPPING_BOARD = new ChoppingBoardBlock(blockProperties("chopping_board"));
    public static final Block OIL_BLOCK = new OilBlock(blockProperties("oil_block"));
    public static final Block ENAMEL_BASIN = new EnamelBasinBlock(blockProperties("enamel_basin"));
    public static final Block KITCHENWARE_RACKS = new KitchenwareRacksBlock(blockProperties("kitchenware_racks"));
    public static final Block CHILI_RISTRA = new ChiliRistraBlock(blockProperties("chili_ristra"));
    public static final Block STRUNG_MUSHROOMS = new StrungMushroomsBlock(blockProperties("strung_mushrooms"));
    public static final Block STRAW_BLOCK = new StrawBlocks(blockProperties("straw_block"));
    public static final Block SHAWARMA_SPIT = new ShawarmaSpitBlock(blockProperties("shawarma_spit"));
    public static final Block MILLSTONE = new MillstoneBlock(blockProperties("millstone"));
    public static final Block STEAMER = new SteamerBlock(blockProperties("steamer"));
    public static final Block RECIPE_BLOCK = new RecipeBlock(blockProperties("recipe_block"));
    public static final Block OIL_POT = new OilPotBlock(blockProperties("oil_pot"));
    public static final Block COLD_CUT_HAM_SLICES = new FoodBiteThreeByThreeBlock(
            blockProperties("cold_cut_ham_slices"), ModFoods.COLD_CUT_HAM_SLICES_BLOCK, 8, null);

    // Crop blocks
    public static final Block TOMATO_CROP = new BaseCropBlock(blockProperties("tomato_crop"), () -> ModItems.TOMATO, () -> ModItems.TOMATO_SEED);
    public static final Block CHILI_CROP = new ChiliCropBlock(blockProperties("chili_crop"));
    public static final Block LETTUCE_CROP = new LettuceCropBlock(blockProperties("lettuce_crop"));
    public static final Block RICE_CROP = new RiceCropBlock(blockProperties("rice_crop"));

    // Cook stools
    public static final Block COOK_STOOL_OAK = new CookStoolBlock(blockProperties("cook_stool_oak"));
    public static final Block COOK_STOOL_SPRUCE = new CookStoolBlock(blockProperties("cook_stool_spruce"));
    public static final Block COOK_STOOL_ACACIA = new CookStoolBlock(blockProperties("cook_stool_acacia"));
    public static final Block COOK_STOOL_BAMBOO = new CookStoolBlock(blockProperties("cook_stool_bamboo"));
    public static final Block COOK_STOOL_BIRCH = new CookStoolBlock(blockProperties("cook_stool_birch"));
    public static final Block COOK_STOOL_CHERRY = new CookStoolBlock(blockProperties("cook_stool_cherry"));
    public static final Block COOK_STOOL_CRIMSON = new CookStoolBlock(blockProperties("cook_stool_crimson"));
    public static final Block COOK_STOOL_DARK_OAK = new CookStoolBlock(blockProperties("cook_stool_dark_oak"));
    public static final Block COOK_STOOL_JUNGLE = new CookStoolBlock(blockProperties("cook_stool_jungle"));
    public static final Block COOK_STOOL_MANGROVE = new CookStoolBlock(blockProperties("cook_stool_mangrove"));
    public static final Block COOK_STOOL_WARPED = new CookStoolBlock(blockProperties("cook_stool_warped"));

    // Chairs
    public static final Block CHAIR_OAK = new ChairBlock(blockProperties("chair_oak"));
    public static final Block CHAIR_SPRUCE = new ChairBlock(blockProperties("chair_spruce"));
    public static final Block CHAIR_ACACIA = new ChairBlock(blockProperties("chair_acacia"));
    public static final Block CHAIR_BAMBOO = new ChairBlock(blockProperties("chair_bamboo"));
    public static final Block CHAIR_BIRCH = new ChairBlock(blockProperties("chair_birch"));
    public static final Block CHAIR_CHERRY = new ChairBlock(blockProperties("chair_cherry"));
    public static final Block CHAIR_CRIMSON = new ChairBlock(blockProperties("chair_crimson"));
    public static final Block CHAIR_DARK_OAK = new ChairBlock(blockProperties("chair_dark_oak"));
    public static final Block CHAIR_JUNGLE = new ChairBlock(blockProperties("chair_jungle"));
    public static final Block CHAIR_MANGROVE = new ChairBlock(blockProperties("chair_mangrove"));
    public static final Block CHAIR_WARPED = new ChairBlock(blockProperties("chair_warped"));

    // Tables
    public static final Block TABLE_OAK = new TableBlock(blockProperties("table_oak"));
    public static final Block TABLE_SPRUCE = new TableBlock(blockProperties("table_spruce"));
    public static final Block TABLE_ACACIA = new TableBlock(blockProperties("table_acacia"));
    public static final Block TABLE_BAMBOO = new TableBlock(blockProperties("table_bamboo"));
    public static final Block TABLE_BIRCH = new TableBlock(blockProperties("table_birch"));
    public static final Block TABLE_CHERRY = new TableBlock(blockProperties("table_cherry"));
    public static final Block TABLE_CRIMSON = new TableBlock(blockProperties("table_crimson"));
    public static final Block TABLE_DARK_OAK = new TableBlock(blockProperties("table_dark_oak"));
    public static final Block TABLE_JUNGLE = new TableBlock(blockProperties("table_jungle"));
    public static final Block TABLE_MANGROVE = new TableBlock(blockProperties("table_mangrove"));
    public static final Block TABLE_WARPED = new TableBlock(blockProperties("table_warped"));

    // Block entities
    public static final BlockEntityType<PotBlockEntity> POT_BE = FabricBlockEntityTypeBuilder.create(PotBlockEntity::new, POT).build();
    public static final BlockEntityType<StockpotBlockEntity> STOCKPOT_BE = FabricBlockEntityTypeBuilder.create(StockpotBlockEntity::new, STOCKPOT).build();
    public static final BlockEntityType<FruitBasketBlockEntity> FRUIT_BASKET_BE = FabricBlockEntityTypeBuilder.create(FruitBasketBlockEntity::new, FRUIT_BASKET).build();
    public static final BlockEntityType<ChoppingBoardBlockEntity> CHOPPING_BOARD_BE = FabricBlockEntityTypeBuilder.create(ChoppingBoardBlockEntity::new, CHOPPING_BOARD).build();
    public static final BlockEntityType<KitchenwareRacksBlockEntity> KITCHENWARE_RACKS_BE = FabricBlockEntityTypeBuilder.create(KitchenwareRacksBlockEntity::new, KITCHENWARE_RACKS).build();
    public static final BlockEntityType<ShawarmaSpitBlockEntity> SHAWARMA_SPIT_BE = FabricBlockEntityTypeBuilder.create(ShawarmaSpitBlockEntity::new, SHAWARMA_SPIT).build();
    public static final BlockEntityType<SteamerBlockEntity> STEAMER_BE = FabricBlockEntityTypeBuilder.create(SteamerBlockEntity::new, STEAMER).build();
    public static final BlockEntityType<MillstoneBlockEntity> MILLSTONE_BE = FabricBlockEntityTypeBuilder.create(MillstoneBlockEntity::new, MILLSTONE).build();
    public static final BlockEntityType<RecipeBlockEntity> RECIPE_BLOCK_BE = FabricBlockEntityTypeBuilder.create(RecipeBlockEntity::new, RECIPE_BLOCK).build();
    public static final BlockEntityType<OilPotBlockEntity> OIL_POT_BE = FabricBlockEntityTypeBuilder.create(OilPotBlockEntity::new, OIL_POT).build();
    public static final BlockEntityType<FoodBiteThreeByThreeBlockEntity> FOOD_BITE_THREE_BY_THREE_BE =
            FabricBlockEntityTypeBuilder.create(FoodBiteThreeByThreeBlockEntity::new, COLD_CUT_HAM_SLICES).build();

    public static final BlockEntityType<ChairBlockEntity> CHAIR_BE = FabricBlockEntityTypeBuilder.create(ChairBlockEntity::new,
            CHAIR_OAK, CHAIR_SPRUCE, CHAIR_ACACIA, CHAIR_BAMBOO,
            CHAIR_BIRCH, CHAIR_CHERRY, CHAIR_CRIMSON, CHAIR_DARK_OAK,
            CHAIR_JUNGLE, CHAIR_MANGROVE, CHAIR_WARPED
    ).build();

    public static final BlockEntityType<TableBlockEntity> TABLE_BE = FabricBlockEntityTypeBuilder.create(TableBlockEntity::new,
            TABLE_OAK, TABLE_SPRUCE, TABLE_ACACIA, TABLE_BAMBOO,
            TABLE_BIRCH, TABLE_CHERRY, TABLE_CRIMSON, TABLE_DARK_OAK,
            TABLE_JUNGLE, TABLE_MANGROVE, TABLE_WARPED
    ).build();

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static ResourceKey<Block> blockKey(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    }

    private static BlockBehaviour.Properties blockProperties(String path) {
        return BlockBehaviour.Properties.of().setId(blockKey(path));
    }

    public static void registerBlocks() {
        // Kitchen blocks
        Registry.register(BuiltInRegistries.BLOCK, id("stove"), STOVE);
        Registry.register(BuiltInRegistries.BLOCK, id("pot"), POT);
        Registry.register(BuiltInRegistries.BLOCK, id("stockpot"), STOCKPOT);
        Registry.register(BuiltInRegistries.BLOCK, id("fruit_basket"), FRUIT_BASKET);
        Registry.register(BuiltInRegistries.BLOCK, id("chopping_board"), CHOPPING_BOARD);
        Registry.register(BuiltInRegistries.BLOCK, id("oil_block"), OIL_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, id("enamel_basin"), ENAMEL_BASIN);
        Registry.register(BuiltInRegistries.BLOCK, id("kitchenware_racks"), KITCHENWARE_RACKS);
        Registry.register(BuiltInRegistries.BLOCK, id("chili_ristra"), CHILI_RISTRA);
        Registry.register(BuiltInRegistries.BLOCK, id("strung_mushrooms"), STRUNG_MUSHROOMS);
        Registry.register(BuiltInRegistries.BLOCK, id("straw_block"), STRAW_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, id("shawarma_spit"), SHAWARMA_SPIT);
        Registry.register(BuiltInRegistries.BLOCK, id("steamer"), STEAMER);
        Registry.register(BuiltInRegistries.BLOCK, id("millstone"), MILLSTONE);
        Registry.register(BuiltInRegistries.BLOCK, id("recipe_block"), RECIPE_BLOCK);
        Registry.register(BuiltInRegistries.BLOCK, id("oil_pot"), OIL_POT);
        Registry.register(BuiltInRegistries.BLOCK, id("cold_cut_ham_slices"), COLD_CUT_HAM_SLICES);

        // Crop blocks
        Registry.register(BuiltInRegistries.BLOCK, id("tomato_crop"), TOMATO_CROP);
        Registry.register(BuiltInRegistries.BLOCK, id("chili_crop"), CHILI_CROP);
        Registry.register(BuiltInRegistries.BLOCK, id("lettuce_crop"), LETTUCE_CROP);
        Registry.register(BuiltInRegistries.BLOCK, id("rice_crop"), RICE_CROP);

        // Cook stools
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_oak"), COOK_STOOL_OAK);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_spruce"), COOK_STOOL_SPRUCE);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_acacia"), COOK_STOOL_ACACIA);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_bamboo"), COOK_STOOL_BAMBOO);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_birch"), COOK_STOOL_BIRCH);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_cherry"), COOK_STOOL_CHERRY);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_crimson"), COOK_STOOL_CRIMSON);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_dark_oak"), COOK_STOOL_DARK_OAK);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_jungle"), COOK_STOOL_JUNGLE);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_mangrove"), COOK_STOOL_MANGROVE);
        Registry.register(BuiltInRegistries.BLOCK, id("cook_stool_warped"), COOK_STOOL_WARPED);

        // Chairs
        Registry.register(BuiltInRegistries.BLOCK, id("chair_oak"), CHAIR_OAK);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_spruce"), CHAIR_SPRUCE);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_acacia"), CHAIR_ACACIA);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_bamboo"), CHAIR_BAMBOO);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_birch"), CHAIR_BIRCH);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_cherry"), CHAIR_CHERRY);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_crimson"), CHAIR_CRIMSON);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_dark_oak"), CHAIR_DARK_OAK);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_jungle"), CHAIR_JUNGLE);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_mangrove"), CHAIR_MANGROVE);
        Registry.register(BuiltInRegistries.BLOCK, id("chair_warped"), CHAIR_WARPED);

        // Tables
        Registry.register(BuiltInRegistries.BLOCK, id("table_oak"), TABLE_OAK);
        Registry.register(BuiltInRegistries.BLOCK, id("table_spruce"), TABLE_SPRUCE);
        Registry.register(BuiltInRegistries.BLOCK, id("table_acacia"), TABLE_ACACIA);
        Registry.register(BuiltInRegistries.BLOCK, id("table_bamboo"), TABLE_BAMBOO);
        Registry.register(BuiltInRegistries.BLOCK, id("table_birch"), TABLE_BIRCH);
        Registry.register(BuiltInRegistries.BLOCK, id("table_cherry"), TABLE_CHERRY);
        Registry.register(BuiltInRegistries.BLOCK, id("table_crimson"), TABLE_CRIMSON);
        Registry.register(BuiltInRegistries.BLOCK, id("table_dark_oak"), TABLE_DARK_OAK);
        Registry.register(BuiltInRegistries.BLOCK, id("table_jungle"), TABLE_JUNGLE);
        Registry.register(BuiltInRegistries.BLOCK, id("table_mangrove"), TABLE_MANGROVE);
        Registry.register(BuiltInRegistries.BLOCK, id("table_warped"), TABLE_WARPED);

        // Block entities
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("pot"), POT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("stockpot"), STOCKPOT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("fruit_basket"), FRUIT_BASKET_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("chopping_board"), CHOPPING_BOARD_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("kitchenware_racks"), KITCHENWARE_RACKS_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("shawarma_spit"), SHAWARMA_SPIT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("chair"), CHAIR_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("table"), TABLE_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("steamer"), STEAMER_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("millstone"), MILLSTONE_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("recipe_book"), RECIPE_BLOCK_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("oil_pot"), OIL_POT_BE);
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id("food_bite_three_by_three"), FOOD_BITE_THREE_BY_THREE_BE);
    }
}
