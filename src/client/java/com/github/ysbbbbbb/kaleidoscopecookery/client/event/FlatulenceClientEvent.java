package com.github.ysbbbbbb.kaleidoscopecookery.client.event;

import com.github.ysbbbbbb.kaleidoscopecookery.client.network.ClientNetworkHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@Environment(EnvType.CLIENT)
public final class FlatulenceClientEvent {
    private static boolean wasShiftPressed = false;

    private FlatulenceClientEvent() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(FlatulenceClientEvent::onClientTick);
    }

    private static void onClientTick(Minecraft client) {
        KeyMapping keyShift = client.options.keyShift;
        boolean isShiftPressed = keyShift.isDown();
        boolean justPressed = isShiftPressed && !wasShiftPressed;
        wasShiftPressed = isShiftPressed;

        if (!justPressed || !isInGame(client)) {
            return;
        }
        LocalPlayer player = client.player;
        if (player == null || !player.hasEffect(ModEffects.FLATULENCE)) {
            return;
        }
        keyShift.consumeClick();
        ClientNetworkHandler.sendFlatulence();
    }

    private static boolean isInGame(Minecraft client) {
        if (client.player == null || client.level == null) {
            return false;
        }
        if (!client.mouseHandler.isMouseGrabbed()) {
            return false;
        }
        return client.isWindowActive();
    }
}
