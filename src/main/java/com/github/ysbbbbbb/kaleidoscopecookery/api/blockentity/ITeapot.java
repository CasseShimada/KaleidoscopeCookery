package com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public interface ITeapot {
    int PUT_INGREDIENT = 0;
    int PROCESSING = 1;
    int FINISHED = 2;

    int getStatus();

    boolean hasHeatSource(Level level);

    boolean addTeaFluid(Level level, LivingEntity user, ItemStack itemStack);

    boolean removeTeaFluid(Level level, LivingEntity user, ItemStack itemStack);

    boolean addIngredient(Level level, LivingEntity user, ItemStack itemStack);

    boolean removeIngredient(Level level, LivingEntity user);

    boolean takeTeapot(Level level, LivingEntity user);
}
