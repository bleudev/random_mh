package com.bleudev.random_mh.network.payload;

import com.bleudev.random_mh.RandomMhHelper;
import com.bleudev.random_mh.network.RandomMhPackets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

public record RolePayload(@NotNull RandomMhHelper.MhRole role) implements CustomPacketPayload {
    @Override
    public @NotNull Type<? extends @NotNull CustomPacketPayload> type() {
        return TYPE;
    }

    public static Type<@NotNull RolePayload> TYPE = new Type<>(RandomMhPackets.PAYLOAD_ROLE);
    public static StreamCodec<@NotNull FriendlyByteBuf, @NotNull RolePayload> STREAM_CODEC = StreamCodec.composite(
        RandomMhHelper.MhRole.STREAM_CODEC, RolePayload::role,
        RolePayload::new
    );
}
