package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootTypes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.Identifier;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecipeRandomlyFunction extends LootItemConditionalFunction {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_randomly");
    public static final MapCodec<RecipeRandomlyFunction> CODEC = RecordCodecBuilder.mapCodec(instance -> commonFields(instance).and(
                    RecipeItem.RecipeRecord.CODEC.listOf().optionalFieldOf("recipes", List.of()).forGetter(f -> f.possibleRecipes)
            ).apply(instance, RecipeRandomlyFunction::new)
    );
    private final List<RecipeItem.RecipeRecord> possibleRecipes;

    protected RecipeRandomlyFunction(List<LootItemCondition> predicates, List<RecipeItem.RecipeRecord> possibleRecipes) {
        super(predicates);
        this.possibleRecipes = List.copyOf(possibleRecipes);
    }

    @Override
    public @NotNull MapCodec<? extends LootItemConditionalFunction> codec() {
        return ModLootTypes.RECIPE_RANDOMLY;
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, LootContext context) {
        RandomSource random = context.getRandom();

        if (setConfiguredRecipe(stack, random)) {
            return stack;
        }

        Item result = getRandomFoodBiteResult(random);
        if (result == null) {
            return stack;
        }
        RecipeManager recipeManager = context.getLevel().recipeAccess();

        // 炒锅配方
        for (var recipeHolder : recipeManager.getRecipes()) {
            if (recipeHolder.value().getType() != ModRecipes.POT_RECIPE) {
                continue;
            }
            PotRecipe recipe = (PotRecipe) recipeHolder.value();
            ItemStack resultItem = recipe.result().create();
            if (!resultItem.is(result)) {
                continue;
            }
            setGeneratedRecipe(stack, context, recipe.getIngredients(), resultItem, RecipeItem.POT);
            return stack;
        }

        // 灵活炒锅配方
        for (var recipeHolder : recipeManager.getRecipes()) {
            if (recipeHolder.value().getType() != ModRecipes.FLEX_POT_RECIPE) {
                continue;
            }
            FlexPotRecipe recipe = (FlexPotRecipe) recipeHolder.value();
            ItemStack resultItem = recipe.result().create();
            if (!resultItem.is(result)) {
                continue;
            }
            setGeneratedRecipe(stack, context, recipe.getIngredients(), resultItem, RecipeItem.POT);
            return stack;
        }

        // 汤锅配方
        for (var recipeHolder : recipeManager.getRecipes()) {
            if (recipeHolder.value().getType() != ModRecipes.STOCKPOT_RECIPE) {
                continue;
            }
            StockpotRecipe recipe = (StockpotRecipe) recipeHolder.value();
            ItemStack resultItem = recipe.result().create();
            if (!resultItem.is(result)) {
                continue;
            }
            setGeneratedRecipe(stack, context, recipe.getIngredients(), resultItem, RecipeItem.STOCKPOT);
            return stack;
        }

        // 灵活汤锅配方
        for (var recipeHolder : recipeManager.getRecipes()) {
            if (recipeHolder.value().getType() != ModRecipes.FLEX_STOCKPOT_RECIPE) {
                continue;
            }
            FlexStockpotRecipe recipe = (FlexStockpotRecipe) recipeHolder.value();
            ItemStack resultItem = recipe.result().create();
            if (!resultItem.is(result)) {
                continue;
            }
            setGeneratedRecipe(stack, context, recipe.getIngredients(), resultItem, RecipeItem.STOCKPOT);
            return stack;
        }

        return stack;
    }

    private boolean setConfiguredRecipe(ItemStack stack, RandomSource random) {
        if (this.possibleRecipes.isEmpty()) {
            return false;
        }
        RecipeItem.RecipeRecord record = this.possibleRecipes.get(random.nextInt(this.possibleRecipes.size()));
        RecipeItem.setRecipe(stack, record);
        return true;
    }

    @Nullable
    private static Item getRandomFoodBiteResult(RandomSource random) {
        List<Identifier> keys = FoodBiteRegistry.ids();
        if (keys.isEmpty()) {
            return null;
        }
        Identifier randomKey = keys.get(random.nextInt(keys.size()));
        return FoodBiteRegistry.getItem(randomKey);
    }

    private static List<ItemStack> getIngredientStacks(LootContext context, List<Ingredient> ingredients) {
        return ingredients.stream()
                .map(ingredient -> ItemUtils.getFirstIngredientStack(context.getLevel(), ingredient))
                .filter(itemStack -> !itemStack.isEmpty())
                .toList();
    }

    private static void setGeneratedRecipe(ItemStack stack, LootContext context, List<Ingredient> ingredients,
                                           ItemStack resultItem, Identifier recipeType) {
        List<ItemStack> inputs = getIngredientStacks(context, ingredients);
        RecipeItem.setRecipe(stack, new RecipeItem.RecipeRecord(inputs, resultItem, recipeType));
    }

    public static Builder randomRecipe() {
        return new Builder();
    }

    public static class Builder extends LootItemConditionalFunction.Builder<Builder> {
        private final List<RecipeItem.RecipeRecord> recipes = new ArrayList<>();

        @Override
        protected @NotNull Builder getThis() {
            return this;
        }

        public Builder withRecord(RecipeItem.RecipeRecord record) {
            this.recipes.add(record);
            return this;
        }

        public Builder pot(ItemLike output, ItemLike... input) {
            List<ItemStack> list = Arrays.stream(input).map(ItemStack::new).toList();
            RecipeItem.RecipeRecord record = new RecipeItem.RecipeRecord(list, new ItemStack(output), RecipeItem.POT);
            return withRecord(record);
        }

        public Builder stockpot(ItemLike output, ItemLike... input) {
            List<ItemStack> list = Arrays.stream(input).map(ItemStack::new).toList();
            RecipeItem.RecipeRecord record = new RecipeItem.RecipeRecord(list, new ItemStack(output), RecipeItem.STOCKPOT);
            return withRecord(record);
        }

        @Override
        public LootItemFunction build() {
            return new RecipeRandomlyFunction(this.getConditions(), this.recipes);
        }
    }
}
