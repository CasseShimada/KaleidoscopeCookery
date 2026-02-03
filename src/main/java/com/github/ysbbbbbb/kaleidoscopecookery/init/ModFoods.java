package com.github.ysbbbbbb.kaleidoscopecookery.init;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects.*;
import static net.minecraft.world.effect.MobEffects.*;

public interface ModFoods {
    float FAST_CONSUME_SECONDS = 0.8F;
    Map<FoodProperties, Consumable> CONSUMABLES = new IdentityHashMap<>();

    static FoodBuilder builder() {
        return new FoodBuilder();
    }

    static Item.Properties applyFood(Item.Properties properties, FoodProperties food) {
        Consumable consumable = CONSUMABLES.get(food);
        if (consumable != null) {
            return properties.food(food, consumable);
        }
        return properties.food(food);
    }

    static Consumable getConsumable(FoodProperties food) {
        return CONSUMABLES.get(food);
    }

    final class FoodBuilder {
        private final FoodProperties.Builder builder = new FoodProperties.Builder();
        private final List<ApplyStatusEffectsConsumeEffect> effects = new ArrayList<>();
        private boolean fast;

        public FoodBuilder nutrition(int nutrition) {
            builder.nutrition(nutrition);
            return this;
        }

        public FoodBuilder saturationModifier(float saturation) {
            builder.saturationModifier(saturation);
            return this;
        }

        public FoodBuilder alwaysEdible() {
            builder.alwaysEdible();
            return this;
        }

        public FoodBuilder effect(MobEffectInstance effect, float probability) {
            effects.add(new ApplyStatusEffectsConsumeEffect(effect, probability));
            return this;
        }

        public FoodBuilder fast() {
            this.fast = true;
            return this;
        }

        public FoodProperties build() {
            FoodProperties food = builder.build();
            if (!effects.isEmpty() || fast) {
                Consumable.Builder consumable = Consumable.builder();
                if (fast) {
                    consumable.consumeSeconds(FAST_CONSUME_SECONDS);
                }
                for (ApplyStatusEffectsConsumeEffect effect : effects) {
                    consumable.onConsume(effect);
                }
                CONSUMABLES.put(food, consumable.build());
            }
            return food;
        }
    }

    // 番茄
    FoodProperties TOMATO = (builder())
            .nutrition(2).saturationModifier(0.5F)
            .alwaysEdible().build();

    // 辣椒
    FoodProperties CHILI = (builder())
            .nutrition(1).saturationModifier(0)
            .alwaysEdible().build();

    // 生菜
    FoodProperties LETTUCE = (builder())
            .nutrition(2).saturationModifier(0)
            .alwaysEdible().build();

    // 猪儿虫
    FoodProperties CATERPILLAR = (builder())
            .nutrition(18).saturationModifier(0.2F).alwaysEdible()
            .effect(new MobEffectInstance(NAUSEA, 200), 1F)
            .build();

    // 刺身
    FoodProperties SASHIMI = (builder())
            .nutrition(1).saturationModifier(0.5F)
            .alwaysEdible().build();

    // 生羊排
    FoodProperties RAW_LAMB_CHOPS = (builder())
            .nutrition(1).saturationModifier(0.5F)
            .alwaysEdible().build();

    // 生切制小肉
    FoodProperties RAW_CUT_SMALL_MEATS = (builder())
            .nutrition(2).saturationModifier(0.3F)
            .alwaysEdible().build();

    // 生牛杂
    FoodProperties RAW_COW_OFFAL = (builder())
            .nutrition(2).saturationModifier(0.3F)
            .alwaysEdible().build();

    // 生五花肉
    FoodProperties RAW_PORK_BELLY = (builder())
            .nutrition(2).saturationModifier(0.3F)
            .alwaysEdible().build();

    // 生驴肉
    FoodProperties RAW_DONKEY_MEAT = (builder())
            .nutrition(2).saturationModifier(0.3F)
            .alwaysEdible().build();

    // 生丸子
    FoodProperties RAW_MEATBALL = (builder())
            .nutrition(4).saturationModifier(0.3F)
            .alwaysEdible().build();

