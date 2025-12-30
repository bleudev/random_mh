package com.bleudev.random_mh.neoforge.config;

import com.google.gson.GsonBuilder;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import net.neoforged.fml.loading.FMLPaths;

import static com.bleudev.random_mh.RandomMh.getIdentifier;

public class RandomMhConfig {
    public static ConfigClassHandler<RandomMhConfig> HANDLER = ConfigClassHandler.createBuilder(RandomMhConfig.class)
        .id(getIdentifier("config_handler"))
        .serializer(c -> GsonConfigSerializerBuilder.create(c)
            .setPath(FMLPaths.CONFIGDIR.get().resolve("random_mh_config.json5"))
            .appendGsonBuilder(GsonBuilder::setPrettyPrinting)
            .setJson5(true)
            .build())
        .build();
    @SerialEntry
    public int speedrunnersCount = 1;
    @SerialEntry
    public boolean shouldRandomiseSpeedrunners = true;
    @SerialEntry
    public int randomisationTime = 6000;
}
