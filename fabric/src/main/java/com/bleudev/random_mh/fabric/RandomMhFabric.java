package com.bleudev.random_mh.fabric;

import com.bleudev.random_mh.RandomMh;
import net.fabricmc.api.ModInitializer;

public final class RandomMhFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        // This code runs as soon as Minecraft is in a mod-load-ready state.
        // However, some things (like resources) may still be uninitialized.
        // Proceed with mild caution.

        // Run our common setup.
        RandomMh.init();
    }
}
