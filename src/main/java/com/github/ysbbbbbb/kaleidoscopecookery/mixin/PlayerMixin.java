package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyPlayerDataCompat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin {
    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void loadLegacyCookeryPlayerData(ValueInput input, CallbackInfo ci) {
        LegacyPlayerDataCompat.loadFlatulenceStartingPosition((Player) (Object) this, input);
    }
}
