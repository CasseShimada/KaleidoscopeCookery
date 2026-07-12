package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenwareRacksBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public enum KitchenwareRackComponentProvider implements IServerExtensionProvider<ItemStack> {
    INSTANCE;

    @Override
    @Nullable
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        Object target = accessor.getTarget();
        if (target instanceof KitchenwareRacksBlockEntity kitchenwareRacks) {
            List<ItemStack> list = new ArrayList<>();
            if (!kitchenwareRacks.getItemLeft().isEmpty()) {
                list.add(kitchenwareRacks.getItemLeft());
            }
            if (!kitchenwareRacks.getItemRight().isEmpty()) {
                list.add(kitchenwareRacks.getItemRight());
            }
            return List.of(new ViewGroup<>(list));
        }
        return null;
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.KITCHENWARE_RACK;
    }
}
