package com.github.ysbbbbbb.kaleidoscopecookery.init;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.CookeryEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.FlatulenceEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.SulfurEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.TundraStriderEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.VigorEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.WarmthEffect;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffect;

public final class ModEffects {
    public static Holder<MobEffect> FLATULENCE;
    public static Holder<MobEffect> TUNDRA_STRIDER;
    public static Holder<MobEffect> WARMTH;
    public static Holder<MobEffect> SATIATED_SHIELD;
    public static Holder<MobEffect> VIGOR;
    public static Holder<MobEffect> SULFUR;
    public static Holder<MobEffect> MUSTARD;
    public static Holder<MobEffect> PRESERVATION;
    public static Holder<MobEffect> HINDER;
    public static Holder<MobEffect> PROJECTILE_DODGE;
    public static Holder<MobEffect> INSTANT_SMELTING;
    public static Holder<MobEffect> VITALITY;

    private ModEffects() {
    }

    public static void registerEffects() {
        FLATULENCE = register("flatulence", new FlatulenceEffect(0xFFC6C6));
        TUNDRA_STRIDER = register("tundra_strider", new TundraStriderEffect(0xA1F8FC));
        WARMTH = register("warmth", new WarmthEffect(0xFF5F0E));
        SATIATED_SHIELD = register("satiated_shield", new CookeryEffect(0xFF1313));
        VIGOR = register("vigor", new VigorEffect(0x84C322));
        SULFUR = register("sulfur", new SulfurEffect(0xE8B75E));
        MUSTARD = register("mustard", new CookeryEffect(0x5A6D09));
        PRESERVATION = register("preservation", new CookeryEffect(0xAEC639));
        HINDER = register("hinder", new CookeryEffect(0x9E7E5A));
        PROJECTILE_DODGE = register("projectile_dodge", new CookeryEffect(0x8E27F7));
        INSTANT_SMELTING = register("instant_smelting", new CookeryEffect(0xF07C1C));
        VITALITY = register("vitality", new CookeryEffect(0x6A9E4E));
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static Holder<MobEffect> register(String path, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, id(path), effect);
    }
}
