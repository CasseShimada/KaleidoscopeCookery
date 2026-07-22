package com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit;

import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenwareRacksBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.overlay.CookeryOverlayData;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import mcp.mobius.waila.api.IDataProvider;
import mcp.mobius.waila.api.IDataWriter;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.IServerAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;

import java.util.ArrayList;
import java.util.List;

public enum WthitDataProvider implements IDataProvider<BlockEntity> {
    INSTANCE;

    public static final String DATA_KEY = "kaleidoscope_cookery";
    public static final String KIND = "kind";
    public static final String ITEMS = "items";
    public static final String COOK_TIMES = "cook_times";
    public static final String CUT_COUNT = "cut_count";
    public static final String MAX_CUT_COUNT = "max_cut_count";
    public static final String OIL_COUNT = "oil_count";
    public static final String PROGRESS = "progress";
    public static final String INPUT = "input";
    public static final String OUTPUT = "output";

    @Override
    public void appendData(IDataWriter writer, IServerAccessor<BlockEntity> accessor, IPluginConfig config) {
        BlockEntity target = accessor.getTarget();
        Identifier option = optionFor(target);
        if (option == null || !config.getBoolean(option)) {
            return;
        }

        CompoundTag payload = new CompoundTag();
        if (isItemStorage(target)) {
            payload.putString(KIND, "storage");
            storeItems(payload, CookeryOverlayData.itemStorage(target));
        } else if (target instanceof ShawarmaSpitBlockEntity shawarma) {
            payload.putString(KIND, "shawarma");
            storeShawarma(payload, shawarma);
        } else if (target instanceof ChoppingBoardBlockEntity choppingBoard) {
            payload.putString(KIND, "chopping");
            storeItems(payload, List.of(choppingBoard.getCurrentCutStack()));
            payload.putInt(CUT_COUNT, choppingBoard.getCurrentCutCount());
            payload.putInt(MAX_CUT_COUNT, choppingBoard.getMaxCutCount());
        } else if (target instanceof OilPotBlockEntity oilPot) {
            payload.putString(KIND, "oil_pot");
            payload.putInt(OIL_COUNT, oilPot.getOilCount());
        } else if (target instanceof MillstoneBlockEntity millstone) {
            payload.putString(KIND, "millstone");
            if (!millstone.getInput().isEmpty()) {
                payload.store(INPUT, ItemStack.CODEC, millstone.getInput());
            }
            storeItems(payload, millstone.getOutputs());
            payload.putFloat(PROGRESS, millstone.getProgressPercent());
        } else if (target instanceof RecipeBlockEntity recipeBlock) {
            ItemStack recipeStack = recipeBlock.getItem();
            RecipeItem.RecipeRecord recipe = RecipeItem.getRecipe(recipeStack);
            if (recipe == null) {
                return;
            }
            payload.putString(KIND, "recipe");
            storeItems(payload, recipe.input());
            payload.store(OUTPUT, ItemStack.CODEC, recipe.output());
        } else {
            return;
        }

        writer.raw().put(DATA_KEY, payload);
    }

    private static void storeShawarma(CompoundTag payload, ShawarmaSpitBlockEntity first) {
        List<ItemStack> items = new ArrayList<>(2);
        List<Integer> cookTimes = new ArrayList<>(2);
        BlockPos position = first.getBlockPos();
        DoubleBlockHalf half = first.getBlockState().getValue(ShawarmaSpitBlock.HALF);
        BlockPos otherPosition = half == DoubleBlockHalf.LOWER ? position.above() : position.below();
        BlockEntity other = first.getLevel() == null ? null : first.getLevel().getBlockEntity(otherPosition);

        ShawarmaSpitBlockEntity upper = half == DoubleBlockHalf.UPPER ? first
                : other instanceof ShawarmaSpitBlockEntity spit ? spit : null;
        ShawarmaSpitBlockEntity lower = half == DoubleBlockHalf.LOWER ? first
                : other instanceof ShawarmaSpitBlockEntity spit ? spit : null;
        addShawarma(items, cookTimes, upper);
        addShawarma(items, cookTimes, lower);
        storeItems(payload, items);
        payload.putInt(COOK_TIMES + "_count", cookTimes.size());
        for (int i = 0; i < cookTimes.size(); i++) {
            payload.putInt(COOK_TIMES + '_' + i, cookTimes.get(i));
        }
    }

    private static void addShawarma(List<ItemStack> items, List<Integer> cookTimes,
                                    ShawarmaSpitBlockEntity shawarma) {
        if (shawarma != null && !shawarma.getStoredItem().isEmpty()) {
            items.add(shawarma.getStoredItem());
            cookTimes.add(shawarma.getCookTime());
        }
    }

    private static void storeItems(CompoundTag payload, List<ItemStack> stacks) {
        List<ItemStack> visible = stacks.stream()
                .filter(stack -> !stack.isEmpty())
                .map(ItemStack::copy)
                .toList();
        payload.putInt(ITEMS + "_count", visible.size());
        for (int i = 0; i < visible.size(); i++) {
            payload.store(ITEMS + '_' + i, ItemStack.CODEC, visible.get(i));
        }
    }

    private static boolean isItemStorage(BlockEntity target) {
        return target instanceof FruitBasketBlockEntity
                || target instanceof KitchenwareRacksBlockEntity
                || target instanceof TableBlockEntity
                || target instanceof PotBlockEntity
                || target instanceof StockpotBlockEntity
                || target instanceof SteamerBlockEntity;
    }

    private static Identifier optionFor(BlockEntity target) {
        if (target instanceof FruitBasketBlockEntity) return WthitOptions.FRUIT_BASKET;
        if (target instanceof KitchenwareRacksBlockEntity) return WthitOptions.KITCHENWARE_RACK;
        if (target instanceof TableBlockEntity) return WthitOptions.TABLE;
        if (target instanceof PotBlockEntity) return WthitOptions.POT;
        if (target instanceof StockpotBlockEntity) return WthitOptions.STOCKPOT;
        if (target instanceof SteamerBlockEntity) return WthitOptions.STEAMER;
        if (target instanceof ShawarmaSpitBlockEntity) return WthitOptions.SHAWARMA_SPIT;
        if (target instanceof ChoppingBoardBlockEntity) return WthitOptions.CHOPPING_BOARD;
        if (target instanceof OilPotBlockEntity) return WthitOptions.OIL_POT;
        if (target instanceof MillstoneBlockEntity) return WthitOptions.MILLSTONE;
        if (target instanceof RecipeBlockEntity) return WthitOptions.RECIPE_BLOCK;
        return null;
    }
}
