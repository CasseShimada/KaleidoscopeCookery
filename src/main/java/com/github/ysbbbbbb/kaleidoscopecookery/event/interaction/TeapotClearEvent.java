package com.github.ysbbbbbb.kaleidoscopecookery.event.interaction;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeapotItem;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;

public final class TeapotClearEvent {
    private TeapotClearEvent() {
    }

    public static void register() {
        AttackBlockCallback.EVENT.register((player, level, hand, pos, direction) -> {
            if (hand != InteractionHand.MAIN_HAND
                    || !player.isSecondaryUseActive()
                    || !player.getMainHandItem().is(ModItems.TEAPOT)) {
                return InteractionResult.PASS;
            }
            if (!level.isClientSide()) {
                TeapotItem.clearAll(player.getMainHandItem(), player);
            }
            return InteractionResult.SUCCESS;
        });
    }
}
