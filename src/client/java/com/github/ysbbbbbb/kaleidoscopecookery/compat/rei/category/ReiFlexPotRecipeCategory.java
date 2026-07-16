package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.util.ClientRecipeLookup;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
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
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.crafting.RecipeHolder;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReiFlexPotRecipeCategory implements DisplayCategory<ReiFlexPotRecipeCategory.FlexPotRecipeDisplay> {
    public static final CategoryIdentifier<FlexPotRecipeDisplay> ID =
            CategoryIdentifier.of(KaleidoscopeCookery.MOD_ID, "plugin/flex_pot");
    private static final Identifier BG = Identifier.fromNamespaceAndPath(
            KaleidoscopeCookery.MOD_ID, "textures/gui/jei/pot.png");
    private static final Component TITLE = ComponentUtils.formatList(List.of(
            Component.translatable("jei.kaleidoscope_cookery.flex_recipe"),
            Component.translatable("block.kaleidoscope_cookery.pot")
    ), CommonComponents.SPACE);
    public static final int WIDTH = 176;
    public static final int HEIGHT = 102;

    @Override
    public CategoryIdentifier<FlexPotRecipeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public List<Widget> setupDisplay(FlexPotRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.x;
        int startY = bounds.y;
        Component stirFryCount = Component.translatable(
                "jei.kaleidoscope_cookery.pot.stir_fry_count", display.stirFryCount);

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(BG, startX, startY, 0, 0, WIDTH, HEIGHT));
        widgets.add(Widgets.withTranslate(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) -> {
            drawCenteredString(guiGraphics, Component.translatable("jei.kaleidoscope_cookery.flex_recipe"), WIDTH / 2, 5);
            drawCenteredString(guiGraphics, stirFryCount, WIDTH / 2, 85);
        }), startX, startY));

        List<EntryIngredient> inputs = display.getInputEntries();
        for (int i = 0; i < inputs.size(); i++) {
            int xOffset = (i % 3) * 18 + 15;
            int yOffset = (i / 3) * 18 + 24;
            widgets.add(Widgets.createSlot(new Point(startX + xOffset, startY + yOffset))
                    .entries(inputs.get(i))
                    .disableBackground()
                    .markInput());
            if (!inputs.get(i).isEmpty()) {
                widgets.add(Widgets.withTranslate(Widgets.createDrawableWidget((guiGraphics, mouseX, mouseY, delta) ->
                        guiGraphics.drawString(Minecraft.getInstance().font, Component.literal("*"),
                                xOffset, yOffset, 0xFFFFFF, true)), startX, startY));
            }
        }
        if (!display.carrier.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(startX + 133, startY + 18))
                    .entries(display.carrier)
                    .disableBackground()
                    .markInput());
        }
        widgets.add(Widgets.createSlot(new Point(startX + 143, startY + 60))
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
    public int getDisplayWidth(FlexPotRecipeDisplay display) {
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
        return EntryStacks.of(ModItems.POT);
    }

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiFlexPotRecipeCategory());
        registry.addWorkstations(ID,
                ReiUtil.ofItem(ModItems.POT),
                ReiUtil.ofTag(TagMod.KITCHEN_SHOVEL),
                ReiUtil.ofItem(ModItems.OIL));
    }

    public static void registerDisplays(DisplayRegistry registry) {
        for (RecipeHolder<FlexPotRecipe> holder : ClientRecipeLookup.getRecipes(ModRecipes.FLEX_POT_RECIPE)) {
            FlexPotRecipe recipe = holder.value();
            List<EntryIngredient> inputs = ReiUtil.ofIngredients(recipe.getIngredients());
            List<EntryIngredient> outputs = ReiUtil.ofItemStacks(recipe.result().create());
            EntryIngredient carrier = recipe.carrier()
                    .map(ReiUtil::ofIngredient)
                    .orElse(EntryIngredient.empty());
            registry.add(new FlexPotRecipeDisplay(holder.id().identifier(), inputs, outputs,
                    carrier, recipe.stirFryCount()));
        }
    }

    public static class FlexPotRecipeDisplay extends BasicDisplay {
        public final EntryIngredient carrier;
        public final int stirFryCount;

        public FlexPotRecipeDisplay(Identifier location, List<EntryIngredient> inputs,
                                    List<EntryIngredient> outputs, EntryIngredient carrier, int stirFryCount) {
            super(inputs, outputs, Optional.of(location));
            this.carrier = carrier;
            this.stirFryCount = stirFryCount;
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return ID;
        }

        @Override
        public DisplaySerializer<? extends FlexPotRecipeDisplay> getSerializer() {
            return DisplaySerializer.of(MapCodec.unit(this), StreamCodec.unit(this));
        }
    }
}
