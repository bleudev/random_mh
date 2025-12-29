package com.bleudev.random_mh.neoforge;

import com.bleudev.random_mh.RandomMh;
import com.bleudev.random_mh.client.RandomMhClient;
import net.neoforged.fml.common.Mod;

@Mod(RandomMh.MOD_ID)
public final class RandomMhNeoForge {
    public RandomMhNeoForge() {
        // Run our common setup.
        RandomMh.init();
        RandomMhClient.init();
    }
}
