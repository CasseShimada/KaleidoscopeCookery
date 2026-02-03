package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.block.dispenser.OilPotDispenseBehavior;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteOneByTwoBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.event.*;
import com.github.ysbbbbbb.kaleidoscopecookery.event.effect.FlatulenceServerEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.effect.PreservationEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.event.effect.SatiatedShieldEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.BowlFoodBlockItem;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class CommonRegistry {
    public static void init() {
        addComposter();
        registerFoodBiteBlocks();
        registerServerEvents();
        addDispenserBehavior();
    }

    public static void registerServerEvents() {
        SatiatedShieldEvent.register();
        FlatulenceServerEvent.register();
        PreservationEvent.register();
        ArmorEffectHandler.register();
        AddVillageStructuresEvent.register();
        EntityJoinWorldEvent.register();
        HoeUseEvent.register();
        RightClickEvent.register();
        LeftClickEvent.register();
        ExtraLootTableDrop.register();
        SickleHarvestNetherWartEvent.register();
    }

    private static void registerFoodBiteBlocks() {
        FoodBiteRegistry.init();

        FoodBiteRegistry.FOOD_DATA_MAP.forEach((resourceLocation, data) -> {
            BlockBehaviour.Properties properties = BlockBehaviour.Properties.of()
                    .setId(ResourceKey.create(Registries.BLOCK, resourceLocation));
            FoodBiteBlock block = data.blockType() == FoodBiteRegistry.BlockType.ONE_BY_TWO
                    ? new FoodBiteOneByTwoBlock(properties, data.blockFood(), data.maxBites(), data.animateTick())
                    : new FoodBiteBlock(properties, data.blockFood(), data.maxBites(), data.animateTick());
            if (data.getAABB() != null) {
                block.setAABB(data.getAABB());
            }
            Registry.register(BuiltInRegistries.BLOCK, resourceLocation, block);

            Item.Properties itemProperties = new Item.Properties()
                    .setId(ResourceKey.create(Registries.ITEM, resourceLocation));
            BowlFoodBlockItem item = new BowlFoodBlockItem(block, data.itemFood(), itemProperties);
            Registry.register(BuiltInRegistries.ITEM, resourceLocation, item);
        });
    }

    private static void addComposter() {
        CompostingChanceRegistry.INSTANCE.add(ModItems.TOMATO_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.CHILI_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.LETTUCE_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.WILD_RICE_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RICE_SEED, 0.3F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.TOMATO, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RED_CHILI, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.GREEN_CHILI, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.LETTUCE, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.RICE_PANICLE, 0.65F);
        CompostingChanceRegistry.INSTANCE.add(ModItems.CATERPILLAR, 1.0F);
    }

    private static void addDispenserBehavior() {
        DispenserBlock.registerBehavior(ModItems.OIL_POT, new OilPotDispenseBehavior());
    }
}
