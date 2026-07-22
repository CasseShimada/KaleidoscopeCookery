package com.github.ysbbbbbb.kaleidoscopecookery.compat.wthit;

import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.EnamelBasinBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
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
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import mcp.mobius.waila.api.IBlockAccessor;
import mcp.mobius.waila.api.IBlockComponentProvider;
import mcp.mobius.waila.api.IPluginConfig;
import mcp.mobius.waila.api.ITargetRedirector;
import mcp.mobius.waila.api.ITooltip;
import mcp.mobius.waila.api.component.ItemComponent;
import mcp.mobius.waila.api.component.ItemListComponent;
import mcp.mobius.waila.api.component.ProgressArrowComponent;
import mcp.mobius.waila.api.component.SpacingComponent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jspecify.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public enum WthitComponentProvider implements IBlockComponentProvider {
    INSTANCE;

    @Override
    public ITargetRedirector.@Nullable Result redirect(ITargetRedirector redirect, IBlockAccessor accessor,
                                                        IPluginConfig config) {
        if (!(accessor.getBlock() instanceof MillstoneBlock)) {
            return null;
        }
        NinePart part = accessor.getBlockState().getValue(MillstoneBlock.PART);
        BlockPos center = accessor.getPosition().subtract(new Vec3i(part.getPosX(), 0, part.getPosY()));
        return center.equals(accessor.getPosition())
                ? null
                : redirect.to(accessor.getBlockHitResult().withPosition(center));
    }

    @Override
    public void appendBody(ITooltip tooltip, IBlockAccessor accessor, IPluginConfig config) {
        if (accessor.getBlock() instanceof EnamelBasinBlock) {
            if (config.getBoolean(WthitOptions.ENAMEL_BASIN)) {
                int oilCount = accessor.getBlockState().getValue(EnamelBasinBlock.OIL_COUNT);
                tooltip.addLine(Component.translatable(
                        "overlay.kaleidoscope_cookery.enamel_basin.oil_count", oilCount));
            }
            return;
        }
        if (accessor.getBlock() instanceof FoodBiteBlock) {
            if (config.getBoolean(WthitOptions.FOOD_BITE_BLOCK)) {
                int quality = accessor.getBlockState().getValue(FoodBiteBlock.QUALITY);
                if (quality != FoodBiteBlock.DEFAULT_QUALITY) {
                    tooltip.addLine(Quality.BY_ID.apply(quality).getTooltip());
                }
            }
            return;
        }

        Identifier option = optionFor(accessor.getBlockEntity());
        if (option == null || !config.getBoolean(option)) {
            return;
        }
        CompoundTag payload = accessor.getData().raw().getCompound(WthitDataProvider.DATA_KEY).orElse(null);
        if (payload == null) {
            return;
        }
        String kind = payload.getString(WthitDataProvider.KIND).orElse("");
        List<ItemStack> items = readItems(payload);
        switch (kind) {
            case "storage" -> {
                if (!items.isEmpty()) tooltip.addLine(new ItemListComponent(items));
            }
            case "shawarma" -> appendShawarma(tooltip, payload, items);
            case "chopping" -> appendChopping(tooltip, payload, items);
            case "oil_pot" -> appendOilPot(tooltip, payload);
            case "millstone" -> appendMillstone(tooltip, payload, items);
            case "recipe" -> appendRecipe(tooltip, payload, items);
            default -> {
            }
        }
    }

    private static void appendShawarma(ITooltip tooltip, CompoundTag payload, List<ItemStack> items) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            var line = tooltip.addLine()
                    .with(new ItemComponent(stack.copyWithCount(1)))
                    .with(new SpacingComponent(3, 1))
                    .with(Component.literal(stack.getCount() + "×").append(stack.getHoverName())
                            .withStyle(ChatFormatting.GRAY));
            int cookTime = payload.getInt(WthitDataProvider.COOK_TIMES + '_' + i).orElse(0);
            if (cookTime > 0) {
                line.with(new SpacingComponent(3, 1)).with(Component.translatable(
                        "overlay.kaleidoscope_cookery.shawarma_spit.cook_time", cookTime / 20.0F)
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static void appendChopping(ITooltip tooltip, CompoundTag payload, List<ItemStack> items) {
        if (items.isEmpty()) return;
        ItemStack stack = items.getFirst();
        tooltip.addLine().with(new ItemComponent(stack)).with(new SpacingComponent(3, 1)).with(stack.getHoverName());
        tooltip.addLine(Component.translatable("overlay.kaleidoscope_cookery.chopping_board.cut_count",
                payload.getInt(WthitDataProvider.CUT_COUNT).orElse(0),
                payload.getInt(WthitDataProvider.MAX_CUT_COUNT).orElse(0)));
    }

    private static void appendOilPot(ITooltip tooltip, CompoundTag payload) {
        int count = payload.getInt(WthitDataProvider.OIL_COUNT).orElse(0);
        tooltip.addLine(count > 0
                ? Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.count", count)
                : Component.translatable("tooltip.kaleidoscope_cookery.oil_pot.empty"));
    }

    private static void appendMillstone(ITooltip tooltip, CompoundTag payload, List<ItemStack> outputs) {
        ItemStack input = payload.read(WthitDataProvider.INPUT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        if (input.isEmpty() && outputs.isEmpty()) return;
        var line = tooltip.addLine();
        if (!input.isEmpty()) line.with(new ItemComponent(input));
        line.with(new ProgressArrowComponent(payload.getFloat(WthitDataProvider.PROGRESS).orElse(0.0F)));
        outputs.forEach(output -> line.with(new ItemComponent(output)));
    }

    private static void appendRecipe(ITooltip tooltip, CompoundTag payload, List<ItemStack> inputs) {
        ItemStack output = payload.read(WthitDataProvider.OUTPUT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        if (output.isEmpty()) return;
        tooltip.addLine(output.getHoverName());
        if (!inputs.isEmpty()) tooltip.addLine(new ItemListComponent(inputs));
        tooltip.addLine().with(new ProgressArrowComponent(1.0F)).with(new ItemComponent(output));
    }

    private static List<ItemStack> readItems(CompoundTag payload) {
        int count = payload.getInt(WthitDataProvider.ITEMS + "_count").orElse(0);
        List<ItemStack> items = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            payload.read(WthitDataProvider.ITEMS + '_' + i, ItemStack.CODEC)
                    .filter(stack -> !stack.isEmpty())
                    .ifPresent(items::add);
        }
        return items;
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
