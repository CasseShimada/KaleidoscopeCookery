package com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer;

import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.output.RandomOutput;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyIngredientCompat;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.MapLike;
import com.mojang.serialization.RecordBuilder;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.List;
import java.util.stream.Stream;

public final class MillstoneRecipeSerializer {
    private static final MapCodec<List<RandomOutput>> RESULTS_CODEC = new MapCodec<>() {
        @Override
        public <T> DataResult<List<RandomOutput>> decode(DynamicOps<T> ops, MapLike<T> input) {
            T resultsNode = input.get("results");
            if (resultsNode != null) {
                return RandomOutput.CODEC.listOf().parse(ops, resultsNode).flatMap(MillstoneRecipeSerializer::validateResults);
            }
            T resultNode = input.get("result");
            if (resultNode != null) {
                return RandomOutput.CODEC.parse(ops, resultNode).map(List::of);
            }
            return DataResult.error(() -> "Millstone recipe is missing both 'result' and 'results'");
        }

        @Override
        public <T> RecordBuilder<T> encode(List<RandomOutput> outputs, DynamicOps<T> ops, RecordBuilder<T> prefix) {
            if (outputs.size() == 1) {
                return prefix.add("result", RandomOutput.CODEC.encodeStart(ops, outputs.getFirst()));
            }
            return prefix.add("results", RandomOutput.CODEC.listOf().encodeStart(ops, outputs));
        }

        @Override
        public <T> Stream<T> keys(DynamicOps<T> ops) {
            return Stream.of(ops.createString("result"), ops.createString("results"));
        }
    };

    public static final MapCodec<MillstoneRecipe> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    LegacyIngredientCompat.CODEC.fieldOf("ingredient").forGetter(MillstoneRecipe::getIngredient),
                    RESULTS_CODEC.forGetter(MillstoneRecipe::results),
                    LegacyIngredientCompat.CODEC.optionalFieldOf("carrier").forGetter(MillstoneRecipe::getCarrier)
            ).apply(instance, MillstoneRecipe::new)
    );

    public static final StreamCodec<RegistryFriendlyByteBuf, MillstoneRecipe> STREAM_CODEC = StreamCodec.composite(
            Ingredient.CONTENTS_STREAM_CODEC, MillstoneRecipe::getIngredient,
            RandomOutput.STREAM_CODEC.apply(ByteBufCodecs.list(MillstoneRecipe.MAX_OUTPUTS)), MillstoneRecipe::results,
            Ingredient.OPTIONAL_CONTENTS_STREAM_CODEC, MillstoneRecipe::getCarrier,
            MillstoneRecipe::new);

    private static DataResult<List<RandomOutput>> validateResults(List<RandomOutput> outputs) {
        if (outputs.isEmpty() || outputs.size() > MillstoneRecipe.MAX_OUTPUTS) {
            return DataResult.error(() -> "Millstone recipes require between 1 and "
                    + MillstoneRecipe.MAX_OUTPUTS + " outputs");
        }
        return DataResult.success(outputs);
    }

    private MillstoneRecipeSerializer() {
    }
}
