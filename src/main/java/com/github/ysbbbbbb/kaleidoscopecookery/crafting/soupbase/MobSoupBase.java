package com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase;

import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MobBucketItem;

import java.util.Objects;

public class MobSoupBase extends FluidSoupBase {
    private final EntityType<?> type;

    public MobSoupBase(Identifier name, Item bucket, int bubbleColor, EntityType<?> type) {
        super(name, bucket, bubbleColor);
        if (!(bucket instanceof MobBucketItem)) {
            throw new IllegalArgumentException("Mob bucket item must have a valid entity type!");
        }
        this.type = Objects.requireNonNull(type, "type");
    }

    public MobSoupBase(Identifier name, Item bucket, EntityType<?> type) {
        this(name, bucket, 0x3F76E4, type);
    }

    public EntityType<?> getEntityType() {
        return this.type;
    }
}
