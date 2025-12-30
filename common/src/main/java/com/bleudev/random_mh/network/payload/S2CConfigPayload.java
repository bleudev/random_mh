package com.bleudev.random_mh.network.payload;

import com.bleudev.random_mh.network.RandomMhPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record S2CConfigPayload() implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<@NotNull S2CConfigPayload> TYPE = new Type<>(RandomMhPackets.PAYLOAD_S2C_CONFIG);
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull S2CConfigPayload> STREAM_CODEC = StreamCodec.unit(new S2CConfigPayload());
}
