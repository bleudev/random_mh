package com.bleudev.random_mh.neoforge.config;

import com.bleudev.random_mh.config.RandomMhGameConfig;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class RandomMhConfigProvider {
    @Contract(value = "_ -> new", pure = true)
    private static @NotNull RandomMhGameConfig getFromConfig(RandomMhConfig config) {
        return new RandomMhGameConfig() {
            @Override public int speedrunnersCount() {
                RandomMhConfig.HANDLER.load();
                return config.speedrunnersCount;
            }
            @Override public boolean shouldRandomiseSpeedrunners() {
                RandomMhConfig.HANDLER.load();
                return config.shouldRandomiseSpeedrunners;
            }
            @Override public int randomisationTime() {
                RandomMhConfig.HANDLER.load();
                return config.randomisationTime;
            }
        };
    }

    @Contract(" -> new")
    public static @NotNull RandomMhGameConfig getConfig() {
        return getFromConfig(RandomMhConfig.HANDLER.instance());
    }
    @Contract(" -> new")
    public static @NotNull RandomMhGameConfig getDefaultConfig() {
        return getFromConfig(RandomMhConfig.HANDLER.defaults());
    }

    public static void setSpeedrunnersCount(int v) {
        RandomMhConfig.HANDLER.instance().speedrunnersCount = v;
        RandomMhConfig.HANDLER.save();
    }
    public static void setShouldRandomiseSpeedrunners(boolean v) {
        RandomMhConfig.HANDLER.instance().shouldRandomiseSpeedrunners = v;
        RandomMhConfig.HANDLER.save();
    }
    public static void setRandomisationTime(int v) {
        RandomMhConfig.HANDLER.instance().randomisationTime = v;
        RandomMhConfig.HANDLER.save();
    }
}
