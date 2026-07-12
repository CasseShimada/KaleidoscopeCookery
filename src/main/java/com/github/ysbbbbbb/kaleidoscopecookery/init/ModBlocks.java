package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.BaseCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.ChiliCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.LettuceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.crop.RiceCropBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.ChairBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.CookStoolBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.FruitBasketBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.StackableFoodBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.EmptyCupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.*;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.ChiliRistraBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.OilBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.RecipeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.StrawBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.StrungMushroomsBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.TrashCanBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.ChairBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.food.FoodBiteThreeByThreeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.*;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.shapes.Shapes;

public final class ModBlocks {
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
    public static final Block TEAPOT = new TeapotBlock(blockProperties("teapot"));
    public static final Block EMPTY_CUP = new EmptyCupBlock(blockProperties("empty_cup"));
    public static final Block TRASH_CAN = new TrashCanBlock(blockProperties("trash_can"));
    public static final Block RECIPE_BLOCK = new RecipeBlock(blockProperties("recipe_block"));
    public static final Block OIL_POT = new OilPotBlock(blockProperties("oil_pot"));
    public static final Block COLD_CUT_HAM_SLICES = new FoodBiteThreeByThreeBlock(
            blockProperties("cold_cut_ham_slices"), ModFoods.COLD_CUT_HAM_SLICES_BLOCK, 8, null);
    public static final Block BAMBOO_TUBE_RICE_BLOCK = new StackableFoodBlock(
            blockProperties("bamboo_tube_rice"), 4, () -> ModItems.BAMBOO_TUBE_RICE,
            Block.box(4, 0, 4, 12, 10, 12),
            Shapes.or(Block.box(7, 0, 1, 15, 10, 9), Block.box(1, 0, 7, 9, 10, 15)),
            Shapes.or(Block.box(0, 0, 6, 16, 10, 15), Block.box(4, 0, 0, 12, 10, 15)),
            Block.box(0, 0, 0, 16, 10, 16));

    // Crop blocks
    public static final Block TOMATO_CROP = new BaseCropBlock(
            blockProperties("tomato_crop"), () -> ModItems.TOMATO_SEED, ModLootTables.HARVEST_TOMATO_CROP);
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
    public static final BlockEntityType<TeapotBlockEntity> TEAPOT_BE = FabricBlockEntityTypeBuilder.create(TeapotBlockEntity::new, TEAPOT).build();
    public static final BlockEntityType<TrashCanBlockEntity> TRASH_CAN_BE = FabricBlockEntityTypeBuilder.create(TrashCanBlockEntity::new, TRASH_CAN).build();
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

    private ModBlocks() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static ResourceKey<Block> blockKey(String path) {
        return ResourceKey.create(Registries.BLOCK, id(path));
    }

    private static BlockBehaviour.Properties blockProperties(String path) {
        return BlockBehaviour.Properties.of().setId(blockKey(path));
    }

    private static void registerBlock(String path, Block block) {
        registerBlock(id(path), block);
    }

    private static void registerBlock(Identifier id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static void registerBlockEntity(String path, BlockEntityType<?> blockEntityType) {
        Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, id(path), blockEntityType);
    }

