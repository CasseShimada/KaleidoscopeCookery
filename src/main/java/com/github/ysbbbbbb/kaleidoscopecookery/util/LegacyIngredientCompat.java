package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.RegistryOps;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.ValueInput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class LegacyIngredientCompat {
    public static final Codec<Ingredient> CODEC = Codec.of(
            Ingredient.CODEC, new CompatibleIngredientDecoder());

    private LegacyIngredientCompat() {
    }

    public static Optional<Ingredient> read(ValueInput input, String key) {
        Optional<Ingredient> current = input.read(key, CODEC);
        if (current.isPresent()) {
            return current;
        }
        return input.getString(key).flatMap(json -> readLegacyJson(input, key, json));
    }

    private static Optional<Ingredient> readLegacyJson(ValueInput input, String key, String json) {
        try {
            RegistryOps<JsonElement> ops = RegistryOps.create(JsonOps.INSTANCE, input.lookup());
            return CODEC.parse(ops, JsonParser.parseString(json)).resultOrPartial(error ->
                    KaleidoscopeCookery.LOGGER.warn("Failed to decode legacy ingredient {}: {}", key, error));
        } catch (RuntimeException e) {
            KaleidoscopeCookery.LOGGER.warn("Failed to parse legacy ingredient {}", key, e);
            return Optional.empty();
        }
    }

    private static JsonElement normalize(JsonElement element) {
        if (element.isJsonArray()) {
            JsonArray normalized = new JsonArray();
            for (JsonElement entry : element.getAsJsonArray()) {
                normalized.add(normalize(entry));
            }
            return normalized;
        }
        if (!element.isJsonObject()) {
            return element;
        }
        JsonObject object = element.getAsJsonObject();
        if (object.has("item") && object.get("item").isJsonPrimitive()) {
            return new JsonPrimitive(object.get("item").getAsString());
        }
        if (object.has("tag") && object.get("tag").isJsonPrimitive()) {
            return new JsonPrimitive("#" + object.get("tag").getAsString());
        }
        return object;
    }

    private static final class CompatibleIngredientDecoder implements Decoder<Ingredient> {
        @Override
        public <T> DataResult<Pair<Ingredient, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<Ingredient, T>> current = Ingredient.CODEC.decode(ops, input);
            if (current.result().isPresent()) {
                return current;
            }
            try {
                JsonElement legacyJson = ops.convertTo(JsonOps.INSTANCE, input);
                if (legacyJson.isJsonArray()) {
                    return decodeLegacyAlternatives(ops, input, legacyJson.getAsJsonArray());
                }
                JsonElement normalized = normalize(legacyJson);
                if (normalized.equals(legacyJson)) {
                    return current;
                }
                T normalizedInput = JsonOps.INSTANCE.convertTo(ops, normalized);
                return Ingredient.CODEC.decode(ops, normalizedInput);
            } catch (RuntimeException e) {
                return DataResult.error(() -> "Failed to normalize Forge ingredient: " + e.getMessage());
            }
        }

        private static <T> DataResult<Pair<Ingredient, T>> decodeLegacyAlternatives(
                DynamicOps<T> ops, T input, JsonArray alternatives) {
            List<Ingredient> decodedAlternatives = new ArrayList<>(alternatives.size());
            for (JsonElement alternative : alternatives) {
                JsonElement normalized = normalize(alternative);
                T normalizedInput = JsonOps.INSTANCE.convertTo(ops, normalized);
                DataResult<Ingredient> decoded = Ingredient.CODEC.parse(ops, normalizedInput);
                Optional<Ingredient> result = decoded.result();
                if (result.isEmpty()) {
                    String error = decoded.error()
                            .map(DataResult.Error::message)
                            .orElse("unknown ingredient error");
                    return DataResult.error(() -> "Failed to decode Forge ingredient alternative: " + error);
                }
                decodedAlternatives.add(result.orElseThrow());
            }
            try {
                Ingredient merged = Ingredient.of(decodedAlternatives.stream()
                        .flatMap(Ingredient::items)
                        .map(holder -> holder.value()));
                return DataResult.success(Pair.of(merged, input));
            } catch (RuntimeException e) {
                return DataResult.error(() -> "Failed to merge Forge ingredient alternatives: " + e.getMessage());
            }
        }
    }
}
