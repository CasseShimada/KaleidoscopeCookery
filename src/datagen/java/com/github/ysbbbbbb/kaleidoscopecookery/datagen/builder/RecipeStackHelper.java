package com.github.ysbbbbbb.kaleidoscopecookery.datagen.builder;

import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.ItemLike;

public final class RecipeStackHelper {
    private RecipeStackHelper() {
    }

    public static ItemStackTemplate template(ItemLike itemLike) {
        return template(itemLike, 1);
    }

    public static ItemStackTemplate template(ItemLike itemLike, int count) {
        return new ItemStackTemplate(itemLike.asItem(), count);
    }
}
