package com.bleudev.random_mh.fabric;

import com.bleudev.random_mh.RandomMh;
import com.bleudev.random_mh.network.RandomMhPackets;
import com.bleudev.random_mh.network.payload.C2SConfigPayload;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

public final class RandomMhFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        RandomMh.init();
        PayloadTypeRegistry.playC2S().register(C2SConfigPayload.TYPE, C2SConfigPayload.STREAM_CODEC);
        ServerPlayNetworking.registerGlobalReceiver(C2SConfigPayload.TYPE, (payload, context) -> {
            RandomMhPackets.handleC2SConfigPayload(payload, context.player());
        });
    }
}
