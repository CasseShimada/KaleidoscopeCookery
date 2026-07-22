package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.overlay.CookeryOverlayData;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum FruitBasketComponentProvider implements IServerExtensionProvider<ItemStack> {
    INSTANCE;

    @Override
    @Nullable
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        Object target = accessor.getTarget();
        if (target instanceof FruitBasketBlockEntity fruitBasket) {
            List<ItemStack> list = CookeryOverlayData.itemStorage(fruitBasket);
            return List.of(new ViewGroup<>(list));
        }
        return null;
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.FRUIT_BASKET;
    }
}
