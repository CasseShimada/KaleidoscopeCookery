package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.RiceBowlRecipe;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;

public final class RiceBowlRecipeSerializer {
    public static final MapCodec<RiceBowlRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    CraftingBookCategory.CODEC.optionalFieldOf("category", CraftingBookCategory.MISC).forGetter(RiceBowlRecipe::category),
                    Ingredient.CODEC.fieldOf("ingredient").forGetter(RiceBowlRecipe::getIngredient),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(RiceBowlRecipe::result)
            ).apply(instance, RiceBowlRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, RiceBowlRecipe> STREAM_CODEC = StreamCodec.composite(
            CraftingBookCategory.STREAM_CODEC, RiceBowlRecipe::category,
            Ingredient.CONTENTS_STREAM_CODEC, RiceBowlRecipe::getIngredient,
            ItemStackTemplate.STREAM_CODEC, RiceBowlRecipe::result,
            RiceBowlRecipe::new
    );

    private RiceBowlRecipeSerializer() {
    }
}
