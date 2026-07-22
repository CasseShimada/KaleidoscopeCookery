package com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.EnamelBasinBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenwareRacksBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import mcp.mobius.waila.api.IClientRegistrar;
import mcp.mobius.waila.api.IWailaClientPlugin;

public final class ModWthitClientPlugin implements IWailaClientPlugin {
    @Override
    public void register(IClientRegistrar registrar) {
        registrar.body(WthitComponentProvider.INSTANCE, FruitBasketBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, KitchenwareRacksBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, TableBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, PotBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, StockpotBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, SteamerBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, ShawarmaSpitBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, ChoppingBoardBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, OilPotBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, MillstoneBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, RecipeBlockEntity.class);
        registrar.body(WthitComponentProvider.INSTANCE, EnamelBasinBlock.class);
        registrar.body(WthitComponentProvider.INSTANCE, FoodBiteBlock.class);
        registrar.redirect(WthitComponentProvider.INSTANCE, MillstoneBlock.class);
    }
}
