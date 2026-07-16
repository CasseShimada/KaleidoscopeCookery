package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotVisuals;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyIngredientCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyRecipeResultCompat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class StockpotRecipeSerializer {
    public static final int DEFAULT_TIME = 300;
    public static final int DEFAULT_COOKING_BUBBLE_COLOR = StockpotVisuals.DEFAULT_COOKING_BUBBLE_COLOR;
    public static final int DEFAULT_FINISHED_BUBBLE_COLOR = StockpotVisuals.DEFAULT_FINISHED_BUBBLE_COLOR;
    public static final Ingredient DEFAULT_CARRIER = Ingredient.of(Items.BOWL);
    public static final Identifier DEFAULT_SOUP_BASE = ModSoupBases.WATER;
    public static final Identifier EMPTY_ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot/empty");
    public static final Identifier DEFAULT_COOKING_TEXTURE = StockpotVisuals.DEFAULT_COOKING_TEXTURE;
    public static final Identifier DEFAULT_FINISHED_TEXTURE = StockpotVisuals.DEFAULT_FINISHED_TEXTURE;
    private static final ItemStackTemplate EMPTY_RESULT = new ItemStackTemplate(Items.SUSPICIOUS_STEW);

    public static RecipeHolder<StockpotRecipe> getEmptyRecipe() {
        StockpotRecipe stockpotRecipe = new StockpotRecipe(new ArrayList<>(), DEFAULT_SOUP_BASE,
                EMPTY_RESULT, DEFAULT_TIME, DEFAULT_CARRIER,
                DEFAULT_COOKING_TEXTURE, DEFAULT_FINISHED_TEXTURE,
                DEFAULT_COOKING_BUBBLE_COLOR,
                DEFAULT_FINISHED_BUBBLE_COLOR);
        ResourceKey<Recipe<?>> emptyKey = ResourceKey.create(Registries.RECIPE, EMPTY_ID);
        return new RecipeHolder<>(emptyKey, stockpotRecipe);
    }

    public static final MapCodec<StockpotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            LegacyIngredientCompat.CODEC.listOf().fieldOf("ingredients").forGetter(StockpotRecipe::getIngredients),
            Identifier.CODEC.optionalFieldOf("soup_base", DEFAULT_SOUP_BASE).forGetter(StockpotRecipe::soupBase),
            LegacyRecipeResultCompat.ITEM_STACK_TEMPLATE_CODEC.fieldOf("result").forGetter(StockpotRecipe::result),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(StockpotRecipe::time),
            LegacyIngredientCompat.CODEC.optionalFieldOf("carrier").forGetter(StockpotRecipeSerializer::carrierForJson),
            Codec.BOOL.optionalFieldOf("empty_carrier", false).forGetter(recipe -> recipe.carrier().isEmpty()),
            StockpotVisuals.CODEC.forGetter(StockpotVisuals::from)
    ).apply(instance, StockpotRecipeSerializer::createFromJson));

    public static final StreamCodec<RegistryFriendlyByteBuf, StockpotRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), StockpotRecipe::getIngredients,
            Identifier.STREAM_CODEC, StockpotRecipe::soupBase,
            ItemStackTemplate.STREAM_CODEC, StockpotRecipe::result,
            ByteBufCodecs.INT, StockpotRecipe::time,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, StockpotRecipe::carrier,
            StockpotVisuals.STREAM_CODEC, StockpotVisuals::from,
            StockpotRecipeSerializer::create);

    private static StockpotRecipe create(List<Ingredient> ingredients, Identifier soupBase,
                                         ItemStackTemplate result, int time, Optional<Ingredient> carrier,
                                         StockpotVisuals visuals) {
        return new StockpotRecipe(ingredients, soupBase, result, time, carrier,
                visuals.cookingTexture(), visuals.finishedTexture(),
                visuals.cookingBubbleColor(), visuals.finishedBubbleColor());
    }

    private static StockpotRecipe createFromJson(List<Ingredient> ingredients, Identifier soupBase,
                                                 ItemStackTemplate result, int time,
                                                 Optional<Ingredient> carrier, boolean emptyCarrier,
                                                 StockpotVisuals visuals) {
        Optional<Ingredient> resolvedCarrier = emptyCarrier
                ? Optional.empty()
                : Optional.of(carrier.orElse(DEFAULT_CARRIER));
        return create(ingredients, soupBase, result, time, resolvedCarrier, visuals);
    }

    private static Optional<Ingredient> carrierForJson(StockpotRecipe recipe) {
        return recipe.carrier();
    }

    private StockpotRecipeSerializer() {
    }
}