    public static void registerBlocks() {
        // Kitchen blocks
        registerBlock("stove", STOVE);
        registerBlock("pot", POT);
        registerBlock("stockpot", STOCKPOT);
        registerBlock("fruit_basket", FRUIT_BASKET);
        registerBlock("chopping_board", CHOPPING_BOARD);
        registerBlock("oil_block", OIL_BLOCK);
        registerBlock("enamel_basin", ENAMEL_BASIN);
        registerBlock("kitchenware_racks", KITCHENWARE_RACKS);
        registerBlock("chili_ristra", CHILI_RISTRA);
        registerBlock("strung_mushrooms", STRUNG_MUSHROOMS);
        registerBlock("straw_block", STRAW_BLOCK);
        registerBlock("shawarma_spit", SHAWARMA_SPIT);
        registerBlock("steamer", STEAMER);
        registerBlock("teapot", TEAPOT);
        registerBlock("empty_cup", EMPTY_CUP);
        registerBlock("trash_can", TRASH_CAN);
        registerBlock("millstone", MILLSTONE);
        registerBlock("recipe_block", RECIPE_BLOCK);
        registerBlock("oil_pot", OIL_POT);
        registerBlock("cold_cut_ham_slices", COLD_CUT_HAM_SLICES);
        registerBlock("bamboo_tube_rice", BAMBOO_TUBE_RICE_BLOCK);

        TeacupRegistry.forEachData((id, data) -> {
            TeacupBlock block = new TeacupBlock(blockProperties(id.getPath()), data.getMaxCount());
            if (data.getAABB() != null) {
                block.setAABB(data.getAABB());
            }
            registerBlock(id, block);
        });

        // Crop blocks
        registerBlock("tomato_crop", TOMATO_CROP);
        registerBlock("chili_crop", CHILI_CROP);
        registerBlock("lettuce_crop", LETTUCE_CROP);
        registerBlock("rice_crop", RICE_CROP);

        // Cook stools
        registerBlock("cook_stool_oak", COOK_STOOL_OAK);
        registerBlock("cook_stool_spruce", COOK_STOOL_SPRUCE);
        registerBlock("cook_stool_acacia", COOK_STOOL_ACACIA);
        registerBlock("cook_stool_bamboo", COOK_STOOL_BAMBOO);
        registerBlock("cook_stool_birch", COOK_STOOL_BIRCH);
        registerBlock("cook_stool_cherry", COOK_STOOL_CHERRY);
        registerBlock("cook_stool_crimson", COOK_STOOL_CRIMSON);
        registerBlock("cook_stool_dark_oak", COOK_STOOL_DARK_OAK);
        registerBlock("cook_stool_jungle", COOK_STOOL_JUNGLE);
        registerBlock("cook_stool_mangrove", COOK_STOOL_MANGROVE);
        registerBlock("cook_stool_warped", COOK_STOOL_WARPED);

        // Chairs
        registerBlock("chair_oak", CHAIR_OAK);
        registerBlock("chair_spruce", CHAIR_SPRUCE);
        registerBlock("chair_acacia", CHAIR_ACACIA);
        registerBlock("chair_bamboo", CHAIR_BAMBOO);
        registerBlock("chair_birch", CHAIR_BIRCH);
        registerBlock("chair_cherry", CHAIR_CHERRY);
        registerBlock("chair_crimson", CHAIR_CRIMSON);
        registerBlock("chair_dark_oak", CHAIR_DARK_OAK);
        registerBlock("chair_jungle", CHAIR_JUNGLE);
        registerBlock("chair_mangrove", CHAIR_MANGROVE);
        registerBlock("chair_warped", CHAIR_WARPED);

        // Tables
        registerBlock("table_oak", TABLE_OAK);
        registerBlock("table_spruce", TABLE_SPRUCE);
        registerBlock("table_acacia", TABLE_ACACIA);
        registerBlock("table_bamboo", TABLE_BAMBOO);
        registerBlock("table_birch", TABLE_BIRCH);
        registerBlock("table_cherry", TABLE_CHERRY);
        registerBlock("table_crimson", TABLE_CRIMSON);
        registerBlock("table_dark_oak", TABLE_DARK_OAK);
        registerBlock("table_jungle", TABLE_JUNGLE);
        registerBlock("table_mangrove", TABLE_MANGROVE);
        registerBlock("table_warped", TABLE_WARPED);

        // Block entities
        registerBlockEntity("pot", POT_BE);
        registerBlockEntity("stockpot", STOCKPOT_BE);
        registerBlockEntity("fruit_basket", FRUIT_BASKET_BE);
        registerBlockEntity("chopping_board", CHOPPING_BOARD_BE);
        registerBlockEntity("kitchenware_racks", KITCHENWARE_RACKS_BE);
        registerBlockEntity("shawarma_spit", SHAWARMA_SPIT_BE);
        registerBlockEntity("chair", CHAIR_BE);
        registerBlockEntity("table", TABLE_BE);
        registerBlockEntity("steamer", STEAMER_BE);
        registerBlockEntity("teapot", TEAPOT_BE);
        registerBlockEntity("trash_can", TRASH_CAN_BE);
        registerBlockEntity("millstone", MILLSTONE_BE);
        // Keep this legacy block entity id for existing world save compatibility.
        registerBlockEntity("recipe_book", RECIPE_BLOCK_BE);
        registerBlockEntity("oil_pot", OIL_POT_BE);
        registerBlockEntity("food_bite_three_by_three", FOOD_BITE_THREE_BY_THREE_BE);
    }
}
