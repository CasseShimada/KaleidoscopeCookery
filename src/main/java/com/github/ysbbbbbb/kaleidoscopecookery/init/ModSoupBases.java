package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public final class ModSoupBases {
    public static final Identifier WATER = itemId(Items.WATER_BUCKET);
    public static final Identifier LAVA = itemId(Items.LAVA_BUCKET);
    public static final Identifier AXOLOTL_BUCKET = itemId(Items.AXOLOTL_BUCKET);
    public static final Identifier COD_BUCKET = itemId(Items.COD_BUCKET);
    public static final Identifier SALMON_BUCKET = itemId(Items.SALMON_BUCKET);
    public static final Identifier TROPICAL_FISH_BUCKET = itemId(Items.TROPICAL_FISH_BUCKET);
    public static final Identifier PUFFERFISH_BUCKET = itemId(Items.PUFFERFISH_BUCKET);
    public static final Identifier TADPOLE_BUCKET = itemId(Items.TADPOLE_BUCKET);

    private ModSoupBases() {
    }

    public static void registerSoupBases() {
        SoupBaseManager.registerFluidSoupBase(WATER, Items.WATER_BUCKET, 0x3F76E4);
        SoupBaseManager.registerFluidSoupBase(LAVA, Items.LAVA_BUCKET, 0xFF9838);

        SoupBaseManager.registerMobSoupBase(AXOLOTL_BUCKET, Items.AXOLOTL_BUCKET, EntityTypes.AXOLOTL);
        SoupBaseManager.registerMobSoupBase(COD_BUCKET, Items.COD_BUCKET, EntityTypes.COD);
        SoupBaseManager.registerMobSoupBase(SALMON_BUCKET, Items.SALMON_BUCKET, EntityTypes.SALMON);
        SoupBaseManager.registerMobSoupBase(TROPICAL_FISH_BUCKET, Items.TROPICAL_FISH_BUCKET, EntityTypes.TROPICAL_FISH);
        SoupBaseManager.registerMobSoupBase(PUFFERFISH_BUCKET, Items.PUFFERFISH_BUCKET, EntityTypes.PUFFERFISH);
        SoupBaseManager.registerMobSoupBase(TADPOLE_BUCKET, Items.TADPOLE_BUCKET, EntityTypes.TADPOLE);
    }

    private static Identifier itemId(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }
}
