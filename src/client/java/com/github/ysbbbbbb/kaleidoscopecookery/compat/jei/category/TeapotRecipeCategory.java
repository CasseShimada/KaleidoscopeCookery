package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.client.util.RecipeJsonLoader;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
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
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Environment(EnvType.CLIENT)
public class TeapotRecipeCategory implements IRecipeCategory<RecipeHolder<TeapotRecipe>> {
    public static final IRecipeHolderType<TeapotRecipe> TYPE =
            IRecipeHolderType.create(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "teapot"));
    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/teapot.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.teapot");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 78;
    private final IDrawable bgDraw;
    private final IDrawable iconDraw;

    public TeapotRecipeCategory(IGuiHelper guiHelper) {
        this.bgDraw = guiHelper.createDrawable(BG, 0, 0, WIDTH, HEIGHT);
        this.iconDraw = guiHelper.createDrawableItemStack(ModItems.TEAPOT.getDefaultInstance());
    }

    public static List<RecipeHolder<TeapotRecipe>> getRecipes() {
        return RecipeJsonLoader.getRecipes(ModRecipes.TEAPOT_RECIPE, ModRecipes.TEAPOT_SERIALIZER);
    }

    @Override
    public void draw(RecipeHolder<TeapotRecipe> holder, IRecipeSlotsView recipeSlotsView,
                     GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        TeapotRecipe recipe = holder.value();
        this.bgDraw.draw(guiGraphics);
        drawCenteredString(guiGraphics, Component.translatable("jei.kaleidoscope_cookery.teapot.time", recipe.time() / 20),
                WIDTH / 2, 70);
    }

    private void drawCenteredString(GuiGraphicsExtractor guiGraphics, Component text, int centerX, int y) {
        Font font = Minecraft.getInstance().font;
        FormattedCharSequence sequence = text.getVisualOrderText();
        guiGraphics.text(font, sequence, centerX - font.width(sequence) / 2, y, 0x555555, false);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<TeapotRecipe> holder, IFocusGroup focuses) {
        TeapotRecipe recipe = holder.value();
        List<ItemStack> inputs = getIngredientStacks(recipe);
        ItemStack output = recipe.getResult();
        Fluid fluid = BuiltInRegistries.FLUID.getValue(recipe.teaFluid());
        Item bucket = recipe.teaFluid().equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID) || fluid == null
                ? Items.WATER_BUCKET
                : fluid.getBucket();

        builder.addSlot(RecipeIngredientRole.INPUT, 65, 3).setStandardSlotBackground().add(bucket.getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.INPUT, 83, 3).setStandardSlotBackground().addItemStacks(inputs);
        builder.addSlot(RecipeIngredientRole.OUTPUT, 128, 30).add(output);
    }

    private static List<ItemStack> getIngredientStacks(TeapotRecipe recipe) {
        ClientLevel level = Minecraft.getInstance().level;
        if (level != null) {
            List<ItemStack> stacks = recipe.ingredient().display()
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
        return recipe.ingredient().items()
                .map(item -> item.value().getDefaultInstance().copyWithCount(recipe.ingredientCount()))
                .toList();
    }

    @Override
    public @NotNull IRecipeType<RecipeHolder<TeapotRecipe>> getRecipeType() {
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
