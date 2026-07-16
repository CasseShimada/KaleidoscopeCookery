package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import net.minecraft.world.food.FoodData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(FoodData.class)
public interface FoodDataAccessor {
    @Accessor("exhaustionLevel")
    void kaleidoscopeCookery$setExhaustionLevel(float exhaustionLevel);
}
