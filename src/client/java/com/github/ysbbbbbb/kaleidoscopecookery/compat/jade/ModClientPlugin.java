package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ChoppingBoardBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.EnamelBasinBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.OilPotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.RecipeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.ChoppingBoardComponentProvider;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.EnamelBasinComponentProvider;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.ItemStorageClientProvider;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.MillstoneComponentProvider;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.OilPotComponentProvider;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.RecipeBlockComponentProvider;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block.ShawarmaSpitComponentProvider;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;

@WailaPlugin
public final class ModClientPlugin implements IWailaPlugin {
    @Override
    public void registerClient(IWailaClientRegistration registration) {
        registration.registerBlockComponent(ShawarmaSpitComponentProvider.INSTANCE, ShawarmaSpitBlock.class);
        registration.registerBlockComponent(ChoppingBoardComponentProvider.INSTANCE, ChoppingBoardBlock.class);
        registration.registerBlockComponent(EnamelBasinComponentProvider.INSTANCE, EnamelBasinBlock.class);
        registration.registerBlockComponent(OilPotComponentProvider.INSTANCE, OilPotBlock.class);
        registration.registerBlockComponent(MillstoneComponentProvider.INSTANCE, MillstoneBlock.class);
        registration.registerBlockComponent(RecipeBlockComponentProvider.INSTANCE, RecipeBlock.class);

        for (ItemStorageClientProvider provider : ItemStorageClientProvider.values()) {
            registration.registerItemStorageClient(provider);
        }
    }
}
