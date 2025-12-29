package com.bleudev.random_mh;

import com.bleudev.random_mh.config.RandomMhGameConfig;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.TickEvent;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.permissions.Permissions;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.function.Predicate;

import static com.bleudev.random_mh.RandomMhHelper.HUNTER_COMPONENT;
import static com.bleudev.random_mh.RandomMhHelper.SPEEDRUNNER_COMPONENT;

public final class RandomMh {
    public static final String MOD_ID = "random_mh";

    public static Identifier getIdentifier(String path) {
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
                        if (RandomMhHelper.getRole(pl) == RandomMhHelper.MhRole.SPEEDRUNNER)
                            ctx.getSource().sendSuccess(() -> SPEEDRUNNER_COMPONENT, false);
                        else
                            ctx.getSource().sendSuccess(() -> HUNTER_COMPONENT, false);
                        return 1;
                    }))
                .then(createLiteral("start")
                    .requires(requireAdmin)
                    .executes(ctx -> {
                        ctx.getSource().sendSuccess(() -> Component.translatable("commands.random_mh.start.success"), false);
                        RandomMhHelper.start(ctx.getSource().getServer(), new RandomMhGameConfig() {
                            @Override
                            public int speedrunnersCount() {
                                return 1;
                            }

                            @Override
                            public boolean shouldRandomiseSpeedrunners() {
                                return true;
                            }

                            @Override
                            public int randomisationTime() {
                                return 200;
                            }
                        });
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
    }
}
