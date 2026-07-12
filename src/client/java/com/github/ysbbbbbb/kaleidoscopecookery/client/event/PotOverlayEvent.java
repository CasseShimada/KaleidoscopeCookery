package com.github.ysbbbbbb.kaleidoscopecookery.client.event;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.TeapotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariantAttributes;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

@Environment(EnvType.CLIENT)
public final class PotOverlayEvent {
    private static final Identifier ID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "pot_overlay");
    private static final String EMPTY_TEXT = "tooltip.kaleidoscope_cookery.empty";

    private PotOverlayEvent() {
    }

    public static void register() {
        HudElementRegistry.attachElementAfter(VanillaHudElements.OVERLAY_MESSAGE, ID, PotOverlayEvent::render);
    }

    private static void render(GuiGraphicsExtractor guiGraphics, DeltaTracker tickCounter) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft.gameMode == null || minecraft.gameMode.getPlayerMode() == GameType.SPECTATOR) {
            return;
        }
        HitResult hitResult = minecraft.hitResult;
        if (!(hitResult instanceof BlockHitResult result)) {
            return;
        }
        if (result.getType() != HitResult.Type.BLOCK) {
            return;
        }
        LocalPlayer player = minecraft.player;
        if (player == null) {
            return;
        }
        Level level = player.level();
        BlockPos blockPos = result.getBlockPos();
        BlockState blockState = player.level().getBlockState(blockPos);
        BlockEntity blockEntity = level.getBlockEntity(blockPos);
        Font font = Minecraft.getInstance().font;
        int screenWidth = guiGraphics.guiWidth();
        int screenHeight = guiGraphics.guiHeight();
        int x = screenWidth / 2;
        int y = screenHeight - 72;
        if (blockState.is(ModBlocks.TEAPOT) && blockEntity instanceof TeapotBlockEntity teapot) {
            renderTeapot(guiGraphics, font, teapot, x, y);
            return;
        }
        if (blockState.is(ModBlocks.POT) && blockEntity instanceof PotBlockEntity pot) {
            renderPot(guiGraphics, font, level, blockState, pot, x, y);
        }
    }

    private static void renderPot(GuiGraphicsExtractor guiGraphics, Font font, Level level, BlockState blockState,
                                  PotBlockEntity pot, int x, int y) {
        if (blockState.getValue(PotBlock.HAS_OIL) && pot.hasHeatSource(level)) {
            int status = pot.getStatus();
            if (status == PotBlockEntity.PUT_INGREDIENT) {
                drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.add_ingredient"), x, y, 0xFFFFFF);
                return;
            }
            if (status == PotBlockEntity.COOKING) {
                drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.need_stir_fry"), x, y, 0xFFFFFF);
                return;
            }
            if (status == PotBlockEntity.FINISHED) {
                drawWordWrap(guiGraphics, font, Component.translatable("tip.kaleidoscope_cookery.pot.done"), x, y, 0xFF5555);
            }
        }
    }

    private static void renderTeapot(GuiGraphicsExtractor guiGraphics, Font font, TeapotBlockEntity teapot, int x, int y) {
        drawWordWrap(guiGraphics, font, teapot.getStatusText(), x, y, 0xFFFFFF);
        int status = teapot.getStatus();
        if (status == ITeapot.PUT_INGREDIENT) {
            Component fluidText = fluidName(teapot.getTeaFluidId());
            Component itemText = teapot.getInput().isEmpty()
                    ? Component.translatable(EMPTY_TEXT)
                    : teapot.getInput().getHoverName().copy().append(Component.literal(" x%d".formatted(teapot.getInput().getCount())));
            drawWordWrap(guiGraphics, font,
                    Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.fluid_ingredient", fluidText, itemText, teapot.getInput().getCount()),
                    x, y + font.lineHeight, 0xFFFFFF);
        } else if (status == ITeapot.FINISHED) {
            Component itemText = teapot.getResult().isEmpty()
                    ? Component.translatable(EMPTY_TEXT)
                    : teapot.getResult().getHoverName().copy().append(Component.literal(" x%d".formatted(teapot.getResult().getCount())));
            drawWordWrap(guiGraphics, font,
                    Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.result", itemText, teapot.getResult().getCount()),
                    x, y + font.lineHeight, 0xFF5555);
        }
    }

    private static Component fluidName(Identifier id) {
        if (TeapotRecipeSerializer.EMPTY_TEA_FLUID.equals(id)) {
            return Component.translatable(EMPTY_TEXT);
        }
        Fluid fluid = net.minecraft.core.registries.BuiltInRegistries.FLUID.getValue(id);
        if (fluid == null) {
            return Component.literal(id.toString());
        }
        return FluidVariantAttributes.getName(FluidVariant.of(fluid));
    }

    private static void drawWordWrap(GuiGraphicsExtractor graphics, Font font, FormattedText text, int pX, int pY, int color) {
        for (FormattedCharSequence sequence : font.split(text, 100)) {
            graphics.text(font, sequence, pX - font.width(sequence) / 2, pY, color);
            pY += font.lineHeight;
        }
    }
}
