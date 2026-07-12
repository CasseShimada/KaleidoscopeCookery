package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IPot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum PotComponentProvider implements IServerExtensionProvider<ItemStack> {
    INSTANCE;

    @Override
    @Nullable
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        Object target = accessor.getTarget();
        if (target instanceof PotBlockEntity pot) {
            if (pot.getStatus() < IPot.FINISHED) {
                List<ItemStack> list = pot.getInputs().stream().filter(s -> !s.isEmpty()).toList();
                return List.of(new ViewGroup<>(list));
            }
        }
        return null;
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.POT;
    }
}
