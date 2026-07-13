package com.github.ysbbbbbb.kaleidoscopecookery.init.registry;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import static com.github.ysbbbbbb.kaleidoscopecookery.init.ModFoods.*;

public final class FoodBiteRegistry {
    private static final Map<Identifier, FoodData> FOOD_DATA_MAP = new LinkedHashMap<>();

    public static final Identifier DARK_CUISINE;
    public static final Identifier SUSPICIOUS_STIR_FRY;
    public static final Identifier SLIME_BALL_MEAL;
    public static final Identifier FONDANT_PIE;
    public static final Identifier DONGPO_PORK;
    public static final Identifier FONDANT_SPIDER_EYE;
    public static final Identifier CHORUS_FRIED_EGG;
    public static final Identifier BRAISED_FISH;
    public static final Identifier GOLDEN_SALAD;
    public static final Identifier SPICY_CHICKEN;
    public static final Identifier YAKITORI;
    public static final Identifier PAN_SEARED_KNIGHT_STEAK;
    public static final Identifier STARGAZY_PIE;
    public static final Identifier SWEET_AND_SOUR_ENDER_PEARLS;
    public static final Identifier CRYSTAL_LAMB_CHOP;
    public static final Identifier BLAZE_LAMB_CHOP;
    public static final Identifier FROST_LAMB_CHOP;
    public static final Identifier NETHER_STYLE_SASHIMI;
    public static final Identifier END_STYLE_SASHIMI;
    public static final Identifier DESERT_STYLE_SASHIMI;
    public static final Identifier TUNDRA_STYLE_SASHIMI;
    public static final Identifier COLD_STYLE_SASHIMI;
    public static final Identifier SHENGJIAN_MANTOU;
    public static final Identifier CANDIED_POTATO;
    public static final Identifier DOUGH_DROP_SOUP;
    public static final Identifier STUFFED_TIGER_SKIN_PEPPER;
    public static final Identifier SPICY_RABBIT_HEAD;
    public static final Identifier FOUR_JOY_MEATBALL_SOUP;
    public static final Identifier NUMBING_SPICY_CHICKEN;
    public static final Identifier FRIED_CATERPILLAR;
    public static final Identifier FRIED_SPRING_ROLL;
    public static final Identifier SPICY_BLOOD_STEW;
    public static final Identifier FRUIT_PLATTER;
    public static final Identifier BRAISED_PORK_RIBS;
    public static final Identifier COLD_ROASTED_MEAT;
    public static final Identifier OIL_SPLASHED_FISH;
    public static final Identifier BROWN_MUSHROOM_POT_SOUP;
    public static final Identifier RED_MUSHROOM_POT_SOUP;
    public static final Identifier WARPED_FUNGUS_POT_SOUP;
    public static final Identifier CRIMSON_FUNGUS_POT_SOUP;
    public static final Identifier BUDDHA_JUMPS_OVER_THE_WALL;

    private FoodBiteRegistry() {
    }

