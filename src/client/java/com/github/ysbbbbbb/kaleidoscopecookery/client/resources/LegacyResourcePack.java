package com.github.ysbbbbbb.kaleidoscopecookery.client.resources;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.fabric.api.resource.v1.ResourceLoader;
import net.fabricmc.fabric.api.resource.v1.pack.PackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public final class LegacyResourcePack {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "legacy_resources_pack");

    private LegacyResourcePack() {
    }

    public static void register() {
        var modContainer = FabricLoader.getInstance().getModContainer(KaleidoscopeCookery.MOD_ID)
                .orElseThrow(() -> new IllegalStateException("Missing Kaleidoscope Cookery mod container"));
        boolean registered = ResourceLoader.registerBuiltinPack(
                ID,
                modContainer,
                Component.translatable("pack.kaleidoscope_cookery.legacy_resources_pack.title"),
                PackActivationType.NORMAL);
        if (!registered) {
            throw new IllegalStateException("Missing built-in legacy resource pack: " + ID);
        }
    }
}
