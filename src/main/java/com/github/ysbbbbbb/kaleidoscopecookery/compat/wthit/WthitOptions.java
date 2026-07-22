package com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.resources.Identifier;

import java.util.List;

public final class WthitOptions {
    public static final Identifier FRUIT_BASKET = id("fruit_basket");
    public static final Identifier KITCHENWARE_RACK = id("kitchenware_rack");
    public static final Identifier TABLE = id("table");
    public static final Identifier POT = id("pot");
    public static final Identifier STOCKPOT = id("stockpot");
    public static final Identifier STEAMER = id("steamer");
    public static final Identifier SHAWARMA_SPIT = id("shawarma_spit");
    public static final Identifier CHOPPING_BOARD = id("chopping_board");
    public static final Identifier ENAMEL_BASIN = id("enamel_basin");
    public static final Identifier FOOD_BITE_BLOCK = id("food_bite_block");
    public static final Identifier OIL_POT = id("oil_pot");
    public static final Identifier MILLSTONE = id("millstone");
    public static final Identifier RECIPE_BLOCK = id("recipe_block");

    public static final List<Identifier> SERVER_FEATURES = List.of(
            FRUIT_BASKET, KITCHENWARE_RACK, TABLE, POT, STOCKPOT, STEAMER,
            SHAWARMA_SPIT, CHOPPING_BOARD, OIL_POT, MILLSTONE, RECIPE_BLOCK);
    public static final List<Identifier> CLIENT_FEATURES = List.of(ENAMEL_BASIN, FOOD_BITE_BLOCK);

    private WthitOptions() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }
}
