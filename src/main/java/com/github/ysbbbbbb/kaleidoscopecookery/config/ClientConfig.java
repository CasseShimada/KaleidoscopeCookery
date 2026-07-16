package com.github.ysbbbbbb.kaleidoscopecookery.config;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class ClientConfig {
    private static final String FILE_NAME = "kaleidoscope_cookery-client.json";
    private static final String LEGACY_FILE_NAME = "kaleidoscope_cookery-client.toml";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final boolean DEFAULT_SHOW_FOOD_EFFECT_TOOLTIPS = true;

    private static ClientConfig instance;

    private boolean showFoodEffectTooltips = DEFAULT_SHOW_FOOD_EFFECT_TOOLTIPS;

    private ClientConfig() {
    }

    public static void init() {
        get();
    }

    public static ClientConfig get() {
        if (instance == null) {
            instance = load(FabricLoader.getInstance().getConfigDir());
        }
        return instance;
    }

    public boolean showFoodEffectTooltips() {
        return showFoodEffectTooltips;
    }

    static ClientConfig fromLegacyToml(String toml) {
        ClientConfig config = new ClientConfig();
        boolean inCookerySection = false;
        for (String rawLine : toml.lines().toList()) {
            String line = stripComment(rawLine).trim();
            if (line.isEmpty()) {
                continue;
            }
            if (line.startsWith("[") && line.endsWith("]")) {
                inCookerySection = line.substring(1, line.length() - 1).trim().equals("cookery");
                continue;
            }
            if (!inCookerySection) {
                continue;
            }
            int separator = line.indexOf('=');
            if (separator <= 0 || !line.substring(0, separator).trim().equals("ShowFoodEffectTooltips")) {
                continue;
            }
            String value = line.substring(separator + 1).trim();
            if (value.equalsIgnoreCase("true")) {
                config.showFoodEffectTooltips = true;
            } else if (value.equalsIgnoreCase("false")) {
                config.showFoodEffectTooltips = false;
            } else {
                KaleidoscopeCookery.LOGGER.warn(
                        "Ignoring invalid legacy boolean config value ShowFoodEffectTooltips={}", value);
            }
        }
        return config;
    }

    private static ClientConfig load(Path configDirectory) {
        Path configPath = configDirectory.resolve(FILE_NAME);
        if (Files.exists(configPath)) {
            try {
                ClientConfig config = GSON.fromJson(Files.readString(configPath), ClientConfig.class);
                if (config != null) {
                    save(config, configPath);
                    return config;
                }
            } catch (IOException | JsonParseException e) {
                KaleidoscopeCookery.LOGGER.warn("Failed to read {}, using defaults.", configPath, e);
            }
        }

        Path legacyPath = configDirectory.resolve(LEGACY_FILE_NAME);
        ClientConfig config = new ClientConfig();
        if (Files.exists(legacyPath)) {
            try {
                config = fromLegacyToml(Files.readString(legacyPath));
                KaleidoscopeCookery.LOGGER.info("Imported legacy client config from {}", legacyPath);
            } catch (IOException e) {
                KaleidoscopeCookery.LOGGER.warn("Failed to read legacy client config {}, using defaults.", legacyPath, e);
            }
        }
        save(config, configPath);
        return config;
    }

    private static String stripComment(String line) {
        boolean inSingleQuote = false;
        boolean inDoubleQuote = false;
        for (int i = 0; i < line.length(); i++) {
            char character = line.charAt(i);
            if (character == '\'' && !inDoubleQuote) {
                inSingleQuote = !inSingleQuote;
            } else if (character == '"' && !inSingleQuote && (i == 0 || line.charAt(i - 1) != '\\')) {
                inDoubleQuote = !inDoubleQuote;
            } else if (character == '#' && !inSingleQuote && !inDoubleQuote) {
                return line.substring(0, i);
            }
        }
        return line;
    }

    private static void save(ClientConfig config, Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(config));
        } catch (IOException e) {
            KaleidoscopeCookery.LOGGER.warn("Failed to write client config file {}.", configPath, e);
        }
    }
}
