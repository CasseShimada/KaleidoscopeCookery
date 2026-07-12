package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class BowlFoodOnlyItem extends CookeryTooltipItem {
    private final List<MobEffectInstance> effectInstances = new ArrayList<>();

    public BowlFoodOnlyItem(Properties itemProperties, FoodProperties properties) {
        super(ModFoods.applyFood(itemProperties, properties));
        Consumable consumable = ModFoods.getConsumable(properties);
        if (consumable != null) {
            for (ConsumeEffect effect : consumable.onConsumeEffects()) {
                if (effect instanceof ApplyStatusEffectsConsumeEffect apply && apply.probability() >= 1F) {
                    effectInstances.addAll(apply.effects());
                }
            }
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        ItemStack consumed = stack.copy();
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        if (entity instanceof Player player) {
            FoodQualityHelper.applyQualityAfterConsume(consumed, player);
        }
        if (entity.hasInfiniteMaterials()) {
            return itemStack;
        }
        ItemStack bowl = new ItemStack(Items.BOWL);
        if (itemStack.isEmpty()) {
            return bowl;
        }
        if (!level.isClientSide()) {
            if (entity instanceof Player player) {
                ItemUtils.giveItemToPlayer(player, bowl);
            } else {
                ItemUtils.getItemToLivingEntity(entity, bowl);
            }
        }
        return itemStack;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        FoodQualityHelper.addQualityAndEffects(stack, this.effectInstances, context, tooltipComponents, false);
    }
}
