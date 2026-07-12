package com.github.ysbbbbbb.kaleidoscopecookery.client.resources;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public record ItemRenderReplacer(Map<Identifier, Identifier> pot,
                                 Map<Identifier, Identifier> stockpotCooking,
                                 Map<Identifier, Identifier> stockpotFinished,
                                 Map<Identifier, Identifier> millstone,
                                 Map<Identifier, Identifier> steamer) {
    private static final Codec<Identifier> ITEM_MODEL_CODEC = Codec.STRING.comapFlatMap(
            ItemRenderReplacer::parseItemModelId, Identifier::toString).stable();
    private static final Codec<Map<Identifier, Identifier>> OVERRIDE_MAP_CODEC = Codec.unboundedMap(
            Identifier.CODEC, ITEM_MODEL_CODEC);
    public static final Codec<ItemRenderReplacer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            OVERRIDE_MAP_CODEC.optionalFieldOf("pot", Map.of()).forGetter(ItemRenderReplacer::pot),
            OVERRIDE_MAP_CODEC.optionalFieldOf("stockpot_cooking", Map.of()).forGetter(ItemRenderReplacer::stockpotCooking),
            OVERRIDE_MAP_CODEC.optionalFieldOf("stockpot_finished", Map.of()).forGetter(ItemRenderReplacer::stockpotFinished),
            OVERRIDE_MAP_CODEC.optionalFieldOf("millstone", Map.of()).forGetter(ItemRenderReplacer::millstone),
            OVERRIDE_MAP_CODEC.optionalFieldOf("steamer", Map.of()).forGetter(ItemRenderReplacer::steamer)
    ).apply(instance, ItemRenderReplacer::new));

    public ItemRenderReplacer {
        pot = new HashMap<>(pot);
        stockpotCooking = new HashMap<>(stockpotCooking);
        stockpotFinished = new HashMap<>(stockpotFinished);
        millstone = new HashMap<>(millstone);
        steamer = new HashMap<>(steamer);
    }

    public ItemRenderReplacer() {
        this(new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>(), new HashMap<>());
    }

    public static void updateRenderState(ItemModelResolver resolver, ItemStackRenderState renderState,
                                         ItemStack stack, ItemDisplayContext displayContext,
                                         @Nullable Level level, int seed, Map<Identifier, Identifier> models) {
        ItemStack renderStack = applyModelOverride(stack, models);
        resolver.updateForTopItem(renderState, renderStack, displayContext, level, null, seed);
    }

    public static ItemStack applyModelOverride(ItemStack stack, Map<Identifier, Identifier> models) {
        Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        Identifier modelId = models.get(key);
        if (modelId == null) {
            return stack;
        }
        ItemStack copy = stack.copy();
        copy.set(DataComponents.ITEM_MODEL, modelId);
        return copy;
    }

    private static DataResult<Identifier> parseItemModelId(String input) {
        String id = input.split("#", 2)[0];
        Identifier modelId = Identifier.tryParse(id);
        if (modelId == null) {
            return DataResult.error(() -> "Not a valid item model id: " + input);
        }
        return DataResult.success(modelId);
    }

    public void addAll(ItemRenderReplacer other) {
        this.pot.putAll(other.pot);
        this.stockpotCooking.putAll(other.stockpotCooking);
        this.stockpotFinished.putAll(other.stockpotFinished);
        this.millstone.putAll(other.millstone);
        this.steamer.putAll(other.steamer);
    }

    public void clear() {
        this.pot.clear();
        this.stockpotCooking.clear();
        this.stockpotFinished.clear();
        this.millstone.clear();
        this.steamer.clear();
    }
}
