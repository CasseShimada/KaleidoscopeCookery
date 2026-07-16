package com.github.ysbbbbbb.kaleidoscopecookery.event.interaction;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;

public final class FruitBasketTakeOutEvent {
    private static final Identifier MAID_SMART_SLAB = Identifier.fromNamespaceAndPath(
            "touhou_little_maid", "smart_slab_has_maid");

    private FruitBasketTakeOutEvent() {
    }

    public static void register() {
        UseBlockCallback.EVENT.register(FruitBasketTakeOutEvent::onUseBlock);
    }

    private static InteractionResult onUseBlock(Player player, Level level, InteractionHand hand,
                                                BlockHitResult hitResult) {
        if (hand != InteractionHand.MAIN_HAND || !player.isSecondaryUseActive()) {
            return InteractionResult.PASS;
        }
        ItemStack stack = player.getItemInHand(hand);
        if (isExcludedItem(stack)
                || stack.getItem() instanceof BlockItem && hitResult.getDirection() != Direction.UP
                || !(level.getBlockEntity(hitResult.getBlockPos()) instanceof FruitBasketBlockEntity fruitBasket)) {
            return InteractionResult.PASS;
        }
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        fruitBasket.takeOut(player);
        return InteractionResult.CONSUME;
    }

    private static boolean isExcludedItem(ItemStack stack) {
        return stack.is(Items.DEBUG_STICK)
                || stack.is(Items.FIREWORK_ROCKET)
                || BuiltInRegistries.ITEM.getOptional(MAID_SMART_SLAB).map(stack::is).orElse(false);
    }
}
