package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.tooltip.ItemContainerTooltip;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TransmutationLunchBagItem extends CookeryTooltipItem {
    private static final int MAX_SIZE = 16;

    public TransmutationLunchBagItem(Properties properties) {
        super(properties.stacksTo(1));
    }

    public static boolean hasItems(ItemStack bag) {
        return bag.has(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
    }

    public static ItemStackContainer getItems(ItemStack bag) {
        ItemContainer container = bag.get(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
        if (container != null) {
            return container.toContainer();
        }
        return new ItemStackContainer(MAX_SIZE);
    }

    public static void setItems(ItemStack bag, ItemStackContainer items) {
        // 先判断是否全空
        boolean allEmpty = true;
        for (int i = 0; i < items.size(); i++) {
            if (!items.get(i).isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        if (allEmpty) {
            bag.remove(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS);
        } else {
            bag.set(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS, ItemContainer.of(items));
        }
    }

    @Override
    public @NotNull InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        BlockEntity blockEntity = level.getBlockEntity(context.getClickedPos());
        if (!(blockEntity instanceof FruitBasketBlockEntity fruitBasket)) {
            return super.useOn(context);
        }
        Player player = context.getPlayer();
        if (player == null) {
            return super.useOn(context);
        }
        ItemStack bag = context.getItemInHand();
        if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        ItemStackContainer bagItems = TransmutationLunchBagItem.getItems(bag);
        ItemStackContainer fruitBasketItems = ItemStackContainer.wrap(fruitBasket.getItems());

        // 先检查果篮是否为空
        boolean basketEmpty = true;
        for (int i = 0; i < fruitBasketItems.size(); i++) {
            if (!fruitBasketItems.get(i).isEmpty()) {
                basketEmpty = false;
                break;
            }
        }

        // 果篮空了，那么尝试放入物品
        if (hasItems(bag) && basketEmpty) {
            for (int i = 0; i < bagItems.size(); i++) {
                ItemStack stack = bagItems.get(i);
                if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
                    ItemStack remaining = ItemUtils.insertItemStacked(fruitBasketItems, stack);
                    bagItems.extractItem(i, stack.getCount() - remaining.getCount());
                }
            }
            TransmutationLunchBagItem.setItems(bag, bagItems);
            fruitBasket.refresh();
            playRemoveOneSound(player);
            return InteractionResult.CONSUME;
        }

        // 果篮不为空，尝试取出物品
        for (int i = 0; i < fruitBasketItems.size(); i++) {
            ItemStack stack = fruitBasketItems.get(i);
            if (!stack.isEmpty() && canAdd(stack)) {
                ItemStack remaining = ItemUtils.insertItemStacked(bagItems, stack);
                fruitBasketItems.extractItem(i, stack.getCount() - remaining.getCount());
            }
        }
        TransmutationLunchBagItem.setItems(bag, bagItems);
        fruitBasket.refresh();
        playDropContentsSound(player);
        return InteractionResult.CONSUME;
    }

    @Override
    public @NotNull InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);
        // 潜行右键使用，取出物品
        if (player.isSecondaryUseActive() && hasItems(itemInHand)) {
            if (level.isClientSide()) {
                return InteractionResult.SUCCESS;
            }
            if (dropContents(itemInHand, player)) {
                this.playDropContentsSound(player);
                player.awardStat(Stats.ITEM_USED.get(this));
                return InteractionResult.CONSUME;
            }
            return InteractionResult.FAIL;
        }

        // 非潜行状态，并且里面有物品
        if (!player.isSecondaryUseActive() && hasItems(itemInHand)) {
            boolean hasFood = false;
            ItemStackContainer items = getItems(itemInHand);
            for (int i = 0; i < items.size(); i++) {
                ItemStack stackInSlot = items.get(i);
                if (!stackInSlot.isEmpty()) {
                    hasFood = true;
                    break;
                }
            }
            if (hasFood) {
                player.startUsingItem(hand);
                return InteractionResult.CONSUME;
            }
        }

        return InteractionResult.FAIL;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack bag, Level level, LivingEntity entity) {
        if (!hasItems(bag)) {
            return bag;
        }
        if (level.isClientSide()) {
            return bag;
        }
        ItemStack food = ItemStack.EMPTY;
        List<ApplyStatusEffectsConsumeEffect> effects = new ArrayList<>();
        boolean consumeContents = !entity.hasInfiniteMaterials();

        ItemStackContainer items = getItems(bag);
        for (int i = 0; i < items.size(); i++) {
            ItemStack stackInSlot = items.get(i);
            if (stackInSlot.isEmpty()) {
                continue;
            }

            // 先检查是不是食物
            FoodProperties properties = stackInSlot.get(DataComponents.FOOD);
            if (properties != null) {
                // 第一个食物的效果不加入其中，避免重复
                if (!food.isEmpty()) {
                    Consumable consumable = stackInSlot.get(DataComponents.CONSUMABLE);
                    if (consumable != null) {
                        for (ConsumeEffect effect : consumable.onConsumeEffects()) {
                            if (effect instanceof ApplyStatusEffectsConsumeEffect apply) {
                                effects.add(apply);
                            }
                        }
                    }
                } else {
                    food = consumeContents ? items.extractItem(i, 1) : stackInSlot.copyWithCount(1);
                }
                continue;
            }

            // 其次检查是不是药水
            PotionContents potionContents = stackInSlot.get(DataComponents.POTION_CONTENTS);
            if (potionContents != null) {
                // 第一个药水的效果不加入其中，避免重复
                if (!food.isEmpty()) {
                    potionContents.customEffects().forEach(e -> effects.add(new ApplyStatusEffectsConsumeEffect(e, 1F)));
                } else {
                    food = consumeContents ? items.extractItem(i, 1) : stackInSlot.copyWithCount(1);
                }
            }
        }

        if (!food.isEmpty()) {
            // 消耗物品
            ItemStack returnStack = food.finishUsingItem(level, entity);
            Item containerItem = ItemUtils.getContainerItem(food);

            // 返还容器
            if (!entity.hasInfiniteMaterials()) {
                if (!returnStack.isEmpty()) {
                    ItemUtils.getItemToLivingEntity(entity, returnStack);
                } else if (containerItem != Items.AIR) {
                    ItemUtils.getItemToLivingEntity(entity, containerItem.getDefaultInstance());
                }
            }

            // 处理效果
            for (ApplyStatusEffectsConsumeEffect effect : effects) {
                if (level.isClientSide() || effect.probability() <= 0.0F || level.getRandom().nextFloat() >= effect.probability()) {
                    continue;
                }
                for (MobEffectInstance instance : effect.effects()) {
                    entity.addEffect(new MobEffectInstance(instance));
                }
            }

            // 给予成就
            if (entity instanceof ServerPlayer player) {
                ModTrigger.EVENT.trigger(player, ModEventTriggerType.USE_TRANSMUTATION_LUNCH_BAG);
            }

            // 更新饭袋数据
            if (consumeContents) {
                setItems(bag, items);
            }
            return bag;
        }

        return bag;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack stack) {
        return hasItems(stack) ? ItemUseAnimation.EAT : ItemUseAnimation.NONE;
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 32;
    }

    @Override
    public boolean overrideStackedOnOther(ItemStack bag, Slot slot, ClickAction action, Player player) {
        if (bag.getCount() != 1 || action != ClickAction.SECONDARY) {
            return false;
        }
        ItemStack clickItem = slot.getItem();
        if (clickItem.isEmpty()) {
            // 当点击的地方为空，那么取出物品
            this.playRemoveOneSound(player);
            removeOne(bag).ifPresent(stack -> add(bag, slot.safeInsert(stack)));
        } else if (clickItem.getItem().canFitInsideContainerItems() && canAdd(clickItem)) {
            // 否则，放入食物
            int addCount = add(bag, clickItem);
            if (addCount > 0) {
                slot.safeTake(clickItem.getCount(), addCount, player);
                this.playInsertSound(player);
            }
        }
        return true;
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack bag, ItemStack other, Slot slot, ClickAction action, Player
            player, SlotAccess access) {
        if (bag.getCount() != 1) {
            return false;
        }
        if (action != ClickAction.SECONDARY || !slot.allowModification(player)) {
            return false;
        }
        if (other.isEmpty()) {
            removeOne(bag).ifPresent(stack -> {
                this.playRemoveOneSound(player);
                access.set(stack);
            });
        } else {
            int added = add(bag, other);
            if (added > 0) {
                this.playInsertSound(player);
                other.shrink(added);
            }
        }
        return true;
    }

    public static boolean canAdd(ItemStack food) {
        if (food.isEmpty()) {
            return false;
        }
        if (!food.getItem().canFitInsideContainerItems()) {
            return false;
        }
        return food.has(DataComponents.FOOD) || food.has(DataComponents.POTION_CONTENTS);
    }

    private static Optional<ItemStack> removeOne(ItemStack bag) {
        if (!hasItems(bag)) {
            return Optional.empty();
        }
        ItemStackContainer items = getItems(bag);
        for (int i = 0; i < items.size(); i++) {
            ItemStack extractItem = items.extractItem(i, items.get(i).getCount());
            if (!extractItem.isEmpty()) {
                setItems(bag, items);
                return Optional.of(extractItem);
            }
        }
        return Optional.empty();
    }

    private static int add(ItemStack bag, ItemStack food) {
        if (food.isEmpty() || !food.getItem().canFitInsideContainerItems() || !canAdd(food)) {
            return 0;
        }
        int totalCount = food.getCount();

        ItemStackContainer items = getItems(bag);
        ItemStack remaining = ItemUtils.insertItemStacked(items, food);

        int addCount = totalCount - (remaining.isEmpty() ? 0 : remaining.getCount());
        if (addCount > 0) {
            setItems(bag, items);
            return addCount;
        }
        return 0;
    }

    private static boolean dropContents(ItemStack bag, Player player) {
        if (!hasItems(bag)) {
            return false;
        }
        boolean result = false;
        ItemStackContainer items = getItems(bag);
        for (int i = 0; i < items.size(); i++) {
            ItemStack stack = items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemUtils.giveItemToPlayer(player, stack);
            result = true;
        }
        if (result) {
            items = new ItemStackContainer(MAX_SIZE);
            setItems(bag, items);
        }
        return result;
    }

    private void playRemoveOneSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_REMOVE_ONE, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playInsertSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_INSERT, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    private void playDropContentsSound(Entity pEntity) {
        pEntity.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 0.8F, 0.8F + pEntity.level().getRandom().nextFloat() * 0.4F);
    }

    @Override
    public @NotNull Optional<TooltipComponent> getTooltipImage(ItemStack stack) {
        if (!hasItems(stack)) {
            return Optional.empty();
        }
        ItemStackContainer items = getItems(stack);
        return Optional.of(new ItemContainerTooltip(items.copyStacks()));
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.transmutation_lunch_bag").withStyle(ChatFormatting.GRAY));
    }

    public record ItemContainer(ItemStackContainer items) {
        public ItemContainer {
            items = copyItems(items);
        }

        public static ItemContainer of(ItemStackContainer items) {
            return new ItemContainer(items);
        }

        public ItemStackContainer toContainer() {
            return copyItems(this.items);
        }

        @Override
        public ItemStackContainer items() {
            return toContainer();
        }

        private static ItemStackContainer copyItems(ItemStackContainer items) {
            return ItemStackContainer.copyOf(items, MAX_SIZE);
        }

        private static ItemContainer fromList(List<ItemStack> list) {
            ItemStackContainer handler = new ItemStackContainer(MAX_SIZE);
            for (int i = 0; i < Math.min(list.size(), handler.size()); i++) {
                handler.set(i, list.get(i));
            }
            return new ItemContainer(handler);
        }

        private List<ItemStack> itemsForCodec() {
            ItemStackContainer handler = this.items();
            List<ItemStack> output = new ArrayList<>();
            for (int i = 0; i < handler.size(); i++) {
                output.add(handler.get(i));
            }
            return output;
        }

        public static final Codec<ItemContainer> CODEC = ItemStack.OPTIONAL_CODEC.listOf().xmap(
                ItemContainer::fromList,
                ItemContainer::itemsForCodec
        );

        public static final StreamCodec<RegistryFriendlyByteBuf, ItemContainer> STREAM_CODEC = new StreamCodec<>() {
            @Override
            public @NotNull ItemContainer decode(RegistryFriendlyByteBuf buffer) {
                CompoundTag compoundTag = buffer.readNbt();
                ItemStackContainer handler = new ItemStackContainer(MAX_SIZE);
                if (compoundTag != null) {
                    handler.deserializeNBT(buffer.registryAccess(), compoundTag);
                }
                return new ItemContainer(handler);
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buffer, ItemContainer value) {
                CompoundTag compoundTag = value.items().serializeNBT(buffer.registryAccess());
                buffer.writeNbt(compoundTag);
            }
        };
    }
}
