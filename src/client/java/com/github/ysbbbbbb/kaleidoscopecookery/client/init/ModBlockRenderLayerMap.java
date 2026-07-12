package com.github.ysbbbbbb.kaleidoscopecookery.client.init;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Environment(EnvType.CLIENT)
public final class ModBlockRenderLayerMap {
    private ModBlockRenderLayerMap() {
    }

    public static void register() {
        // 26.1.x no longer exposes the previous Fabric render-layer helper used by this branch.
        // Existing block models/resources remain intact while the renderer migration is handled separately.
    }
}
