package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ThrowableBaoziEntity;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;

public final class ModEntities {
    private static final ResourceKey<EntityType<?>> SIT_KEY = key("sit");
    private static final ResourceKey<EntityType<?>> SCARECROW_KEY = key("scarecrow");
    private static final ResourceKey<EntityType<?>> THROWABLE_BAOZI_KEY = key("throwable_baozi");

    public static final EntityType<SitEntity> SIT = EntityType.Builder.<SitEntity>of(SitEntity::new, MobCategory.MISC)
            .sized(0.5F, 0.1F)
            .clientTrackingRange(10)
            .noSummon()
            .build(SIT_KEY);
    public static final EntityType<ScarecrowEntity> SCARECROW = EntityType.Builder
            .<ScarecrowEntity>of(ScarecrowEntity::new, MobCategory.MISC)
            .sized(0.5F, 2.375F)
            .clientTrackingRange(10)
            .build(SCARECROW_KEY);
    public static final EntityType<ThrowableBaoziEntity> THROWABLE_BAOZI = EntityType.Builder
            .<ThrowableBaoziEntity>of(ThrowableBaoziEntity::new, MobCategory.MISC)
            .sized(0.25F, 0.25F)
            .clientTrackingRange(4)
            .updateInterval(10)
            .build(THROWABLE_BAOZI_KEY);

    private ModEntities() {
    }

    public static void registerEntities() {
        register(SIT_KEY, SIT);
        register(SCARECROW_KEY, SCARECROW);
        register(THROWABLE_BAOZI_KEY, THROWABLE_BAOZI);

        FabricDefaultAttributeRegistry.register(SCARECROW, ScarecrowEntity.createAttributes());
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static ResourceKey<EntityType<?>> key(String path) {
        return ResourceKey.create(Registries.ENTITY_TYPE, id(path));
    }

    private static <T extends Entity> void register(ResourceKey<EntityType<?>> key, EntityType<T> entityType) {
        Registry.register(BuiltInRegistries.ENTITY_TYPE, key, entityType);
    }
}
