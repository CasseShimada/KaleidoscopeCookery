package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.util.ClientRecipeLookup;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.api.recipe.types.IRecipeHolderType;
import mezz.jei.api.recipe.types.IRecipeType;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class FlexPotRecipeCategory implements IRecipeCategory<RecipeHolder<FlexPotRecipe>> {
    public static final IRecipeHolderType<FlexPotRecipe> TYPE =
            IRecipeHolderType.create(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "flex_pot"));
    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/pot.png");
    private static final Component TITLE = ComponentUtils.formatList(List.of(
            Component.translatable("jei.kaleidoscope_cookery.flex_recipe"),
            Component.translatable("block.kaleidoscope_cookery.pot")
    ), CommonComponents.SPACE);
    public static final int WIDTH = 176;
    public static final int HEIGHT = 102;
    private final IDrawable bgDraw;
    private final IDrawable iconDraw;
    private final IDrawable slotDraw;

    public FlexPotRecipeCategory(IGuiHelper guiHelper) {
        this.bgDraw = guiHelper.createDrawable(BG, 0, 0, WIDTH, HEIGHT);
        this.iconDraw = guiHelper.createDrawableItemStack(ModItems.POT.getDefaultInstance());
        this.slotDraw = guiHelper.getSlotDrawable();
    }

    public static List<RecipeHolder<FlexPotRecipe>> getRecipes() {
        return ClientRecipeLookup.getRecipes(ModRecipes.FLEX_POT_RECIPE);
    }

    @Override
    public void draw(RecipeHolder<FlexPotRecipe> holder, IRecipeSlotsView recipeSlotsView,
                     GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        FlexPotRecipe recipe = holder.value();
        this.bgDraw.draw(guiGraphics);
        drawCenteredString(guiGraphics, Component.translatable("jei.kaleidoscope_cookery.flex_recipe"), WIDTH / 2, 5);
        drawCenteredString(guiGraphics,
                Component.translatable("jei.kaleidoscope_cookery.pot.stir_fry_count", recipe.stirFryCount()),
                WIDTH / 2, 85);
    }

    private void drawCenteredString(GuiGraphicsExtractor guiGraphics, Component text, int centerX, int y) {
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = text.getVisualOrderText();
        guiGraphics.text(font, sequence, centerX - font.width(sequence) / 2, y, 0x555555, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<FlexPotRecipe> holder, IFocusGroup focuses) {
        FlexPotRecipe recipe = holder.value();
        NonNullList<Ingredient> inputs = recipe.getIngredients();
        ItemStack output = recipe.getResult();
        for (int i = 0; i < inputs.size(); i++) {
            int xOffset = (i % 3) * 18 + 15;
            int yOffset = (i / 3) * 18 + 24;
            builder.addSlot(RecipeIngredientRole.INPUT, xOffset, yOffset).add(inputs.get(i)).setBackground(slotDraw, -1, -1);
        }
        recipe.carrier().ifPresent(carrier ->
                builder.addSlot(RecipeIngredientRole.INPUT, 133, 18).add(carrier));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 143, 60).add(output).setBackground(slotDraw, -1, -1);
    }

    @Override
    public @NotNull IRecipeType<RecipeHolder<FlexPotRecipe>> getRecipeType() {
        return TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return TITLE;
    }

    @Override
    public int getWidth() {
        return WIDTH;
    }

    @Override
    public int getHeight() {
        return HEIGHT;
    }

    @Override
    @Nullable
    public IDrawable getIcon() {
        return iconDraw;
    }
}
