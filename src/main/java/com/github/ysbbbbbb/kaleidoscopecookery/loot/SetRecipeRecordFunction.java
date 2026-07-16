package com.github.ysbbbbbb.kaleidoscopecookery.loot;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootTypes;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/** Builds the ItemStack-based recipe component after item defaults finish bootstrapping. */
public final class SetRecipeRecordFunction extends LootItemConditionalFunction {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "set_recipe_record");

    private static final Codec<Item> ITEM_CODEC = BuiltInRegistries.ITEM.byNameCodec();
    private static final Codec<Identifier> RECIPE_TYPE_CODEC = Identifier.CODEC.validate(type ->
            type.equals(RecipeItem.POT) || type.equals(RecipeItem.STOCKPOT)
                    ? DataResult.success(type)
                    : DataResult.error(() -> "Unsupported Cookery recipe record type: " + type));

    public static final MapCodec<SetRecipeRecordFunction> CODEC = RecordCodecBuilder.mapCodec(instance ->
            commonFields(instance).and(instance.group(
                    ITEM_CODEC.listOf().fieldOf("input").forGetter(function -> function.input),
                    ITEM_CODEC.fieldOf("output").forGetter(function -> function.output),
                    RECIPE_TYPE_CODEC.fieldOf("type").forGetter(function -> function.type)
            )).apply(instance, SetRecipeRecordFunction::new));

    private final List<Item> input;
    private final Item output;
    private final Identifier type;

    private SetRecipeRecordFunction(List<LootItemCondition> predicates, List<Item> input,
                                    Item output, Identifier type) {
        super(predicates);
        this.input = List.copyOf(input);
        this.output = output;
        this.type = type;
    }

    @Override
    public @NotNull MapCodec<? extends LootItemConditionalFunction> codec() {
        return ModLootTypes.SET_RECIPE_RECORD;
    }

    @Override
    protected @NotNull ItemStack run(ItemStack stack, LootContext context) {
        if (stack.getItem() instanceof RecipeItem) {
            List<ItemStack> inputStacks = this.input.stream().map(ItemStack::new).toList();
            RecipeItem.setRecipe(stack, new RecipeItem.RecipeRecord(
                    inputStacks, new ItemStack(this.output), this.type));
        }
        return stack;
    }
}
