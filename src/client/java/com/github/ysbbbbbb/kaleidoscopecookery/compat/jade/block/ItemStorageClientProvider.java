package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import snownee.jade.api.Accessor;
import snownee.jade.api.view.ClientViewGroup;
import snownee.jade.api.view.IClientExtensionProvider;
import snownee.jade.api.view.ItemView;
import snownee.jade.api.view.ViewGroup;

import java.util.List;

public enum ItemStorageClientProvider implements IClientExtensionProvider<ItemStack, ItemView> {
    FRUIT_BASKET(ModPlugin.FRUIT_BASKET),
    KITCHENWARE_RACK(ModPlugin.KITCHENWARE_RACK),
    TABLE(ModPlugin.TABLE),
    POT(ModPlugin.POT),
    STOCKPOT(ModPlugin.STOCKPOT),
    STEAMER(ModPlugin.STEAMER);

    private final Identifier uid;

    ItemStorageClientProvider(Identifier uid) {
        this.uid = uid;
    }

    @Override
    public List<ClientViewGroup<ItemView>> getClientGroups(
            Accessor<?> accessor, List<ViewGroup<ItemStack>> groups) {
        return ClientViewGroup.map(groups, ItemView::new, null);
    }

    @Override
    public Identifier getUid() {
        return uid;
    }
}
