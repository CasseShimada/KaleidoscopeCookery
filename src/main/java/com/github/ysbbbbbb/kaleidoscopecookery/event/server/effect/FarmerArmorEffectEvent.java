package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;

public final class FarmerArmorEffectEvent {
    private static final int ARMOR_CHECK_INTERVAL_TICKS = 20;
    private static final int DOLPHINS_GRACE_REFRESH_TICKS = 25;
    private static final EquipmentSlot[] FARMER_ARMOR_SLOTS = {
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET
    };

    private FarmerArmorEffectEvent() {
    }

    public static void onLivingTick(LivingEntity entity) {
        if (entity.level().isClientSide() || entity.tickCount % ARMOR_CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        if (!entity.isInWater()) {
            return;
        }
        if (!hasFullFarmerArmor(entity)) {
            return;
        }
        entity.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE,
                DOLPHINS_GRACE_REFRESH_TICKS, 0, true, true, false));
    }

    private static boolean hasFullFarmerArmor(LivingEntity entity) {
        for (EquipmentSlot slot : FARMER_ARMOR_SLOTS) {
            if (!entity.getItemBySlot(slot).is(TagMod.FARMER_ARMOR)) {
                return false;
            }
        }
        return true;
    }
}
