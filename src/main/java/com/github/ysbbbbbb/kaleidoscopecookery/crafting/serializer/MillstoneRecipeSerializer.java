package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

public final class MillstoneRecipeSerializer {
    public static final MapCodec<MillstoneRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(MillstoneRecipe::getIngredient),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(MillstoneRecipe::getResultTemplate),
                    Ingredient.CODEC.optionalFieldOf("carrier").forGetter(MillstoneRecipe::getCarrier)
            ).apply(instance, MillstoneRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MillstoneRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, MillstoneRecipe::getIngredient,
            ItemStackTemplate.STREAM_CODEC, MillstoneRecipe::getResultTemplate,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, MillstoneRecipe::getCarrier,
            MillstoneRecipe::new);

    private MillstoneRecipeSerializer() {
    }
}
