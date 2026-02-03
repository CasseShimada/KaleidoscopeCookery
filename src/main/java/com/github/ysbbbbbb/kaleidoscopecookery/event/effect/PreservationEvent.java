package com.github.ysbbbbbb.kaleidoscopecookery.event.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import static net.minecraft.world.effect.MobEffectCategory.HARMFUL;

public class PreservationEvent {
    public static void register() {
        UseItemCallback.EVENT.register(PreservationEvent::onUseItem);
    }

    private static InteractionResult onUseItem(Player player, Level world, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.hasEffect(ModEffects.PRESERVATION) && stack.is(TagMod.PRESERVATION_FOOD)) {
            Consumable consumable = stack.get(DataComponents.CONSUMABLE);
            if (consumable == null) {
                return InteractionResult.PASS;
            }
            for (ConsumeEffect effect : consumable.onConsumeEffects()) {
                if (!(effect instanceof ApplyStatusEffectsConsumeEffect apply)) {
                    continue;
                }
                for (MobEffectInstance instance : apply.effects()) {
                    Holder<MobEffect> mobEffect = instance.getEffect();
                    if (mobEffect.value().getCategory() == HARMFUL) {
                        player.removeEffect(mobEffect);
                    }
                }
            }
        }
        return InteractionResult.PASS;
    }
}
