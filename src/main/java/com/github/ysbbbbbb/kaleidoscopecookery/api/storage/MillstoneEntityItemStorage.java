package com.github.ysbbbbbb.kaleidoscopecookery.api.storage;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.fabric.api.lookup.v1.entity.EntityApiLookup;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

/**
 * Item storage exposed by entities that can automatically supply a millstone.
 * Fabric Transfer API has no standard entity item-storage lookup, so integrations
 * may register their entity types with this lookup.
 */
public final class MillstoneEntityItemStorage {
    public static final EntityApiLookup<Storage<ItemVariant>, Void> SOURCE = EntityApiLookup.get(
            Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone_entity_item_storage"),
            Storage.asClass(), Void.class);

    private MillstoneEntityItemStorage() {
    }

    public static @Nullable Storage<ItemVariant> find(Entity entity) {
        return SOURCE.find(entity, null);
    }
}
