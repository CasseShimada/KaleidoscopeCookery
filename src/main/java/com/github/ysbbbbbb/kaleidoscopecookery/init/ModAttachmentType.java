package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.phys.Vec3;

public final class ModAttachmentType {
    public static final AttachmentType<Vec3> FLATULENCE_EFFECT_STARTING_POSITION = AttachmentRegistry.createDefaulted(
            id("flatulence_effect_starting_position"),
            () -> Vec3.ZERO);

    private ModAttachmentType() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }
}
