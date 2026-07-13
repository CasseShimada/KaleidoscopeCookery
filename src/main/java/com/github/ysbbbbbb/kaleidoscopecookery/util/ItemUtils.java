package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
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

public final class ItemUtils {
    private ItemUtils() {
    }

    public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack) {
        giveItemToLivingEntity(entity, stack, -1, false);
    }

    public static void getItemToLivingEntity(LivingEntity entity, ItemStack stack, int preferredSlot) {
        giveItemToLivingEntity(entity, stack, preferredSlot, true);
    }

    private static void giveItemToLivingEntity(LivingEntity entity, ItemStack stack,
                                               int preferredSlot, boolean usePreferredSlot) {
        if (stack.isEmpty()) {
            return;
        }
        if (entity.getMainHandItem().isEmpty()) {
            RandomSource random = entity.level().getRandom();
            entity.setItemInHand(InteractionHand.MAIN_HAND, stack);
            entity.playSound(SoundEvents.ITEM_PICKUP, 0.2F, ((random.nextFloat() - random.nextFloat()) * 0.7F + 1.0F) * 2.0F);
        } else if (entity instanceof Player player) {
            if (usePreferredSlot) {
                giveItemToPlayer(player, stack, preferredSlot);
            } else {
                player.getInventory().placeItemBackInInventory(stack);
            }
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
                remainder = insertIntoPlayerInventory(inventory, preferredSlot, stack);
            }

            if (!remainder.isEmpty()) {
                remainder = insertIntoPlayerInventory(inventory, -1, remainder);
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

    private static ItemStack insertIntoPlayerInventory(Inventory inventory, int slot, ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        int countBefore = countMatchingItems(inventory, stack);
        inventory.add(slot, stack.copy());
        int inserted = countMatchingItems(inventory, stack) - countBefore;
        return inserted >= stack.getCount()
                ? ItemStack.EMPTY
                : stack.copyWithCount(stack.getCount() - inserted);
    }

    private static int countMatchingItems(Inventory inventory, ItemStack stack) {
        return inventory.getNonEquipmentItems().stream()
                .filter(slot -> ItemStack.isSameItemSameComponents(slot, stack))
                .mapToInt(ItemStack::getCount)
                .sum();
    }

    private static ItemStack insertItem(ItemStackContainer dest, ItemStack stack) {
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

    public static ItemStack getContainerStack(ItemStack stack) {
        if (stack.isEmpty()) {
            return ItemStack.EMPTY;
        }
        UseRemainder useRemainder = stack.get(DataComponents.USE_REMAINDER);
        if (useRemainder != null) {
            ItemStack remainderStack = useRemainder.convertInto().create();
            if (!remainderStack.isEmpty()) {
                return remainderStack;
            }
        }
        Item item = stack.getItem();
        ItemStack remainingItem = item.getCraftingRemainder().create();
        if (!remainingItem.isEmpty()) {
            return remainingItem;
        }
        if (stack.is(TagMod.BOWL_CONTAINER)) {
            return Items.BOWL.getDefaultInstance();
        } else if (stack.is(TagMod.GLASS_BOTTLE_CONTAINER)) {
            return Items.GLASS_BOTTLE.getDefaultInstance();
        } else if (stack.is(TagMod.BUCKET_CONTAINER)) {
            return Items.BUCKET.getDefaultInstance();
        } else if (stack.is(Items.POTION)) {
            return Items.GLASS_BOTTLE.getDefaultInstance();
        }
        return ItemStack.EMPTY;
    }

    public static Component getIngredientName(Level level, Ingredient ingredient) {
        ItemStack stack = getFirstIngredientStack(level, ingredient);
        return stack.isEmpty() ? Component.empty() : stack.getHoverName();
    }

    public static ItemStack getFirstIngredientStack(Level level, Ingredient ingredient) {
        return ingredient.display().resolveForFirstStack(SlotDisplayContext.fromLevel(level));
    }
}
