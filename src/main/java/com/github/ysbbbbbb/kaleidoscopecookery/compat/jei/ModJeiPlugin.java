package com.github.ysbbbbbb.kaleidoscopecookery.compat.jei;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.jei.category.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class ModJeiPlugin implements IModPlugin {
    private static final Identifier UID = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, "jei");

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new PotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new ChoppingBoardRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new StockpotRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new MillstoneRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new SteamerRecipeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(PotRecipeCategory.TYPE, PotRecipeCategory.getRecipes());
        registration.addRecipes(ChoppingBoardRecipeCategory.TYPE, ChoppingBoardRecipeCategory.getRecipes());
        registration.addRecipes(StockpotRecipeCategory.TYPE, StockpotRecipeCategory.getRecipes());
        registration.addRecipes(MillstoneRecipeCategory.TYPE, MillstoneRecipeCategory.getRecipes());
        registration.addRecipes(SteamerRecipeCategory.TYPE, SteamerRecipeCategory.getRecipes());
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addCraftingStation(PotRecipeCategory.TYPE, ModItems.POT);
        registration.addCraftingStation(ChoppingBoardRecipeCategory.TYPE, ModItems.CHOPPING_BOARD);
        registration.addCraftingStation(StockpotRecipeCategory.TYPE, ModItems.STOCKPOT);
        registration.addCraftingStation(MillstoneRecipeCategory.TYPE, ModItems.MILLSTONE);
        registration.addCraftingStation(SteamerRecipeCategory.TYPE, ModItems.STEAMER);
    }

    @Override
    public @NotNull Identifier getPluginUid() {
        return UID;
    }
}
