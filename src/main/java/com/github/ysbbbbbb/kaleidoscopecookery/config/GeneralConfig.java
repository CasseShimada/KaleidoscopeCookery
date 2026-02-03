package com.github.ysbbbbbb.kaleidoscopecookery.config;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class GeneralConfig {
    private static final String FILE_NAME = "kaleidoscope_cookery.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static GeneralConfig INSTANCE;

    /**
     * Whether enabling the Satiated Shield effect.
     */
    public boolean satiatedShieldAbsorbEnabled = true;
    /**
     * Whether the Satiated Shield effect should absorb excess damage beyond its capacity.
     */
    public boolean satiatedShieldAbsorbExcessDamage = true;

    private GeneralConfig() {
    }

    public static void init() {
        get();
    }

    public static GeneralConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load();
        }
        return INSTANCE;
    }

    private static GeneralConfig load() {
        Path configPath = FabricLoader.getInstance().getConfigDir().resolve(FILE_NAME);
        if (Files.exists(configPath)) {
            try {
                String json = Files.readString(configPath);
                GeneralConfig config = GSON.fromJson(json, GeneralConfig.class);
                if (config != null) {
                    return config;
                }
            } catch (IOException | JsonSyntaxException e) {
                KaleidoscopeCookery.LOGGER.warn("Failed to read config, using defaults.", e);
            }
        }

        GeneralConfig config = new GeneralConfig();
        save(config, configPath);
        return config;
    }

    private static void save(GeneralConfig config, Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(config));
        } catch (IOException e) {
            KaleidoscopeCookery.LOGGER.warn("Failed to write config file.", e);
        }
    }
}
