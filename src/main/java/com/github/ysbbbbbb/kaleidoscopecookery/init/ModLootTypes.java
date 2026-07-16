package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceBlockMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.AdvanceEntityMatchTool;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.RecipeRandomlyFunction;
import com.github.ysbbbbbb.kaleidoscopecookery.loot.SetRecipeRecordFunction;
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public final class ModLootTypes {
    public static final MapCodec<AdvanceEntityMatchTool> ADVANCE_ENTITY_MATCH_TOOL = AdvanceEntityMatchTool.CODEC;
    public static final MapCodec<AdvanceBlockMatchTool> ADVANCE_BLOCK_MATCH_TOOL = AdvanceBlockMatchTool.CODEC;
    public static final MapCodec<RecipeRandomlyFunction> RECIPE_RANDOMLY = RecipeRandomlyFunction.CODEC;
    public static final MapCodec<SetRecipeRecordFunction> SET_RECIPE_RECORD = SetRecipeRecordFunction.CODEC;

    private ModLootTypes() {
    }

    public static void registerLootTypes() {
        registerCondition(AdvanceEntityMatchTool.ID, ADVANCE_ENTITY_MATCH_TOOL);
        registerCondition(AdvanceBlockMatchTool.ID, ADVANCE_BLOCK_MATCH_TOOL);
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, RecipeRandomlyFunction.ID, RECIPE_RANDOMLY);
        Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, SetRecipeRecordFunction.ID, SET_RECIPE_RECORD);
    }

    private static void registerCondition(Identifier id, MapCodec<? extends LootItemCondition> codec) {
        Registry.register(BuiltInRegistries.LOOT_CONDITION_TYPE, id, codec);
    }
}
