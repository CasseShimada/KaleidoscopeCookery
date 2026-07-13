package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class KaleidoscopeCookeryGameTests {
    @GameTest
    public void registrationsKeepStableIds(GameTestHelper helper) {
        Identifier potId = id("pot");

        helper.assertValueEqual(BuiltInRegistries.BLOCK.getKey(ModBlocks.POT), potId,
                "Pot block registry ID changed");
        helper.assertTrue(BuiltInRegistries.BLOCK.getValue(potId) == ModBlocks.POT,
                "Pot block registry does not contain the registered instance");
        helper.assertValueEqual(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(ModBlocks.RECIPE_BLOCK_BE),
                id("recipe_book"), "Legacy recipe block entity ID changed");
        helper.assertTrue(BuiltInRegistries.RECIPE_SERIALIZER.getValue(potId) == ModRecipes.POT_SERIALIZER,
                "Pot recipe serializer registry does not contain the registered instance");
        helper.assertTrue(BuiltInRegistries.RECIPE_TYPE.getValue(potId) == ModRecipes.POT_RECIPE,
                "Pot recipe type registry does not contain the registered instance");
        helper.succeed();
    }

    @GameTest
    public void potPlacementCreatesExpectedBlockEntity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POT);

        PotBlockEntity blockEntity = helper.getBlockEntity(pos, PotBlockEntity.class);
        helper.assertTrue(blockEntity.getType() == ModBlocks.POT_BE,
                "Placed pot created an unexpected block entity type");
        helper.succeed();
    }

    @GameTest
    public void generatedPotRecipeLoadsAndMatches(GameTestHelper helper) {
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, id("pot/baked_potato"));
        RecipeHolder<?> holder = helper.getLevel().recipeAccess().byKey(recipeKey)
                .orElseThrow(() -> helper.assertionException("Missing generated pot recipe %s", recipeKey.identifier()));

        helper.assertTrue(holder.value() instanceof PotRecipe,
                "Generated baked potato recipe did not decode as a pot recipe");
        PotRecipe recipe = (PotRecipe) holder.value();
        helper.assertTrue(recipe.matches(new SimpleInput(List.of(new ItemStack(Items.POTATO))), helper.getLevel()),
                "Generated baked potato recipe rejected its declared ingredient");
        helper.assertFalse(recipe.matches(new SimpleInput(List.of(new ItemStack(Items.CARROT))), helper.getLevel()),
                "Generated baked potato recipe accepted an unrelated ingredient");
        helper.succeed();
    }

    @GameTest
    public void foodContainersUseVanillaRemainderComponents(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        ItemStack bambooRemainder = new ItemStack(ModItems.BAMBOO_TUBE_RICE)
                .finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(bambooRemainder.is(Items.BAMBOO),
                "Bamboo tube rice did not convert into bamboo through USE_REMAINDER");

        ItemStack tea = new ItemStack(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA), 2);
        var useRemainder = tea.get(DataComponents.USE_REMAINDER);
        helper.assertTrue(useRemainder != null, "Tea did not register a USE_REMAINDER component");
        List<ItemStack> extraRemainders = new ArrayList<>();
        ItemStack remainingTea = useRemainder.convertIntoRemainder(
                tea.copyWithCount(1), tea.getCount(), false, extraRemainders::add);
        helper.assertTrue(remainingTea.is(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA))
                        && remainingTea.getCount() == 1,
                "Stacked tea did not preserve the remaining drink stack");
        helper.assertTrue(extraRemainders.stream()
                        .anyMatch(stack -> stack.is(ModItems.EMPTY_CUP)),
                "Stacked tea did not return the empty cup through vanilla use handling");

        ItemStack namedBowl = Items.BOWL.getDefaultInstance();
        namedBowl.set(DataComponents.CUSTOM_NAME, Component.literal("Preserved remainder"));
        ItemStack componentCarrier = Items.APPLE.getDefaultInstance();
        componentCarrier.set(DataComponents.USE_REMAINDER,
                new UseRemainder(ItemStackTemplate.fromNonEmptyStack(namedBowl)));
        helper.assertTrue(ItemStack.isSameItemSameComponents(
                        ItemUtils.getContainerStack(componentCarrier), namedBowl),
                "Container lookup discarded remainder stack components");
        helper.succeed();
    }

    @GameTest
    public void playerItemInsertionPreservesFallbackAndRemainders(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(1, 1, 1));
        player.getInventory().setItem(4, new ItemStack(Items.CARROT, 63));

        ItemUtils.giveItemToPlayer(player, new ItemStack(Items.CARROT, 2), 4);
        helper.assertValueEqual(player.getInventory().getItem(4).getCount(), 64,
                "Preferred inventory slot did not fill first");
        helper.assertTrue(player.getInventory().getItem(0).is(Items.CARROT)
                        && player.getInventory().getItem(0).getCount() == 1,
                "Preferred-slot remainder did not fall back to the regular inventory");

        fillInventory(player, Items.STONE);
        ItemUtils.giveItemToPlayer(player, new ItemStack(Items.POTATO));

        Player creativePlayer = helper.makeMockPlayer(GameType.CREATIVE);
        moveIntoTest(helper, creativePlayer, new BlockPos(2, 1, 1));
        fillInventory(creativePlayer, Items.STONE);
        ItemUtils.giveItemToPlayer(creativePlayer, new ItemStack(Items.BEETROOT));
        helper.runAfterDelay(1, () -> {
            helper.assertTrue(hasDroppedItem(helper, player, Items.POTATO),
                    "Full survival inventory did not drop the remaining item");
            helper.assertTrue(hasDroppedItem(helper, creativePlayer, Items.BEETROOT),
                    "Full creative inventory silently discarded the remaining item");
            helper.succeed();
        });
    }

    @GameTest
    public void stockpotSoupBaseIdsRemainCompatible(GameTestHelper helper) {
        Identifier water = vanillaId("water");
        Identifier lava = vanillaId("lava");

        helper.assertValueEqual(ModSoupBases.WATER, water, "Water soup-base ID changed");
        helper.assertValueEqual(ModSoupBases.LAVA, lava, "Lava soup-base ID changed");
        helper.assertTrue(SoupBaseManager.getSoupBase(vanillaId("water_bucket"))
                        == SoupBaseManager.getSoupBase(water),
                "Migrated water bucket soup-base ID did not resolve to water");
        helper.assertTrue(SoupBaseManager.getSoupBase(vanillaId("lava_bucket"))
                        == SoupBaseManager.getSoupBase(lava),
                "Migrated lava bucket soup-base ID did not resolve to lava");
        Map<Identifier, ISoupBase> soupBases = SoupBaseManager.getAllSoupBases();
        boolean mutationRejected = false;
        try {
            soupBases.put(water, soupBases.get(water));
        } catch (UnsupportedOperationException expected) {
            mutationRejected = true;
        }
        helper.assertTrue(mutationRejected, "Soup-base registry exposed mutable internal state");

        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(
                Registries.RECIPE, id("stockpot/four_joy_meatball_soup"));
        RecipeHolder<?> holder = helper.getLevel().recipeAccess().byKey(recipeKey)
                .orElseThrow(() -> helper.assertionException("Missing stockpot recipe %s", recipeKey.identifier()));
        helper.assertTrue(holder.value() instanceof StockpotRecipe,
                "Stockpot recipe did not decode as a stockpot recipe");
        helper.assertValueEqual(((StockpotRecipe) holder.value()).soupBase(), water,
                "Stockpot recipe did not preserve the stable water soup-base ID");

        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.STOCKPOT);
        StockpotBlockEntity stockpot = helper.getBlockEntity(pos, StockpotBlockEntity.class);
        CompoundTag tag = stockpot.saveCustomOnly(helper.getLevel().registryAccess());
        tag.putString("SoupBaseId", "minecraft:water_bucket");
        stockpot.loadCustomOnly(TagValueInput.create(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));
        helper.assertValueEqual(stockpot.getSoupBaseId(), water,
                "Migrated stockpot NBT did not normalize the water bucket alias");
        helper.assertTrue(stockpot.getSoupBase() == SoupBaseManager.getSoupBase(water),
                "Normalized stockpot NBT did not resolve its soup base");
        helper.succeed();
    }

    @GameTest
    public void satiatedShieldUsesVanillaExhaustionUnits(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(5.0F);
        player.getActiveEffectsMap().put(ModEffects.SATIATED_SHIELD,
                new MobEffectInstance(ModEffects.SATIATED_SHIELD, 200));
        float initialHealth = player.getHealth();

        boolean damaged = player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 4.0F);

        helper.assertFalse(damaged, "Satiated shield did not cancel Fabric's allow-damage callback");
        helper.assertValueEqual(player.getHealth(), initialHealth,
                "Satiated shield allowed ordinary damage to reduce health");
        CompoundTag foodDataTag = foodDataTag(player);
        helper.assertValueEqual(foodDataTag.getFloatOr("foodExhaustionLevel", -1.0F), 8.0F,
                "Four damage did not add eight vanilla exhaustion points");

        ServerPlayer foodTickPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        foodTickPlayer.getFoodData().readAdditionalSaveData(TagValueInput.create(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), foodDataTag));
        foodTickPlayer.getFoodData().tick(foodTickPlayer);
        helper.assertValueEqual(foodTickPlayer.getFoodData().getSaturationLevel(), 4.0F,
                "Vanilla food ticking did not consume saturation from shield exhaustion");
        helper.assertValueEqual(foodTickPlayer.getFoodData().getFoodLevel(), 20,
                "Shield exhaustion skipped saturation and consumed hunger first");
        helper.succeed();
    }

    @GameTest
    public void satiatedShieldPreservesOverflowDamage(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(1);
        player.getFoodData().setSaturation(0.0F);
        player.getActiveEffectsMap().put(ModEffects.SATIATED_SHIELD,
                new MobEffectInstance(ModEffects.SATIATED_SHIELD, 200));
        float initialHealth = player.getHealth();

        GeneralConfig config = GeneralConfig.get();
        boolean previousSetting = config.satiatedShieldAbsorbExcessDamage;
        boolean damaged;
        try {
            config.satiatedShieldAbsorbExcessDamage = false;
            damaged = player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 4.0F);
        } finally {
            config.satiatedShieldAbsorbExcessDamage = previousSetting;
        }

        helper.assertFalse(damaged, "Satiated shield did not cancel the original damage call");
        helper.assertValueEqual(player.getHealth(), initialHealth - 2.0F,
                "Damage beyond the available hunger shield was not preserved");
        helper.succeed();
    }

    @GameTest
    public void sickleCallbackUsesExplicitHarvestResults(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        SickleHarvestCallback.Result defaultResult = SickleHarvestCallback.EVENT.invoker().harvest(
                player, ItemStack.EMPTY, BlockPos.ZERO, Blocks.WHEAT.defaultBlockState());
        SickleHarvestCallback.Result netherWartResult = SickleHarvestCallback.EVENT.invoker().harvest(
                player, ItemStack.EMPTY, BlockPos.ZERO, Blocks.NETHER_WART.defaultBlockState());

        helper.assertValueEqual(defaultResult, SickleHarvestCallback.Result.PASS,
                "Sickle callback intercepted an unrelated crop");
        helper.assertValueEqual(netherWartResult, SickleHarvestCallback.Result.SKIP,
                "Immature nether wart did not stop default sickle harvesting");
        helper.succeed();
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static Identifier vanillaId(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    private static void fillInventory(Player player, net.minecraft.world.level.ItemLike item) {
        for (int slot = 0; slot < player.getInventory().getNonEquipmentItems().size(); slot++) {
            player.getInventory().setItem(slot, new ItemStack(item, item.asItem().getDefaultMaxStackSize()));
        }
    }

    private static void moveIntoTest(GameTestHelper helper, Player player, BlockPos relativePos) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        player.setPos(absolutePos.getX() + 0.5, absolutePos.getY(), absolutePos.getZ() + 0.5);
    }

    private static boolean hasDroppedItem(GameTestHelper helper, Player player, net.minecraft.world.level.ItemLike item) {
        return !helper.getLevel().getEntitiesOfClass(ItemEntity.class, player.getBoundingBox().inflate(2.0),
                entity -> entity.getItem().is(item.asItem())).isEmpty();
    }

    private static CompoundTag foodDataTag(Player player) {
        TagValueOutput output = TagValueOutput.createWithoutContext(ProblemReporter.DISCARDING);
        player.getFoodData().addAdditionalSaveData(output);
        return output.buildResult();
    }
}
