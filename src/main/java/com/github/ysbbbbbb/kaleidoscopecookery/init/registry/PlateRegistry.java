package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.PlateBlockItem;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class PlateRegistry {
    private static final Map<Identifier, PlateData> PLATE_DATA_MAP = new LinkedHashMap<>();

    public static final Identifier SHENGJIAN_MANTOU_PLATE = registerPlateData(
            "shengjian_mantou_plate", PlateData.create(5)
                    .setServingItems(() -> FoodBiteRegistry.getItem(FoodBiteRegistry.SHENGJIAN_MANTOU))
                    .setLootItem(() -> Items.BOWL));
    public static final Identifier BAOZI_PLATE = registerPlateData("baozi_plate", PlateData.create(5)
            .setServingItems(() -> ModItems.BAOZI)
            .setLootItem(() -> Items.BOWL));
    public static final Identifier QINGTUAN_PLATE = registerPlateData("qingtuan_plate", PlateData.create(4)
            .setServingItems(() -> ModItems.QINGTUAN)
            .setLootItem(() -> Items.BOWL));
    public static final Identifier STICKY_CANDY_PLATE = registerPlateData(
            "sticky_candy_plate", PlateData.create(4)
                    .setServingItems(() -> ModItems.STICKY_CANDY)
                    .setLootItem(() -> Items.BOWL));
    public static final Identifier STICKY_RICE_CAKE_PLATE = registerPlateData(
            "sticky_rice_cake_plate", PlateData.create(5)
                    .setServingItems(() -> ModItems.STICKY_RICE_CAKE)
                    .setLootItem(() -> Items.BOWL));
    public static final Identifier ZONGZI_PLATE = registerPlateData("zongzi_plate", PlateData.create(4)
            .setServingItems(() -> ModItems.ZONGZI)
            .setLootItem(() -> Items.BOWL));
    public static final Identifier BERRY_PLATTER = registerPlateData("berry_platter", PlateData.create(4)
            .addServingItems(() -> Items.SWEET_BERRIES, () -> Items.GLOW_BERRIES)
            .setLootItem(() -> Items.BOWL)
            .platterAABB());
    public static final Identifier APPLE_PLATTER = registerPlateData("apple_platter", PlateData.create(4)
            .setServingItems(() -> Items.APPLE)
            .setLootItem(() -> Items.BOWL)
            .platterAABB());
    public static final Identifier TOMATO_PLATTER = registerPlateData("tomato_platter", PlateData.create(5)
            .setServingItems(() -> ModItems.TOMATO)
            .setLootItem(() -> Items.BOWL)
            .platterAABB());
    public static final Identifier WATERMELON_PLATTER = registerPlateData(
            "watermelon_platter", PlateData.create(3)
                    .setServingItems(() -> Items.MELON_SLICE)
                    .setLootItem(() -> Items.BOWL)
                    .platterAABB());
    public static final Identifier CHORUS_FRUIT_PLATTER = registerPlateData(
            "chorus_fruit_platter", PlateData.create(5)
                    .setServingItems(() -> Items.CHORUS_FRUIT)
                    .setLootItem(() -> Items.BOWL)
                    .platterAABB());

    private PlateRegistry() {
    }

    public static void init() {
        registerBlocksAndItems();
    }

    public static List<Identifier> ids() {
        return List.copyOf(PLATE_DATA_MAP.keySet());
    }

    public static void forEachData(BiConsumer<Identifier, PlateData> consumer) {
        PLATE_DATA_MAP.forEach(consumer);
    }

    private static void registerBlocksAndItems() {
        PLATE_DATA_MAP.forEach((id, data) -> {
            PlateBlock block = PlateBlock.create(blockProperties(id), data.maxCount, data.servingItems);
            if (data.aabb != null) {
                block.setAABB(data.aabb);
            }
            registerBlock(id, block);

            registerItem(id, new PlateBlockItem(block, itemProperties(id), id.getPath()));
        });
    }

    private static BlockBehaviour.Properties blockProperties(Identifier id) {
        return BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, id));
    }

    private static Item.Properties itemProperties(Identifier id) {
        return new Item.Properties().stacksTo(16).setId(ResourceKey.create(Registries.ITEM, id));
    }

    private static void registerBlock(Identifier id, Block block) {
        Registry.register(BuiltInRegistries.BLOCK, id, block);
    }

    private static void registerItem(Identifier id, Item item) {
        Registry.register(BuiltInRegistries.ITEM, id, item);
    }

    private static Identifier registerPlateData(String name, PlateData data) {
        Identifier id = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name);
        PLATE_DATA_MAP.put(id, data);
        return id;
    }

    public static Item getItem(Identifier id) {
        return BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
    }

    public static Block getBlock(Identifier id) {
        return BuiltInRegistries.BLOCK.getOptional(id).orElse(Blocks.AIR);
    }

    public static final class PlateData {
        private final int maxCount;
        private final List<Supplier<Item>> servingItems = new ArrayList<>();
        private final List<Supplier<Item>> lootItems = new ArrayList<>();
        private @Nullable VoxelShape aabb;

        private PlateData(int maxCount) {
            this.maxCount = maxCount;
        }

        public static PlateData create(int maxCount) {
            return new PlateData(maxCount);
        }

        @SafeVarargs
        public final PlateData addServingItems(Supplier<Item>... servingItems) {
            this.servingItems.addAll(Arrays.asList(servingItems));
            return this;
        }

        public PlateData setServingItems(Supplier<Item> servingItem) {
            this.servingItems.clear();
            this.servingItems.add(servingItem);
            return this;
        }

        @SafeVarargs
        public final PlateData addLootItems(Supplier<Item>... lootItems) {
            this.lootItems.addAll(Arrays.asList(lootItems));
            return this;
        }

        public PlateData setLootItem(Supplier<Item> lootItem) {
            this.lootItems.clear();
            this.lootItems.add(lootItem);
            return this;
        }

        public List<Supplier<Item>> lootItems() {
            return List.copyOf(this.lootItems);
        }

        public PlateData platterAABB() {
            this.aabb = Shapes.or(
                    Block.box(4, 0, 4, 12, 2, 12),
                    Block.box(6, 2, 6, 10, 4, 10),
                    Block.box(1, 4, 1, 15, 6, 15));
            return this;
        }
    }
}
