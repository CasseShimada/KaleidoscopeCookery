package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.component.ItemContainerContents;

public final class ModDataComponents {
    public static final DataComponentType<ItemContainerContents> FRUIT_BASKET_ITEMS = register(
            "fruit_basket_items",
            DataComponentType.<ItemContainerContents>builder()
                    .persistent(ItemStackContainer.CONTENTS_CODEC)
                    .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                    .build()
    );

    public static final DataComponentType<Boolean> KITCHEN_SHOVEL_HAS_OIL = register(
            "kitchen_shovel_has_oil",
            DataComponentType.<Boolean>builder()
                    .persistent(Codec.BOOL)
                    .networkSynchronized(ByteBufCodecs.BOOL)
                    .build()
    );

    public static final DataComponentType<Integer> OIL_POT_OIL_COUNT = register(
            "oil_pot_oil_count",
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.VAR_INT)
                    .build()
    );

    /** Compatibility alias used by the interim 1.21.11/26.x Fabric port. */
    @Deprecated(forRemoval = false)
    public static final DataComponentType<Integer> OIL_POT_COUNT = register(
            "oil_pot_count",
            DataComponentType.<Integer>builder()
                    .persistent(Codec.INT)
                    .networkSynchronized(ByteBufCodecs.INT)
                    .build()
    );

    public static final DataComponentType<ItemContainerContents> TRANSMUTATION_LUNCH_BAG_ITEMS =
            register(
                    "transmutation_lunch_bag_items",
                    DataComponentType.<ItemContainerContents>builder()
                            .persistent(ItemStackContainer.CONTENTS_CODEC)
                            .networkSynchronized(ItemContainerContents.STREAM_CODEC)
                            .build()
            );

    public static final DataComponentType<RecipeItem.RecipeRecord> RECIPE_RECORD = register(
            "recipe_record",
            DataComponentType.<RecipeItem.RecipeRecord>builder()
                    .persistent(RecipeItem.RecipeRecord.CODEC)
                    .networkSynchronized(RecipeItem.RecipeRecord.STREAM_CODEC)
                    .build()
    );

    public static final DataComponentType<Quality> QUALITY = register(
            "quality",
            DataComponentType.<Quality>builder()
                    .persistent(Quality.CODEC)
                    .networkSynchronized(Quality.STREAM_CODEC)
                    .build()
    );

    public static void registerDataComponents() {
        // 注册方法，用于确保类被加载
    }

    private ModDataComponents() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static <T> DataComponentType<T> register(String path, DataComponentType<T> componentType) {
        return Registry.register(BuiltInRegistries.DATA_COMPONENT_TYPE, id(path), componentType);
    }
}
