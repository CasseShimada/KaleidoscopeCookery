package com.github.ysbbbbbb.kaleidoscopecookery.event.server.effect;

import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;

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

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(FarmerArmorEffectEvent::onServerTick);
    }

    private static void onServerTick(MinecraftServer server) {
        if (server.getTickCount() % ARMOR_CHECK_INTERVAL_TICKS != 0) {
            return;
        }
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            applyFarmerArmorEffect(player);
        }
    }

    private static void applyFarmerArmorEffect(ServerPlayer player) {
        if (!player.isInWater()) {
            return;
        }
        if (!hasFullFarmerArmor(player)) {
            return;
        }
        player.addEffect(new MobEffectInstance(MobEffects.DOLPHINS_GRACE, DOLPHINS_GRACE_REFRESH_TICKS, 0, true, true, false));
    }

    private static boolean hasFullFarmerArmor(ServerPlayer player) {
        for (EquipmentSlot slot : FARMER_ARMOR_SLOTS) {
            if (!player.getItemBySlot(slot).is(TagMod.FARMER_ARMOR)) {
                return false;
            }
        }
        return true;
    }
}
