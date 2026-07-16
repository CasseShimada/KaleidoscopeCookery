package com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.util.ClientRecipeLookup;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.rei.ReiUtil;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.output.RandomOutput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.basic.BasicDisplay;
import me.shedaniel.rei.api.common.display.DisplaySerializer;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.crafting.RecipeHolder;
import com.mojang.serialization.MapCodec;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReiMillstoneRecipeCategory implements DisplayCategory<ReiMillstoneRecipeCategory.MillstoneRecipeDisplay> {
    public static final CategoryIdentifier<MillstoneRecipeDisplay> ID = CategoryIdentifier.of(KaleidoscopeCookery.MOD_ID, "plugin/millstone");
    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/millstone.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.millstone");

    public static final int WIDTH = 196;
    public static final int HEIGHT = 95;

    @Override
    public CategoryIdentifier<MillstoneRecipeDisplay> getCategoryIdentifier() {
        return ID;
    }

    @Override
    public List<Widget> setupDisplay(MillstoneRecipeDisplay display, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        int startX = bounds.x;
        int startY = bounds.y;

        widgets.add(Widgets.createRecipeBase(bounds));
        widgets.add(Widgets.createTexturedWidget(BG, startX, startY, 0, 0, WIDTH, HEIGHT));
        widgets.add(Widgets.createSlot(new Point(startX + 69, startY + 39))
                .entries(display.getInputEntries().getFirst())
                .markInput());
        List<EntryIngredient> outputs = display.getOutputEntries();
        widgets.add(Widgets.createSlot(new Point(startX + 150, startY + 47))
                .entries(outputs.getFirst())
                .disableBackground()
                .markOutput());
        for (int i = 1; i < outputs.size(); i++) {
            int x = switch (i) {
                case 2 -> 128;
                case 3 -> 172;
                default -> 150;
            };
            widgets.add(Widgets.createSlot(new Point(startX + x, startY + 20))
                    .entries(outputs.get(i))
                    .markOutput());
        }
        if (!display.carrier.isEmpty()) {
            widgets.add(Widgets.createSlot(new Point(startX + 115, startY + 36))
                    .entries(display.carrier)
                    .disableBackground()
                    .markInput());
        }

        return widgets;
    }

    @Override
    public int getDisplayWidth(MillstoneRecipeDisplay display) {
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
        return EntryStacks.of(ModItems.MILLSTONE);
    }

    public static void registerCategories(CategoryRegistry registry) {
        registry.add(new ReiMillstoneRecipeCategory());
        registry.addWorkstations(ReiMillstoneRecipeCategory.ID,
                ReiUtil.ofItem(ModItems.MILLSTONE)
        );
    }

    public static void registerDisplays(DisplayRegistry registry) {
        for (RecipeHolder<MillstoneRecipe> r
                : ClientRecipeLookup.getRecipes(ModRecipes.MILLSTONE_RECIPE)) {
            List<EntryIngredient> input = ReiUtil.ofIngredients(r.value().getIngredient());
            List<EntryIngredient> output = r.value().results().stream()
                    .filter(randomOutput -> !randomOutput.isEmpty())
                    .map(RandomOutput::stack)
                    .map(ReiUtil::ofItemStack)
                    .toList();
            EntryIngredient carrier = r.value().getCarrier()
                    .map(ReiUtil::ofIngredient)
                    .orElse(EntryIngredient.empty());

            registry.add(new MillstoneRecipeDisplay(r.id().identifier(), input, output, carrier));
        }
    }

    public static class MillstoneRecipeDisplay extends BasicDisplay {
        public final EntryIngredient carrier;

        public MillstoneRecipeDisplay(Identifier location, List<EntryIngredient> inputs, List<EntryIngredient> outputs, EntryIngredient carrier) {
            super(inputs, outputs, Optional.of(location));
            this.carrier = carrier;
        }

        @Override
        public CategoryIdentifier<?> getCategoryIdentifier() {
            return ID;
        }

        @Override
        public DisplaySerializer<? extends MillstoneRecipeDisplay> getSerializer() {
            return DisplaySerializer.of(MapCodec.unit(this), StreamCodec.unit(this));
        }
    }
}
