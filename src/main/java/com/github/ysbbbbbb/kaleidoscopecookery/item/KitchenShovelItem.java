package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShovelItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents.KITCHEN_SHOVEL_HAS_OIL;

public class KitchenShovelItem extends ShovelItem {
    public KitchenShovelItem(Item.Properties properties) {
        super(ToolMaterial.IRON, -1.0F, -2.0F, properties);
    }

    public static void setHasOil(ItemStack stack, boolean hasOil) {
        stack.set(KITCHEN_SHOVEL_HAS_OIL, hasOil);
    }

    public static boolean hasOil(ItemStack stack) {
        if (stack.has(KITCHEN_SHOVEL_HAS_OIL)) {
            return Boolean.TRUE.equals(stack.get(KITCHEN_SHOVEL_HAS_OIL));
        }
        return false;
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        BlockPos clickedPos = context.getClickedPos();
        Level level = context.getLevel();
        Player player = context.getPlayer();
        ItemStack stack = context.getItemInHand();

        if (level.getBlockEntity(clickedPos) instanceof PotBlockEntity potBlockEntity
            && player != null && player.isSecondaryUseActive()
            && potBlockEntity.getStatus() == PotBlockEntity.FINISHED
            && !potBlockEntity.hasCarrier()) {
            if (!level.isClientSide()) {
                potBlockEntity.takeOutProduct(level, player, stack);
            }
            return level.isClientSide() ? InteractionResult.SUCCESS : InteractionResult.CONSUME;
        }

        InteractionResult result = super.useOn(context);
        if (!level.isClientSide() && result.consumesAction() && hasOil(context.getItemInHand())) {
            setHasOil(context.getItemInHand(), false);
        }
        return result;
    }
}
