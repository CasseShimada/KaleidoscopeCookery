package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.IServerExtensionProvider;
import snownee.jade.api.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public enum TableComponentProvider implements IServerExtensionProvider<ItemStack> {
    INSTANCE;

    @Override
    @Nullable
    public List<ViewGroup<ItemStack>> getGroups(Accessor<?> accessor) {
        Object target = accessor.getTarget();
        if (target instanceof TableBlockEntity table) {
            ItemStackContainer handler = ItemStackContainer.copyOf(table.getItems());
            List<ItemStack> list = new ArrayList<>();
            for (int i = 0; i < handler.size(); i++) {
                ItemStack stack = handler.get(i);
                if (stack.isEmpty()) {
                    continue;
                }
                list.add(stack.copy());
            }
            return List.of(new ViewGroup<>(list));
        }
        return null;
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.TABLE;
    }
}
