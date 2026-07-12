package com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public record TeapotRecipe(Identifier teaFluid, Ingredient ingredient, int ingredientCount, int time,
                           ItemStackTemplate resultTemplate) implements BaseRecipe<TeapotInput> {
    public static final int OUTPUT_COUNT = 12;

    public TeapotRecipe(Identifier teaFluid, Ingredient ingredient, int ingredientCount, int time, ItemStack result) {
        this(teaFluid, ingredient, ingredientCount, time, ItemStackTemplate.fromNonEmptyStack(result));
    }

    @Override
    public boolean matches(TeapotInput container, Level level) {
        ItemStack stack = container.getItemStack();
        return this.teaFluid.equals(container.getTeaFluid()) && this.ingredient.test(stack) && stack.getCount() >= this.ingredientCount;
    }

    @Override
    public ItemStack assemble(TeapotInput container) {
        return this.resultTemplate.create().copyWithCount(OUTPUT_COUNT);
    }

    @Override
    public ItemStack getResult() {
        return this.resultTemplate.create().copyWithCount(OUTPUT_COUNT);
    }

    @Override
    public ItemStackTemplate result() {
        return resultTemplate;
    }

    @Override
    public RecipeSerializer<TeapotRecipe> getSerializer() {
        return ModRecipes.TEAPOT_SERIALIZER;
    }

    @Override
    public RecipeType<TeapotRecipe> getType() {
        return ModRecipes.TEAPOT_RECIPE;
    }
}
