package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public final class TeacupRegistry {
    private static final Map<Identifier, TeacupData> TEACUP_DATA_MAP = new LinkedHashMap<>();

    public static final Identifier BARLEY_TEA = registerTeacupData("barley_tea", TeacupData.create(4)
            .addEffect(() -> new MobEffectInstance(ModEffects.VITALITY, 8 * 60 * 20)));
    public static final Identifier TIEGUANYIN = registerTeacupData("tieguanyin", TeacupData.create(4)
            .addEffect(() -> new MobEffectInstance(ModEffects.INSTANT_SMELTING, 2 * 60 * 20)));
    public static final Identifier BILUOCHUN = registerTeacupData("biluochun", TeacupData.create(4)
            .addEffect(() -> new MobEffectInstance(ModEffects.PROJECTILE_DODGE, 2 * 60 * 20)));
    public static final Identifier OOLONG = registerTeacupData("oolong", TeacupData.create(4)
            .addEffect(() -> new MobEffectInstance(MobEffects.SLOW_FALLING, 6 * 60 * 20))
            .addEffect(() -> new MobEffectInstance(MobEffects.JUMP_BOOST, 6 * 60 * 20)));
    public static final Identifier SAKURA_FUBUKI = registerTeacupData("sakura_fubuki", TeacupData.create(4)
            .addEffect(() -> new MobEffectInstance(ModEffects.HINDER, 6 * 60 * 20)));
    public static final Identifier FLOWER_TEA = registerTeacupData("flower_tea", TeacupData.create(4)
            .addEffect(() -> new MobEffectInstance(MobEffects.REGENERATION, 20 * 20)));

    private TeacupRegistry() {
    }

    public static void init() {
    }

    public static List<Identifier> ids() {
        return List.copyOf(TEACUP_DATA_MAP.keySet());
    }

    public static void forEachData(BiConsumer<Identifier, TeacupData> consumer) {
        TEACUP_DATA_MAP.forEach(consumer);
    }

    private static Identifier registerTeacupData(String name, TeacupData data) {
        Identifier id = Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name);
        TEACUP_DATA_MAP.put(id, data);
        return id;
    }

    public static Item getItem(Identifier id) {
        return BuiltInRegistries.ITEM.getOptional(id).orElse(Items.AIR);
    }

    public static Block getBlock(Identifier id) {
        return BuiltInRegistries.BLOCK.getOptional(id).orElse(Blocks.AIR);
    }

    public static final class TeacupData {
        private final int maxCount;
        private final List<Pair<Supplier<MobEffectInstance>, Float>> effects = new ArrayList<>();
        private @Nullable VoxelShape aabb;

        private TeacupData(int maxCount) {
            this.maxCount = maxCount;
        }

        public static TeacupData create(int maxCount) {
            return new TeacupData(maxCount);
        }

        public TeacupData addEffect(Supplier<MobEffectInstance> effect) {
            return this.addEffect(effect, 1.0F);
        }

        public TeacupData addEffect(Supplier<MobEffectInstance> effect, float probability) {
            this.effects.add(Pair.of(effect, probability));
            return this;
        }

        public TeacupData setAABB(VoxelShape aabb) {
            this.aabb = aabb;
            return this;
        }

        public int getMaxCount() {
            return maxCount;
        }

        public List<Pair<Supplier<MobEffectInstance>, Float>> getEffects() {
            return List.copyOf(effects);
        }

        public @Nullable VoxelShape getAABB() {
            return aabb;
        }
    }
}