    // 熟羊排
    FoodProperties COOKED_LAMB_CHOPS = (builder())
            .nutrition(3).saturationModifier(0.8F)
            .alwaysEdible().build();

    // 熟切制小肉
    FoodProperties COOKED_CUT_SMALL_MEATS = (builder())
            .nutrition(4).saturationModifier(0.8F)
            .alwaysEdible().build();

    // 熟牛杂
    FoodProperties COOKED_COW_OFFAL = (builder())
            .nutrition(4).saturationModifier(0.8F)
            .alwaysEdible().build();

    // 熟五花肉
    FoodProperties COOKED_PORK_BELLY = (builder())
            .nutrition(4).saturationModifier(0.8F)
            .alwaysEdible().build();

    // 熟驴肉
    FoodProperties COOKED_DONKEY_MEAT = (builder())
            .nutrition(6).saturationModifier(0.8F)
            .alwaysEdible().build();

    // 熟丸子
    FoodProperties COOKED_MEATBALL = (builder())
            .nutrition(8).saturationModifier(0.8F)
            .alwaysEdible().build();

    // 驴肉火烧
    FoodProperties DONKEY_BURGER = (builder())
            .nutrition(12).saturationModifier(0.8F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 45 * 20), 1.0F)
            .alwaysEdible().build();

    // 包子
    FoodProperties BAOZI = (builder())
            .nutrition(8).saturationModifier(1)
            .effect(new MobEffectInstance(ABSORPTION, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 饺子
    FoodProperties DUMPLING = (builder())
            .nutrition(8).saturationModifier(1)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 烤包子
    FoodProperties SAMSA = (builder())
            .nutrition(8).saturationModifier(1)
            .effect(new MobEffectInstance(HASTE, 100 * 20, 1), 1.0F)
            .alwaysEdible().build();

    // 馒头
    FoodProperties MANTOU = (builder())
            .nutrition(6).saturationModifier(0.9F)
            .fast().alwaysEdible().build();

    // 馅饼
    FoodProperties MEAT_PIE = (builder())
            .nutrition(8).saturationModifier(1)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 牛肉面
    FoodProperties BEEF_NOODLE = (builder())
            .nutrition(14).saturationModifier(0.643f)
            .effect(new MobEffectInstance(WARMTH, 3 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 烩面
    FoodProperties HUI_NOODLE = (builder())
            .nutrition(14).saturationModifier(0.643f)
            .effect(new MobEffectInstance(WARMTH, 3 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 乌冬面
    FoodProperties UDON_NOODLE = (builder())
            .nutrition(14).saturationModifier(0.643f)
            .effect(new MobEffectInstance(WARMTH, 3 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 煎蛋
    FoodProperties FRIED_EGG = (builder())
            .nutrition(4).saturationModifier(0.5F)
            .alwaysEdible().build();

    // 黑暗料理
    FoodProperties DARK_CUISINE_BLOCK = (builder())
            .nutrition(1).saturationModifier(0).alwaysEdible()
            .effect(new MobEffectInstance(BLINDNESS, 300), 0.33F)
            .effect(new MobEffectInstance(POISON, 100), 0.33F)
            .build();

    FoodProperties DARK_CUISINE_ITEM = (builder())
            .nutrition(2).saturationModifier(0).alwaysEdible()
            .effect(new MobEffectInstance(BLINDNESS, 300), 0.33F)
            .effect(new MobEffectInstance(POISON, 100), 0.33F)
            .build();

    // 迷之炒菜
    FoodProperties SUSPICIOUS_STIR_FRY_BLOCK = (builder())
            .nutrition(4).saturationModifier(0.4F).alwaysEdible()
            .effect(new MobEffectInstance(SPEED, 1200), 0.15F)
            .effect(new MobEffectInstance(JUMP_BOOST, 1200), 0.15F)
            .effect(new MobEffectInstance(HASTE, 1200), 0.15F)
            .effect(new MobEffectInstance(LUCK, 1200), 0.15F)
            .effect(new MobEffectInstance(MINING_FATIGUE, 1200), 0.15F)
            .effect(new MobEffectInstance(NAUSEA, 400), 0.15F)
            .build();

    FoodProperties SUSPICIOUS_STIR_FRY_ITEM = (builder())
            .nutrition(4).saturationModifier(0.4F).alwaysEdible()
            .effect(new MobEffectInstance(SPEED, 1200), 0.15F)
            .effect(new MobEffectInstance(JUMP_BOOST, 1200), 0.15F)
            .effect(new MobEffectInstance(HASTE, 1200), 0.15F)
            .effect(new MobEffectInstance(LUCK, 1200), 0.15F)
            .effect(new MobEffectInstance(MINING_FATIGUE, 1200), 0.15F)
            .effect(new MobEffectInstance(NAUSEA, 400), 0.15F)
            .build();

    // 粘液饭
    FoodProperties SLIME_BALL_MEAL_BLOCK = (builder())
            .nutrition(1).saturationModifier(0)
            .effect(new MobEffectInstance(JUMP_BOOST, 1000), 1)
            .alwaysEdible().build();

    FoodProperties SLIME_BALL_MEAL_ITEM = (builder())
            .nutrition(4).saturationModifier(0)
            .effect(new MobEffectInstance(JUMP_BOOST, 1000), 1)
            .alwaysEdible().build();

    // 翻糖派
    FoodProperties FONDANT_PIE_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.5F)
            .effect(new MobEffectInstance(LUCK, 2800), 1.0F)
            .effect(new MobEffectInstance(REGENERATION, 200), 1.0F)
            .alwaysEdible().build();

    FoodProperties FONDANT_PIE_ITEM = (builder())
            .nutrition(9).saturationModifier(0.5F)
            .effect(new MobEffectInstance(LUCK, 2800), 1.0F)
            .effect(new MobEffectInstance(REGENERATION, 200), 1.0F)
            .alwaysEdible().build();

    // 东坡肉
    FoodProperties DONGPO_PORK_BLOCK = (builder())
            .nutrition(5).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .alwaysEdible().build();

    FoodProperties DONGPO_PORK_ITEM = (builder())
            .nutrition(15).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .alwaysEdible().build();

    // 翻糖蛛眼
    FoodProperties FONDANT_SPIDER_EYE_BLOCK = (builder())
            .nutrition(1).saturationModifier(0.5F)
            .effect(new MobEffectInstance(SULFUR, 1000), 1.0F)
            .alwaysEdible().build();

    FoodProperties FONDANT_SPIDER_EYE_ITEM = (builder())
            .nutrition(6).saturationModifier(0.5F)
            .effect(new MobEffectInstance(SULFUR, 1000), 1.0F)
            .alwaysEdible().build();

    // 荷包紫颂烧
    FoodProperties CHORUS_FRIED_EGG_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.7F)
            .effect(new MobEffectInstance(FLATULENCE, 400), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    FoodProperties CHORUS_FRIED_EGG_ITEM = (builder())
            .nutrition(9).saturationModifier(0.7F)
            .effect(new MobEffectInstance(FLATULENCE, 400), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 红烧鱼
    FoodProperties BRAISED_FISH_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.8F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WATER_BREATHING, 3600), 1.0F)
            .alwaysEdible().build();

    FoodProperties BRAISED_FISH_ITEM = (builder())
            .nutrition(8).saturationModifier(0.8F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WATER_BREATHING, 3600), 1.0F)
            .alwaysEdible().build();

    // 黄金沙拉
    FoodProperties GOLDEN_SALAD_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.6F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .effect(new MobEffectInstance(REGENERATION, 200), 1.0F)
            .alwaysEdible().build();

    FoodProperties GOLDEN_SALAD_ITEM = (builder())
            .nutrition(12).saturationModifier(0.7F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .effect(new MobEffectInstance(REGENERATION, 200), 1.0F)
            .alwaysEdible().build();

    // 辣子鸡
    FoodProperties SPICY_CHICKEN_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.8F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 1600), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    FoodProperties SPICY_CHICKEN_ITEM = (builder())
            .nutrition(8).saturationModifier(0.8F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 1600), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 烧鸟串
    FoodProperties YAKITORI_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.6F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .alwaysEdible().build();

    FoodProperties YAKITORI_ITEM = (builder())
            .nutrition(10).saturationModifier(0.6F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .alwaysEdible().build();

    // 水晶羊排
    FoodProperties CRYSTAL_LAMB_CHOP_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.4F)
            .effect(new MobEffectInstance(HASTE, 1200), 1.0F)
            .alwaysEdible().build();

    FoodProperties CRYSTAL_LAMB_CHOP_ITEM = (builder())
            .nutrition(10).saturationModifier(0.4F)
            .effect(new MobEffectInstance(HASTE, 1200), 1.0F)
            .alwaysEdible().build();

    // 下界风味刺身
    FoodProperties NETHER_STYLE_SASHIMI_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.3F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 1600), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    FoodProperties NETHER_STYLE_SASHIMI_ITEM = (builder())
            .nutrition(12).saturationModifier(0.4F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 1600), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 香煎骑士牛排
    FoodProperties PAN_SEARED_KNIGHT_STEAK_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .alwaysEdible().build();

    FoodProperties PAN_SEARED_KNIGHT_STEAK_ITEM = (builder())
            .nutrition(10).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .alwaysEdible().build();

    // 仰望星空派
    FoodProperties STARGAZY_PIE_BLOCK = (builder())
            .nutrition(1).saturationModifier(0.7F)
            .effect(new MobEffectInstance(FLATULENCE, 600), 1.0F)
            .effect(new MobEffectInstance(UNLUCK, 3600), 1.0F)
            .alwaysEdible().build();

    FoodProperties STARGAZY_PIE_ITEM = (builder())
            .nutrition(6).saturationModifier(0.7F)
            .effect(new MobEffectInstance(FLATULENCE, 600), 1.0F)
            .effect(new MobEffectInstance(UNLUCK, 3600), 1.0F)
            .alwaysEdible().build();

    // 珍珠咕噜肉
    FoodProperties SWEET_AND_SOUR_ENDER_PEARLS_BLOCK = (builder())
            .nutrition(1).saturationModifier(0.5F)
            .effect(new MobEffectInstance(FLATULENCE, 400), 1.0F)
            .effect(new MobEffectInstance(SLOW_FALLING, 600), 1.0F)
            .alwaysEdible().build();

    FoodProperties SWEET_AND_SOUR_ENDER_PEARLS_ITEM = (builder())
            .nutrition(3).saturationModifier(0.5F)
            .effect(new MobEffectInstance(FLATULENCE, 400), 1.0F)
            .effect(new MobEffectInstance(SLOW_FALLING, 600), 1.0F)
            .alwaysEdible().build();

    // 烈焰羊排
    FoodProperties BLAZE_LAMB_CHOP_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.4F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 1600), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    FoodProperties BLAZE_LAMB_CHOP_ITEM = (builder())
            .nutrition(10).saturationModifier(0.4F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 1600), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 凛冬羊排
    FoodProperties FROST_LAMB_CHOP_BLOCK = (builder())
            .nutrition(2).saturationModifier(0.4F)
            .effect(new MobEffectInstance(TUNDRA_STRIDER, 900), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    FoodProperties FROST_LAMB_CHOP_ITEM = (builder())
            .nutrition(10).saturationModifier(0.4F)
            .effect(new MobEffectInstance(TUNDRA_STRIDER, 900), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 末地风味刺身
    FoodProperties END_STYLE_SASHIMI_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.3F)
            .effect(new MobEffectInstance(SLOW_FALLING, 600), 1.0F)
            .effect(new MobEffectInstance(ABSORPTION, 800), 1.0F)
            .alwaysEdible().build();

    FoodProperties END_STYLE_SASHIMI_ITEM = (builder())
            .nutrition(12).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SLOW_FALLING, 600), 1.0F)
            .effect(new MobEffectInstance(ABSORPTION, 800), 1.0F)
            .alwaysEdible().build();

    // 沙漠风味刺身
    FoodProperties DESERT_STYLE_SASHIMI_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.3F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .effect(new MobEffectInstance(MUSTARD, 1600), 1.0F)
            .alwaysEdible().build();

    FoodProperties DESERT_STYLE_SASHIMI_ITEM = (builder())
            .nutrition(12).saturationModifier(0.4F)
            .effect(new MobEffectInstance(WARMTH, 800), 1.0F)
            .effect(new MobEffectInstance(MUSTARD, 1600), 1.0F)
            .alwaysEdible().build();

    // 苔原风味刺身
    FoodProperties TUNDRA_STYLE_SASHIMI_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.3F)
            .effect(new MobEffectInstance(VIGOR, 900), 1.0F)
            .effect(new MobEffectInstance(PRESERVATION, 2400), 1.0F)
            .alwaysEdible().build();

    FoodProperties TUNDRA_STYLE_SASHIMI_ITEM = (builder())
            .nutrition(12).saturationModifier(0.4F)
            .effect(new MobEffectInstance(VIGOR, 900), 1.0F)
            .effect(new MobEffectInstance(PRESERVATION, 2400), 1.0F)
            .alwaysEdible().build();

    // 寒带风味刺身
    FoodProperties COLD_STYLE_SASHIMI_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.3F)
            .effect(new MobEffectInstance(TUNDRA_STRIDER, 900), 1.0F)
            .effect(new MobEffectInstance(VIGOR, 900), 1.0F)
            .alwaysEdible().build();

    FoodProperties COLD_STYLE_SASHIMI_ITEM = (builder())
            .nutrition(12).saturationModifier(0.4F)
            .effect(new MobEffectInstance(TUNDRA_STRIDER, 900), 1.0F)
            .effect(new MobEffectInstance(VIGOR, 900), 1.0F)
            .alwaysEdible().build();

    // 水煎包
    FoodProperties SHENGJIAN_MANTOU_ITEM = (builder())
            .nutrition(8).saturationModifier(1)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties SHENGJIAN_MANTOU_BLOCK = (builder())
            .nutrition(2).saturationModifier(1)
            .effect(new MobEffectInstance(WARMTH, 6000), 1.0F)
            .alwaysEdible().build();

    // 拔丝土豆
    FoodProperties CANDIED_POTATO_BLOCK = (builder())
            .nutrition(5).saturationModifier(0.55F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties CANDIED_POTATO_ITEM = (builder())
            .nutrition(20).saturationModifier(0.55F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 疙瘩汤
    FoodProperties DOUGH_DROP_SOUP_BLOCK = (builder())
            .nutrition(2).saturationModifier(1)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties DOUGH_DROP_SOUP_ITEM = (builder())
            .nutrition(8).saturationModifier(1)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 虎皮青椒酿肉
    FoodProperties STUFFED_TIGER_SKIN_PEPPER_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties STUFFED_TIGER_SKIN_PEPPER_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 麻辣兔头
    FoodProperties SPICY_RABBIT_HEAD_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties SPICY_RABBIT_HEAD_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 四喜丸子汤
    FoodProperties FOUR_JOY_MEATBALL_SOUP_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties FOUR_JOY_MEATBALL_SOUP_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 椒麻鸡
    FoodProperties NUMBING_SPICY_CHICKEN_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties NUMBING_SPICY_CHICKEN_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 油炸猪儿虫
    FoodProperties FRIED_CATERPILLAR_BLOCK = (builder())
            .nutrition(4).saturationModifier(0.8F)
            .effect(new MobEffectInstance(FLATULENCE, 10 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties FRIED_CATERPILLAR_ITEM = (builder())
            .nutrition(12).saturationModifier(0.8F)
            .effect(new MobEffectInstance(FLATULENCE, 10 * 20), 1.0F)
            .alwaysEdible().build();

    // 炸春卷
    FoodProperties FRIED_SPRING_ROLL_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties FRIED_SPRING_ROLL_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 毛血旺
    FoodProperties SPICY_BLOOD_STEW_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties SPICY_BLOOD_STEW_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 水果拼盘
    FoodProperties FRUIT_PLATTER_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .alwaysEdible().build();

    FoodProperties FRUIT_PLATTER_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .alwaysEdible().build();

    // 冷肉炙
    FoodProperties COLD_ROASTED_MEAT_BLOCK = (builder())
            .nutrition(4).saturationModifier(0.8F)
            .alwaysEdible().build();

    FoodProperties COLD_ROASTED_MEAT_ITEM = (builder())
            .nutrition(16).saturationModifier(0.8F)
            .alwaysEdible().build();

    // 红烧排骨
    FoodProperties BRAISED_PORK_RIBS_BLOCK = (builder())
            .nutrition(4).saturationModifier(0.8F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties BRAISED_PORK_RIBS_ITEM = (builder())
            .nutrition(16).saturationModifier(0.8F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 油泼鱼
    FoodProperties OIL_SPLASHED_FISH_BLOCK = (builder())
            .nutrition(3).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties OIL_SPLASHED_FISH_ITEM = (builder())
            .nutrition(13).saturationModifier(0.615F)
            .effect(new MobEffectInstance(WARMTH, 80 * 20), 1.0F)
            .alwaysEdible().build();

    // 棕色蘑菇瓦罐汤
    FoodProperties BROWN_MUSHROOM_POT_SOUP_BLOCK = (builder())
            .nutrition(6).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties BROWN_MUSHROOM_POT_SOUP_ITEM = (builder())
            .nutrition(18).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 红色蘑菇瓦罐汤
    FoodProperties RED_MUSHROOM_POT_SOUP_BLOCK = (builder())
            .nutrition(6).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties RED_MUSHROOM_POT_SOUP_ITEM = (builder())
            .nutrition(18).saturationModifier(0.667F)
            .effect(new MobEffectInstance(WARMTH, 5 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 诡异菌瓦罐汤
    FoodProperties WARPED_FUNGUS_POT_SOUP_BLOCK = (builder())
            .nutrition(6).saturationModifier(0.667F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties WARPED_FUNGUS_POT_SOUP_ITEM = (builder())
            .nutrition(18).saturationModifier(0.667F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 绯红菌瓦罐汤
    FoodProperties CRIMSON_FUNGUS_POT_SOUP_BLOCK = (builder())
            .nutrition(6).saturationModifier(0.667F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties CRIMSON_FUNGUS_POT_SOUP_ITEM = (builder())
            .nutrition(18).saturationModifier(0.667F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 2 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 佛跳墙
    FoodProperties BUDDHA_JUMPS_OVER_THE_WALL_BLOCK = (builder())
            .nutrition(7).saturationModifier(0.521F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 3 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    FoodProperties BUDDHA_JUMPS_OVER_THE_WALL_ITEM = (builder())
            .nutrition(20).saturationModifier(0.55F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 3 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 番茄炒蛋
    FoodProperties SCRAMBLE_EGG_WITH_TOMATOES = (builder())
            .nutrition(6).saturationModifier(0.3F)
            .effect(new MobEffectInstance(VIGOR, 1400), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 900), 1.0F)
            .alwaysEdible().build();

    // 爆炒牛杂
    FoodProperties STIR_FRIED_BEEF_OFFAL = (builder())
            .nutrition(7).saturationModifier(0.3F)
            .effect(new MobEffectInstance(FLATULENCE, 600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 1200), 1.0F)
            .alwaysEdible().build();

    // 红烧牛肉
    FoodProperties BRAISED_BEEF = (builder())
            .nutrition(8).saturationModifier(0.3F)
            .effect(new MobEffectInstance(FLATULENCE, 1000), 1.0F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .alwaysEdible().build();

    // 青椒炒肉
    FoodProperties STIR_FRIED_PORK_WITH_PEPPERS = (builder())
            .nutrition(7).saturationModifier(0.4F)
            .effect(new MobEffectInstance(MUSTARD, 2400), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 糖醋里脊
    FoodProperties SWEET_AND_SOUR_PORK = (builder())
            .nutrition(6).saturationModifier(0.3F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(LUCK, 4200), 1.0F)
            .alwaysEdible().build();

    // 鱼香肉丝
    FoodProperties FISH_FLAVORED_SHREDDED_PORK = (builder())
            .nutrition(7).saturationModifier(0.2F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 2400), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 900), 1.0F)
            .alwaysEdible().build();

    // 田园杂蔬
    FoodProperties COUNTRY_STYLE_MIXED_VEGETABLES = (builder())
            .nutrition(4).saturationModifier(0.4F)
            .effect(new MobEffectInstance(PRESERVATION, 4400), 1.0F)
            .effect(new MobEffectInstance(VIGOR, 900), 1.0F)
            .alwaysEdible().build();

    // 米饭
    FoodProperties COOKED_RICE = (builder())
            .nutrition(6).saturationModifier(0.3F)
            .alwaysEdible().build();

    // 蛋炒饭
    FoodProperties EGG_FRIED_RICE = (builder())
            .nutrition(9).saturationModifier(0.3F)
            .alwaysEdible().build();

    // 美味蛋炒饭
    FoodProperties DELICIOUS_EGG_FRIED_RICE = (builder())
            .nutrition(12).saturationModifier(0.4F)
            .effect(new MobEffectInstance(WARMTH, 900), 1.0F)
            .alwaysEdible().build();

    // 谜之炒菜盖饭
    FoodProperties SUSPICIOUS_STIR_FRY_RICE_BOWL = (builder())
            .nutrition(8).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SPEED, 1200), 0.15F)
            .effect(new MobEffectInstance(JUMP_BOOST, 1200), 0.15F)
            .effect(new MobEffectInstance(HASTE, 1200), 0.15F)
            .effect(new MobEffectInstance(LUCK, 1200), 0.15F)
            .effect(new MobEffectInstance(MINING_FATIGUE, 1200), 0.15F)
            .effect(new MobEffectInstance(NAUSEA, 400), 0.15F)
            .alwaysEdible().build();

    // 番茄炒蛋盖饭
    FoodProperties SCRAMBLE_EGG_WITH_TOMATOES_RICE_BOWL = (builder())
            .nutrition(12).saturationModifier(0.5F)
            .effect(new MobEffectInstance(VIGOR, 1400), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 900), 1.0F)
            .alwaysEdible().build();

    // 爆炒牛杂盖饭
    FoodProperties STIR_FRIED_BEEF_OFFAL_RICE_BOWL = (builder())
            .nutrition(14).saturationModifier(0.4F)
            .effect(new MobEffectInstance(FLATULENCE, 600), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 1200), 1.0F)
            .alwaysEdible().build();

    // 红烧牛肉盖饭
    FoodProperties BRAISED_BEEF_RICE_BOWL = (builder())
            .nutrition(16).saturationModifier(0.4F)
            .effect(new MobEffectInstance(FLATULENCE, 1000), 1.0F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .alwaysEdible().build();

    // 青椒炒肉盖饭
    FoodProperties STIR_FRIED_PORK_WITH_PEPPERS_RICE_BOWL = (builder())
            .nutrition(15).saturationModifier(0.4F)
            .effect(new MobEffectInstance(MUSTARD, 2400), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 糖醋里脊盖饭
    FoodProperties SWEET_AND_SOUR_PORK_RICE_BOWL = (builder())
            .nutrition(14).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .effect(new MobEffectInstance(LUCK, 4200), 1.0F)
            .alwaysEdible().build();

    // 鱼香肉丝盖饭
    FoodProperties FISH_FLAVORED_SHREDDED_PORK_RICE_BOWL = (builder())
            .nutrition(14).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 2400), 1.0F)
            .effect(new MobEffectInstance(WARMTH, 900), 1.0F)
            .alwaysEdible().build();

    // 红烧鱼盖饭
    FoodProperties BRAISED_FISH_RICE_BOWL = (builder())
            .nutrition(14).saturationModifier(0.4F)
            .effect(new MobEffectInstance(WATER_BREATHING, 3600), 1.0F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 1600), 1.0F)
            .alwaysEdible().build();

    // 辣子鸡盖饭
    FoodProperties SPICY_CHICKEN_RICE_BOWL = (builder())
            .nutrition(14).saturationModifier(0.4F)
            .effect(new MobEffectInstance(FIRE_RESISTANCE, 1600), 1.0F)
            .effect(new MobEffectInstance(RESISTANCE, 2000), 1.0F)
            .alwaysEdible().build();

    // 大骨汤
    FoodProperties PORK_BONE_SOUP = (builder())
            .nutrition(2).saturationModifier(0.5F)
            .effect(new MobEffectInstance(VIGOR, 3600), 1.0F)
            .alwaysEdible().build();

    // 海鲜味噌汤
    FoodProperties SEAFOOD_MISO_SOUP = (builder())
            .nutrition(4).saturationModifier(0.3F)
            .effect(new MobEffectInstance(WATER_BREATHING, 3600), 1.0F)
            .effect(new MobEffectInstance(DOLPHINS_GRACE, 800), 1.0F)
            .alwaysEdible().build();

    // 恐惧浓汤
    FoodProperties FEARSOME_THICK_SOUP = (builder())
            .nutrition(2).saturationModifier(0.5F)
            .effect(new MobEffectInstance(SULFUR, 9600), 1.0F)
            .effect(new MobEffectInstance(MUSTARD, 1600), 1.0F)
            .alwaysEdible().build();

    // 萝卜羊肉汤
    FoodProperties LAMB_AND_RADISH_SOUP = (builder())
            .nutrition(4).saturationModifier(0.4F)
            .effect(new MobEffectInstance(TUNDRA_STRIDER, 3200), 1.0F)
            .alwaysEdible().build();

    // 土豆炖牛肉
    FoodProperties BRAISED_BEEF_WITH_POTATOES = (builder())
            .nutrition(5).saturationModifier(0.4F)
            .effect(new MobEffectInstance(WARMTH, 5400), 1.0F)
            .alwaysEdible().build();

    // 野菌兔肉汤
    FoodProperties WILD_MUSHROOM_RABBIT_SOUP = (builder())
            .nutrition(4).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SPEED, 600), 1.0F)
            .alwaysEdible().build();

    // 番茄牛腩汤
    FoodProperties TOMATO_BEEF_BRISKET_SOUP = (builder())
            .nutrition(5).saturationModifier(0.4F)
            .effect(new MobEffectInstance(SATIATED_SHIELD, 3600), 1.0F)
            .alwaysEdible().build();

    // 河豚汤
    FoodProperties PUFFERFISH_SOUP = (builder())
            .nutrition(2).saturationModifier(0.8F)
            .effect(new MobEffectInstance(MUSTARD, 3600), 1.0F)
            .effect(new MobEffectInstance(POISON, 300), 0.3F)
            .alwaysEdible().build();

    // 罗宋汤
    FoodProperties BORSCHT = (builder())
            .nutrition(4).saturationModifier(0.4F)
            .effect(new MobEffectInstance(FLATULENCE, 3000), 1.0F)
            .alwaysEdible().build();

    // 牛丸汤
    FoodProperties BEEF_MEATBALL_SOUP = (builder())
            .nutrition(5).saturationModifier(0.4F)
            .effect(new MobEffectInstance(PRESERVATION, 3600), 1.0F)
            .alwaysEdible().build();

    // 小鸡炖蘑菇
    FoodProperties CHICKEN_AND_MUSHROOM_STEW = (builder())
            .nutrition(4).saturationModifier(0.4F)
            .effect(new MobEffectInstance(WARMTH, 5400), 1.0F)
            .alwaysEdible().build();

    // 驴肉汤
    FoodProperties DONKEY_SOUP = (builder())
            .nutrition(6).saturationModifier(0.667f)
            .effect(new MobEffectInstance(WARMTH, 8 * 60 * 20), 1.0F)
            .alwaysEdible().build();

    // 冷切火腿片（仅方块形态可食）
    FoodProperties COLD_CUT_HAM_SLICES_BLOCK = (builder())
            .nutrition(4).saturationModifier(0.8F)
            .alwaysEdible().build();
}
