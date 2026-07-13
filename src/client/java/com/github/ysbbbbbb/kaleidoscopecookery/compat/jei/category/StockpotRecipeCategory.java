package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.client.util.ClientRecipeLookup;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight.FarmersDelightCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.mojang.logging.LogUtils;
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
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;

@Environment(EnvType.CLIENT)
public class StockpotRecipeCategory implements IRecipeCategory<RecipeHolder<StockpotRecipe>> {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final IRecipeHolderType<StockpotRecipe> TYPE = IRecipeHolderType.create(Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "stockpot"));
    private static final Identifier BG = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "textures/gui/jei/stockpot.png");
    private static final MutableComponent TITLE = Component.translatable("block.kaleidoscope_cookery.stockpot");
    public static final int WIDTH = 176;
    public static final int HEIGHT = 102;
    private final IDrawable bgDraw;
    private final IDrawable iconDraw;
    private final IDrawable slotDraw;
    private static final Comparator<RecipeHolder<StockpotRecipe>> RECIPE_ORDER =
            Comparator.comparing((RecipeHolder<StockpotRecipe> holder) ->
                            BuiltInRegistries.ITEM.getKey(holder.value().getResult().getItem()).toString())
                    .thenComparingInt(holder -> holder.value().getResult().getCount())
                    .thenComparing(holder -> holder.id().identifier().toString());

    public StockpotRecipeCategory(IGuiHelper guiHelper) {
        this.bgDraw = guiHelper.createDrawable(BG, 0, 0, WIDTH, HEIGHT);
        this.iconDraw = guiHelper.createDrawableItemStack(ModItems.STOCKPOT.getDefaultInstance());
        this.slotDraw = guiHelper.getSlotDrawable();
    }

    public static List<RecipeHolder<StockpotRecipe>> getRecipes() {
        List<RecipeHolder<StockpotRecipe>> recipes = ClientRecipeLookup.getRecipes(ModRecipes.STOCKPOT_RECIPE);
        FarmersDelightCompat.appendStockpotRecipes(Minecraft.getInstance().level, recipes);
        recipes.sort(RECIPE_ORDER);
        return recipes;
    }

    @Override
    public void draw(RecipeHolder<StockpotRecipe> recipe, IRecipeSlotsView recipeSlotsView, GuiGraphicsExtractor guiGraphics, double mouseX, double mouseY) {
        this.bgDraw.draw(guiGraphics);
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, RecipeHolder<StockpotRecipe> holder, IFocusGroup focuses) {
        StockpotRecipe recipe = holder.value();
        NonNullList<Ingredient> inputs = recipe.getIngredients();
        ItemStack output = recipe.getResult();
        for (int i = 0; i < inputs.size(); i++) {
            int xOffset = (i % 3) * 18 + 15;
            int yOffset = (i / 3) * 18 + 25;
            builder.addSlot(RecipeIngredientRole.INPUT, xOffset, yOffset).add(inputs.get(i)).setBackground(slotDraw, -1, -1);
        }
        ISoupBase soupBase = SoupBaseManager.getSoupBase(recipe.soupBase());
        if (soupBase == null) {
            LOGGER.warn("Skipping missing soup base {} in JEI stockpot recipe {}", recipe.soupBase(), holder.id().identifier());
        } else {
            ItemStack displayStack = soupBase.getDisplayStack();
            if (!displayStack.isEmpty()) {
                builder.addSlot(RecipeIngredientRole.INPUT, 72, 61).add(Ingredient.of(displayStack.getItem()));
            }
        }
        if (!recipe.carrier().isEmpty()) {
            builder.addSlot(RecipeIngredientRole.INPUT, 133, 18).add(recipe.carrier()).setBackground(slotDraw, -1, -1);
        }
        builder.addSlot(RecipeIngredientRole.OUTPUT, 143, 60).add(output);
    }

    @Override
    public @NotNull IRecipeType<RecipeHolder<StockpotRecipe>> getRecipeType() {
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
