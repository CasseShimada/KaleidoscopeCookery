package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenwareRacksBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.*;
import net.minecraft.resources.Identifier;
import snownee.jade.api.IWailaCommonRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public class ModPlugin implements IWailaPlugin {
    public static final Identifier SHAWARMA_SPIT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "shawarma_spit");
    public static final Identifier POT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot");
    public static final Identifier STOCKPOT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot");
    public static final Identifier CHOPPING_BOARD = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "chopping_board");
    public static final Identifier MILLSTONE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "millstone");
    public static final Identifier ENAMEL_BASIN = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "enamel_basin");
    public static final Identifier TABLE = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "table");
    public static final Identifier FRUIT_BASKET = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "fruit_basket");
    public static final Identifier KITCHENWARE_RACK = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "kitchenware_rack");
    public static final Identifier OIL_POT = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "oil_pot");
    public static final Identifier RECIPE_BLOCK = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "recipe_block");
    public static final Identifier STEAMER = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "steamer");

    @Override
    public void register(IWailaCommonRegistration registration) {
        registration.registerItemStorage(FruitBasketComponentProvider.INSTANCE, FruitBasketBlockEntity.class);
        registration.registerItemStorage(KitchenwareRackComponentProvider.INSTANCE, KitchenwareRacksBlockEntity.class);
        registration.registerItemStorage(TableComponentProvider.INSTANCE, TableBlockEntity.class);
        registration.registerItemStorage(PotComponentProvider.INSTANCE, PotBlockEntity.class);
        registration.registerItemStorage(StockpotComponentProvider.INSTANCE, StockpotBlockEntity.class);
        registration.registerItemStorage(SteamerComponentProvider.INSTANCE, SteamerBlockEntity.class);
    }
}
