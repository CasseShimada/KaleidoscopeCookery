package com.github.ysbbbbbb.kaleidoscopecookery.config;

public final class ClientConfigTestAccess {
    private ClientConfigTestAccess() {
    }

    public static ClientConfig fromLegacyToml(String toml) {
        return ClientConfig.fromLegacyToml(toml);
    }
}
