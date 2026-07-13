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
    public static final Holder<MobEffect> FLATULENCE = register("flatulence", new FlatulenceEffect(0xFFC6C6));
    public static final Holder<MobEffect> TUNDRA_STRIDER = register(
            "tundra_strider", new TundraStriderEffect(0xA1F8FC));
    public static final Holder<MobEffect> WARMTH = register("warmth", new WarmthEffect(0xFF5F0E));
    public static final Holder<MobEffect> SATIATED_SHIELD = register(
            "satiated_shield", new CookeryEffect(0xFF1313));
    public static final Holder<MobEffect> VIGOR = register("vigor", new VigorEffect(0x84C322));
    public static final Holder<MobEffect> SULFUR = register("sulfur", new SulfurEffect(0xE8B75E));
    public static final Holder<MobEffect> MUSTARD = register("mustard", new CookeryEffect(0x5A6D09));
    public static final Holder<MobEffect> PRESERVATION = register("preservation", new CookeryEffect(0xAEC639));
    public static final Holder<MobEffect> HINDER = register("hinder", new CookeryEffect(0x9E7E5A));
    public static final Holder<MobEffect> PROJECTILE_DODGE = register(
            "projectile_dodge", new CookeryEffect(0x8E27F7));
    public static final Holder<MobEffect> INSTANT_SMELTING = register(
            "instant_smelting", new CookeryEffect(0xF07C1C));
    public static final Holder<MobEffect> VITALITY = register("vitality", new CookeryEffect(0x6A9E4E));

    private ModEffects() {
    }

    public static void registerEffects() {
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static Holder<MobEffect> register(String path, MobEffect effect) {
        return Registry.registerForHolder(BuiltInRegistries.MOB_EFFECT, id(path), effect);
    }
}
