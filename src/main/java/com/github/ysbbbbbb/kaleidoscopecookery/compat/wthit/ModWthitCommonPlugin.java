package com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit;

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
import mcp.mobius.waila.api.ICommonRegistrar;
import mcp.mobius.waila.api.IWailaCommonPlugin;

public final class ModWthitCommonPlugin implements IWailaCommonPlugin {
    @Override
    public void register(ICommonRegistrar registrar) {
        WthitOptions.SERVER_FEATURES.forEach(key -> registrar.featureConfig(key, false));
        WthitOptions.CLIENT_FEATURES.forEach(key -> registrar.featureConfig(key, true));

        registrar.blockData(WthitDataProvider.INSTANCE, FruitBasketBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, KitchenwareRacksBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, TableBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, PotBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, StockpotBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, SteamerBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, ShawarmaSpitBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, ChoppingBoardBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, OilPotBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, MillstoneBlockEntity.class);
        registrar.blockData(WthitDataProvider.INSTANCE, RecipeBlockEntity.class);
    }
}
