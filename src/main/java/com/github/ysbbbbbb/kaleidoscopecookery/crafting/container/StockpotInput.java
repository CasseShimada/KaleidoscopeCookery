package com.github.ysbbbbbb.kaleidoscopecookery.crafting.container;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseIds;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeInput;

import java.util.List;

public record StockpotInput(List<ItemStack> items, Identifier soupBase) implements RecipeInput {
    public StockpotInput {
        items = List.copyOf(items);
        soupBase = SoupBaseIds.normalize(soupBase);
    }

    @Override
    public ItemStack getItem(int index) {
        return this.items.get(index);
    }

    @Override
    public int size() {
        return this.items.size();
    }

    @Deprecated(forRemoval = true)
    public List<ItemStack> getInputs() {
        return this.items;
    }

    @Deprecated(forRemoval = true)
    public Identifier getSoupBase() {
        return this.soupBase;
    }
}
