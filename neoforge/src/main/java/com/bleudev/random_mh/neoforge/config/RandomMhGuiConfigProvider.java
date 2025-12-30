package com.bleudev.random_mh.neoforge.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import static com.bleudev.random_mh.neoforge.config.RandomMhConfigProvider.getConfig;
import static com.bleudev.random_mh.neoforge.config.RandomMhConfigProvider.getDefaultConfig;

public class RandomMhGuiConfigProvider {
    public static Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Component.literal("Random manhunt config"))
            .category(ConfigCategory.createBuilder()
                .name(Component.literal("General"))
                .tooltip(Component.literal("General category"))
                .option(Option.<Integer>createBuilder()
                    .name(Component.literal("Speedrunners count"))
                    .description(OptionDescription.of(Component.literal("How much speedrunners will be")))
                    .binding(Binding.generic(getDefaultConfig().speedrunnersCount(), getConfig()::speedrunnersCount, RandomMhConfigProvider::setSpeedrunnersCount))
                    .controller(o -> IntegerFieldControllerBuilder.create(o).min(0))
                    .build())
                .build())
            .build().generateScreen(parent);
    }
}
