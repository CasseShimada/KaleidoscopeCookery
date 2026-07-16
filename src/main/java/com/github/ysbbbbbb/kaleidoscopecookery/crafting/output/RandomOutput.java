package com.github.ysbbbbbb.kaleidoscopecookery.crafting.output;

import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyRecipeResultCompat;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;

public record RandomOutput(ItemStackTemplate template, float chance) {
    private static final Codec<RandomOutput> CURRENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ItemStackTemplate.MAP_CODEC.forGetter(RandomOutput::template),
            Codec.FLOAT.optionalFieldOf("chance", 1.0F).forGetter(RandomOutput::chance)
    ).apply(instance, RandomOutput::new));
    public static final Codec<RandomOutput> CODEC = LegacyRecipeResultCompat.wrap(CURRENT_CODEC);

    public static final StreamCodec<RegistryFriendlyByteBuf, RandomOutput> STREAM_CODEC = StreamCodec.composite(
            ItemStackTemplate.STREAM_CODEC, RandomOutput::template,
            ByteBufCodecs.FLOAT, RandomOutput::chance,
            RandomOutput::new);

    public RandomOutput {
        chance = Mth.clamp(chance, 0.0F, 1.0F);
    }

    public RandomOutput(ItemStack stack, float chance) {
        this(ItemStackTemplate.fromNonEmptyStack(stack), chance);
    }

    public ItemStack stack() {
        return this.template.create();
    }

    public boolean isEmpty() {
        return this.stack().isEmpty();
    }
}
