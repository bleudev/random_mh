package com.bleudev.random_mh.network.payload;

import com.bleudev.random_mh.config.RandomMhGameConfig;
import com.bleudev.random_mh.network.RandomMhPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record C2SConfigPayload(RandomMhGameConfig config) implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }
    public static final Type<@NotNull C2SConfigPayload> TYPE = new Type<>(RandomMhPackets.PAYLOAD_C2S_CONFIG);
    public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull C2SConfigPayload> STREAM_CODEC = StreamCodec.composite(
        RandomMhGameConfig.STREAM_CODEC, C2SConfigPayload::config,
        C2SConfigPayload::new
    );
}
