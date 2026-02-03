package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class OilPotItem extends BlockItem {
    public static final Identifier HAS_OIL_PROPERTY = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "has_oil");
    public static final int MAX_COUNT = OilPotBlockEntity.MAX_OIL_COUNT;

    private static final int NO_OIL = 0;
    private static final int HAS_OIL = 1;

    public OilPotItem(Properties properties) {
        super(ModBlocks.OIL_POT, properties.stacksTo(1));
    }

    public static void setOilCount(ItemStack stack, int count) {
        count = Mth.clamp(count, 0, MAX_COUNT);
        if (count <= 0) {
            stack.remove(ModDataComponents.OIL_POT_COUNT);
        } else {
            stack.set(ModDataComponents.OIL_POT_COUNT, count);
        }
    }

    public static int getOilCount(ItemStack stack) {
        Integer count = stack.get(ModDataComponents.OIL_POT_COUNT);
        return count == null ? 0 : count;
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

    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (hasOil(stack)) {
            return HAS_OIL;
        }
        return NO_OIL;
    }

    @Override
        public void appendHoverText(ItemStack pStack, TooltipContext context, TooltipDisplay display, Consumer<Component> pTooltipComponents, TooltipFlag pIsAdvanced) {
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
