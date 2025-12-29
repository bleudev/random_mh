package com.bleudev.random_mh.network;

import com.bleudev.random_mh.network.payload.RolePayload;
import com.bleudev.random_mh.network.payload.TickRandomMhBossBarPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.resources.Identifier;

import static com.bleudev.random_mh.RandomMh.getIdentifier;

public class RandomMhPackets {
    public static final Identifier PAYLOAD_ROLE = getIdentifier("role_payload");
    public static final Identifier PAYLOAD_TICK_RANDOM_MH_BOSS_BAR = getIdentifier("tick_random_mh_boss_bar");

    public static void initialize() {
        NetworkManager.registerS2CPayloadType(RolePayload.TYPE, RolePayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(TickRandomMhBossBarPayload.TYPE, TickRandomMhBossBarPayload.STREAM_CODEC);
    }
}
