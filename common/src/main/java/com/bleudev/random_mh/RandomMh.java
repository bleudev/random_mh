package com.bleudev.random_mh;

import com.bleudev.random_mh.network.payload.S2CConfigPayload;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.EntityEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.architectury.networking.NetworkManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.Permissions;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.GameType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static com.bleudev.random_mh.RandomMhHelper.endWithHuntersWin;
import static com.bleudev.random_mh.RandomMhHelper.endWithSpeedrunnersWin;

public final class RandomMh {
    public static final String MOD_ID = "random_mh";

    @Contract("_ -> new")
    public static @NotNull Identifier getIdentifier(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    // For better types
    @Contract(value = "_ -> new", pure = true)
    private static @NotNull LiteralArgumentBuilder<CommandSourceStack> createLiteral(String name) {
        return LiteralArgumentBuilder.literal(name);
    }

    public static void init() {
        Predicate<CommandSourceStack> requireAdmin = s -> s.permissions().hasPermission(Permissions.COMMANDS_GAMEMASTER);

        CommandRegistrationEvent.EVENT.register((cd,  cbc, cs) -> {
            cd.register(createLiteral("random_mh")
                .then(createLiteral("role")
                    .executes(ctx -> {
                        var pl = ctx.getSource().getPlayer();
                        if (pl == null) {
                            ctx.getSource().sendFailure(Component.translatable("commands.random_mh.role.failure.not_a_player"));
                            return -1;
                        }
                        var c = RandomMhHelper.getRole(pl.getGameProfile().name()).getTitleComponent();
                        if (!c.getString().isEmpty())
                            ctx.getSource().sendSuccess(() -> c, false);
                        return 1;
                    }))
                .then(createLiteral("start")
                    .requires(requireAdmin)
                    .executes(ctx -> {
                        var pl = ctx.getSource().getPlayer();
                        if (pl == null) {
                            ctx.getSource().sendFailure(Component.translatable("commands.random_mh.role.failure.not_a_player"));
                            return -1;
                        }
                        NetworkManager.sendToPlayer(pl, new S2CConfigPayload());
                        return 1;
                    }))
                .then(createLiteral("stop")
                    .requires(requireAdmin)
                    .executes(ctx -> {
                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.random_mh.stop.success"), false);
                        RandomMhHelper.stop(ctx.getSource().getServer());
                        return 1;
                    })));
        });
        TickEvent.SERVER_POST.register(RandomMhHelper::tick);
        EntityEvent.LIVING_DEATH.register((e, source) -> {
            if (!RandomMhHelper.isStarted()) return EventResult.interruptDefault();
            if (e instanceof ServerPlayer player && RandomMhHelper.getRole(player.getGameProfile().name()) == RandomMhHelper.MhRole.SPEEDRUNNER) {
                player.setGameMode(GameType.SPECTATOR);
                var s = e.level().getServer();
                if (RandomMhHelper.shouldEndGameWithHuntersWin(s)) endWithHuntersWin(s);
            } else if (e instanceof EnderDragon) {
                var s = e.level().getServer();
                if (s != null && RandomMhHelper.canEndGameWithSpeedrunnersWin(s)) endWithSpeedrunnersWin(s);
            }
            return EventResult.interruptDefault();
        });
    }
}
