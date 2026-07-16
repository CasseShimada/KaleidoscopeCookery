package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei;

import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

import java.util.Arrays;
import java.util.List;
import java.util.stream.StreamSupport;

public class ReiUtil {
    public static EntryIngredient ofIngredient(Ingredient ingredient) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            List<ItemStack> stacks = ingredient.display()
                    .resolveForStacks(SlotDisplayContext.fromLevel(level));
            if (!stacks.isEmpty()) {
                return EntryIngredient.of(stacks.stream().map(EntryStacks::of).toList());
            }
        }
        return ofIngredientWithoutLevel(ingredient);
    }

    @SuppressWarnings("deprecation")
    private static EntryIngredient ofIngredientWithoutLevel(Ingredient ingredient) {
        return EntryIngredient.of(ingredient.items()
                .map(Holder::value)
                .map(Item::getDefaultInstance)
                .map(EntryStacks::of)
                .toList());
    }

    public static EntryIngredient ofItem(Item item) {
        return EntryIngredient.of(EntryStacks.of(item));
    }

    public static EntryIngredient ofItemStack(ItemStack itemStack) {
        return EntryIngredient.of(EntryStacks.of(itemStack));
    }

    public static EntryIngredient ofTag(TagKey<Item> tagKey) {
        return EntryIngredient.of(StreamSupport.stream(BuiltInRegistries.ITEM.getTagOrEmpty(tagKey).spliterator(), false)
                .map(Holder::value)
                .map(EntryStacks::of)
                .toList());
    }

    public static List<EntryIngredient> ofItems(Item... items) {
        return Arrays.stream(items).map(ReiUtil::ofItem).toList();
    }

    public static List<EntryIngredient> ofItemStacks(ItemStack... items) {
        return Arrays.stream(items).map(ReiUtil::ofItemStack).toList();
    }

    public static List<EntryIngredient> ofItems(List<Item> items) {
        return items.stream().map(ReiUtil::ofItem).toList();
    }

    public static List<EntryIngredient> ofIngredients(Ingredient... ingredients) {
        return Arrays.stream(ingredients).map(ReiUtil::ofIngredient).toList();
    }

    public static List<EntryIngredient> ofIngredients(List<Ingredient> ingredients) {
        return ingredients.stream().map(ReiUtil::ofIngredient).toList();
    }
}
