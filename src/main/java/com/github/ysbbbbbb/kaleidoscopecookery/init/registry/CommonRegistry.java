package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.api.storage.MillstoneEntityItemStorage;
import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.OilPotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.resources.MillstoneBindableDataReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.transfer.ChestedHorseItemStorage;
import net.fabricmc.fabric.api.registry.CompostableRegistry;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

public final class CommonRegistry {
    private CommonRegistry() {
    }

    public static void registerContent() {
        registerFoodBiteBlocks();
        PlateRegistry.init();
        TeacupRegistry.init();
    }

    public static void registerIntegrations() {
        registerCompostables();
        registerTransferApiStorage();
        registerDispenserBehaviors();
        registerResourceReloadListeners();
    }

    private static void registerTransferApiStorage() {
        ItemStorage.SIDED.registerForBlockEntity((millstone, direction) ->
                direction == Direction.UP ? millstone.getInputStorage() : null, ModBlocks.MILLSTONE_BE);
        ItemStorage.SIDED.registerForBlockEntity((oilPot, direction) -> oilPot.getItemStorage(), ModBlocks.OIL_POT_BE);
        MillstoneEntityItemStorage.SOURCE.registerFallback((entity, context) ->
                entity instanceof net.minecraft.world.entity.animal.equine.AbstractChestedHorse horse
                        ? new ChestedHorseItemStorage(horse)
                        : null);
    }

    private static void registerFoodBiteBlocks() {
        FoodBiteRegistry.init();

        FoodBiteRegistry.forEachData(CommonRegistry::registerFoodBiteBlock);
    }

    private static void registerFoodBiteBlock(Identifier id, FoodBiteRegistry.FoodData data) {
        FoodBiteBlock block = data.blockType() == FoodBiteRegistry.BlockType.ONE_BY_TWO
                ? FoodBiteOneByTwoBlock.create(blockProperties(id), data.blockFood(), data.maxBites(), data.animateTick())
                : FoodBiteBlock.create(blockProperties(id), data.blockFood(), data.maxBites(), data.animateTick());
        if (data.getAABB() != null) {
            block.setAABB(data.getAABB());
        }
        registerBlock(id, block);

        BowlFoodBlockItem item = new BowlFoodBlockItem(block, data.itemFood(), itemProperties(id));
        registerItem(id, item);
    }

    private static BlockBehaviour.Properties blockProperties(Identifier id) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id));
    }

    private static Item.Properties itemProperties(Identifier id) {
        return new Item.Properties().setId(ResourceKey.create(Registries.ITEM, id));
    }

    private static void registerBlock(Identifier id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static void registerItem(Identifier id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static void registerCompostables() {
        CompostableRegistry.INSTANCE.add(ModItems.TOMATO_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.CHILI_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.LETTUCE_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.WILD_RICE_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.RICE_SEED, 0.3F);
        CompostableRegistry.INSTANCE.add(ModItems.TOMATO, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.RED_CHILI, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.GREEN_CHILI, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.LETTUCE, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.RICE_PANICLE, 0.65F);
        CompostableRegistry.INSTANCE.add(ModItems.CATERPILLAR, 1.0F);
    }

    private static void registerDispenserBehaviors() {
        DispenserBlock.registerBehavior(ModItems.OIL_POT, new OilPotDispenseBehavior());
    }

    private static void registerResourceReloadListeners() {
        ResourceLoader.get(PackType.SERVER_DATA)
                .registerReloadListener(MillstoneBindableDataReloadListener.ID, new MillstoneBindableDataReloadListener());
    }
}
