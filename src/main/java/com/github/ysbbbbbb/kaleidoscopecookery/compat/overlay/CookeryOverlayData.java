package com.github.ysbbbbbb.kaleidoscopecookery.compat.overlay;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IPot;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenwareRacksBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.List;
import java.util.stream.Stream;

/**
 * Overlay-neutral snapshots. Returned stacks are defensive copies and contain only data that an
 * overlay needs to render, so Jade and WTHIT cannot accidentally expose or mutate live inventory.
 */
public final class CookeryOverlayData {
    private CookeryOverlayData() {
    }

    public static List<ItemStack> itemStorage(BlockEntity blockEntity) {
        Stream<ItemStack> stacks;
        if (blockEntity instanceof FruitBasketBlockEntity fruitBasket) {
            stacks = fruitBasket.getItems().stream();
        } else if (blockEntity instanceof KitchenwareRacksBlockEntity racks) {
            stacks = Stream.of(racks.getItemLeft(), racks.getItemRight());
        } else if (blockEntity instanceof TableBlockEntity table) {
            stacks = table.getItems().stream();
        } else if (blockEntity instanceof PotBlockEntity pot && pot.getStatus() < IPot.FINISHED) {
            stacks = pot.getInputs().stream();
        } else if (blockEntity instanceof StockpotBlockEntity stockpot
                && stockpot.getStatus() < IStockpot.FINISHED) {
            stacks = stockpot.getInputs().stream();
        } else if (blockEntity instanceof SteamerBlockEntity steamer) {
            stacks = steamer.getItems().stream();
        } else {
            return List.of();
        }
        return stacks.filter(stack -> !stack.isEmpty()).map(ItemStack::copy).toList();
    }
}
