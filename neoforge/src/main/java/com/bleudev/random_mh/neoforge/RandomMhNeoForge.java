package com.bleudev.random_mh.neoforge;

import com.bleudev.random_mh.RandomMh;
import net.neoforged.fml.common.Mod;

@Mod(RandomMh.MOD_ID)
public final class RandomMhNeoForge {
    public RandomMhNeoForge() {
        // Run our common setup.
        RandomMh.init();
    }
}
