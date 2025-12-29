package com.bleudev.random_mh.fabric.client;

import com.bleudev.random_mh.client.RandomMhClient;
import net.fabricmc.api.ClientModInitializer;

public final class RandomMhFabricClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        RandomMhClient.init();
    }
}
