package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum StockpotComponentProvider implements IServerExtensionProvider<ItemStack> {
    INSTANCE;

    @Override
    @Nullable
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        Object target = accessor.getTarget();
        if (target instanceof StockpotBlockEntity stockpot) {
            if (stockpot.getStatus() < IStockpot.FINISHED) {
                List<ItemStack> list = stockpot.getInputs().stream()
                        .filter(stack -> !stack.isEmpty())
                        .map(ItemStack::copy)
                        .toList();
                return List.of(new ViewGroup<>(list));
            }
        }
        return null;
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.STOCKPOT;
    }
}
