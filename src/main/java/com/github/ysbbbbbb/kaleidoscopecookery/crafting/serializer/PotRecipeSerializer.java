package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyIngredientCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyRecipeResultCompat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.Ingredient;

public final class PotRecipeSerializer {
    public static final MapCodec<PotRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    Codec.INT.optionalFieldOf("time", 200).forGetter(PotRecipe::time),
                    Codec.INT.optionalFieldOf("stir_fry_count", 3).forGetter(PotRecipe::stirFryCount),
                    LegacyIngredientCompat.CODEC.optionalFieldOf("carrier").forGetter(PotRecipe::carrier),
                    LegacyIngredientCompat.CODEC.listOf().fieldOf("ingredients").forGetter(recipe -> recipe.ingredients().stream().toList()),
                    LegacyRecipeResultCompat.ITEM_STACK_TEMPLATE_CODEC.fieldOf("result").forGetter(PotRecipe::result)
            ).apply(instance, PotRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, PotRecipe> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, PotRecipe::time,
            ByteBufCodecs.INT, PotRecipe::stirFryCount,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, PotRecipe::carrier,
            Ingredient.CONTENTS_STREAM_CODEC.apply(ByteBufCodecs.list()), PotRecipe::ingredients,
            ItemStackTemplate.STREAM_CODEC, PotRecipe::result,
            PotRecipe::new);

    private PotRecipeSerializer() {
    }
}
