package com.github.ysbbbbbb.kaleidoscopecookery.event;

import com.github.ysbbbbbb.kaleidoscopecookery.api.event.RecipeItemEvent;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ActionEventCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FruitBasketItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
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
        ActionEventCallback.CheckSpecialItem.EVENT.register(SpecialRecipeItemEvent::onCheckSpecialItem);
    }

    private static void registerDeductItemEvent() {
        ActionEventCallback.DeductSpecialItem.EVENT.register(SpecialRecipeItemEvent::onDeductSpecialItem);
    }

    private static void onCheckSpecialItem(RecipeItemEvent.CheckItem event) {
        ItemStackContainer items = getSpecialContainerItems(event.getStack());
        if (items != null) {
            addItems(event, items);
            return;
        }
        Storage<ItemVariant> storage = findItemStorage(event);
        if (storage != null) {
            addItems(event, storage);
        }
    }

    private static void onDeductSpecialItem(RecipeItemEvent.DeductItem event) {
        ItemStack stack = event.getStack();
        ItemStackContainer items = getSpecialContainerItems(stack);
        if (items != null) {
            deductItems(event, items);
            saveSpecialContainerItems(stack, items);
            return;
        }
        Storage<ItemVariant> storage = findItemStorage(event);
        if (storage != null) {
            deductItems(event, storage);
        }
    }

    @Nullable
    private static Storage<ItemVariant> findItemStorage(RecipeItemEvent event) {
        ContainerItemContext context = event.getContainerContext();
        return context == null ? null : context.find(ItemStorage.ITEM);
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

    private static void addItems(RecipeItemEvent.CheckItem event, Storage<ItemVariant> storage) {
        for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
            int count = (int) Math.min(view.getAmount(), Integer.MAX_VALUE);
            event.addItem(view.getResource().getItem(), count);
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

    private static void deductItems(RecipeItemEvent.DeductItem event, Storage<ItemVariant> storage) {
        long extracted = 0;
        int needCount = event.getNeedCount();
        Item needItem = event.getNeedItem();
        try (Transaction transaction = Transaction.openOuter()) {
            for (StorageView<ItemVariant> view : storage.nonEmptyViews()) {
                ItemVariant resource = view.getResource();
                if (resource.getItem() == needItem) {
                    extracted += view.extract(resource, needCount - extracted, transaction);
                    if (extracted >= needCount) {
                        break;
                    }
                }
            }
            if (extracted > 0) {
                transaction.commit();
            }
        }
        event.deduct((int) extracted);
    }
}
