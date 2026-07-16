package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyIngredientCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyRecipeResultCompat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

public final class TeapotRecipeSerializer {
    public static final int DEFAULT_TIME = 2400;
    public static final int DEFAULT_INGREDIENT_COUNT = 12;
    public static final Identifier EMPTY_TEA_FLUID = Identifier.fromNamespaceAndPath("minecraft", "empty");

    public static final MapCodec<TeapotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Identifier.CODEC.optionalFieldOf("tea_fluid", EMPTY_TEA_FLUID).forGetter(TeapotRecipe::teaFluid),
            LegacyIngredientCompat.CODEC.optionalFieldOf("ingredient").forGetter(TeapotRecipe::ingredient),
            Codec.INT.optionalFieldOf("ingredient_count", DEFAULT_INGREDIENT_COUNT).forGetter(TeapotRecipe::ingredientCount),
            Codec.INT.optionalFieldOf("time", DEFAULT_TIME).forGetter(TeapotRecipe::time),
            LegacyRecipeResultCompat.ITEM_STACK_TEMPLATE_CODEC.fieldOf("result").forGetter(TeapotRecipe::result)
    ).apply(instance, TeapotRecipe::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, TeapotRecipe> STREAM_CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, TeapotRecipe::teaFluid,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, TeapotRecipe::ingredient,
            ByteBufCodecs.INT, TeapotRecipe::ingredientCount,
            ByteBufCodecs.INT, TeapotRecipe::time,
            ItemStackTemplate.STREAM_CODEC, TeapotRecipe::result,
            TeapotRecipe::new);

    private TeapotRecipeSerializer() {
    }
}
