package com.github.ysbbbbbb.kaleidoscopecookery.client.network;

import com.github.ysbbbbbb.kaleidoscopecookery.network.message.FlatulenceMessage;
import com.github.ysbbbbbb.kaleidoscopecookery.network.message.ThrowBaoziMessage;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

@Environment(EnvType.CLIENT)
public final class ClientNetworkHandler {
    private ClientNetworkHandler() {
    }

    public static void sendFlatulence() {
        sendIfAvailable(FlatulenceMessage.TYPE, FlatulenceMessage.INSTANCE);
    }

    public static boolean sendThrowBaozi() {
        return sendIfAvailable(ThrowBaoziMessage.TYPE, ThrowBaoziMessage.INSTANCE);
    }

    private static <T extends CustomPacketPayload> boolean sendIfAvailable(CustomPacketPayload.Type<T> type, T payload) {
        if (ClientPlayNetworking.canSend(type)) {
            ClientPlayNetworking.send(payload);
            return true;
        }
        return false;
    }
}
