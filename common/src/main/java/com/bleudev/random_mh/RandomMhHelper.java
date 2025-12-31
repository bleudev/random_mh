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
import net.minecraft.world.BossEvent;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class RandomMhHelper {
    public static final Component SPEEDRUNNER_COMPONENT = Component.translatable("random_mh.general.you_are_speedrunner").withStyle(ChatFormatting.AQUA);
    public static final Component HUNTER_COMPONENT = Component.translatable("random_mh.general.you_are_hunter").withStyle(ChatFormatting.RED);
    public static final Component SPEEDRUNNER_WON_COMPONENT = Component.translatable("random_mh.general.won.speedrunner").withStyle(ChatFormatting.AQUA);
    public static final Component HUNTER_WON_COMPONENT = Component.translatable("random_mh.general.won.hunter").withStyle(ChatFormatting.RED);
    public static final Component SPEEDRUNNER_LOSE_COMPONENT = Component.translatable("random_mh.general.lose.speedrunner").withStyle(ChatFormatting.AQUA);
    public static final Component HUNTER_LOSE_COMPONENT = Component.translatable("random_mh.general.lose.hunter").withStyle(ChatFormatting.RED);

    public static final Component BOSSBAR_SPEEDRUNNER_COMPONENT = Component.translatable("random_mh.general.bossbar.speedrunner");
    public static final Component BOSSBAR_HUNTER_COMPONENT = Component.translatable("random_mh.general.bossbar.hunter");

    public enum MhRole {
        NULL("null", Component.empty(), Component.empty(), BossEvent.BossBarColor.WHITE, Component.empty(), Component.empty()),
        SPEEDRUNNER("speedrunner", SPEEDRUNNER_COMPONENT, BOSSBAR_SPEEDRUNNER_COMPONENT, BossEvent.BossBarColor.BLUE, SPEEDRUNNER_WON_COMPONENT, SPEEDRUNNER_LOSE_COMPONENT),
        HUNTER("hunter", HUNTER_COMPONENT, BOSSBAR_HUNTER_COMPONENT, BossEvent.BossBarColor.RED, HUNTER_WON_COMPONENT, HUNTER_LOSE_COMPONENT);

        private final String name;
        private final Component titleComponent;
        private final Component bossBarComponent;
        private final BossEvent.BossBarColor bossBarColor;
        private final Component wonComponent;
        private final Component loseComponent;

        MhRole(String name, Component titleComponent, Component bossBarComponent, BossEvent.BossBarColor bossBarColor, Component wonComponent, Component loseComponent) {
            this.name = name;
            this.titleComponent = titleComponent;
            this.bossBarComponent = bossBarComponent;
            this.bossBarColor = bossBarColor;
            this.wonComponent = wonComponent;
            this.loseComponent = loseComponent;
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
        public Component getWonComponent() {
            return wonComponent;
        }
        public Component getLoseComponent() {
            return loseComponent;
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

    private static final ArrayList<String> speedrunners = new ArrayList<>();
    private static final ArrayList<String> hunters = new ArrayList<>();
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
                speedrunners.add(pl.getGameProfile().name());
                pl.connection.send(new ClientboundSetTitleTextPacket(MhRole.SPEEDRUNNER.getTitleComponent()));
                NetworkManager.sendToPlayer(pl, new RolePayload(MhRole.SPEEDRUNNER));
                maxSpeedrunnersCount.getAndDecrement();
            } else {
                hunters.add(pl.getGameProfile().name());
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

        server.getPlayerList().getPlayers().forEach(pl -> {
            if (getRole(pl.getGameProfile().name()) != MhRole.NULL)
                NetworkManager.sendToPlayer(pl, new TickRandomMhBossBarPayload(t, config.randomisationTime()));
        });
        t++;
    }

    public static MhRole getRole(String nickname) {
        if (speedrunners.contains(nickname)) return MhRole.SPEEDRUNNER;
        else if (hunters.contains(nickname)) return MhRole.HUNTER;
        return MhRole.NULL;
    }


    public static boolean shouldEndGameWithHuntersWin(@NotNull MinecraftServer server) {
        return server.getPlayerList().getPlayers().stream().allMatch(pl ->
            getRole(pl.getGameProfile().name()) == MhRole.HUNTER || pl.gameMode() == GameType.SPECTATOR);
    }
    public static boolean canEndGameWithSpeedrunnersWin(@NotNull MinecraftServer server) {
        return speedrunners.stream().anyMatch(n -> {
            var pl = server.getPlayerList().getPlayer(n);
            if (pl != null)
                return pl.level().dimension().equals(Level.END);
            return false;
        });
    }
    public static void endWithHuntersWin(MinecraftServer server) {
        hunters.forEach(n -> {
            var pl = server.getPlayerList().getPlayer(n);
            if (pl != null)
                pl.connection.send(new ClientboundSetTitleTextPacket(MhRole.HUNTER.getWonComponent()));
        });
        speedrunners.forEach(n -> {
            var pl = server.getPlayerList().getPlayer(n);
            if (pl != null)
                pl.connection.send(new ClientboundSetTitleTextPacket(MhRole.SPEEDRUNNER.getLoseComponent()));
        });
        RandomMhHelper.stop(server);
    }
    public static void endWithSpeedrunnersWin(MinecraftServer server) {
        speedrunners.forEach(n -> {
            var pl = server.getPlayerList().getPlayer(n);
            if (pl != null)
                pl.connection.send(new ClientboundSetTitleTextPacket(MhRole.SPEEDRUNNER.getWonComponent()));
        });
        hunters.forEach(n -> {
            var pl = server.getPlayerList().getPlayer(n);
            if (pl != null)
                pl.connection.send(new ClientboundSetTitleTextPacket(MhRole.HUNTER.getLoseComponent()));
        });
        RandomMhHelper.stop(server);
    }
}
