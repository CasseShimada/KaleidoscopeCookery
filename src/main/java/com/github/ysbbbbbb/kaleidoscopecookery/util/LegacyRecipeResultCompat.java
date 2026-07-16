package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.minecraft.world.item.ItemStackTemplate;

public final class LegacyRecipeResultCompat {
    public static final Codec<ItemStackTemplate> ITEM_STACK_TEMPLATE_CODEC = wrap(ItemStackTemplate.CODEC);

    private LegacyRecipeResultCompat() {
    }

    public static <A> Codec<A> wrap(Codec<A> currentCodec) {
        return Codec.of(currentCodec, new LegacyItemKeyDecoder<>(currentCodec));
    }

    private static JsonElement normalize(JsonElement element) {
        if (!element.isJsonObject()) {
            return element;
        }
        JsonObject object = element.getAsJsonObject();
        if (!object.has("item") || object.has("id")) {
            return object;
        }
        JsonObject normalized = object.deepCopy();
        normalized.add("id", normalized.remove("item"));
        return normalized;
    }

    private record LegacyItemKeyDecoder<A>(Codec<A> currentCodec) implements Decoder<A> {
        @Override
        public <T> DataResult<Pair<A, T>> decode(DynamicOps<T> ops, T input) {
            DataResult<Pair<A, T>> current = this.currentCodec.decode(ops, input);
            if (current.result().isPresent()) {
                return current;
            }
            try {
                JsonElement legacyJson = ops.convertTo(JsonOps.INSTANCE, input);
                JsonElement normalized = normalize(legacyJson);
                if (normalized.equals(legacyJson)) {
                    return current;
                }
                T normalizedInput = JsonOps.INSTANCE.convertTo(ops, normalized);
                return this.currentCodec.decode(ops, normalizedInput);
            } catch (RuntimeException e) {
                return DataResult.error(() -> "Failed to normalize Forge recipe result: " + e.getMessage());
            }
        }
    }
}
