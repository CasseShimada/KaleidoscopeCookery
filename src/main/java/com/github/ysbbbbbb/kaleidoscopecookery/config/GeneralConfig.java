package com.github.ysbbbbbb.kaleidoscopecookery.config;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class GeneralConfig {
    private static final String FILE_NAME = "kaleidoscope_cookery.json";
    private static final String LEGACY_FILE_NAME = "kaleidoscope_cookery-common.toml";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static final boolean DEFAULT_ABSORB_ENABLED = true;
    private static final boolean DEFAULT_ABSORB_EXCESS_DAMAGE = true;
    private static final boolean DEFAULT_DISABLE_WHEN_HUNGRY = true;
    private static final int DEFAULT_MIN_FOOD_LEVEL = 4;
    private static final double DEFAULT_ADDITIONAL_EXHAUSTION_PER_DAMAGE = 2.0;
    private static final double DEFAULT_DAMAGE_REDUCTION_PERCENT = 1.0;
    private static final double DEFAULT_MAX_DAMAGE_REDUCTION = 64.0;
    private static final double DEFAULT_MIN_DAMAGE = 0.0;
    private static final double DEFAULT_WEAKNESS_DAMAGE_MULTIPLIER = 2.0;

    private static GeneralConfig INSTANCE;

    private boolean satiatedShieldAbsorbEnabled = DEFAULT_ABSORB_ENABLED;
    private boolean satiatedShieldAbsorbExcessDamage = DEFAULT_ABSORB_EXCESS_DAMAGE;
    private boolean satiatedShieldDisableWhenHungryEffect = DEFAULT_DISABLE_WHEN_HUNGRY;
    private int satiatedShieldMinFoodLevel = DEFAULT_MIN_FOOD_LEVEL;
    private double satiatedShieldAdditionalExhaustionPerDamage = DEFAULT_ADDITIONAL_EXHAUSTION_PER_DAMAGE;
    private double satiatedShieldDamageReductionPercent = DEFAULT_DAMAGE_REDUCTION_PERCENT;
    private double satiatedShieldMaxDamageReduction = DEFAULT_MAX_DAMAGE_REDUCTION;
    private double satiatedShieldMinDamage = DEFAULT_MIN_DAMAGE;
    private double satiatedShieldWeaknessDamageMultiplier = DEFAULT_WEAKNESS_DAMAGE_MULTIPLIER;

    private GeneralConfig() {
    }

    public static void init() {
        get();
    }

    public static GeneralConfig get() {
        if (INSTANCE == null) {
            INSTANCE = load(FabricLoader.getInstance().getConfigDir());
        }
        return INSTANCE;
    }

    public boolean satiatedShieldAbsorbEnabled() {
        return satiatedShieldAbsorbEnabled;
    }

    public boolean satiatedShieldAbsorbExcessDamage() {
        return satiatedShieldAbsorbExcessDamage;
    }

    public boolean satiatedShieldDisableWhenHungryEffect() {
        return satiatedShieldDisableWhenHungryEffect;
    }

    public int satiatedShieldMinFoodLevel() {
        return satiatedShieldMinFoodLevel;
    }

    public double satiatedShieldAdditionalExhaustionPerDamage() {
        return satiatedShieldAdditionalExhaustionPerDamage;
    }

    public double satiatedShieldDamageReductionPercent() {
        return satiatedShieldDamageReductionPercent;
    }

    public double satiatedShieldMaxDamageReduction() {
        return satiatedShieldMaxDamageReduction;
    }

    public double satiatedShieldMinDamage() {
        return satiatedShieldMinDamage;
    }

    public double satiatedShieldWeaknessDamageMultiplier() {
        return satiatedShieldWeaknessDamageMultiplier;
    }

    void setSatiatedShieldAbsorbExcessDamage(boolean absorbExcessDamage) {
        satiatedShieldAbsorbExcessDamage = absorbExcessDamage;
    }

    void setSatiatedShieldMinFoodLevel(int minFoodLevel) {
        satiatedShieldMinFoodLevel = minFoodLevel;
    }

    private static GeneralConfig load(Path configDirectory) {
        Path configPath = configDirectory.resolve(FILE_NAME);
        if (Files.exists(configPath)) {
            try {
                GeneralConfig config = GSON.fromJson(Files.readString(configPath), GeneralConfig.class);
                if (config != null) {
                    config.validate();
                    save(config, configPath);
                    return config;
                }
            } catch (IOException | JsonParseException e) {
                KaleidoscopeCookery.LOGGER.warn("Failed to read {}, using defaults.", configPath, e);
            }
        }

        Path legacyPath = configDirectory.resolve(LEGACY_FILE_NAME);
        GeneralConfig config = new GeneralConfig();
        if (Files.exists(legacyPath)) {
            try {
                config = fromLegacyToml(Files.readString(legacyPath));
                KaleidoscopeCookery.LOGGER.info("Imported legacy common config from {}", legacyPath);
            } catch (IOException e) {
                KaleidoscopeCookery.LOGGER.warn("Failed to read legacy config {}, using defaults.", legacyPath, e);
            }
        }
        config.validate();
        save(config, configPath);
        return config;
    }

    static GeneralConfig fromLegacyToml(String toml) {
        GeneralConfig config = new GeneralConfig();
        Map<String, String> cookeryValues = parseCookerySection(toml);

        config.satiatedShieldAbsorbEnabled = parseBoolean(cookeryValues,
                "SatiatedShieldAbsorbEnabled", DEFAULT_ABSORB_ENABLED);
        config.satiatedShieldAbsorbExcessDamage = parseBoolean(cookeryValues,
                "SatiatedShieldAbsorbExcessDamage", DEFAULT_ABSORB_EXCESS_DAMAGE);
        config.satiatedShieldDisableWhenHungryEffect = parseBoolean(cookeryValues,
                "IS_SATIATED_SHIELD_DISABLE_WHEN_HUNGRY_EFFECT", DEFAULT_DISABLE_WHEN_HUNGRY);
        config.satiatedShieldMinFoodLevel = parseInt(cookeryValues,
                "SATIATED_SHIELD_MIN_FOOD_LEVEL", DEFAULT_MIN_FOOD_LEVEL);
        config.satiatedShieldAdditionalExhaustionPerDamage = parseDouble(cookeryValues,
                "SATIATED_SHIELD_ADDITIONAL_EXHAUSTION_PER_DAMAGE", DEFAULT_ADDITIONAL_EXHAUSTION_PER_DAMAGE);
        config.satiatedShieldDamageReductionPercent = parseDouble(cookeryValues,
                "SATIATED_SHIELD_DAMAGE_REDUCTION_PERCENT", DEFAULT_DAMAGE_REDUCTION_PERCENT);
        config.satiatedShieldMaxDamageReduction = parseDouble(cookeryValues,
                "SATIATED_SHIELD_MAX_DAMAGE_REDUCTION", DEFAULT_MAX_DAMAGE_REDUCTION);
        config.satiatedShieldMinDamage = parseDouble(cookeryValues,
                "SATIATED_SHIELD_MIN_DAMAGE", DEFAULT_MIN_DAMAGE);
        config.satiatedShieldWeaknessDamageMultiplier = parseDouble(cookeryValues,
                "SATIATED_SHIELD_WEAKNESS_DAMAGE_MULTIPLIER", DEFAULT_WEAKNESS_DAMAGE_MULTIPLIER);
        config.validate();
        return config;
    }

    private static Map<String, String> parseCookerySection(String toml) {
        Map<String, String> values = new HashMap<>();
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
            if (separator > 0) {
                values.put(line.substring(0, separator).trim(), line.substring(separator + 1).trim());
            }
        }
        return values;
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

    private static boolean parseBoolean(Map<String, String> values, String key, boolean defaultValue) {
        String value = values.get(key);
        if (value == null) {
            return defaultValue;
        }
        if (value.equalsIgnoreCase("true")) {
            return true;
        }
        if (value.equalsIgnoreCase("false")) {
            return false;
        }
        KaleidoscopeCookery.LOGGER.warn("Ignoring invalid legacy boolean config value {}={}", key, value);
        return defaultValue;
    }

    private static int parseInt(Map<String, String> values, String key, int defaultValue) {
        String value = values.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value.replace("_", ""));
        } catch (NumberFormatException e) {
            KaleidoscopeCookery.LOGGER.warn("Ignoring invalid legacy integer config value {}={}", key, value);
            return defaultValue;
        }
    }

    private static double parseDouble(Map<String, String> values, String key, double defaultValue) {
        String value = values.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Double.parseDouble(value.replace("_", ""));
        } catch (NumberFormatException e) {
            KaleidoscopeCookery.LOGGER.warn("Ignoring invalid legacy decimal config value {}={}", key, value);
            return defaultValue;
        }
    }

    private void validate() {
        satiatedShieldMinFoodLevel = validRange(satiatedShieldMinFoodLevel, 1, 20)
                ? satiatedShieldMinFoodLevel : DEFAULT_MIN_FOOD_LEVEL;
        satiatedShieldAdditionalExhaustionPerDamage = validRange(
                satiatedShieldAdditionalExhaustionPerDamage, 0.0, 40.0)
                ? satiatedShieldAdditionalExhaustionPerDamage : DEFAULT_ADDITIONAL_EXHAUSTION_PER_DAMAGE;
        satiatedShieldDamageReductionPercent = validRange(satiatedShieldDamageReductionPercent, 0.0, 1.0)
                ? satiatedShieldDamageReductionPercent : DEFAULT_DAMAGE_REDUCTION_PERCENT;
        satiatedShieldMaxDamageReduction = validRange(satiatedShieldMaxDamageReduction, 0.0, Integer.MAX_VALUE)
                ? satiatedShieldMaxDamageReduction : DEFAULT_MAX_DAMAGE_REDUCTION;
        satiatedShieldMinDamage = validRange(satiatedShieldMinDamage, 0.0, Integer.MAX_VALUE)
                ? satiatedShieldMinDamage : DEFAULT_MIN_DAMAGE;
        satiatedShieldWeaknessDamageMultiplier = validRange(
                satiatedShieldWeaknessDamageMultiplier, 1.0, Integer.MAX_VALUE)
                ? satiatedShieldWeaknessDamageMultiplier : DEFAULT_WEAKNESS_DAMAGE_MULTIPLIER;
    }

    private static boolean validRange(int value, int minimum, int maximum) {
        return value >= minimum && value <= maximum;
    }

    private static boolean validRange(double value, double minimum, double maximum) {
        return Double.isFinite(value) && value >= minimum && value <= maximum;
    }

    private static void save(GeneralConfig config, Path configPath) {
        try {
            Files.createDirectories(configPath.getParent());
            Files.writeString(configPath, GSON.toJson(config));
        } catch (IOException e) {
            KaleidoscopeCookery.LOGGER.warn("Failed to write config file {}.", configPath, e);
        }
    }
}
