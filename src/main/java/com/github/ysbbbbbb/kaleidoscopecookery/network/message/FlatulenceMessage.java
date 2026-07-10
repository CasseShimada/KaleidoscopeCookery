package com.github.ysbbbbbb.kaleidoscopecookery.network.message;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record FlatulenceMessage() implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flatulence");
    public static final Type<FlatulenceMessage> TYPE = new Type<>(ID);
    public static final FlatulenceMessage INSTANCE = new FlatulenceMessage();
    public static final StreamCodec<RegistryFriendlyByteBuf, FlatulenceMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<FlatulenceMessage> type() {
        return TYPE;
    }
}
