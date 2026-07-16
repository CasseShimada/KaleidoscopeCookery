package com.github.ysbbbbbb.kaleidoscopecookery.mixin.client;

import net.minecraft.client.gui.Hud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Hud.class)
public interface HudAccessor {
    @Accessor("overlayMessageTime")
    int kaleidoscopeCookery$getOverlayMessageTime();
}
