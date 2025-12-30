package com.bleudev.random_mh.neoforge;

import com.bleudev.random_mh.RandomMh;
import com.bleudev.random_mh.client.RandomMhClient;
import com.bleudev.random_mh.neoforge.config.RandomMhConfigProvider;
import com.bleudev.random_mh.neoforge.config.RandomMhGuiConfigProvider;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

@Mod(RandomMh.MOD_ID)
public final class RandomMhNeoForge {
    public RandomMhNeoForge() {
        // Run our common setup.
        RandomMh.init();
        RandomMhClient.init(RandomMhConfigProvider::getConfig);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
            (c, parent) -> RandomMhGuiConfigProvider.getConfigScreen(parent));
    }
}
