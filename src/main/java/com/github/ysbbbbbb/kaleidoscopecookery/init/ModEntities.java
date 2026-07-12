package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ThrowableBaoziEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public final class ModEntities {
    public static final EntityType<SitEntity> SIT = SitEntity.TYPE;
    public static final EntityType<ScarecrowEntity> SCARECROW = ScarecrowEntity.TYPE;
    public static final EntityType<ThrowableBaoziEntity> THROWABLE_BAOZI = ThrowableBaoziEntity.TYPE;

    private ModEntities() {
    }

    public static void registerEntities() {
        register("sit", SIT);
        register("scarecrow", SCARECROW);
        register("throwable_baozi", THROWABLE_BAOZI);

        // Register entity attributes
        FabricDefaultAttributeRegistry.register(SCARECROW, LivingEntity.createLivingAttributes());
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static <T extends net.minecraft.world.entity.Entity> void register(String path, EntityType<T> entityType) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, id(path), entityType);
    }
}