    static {
        DARK_CUISINE = registerFoodData("dark_cuisine", FoodData
                .create(3, DARK_CUISINE_BLOCK, DARK_CUISINE_ITEM)
                .setAnimateTick(FoodBiteAnimateTicks.DARK_CUISINE_ANIMATE_TICK));

        SUSPICIOUS_STIR_FRY = registerFoodData("suspicious_stir_fry", FoodData
                .create(1, SUSPICIOUS_STIR_FRY_BLOCK, SUSPICIOUS_STIR_FRY_ITEM)
                .setAnimateTick(FoodBiteAnimateTicks.SUSPICIOUS_STIR_FRY_ANIMATE_TICK));

        SLIME_BALL_MEAL = registerFoodData("slime_ball_meal", FoodData
                .create(3, SLIME_BALL_MEAL_BLOCK, SLIME_BALL_MEAL_ITEM));

        FONDANT_PIE = registerFoodData("fondant_pie", FoodData
                .create(4, FONDANT_PIE_BLOCK, FONDANT_PIE_ITEM));

        DONGPO_PORK = registerFoodData("dongpo_pork", FoodData
                .create(3, DONGPO_PORK_BLOCK, DONGPO_PORK_ITEM)
                .addLootItems(Items.BAMBOO));

        FONDANT_SPIDER_EYE = registerFoodData("fondant_spider_eye", FoodData
                .create(4, FONDANT_SPIDER_EYE_BLOCK, FONDANT_SPIDER_EYE_ITEM));

        CHORUS_FRIED_EGG = registerFoodData("chorus_fried_egg", FoodData
                .create(3, CHORUS_FRIED_EGG_BLOCK, CHORUS_FRIED_EGG_ITEM));

        BRAISED_FISH = registerFoodData("braised_fish", FoodData
                .create(4, BRAISED_FISH_BLOCK, BRAISED_FISH_ITEM)
                .addLootItems(Items.BONE, Items.BONE_MEAL));

        GOLDEN_SALAD = registerFoodData("golden_salad", FoodData
                .create(6, GOLDEN_SALAD_BLOCK, GOLDEN_SALAD_ITEM));

        SPICY_CHICKEN = registerFoodData("spicy_chicken", FoodData
                .create(4, SPICY_CHICKEN_BLOCK, SPICY_CHICKEN_ITEM));

        YAKITORI = registerFoodData("yakitori", FoodData
                .create(4, YAKITORI_BLOCK, YAKITORI_ITEM));

        PAN_SEARED_KNIGHT_STEAK = registerFoodData("pan_seared_knight_steak", FoodData
                .create(4, PAN_SEARED_KNIGHT_STEAK_BLOCK, PAN_SEARED_KNIGHT_STEAK_ITEM)
                .addLootItems(Items.BONE, Items.BONE_MEAL));

        STARGAZY_PIE = registerFoodData("stargazy_pie", FoodData
                .create(4, STARGAZY_PIE_BLOCK, STARGAZY_PIE_ITEM));

        SWEET_AND_SOUR_ENDER_PEARLS = registerFoodData("sweet_and_sour_ender_pearls", FoodData
                .create(3, SWEET_AND_SOUR_ENDER_PEARLS_BLOCK, SWEET_AND_SOUR_ENDER_PEARLS_ITEM));

        CRYSTAL_LAMB_CHOP = registerFoodData("crystal_lamb_chop", FoodData
                .create(3, CRYSTAL_LAMB_CHOP_BLOCK, CRYSTAL_LAMB_CHOP_ITEM)
                .addLootItems(Items.AMETHYST_SHARD));

        BLAZE_LAMB_CHOP = registerFoodData("blaze_lamb_chop", FoodData
                .create(3, BLAZE_LAMB_CHOP_BLOCK, BLAZE_LAMB_CHOP_ITEM)
                .addLootItems(Items.BLAZE_ROD));

        FROST_LAMB_CHOP = registerFoodData("frost_lamb_chop", FoodData
                .create(3, FROST_LAMB_CHOP_BLOCK, FROST_LAMB_CHOP_ITEM)
                .addLootItems(Items.BLUE_ICE));

        NETHER_STYLE_SASHIMI = registerFoodData("nether_style_sashimi", FoodData
                .create(4, NETHER_STYLE_SASHIMI_BLOCK, NETHER_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.CRIMSON_FUNGUS, Items.WARPED_FUNGUS));

        END_STYLE_SASHIMI = registerFoodData("end_style_sashimi", FoodData
                .create(4, END_STYLE_SASHIMI_BLOCK, END_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.CHORUS_FRUIT));

        DESERT_STYLE_SASHIMI = registerFoodData("desert_style_sashimi", FoodData
                .create(4, DESERT_STYLE_SASHIMI_BLOCK, DESERT_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.CACTUS));

        TUNDRA_STYLE_SASHIMI = registerFoodData("tundra_style_sashimi", FoodData
                .create(4, TUNDRA_STYLE_SASHIMI_BLOCK, TUNDRA_STYLE_SASHIMI_ITEM));

        COLD_STYLE_SASHIMI = registerFoodData("cold_style_sashimi", FoodData
                .create(4, COLD_STYLE_SASHIMI_BLOCK, COLD_STYLE_SASHIMI_ITEM)
                .addLootItems(Items.SNOWBALL, Items.SNOWBALL));

        SHENGJIAN_MANTOU = registerFoodData("shengjian_mantou", FoodData
                .create(4, SHENGJIAN_MANTOU_BLOCK, SHENGJIAN_MANTOU_ITEM));

        CANDIED_POTATO = registerFoodData("candied_potato", FoodData
                .create(3, CANDIED_POTATO_BLOCK, CANDIED_POTATO_ITEM));

        DOUGH_DROP_SOUP = registerFoodData("dough_drop_soup", FoodData
                .create(3, DOUGH_DROP_SOUP_BLOCK, DOUGH_DROP_SOUP_ITEM)
                .bowlAABB());

        STUFFED_TIGER_SKIN_PEPPER = registerFoodData("stuffed_tiger_skin_pepper", FoodData
                .create(5, STUFFED_TIGER_SKIN_PEPPER_BLOCK, STUFFED_TIGER_SKIN_PEPPER_ITEM));

        SPICY_RABBIT_HEAD = registerFoodData("spicy_rabbit_head", FoodData
                .create(3, SPICY_RABBIT_HEAD_BLOCK, SPICY_RABBIT_HEAD_ITEM));

        FOUR_JOY_MEATBALL_SOUP = registerFoodData("four_joy_meatball_soup", FoodData
                .create(4, FOUR_JOY_MEATBALL_SOUP_BLOCK, FOUR_JOY_MEATBALL_SOUP_ITEM)
                .bowlAABB());

        NUMBING_SPICY_CHICKEN = registerFoodData("numbing_spicy_chicken", FoodData
                .create(3, NUMBING_SPICY_CHICKEN_BLOCK, NUMBING_SPICY_CHICKEN_ITEM)
                .bowlAABB());

        FRIED_CATERPILLAR = registerFoodData("fried_caterpillar", FoodData
                .create(3, FRIED_CATERPILLAR_BLOCK, FRIED_CATERPILLAR_ITEM)
                .setAABB(Block.box(1, 0, 3, 15, 4, 13)));

        FRIED_SPRING_ROLL = registerFoodData("fried_spring_roll", FoodData
                .create(3, FRIED_SPRING_ROLL_BLOCK, FRIED_SPRING_ROLL_ITEM));

        SPICY_BLOOD_STEW = registerFoodData("spicy_blood_stew", FoodData
                .create(3, SPICY_BLOOD_STEW_BLOCK, SPICY_BLOOD_STEW_ITEM)
                .bowlAABB());

        FRUIT_PLATTER = registerFoodData("fruit_platter", FoodData
                .create(4, FRUIT_PLATTER_BLOCK, FRUIT_PLATTER_ITEM));

        BRAISED_PORK_RIBS = registerFoodData("braised_pork_ribs", FoodData
                .createOneByTwo(4, BRAISED_PORK_RIBS_BLOCK, BRAISED_PORK_RIBS_ITEM)
                .addLootItems(Items.BONE));

        COLD_ROASTED_MEAT = registerFoodData("cold_roasted_meat", FoodData
                .createOneByTwo(3, COLD_ROASTED_MEAT_BLOCK, COLD_ROASTED_MEAT_ITEM));

        OIL_SPLASHED_FISH = registerFoodData("oil_splashed_fish", FoodData
                .createOneByTwo(5, OIL_SPLASHED_FISH_BLOCK, OIL_SPLASHED_FISH_ITEM)
                .addLootItems(Items.BONE_MEAL));

        BROWN_MUSHROOM_POT_SOUP = registerFoodData("brown_mushroom_pot_soup", FoodData
                .create(2, BROWN_MUSHROOM_POT_SOUP_BLOCK, BROWN_MUSHROOM_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        RED_MUSHROOM_POT_SOUP = registerFoodData("red_mushroom_pot_soup", FoodData
                .create(2, RED_MUSHROOM_POT_SOUP_BLOCK, RED_MUSHROOM_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        WARPED_FUNGUS_POT_SOUP = registerFoodData("warped_fungus_pot_soup", FoodData
                .create(2, WARPED_FUNGUS_POT_SOUP_BLOCK, WARPED_FUNGUS_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        CRIMSON_FUNGUS_POT_SOUP = registerFoodData("crimson_fungus_pot_soup", FoodData
                .create(2, CRIMSON_FUNGUS_POT_SOUP_BLOCK, CRIMSON_FUNGUS_POT_SOUP_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());

        BUDDHA_JUMPS_OVER_THE_WALL = registerFoodData("buddha_jumps_over_the_wall", FoodData
                .create(2, BUDDHA_JUMPS_OVER_THE_WALL_BLOCK, BUDDHA_JUMPS_OVER_THE_WALL_ITEM)
                .setLootItem(Items.FLOWER_POT)
                .soupPotAABB()
                .potSoupAnimateTick());
    }

    public static void init() {
    }

    public static List<Identifier> ids() {
        return List.copyOf(FOOD_DATA_MAP.keySet());
    }

    public static void forEachData(BiConsumer<Identifier, FoodData> consumer) {
        FOOD_DATA_MAP.forEach(consumer);
    }

    private static Identifier registerFoodData(Identifier foodName, FoodData data) {
        FOOD_DATA_MAP.put(foodName, data);
        return foodName;
    }

    private static Identifier registerFoodData(String foodName, FoodData data) {
        Identifier id = mcLoc(foodName);
        FOOD_DATA_MAP.put(id, data);
        return id;
    }

    public static Identifier mcLoc(String name) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, name);
    }

    public static Item getItem(Identifier name) {
        return BuiltInRegistries.ITEM.getOptional(name).orElse(Items.AIR);
    }

    public static Block getBlock(Identifier name) {
        return BuiltInRegistries.BLOCK.getOptional(name).orElse(Blocks.AIR);
    }

    public static final class FoodData {
        private final BlockType blockType;
        private final int maxBites;
        private final List<ItemLike> lootItems = new ArrayList<>();
        private final FoodProperties blockFood;
        private final FoodProperties itemFood;
        private @Nullable FoodBiteAnimateTicks.AnimateTick animateTick = null;
        private @Nullable VoxelShape aabb = null;

        private FoodData(BlockType blockType, int maxBites, FoodProperties blockFood, FoodProperties itemFood) {
            this.blockType = blockType;
            this.maxBites = maxBites;
            this.lootItems.add(Items.BOWL);
            this.blockFood = blockFood;
            this.itemFood = itemFood;
        }

        public static FoodData create(int maxBites, FoodProperties blockFood, FoodProperties itemFood) {
            return new FoodData(BlockType.SINGLE, maxBites, blockFood, itemFood);
        }

        public static FoodData createOneByTwo(int maxBites, FoodProperties blockFood, FoodProperties itemFood) {
            return new FoodData(BlockType.ONE_BY_TWO, maxBites, blockFood, itemFood);
        }

        public FoodData setAnimateTick(FoodBiteAnimateTicks.AnimateTick animateTick) {
            this.animateTick = animateTick;
            return this;
        }

        public FoodData potSoupAnimateTick() {
            this.animateTick = FoodBiteAnimateTicks.POT_SOUP_ANIMATE_TICK;
            return this;
        }

        public FoodData addLootItems(ItemLike... lootItems) {
            this.lootItems.addAll(Arrays.stream(lootItems).toList());
            return this;
        }

        public FoodData setLootItem(ItemLike lootItem) {
            this.lootItems.clear();
            this.lootItems.add(lootItem);
            return this;
        }

        public FoodData setAABB(VoxelShape aabb) {
            this.aabb = aabb;
            return this;
        }

        public FoodData bowlAABB() {
            this.aabb = Block.box(2, 0, 2, 14, 6, 14);
            return this;
        }

        public FoodData soupPotAABB() {
            this.aabb = Shapes.or(
                    Block.box(1, 0, 1, 15, 1, 15),
                    Block.box(4, 1, 4, 12, 7, 12)
            );
            return this;
        }

        public int maxBites() {
            return maxBites;
        }

        public BlockType blockType() {
            return blockType;
        }

        @Nullable
        public VoxelShape getAABB() {
            return aabb;
        }

        @Nullable
        public FoodBiteAnimateTicks.AnimateTick animateTick() {
            return animateTick;
        }

        public List<ItemLike> getLootItems() {
            return List.copyOf(lootItems);
        }

        public FoodProperties blockFood() {
            return blockFood;
        }

        public FoodProperties itemFood() {
            return itemFood;
        }
    }

    public enum BlockType {
        SINGLE,
        ONE_BY_TWO
    }
}
