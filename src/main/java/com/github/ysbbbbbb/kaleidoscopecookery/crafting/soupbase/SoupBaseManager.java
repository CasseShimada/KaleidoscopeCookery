package com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase;

import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public final class SoupBaseManager {
    private static final Map<Identifier, ISoupBase> ALL_SOUP_BASES = new LinkedHashMap<>();
    private static final Map<Identifier, ISoupBase> SOUP_BASES_VIEW = Collections.unmodifiableMap(ALL_SOUP_BASES);

    private SoupBaseManager() {
    }

    public static void registerSoupBase(ISoupBase soupBase) {
        Objects.requireNonNull(soupBase, "soupBase");
        Identifier name = Objects.requireNonNull(soupBase.getName(), "soupBase name");
        Identifier normalizedName = SoupBaseIds.normalize(name);
        if (!name.equals(normalizedName)) {
            throw new IllegalArgumentException(
                    "Soup base id " + name + " is a legacy alias; register " + normalizedName + " instead");
        }
        if (ALL_SOUP_BASES.putIfAbsent(name, soupBase) != null) {
            throw new IllegalArgumentException("Soup base with name " + name + " already exists!");
        }
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
        return SOUP_BASES_VIEW;
    }
}
