package com.github.ysbbbbbb.kaleidoscopecookery.util;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.Vec3;

import java.util.Optional;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModAttachmentType.FLATULENCE_EFFECT_STARTING_POSITION;
import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.FLATULENCE;

public final class LegacyPlayerDataCompat {
    private static final String FORGE_DATA = "ForgeData";
    private static final String FLATULENCE_START = "FlatulenceEffectStartingPosition";

    private LegacyPlayerDataCompat() {
    }

    public static void loadFlatulenceStartingPosition(Player player, ValueInput input) {
        if (!player.hasEffect(FLATULENCE) || player.hasAttached(FLATULENCE_EFFECT_STARTING_POSITION)) {
            return;
        }

        input.child(FORGE_DATA)
                .flatMap(data -> data.child(FLATULENCE_START))
                .flatMap(LegacyPlayerDataCompat::readLegacyBlockPosition)
                .ifPresent(position -> player.setAttached(FLATULENCE_EFFECT_STARTING_POSITION, position));
    }

    private static Optional<Vec3> readLegacyBlockPosition(ValueInput input) {
        Optional<Integer> x = input.getInt("X");
        Optional<Integer> y = input.getInt("Y");
        Optional<Integer> z = input.getInt("Z");
        if (x.isEmpty() || y.isEmpty() || z.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(new Vec3(x.get(), y.get(), z.get()));
    }
}
