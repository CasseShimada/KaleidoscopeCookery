package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

public final class FlexPotRecipeSerializer {
    public static final MapCodec<FlexPotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("time", 200).forGetter(FlexPotRecipe::time),
                    Codec.INT.optionalFieldOf("stir_fry_count", 3).forGetter(FlexPotRecipe::stirFryCount),
                    Ingredient.CODEC.optionalFieldOf("carrier", Ingredient.of(Items.BOWL)).forGetter(FlexPotRecipe::carrier),
                    Ingredient.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients().stream().toList()),
                    ItemStackTemplate.CODEC.fieldOf("result").forGetter(FlexPotRecipe::result)
            ).apply(instance, FlexPotRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, FlexPotRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, FlexPotRecipe::time,
            ByteBufCodecs.INT, FlexPotRecipe::stirFryCount,
            Ingredient.CONTENTS_STREAM_CODEC, FlexPotRecipe::carrier,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), FlexPotRecipe::ingredients,
            ItemStackTemplate.STREAM_CODEC, FlexPotRecipe::result,
            FlexPotRecipe::new
    );

    private FlexPotRecipeSerializer() {
    }
}
