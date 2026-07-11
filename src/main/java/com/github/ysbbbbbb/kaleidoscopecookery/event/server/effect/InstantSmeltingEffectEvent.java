package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.ArrayList;
import java.util.List;

public final class InstantSmeltingEffectEvent {
    private static final int BASE_CONVERSIONS = 1;

    private InstantSmeltingEffectEvent() {
    }

    public static void register() {
        LootTableEvents.MODIFY_DROPS.register(InstantSmeltingEffectEvent::modifyDrops);
    }

    private static void modifyDrops(Holder<LootTable> lootTable, LootContext context, List<ItemStack> drops) {
        BlockState state = context.getOptionalParameter(LootContextParams.BLOCK_STATE);
        if (state == null || !state.is(ConventionalBlockTags.ORES) || !isRootBlockLootTable(lootTable, state)) {
            return;
        }

        Entity entity = context.getOptionalParameter(LootContextParams.THIS_ENTITY);
        if (!(entity instanceof LivingEntity living)) {
            return;
        }

        MobEffectInstance effect = living.getEffect(ModEffects.INSTANT_SMELTING);
        if (effect == null || drops.isEmpty()) {
            return;
        }

        smeltDrops(context.getLevel(), drops, effect.getAmplifier() + BASE_CONVERSIONS);
    }

    private static boolean isRootBlockLootTable(Holder<LootTable> lootTable, BlockState state) {
        return state.getBlock().getLootTable().filter(lootTable::is).isPresent();
    }

    private static void smeltDrops(ServerLevel level, List<ItemStack> drops, int conversionLimit) {
        int remaining = conversionLimit;
        List<ItemStack> convertedDrops = new ArrayList<>();
        List<ItemStack> keptDrops = new ArrayList<>();

        for (ItemStack drop : drops) {
            ItemStack rest = drop.copy();
            int convertedCount = trySmeltDrop(level, rest, remaining, convertedDrops);
            remaining -= convertedCount;
            if (!rest.isEmpty()) {
                keptDrops.add(rest);
            }
        }

        if (remaining == conversionLimit) {
            return;
        }

        convertedDrops.addAll(keptDrops);
        drops.clear();
        drops.addAll(convertedDrops);
    }

    private static int trySmeltDrop(ServerLevel level, ItemStack rest, int remaining,
                                    List<ItemStack> convertedDrops) {
        if (remaining <= 0 || rest.isEmpty()) {
            return 0;
        }

        SingleRecipeInput input = new SingleRecipeInput(rest);
        var recipe = level.recipeAccess().getRecipeFor(RecipeType.SMELTING, input, level);
        if (recipe.isEmpty()) {
            return 0;
        }

        ItemStack result = recipe.get().value().assemble(input);
        if (result.isEmpty()) {
            return 0;
        }

        int convertedCount = Math.min(remaining, rest.getCount());
        rest.shrink(convertedCount);
        convertedDrops.add(result.copyWithCount(result.getCount() * convertedCount));
        return convertedCount;
    }
}
