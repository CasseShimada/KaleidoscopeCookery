package com.github.ysbbbbbb.kaleidoscopecookery.client.render;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.fabric.api.client.rendering.v1.RenderStateDataKey;

public final class TrashCanRenderState {
    public static final RenderStateDataKey<Boolean> HIDDEN = RenderStateDataKey.create(
            () -> KaleidoscopeCookery.MOD_ID + ":trash_can_hidden");

    private TrashCanRenderState() {
    }
}
