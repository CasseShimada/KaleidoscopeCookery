package com.github.ysbbbbbb.kaleidoscopecookery.mixin;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootTables;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModVillager;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

@Mixin(GiveGiftToHero.class)
public abstract class GiveGiftToHeroMixin {
    @Shadow
    @Final
    @Mutable
    private static Map<ResourceKey<VillagerProfession>, ResourceKey<LootTable>> GIFTS;

    @Inject(method = "<clinit>", at = @At("TAIL"))
    private static void kaleidoscopeCookery$registerChefGift(CallbackInfo callbackInfo) {
        GIFTS = ImmutableMap.<ResourceKey<VillagerProfession>, ResourceKey<LootTable>>builder()
                .putAll(GIFTS)
                .put(ModVillager.CHEF_KEY, ModLootTables.CHEF_GIFT)
                .build();
    }
}
