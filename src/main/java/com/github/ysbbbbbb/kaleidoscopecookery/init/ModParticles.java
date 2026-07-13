package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.particle.StockpotParticleOptions;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class ModParticles {
    public static final SimpleParticleType COOKING = FabricParticleTypes.simple();
    public static final ParticleType<StockpotParticleOptions> STOCKPOT = FabricParticleTypes.complex(
            false, StockpotParticleOptions.CODEC, StockpotParticleOptions.STREAM_CODEC);

    private ModParticles() {
    }

    public static void registerParticles() {
        register("cooking_particle", COOKING);
        register("stockpot_particle", STOCKPOT);
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static <T extends ParticleOptions> void register(String path, ParticleType<T> particleType) {
        Registry.register(BuiltInRegistries.PARTICLE_TYPE, id(path), particleType);
    }
}
