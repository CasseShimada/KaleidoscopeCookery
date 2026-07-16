package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.util.ClientRecipeLookup;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.mojang.serialization.MapCodec;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.compat.GuiGraphics;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReiTeapotRecipeCategory implements DisplayCategory<ReiTeapotRecipeCategory.TeapotRecipeDisplay> {
    public static final CategoryIdentifier<TeapotRecipeDisplay> ID =
            CategoryIdentifier.of(KaleidoscopeCookery.MOD_ID, "plugin/teapot");
    private static final Identifier BG = Identifier.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "textures/gui/jei/teapot.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.teapot");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;

    @Override
    public CategoryIdentifier<TeapotRecipeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public List<Widget> setupDisplay(TeapotRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.x;
        int startY = bounds.y;
        Component brewTime = Component.translatable(
                "jei.kaleidoscope_cookery.teapot.time", display.brewTime / 20);

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(BG, startX, startY, 0, 0, WIDTH, HEIGHT));
        widgets.add(Widgets.withTranslate(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) ->
                drawCenteredString(guiGraphics, brewTime, WIDTH / 2, 70)), startX, startY));
        widgets.add(Widgets.createSlot(new Point(startX + 65, startY + 3))
                .entries(display.getInputEntries().get(0))
                .markInput());
        widgets.add(Widgets.createSlot(new Point(startX + 83, startY + 3))
                .entries(display.getInputEntries().get(1))
                .markInput());
        widgets.add(Widgets.createSlot(new Point(startX + 128, startY + 30))
                .entries(display.getOutputEntries().getFirst())
                .disableBackground()
                .markOutput());

        return widgets;
    }

    private static void drawCenteredString(GuiGraphics guiGraphics, Component text, int centerX, int y) {
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = text.getVisualOrderText();
        guiGraphics.drawString(font, sequence, centerX - font.width(sequence) / 2, y, 0x555555, false);
    }

    @Override
    public int getDisplayWidth(TeapotRecipeDisplay display) {
        return WIDTH;
    }

    @Override
    public int getDisplayHeight() {
        return HEIGHT;
    }

    @Override
    public Component getTitle() {
        return TITLE;
    }

    @Override
    public Renderer getIcon() {
        return EntryStacks.of(ModItems.TEAPOT);
    }

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiTeapotRecipeCategory());
        registry.addWorkstations(ID, ReiUtil.ofItem(ModItems.TEAPOT));
    }

    public static void registerDisplays(DisplayRegistry registry) {
        for (RecipeHolder<TeapotRecipe> holder : ClientRecipeLookup.getRecipes(ModRecipes.TEAPOT_RECIPE)) {
            TeapotRecipe recipe = holder.value();
            Fluid fluid = BuiltInRegistries.FLUID.getValue(recipe.teaFluid());
            Item bucket = recipe.teaFluid().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID) || fluid == null
                    ? Items.WATER_BUCKET
                    : fluid.getBucket();
            EntryIngredient fluidInput = ReiUtil.ofItem(bucket);
            EntryIngredient ingredientInput = EntryIngredient.of(getIngredientStacks(recipe).stream()
                    .map(EntryStacks::of)
                    .toList());
            List<EntryIngredient> outputs = ReiUtil.ofItemStacks(recipe.getResult());

            registry.add(new TeapotRecipeDisplay(holder.id().identifier(), fluidInput,
                    ingredientInput, outputs, recipe.time()));
        }
    }

    private static List<ItemStack> getIngredientStacks(TeapotRecipe recipe) {
        if (recipe.ingredient().isEmpty()) {
            return List.of();
        }
        var ingredient = recipe.ingredient().orElseThrow();
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            List<ItemStack> stacks = ingredient.display()
                    .resolveForStacks(SlotDisplayContext.fromLevel(level))
                    .stream()
                    .map(stack -> stack.copyWithCount(recipe.ingredientCount()))
                    .toList();
            if (!stacks.isEmpty()) {
                return stacks;
            }
        }
        return getIngredientStacksWithoutLevel(recipe);
    }

    @SuppressWarnings("deprecation")
    private static List<ItemStack> getIngredientStacksWithoutLevel(TeapotRecipe recipe) {
        return recipe.ingredient().stream()
                .flatMap(ingredient -> ingredient.items())
                .map(item -> item.value().getDefaultInstance().copyWithCount(recipe.ingredientCount()))
                .toList();
    }

    public static class TeapotRecipeDisplay extends BasicDisplay {
        public final int brewTime;

        public TeapotRecipeDisplay(Identifier location, EntryIngredient fluidInput,
                                   EntryIngredient ingredientInput, List<EntryIngredient> outputs, int brewTime) {
            super(List.of(fluidInput, ingredientInput), outputs, Optional.of(location));
            this.brewTime = brewTime;
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return ID;
        }

        @Override
        public DisplaySerializer<? extends TeapotRecipeDisplay> getSerializer() {
            return DisplaySerializer.of(MapCodec.unit(this), StreamCodec.unit(this));
        }
    }
}
