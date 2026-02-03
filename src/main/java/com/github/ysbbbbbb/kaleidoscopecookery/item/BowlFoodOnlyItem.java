package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.consume_effects.ConsumeEffect;
import net.minecraft.world.level.Level;

import java.util.List;
import java.util.function.Consumer;

public class BowlFoodOnlyItem extends Item {
    private final List<MobEffectInstance> effectInstances = Lists.newArrayList();

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
        ItemStack itemStack = super.finishUsingItem(stack, level, entity);
        ItemStack bowl = new ItemStack(Items.BOWL);
        if (itemStack.isEmpty()) {
            return bowl;
        }
        if (entity instanceof Player player) {
            player.getInventory().placeItemBackInInventory(bowl);
        } else {
            ItemEntity itemEntity = new ItemEntity(level, entity.getX(), entity.getY(), entity.getZ(), bowl);
            level.addFreshEntity(itemEntity);
        }
        return itemStack;
    }

    @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltipComponents, TooltipFlag tooltipFlag) {
        if (!this.effectInstances.isEmpty()) {
            PotionContents.addPotionTooltip(this.effectInstances, tooltipComponents, 1.0F, context.tickRate());
        }
    }
}
