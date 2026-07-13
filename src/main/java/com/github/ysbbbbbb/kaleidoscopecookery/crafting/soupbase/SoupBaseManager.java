package com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.LinkedHashMap;
import java.util.Map;

public final class SoupBaseManager {
    private static final Map<Identifier, ISoupBase> ALL_SOUP_BASES = new LinkedHashMap<>();

    private SoupBaseManager() {
    }

    public static void registerSoupBase(ISoupBase soupBase) {
        if (ALL_SOUP_BASES.containsKey(soupBase.getName())) {
            throw new IllegalArgumentException("Soup base with name " + soupBase.getName() + " already exists!");
        }
        ALL_SOUP_BASES.put(soupBase.getName(), soupBase);
    }

    public static void registerFluidSoupBase(Identifier name, Item bucketItem, int bubbleColor) {
        registerSoupBase(new FluidSoupBase(name, bucketItem, bubbleColor));
    }

    public static void registerMobSoupBase(Identifier name, Item bucketItem, int bubbleColor, EntityType<?> type) {
        registerSoupBase(new MobSoupBase(name, bucketItem, bubbleColor, type));
    }

    public static void registerMobSoupBase(Identifier name, Item mobBucketItem, EntityType<?> type) {
        registerSoupBase(new MobSoupBase(name, mobBucketItem, type));
    }

    public static ISoupBase getSoupBase(Identifier name) {
        return ALL_SOUP_BASES.get(SoupBaseIds.normalize(name));
    }

    public static boolean containsSoupBase(Identifier name) {
        return ALL_SOUP_BASES.containsKey(SoupBaseIds.normalize(name));
    }

    public static Map<Identifier, ISoupBase> getAllSoupBases() {
        return ALL_SOUP_BASES;
    }
}
