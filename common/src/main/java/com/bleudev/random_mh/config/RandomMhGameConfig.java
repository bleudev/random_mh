package com.bleudev.random_mh.config;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public interface RandomMhGameConfig {
    int speedrunnersCount();
    boolean shouldRandomiseSpeedrunners();
    int randomisationTime();

    StreamCodec<@NotNull FriendlyByteBuf, @NotNull RandomMhGameConfig> STREAM_CODEC = StreamCodec.composite(
        ByteBufCodecs.INT, RandomMhGameConfig::speedrunnersCount,
        ByteBufCodecs.BOOL, RandomMhGameConfig::shouldRandomiseSpeedrunners,
        ByteBufCodecs.INT, RandomMhGameConfig::randomisationTime,
        (i, b, i2) -> new RandomMhGameConfig() {
            @Override public int speedrunnersCount() {
                return i;
            }
            @Override public boolean shouldRandomiseSpeedrunners() {
                return b;
            }
            @Override public int randomisationTime() {
                return i2;
            }
        }
    );
}
