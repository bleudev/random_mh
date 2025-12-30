package com.bleudev.random_mh.client;

import com.bleudev.random_mh.RandomMhHelper;
import com.bleudev.random_mh.config.RandomMhGameConfig;
import com.bleudev.random_mh.mixin.client.BossHealthOverlayAccessor;
import com.bleudev.random_mh.network.payload.RolePayload;
import com.bleudev.random_mh.network.payload.TickRandomMhBossBarPayload;
import dev.architectury.event.events.client.ClientTickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.client.gui.components.BossHealthOverlay;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.function.Supplier;

public class RandomMhClient {
    private static final UUID randomMhBossBarUUID = UUID.randomUUID();

    private static @NotNull RandomMhHelper.MhRole currentRole = RandomMhHelper.MhRole.NULL;
    private static int randomMhBossBarTick = 0;
    private static int randomMhBossBarDuration = 0;

    public static void init(Supplier<RandomMhGameConfig> configSupplier) {
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, RolePayload.TYPE, RolePayload.STREAM_CODEC, (payload, ctx) -> {
            currentRole = payload.role();
            var c = currentRole.getTitleComponent();
            if (!c.getString().isEmpty())
                ctx.getPlayer().displayClientMessage(c, false);
        });
        NetworkManager.registerReceiver(NetworkManager.Side.S2C, TickRandomMhBossBarPayload.TYPE, TickRandomMhBossBarPayload.STREAM_CODEC, (payload, ctx) -> {
            randomMhBossBarTick = payload.tick();
            randomMhBossBarDuration = payload.duration();
        });
        ClientTickEvent.CLIENT_POST.register(mc -> {
            var bossEvent = new BossEvent(randomMhBossBarUUID, currentRole.getBossBarComponent(), currentRole.getBossBarColor(), BossEvent.BossBarOverlay.PROGRESS) {};
            bossEvent.setProgress((float) randomMhBossBarTick / randomMhBossBarDuration);
            BossHealthOverlay bossHealthOverlay = mc.gui.getBossOverlay();
            if (((BossHealthOverlayAccessor) bossHealthOverlay).random_mh$events().containsKey(randomMhBossBarUUID)) {
                if (currentRole == RandomMhHelper.MhRole.NULL) {
                    bossHealthOverlay.update(ClientboundBossEventPacket.createRemovePacket(randomMhBossBarUUID));
                    return;
                }
                bossHealthOverlay.update(ClientboundBossEventPacket.createUpdateProgressPacket(bossEvent));
                bossHealthOverlay.update(ClientboundBossEventPacket.createUpdateNamePacket(bossEvent));
                bossHealthOverlay.update(ClientboundBossEventPacket.createUpdateStylePacket(bossEvent));
            } else if (currentRole != RandomMhHelper.MhRole.NULL)
                bossHealthOverlay.update(ClientboundBossEventPacket.createAddPacket(bossEvent));
        });
    }
}
