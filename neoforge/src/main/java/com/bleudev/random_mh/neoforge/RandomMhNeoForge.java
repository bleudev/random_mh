package com.bleudev.random_mh.neoforge;

import com.bleudev.random_mh.RandomMh;
import com.bleudev.random_mh.client.RandomMhClient;
import com.bleudev.random_mh.neoforge.config.RandomMhConfigProvider;
import com.bleudev.random_mh.neoforge.config.RandomMhGuiConfigProvider;
import com.bleudev.random_mh.network.RandomMhPackets;
import com.bleudev.random_mh.network.payload.C2SConfigPayload;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import org.jetbrains.annotations.NotNull;

@Mod(RandomMh.MOD_ID)
public final class RandomMhNeoForge {
    public RandomMhNeoForge() {
        // Run our common setup.
        RandomMh.init();
        RandomMhClient.init(RandomMhConfigProvider::getConfig);
        ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory.class, () ->
            (c, parent) -> RandomMhGuiConfigProvider.getConfigScreen(parent));
    }

    @SubscribeEvent
    public static void register(@NotNull RegisterPayloadHandlersEvent event) {
        event.registrar("1").playToServer(C2SConfigPayload.TYPE, C2SConfigPayload.STREAM_CODEC,
            (p, c) -> RandomMhPackets.handleC2SConfigPayload(p, c.player()));
    }
}
