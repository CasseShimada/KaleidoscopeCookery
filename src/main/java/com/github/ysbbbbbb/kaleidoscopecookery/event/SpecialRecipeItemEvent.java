package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.RecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FruitBasketItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class SpecialRecipeItemEvent {
    private SpecialRecipeItemEvent() {
    }

    public static void register() {
        registerCheckItemEvent();
        registerDeductItemEvent();
    }

    private static void registerCheckItemEvent() {
        ModEvents.CHECK_SPECIAL_ITEM.register(SpecialRecipeItemEvent::onCheckSpecialItem);
    }

    private static void registerDeductItemEvent() {
        ModEvents.DEDUCT_SPECIAL_ITEM.register(SpecialRecipeItemEvent::onDeductSpecialItem);
    }

    private static void onCheckSpecialItem(RecipeItemEvent.CheckItem event) {
        ItemStackContainer items = getSpecialContainerItems(event.getStack());
        if (items != null) {
            addItems(event, items);
        }
    }

    private static void onDeductSpecialItem(RecipeItemEvent.DeductItem event) {
        ItemStack stack = event.getStack();
        ItemStackContainer items = getSpecialContainerItems(stack);
        if (items == null) {
            return;
        }
        deductItems(event, items);
        saveSpecialContainerItems(stack, items);
    }

    @Nullable
    private static ItemStackContainer getSpecialContainerItems(ItemStack stack) {
        if (stack.is(ModItems.FRUIT_BASKET)) {
            return FruitBasketItem.getItems(stack);
        }
        if (stack.is(ModItems.TRANSMUTATION_LUNCH_BAG)) {
            return TransmutationLunchBagItem.getItems(stack);
        }
        return null;
    }

    private static void saveSpecialContainerItems(ItemStack stack, ItemStackContainer items) {
        if (stack.is(ModItems.FRUIT_BASKET)) {
            FruitBasketItem.saveItems(stack, items);
        } else if (stack.is(ModItems.TRANSMUTATION_LUNCH_BAG)) {
            TransmutationLunchBagItem.setItems(stack, items);
        }
    }

    private static void addItems(RecipeItemEvent.CheckItem event, ItemStackContainer items) {
        for (int i = 0; i < items.size(); i++) {
            ItemStack slotStack = items.get(i);
            if (!slotStack.isEmpty()) {
                event.addItem(slotStack.getItem(), slotStack.getCount());
            }
        }
    }

    private static void deductItems(RecipeItemEvent.DeductItem event, ItemStackContainer items) {
        Item needItem = event.getNeedItem();
        for (int i = 0; i < items.size(); i++) {
            int needCount = event.getNeedCount();
            if (needCount <= 0) {
                return;
            }
            ItemStack slotStack = items.get(i);
            if (slotStack.is(needItem)) {
                ItemStack extractItem = items.extractItem(i, needCount);
                event.deduct(extractItem.getCount());
            }
        }
    }
}
