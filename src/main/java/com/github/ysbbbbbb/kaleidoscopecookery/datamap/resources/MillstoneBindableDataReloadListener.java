package com.github.ysbbbbbb.kaleidoscopecookery.datamap.resources;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.MillstoneBindableData;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;

import java.io.BufferedReader;
import java.util.HashMap;
import java.util.Map;

public final class MillstoneBindableDataReloadListener
        extends SimplePreparableReloadListener<Map<EntityType<?>, MillstoneBindableData>> {
    private static volatile Map<EntityType<?>, MillstoneBindableData> data = Map.of();
    private static final Identifier FILE_PATH = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "datamap/millstone_bindable_data.json");
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone_bindable_data");

    public static MillstoneBindableData getData(EntityType<?> entityType) {
        return data.getOrDefault(entityType, MillstoneBindableData.DEFAULT);
    }

    @Override
    protected Map<EntityType<?>, MillstoneBindableData> prepare(
            ResourceManager resourceManager, ProfilerFiller profiler) {
        Map<EntityType<?>, MillstoneBindableData> prepared = new HashMap<>();
        int loaded = 0;
        for (Resource resource : resourceManager.getResourceStack(FILE_PATH)) {
            if (load(resource, prepared)) {
                loaded++;
            }
        }
        if (loaded > 0) {
            KaleidoscopeCookery.LOGGER.info("Loaded millstone bindable data from {} resource pack(s)", loaded);
        }
        return Map.copyOf(prepared);
    }

    @Override
    protected void apply(Map<EntityType<?>, MillstoneBindableData> prepared,
                         ResourceManager resourceManager, ProfilerFiller profiler) {
        data = prepared;
    }

    private static boolean load(Resource resource, Map<EntityType<?>, MillstoneBindableData> prepared) {
        try (BufferedReader reader = resource.openAsReader()) {
            JsonElement jsonElement = JsonParser.parseReader(reader);
            var parsed = MillstoneBindableData.CODEC.parse(JsonOps.INSTANCE, jsonElement)
                    .resultOrPartial(message -> KaleidoscopeCookery.LOGGER.error(
                            "Failed to parse millstone bindable data from {}: {}",
                            resource.sourcePackId(), message));
            parsed.ifPresent(prepared::putAll);
            return parsed.isPresent();
        } catch (Exception e) {
            KaleidoscopeCookery.LOGGER.error("Failed to load millstone bindable data from {}", resource.sourcePackId(), e);
            return false;
        }
    }
}
