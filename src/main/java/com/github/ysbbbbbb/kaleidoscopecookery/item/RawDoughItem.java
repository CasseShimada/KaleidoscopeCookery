package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class RawDoughItem extends CookeryTooltipItem {
    private static final int MIN_USE_DURATION = 30;

    public RawDoughItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

    @Override
    public @NotNull ItemUseAnimation getUseAnimation(ItemStack pStack) {
        return ItemUseAnimation.BOW;
    }

    @Override
    public @NotNull InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack stack = playerIn.getItemInHand(handIn);
        playerIn.startUsingItem(handIn);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
        int time = stack.getUseDuration(entityLiving) - timeLeft;
        if (time < MIN_USE_DURATION) {
            return false;
        }
        if (worldIn.isClientSide()) {
            return true;
        }

        int count = entityLiving.hasInfiniteMaterials() ? 1 : stack.getCount();
        ItemStack noodles = new ItemStack(ModItems.RAW_NOODLES, count);
        stack.consume(count, entityLiving);
        worldIn.playSound(null, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(),
                ModSounds.ITEM_DOUGH_TRANSFORM, SoundSource.PLAYERS, 1.0F, 1.0F);
        ItemUtils.getItemToLivingEntity(entityLiving, noodles);
        if (entityLiving instanceof ServerPlayer serverPlayer) {
            ModTrigger.EVENT.trigger(serverPlayer, ModEventTriggerType.PULL_THE_DOUGH);
        }
        return true;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.raw_dough").withStyle(ChatFormatting.GRAY));
    }
}
