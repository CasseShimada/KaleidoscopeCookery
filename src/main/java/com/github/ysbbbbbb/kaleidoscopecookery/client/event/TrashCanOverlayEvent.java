package com.github.ysbbbbbb.kaleidoscopecookery.client.event;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.CameraType;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public final class TrashCanOverlayEvent {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "trash_can_overlay");
    private static final Identifier TRASH_CAN_OVERLAY = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/trash_can_overlay.png");
    private static final int ITEM_PREVIEW_X_OFFSET = -28;
    private static final int ITEM_PREVIEW_Y_OFFSET = 4;
    private static final int ITEM_PREVIEW_SPACING = 20;

    private TrashCanOverlayEvent() {
    }

    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.MISC_OVERLAYS, ID, TrashCanOverlayEvent::render);
    }

    private static void render(GuiGraphicsExtractor guiGraphics, DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        if (isFirstPersonInTrashCan(minecraft, player)) {
            renderTrashCanOverlay(guiGraphics);
            return;
        }
        if (isInTrashCan(player)) {
            return;
        }
        renderTrashCanTip(guiGraphics, minecraft, player);
    }

    private static boolean isFirstPersonInTrashCan(Minecraft minecraft, LocalPlayer player) {
        return isInTrashCan(player) && minecraft.options.getCameraType() == CameraType.FIRST_PERSON;
    }

    private static boolean isInTrashCan(LocalPlayer player) {
        return player.getVehicle() instanceof SitEntity sitEntity && sitEntity.getSitType() == SitEntity.TRASH_CAN;
    }

    private static void renderTrashCanOverlay(GuiGraphicsExtractor guiGraphics) {
        guiGraphics.blit(TRASH_CAN_OVERLAY, 0, 0, guiGraphics.guiWidth(), guiGraphics.guiHeight(), 0, 1, 0, 1);
    }

    private static void renderTrashCanTip(GuiGraphicsExtractor guiGraphics, Minecraft minecraft, LocalPlayer player) {
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult result) || result.getType() != HitResult.Type.BLOCK) {
            return;
        }
        if (!player.level().getBlockState(result.getBlockPos()).is(ModBlocks.TRASH_CAN)) {
            return;
        }
        BlockEntity blockEntity = player.level().getBlockEntity(result.getBlockPos());
        if (!(blockEntity instanceof TrashCanBlockEntity trashCan)) {
            return;
        }

        Font font = minecraft.font;
        int x = guiGraphics.guiWidth() / 2 + ITEM_PREVIEW_X_OFFSET;
        int y = guiGraphics.guiHeight() / 2 + ITEM_PREVIEW_Y_OFFSET;
        for (ItemStack stack : trashCan.getStoredItems()) {
            if (!stack.isEmpty()) {
                guiGraphics.fakeItem(stack, x, y);
                guiGraphics.itemDecorations(font, stack, x, y);
                x += ITEM_PREVIEW_SPACING;
            }
        }
    }
}
