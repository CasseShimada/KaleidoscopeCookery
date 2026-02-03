package com.github.ysbbbbbb.kaleidoscopecookery.client.resources;

import com.google.common.collect.Maps;
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

import java.util.Map;

public record ItemRenderReplacer(Map<Identifier, Identifier> pot,
                                 Map<Identifier, Identifier> stockpotCooking,
                                 Map<Identifier, Identifier> stockpotFinished,
                                 Map<Identifier, Identifier> millstone,
                                 Map<Identifier, Identifier> steamer) {
    public static final Codec<Identifier> RL_CODEC = Codec.STRING.comapFlatMap(ItemRenderReplacer::toLocation, Identifier::toString).stable();
    public static final Codec<ItemRenderReplacer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(Identifier.CODEC, RL_CODEC).fieldOf("pot").forGetter(ItemRenderReplacer::pot),
            Codec.unboundedMap(Identifier.CODEC, RL_CODEC).fieldOf("stockpot_cooking").forGetter(ItemRenderReplacer::stockpotCooking),
            Codec.unboundedMap(Identifier.CODEC, RL_CODEC).fieldOf("stockpot_finished").forGetter(ItemRenderReplacer::stockpotFinished),
            Codec.unboundedMap(Identifier.CODEC, RL_CODEC).fieldOf("millstone").forGetter(ItemRenderReplacer::millstone),
            Codec.unboundedMap(Identifier.CODEC, RL_CODEC).fieldOf("steamer").forGetter(ItemRenderReplacer::steamer)
    ).apply(instance, ItemRenderReplacer::new));

    public ItemRenderReplacer() {
        this(Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap(), Maps.newHashMap());
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

    private static DataResult<Identifier> toLocation(String input) {
        String[] split = input.split("#", 2);
        return DataResult.success(Identifier.parse(split[0]));
    }

    public void addAll(ItemRenderReplacer other) {
        this.pot.putAll(other.pot);
        this.stockpotCooking.putAll(other.stockpotCooking);
        this.stockpotFinished.putAll(other.stockpotFinished);
        this.millstone.putAll(other.millstone);
        this.steamer.putAll(other.steamer);
    }
}
