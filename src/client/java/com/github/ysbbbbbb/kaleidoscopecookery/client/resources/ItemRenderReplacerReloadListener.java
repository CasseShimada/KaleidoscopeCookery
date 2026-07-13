package com.github.ysbbbbbb.kaleidoscopecookery.client.resources;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.util.Map;

public final class ItemRenderReplacerReloadListener extends SimplePreparableReloadListener<ItemRenderReplacer> {
    private static volatile ItemRenderReplacer replacer = new ItemRenderReplacer();
    private static final Identifier FILE_PATH = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "models/item_render_replacer.json");
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "item_render_replacer");

    public static void updatePotRenderState(ItemModelResolver resolver, ItemStackRenderState renderState,
                                            ItemStack stack, ItemDisplayContext displayContext,
                                            @Nullable Level level, int seed) {
        updateRenderState(resolver, renderState, stack, displayContext, level, seed, replacer.pot());
    }

    public static void updateStockpotCookingRenderState(ItemModelResolver resolver, ItemStackRenderState renderState,
                                                        ItemStack stack, ItemDisplayContext displayContext,
                                                        @Nullable Level level, int seed) {
        updateRenderState(resolver, renderState, stack, displayContext, level, seed, replacer.stockpotCooking());
    }

    public static void updateStockpotFinishedRenderState(ItemModelResolver resolver, ItemStackRenderState renderState,
                                                         ItemStack stack, ItemDisplayContext displayContext,
                                                         @Nullable Level level, int seed) {
        updateRenderState(resolver, renderState, stack, displayContext, level, seed, replacer.stockpotFinished());
    }

    public static void updateMillstoneRenderState(ItemModelResolver resolver, ItemStackRenderState renderState,
                                                  ItemStack stack, ItemDisplayContext displayContext,
                                                  @Nullable Level level, int seed) {
        updateRenderState(resolver, renderState, stack, displayContext, level, seed, replacer.millstone());
    }

    public static void updateSteamerRenderState(ItemModelResolver resolver, ItemStackRenderState renderState,
                                                ItemStack stack, ItemDisplayContext displayContext,
                                                @Nullable Level level, int seed) {
        updateRenderState(resolver, renderState, stack, displayContext, level, seed, replacer.steamer());
    }

    public static boolean hasSteamerOverride(ItemStack stack) {
        Identifier key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        return replacer.steamer().containsKey(key);
    }

    @Override
    protected ItemRenderReplacer prepare(ResourceManager resourceManager, ProfilerFiller profiler) {
        ItemRenderReplacer prepared = new ItemRenderReplacer();
        int loaded = 0;
        for (Resource resource : resourceManager.getResourceStack(FILE_PATH)) {
            if (load(resource, prepared)) {
                loaded++;
            }
        }
        if (loaded > 0) {
            KaleidoscopeCookery.LOGGER.info("Loaded item render replacer data from {} resource pack(s)", loaded);
        }
        return prepared;
    }

    @Override
    protected void apply(ItemRenderReplacer prepared, ResourceManager resourceManager, ProfilerFiller profiler) {
        replacer = prepared;
    }

    private static void updateRenderState(ItemModelResolver resolver, ItemStackRenderState renderState,
                                          ItemStack stack, ItemDisplayContext displayContext,
                                          @Nullable Level level, int seed, Map<Identifier, Identifier> models) {
        ItemRenderReplacer.updateRenderState(resolver, renderState, stack, displayContext, level, seed, models);
    }

    private static boolean load(Resource resource, ItemRenderReplacer prepared) {
        try (BufferedReader reader = resource.openAsReader()) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            var parsed = ItemRenderReplacer.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                    .resultOrPartial(message -> KaleidoscopeCookery.LOGGER.error(
                            "Failed to parse item render replacer data from {}: {}",
                            resource.sourcePackId(), message));
            parsed.ifPresent(prepared::addAll);
            return parsed.isPresent();
        } catch (Exception e) {
            KaleidoscopeCookery.LOGGER.error("Failed to load item render replacer data from {}", resource.sourcePackId(), e);
            return false;
        }
    }
}
