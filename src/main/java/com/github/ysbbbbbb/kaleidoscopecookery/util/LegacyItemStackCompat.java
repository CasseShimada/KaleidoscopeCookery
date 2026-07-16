package com.github.ysbbbbbb.kaleidoscopecookery.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Decoder;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import net.minecraft.SharedConstants;
import net.minecraft.core.NonNullList;
import net.minecraft.util.datafix.DataFixers;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.ItemStackWithSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;

public final class LegacyItemStackCompat {
    public static final int FORGE_1_20_1_DATA_VERSION = 3465;

    public static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.of(
            ItemStack.CODEC, new CompatibleItemStackDecoder());
    private static final Codec<ItemStackWithSlot> ITEM_STACK_WITH_SLOT_CODEC = Codec.of(
            ItemStackWithSlot.CODEC, new CompatibleItemStackWithSlotDecoder());

    private LegacyItemStackCompat() {
    }

    public static ItemStack readItemStack(ValueInput input, String key) {
        return input.read(key, ITEM_STACK_CODEC).orElse(ItemStack.EMPTY);
    }

    public static void loadAllItems(ValueInput input, NonNullList<ItemStack> items) {
        for (ItemStackWithSlot entry : input.listOrEmpty("Items", ITEM_STACK_WITH_SLOT_CODEC)) {
            if (entry.isValidInContainer(items.size())) {
                items.set(entry.slot(), entry.stack());
            }
        }
    }

    public static ItemStack readItemStackOrHandler(ValueInput input, String key, int legacySlotCount) {
        ItemStack direct = readItemStack(input, key);
        if (!direct.isEmpty()) {
            return direct;
        }
        NonNullList<ItemStack> legacyItems = NonNullList.withSize(legacySlotCount, ItemStack.EMPTY);
        input.child(key).ifPresent(handler -> loadAllItems(handler, legacyItems));
        return legacyItems.stream().filter(stack -> !stack.isEmpty()).findFirst().orElse(ItemStack.EMPTY);
    }

    private static <T> DataResult<Pair<ItemStack, T>> decodeLegacyItemStack(DynamicOps<T> ops, T input) {
        try {
            Dynamic<T> updated = DataFixers.getDataFixer().update(
                    References.ITEM_STACK,
                    new Dynamic<>(ops, input),
                    FORGE_1_20_1_DATA_VERSION,
                    SharedConstants.getCurrentVersion().dataVersion().version());
            return ItemStack.CODEC.decode(ops, updated.getValue());
        } catch (RuntimeException e) {
            return DataResult.error(() -> "Failed to update Forge 1.20.1 item stack: " + e.getMessage());
        }
    }

    private static boolean isForge120Stack(Dynamic<?> input) {
        return input.get("Count").result().isPresent();
    }

    private static final class CompatibleItemStackDecoder implements Decoder<ItemStack> {
        @Override
        public <T> DataResult<Pair<ItemStack, T>> decode(DynamicOps<T> ops, T input) {
            return isForge120Stack(new Dynamic<>(ops, input))
                    ? decodeLegacyItemStack(ops, input)
                    : ItemStack.CODEC.decode(ops, input);
        }
    }

    private static final class CompatibleItemStackWithSlotDecoder implements Decoder<ItemStackWithSlot> {
        @Override
        public <T> DataResult<Pair<ItemStackWithSlot, T>> decode(DynamicOps<T> ops, T input) {
            if (!isForge120Stack(new Dynamic<>(ops, input))) {
                return ItemStackWithSlot.CODEC.decode(ops, input);
            }
            int slot = new Dynamic<>(ops, input).get("Slot").asInt(-1);
            if (slot < 0 || slot > 255) {
                return DataResult.error(() -> "Invalid legacy item stack slot: " + slot);
            }
            return decodeLegacyItemStack(ops, input)
                    .map(decoded -> Pair.of(new ItemStackWithSlot(slot, decoded.getFirst()), decoded.getSecond()));
        }
    }
}
