package com.bleudev.random_mh.network.payload;

import com.bleudev.random_mh.network.RandomMhPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record TickRandomMhBossBarPayload(int tick, int duration) implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public static Type<@NotNull TickRandomMhBossBarPayload> TYPE = new Type<>(RandomMhPackets.PAYLOAD_TICK_RANDOM_MH_BOSS_BAR);
    public static StreamCodec<@NotNull FriendlyByteBuf, @NotNull TickRandomMhBossBarPayload> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, TickRandomMhBossBarPayload::tick,
        ByteBufCodecs.INT, TickRandomMhBossBarPayload::duration,
        TickRandomMhBossBarPayload::new
    );
}
