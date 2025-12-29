package com.bleudev.random_mh;

import com.bleudev.random_mh.config.RandomMhGameConfig;
import com.bleudev.random_mh.network.payload.RolePayload;
import com.bleudev.random_mh.network.payload.TickRandomMhBossBarPayload;
import dev.architectury.networking.NetworkManager;
import net.minecraft.ChatFormatting;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomMhHelper {
    public static final Component SPEEDRUNNER_COMPONENT = Component.translatable("random_mh.general.you_are_speedrunner").withStyle(ChatFormatting.AQUA);
    public static final Component HUNTER_COMPONENT = Component.translatable("random_mh.general.you_are_hunter").withStyle(ChatFormatting.RED);

    public static final Component BOSSBAR_SPEEDRUNNER_COMPONENT = Component.literal("Hide!");
    public static final Component BOSSBAR_HUNTER_COMPONENT = Component.literal("Find speedrunner!");

    public enum MhRole {
        NULL("null", Component.empty(), Component.empty(), BossEvent.BossBarColor.WHITE),
        SPEEDRUNNER("speedrunner", SPEEDRUNNER_COMPONENT, BOSSBAR_SPEEDRUNNER_COMPONENT, BossEvent.BossBarColor.BLUE),
        HUNTER("hunter", HUNTER_COMPONENT, BOSSBAR_HUNTER_COMPONENT, BossEvent.BossBarColor.RED);

        private final String name;
        private final Component titleComponent;
        private final Component bossBarComponent;
        private final BossEvent.BossBarColor bossBarColor;
        MhRole(String name, Component titleComponent, Component bossBarComponent, BossEvent.BossBarColor bossBarColor) {
            this.name = name;
            this.titleComponent = titleComponent;
            this.bossBarComponent = bossBarComponent;
            this.bossBarColor = bossBarColor;
        }
        public String asString() {
            return name;
        }
        public Component getTitleComponent() {
            return titleComponent;
        }
        public Component getBossBarComponent() {
            return bossBarComponent;
        }
        public BossEvent.BossBarColor getBossBarColor() {
            return bossBarColor;
        }

        @Contract(pure = true)
        public static MhRole fromString(@NotNull String name) throws IllegalStateException {
            return switch (name) {
                case "null" -> MhRole.NULL;
                case "speedrunner" -> MhRole.SPEEDRUNNER;
                case "hunter" -> MhRole.HUNTER;
                default -> throw new IllegalStateException("Unexpected enum name: " + name);
            };
        }

        public static final StreamCodec<@NotNull FriendlyByteBuf, @NotNull MhRole> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public void encode(@NotNull FriendlyByteBuf object, @NotNull MhRole object2) {
                ByteBufCodecs.STRING_UTF8.encode(object, object2.asString());
            }

            @Override
            public @NotNull MhRole decode(@NotNull FriendlyByteBuf object) {
                return MhRole.fromString(ByteBufCodecs.STRING_UTF8.decode(object));
            }
        };
    }

    private static final ArrayList<ServerPlayer> speedrunners = new ArrayList<>();
    private static final ArrayList<ServerPlayer> hunters = new ArrayList<>();
    private static RandomMhGameConfig config;
    private static int t = -1;

    public static void start(MinecraftServer server, RandomMhGameConfig gameConfig) {
        stop(server);
        config = gameConfig;
        t = 0;

        var players = server.getPlayerList().getPlayers();
        var maxSpeedrunnersCount = new AtomicInteger(config.speedrunnersCount());
        var playersCount = new AtomicInteger(players.size());
        players.forEach(pl -> {
            if (pl.getRandom().nextFloat() <= (float) maxSpeedrunnersCount.get() / playersCount.get()) {
                speedrunners.add(pl);
                pl.connection.send(new ClientboundSetTitleTextPacket(MhRole.SPEEDRUNNER.getTitleComponent()));
                NetworkManager.sendToPlayer(pl, new RolePayload(MhRole.SPEEDRUNNER));
                maxSpeedrunnersCount.getAndDecrement();
            } else {
                hunters.add(pl);
                pl.connection.send(new ClientboundSetTitleTextPacket(MhRole.HUNTER.getTitleComponent()));
                NetworkManager.sendToPlayer(pl, new RolePayload(MhRole.HUNTER));
            }
            playersCount.getAndDecrement();
        });
    }
    public static void stop(MinecraftServer server) {
        server.getPlayerList().getPlayers().forEach(pl ->
            NetworkManager.sendToPlayer(pl, new RolePayload(MhRole.NULL)));

        speedrunners.clear();
        hunters.clear();
        config = null;
        t = -1;
    }

    public static boolean isStarted() {
        return t > -1;
    }

    public static void tick(MinecraftServer server) {
        if (!isStarted()) return;
        if (t == config.randomisationTime()) {
            start(server, config);
            return;
        }

        speedrunners.forEach(pl -> NetworkManager.sendToPlayer(pl, new TickRandomMhBossBarPayload(t, config.randomisationTime())));
        hunters.forEach(pl -> NetworkManager.sendToPlayer(pl, new TickRandomMhBossBarPayload(t, config.randomisationTime())));
        t++;
    }

    public static MhRole getRole(ServerPlayer player) {
        if (speedrunners.contains(player)) return MhRole.SPEEDRUNNER;
        else if (hunters.contains(player)) return MhRole.HUNTER;
        return MhRole.NULL;
    }
}
