package com.bleudev.random_mh.network;

import com.bleudev.random_mh.RandomMhHelper;
import com.bleudev.random_mh.network.payload.C2SConfigPayload;
import com.bleudev.random_mh.network.payload.RolePayload;
import com.bleudev.random_mh.network.payload.S2CConfigPayload;
import com.bleudev.random_mh.network.payload.TickRandomMhBossBarPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.bleudev.random_mh.RandomMh.getIdentifier;

public class RandomMhPackets {
    public static final Identifier PAYLOAD_ROLE = getIdentifier("role_payload");
    public static final Identifier PAYLOAD_TICK_RANDOM_MH_BOSS_BAR = getIdentifier("tick_random_mh_boss_bar");
    public static final Identifier PAYLOAD_S2C_CONFIG = getIdentifier("s2c_config");
    public static final Identifier PAYLOAD_C2S_CONFIG = getIdentifier("c2s_config");

    public static void initialize() {
        NetworkManager.registerS2CPayloadType(RolePayload.TYPE, RolePayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(TickRandomMhBossBarPayload.TYPE, TickRandomMhBossBarPayload.STREAM_CODEC);
        NetworkManager.registerS2CPayloadType(S2CConfigPayload.TYPE, S2CConfigPayload.STREAM_CODEC);
    }

    public static void handleC2SConfigPayload(C2SConfigPayload payload, Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            serverPlayer.displayClientMessage(Component.translatable("commands.random_mh.start.success"), false);
            RandomMhHelper.start(player.level().getServer(), payload.config());
        }
    }
}
