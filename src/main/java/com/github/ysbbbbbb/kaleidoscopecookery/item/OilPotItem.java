package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.function.Consumer;

public class OilPotItem extends CookeryTooltipBlockItem {
    public static final int MAX_COUNT = OilPotBlockEntity.MAX_OIL_COUNT;
    private static final String FORGE_OIL_COUNT_KEY = "oil_count";

    public OilPotItem(Properties properties) {
        super(ModBlocks.OIL_POT, properties.stacksTo(1));
    }

    public static void setOilCount(ItemStack stack, int count) {
        count = Mth.clamp(count, 0, MAX_COUNT);
        clearLegacyOilCount(stack);
        if (count <= 0) {
            stack.remove(ModDataComponents.OIL_POT_OIL_COUNT);
        } else {
            stack.set(ModDataComponents.OIL_POT_OIL_COUNT, count);
        }
    }

    public static int getOilCount(ItemStack stack) {
        Integer count = stack.get(ModDataComponents.OIL_POT_OIL_COUNT);
        if (count != null) {
            return Mth.clamp(count, 0, MAX_COUNT);
        }

        count = stack.get(ModDataComponents.OIL_POT_COUNT);
        if (count != null) {
            return Mth.clamp(count, 0, MAX_COUNT);
        }

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return 0;
        }
        return Mth.clamp(customData.copyTag().getIntOr(FORGE_OIL_COUNT_KEY, 0), 0, MAX_COUNT);
    }

    @Override
    public void inventoryTick(ItemStack stack, ServerLevel level, Entity entity, EquipmentSlot slot) {
        super.inventoryTick(stack, level, entity, slot);
        if (!stack.has(ModDataComponents.OIL_POT_OIL_COUNT)) {
            int legacyCount = getOilCount(stack);
            if (legacyCount > 0) {
                setOilCount(stack, legacyCount);
            }
        }
    }

    private static void clearLegacyOilCount(ItemStack stack) {
        stack.remove(ModDataComponents.OIL_POT_COUNT);

        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        if (customData == null) {
            return;
        }
        CompoundTag tag = customData.copyTag();
        if (tag.remove(FORGE_OIL_COUNT_KEY) == null) {
            return;
        }
        if (tag.isEmpty()) {
            stack.remove(DataComponents.CUSTOM_DATA);
        } else {
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }
    }

    public static boolean hasOil(ItemStack stack) {
        return getOilCount(stack) > 0;
    }

    public static void shrinkOilCount(ItemStack stack) {
        int currentCount = getOilCount(stack);
        if (currentCount > 0) {
            setOilCount(stack, currentCount - 1);
        }
    }

    public static ItemStack getFullOilPot() {
        ItemStack stack = new ItemStack(ModBlocks.OIL_POT);
        setOilCount(stack, MAX_COUNT);
        return stack;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack pStack, TooltipContext context, TooltipDisplay display, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
        int oilCount = getOilCount(pStack);
        if (oilCount > 0) {
            pTooltipComponents.accept(Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.count", oilCount)
                    .withStyle(ChatFormatting.GRAY));
        } else {
            pTooltipComponents.accept(Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.empty")
                    .withStyle(ChatFormatting.GRAY));
        }
    }
}
