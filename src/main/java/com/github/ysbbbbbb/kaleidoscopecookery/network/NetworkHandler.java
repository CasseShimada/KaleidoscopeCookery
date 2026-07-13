package com.github.ysbbbbbb.kaleidoscopecookery.network;

import com.github.ysbbbbbb.kaleidoscopecookery.entity.ThrowableBaoziEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.network.message.FlatulenceMessage;
import com.github.ysbbbbbb.kaleidoscopecookery.network.message.ThrowBaoziMessage;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class NetworkHandler {
    private static final int FLATULENCE_PARTICLE_COUNT = 10;
    private static final double FLATULENCE_PARTICLE_Y_OFFSET = 0.25;
    private static final double FLATULENCE_PARTICLE_SPREAD = 0.25;
    private static final double FLATULENCE_PARTICLE_SPEED = 0.1;
    private static final long FLATULENCE_COOLDOWN_TICKS = 10L;
    private static final float FLATULENCE_SOUND_VOLUME = 1.0F;
    private static final float FLATULENCE_SOUND_BASE_PITCH = 0.8F;
    private static final float FLATULENCE_SOUND_PITCH_VARIATION = 0.4F;
    private static final Vec3 FLATULENCE_JUMP_VELOCITY = new Vec3(0, 0.75, 0);
    private static final Map<UUID, Long> LAST_FLATULENCE_TICKS = new HashMap<>();
    private static final int THROWN_BAOZI_COUNT = 1;
    private static final float BAOZI_THROW_SOUND_VOLUME = 0.5F;
    private static final float BAOZI_THROW_SOUND_BASE_PITCH = 0.4F;
    private static final float BAOZI_THROW_SOUND_PITCH_VARIATION = 0.4F;
    private static final float BAOZI_THROW_SOUND_PITCH_OFFSET = 0.8F;
    private static final float BAOZI_THROW_INACCURACY = 1.0F;
    private static final float BAOZI_THROW_VELOCITY = 1.5F;
    private static final int BAOZI_THROW_COOLDOWN_TICKS = 5;

    private NetworkHandler() {
    }

    public static void init() {
        registerServerboundPayloads();
        registerServerboundReceivers();
        registerConnectionEvents();
    }

    private static void registerServerboundPayloads() {
        PayloadTypeRegistry.serverboundPlay().register(FlatulenceMessage.TYPE, FlatulenceMessage.STREAM_CODEC);
        PayloadTypeRegistry.serverboundPlay().register(ThrowBaoziMessage.TYPE, ThrowBaoziMessage.STREAM_CODEC);
    }

    private static void registerServerboundReceivers() {
        ServerPlayNetworking.registerGlobalReceiver(FlatulenceMessage.TYPE, NetworkHandler::handleFlatulence);
        ServerPlayNetworking.registerGlobalReceiver(ThrowBaoziMessage.TYPE, NetworkHandler::handleThrowBaozi);
    }

    private static void registerConnectionEvents() {
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) ->
                LAST_FLATULENCE_TICKS.remove(handler.getPlayer().getUUID()));
    }

    private static void handleFlatulence(FlatulenceMessage payload, ServerPlayNetworking.Context context) {
        handleFlatulence(context.player());
    }

    private static void handleFlatulence(ServerPlayer player) {
        if (!isValidPlayer(player)) {
            return;
        }
        if (!canUseFlatulence(player)) {
            return;
        }
        applyFlatulenceJump(player);
        playFlatulenceEffects(player);
    }

    private static boolean canUseFlatulence(ServerPlayer player) {
        if (!player.hasEffect(ModEffects.FLATULENCE)) {
            return false;
        }

        long gameTime = player.level().getGameTime();
        UUID uuid = player.getUUID();
        Long lastUseTick = LAST_FLATULENCE_TICKS.get(uuid);
        if (lastUseTick != null && gameTime >= lastUseTick && gameTime - lastUseTick < FLATULENCE_COOLDOWN_TICKS) {
            return false;
        }

        LAST_FLATULENCE_TICKS.put(uuid, gameTime);
        return true;
    }

    private static void applyFlatulenceJump(ServerPlayer player) {
        player.addDeltaMovement(FLATULENCE_JUMP_VELOCITY);
        player.hurtMarked = true;
    }

    private static void playFlatulenceEffects(ServerPlayer player) {
        ServerLevel level = player.level();
        level.sendParticles(ParticleTypes.CLOUD,
                player.getX(), player.getY() + FLATULENCE_PARTICLE_Y_OFFSET, player.getZ(),
                FLATULENCE_PARTICLE_COUNT,
                FLATULENCE_PARTICLE_SPREAD, FLATULENCE_PARTICLE_SPREAD, FLATULENCE_PARTICLE_SPREAD,
                FLATULENCE_PARTICLE_SPEED);
        level.playSound(null, player.blockPosition(), ModSounds.ENTITY_FART,
                SoundSource.PLAYERS, FLATULENCE_SOUND_VOLUME, getFlatulenceSoundPitch(level));
    }

    private static float getFlatulenceSoundPitch(ServerLevel level) {
        return FLATULENCE_SOUND_BASE_PITCH + level.getRandom().nextFloat() * FLATULENCE_SOUND_PITCH_VARIATION;
    }

    private static void handleThrowBaozi(ThrowBaoziMessage payload, ServerPlayNetworking.Context context) {
        handleThrowBaozi(context.player());
    }

    private static void handleThrowBaozi(ServerPlayer player) {
        if (!isValidPlayer(player)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!canThrowBaozi(player, stack)) {
            return;
        }
        throwBaozi(player, stack);
    }

    private static boolean isValidPlayer(ServerPlayer player) {
        return !player.hasDisconnected() && !player.isRemoved();
    }

    private static boolean canThrowBaozi(ServerPlayer player, ItemStack stack) {
        return player.isSecondaryUseActive()
                && stack.is(ModItems.BAOZI)
                && !player.getCooldowns().isOnCooldown(stack);
    }

    private static void throwBaozi(ServerPlayer player, ItemStack stack) {
        ServerLevel level = player.level();
        ThrowableBaoziEntity baozi = new ThrowableBaoziEntity(level, player);
        baozi.setItem(stack.copyWithCount(THROWN_BAOZI_COUNT));
        baozi.shootFromRotation(player, player.getXRot(), player.getYRot(), 0,
                BAOZI_THROW_VELOCITY, BAOZI_THROW_INACCURACY);
        if (!level.addFreshEntity(baozi)) {
            return;
        }

        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL,
                BAOZI_THROW_SOUND_VOLUME, getBaoziThrowSoundPitch(level));
        player.getCooldowns().addCooldown(stack, BAOZI_THROW_COOLDOWN_TICKS);
        stack.consume(THROWN_BAOZI_COUNT, player);
    }

    private static float getBaoziThrowSoundPitch(ServerLevel level) {
        return BAOZI_THROW_SOUND_BASE_PITCH
                / (level.getRandom().nextFloat() * BAOZI_THROW_SOUND_PITCH_VARIATION
                + BAOZI_THROW_SOUND_PITCH_OFFSET);
    }
}
