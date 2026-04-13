package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.common.collect.ImmutableSet;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.npc.villager.VillagerProfession;

public class ModVillager {
    public static final ResourceKey<VillagerProfession> CHEF_KEY = ResourceKey.create(Registries.VILLAGER_PROFESSION,
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chef"));
    public static final VillagerProfession CHEF = new VillagerProfession(
            Component.translatable("profession.kaleidoscope_cookery.chef"),
            poi -> poi.value() == ModPoi.STOVE,
            poi -> poi.value() == ModPoi.STOVE,
            ImmutableSet.of(), ImmutableSet.of(), SoundEvents.VILLAGER_WORK_BUTCHER, Int2ObjectMaps.emptyMap());

    public static void registerVillagerProfessions() {
        Registry.register(BuiltInRegistries.VILLAGER_PROFESSION, CHEF_KEY, CHEF);
    }
}
