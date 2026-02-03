package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

public enum OilPotComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (!(blockAccessor.getBlockEntity() instanceof OilPotBlockEntity oilPot)) {
            return;
        }
        int count = oilPot.getOilCount();
        Component text;
        if (count > 0) {
            text = Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.count", count);
        } else {
            text = Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.empty");
        }
        iTooltip.add(JadeUI.text(text));
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.OIL_POT;
    }
}
