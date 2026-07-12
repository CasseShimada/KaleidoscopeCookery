package com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.block;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jade.ModPlugin;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.block.entity.BlockEntity;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;
import snownee.jade.api.ui.JadeUI;

public enum MillstoneComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public void appendTooltip(ITooltip tooltip, BlockAccessor accessor, IPluginConfig pluginConfig) {
        NinePart part = accessor.getBlockState().getValue(MillstoneBlock.PART);
        BlockPos pos = accessor.getPosition();
        BlockPos centerPos = pos.subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        BlockEntity te = accessor.getLevel().getBlockEntity(centerPos);
        if (!(te instanceof MillstoneBlockEntity millstone)) {
            return;
        }
        if (millstone.getInput().isEmpty() && millstone.getOutput().isEmpty()) {
            return;
        }
        tooltip.add(JadeUI.item(millstone.getInput()));
        tooltip.append(JadeUI.progressArrow(millstone.getProgressPercent()));
        tooltip.append(JadeUI.item(millstone.getOutput()));
    }

    @Override
    public Identifier getUid() {
        return ModPlugin.MILLSTONE;
    }
}
