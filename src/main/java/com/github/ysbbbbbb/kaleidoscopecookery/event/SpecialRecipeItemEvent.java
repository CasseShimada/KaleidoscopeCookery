package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.RecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FruitBasketItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.IItemHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.util.neo.ItemStackHandler;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class SpecialRecipeItemEvent {

    private SpecialRecipeItemEvent() {
    }

    public static void register() {
        registerCheckItemEvent();
        registerDeductItemEvent();
    }

    private static void registerCheckItemEvent() {
        ModEvents.CHECK_SPECIAL_ITEM.register(event -> {
            ItemStack stack = event.getStack();

            // 果篮和饭袋，懒得写 cap 了
            if (stack.is(ModItems.FRUIT_BASKET)) {
                ItemStackHandler items = FruitBasketItem.getItems(stack);
                addItems(event, items);
                FruitBasketItem.saveItems(stack, items);
                return;
            }
            if (stack.is(ModItems.TRANSMUTATION_LUNCH_BAG)) {
                ItemStackHandler items = TransmutationLunchBagItem.getItems(stack);
                addItems(event, items);
                TransmutationLunchBagItem.setItems(stack, items);
                return;
            }
        });

    }


    private static void registerDeductItemEvent() {
        ModEvents.DEDUCT_SPECIAL_ITEM.register(event -> {
            ItemStack stack = event.getStack();

            // 果篮和饭袋，懒得写 cap 了
            if (stack.is(ModItems.FRUIT_BASKET)) {
                ItemStackHandler items = FruitBasketItem.getItems(stack);
                deductItems(event, items);
                FruitBasketItem.saveItems(stack, items);
                return;
            }
            if (stack.is(ModItems.TRANSMUTATION_LUNCH_BAG)) {
                ItemStackHandler items = TransmutationLunchBagItem.getItems(stack);
                deductItems(event, items);
                TransmutationLunchBagItem.setItems(stack, items);
                return;
            }
        });
    }

    private static void addItems(RecipeItemEvent.CheckItem event, ItemStackHandler items) {
        for (int i = 0; i < items.getSlots(); i++) {
            ItemStack slotStack = items.getStackInSlot(i);
            if (!slotStack.isEmpty()) {
                event.addItem(slotStack.getItem(), slotStack.getCount());
            }
        }
    }

    private static void deductItems(RecipeItemEvent.DeductItem event, ItemStackHandler items) {
        Item needItem = event.getNeedItem();
        for (int i = 0; i < items.getSlots(); i++) {
            int needCount = event.getNeedCount();
            if (needCount <= 0) {
                return;
            }
            ItemStack slotStack = items.getStackInSlot(i);
            if (slotStack.is(needItem)) {
                ItemStack extractItem = items.extractItem(i, needCount, false);
                event.deduct(extractItem.getCount());
            }
        }
    }
}
