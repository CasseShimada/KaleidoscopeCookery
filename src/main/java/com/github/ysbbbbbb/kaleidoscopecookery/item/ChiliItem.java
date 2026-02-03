package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.server.level.ServerLevel;

public class ChiliItem extends Item {
    private final int damage;

    public ChiliItem(int damage, Properties properties) {
        super(ModFoods.applyFood(properties, ModFoods.CHILI));
        this.damage = damage;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (level instanceof ServerLevel serverLevel) {
            entity.hurtServer(serverLevel, level.damageSources().magic(), this.damage);
        }
        return super.finishUsingItem(stack, level, entity);
    }
}
