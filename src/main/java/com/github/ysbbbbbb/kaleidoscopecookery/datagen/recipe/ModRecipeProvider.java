package com.github.ysbbbbbb.kaleidoscopecookery.datagen.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeBuilder;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Arrays;

public abstract class ModRecipeProvider extends RecipeProvider {
    public ModRecipeProvider(HolderLookup.Provider registries, RecipeOutput output) {
        super(registries, output);
    }

    @Override
    public void buildRecipes() {
        buildRecipes(this.output);
    }

    protected abstract void buildRecipes(RecipeOutput output);

    public Identifier modLoc(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    public String getRecipeIdWithCount(ItemLike itemLike, int count) {
        return RecipeBuilder.getDefaultRecipeId(itemLike.asItem()).getPath() + "_" + count;
    }

    public ItemLike[] getItemsWithCount(ItemLike itemLike, int count) {
        ItemLike[] items = new ItemLike[count];
        Arrays.fill(items, itemLike);
        return items;
    }

    public TagKey<Item>[] getItemsWithCount(TagKey<Item> itemLike, int count) {
        TagKey<Item>[] items = (TagKey<Item>[]) new TagKey<?>[count];
        Arrays.fill(items, itemLike);
        return items;
    }
}
