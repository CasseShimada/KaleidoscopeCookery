package com.github.ysbbbbbb.kaleidoscopecookery.network.message;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ThrowBaoziMessage() implements CustomPacketPayload {
    public static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "throwing_baozi");
    public static final Type<ThrowBaoziMessage> TYPE = new Type<>(ID);
    public static final ThrowBaoziMessage INSTANCE = new ThrowBaoziMessage();
    public static final StreamCodec<RegistryFriendlyByteBuf, ThrowBaoziMessage> STREAM_CODEC = StreamCodec.unit(INSTANCE);

    @Override
    public Type<ThrowBaoziMessage> type() {
        return TYPE;
    }
}
