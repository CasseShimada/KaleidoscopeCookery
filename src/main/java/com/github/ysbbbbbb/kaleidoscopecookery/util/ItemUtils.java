package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.api.item.IHasContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.tuple.Pair;

public final class ItemUtils {
    private ItemUtils() {
    }

    public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack) {
        if (stack.isEmpty()) {
            return;
        }
        if (entity.getMainHandItem().isEmpty()) {
            RandomSource random = entity.level().getRandom();
            entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
            entity.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else if (entity instanceof Player player) {
            player.getInventory().placeItemBackInInventory(stack);
        } else {
            // 否则直接在实体所处位置生成物品
            if (entity.level() instanceof ServerLevel serverLevel) {
                ItemEntity dropItem = entity.spawnAtLocation(serverLevel, stack);
                if (dropItem != null) {
                    dropItem.setPickUpDelay(0);
                }
            }
        }
    }

    public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack, int preferredSlot) {
        if (stack.isEmpty()) {
            return;
        }
        if (entity.getMainHandItem().isEmpty()) {
            RandomSource random = entity.level().getRandom();
            entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
            entity.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else if (entity instanceof Player player) {
            giveItemToPlayer(player, stack, preferredSlot);
        } else {
            // 否则直接在实体所处位置生成物品
            if (entity.level() instanceof ServerLevel serverLevel) {
                ItemEntity dropItem = entity.spawnAtLocation(serverLevel, stack);
                if (dropItem != null) {
                    dropItem.setPickUpDelay(0);
                }
            }
        }
    }

    public static Pair<Integer, ItemStack> getLastStack(NonNullList<ItemStack> itemList) {
        for (int i = itemList.size(); i > 0; i--) {
            int index = i - 1;
            ItemStack stack = itemList.get(index);
            if (!stack.isEmpty()) {
                return Pair.of(index, stack);
            }
        }
        return Pair.of(0, ItemStack.EMPTY);
    }

    public static void giveItemToPlayer(Player player, ItemStack stack) {
        giveItemToPlayer(player, stack, -1);
    }

    public static void giveItemToPlayer(Player player, ItemStack stack, int preferredSlot) {
        if (!stack.isEmpty()) {
            Level level = player.level();
            Inventory inventory = player.getInventory();
            int slotCount = inventory.getNonEquipmentItems().size();
            int originalCount = stack.getCount();
            ItemStack remainder = stack;
            if (preferredSlot >= 0 && preferredSlot < slotCount) {
                remainder = insertIntoPlayerSlot(inventory, preferredSlot, stack);
            }

            if (!remainder.isEmpty()) {
                remainder = insertItemStacked(inventory, remainder, slotCount);
            }

            if (remainder.isEmpty() || remainder.getCount() != originalCount) {
                level.playSound(null, player.getX(), player.getY() + (double)0.5F, player.getZ(), SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
                if (level.isClientSide()) {
                    for (int i = 0; i < slotCount; i++) {
                        ItemStack inSlot = inventory.getItem(i);
                        if (!inSlot.isEmpty()) {
                            inSlot.setPopTime(5);
                        }
                    }
                } else if (player instanceof ServerPlayer serverPlayer) {
                    serverPlayer.containerMenu.broadcastChanges();
                }
            }

            if (!remainder.isEmpty() && !level.isClientSide()) {
                ItemEntity entityItem = new ItemEntity(level, player.getX(), player.getY() + (double)0.5F, player.getZ(), remainder);
                entityItem.setPickUpDelay(40);
                entityItem.setDeltaMovement(entityItem.getDeltaMovement().multiply(0.0F, 1.0F, 0.0F));
                level.addFreshEntity(entityItem);
            }

        }
    }

    private static ItemStack insertItemStacked(Inventory inventory, ItemStack stack, int slotCount) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        if (!stack.isStackable()) {
            return insertItem(inventory, stack, slotCount);
        }
        for (int i = 0; i < slotCount; ++i) {
            ItemStack slot = inventory.getItem(i);
            if (ItemStack.isSameItemSameComponents(slot, stack)) {
                stack = insertIntoPlayerSlot(inventory, i, stack);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }

        return insertItem(inventory, stack, slotCount);
    }

    private static ItemStack insertItem(Inventory inventory, ItemStack stack, int slotCount) {
        for (int i = 0; i < slotCount; ++i) {
            if (inventory.getItem(i).isEmpty()) {
                stack = insertIntoPlayerSlot(inventory, i, stack);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }
        }
        return stack;
    }

    private static ItemStack insertIntoPlayerSlot(Inventory inventory, int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ItemStack existing = inventory.getItem(slot);
        int slotLimit = inventory.getMaxStackSize();
        if (!existing.isEmpty()) {
            if (!ItemStack.isSameItemSameComponents(stack, existing) || !inventory.canPlaceItem(slot, stack)) {
                return stack;
            }
            int limit = Math.min(existing.getMaxStackSize(), slotLimit);
            int space = limit - existing.getCount();
            if (space <= 0) {
                return stack;
            }
            int inserted = Math.min(space, stack.getCount());
            ItemStack remainder = stack.copy();
            ItemStack merged = remainder.split(inserted);
            merged.grow(existing.getCount());
            inventory.setItem(slot, merged);
            inventory.setChanged();
            return remainder.isEmpty() ? ItemStack.EMPTY : remainder;
        }

        if (!inventory.canPlaceItem(slot, stack)) {
            return stack;
        }
        int inserted = Math.min(Math.min(stack.getMaxStackSize(), slotLimit), stack.getCount());
        if (inserted >= stack.getCount()) {
            inventory.setItem(slot, stack);
            inventory.setChanged();
            return ItemStack.EMPTY;
        }
        ItemStack remainder = stack.copy();
        inventory.setItem(slot, remainder.split(inserted));
        inventory.setChanged();
        return remainder;
    }

    public static ItemStack insertItem(ItemStackContainer dest, ItemStack stack) {
        if (dest != null && !stack.isEmpty()) {
            for(int i = 0; i < dest.size(); ++i) {
                stack = dest.insertItem(i, stack);
                if (stack.isEmpty()) {
                    return ItemStack.EMPTY;
                }
            }

        }
        return stack;
    }

    public static ItemStack insertItemStacked(ItemStackContainer inventory, ItemStack stack) {
        if (inventory != null && !stack.isEmpty()) {
            if (!stack.isStackable()) {
                return insertItem(inventory, stack);
            } else {
                int sizeInventory = inventory.size();

                for(int i = 0; i < sizeInventory; ++i) {
                    ItemStack slot = inventory.get(i);
                    if (ItemStack.isSameItemSameComponents(slot, stack)) {
                        stack = inventory.insertItem(i, stack);
                        if (stack.isEmpty()) {
                            break;
                        }
                    }
                }

                if (!stack.isEmpty()) {
                    for(int i = 0; i < sizeInventory; ++i) {
                        if (inventory.get(i).isEmpty()) {
                            stack = inventory.insertItem(i, stack);
                            if (stack.isEmpty()) {
                                break;
                            }
                        }
                    }
                }

                return stack;
            }
        } else {
            return stack;
        }
    }

    public static Item getContainerItem(ItemStack stack) {
        if (stack.isEmpty()) {
            return Items.AIR;
        }
        UseRemainder useRemainder = stack.get(DataComponents.USE_REMAINDER);
        if (useRemainder != null) {
            ItemStack remainderStack = useRemainder.convertInto().create();
            if (!remainderStack.isEmpty()) {
                return remainderStack.getItem();
            }
        }
        Item item = stack.getItem();
        ItemStack remainingItem = item.getCraftingRemainder().create();
        if (!remainingItem.isEmpty()) {
            return remainingItem.getItem();
        }
        if (item instanceof IHasContainer hasContainer) {
            return hasContainer.getContainerItem();
        } else if (stack.is(TagMod.BOWL_CONTAINER)) {
            return Items.BOWL;
        } else if (stack.is(TagMod.GLASS_BOTTLE_CONTAINER)) {
            return Items.GLASS_BOTTLE;
        } else if (stack.is(TagMod.BUCKET_CONTAINER)) {
            return Items.BUCKET;
        } else if (stack.is(Items.POTION)) {
            return Items.GLASS_BOTTLE;
        }
        return Items.AIR;
    }

    public static Component getIngredientName(Level level, Ingredient ingredient) {
        ItemStack stack = getFirstIngredientStack(level, ingredient);
        return stack.isEmpty() ? Component.empty() : stack.getHoverName();
    }

    public static ItemStack getFirstIngredientStack(Level level, Ingredient ingredient) {
        return ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(level));
    }
}
