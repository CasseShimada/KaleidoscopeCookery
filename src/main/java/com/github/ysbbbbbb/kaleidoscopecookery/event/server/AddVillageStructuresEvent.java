package com.github.ysbbbbbb.kaleidoscopecookery.event.server;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.mojang.datafixers.util.Pair;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorList;

import java.util.ArrayList;
import java.util.List;

public final class AddVillageStructuresEvent {
    private static final ResourceKey<StructureProcessorList> CROP_REPLACE_PROCESSOR_LIST_KEY = ResourceKey.create(
            Registries.PROCESSOR_LIST, Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "crop_replace"));

    private static final int KITCHEN_WEIGHT = 4;
    private static final VillageKitchen[] VILLAGE_KITCHENS = {
            new VillageKitchen(Identifier.parse("minecraft:village/plains/houses"),
                    "village/houses/plains_kitchen", KITCHEN_WEIGHT),
            new VillageKitchen(Identifier.parse("minecraft:village/snowy/houses"),
                    "village/houses/snowy_kitchen", KITCHEN_WEIGHT),
            new VillageKitchen(Identifier.parse("minecraft:village/savanna/houses"),
                    "village/houses/savanna_kitchen", KITCHEN_WEIGHT),
            new VillageKitchen(Identifier.parse("minecraft:village/desert/houses"),
                    "village/houses/desert_kitchen", KITCHEN_WEIGHT),
            new VillageKitchen(Identifier.parse("minecraft:village/taiga/houses"),
                    "village/houses/taiga_kitchen", KITCHEN_WEIGHT)
    };

    private AddVillageStructuresEvent() {
    }

    public static void register() {
        ServerLifecycleEvents.SERVER_STARTED.register(server -> addVillageStructures(server.registryAccess()));
    }

    /**
     * 参考自：<a href="https://gist.github.com/TelepathicGrunt/4fdbc445ebcbcbeb43ac748f4b18f342">GitHub TelepathicGrunt</a>
     */
    private static void addVillageStructures(RegistryAccess registryAccess) {
        for (VillageKitchen kitchen : VILLAGE_KITCHENS) {
            addBuildingToPool(registryAccess, kitchen.poolId(), kitchen.structurePath(), kitchen.weight());
        }
    }

    private static void addBuildingToPool(RegistryAccess registryAccess, Identifier poolId,
                                          String structurePath, int weight) {
        try {
            var templatePools = registryAccess.lookup(Registries.TEMPLATE_POOL);
            if (templatePools.isEmpty()) {
                KaleidoscopeCookery.LOGGER.warn("Template pools registry is empty for pool: {}", poolId);
                return;
            }
            var processorLists = registryAccess.lookup(Registries.PROCESSOR_LIST);
            if (processorLists.isEmpty()) {
                KaleidoscopeCookery.LOGGER.warn("Processor lists registry is empty for pool: {}", poolId);
                return;
            }
            ResourceKey<StructureTemplatePool> poolKey = ResourceKey.create(Registries.TEMPLATE_POOL, poolId);
            var poolHolder = templatePools.get().get(poolKey);
            if (poolHolder.isEmpty()) {
                KaleidoscopeCookery.LOGGER.warn("Structure pool not found: {}", poolId);
                return;
            }
            StructureTemplatePool pool = poolHolder.get().value();
            var processorHolder = processorLists.get().get(CROP_REPLACE_PROCESSOR_LIST_KEY);
            if (processorHolder.isEmpty()) {
                KaleidoscopeCookery.LOGGER.warn("Processor list not found: {}", CROP_REPLACE_PROCESSOR_LIST_KEY.identifier());
                return;
            }
            Holder<StructureProcessorList> holder = processorHolder.get();
            Identifier structLocation = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, structurePath);
            SinglePoolElement piece = SinglePoolElement.legacy(structLocation.toString(), holder)
                    .apply(StructureTemplatePool.Projection.RIGID);

            // 添加到 templates 列表
            for (int i = 0; i < weight; i++) {
                pool.templates.add(piece);
            }

            List<Pair<StructurePoolElement, Integer>> newRawTemplates = new ArrayList<>(pool.rawTemplates);
            newRawTemplates.add(Pair.of(piece, weight));
            pool.rawTemplates = newRawTemplates;

        } catch (Exception e) {
            KaleidoscopeCookery.LOGGER.error("Failed to add village structure to pool {}. Skipping this pool.", poolId, e);
        }
    }

    private record VillageKitchen(Identifier poolId, String structurePath, int weight) {
    }
}
