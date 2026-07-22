package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ActionEventCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.api.storage.MillstoneEntityItemStorage;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.ChairBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.FruitBasketBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.PlateBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.decoration.TableBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.EmptyCupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.drink.TeacupBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ChoppingBoardBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.EnamelBasinBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.KitchenwareRacksBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.OilPotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.PotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StoveBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.TeapotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.ChiliRistraBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.StrungMushroomsBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.TrashCanBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.OilPotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.RecipeBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.TableBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.ChairBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ChoppingBoardBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.KitchenwareRacksBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.ShawarmaSpitBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfigTestAccess;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.config.ClientConfigTestAccess;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.FoodEffectTooltipsCompatTestAccess;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight.FarmersDelightCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.output.RandomOutput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexPotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.RiceBowlRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.ChoppingBoardRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.FlexPotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.FlexStockpotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.MillstoneRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.PotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.RiceBowlRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.SteamerRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SimpleSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ThrowableBaoziEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ai.CatLieOnBlockGoal;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.FlatulenceEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.WarmthEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModAttachmentType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModLootTables;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModVillager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.PlateRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.FoodBiteRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FruitBasketItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.OilPotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RawDoughItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TeapotItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.network.NetworkHandler;
import com.github.ysbbbbbb.kaleidoscopecookery.network.message.FlatulenceMessage;
import net.fabricmc.loader.api.FabricLoader;
import com.github.ysbbbbbb.kaleidoscopecookery.network.message.ThrowBaoziMessage;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyItemStackCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyPlayerDataCompat;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import net.minecraft.nbt.NbtOps;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.fabricmc.fabric.api.entity.event.v1.effect.ServerMobEffectEvents;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidConstants;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.storage.base.SingleVariantStorage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Unit;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.chicken.Chicken;
import net.minecraft.world.entity.animal.cow.Cow;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.entity.animal.feline.Cat;
import net.minecraft.world.entity.animal.parrot.Parrot;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ProjectileDeflection;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.Consumable;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.consume_effects.ApplyStatusEffectsConsumeEffect;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FarmlandBlock;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.PowderSnowBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.piston.PistonStructureResolver;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.levelgen.structure.pools.SinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class KaleidoscopeCookeryGameTests {
    private static final Set<String> CARRY_ON_SAFE_BLOCK_PATHS = Set.of(
            "pot", "stockpot", "fruit_basket", "chopping_board", "kitchenware_racks", "teapot",
            "trash_can", "recipe_block", "oil_pot",
            "chair_acacia", "chair_bamboo", "chair_birch", "chair_cherry", "chair_crimson",
            "chair_dark_oak", "chair_jungle", "chair_mangrove", "chair_oak", "chair_spruce", "chair_warped",
            "table_acacia", "table_bamboo", "table_birch", "table_cherry", "table_crimson",
            "table_dark_oak", "table_jungle", "table_mangrove", "table_oak", "table_spruce", "table_warped");
    private static final Map<UUID, Storage<ItemVariant>> TEST_MILLSTONE_ENTITY_STORAGES =
            new ConcurrentHashMap<>();
    private static boolean millstoneEntityStorageTestProviderRegistered;

    @GameTest
    public void plateDropsCombineBaselineBowlWithRemainingServings(GameTestHelper helper) {
        Block block = PlateRegistry.getBlock(PlateRegistry.APPLE_PLATTER);
        helper.assertTrue(block instanceof PlateBlock, "Apple platter is not a PlateBlock");
        PlateBlock plate = (PlateBlock) block;
        BlockPos pos = helper.absolutePos(new BlockPos(1, 1, 1));

        BlockState fullState = plate.defaultBlockState();
        List<ItemStack> fullDrops = Block.getDrops(fullState, helper.getLevel(), pos, null);
        int fullBowls = fullDrops.stream().filter(stack -> stack.is(Items.BOWL)).mapToInt(ItemStack::getCount).sum();
        int fullApples = fullDrops.stream().filter(stack -> stack.is(Items.APPLE)).mapToInt(ItemStack::getCount).sum();
        helper.assertValueEqual(fullBowls, 1, "Full plate lost the Forge bowl loot-table drop");
        helper.assertValueEqual(fullApples, 4, "Full plate did not preserve remaining servings");

        BlockState emptyState = fullState.setValue(plate.getServingsProperty(), 0);
        List<ItemStack> emptyDrops = Block.getDrops(emptyState, helper.getLevel(), pos, null);
        int emptyBowls = emptyDrops.stream().filter(stack -> stack.is(Items.BOWL)).mapToInt(ItemStack::getCount).sum();
        int emptyApples = emptyDrops.stream().filter(stack -> stack.is(Items.APPLE)).mapToInt(ItemStack::getCount).sum();
        helper.assertValueEqual(emptyBowls, 1, "Empty plate lost the Forge bowl loot-table drop");
        helper.assertValueEqual(emptyApples, 0, "Empty plate incorrectly dropped consumed servings");
        helper.succeed();
    }

    @GameTest
    @SuppressWarnings("deprecation")
    public void registrationsKeepStableIds(GameTestHelper helper) {
        Identifier potId = id("pot");

        helper.assertValueEqual(BuiltInRegistries.BLOCK.getKey(ModBlocks.POT), potId,
                "Pot block registry ID changed");
        helper.assertTrue(BuiltInRegistries.BLOCK.getValue(potId) == ModBlocks.POT,
                "Pot block registry does not contain the registered instance");
        helper.assertValueEqual(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(ModBlocks.RECIPE_BLOCK_BE),
                id("recipe_book"), "Legacy recipe block entity ID changed");
        helper.assertValueEqual(BuiltInRegistries.BLOCK_ENTITY_TYPE.getKey(ModBlocks.FORGE_RECIPE_BLOCK_BE),
                id("recipe_block"), "Forge recipe block entity compatibility ID changed");
        helper.assertTrue(ModBlocks.FORGE_RECIPE_BLOCK_BE.isValid(ModBlocks.RECIPE_BLOCK.defaultBlockState()),
                "Forge recipe block entity compatibility type rejects the recipe block");
        helper.assertTrue(BuiltInRegistries.RECIPE_SERIALIZER.getValue(potId) == ModRecipes.POT_SERIALIZER,
                "Pot recipe serializer registry does not contain the registered instance");
        helper.assertTrue(BuiltInRegistries.RECIPE_TYPE.getValue(potId) == ModRecipes.POT_RECIPE,
                "Pot recipe type registry does not contain the registered instance");
        helper.assertValueEqual(BuiltInRegistries.ENTITY_TYPE.getKey(ModEntities.SCARECROW),
                id("scarecrow"), "Scarecrow entity registry ID changed");
        helper.assertValueEqual(BuiltInRegistries.MOB_EFFECT.getKey(ModEffects.FLATULENCE.value()),
                id("flatulence"), "Flatulence effect registry ID changed");
        helper.assertValueEqual(BuiltInRegistries.TRIGGER_TYPES.getKey(ModTrigger.EVENT),
                id("mod_event"), "Mod event trigger registry ID changed");
        helper.assertValueEqual(BuiltInRegistries.TRIGGER_TYPES.getKey(ModTrigger.FLATULENCE_FLY_HEIGHT),
                id("flatulence_fly_height"), "Flatulence distance trigger registry ID changed");
        helper.assertValueEqual(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(ModDataComponents.OIL_POT_OIL_COUNT),
                id("oil_pot_oil_count"), "NeoForge oil pot component compatibility ID changed");
        helper.assertValueEqual(BuiltInRegistries.DATA_COMPONENT_TYPE.getKey(ModDataComponents.OIL_POT_COUNT),
                id("oil_pot_count"), "Interim Fabric oil pot component compatibility ID changed");
        helper.assertTrue(BuiltInRegistries.SOUND_EVENT.getValue(id("block.teapot.processing"))
                        == ModSounds.BLOCK_TEAPOT_PROCESSING,
                "Legacy teapot processing sound ID is not registered");
        helper.assertTrue(BuiltInRegistries.SOUND_EVENT.getValue(id("block.trash_can")) == ModSounds.TRASH_CAN,
                "Legacy trash can sound ID is not registered");
        helper.assertValueEqual(EnamelBasinBlock.MAX_OIL_COUNT, 32,
                "Enamel basin no longer accepts legacy oil-count block states");
        helper.assertValueEqual(ModBlocks.ENAMEL_BASIN.defaultBlockState().getValue(EnamelBasinBlock.OIL_COUNT), 0,
                "Crafted enamel basins no longer default to empty");
        helper.assertValueEqual(
                DefaultAttributes.getSupplier(ModEntities.SCARECROW).getBaseValue(Attributes.STEP_HEIGHT),
                0.0, "Scarecrow-specific default attributes are not registered");
        helper.assertTrue(ModEvents.MILLSTONE_FINISH == ActionEventCallback.MillstoneFinish.EVENT
                        && ModEvents.CHECK_SPECIAL_ITEM == ActionEventCallback.CheckSpecialItem.EVENT
                        && ModEvents.DEDUCT_SPECIAL_ITEM == ActionEventCallback.DeductSpecialItem.EVENT,
                "Legacy event fields do not bridge to the Fabric-style callback events");
        helper.succeed();
    }

    @GameTest
    @SuppressWarnings("deprecation")
    public void oilPotReadsAndMigratesLegacyData(GameTestHelper helper) {
        ItemStack interimFabricStack = new ItemStack(ModItems.OIL_POT);
        interimFabricStack.set(ModDataComponents.OIL_POT_COUNT, 37);
        helper.assertValueEqual(OilPotItem.getOilCount(interimFabricStack), 37,
                "Interim Fabric oil pot component was not read");

        OilPotItem.setOilCount(interimFabricStack, 38);
        helper.assertValueEqual(interimFabricStack.get(ModDataComponents.OIL_POT_OIL_COUNT), 38,
                "Interim Fabric oil pot data was not migrated to the NeoForge-compatible component");
        helper.assertTrue(!interimFabricStack.has(ModDataComponents.OIL_POT_COUNT),
                "Interim Fabric oil pot component remained after migration");

        ItemStack forgeStack = new ItemStack(ModItems.OIL_POT);
        CompoundTag forgeTag = new CompoundTag();
        forgeTag.putInt("oil_count", 41);
        forgeTag.putString("unrelated", "preserve-me");
        forgeStack.set(DataComponents.CUSTOM_DATA, CustomData.of(forgeTag));
        helper.assertValueEqual(OilPotItem.getOilCount(forgeStack), 41,
                "Forge oil_count custom NBT was not read");

        OilPotItem.setOilCount(forgeStack, 42);
        helper.assertValueEqual(forgeStack.get(ModDataComponents.OIL_POT_OIL_COUNT), 42,
                "Forge oil pot data was not migrated to the NeoForge-compatible component");
        CustomData remaining = forgeStack.get(DataComponents.CUSTOM_DATA);
        helper.assertTrue(remaining != null, "Unrelated Forge custom data was removed during migration");
        CompoundTag remainingTag = remaining.copyTag();
        helper.assertValueEqual(remainingTag.getStringOr("unrelated", ""), "preserve-me",
                "Unrelated Forge custom data changed during migration");
        helper.assertTrue(!remainingTag.contains("oil_count"),
                "Migrated Forge oil_count custom NBT remained duplicated");
        helper.succeed();
    }

    @GameTest
    public void compatibilityCodecReadsForge120ItemStack(GameTestHelper helper) {
        CompoundTag legacyStack = legacyStack(Items.APPLE, 3);
        RegistryOps<net.minecraft.nbt.Tag> ops = RegistryOps.create(
                NbtOps.INSTANCE, helper.getLevel().registryAccess());
        ItemStack decoded = LegacyItemStackCompat.ITEM_STACK_CODEC.parse(ops, legacyStack).getOrThrow();
        helper.assertTrue(decoded.is(Items.APPLE) && decoded.getCount() == 3,
                "Compatibility codec cannot read the Forge 1.20.1 stack format");

        CompoundTag handler = legacyItemHandler(4, Map.of(2, legacyStack(Items.CARROT, 5)));
        NonNullList<ItemStack> slots = NonNullList.withSize(4, ItemStack.EMPTY);
        LegacyItemStackCompat.loadAllItems(TagValueInput.create(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), handler), slots);
        helper.assertTrue(slots.get(2).is(Items.CARROT) && slots.get(2).getCount() == 5,
                "Compatibility codec did not preserve a Forge ItemStackHandler slot");

        CompoundTag richStack = legacyStack(Items.DIAMOND_SWORD, 1);
        CompoundTag richTag = new CompoundTag();
        richTag.putInt("Damage", 17);
        CompoundTag display = new CompoundTag();
        display.putString("Name", "{\"text\":\"Migration blade\"}");
        richTag.put("display", display);
        CompoundTag sharpness = new CompoundTag();
        sharpness.putString("id", "minecraft:sharpness");
        sharpness.putShort("lvl", (short) 3);
        ListTag enchantments = new ListTag();
        enchantments.add(sharpness);
        richTag.put("Enchantments", enchantments);
        richTag.putString("migration_marker", "preserve-me");
        richStack.put("tag", richTag);

        ItemStack decodedRichStack = LegacyItemStackCompat.ITEM_STACK_CODEC.parse(ops, richStack).getOrThrow();
        assertRichLegacyStack(helper, decodedRichStack);
        net.minecraft.nbt.Tag resaved = ItemStack.CODEC.encodeStart(ops, decodedRichStack).getOrThrow();
        ItemStack reloadedRichStack = ItemStack.CODEC.parse(ops, resaved).getOrThrow();
        assertRichLegacyStack(helper, reloadedRichStack);
        helper.succeed();
    }

    @GameTest
    public void foodBiteQualityPreservesForgeStateAndLegacyItemData(GameTestHelper helper) {
        int foodBlockCount = 0;
        for (Block block : BuiltInRegistries.BLOCK) {
            Identifier blockId = BuiltInRegistries.BLOCK.getKey(block);
            if (!blockId.getNamespace().equals(KaleidoscopeCookery.MOD_ID)
                    || !(block instanceof FoodBiteBlock foodBlock)) {
                continue;
            }
            foodBlockCount++;
            helper.assertTrue(foodBlock.getStateDefinition().getProperty("quality") == FoodBiteBlock.QUALITY,
                    "Food block lost the Forge quality property: " + blockId);
            helper.assertValueEqual(FoodBiteBlock.QUALITY.getPossibleValues(), List.of(0, 1, 2, 3, 4),
                    "Food block quality range changed: " + blockId);
            helper.assertValueEqual(foodBlock.defaultBlockState().getValue(FoodBiteBlock.QUALITY),
                    FoodBiteBlock.DEFAULT_QUALITY, "Food block missing-quality default changed: " + blockId);
        }
        helper.assertTrue(foodBlockCount > 0, "No registered Cookery food bite blocks were checked");

        FoodBiteBlock candiedPotato = (FoodBiteBlock) BuiltInRegistries.BLOCK.getValue(
                FoodBiteRegistry.CANDIED_POTATO);
        ItemStack placedStack = new ItemStack(candiedPotato);
        QualityUtils.setQuality(placedStack, Quality.SUPERB);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos relativePos = new BlockPos(1, 1, 1);
        BlockPos absolutePos = helper.absolutePos(relativePos);
        BlockHitResult hit = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        BlockState placedState = candiedPotato.getStateForPlacement(new BlockPlaceContext(
                helper.getLevel(), player, InteractionHand.MAIN_HAND, placedStack, hit));
        helper.assertTrue(placedState != null
                        && placedState.getValue(FoodBiteBlock.QUALITY) == Quality.SUPERB.getId(),
                "Food placement did not copy the item quality into the Forge block state");

        List<ItemStack> drops = Block.getDrops(placedState, helper.getLevel(), absolutePos, null);
        ItemStack foodDrop = drops.stream().filter(stack -> stack.is(candiedPotato.asItem()))
                .findFirst().orElseThrow(() -> new AssertionError("Unbitten quality food did not drop itself"));
        helper.assertValueEqual(QualityUtils.getQuality(foodDrop), Quality.SUPERB,
                "Food loot did not copy the Forge block quality into the item component");

        CompoundTag legacyFood = legacyStack(candiedPotato, 1);
        CompoundTag legacyFoodTag = new CompoundTag();
        legacyFoodTag.putInt(QualityUtils.LEGACY_QUALITY, Quality.EXCELLENT.getId());
        legacyFoodTag.putString("unrelated", "preserve-me");
        legacyFood.put("tag", legacyFoodTag);
        RegistryOps<net.minecraft.nbt.Tag> ops = RegistryOps.create(
                NbtOps.INSTANCE, helper.getLevel().registryAccess());
        ItemStack migratedFood = LegacyItemStackCompat.ITEM_STACK_CODEC.parse(ops, legacyFood).getOrThrow();
        helper.assertTrue(QualityUtils.hasQuality(migratedFood),
                "Forge food quality NBT was not recognized after ItemStack DataFix");
        helper.assertValueEqual(QualityUtils.getQuality(migratedFood), Quality.EXCELLENT,
                "Forge food quality NBT changed meaning during component migration");
        CustomData migratedCustomData = migratedFood.get(DataComponents.CUSTOM_DATA);
        helper.assertTrue(migratedCustomData != null
                        && migratedCustomData.copyTag().getStringOr("unrelated", "").equals("preserve-me")
                        && !migratedCustomData.copyTag().contains(QualityUtils.LEGACY_QUALITY),
                "Food quality migration removed unrelated custom data or retained the old key");

        helper.setBlock(relativePos, placedState);
        player.getFoodData().setFoodLevel(0);
        player.getFoodData().setSaturation(0.0F);
        InteractionResult eatResult = candiedPotato.useWithoutItem(
                placedState, helper.getLevel(), absolutePos, player, hit);
        helper.assertValueEqual(eatResult, InteractionResult.CONSUME,
                "Quality food block did not consume a bite");
        helper.assertValueEqual(player.getFoodData().getFoodLevel(), 6,
                "Superb block food did not apply the Forge 1.2 nutrition multiplier");
        MobEffectInstance warmth = player.getEffect(ModEffects.WARMTH);
        helper.assertTrue(warmth != null && warmth.getDuration() == 1920,
                "Superb block food did not apply the Forge 1.2 effect-duration multiplier");
        helper.succeed();
    }

    @GameTest
    public void blockEntitiesReadForge120ItemStorage(GameTestHelper helper) {
        BlockPos basketPos = new BlockPos(1, 1, 1);
        helper.setBlock(basketPos, ModBlocks.FRUIT_BASKET);
        FruitBasketBlockEntity basket = helper.getBlockEntity(basketPos, FruitBasketBlockEntity.class);
        CompoundTag basketTag = new CompoundTag();
        basketTag.put("BasketItems", legacyItemHandler(8, Map.of(
                0, legacyStack(Items.APPLE, 3),
                7, legacyStack(Items.CARROT, 2))));
        basket.loadCustomOnly(valueInput(helper, basketTag));
        helper.assertTrue(basket.getItems().get(0).is(Items.APPLE)
                        && basket.getItems().get(0).getCount() == 3
                        && basket.getItems().get(7).is(Items.CARROT)
                        && basket.getItems().get(7).getCount() == 2,
                "Fruit basket lost Forge ItemStackHandler contents or slot order");

        BlockPos tablePos = new BlockPos(2, 1, 1);
        helper.setBlock(tablePos, ModBlocks.TABLE_OAK.defaultBlockState()
                .setValue(TableBlock.HAS_CARPET, true));
        TableBlockEntity table = helper.getBlockEntity(tablePos, TableBlockEntity.class);
        CompoundTag tableTag = new CompoundTag();
        tableTag.putInt("CarpetColor", DyeColor.RED.getId());
        tableTag.put("ShowItems", legacyItemHandler(4, Map.of(3, legacyStack(Items.BREAD, 4))));
        table.loadCustomOnly(valueInput(helper, tableTag));
        helper.assertTrue(table.getItems().get(3).is(Items.BREAD)
                        && table.getItems().get(3).getCount() == 4,
                "Table lost Forge display contents or slot order");

        BlockPos recipePos = new BlockPos(3, 1, 1);
        helper.setBlock(recipePos, ModBlocks.RECIPE_BLOCK);
        RecipeBlockEntity recipeBlock = helper.getBlockEntity(recipePos, RecipeBlockEntity.class);
        CompoundTag recipeTag = new CompoundTag();
        recipeTag.put("ShowItems", legacyItemHandler(1, Map.of(0, legacyStack(Items.PAPER, 1))));
        recipeBlock.loadCustomOnly(valueInput(helper, recipeTag));
        helper.assertTrue(recipeBlock.getItem().is(Items.PAPER),
                "Recipe block did not unwrap its Forge ItemStackHandler contents");

        BlockPos steamerPos = new BlockPos(4, 1, 1);
        helper.setBlock(steamerPos, ModBlocks.STEAMER);
        SteamerBlockEntity steamer = helper.getBlockEntity(steamerPos, SteamerBlockEntity.class);
        CompoundTag steamerTag = legacyItemHandler(8, Map.of(6, legacyStack(Items.POTATO, 1)));
        steamerTag.putIntArray("CookingProgress", new int[]{0, 0, 0, 0, 0, 0, 11, 0});
        steamerTag.putIntArray("CookingTime", new int[]{0, 0, 0, 0, 0, 0, 20, 0});
        steamer.loadCustomOnly(valueInput(helper, steamerTag));
        helper.assertTrue(steamer.getItems().get(6).is(Items.POTATO)
                        && steamer.getCookingProgress()[6] == 11
                        && steamer.getCookingTime()[6] == 20,
                "Steamer lost Forge contents, slot order, or cooking progress");

        BlockPos trashPos = new BlockPos(5, 1, 1);
        helper.setBlock(trashPos, ModBlocks.TRASH_CAN);
        TrashCanBlockEntity trashCan = helper.getBlockEntity(trashPos, TrashCanBlockEntity.class);
        CompoundTag trashTag = new CompoundTag();
        trashTag.put("Storage", legacyItemHandler(3, Map.of(1, legacyStack(Items.BONE, 6))));
        trashCan.loadCustomOnly(valueInput(helper, trashTag));
        helper.assertTrue(trashCan.getStoredItems().getFirst().is(Items.BONE)
                        && trashCan.getStoredItems().getFirst().getCount() == 6,
                "Trash can lost Forge ItemStackHandler contents");
        helper.succeed();
    }

    @GameTest
    public void millstoneRecipesPreserveRandomMultiOutputs(GameTestHelper helper) {
        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());
        MillstoneRecipe multiOutput = MillstoneRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "ingredient": "minecraft:wheat",
                  "results": [
                    {"id": "minecraft:apple", "count": 2, "chance": 2.0},
                    {"id": "minecraft:carrot", "chance": -1.0}
                  ],
                  "carrier": "minecraft:bowl"
                }
                """)).getOrThrow();

        helper.assertValueEqual(multiOutput.results().size(), 2,
                "Millstone results array did not preserve every output");
        helper.assertValueEqual(multiOutput.results().getFirst().chance(), 1.0F,
                "Millstone chance above one was not clamped like the Forge recipe");
        helper.assertValueEqual(multiOutput.results().get(1).chance(), 0.0F,
                "Millstone chance below zero was not clamped like the Forge recipe");
        helper.assertTrue(multiOutput.getCarrier().isPresent()
                        && multiOutput.getCarrier().orElseThrow().test(Items.BOWL.getDefaultInstance()),
                "Millstone multi-output codec lost the Fabric carrier extension");

        List<ItemStack> rolled = multiOutput.rollResults(3, RandomSource.create(123L));
        int appleCount = rolled.stream().filter(stack -> stack.is(Items.APPLE))
                .mapToInt(ItemStack::getCount).sum();
        helper.assertValueEqual(appleCount, 6,
                "Millstone did not roll the guaranteed output once per input unit");
        helper.assertTrue(rolled.stream().noneMatch(stack -> stack.is(Items.CARROT)),
                "Millstone emitted a zero-chance output");

        MillstoneRecipe singleOutput = MillstoneRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "ingredient": "minecraft:wheat",
                  "result": {"id": "minecraft:bread", "count": 1}
                }
                """)).getOrThrow();
        helper.assertTrue(singleOutput.results().size() == 1 && singleOutput.getResult().is(Items.BREAD),
                "Millstone no longer accepts the historical/current single result field");
        helper.assertTrue(MillstoneRecipeSerializer.CODEC.codec().encodeStart(ops, multiOutput)
                        .getOrThrow().getAsJsonObject().has("results"),
                "Millstone multi-output recipe did not save through the stable results field");
        helper.succeed();
    }

    @GameTest
    public void customRecipeSerializersReadForgeJsonShapes(GameTestHelper helper) {
        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());

        PotRecipe pot = PotRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "carrier": {"item": "minecraft:bowl"},
                  "ingredients": [
                    {"item": "minecraft:wheat"},
                    [{"item": "minecraft:carrot"}, {"tag": "minecraft:planks"}]
                  ],
                  "result": {"item": "minecraft:bread", "count": 2}
                }
                """)).getOrThrow();
        helper.assertTrue(pot.carrier().orElseThrow().test(Items.BOWL.getDefaultInstance())
                        && pot.ingredients().getFirst().test(Items.WHEAT.getDefaultInstance())
                        && pot.ingredients().get(1).test(Items.OAK_PLANKS.getDefaultInstance())
                        && pot.result().create().is(Items.BREAD)
                        && pot.result().create().getCount() == 2,
                "Pot serializer lost Forge object/array ingredients, carrier, or result.item");

        FlexPotRecipe flexPot = FlexPotRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "ingredients": [{"item": "minecraft:potato"}],
                  "result": {"item": "minecraft:baked_potato"}
                }
                """)).getOrThrow();
        helper.assertTrue(flexPot.carrier().isEmpty()
                        && flexPot.ingredients().getFirst().test(Items.POTATO.getDefaultInstance())
                        && flexPot.result().create().is(Items.BAKED_POTATO),
                "Flex pot serializer did not preserve the Forge empty-carrier default");

        ChoppingBoardRecipe chopping = ChoppingBoardRecipeSerializer.CODEC.codec().parse(
                ops, JsonParser.parseString("""
                        {
                          "ingredient": [{"item": "minecraft:porkchop"}, {"tag": "minecraft:planks"}],
                          "result": {"item": "minecraft:stick", "count": 3}
                        }
                        """)).getOrThrow();
        helper.assertTrue(chopping.getIngredient().test(Items.PORKCHOP.getDefaultInstance())
                        && chopping.getIngredient().test(Items.OAK_PLANKS.getDefaultInstance())
                        && chopping.getResult().is(Items.STICK)
                        && chopping.getResult().getCount() == 3,
                "Chopping-board serializer lost a Forge ingredient array or result.item");

        StockpotRecipe stockpot = StockpotRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "ingredients": [{"tag": "minecraft:planks"}],
                  "result": {"item": "minecraft:mushroom_stew"},
                  "carrier": {"item": "minecraft:apple"},
                  "empty_carrier": true
                }
                """)).getOrThrow();
        helper.assertTrue(stockpot.getIngredients().getFirst().test(Items.OAK_PLANKS.getDefaultInstance())
                        && stockpot.carrier().isEmpty()
                        && stockpot.result().create().is(Items.MUSHROOM_STEW),
                "Stockpot serializer did not give Forge empty_carrier precedence over carrier");

        FlexStockpotRecipe flexStockpot = FlexStockpotRecipeSerializer.CODEC.codec().parse(
                ops, JsonParser.parseString("""
                        {
                          "ingredients": [{"item": "minecraft:beef"}],
                          "carrier": {"item": "minecraft:bucket"},
                          "result": {"item": "minecraft:rabbit_stew"}
                        }
                        """)).getOrThrow();
        helper.assertTrue(flexStockpot.getIngredients().getFirst().test(Items.BEEF.getDefaultInstance())
                        && flexStockpot.carrier().test(Items.BUCKET.getDefaultInstance())
                        && flexStockpot.result().create().is(Items.RABBIT_STEW),
                "Flex stockpot serializer lost Forge ingredient, carrier, or result.item");

        SteamerRecipe steamer = SteamerRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "ingredient": {"item": "minecraft:potato"},
                  "result": {"item": "minecraft:baked_potato"}
                }
                """)).getOrThrow();
        helper.assertTrue(steamer.getIngredient().test(Items.POTATO.getDefaultInstance())
                        && steamer.getResult().is(Items.BAKED_POTATO),
                "Steamer serializer lost a Forge ingredient or result.item");

        MillstoneRecipe millstone = MillstoneRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "ingredient": [{"item": "minecraft:wheat"}, {"tag": "minecraft:planks"}],
                  "result": {"item": "minecraft:bread"}
                }
                """)).getOrThrow();
        helper.assertTrue(millstone.getIngredient().test(Items.WHEAT.getDefaultInstance())
                        && millstone.getIngredient().test(Items.OAK_PLANKS.getDefaultInstance())
                        && millstone.getResult().is(Items.BREAD),
                "Millstone serializer lost a Forge ingredient array or result.item");

        TeapotRecipe teapot = TeapotRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "tea_fluid": "minecraft:water",
                  "result": {"item": "minecraft:honey_bottle"}
                }
                """)).getOrThrow();
        helper.assertTrue(teapot.ingredient().isEmpty() && teapot.result().create().is(Items.HONEY_BOTTLE),
                "Teapot serializer did not preserve the Forge empty-ingredient default");

        RiceBowlRecipe riceBowl = RiceBowlRecipeSerializer.CODEC.codec().parse(ops, JsonParser.parseString("""
                {
                  "ingredient": {"item": "minecraft:carrot"},
                  "result": {"item": "minecraft:rabbit_stew"}
                }
                """)).getOrThrow();
        helper.assertTrue(riceBowl.getIngredient().test(Items.CARROT.getDefaultInstance())
                        && riceBowl.getResult().is(Items.RABBIT_STEW),
                "Rice-bowl serializer lost a Forge ingredient or result.item");

        RegistryFriendlyByteBuf flexPotBuffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(), helper.getLevel().registryAccess());
        RegistryFriendlyByteBuf stockpotBuffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(), helper.getLevel().registryAccess());
        RegistryFriendlyByteBuf teapotBuffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            FlexPotRecipeSerializer.STREAM_CODEC.encode(flexPotBuffer, flexPot);
            StockpotRecipeSerializer.STREAM_CODEC.encode(stockpotBuffer, stockpot);
            TeapotRecipeSerializer.STREAM_CODEC.encode(teapotBuffer, teapot);
            helper.assertTrue(FlexPotRecipeSerializer.STREAM_CODEC.decode(flexPotBuffer).carrier().isEmpty(),
                    "Flex pot StreamCodec changed an empty carrier during recipe sync");
            helper.assertTrue(StockpotRecipeSerializer.STREAM_CODEC.decode(stockpotBuffer).carrier().isEmpty(),
                    "Stockpot StreamCodec changed empty_carrier during recipe sync");
            helper.assertTrue(TeapotRecipeSerializer.STREAM_CODEC.decode(teapotBuffer).ingredient().isEmpty(),
                    "Teapot StreamCodec changed an empty ingredient during recipe sync");
        } finally {
            flexPotBuffer.release();
            stockpotBuffer.release();
            teapotBuffer.release();
        }

        var encodedStockpot = StockpotRecipeSerializer.CODEC.codec().encodeStart(ops, stockpot)
                .getOrThrow().getAsJsonObject();
        helper.assertTrue(encodedStockpot.get("empty_carrier").getAsBoolean()
                        && !encodedStockpot.has("carrier")
                        && encodedStockpot.getAsJsonObject("result").has("id")
                        && !encodedStockpot.getAsJsonObject("result").has("item"),
                "Compatible stockpot recipe did not re-encode through stable 26.2 fields");
        helper.assertFalse(FlexPotRecipeSerializer.CODEC.codec().encodeStart(ops, flexPot)
                        .getOrThrow().getAsJsonObject().has("carrier"),
                "Flex pot empty carrier was not omitted when re-encoding");
        helper.assertFalse(TeapotRecipeSerializer.CODEC.codec().encodeStart(ops, teapot)
                        .getOrThrow().getAsJsonObject().has("ingredient"),
                "Teapot empty ingredient was not omitted when re-encoding");
        helper.succeed();
    }

    @GameTest
    public void millstoneReadsAndResavesForgeFourSlotStorage(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.MILLSTONE);
        MillstoneBlockEntity millstone = helper.getBlockEntity(pos, MillstoneBlockEntity.class);
        UUID boundEntity = UUID.fromString("12345678-1234-5678-9abc-def012345678");
        CompoundTag forgeTag = new CompoundTag();
        forgeTag.putIntArray("EntityId", UUIDUtil.uuidToIntArray(boundEntity));
        forgeTag.put("InputItem", legacyStack(Items.WHEAT, 4));
        forgeTag.put("OutputItem", legacyItemHandler(4, Map.of(
                0, legacyStack(Items.APPLE, 3),
                2, legacyStack(Items.CARROT, 5))));
        forgeTag.putString("CarrierIngredient", "{\"item\":\"minecraft:bowl\"}");
        millstone.loadCustomOnly(valueInput(helper, forgeTag));

        List<ItemStack> outputs = millstone.getOutputs();
        helper.assertTrue(millstone.hasEntity() && millstone.getInput().is(Items.WHEAT)
                        && millstone.getInput().getCount() == 4,
                "Millstone lost the Forge UUID or input stack");
        helper.assertTrue(outputs.size() == MillstoneBlockEntity.OUTPUT_SLOT_COUNT
                        && outputs.get(0).is(Items.APPLE) && outputs.get(0).getCount() == 3
                        && outputs.get(1).isEmpty()
                        && outputs.get(2).is(Items.CARROT) && outputs.get(2).getCount() == 5,
                "Millstone lost Forge output slots, counts, or empty-slot order");
        helper.assertTrue(millstone.getCarrier().isPresent()
                        && millstone.getCarrier().orElseThrow().test(Items.BOWL.getDefaultInstance()),
                "Millstone did not read a legacy JSON CarrierIngredient");

        CompoundTag saved = millstone.saveCustomOnly(helper.getLevel().registryAccess());
        BlockPos reloadedPos = new BlockPos(2, 1, 1);
        helper.setBlock(reloadedPos, ModBlocks.MILLSTONE);
        MillstoneBlockEntity reloaded = helper.getBlockEntity(reloadedPos, MillstoneBlockEntity.class);
        reloaded.loadCustomOnly(valueInput(helper, saved));
        List<ItemStack> reloadedOutputs = reloaded.getOutputs();
        helper.assertTrue(reloadedOutputs.get(0).is(Items.APPLE)
                        && reloadedOutputs.get(0).getCount() == 3
                        && reloadedOutputs.get(1).isEmpty()
                        && reloadedOutputs.get(2).is(Items.CARROT)
                        && reloadedOutputs.get(2).getCount() == 5,
                "Millstone four-slot outputs changed after a Fabric save and reload");

        RegistryOps<net.minecraft.nbt.Tag> nbtOps = RegistryOps.create(
                NbtOps.INSTANCE, helper.getLevel().registryAccess());
        CompoundTag interimFabricTag = new CompoundTag();
        interimFabricTag.put("OutputItem", ItemStack.CODEC.encodeStart(
                nbtOps, new ItemStack(Items.POTATO, 7)).getOrThrow());
        reloaded.loadCustomOnly(valueInput(helper, interimFabricTag));
        helper.assertTrue(reloaded.getOutputs().getFirst().is(Items.POTATO)
                        && reloaded.getOutputs().getFirst().getCount() == 7,
                "Millstone did not migrate the interim Fabric single-output save format");
        helper.succeed();
    }

    @GameTest
    public void kitchenBlockEntitiesReadAndResaveForgeStacks(GameTestHelper helper) {
        BlockPos potPos = new BlockPos(1, 1, 1);
        helper.setBlock(potPos, ModBlocks.POT);
        PotBlockEntity pot = helper.getBlockEntity(potPos, PotBlockEntity.class);
        CompoundTag potTag = new CompoundTag();
        potTag.put("Inputs", legacyItemHandler(9, Map.of(
                0, legacyStack(Items.BEEF, 2),
                8, legacyStack(Items.CARROT, 3))));
        potTag.putString("Carrier", "{\"item\":\"minecraft:bowl\"}");
        potTag.put("Result", legacyStack(Items.RABBIT_STEW, 2));
        potTag.putInt("Status", PotBlockEntity.FINISHED);
        potTag.putInt("CurrentTick", 37);
        potTag.putInt("StirFryCount", 4);
        potTag.putLong("Seed", 987654321L);
        pot.loadCustomOnly(valueInput(helper, potTag));
        helper.assertTrue(pot.getInputs().get(0).is(Items.BEEF)
                        && pot.getInputs().get(0).getCount() == 2
                        && pot.getInputs().get(8).is(Items.CARROT)
                        && pot.getInputs().get(8).getCount() == 3
                        && pot.getResult().is(Items.RABBIT_STEW)
                        && pot.getResult().getCount() == 2,
                "Pot lost Forge inputs, slot order, or result");
        helper.assertTrue(pot.hasCarrier() && pot.getStatus() == PotBlockEntity.FINISHED
                        && pot.getCurrentTick() == 37 && pot.getSeed() == 987654321L,
                "Pot lost Forge Carrier or cooking state");
        CompoundTag savedPot = pot.saveCustomOnly(helper.getLevel().registryAccess());
        PotBlockEntity reloadedPot = new PotBlockEntity(potPos, ModBlocks.POT.defaultBlockState());
        reloadedPot.loadCustomOnly(valueInput(helper, savedPot));
        helper.assertTrue(reloadedPot.getInputs().get(8).is(Items.CARROT)
                        && reloadedPot.getResult().is(Items.RABBIT_STEW) && reloadedPot.hasCarrier(),
                "Pot changed its migrated inventory or Carrier after saving");

        BlockPos stockpotPos = new BlockPos(2, 1, 1);
        helper.setBlock(stockpotPos, ModBlocks.STOCKPOT);
        StockpotBlockEntity stockpot = helper.getBlockEntity(stockpotPos, StockpotBlockEntity.class);
        CompoundTag stockpotTag = new CompoundTag();
        stockpotTag.put("Inputs", legacyItemHandler(9, Map.of(
                1, legacyStack(Items.POTATO, 4),
                7, legacyStack(Items.BEETROOT, 2))));
        stockpotTag.putString("RecipeId", "kaleidoscope_cookery:stockpot/legacy_fixture");
        stockpotTag.putString("SoupBaseId", "minecraft:water_bucket");
        stockpotTag.put("Result", legacyStack(Items.BEETROOT_SOUP, 3));
        stockpotTag.putInt("Status", 3);
        stockpotTag.putInt("CurrentTick", 91);
        stockpotTag.putInt("TakeoutCount", 6);
        stockpotTag.put("LidItem", legacyStack(Items.IRON_INGOT, 1));
        stockpot.loadCustomOnly(valueInput(helper, stockpotTag));
        helper.assertTrue(stockpot.getInputs().get(1).is(Items.POTATO)
                        && stockpot.getInputs().get(1).getCount() == 4
                        && stockpot.getInputs().get(7).is(Items.BEETROOT)
                        && stockpot.getResult().is(Items.BEETROOT_SOUP)
                        && stockpot.getResult().getCount() == 3
                        && stockpot.getLidItem().is(Items.IRON_INGOT),
                "Stockpot lost Forge inputs, result, lid, or slot order");
        helper.assertTrue(stockpot.getTakeoutCount() == 6
                        && stockpot.getSoupBaseId().equals(Identifier.fromNamespaceAndPath("minecraft", "water")),
                "Stockpot lost its takeout count or legacy soup-base alias migration");
        StockpotBlockEntity reloadedStockpot = new StockpotBlockEntity(
                stockpotPos, ModBlocks.STOCKPOT.defaultBlockState());
        reloadedStockpot.loadCustomOnly(valueInput(helper,
                stockpot.saveCustomOnly(helper.getLevel().registryAccess())));
        helper.assertTrue(reloadedStockpot.getInputs().get(7).is(Items.BEETROOT)
                        && reloadedStockpot.getResult().is(Items.BEETROOT_SOUP)
                        && reloadedStockpot.getLidItem().is(Items.IRON_INGOT),
                "Stockpot changed migrated stacks after saving");

        BlockPos teapotPos = new BlockPos(3, 1, 1);
        helper.setBlock(teapotPos, ModBlocks.TEAPOT);
        TeapotBlockEntity teapot = helper.getBlockEntity(teapotPos, TeapotBlockEntity.class);
        CompoundTag teapotTag = new CompoundTag();
        teapotTag.put("Input", legacyStack(Items.WHEAT_SEEDS, 5));
        teapotTag.putString("TeaFluidId", "minecraft:water");
        teapotTag.put("Result", legacyStack(Items.POTION, 2));
        teapotTag.putInt("Status", 2);
        teapotTag.putInt("CurrentTick", 55);
        teapot.loadCustomOnly(valueInput(helper, teapotTag));
        helper.assertTrue(teapot.getInput().is(Items.WHEAT_SEEDS) && teapot.getInput().getCount() == 5
                        && teapot.getResult().is(Items.POTION) && teapot.getResult().getCount() == 2
                        && teapot.getTeaFluidId().equals(Identifier.fromNamespaceAndPath("minecraft", "water"))
                        && teapot.getStatus() == 2 && teapot.getCurrentTick() == 55,
                "Teapot lost Forge input, result, fluid, or processing state");
        TeapotBlockEntity reloadedTeapot = new TeapotBlockEntity(teapotPos, ModBlocks.TEAPOT.defaultBlockState());
        reloadedTeapot.loadCustomOnly(valueInput(helper,
                teapot.saveCustomOnly(helper.getLevel().registryAccess())));
        helper.assertTrue(reloadedTeapot.getInput().is(Items.WHEAT_SEEDS)
                        && reloadedTeapot.getResult().is(Items.POTION),
                "Teapot changed migrated stacks after saving");

        BlockPos boardPos = new BlockPos(4, 1, 1);
        helper.setBlock(boardPos, ModBlocks.CHOPPING_BOARD);
        ChoppingBoardBlockEntity board = helper.getBlockEntity(boardPos, ChoppingBoardBlockEntity.class);
        CompoundTag boardTag = new CompoundTag();
        boardTag.putString("ModelId", "kaleidoscope_cookery:block/chopping_board/legacy_fixture");
        boardTag.put("CurrentCutStack", legacyStack(Items.COD, 2));
        boardTag.put("ResultItem", legacyStack(Items.COOKED_COD, 3));
        boardTag.putInt("MaxCutCount", 4);
        boardTag.putInt("CurrentCutCount", 4);
        board.loadCustomOnly(valueInput(helper, boardTag));
        helper.assertTrue(board.getCurrentCutStack().is(Items.COD)
                        && board.getCurrentCutStack().getCount() == 2
                        && board.getStoredDrops().getFirst().is(Items.COOKED_COD)
                        && board.getStoredDrops().getFirst().getCount() == 3
                        && board.getMaxCutCount() == 4 && board.getCurrentCutCount() == 4,
                "Chopping board lost Forge stacks or cut progress");
        ChoppingBoardBlockEntity reloadedBoard = new ChoppingBoardBlockEntity(
                boardPos, ModBlocks.CHOPPING_BOARD.defaultBlockState());
        reloadedBoard.loadCustomOnly(valueInput(helper,
                board.saveCustomOnly(helper.getLevel().registryAccess())));
        helper.assertTrue(reloadedBoard.getCurrentCutStack().is(Items.COD)
                        && reloadedBoard.getStoredDrops().getFirst().is(Items.COOKED_COD),
                "Chopping board changed migrated stacks after saving");

        BlockPos racksPos = new BlockPos(5, 1, 1);
        helper.setBlock(racksPos, ModBlocks.KITCHENWARE_RACKS);
        KitchenwareRacksBlockEntity racks = helper.getBlockEntity(racksPos, KitchenwareRacksBlockEntity.class);
        CompoundTag racksTag = new CompoundTag();
        racksTag.put("LeftItem", legacyStack(Items.WOODEN_SHOVEL, 1));
        racksTag.put("RightItem", legacyStack(Items.IRON_SWORD, 1));
        racks.loadCustomOnly(valueInput(helper, racksTag));
        helper.assertTrue(racks.getItemLeft().is(Items.WOODEN_SHOVEL)
                        && racks.getItemRight().is(Items.IRON_SWORD),
                "Kitchenware racks lost Forge left/right slot order");
        KitchenwareRacksBlockEntity reloadedRacks = new KitchenwareRacksBlockEntity(
                racksPos, ModBlocks.KITCHENWARE_RACKS.defaultBlockState());
        reloadedRacks.loadCustomOnly(valueInput(helper,
                racks.saveCustomOnly(helper.getLevel().registryAccess())));
        helper.assertTrue(reloadedRacks.getItemLeft().is(Items.WOODEN_SHOVEL)
                        && reloadedRacks.getItemRight().is(Items.IRON_SWORD),
                "Kitchenware racks changed migrated slots after saving");

        BlockPos shawarmaPos = new BlockPos(6, 1, 1);
        helper.setBlock(shawarmaPos, ModBlocks.SHAWARMA_SPIT);
        ShawarmaSpitBlockEntity shawarma = helper.getBlockEntity(shawarmaPos, ShawarmaSpitBlockEntity.class);
        CompoundTag shawarmaTag = new CompoundTag();
        shawarmaTag.put("CookingItem", legacyStack(Items.BEEF, 6));
        shawarmaTag.put("CookedItem", legacyStack(Items.COOKED_BEEF, 6));
        shawarmaTag.putInt("CookTime", 123);
        shawarma.loadCustomOnly(valueInput(helper, shawarmaTag));
        helper.assertTrue(shawarma.getStoredItem().is(Items.BEEF)
                        && shawarma.getStoredItem().getCount() == 6 && shawarma.getCookTime() == 123,
                "Shawarma spit lost its Forge cooking stack or timer");
        shawarmaTag.remove("CookingItem");
        shawarma.loadCustomOnly(valueInput(helper, shawarmaTag));
        helper.assertTrue(shawarma.getStoredItem().is(Items.COOKED_BEEF)
                        && shawarma.getStoredItem().getCount() == 6,
                "Shawarma spit lost its Forge cooked output stack");
        ShawarmaSpitBlockEntity reloadedShawarma = new ShawarmaSpitBlockEntity(
                shawarmaPos, ModBlocks.SHAWARMA_SPIT.defaultBlockState());
        reloadedShawarma.loadCustomOnly(valueInput(helper,
                shawarma.saveCustomOnly(helper.getLevel().registryAccess())));
        helper.assertTrue(reloadedShawarma.getStoredItem().is(Items.COOKED_BEEF)
                        && reloadedShawarma.getStoredItem().getCount() == 6
                        && reloadedShawarma.getCookTime() == 123,
                "Shawarma spit changed its migrated output or timer after saving");
        helper.succeed();
    }

    @GameTest
    public void entitiesReadAndResaveForgePersistentData(GameTestHelper helper) {
        ScarecrowEntity scarecrow = new ScarecrowEntity(ModEntities.SCARECROW, helper.getLevel());
        CompoundTag shoulder = new CompoundTag();
        shoulder.putString("id", "minecraft:parrot");
        shoulder.putInt("Variant", 3);
        CompoundTag scarecrowTag = new CompoundTag();
        scarecrowTag.put("HandItems", legacyItemHandler(2, Map.of(
                0, legacyStack(Items.STICK, 2),
                1, legacyStack(Items.SHIELD, 1))));
        scarecrowTag.put("ArmorItems", legacyItemHandler(4, Map.of(
                0, legacyStack(Items.IRON_BOOTS, 1),
                3, legacyStack(Items.IRON_HELMET, 1))));
        scarecrowTag.put("ShoulderEntity", shoulder);
        scarecrow.readAdditionalSaveData(valueInput(helper, scarecrowTag));
        helper.assertTrue(scarecrow.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.STICK)
                        && scarecrow.getItemBySlot(EquipmentSlot.MAINHAND).getCount() == 2
                        && scarecrow.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.SHIELD)
                        && scarecrow.getItemBySlot(EquipmentSlot.FEET).is(Items.IRON_BOOTS)
                        && scarecrow.getItemBySlot(EquipmentSlot.HEAD).is(Items.IRON_HELMET),
                "Scarecrow lost Forge hand/armor handler stacks or slot order");
        helper.assertTrue(scarecrow.getShoulderEntity().getIntOr("Variant", -1) == 3,
                "Scarecrow lost its persisted shoulder entity");

        CompoundTag forgeParrotTag = shoulder.copy();
        forgeParrotTag.putFloat("Health", 4.5F);
        forgeParrotTag.putIntArray("UUID", UUIDUtil.uuidToIntArray(
                UUID.fromString("aaaaaaaa-bbbb-cccc-dddd-eeeeeeeeeeee")));
        var restoredShoulder = net.minecraft.world.entity.EntityType.create(
                        valueInput(helper, forgeParrotTag), helper.getLevel(),
                        new EntitySpawnRequest(EntitySpawnReason.LOAD, false))
                .orElseThrow(() -> helper.assertionException(
                        "Forge 1.20.1 ShoulderEntity could not be created by the current release path"));
        helper.assertTrue(restoredShoulder instanceof Parrot parrot
                        && parrot.getVariant() == Parrot.Variant.YELLOW_BLUE
                        && parrot.getHealth() == 4.5F,
                "Forge 1.20.1 ShoulderEntity lost its integer Variant or health while loading");

        TagValueOutput scarecrowOutput = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        scarecrow.addAdditionalSaveData(scarecrowOutput);
        ScarecrowEntity reloadedScarecrow = new ScarecrowEntity(ModEntities.SCARECROW, helper.getLevel());
        reloadedScarecrow.readAdditionalSaveData(valueInput(helper, scarecrowOutput.buildResult()));
        helper.assertTrue(reloadedScarecrow.getItemBySlot(EquipmentSlot.MAINHAND).is(Items.STICK)
                        && reloadedScarecrow.getItemBySlot(EquipmentSlot.HEAD).is(Items.IRON_HELMET)
                        && reloadedScarecrow.getShoulderEntity().getIntOr("Variant", -1) == 3,
                "Scarecrow changed migrated equipment or shoulder data after saving");

        CompoundTag replacement = new CompoundTag();
        replacement.put("HandItems", legacyItemHandler(2,
                Map.of(1, legacyStack(Items.TORCH, 1))));
        replacement.put("ArmorItems", legacyItemHandler(4, Map.of()));
        reloadedScarecrow.readAdditionalSaveData(valueInput(helper, replacement));
        helper.assertTrue(reloadedScarecrow.getItemBySlot(EquipmentSlot.MAINHAND).isEmpty()
                        && reloadedScarecrow.getItemBySlot(EquipmentSlot.OFFHAND).is(Items.TORCH)
                        && reloadedScarecrow.getItemBySlot(EquipmentSlot.HEAD).isEmpty(),
                "Scarecrow retained stale equipment while loading sparse Forge handlers");

        ThrowableBaoziEntity throwable = new ThrowableBaoziEntity(ModEntities.THROWABLE_BAOZI, helper.getLevel());
        CompoundTag throwableTag = new CompoundTag();
        throwableTag.put("Item", legacyStack(Items.APPLE, 1));
        throwable.readAdditionalSaveData(valueInput(helper, throwableTag));
        helper.assertTrue(throwable.getItem().is(Items.APPLE),
                "Throwable baozi replaced its Forge Item stack with the default baozi");
        TagValueOutput throwableOutput = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        throwable.saveWithoutId(throwableOutput);
        ThrowableBaoziEntity reloadedThrowable = new ThrowableBaoziEntity(
                ModEntities.THROWABLE_BAOZI, helper.getLevel());
        reloadedThrowable.readAdditionalSaveData(valueInput(helper, throwableOutput.buildResult()));
        helper.assertTrue(reloadedThrowable.getItem().is(Items.APPLE),
                "Throwable baozi changed its migrated Item stack after saving");

        SitEntity seat = new SitEntity(helper.getLevel(), helper.absolutePos(new BlockPos(1, 1, 1)),
                0.5, SitEntity.TRASH_CAN);
        TagValueOutput seatOutput = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        seat.saveWithoutId(seatOutput);
        CompoundTag seatTag = seatOutput.buildResult();
        helper.assertValueEqual(seatTag.getIntOr("SitType", -1), SitEntity.TRASH_CAN,
                "Seat no longer saves the legacy SitType integer field");
        SitEntity reloadedSeat = new SitEntity(ModEntities.SIT, helper.getLevel());
        reloadedSeat.load(valueInput(helper, seatTag));
        helper.assertValueEqual(reloadedSeat.getSitType(), SitEntity.TRASH_CAN,
                "Seat did not reload the legacy SitType value");
        helper.succeed();
    }

    @GameTest
    public void generalConfigImportsLegacyForgeToml(GameTestHelper helper) {
        GeneralConfig config = GeneralConfigTestAccess.fromLegacyToml("""
                [unrelated]
                SatiatedShieldAbsorbEnabled = true

                [cookery]
                SatiatedShieldAbsorbEnabled = false
                SatiatedShieldAbsorbExcessDamage = false
                IS_SATIATED_SHIELD_DISABLE_WHEN_HUNGRY_EFFECT = false
                SATIATED_SHIELD_MIN_FOOD_LEVEL = 7
                SATIATED_SHIELD_ADDITIONAL_EXHAUSTION_PER_DAMAGE = 3.5
                SATIATED_SHIELD_DAMAGE_REDUCTION_PERCENT = 0.75 # inline comment
                SATIATED_SHIELD_MAX_DAMAGE_REDUCTION = 48.0
                SATIATED_SHIELD_MIN_DAMAGE = 1.25
                SATIATED_SHIELD_WEAKNESS_DAMAGE_MULTIPLIER = 4.0
                """);

        helper.assertFalse(config.satiatedShieldAbsorbEnabled(),
                "Legacy absorb-enabled setting was not imported from [cookery]");
        helper.assertFalse(config.satiatedShieldAbsorbExcessDamage(),
                "Legacy excess-damage setting was not imported");
        helper.assertFalse(config.satiatedShieldDisableWhenHungryEffect(),
                "Legacy Hunger-effect setting was not imported");
        helper.assertValueEqual(config.satiatedShieldMinFoodLevel(), 7,
                "Legacy minimum food level was not imported");
        helper.assertValueEqual(config.satiatedShieldAdditionalExhaustionPerDamage(), 3.5,
                "Legacy exhaustion-per-damage value was not imported");
        helper.assertValueEqual(config.satiatedShieldDamageReductionPercent(), 0.75,
                "Legacy damage-reduction percentage was not imported");
        helper.assertValueEqual(config.satiatedShieldMaxDamageReduction(), 48.0,
                "Legacy maximum damage reduction was not imported");
        helper.assertValueEqual(config.satiatedShieldMinDamage(), 1.25,
                "Legacy minimum damage was not imported");
        helper.assertValueEqual(config.satiatedShieldWeaknessDamageMultiplier(), 4.0,
                "Legacy weakness multiplier was not imported");

        GeneralConfig invalid = GeneralConfigTestAccess.fromLegacyToml("""
                [cookery]
                SATIATED_SHIELD_MIN_FOOD_LEVEL = 0
                SATIATED_SHIELD_ADDITIONAL_EXHAUSTION_PER_DAMAGE = 41.0
                SATIATED_SHIELD_DAMAGE_REDUCTION_PERCENT = 1.5
                SATIATED_SHIELD_MAX_DAMAGE_REDUCTION = -1.0
                SATIATED_SHIELD_MIN_DAMAGE = -1.0
                SATIATED_SHIELD_WEAKNESS_DAMAGE_MULTIPLIER = 0.5
                """);
        helper.assertValueEqual(invalid.satiatedShieldMinFoodLevel(), 4,
                "Out-of-range legacy values did not fall back to Forge defaults");
        helper.assertValueEqual(invalid.satiatedShieldAdditionalExhaustionPerDamage(), 2.0,
                "Out-of-range exhaustion value did not fall back to the Forge default");
        helper.assertValueEqual(invalid.satiatedShieldDamageReductionPercent(), 1.0,
                "Out-of-range reduction percentage did not fall back to the Forge default");
        helper.assertValueEqual(invalid.satiatedShieldMaxDamageReduction(), 64.0,
                "Out-of-range maximum reduction did not fall back to the Forge default");
        helper.assertValueEqual(invalid.satiatedShieldMinDamage(), 0.0,
                "Out-of-range minimum damage did not fall back to the Forge default");
        helper.assertValueEqual(invalid.satiatedShieldWeaknessDamageMultiplier(), 2.0,
                "Out-of-range weakness multiplier did not fall back to the Forge default");
        helper.succeed();
    }

    @GameTest
    public void clientTooltipConfigAndOptionalModPreserveForgeBehavior(GameTestHelper helper) {
        ClientConfig disabled = ClientConfigTestAccess.fromLegacyToml("""
                [unrelated]
                ShowFoodEffectTooltips = true

                [cookery]
                ShowFoodEffectTooltips = false # preserve the Forge client key
                """);
        helper.assertFalse(disabled.showFoodEffectTooltips(),
                "Legacy Forge client tooltip setting was not imported from [cookery]");

        ClientConfig invalid = ClientConfigTestAccess.fromLegacyToml("""
                [cookery]
                ShowFoodEffectTooltips = sometimes
                """);
        helper.assertTrue(invalid.showFoodEffectTooltips(),
                "Invalid legacy tooltip setting did not fall back to the Forge default");
        helper.assertTrue(FoodEffectTooltipsCompatTestAccess.shouldShowCookeryEffectTooltips(true, false),
                "Cookery effect tooltips were hidden without either suppression condition");
        helper.assertFalse(FoodEffectTooltipsCompatTestAccess.shouldShowCookeryEffectTooltips(false, false),
                "Disabled client config still showed Cookery effect tooltips");
        helper.assertFalse(FoodEffectTooltipsCompatTestAccess.shouldShowCookeryEffectTooltips(true, true),
                "Food Effect Tooltips installation did not suppress duplicate Cookery effect lines");
        helper.succeed();
    }

    @GameTest
    public void farmersDelightCookingRecipeCompatUsesTargetApiWhenInstalled(GameTestHelper helper) {
        if (!FabricLoader.getInstance().isModLoaded(FarmersDelightCompat.ID)) {
            helper.succeed();
            return;
        }

        record ExpectedRecipe(String path, String output, int ingredients, int time) {}
        List<ExpectedRecipe> expectedRecipes = List.of(
                new ExpectedRecipe("cooking/beef_stew", "beef_stew", 3, 200),
                new ExpectedRecipe("cooking/dumplings", "dumplings", 4, 200),
                new ExpectedRecipe("cooking/cabbage_rolls", "cabbage_rolls", 2, 100)
        );
        List<RecipeHolder<StockpotRecipe>> displayedRecipes = new ArrayList<>();
        FarmersDelightCompat.appendStockpotRecipes(helper.getLevel(), displayedRecipes);

        for (ExpectedRecipe expected : expectedRecipes) {
            Identifier recipeId = Identifier.fromNamespaceAndPath("farmersdelight", expected.path());
            ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);
            RecipeHolder<?> raw = helper.getLevel().recipeAccess().byKey(recipeKey)
                    .orElseThrow(() -> helper.assertionException(
                            "Missing Farmer's Delight recipe " + recipeId));
            RecipeHolder<StockpotRecipe> converted = FarmersDelightCompat.tryTransformRecipeHolder(
                    raw, helper.getLevel());
            helper.assertTrue(converted != null,
                    "Farmer's Delight 26.2 CookingPotRecipe did not convert: " + recipeId);
            StockpotRecipe recipe = converted.value();
            helper.assertValueEqual(recipe.ingredients().size(), expected.ingredients(),
                    "Converted Farmer's Delight recipe changed its ingredient count: " + recipeId);
            helper.assertTrue(recipe.result().create().is(BuiltInRegistries.ITEM.getValue(
                            Identifier.fromNamespaceAndPath("farmersdelight", expected.output()))),
                    "Converted Farmer's Delight recipe changed its output: " + recipeId);
            helper.assertValueEqual(recipe.time(), expected.time(),
                    "Converted Farmer's Delight recipe changed its cooking time: " + recipeId);
            helper.assertTrue(recipe.carrier().isPresent()
                            && recipe.carrier().get().test(Items.BOWL.getDefaultInstance()),
                    "Converted Farmer's Delight recipe did not retain its bowl container: " + recipeId);

            List<ItemStack> inputs = recipe.ingredients().stream()
                    .map(ingredient -> ingredient.items().findFirst().orElseThrow().value().getDefaultInstance())
                    .toList();
            RecipeHolder<StockpotRecipe> matched = FarmersDelightCompat.findMatchingRecipe(
                    helper.getLevel(), new StockpotInput(inputs, StockpotRecipeSerializer.DEFAULT_SOUP_BASE));
            helper.assertTrue(matched != null && matched.id().identifier().equals(recipeId),
                    "Cookery stockpot input did not match the installed Farmer's Delight recipe: " + recipeId);
            helper.assertTrue(displayedRecipes.stream()
                            .anyMatch(holder -> holder.id().identifier().equals(recipeId)),
                    "Recipe viewers did not receive the converted Farmer's Delight recipe: " + recipeId);
        }
        helper.succeed();
    }

    @GameTest
    public void carryOnRoundTripPreservesSupportedCookeryData(GameTestHelper helper) {
        if (!CarryOnCompatTestAccess.isLoaded()) {
            helper.succeed();
            return;
        }
        helper.assertValueEqual(CarryOnCompatTestAccess.apiVersion(), 1,
                "Carry On integration is not running against public API 1");
        BlockPos sourcePos = new BlockPos(2, 2, 2);
        BlockPos targetPos = new BlockPos(5, 2, 2);
        prepareCarryOnPlacementArea(helper, sourcePos);
        prepareCarryOnPlacementArea(helper, targetPos);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.CREATIVE);
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setItemInHand(InteractionHand.OFF_HAND, ItemStack.EMPTY);

        List<Block> supportedBlocks = BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(entry -> entry.getKey().identifier().getNamespace().equals(KaleidoscopeCookery.MOD_ID))
                .filter(entry -> CARRY_ON_SAFE_BLOCK_PATHS.contains(entry.getKey().identifier().getPath()))
                .map(Map.Entry::getValue)
                .toList();
        helper.assertValueEqual(supportedBlocks.size(), CARRY_ON_SAFE_BLOCK_PATHS.size(),
                "Carry On runtime fixture did not resolve every declared safe Cookery block");
        for (Block block : supportedBlocks) {
            helper.setBlock(sourcePos, carryOnSourceState(block));
            BlockEntity sourceBlockEntity = helper.getLevel().getBlockEntity(helper.absolutePos(sourcePos));
            helper.assertTrue(sourceBlockEntity != null,
                    "Carry On safe block has no block entity: " + BuiltInRegistries.BLOCK.getKey(block));
            seedCarryOnPayload(helper, player, sourceBlockEntity);
            BlockState sourceState = helper.getBlockState(sourcePos);
            CompoundTag before = saveCarryOnBlockEntity(helper, sourceBlockEntity);

            moveIntoTest(helper, player, sourcePos.above());
            helper.assertTrue(CarryOnCompatTestAccess.tryPickUpBlock(
                            player, helper.absolutePos(sourcePos)),
                    "Carry On public API rejected safe Cookery block %s: %s".formatted(
                            BuiltInRegistries.BLOCK.getKey(block), CarryOnCompatTestAccess.lastResult()));
            helper.assertBlockPresent(Blocks.AIR, sourcePos);
            helper.assertTrue(CarryOnCompatTestAccess.isCarrying(player),
                    "Carry On removed the source without committing its attachment");

            moveIntoTest(helper, player, targetPos.above());
            helper.assertTrue(CarryOnCompatTestAccess.tryPlaceBlock(
                            player, helper.absolutePos(targetPos), Direction.UP),
                    "Carry On public API could not place %s: %s".formatted(
                            BuiltInRegistries.BLOCK.getKey(block), CarryOnCompatTestAccess.lastResult()));
            helper.assertFalse(CarryOnCompatTestAccess.isCarrying(player),
                    "Carry On placed the block without clearing its attachment");
            helper.assertBlockPresent(block, targetPos);
            BlockEntity placedBlockEntity = helper.getLevel().getBlockEntity(helper.absolutePos(targetPos));
            helper.assertTrue(placedBlockEntity != null,
                    "Carry On placement lost Cookery block entity: " + BuiltInRegistries.BLOCK.getKey(block));
            helper.assertValueEqual(saveCarryOnBlockEntity(helper, placedBlockEntity), before,
                    "Carry On changed Cookery block-entity data: " + BuiltInRegistries.BLOCK.getKey(block));
            assertCarryOnStateData(helper, sourceState, helper.getBlockState(targetPos), block);
            helper.setBlock(targetPos, Blocks.AIR);
        }

        for (Block unsafeBlock : List.of(
                ModBlocks.MILLSTONE,
                ModBlocks.STEAMER,
                ModBlocks.SHAWARMA_SPIT,
                ModBlocks.COLD_CUT_HAM_SLICES)) {
            helper.setBlock(sourcePos, unsafeBlock);
            BlockState before = helper.getBlockState(sourcePos);
            moveIntoTest(helper, player, sourcePos.above());
            helper.assertFalse(CarryOnCompatTestAccess.tryPickUpBlock(
                            player, helper.absolutePos(sourcePos)),
                    "Carry On public API picked up unsafe Cookery structure: "
                            + BuiltInRegistries.BLOCK.getKey(unsafeBlock));
            helper.assertValueEqual(helper.getBlockState(sourcePos), before,
                    "Carry On mutated an unsafe structure before rejecting it");
            helper.assertFalse(CarryOnCompatTestAccess.isCarrying(player),
                    "Rejected Carry On pickup left a duplicate attachment");
            helper.setBlock(sourcePos, Blocks.AIR);
        }
        helper.succeed();
    }

    @GameTest
    public void diggusMaximusPublicApiHonorsCookerySafetyBoundaries(GameTestHelper helper) {
        if (!DiggusMaximusCompatTestAccess.isLoaded()) {
            helper.succeed();
            return;
        }
        helper.assertValueEqual(DiggusMaximusCompatTestAccess.apiVersion(), 1,
                "Diggus Maximus integration is not running against public API 1");
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.DIAMOND_PICKAXE));
        moveIntoTest(helper, player, new BlockPos(2, 2, 1));

        List<BlockPos> oilPositions = List.of(
                new BlockPos(2, 1, 2), new BlockPos(3, 1, 2), new BlockPos(4, 1, 2));
        oilPositions.forEach(pos -> helper.setBlock(pos, ModBlocks.OIL_BLOCK));
        BlockPos absoluteOilSeed = helper.absolutePos(oilPositions.getFirst());
        helper.assertTrue(DiggusMaximusCompatTestAccess.canStart(player, absoluteOilSeed),
                "Diggus Maximus did not admit the explicitly included inert oil block");
        helper.assertTrue(DiggusMaximusCompatTestAccess.excavate(player, absoluteOilSeed),
                "Diggus Maximus public request rejected included oil blocks: "
                        + DiggusMaximusCompatTestAccess.lastResult());
        helper.assertValueEqual(DiggusMaximusCompatTestAccess.brokenBlocks(), oilPositions.size(),
                "Diggus Maximus did not break each connected oil block exactly once");
        oilPositions.forEach(pos -> helper.assertBlockPresent(Blocks.AIR, pos));
        int oilDrops = countItem(player, ModItems.OIL_BLOCK) + helper.getLevel().getEntitiesOfClass(ItemEntity.class,
                        new AABB(absoluteOilSeed).inflate(4.0)).stream()
                .filter(entity -> entity.getItem().is(ModItems.OIL_BLOCK))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
        helper.assertValueEqual(oilDrops, oilPositions.size(),
                "Diggus Maximus changed or duplicated vanilla oil-block drops");

        BlockPos potPos = new BlockPos(2, 1, 5);
        helper.setBlock(potPos, ModBlocks.POT);
        PotBlockEntity pot = helper.getBlockEntity(potPos, PotBlockEntity.class);
        pot.addAllIngredients(List.of(new ItemStack(Items.BEEF, 2), new ItemStack(Items.CARROT, 3)), player);
        CompoundTag potBefore = saveCarryOnBlockEntity(helper, pot);
        BlockPos absolutePot = helper.absolutePos(potPos);
        helper.assertFalse(DiggusMaximusCompatTestAccess.canStart(player, absolutePot),
                "Diggus Maximus preview admitted an excluded inventory block entity");
        helper.assertFalse(DiggusMaximusCompatTestAccess.excavate(player, absolutePot),
                "Diggus Maximus excavated an excluded inventory block entity");
        helper.assertBlockPresent(ModBlocks.POT, potPos);
        helper.assertValueEqual(saveCarryOnBlockEntity(helper,
                        helper.getBlockEntity(potPos, PotBlockEntity.class)), potBefore,
                "Rejected Diggus Maximus request changed pot inventory data");

        BlockPos millstoneCenter = new BlockPos(7, 1, 5);
        for (NinePart part : NinePart.values()) {
            helper.setBlock(millstoneCenter.offset(part.getPosX(), 0, part.getPosY()),
                    ModBlocks.MILLSTONE.defaultBlockState().setValue(MillstoneBlock.PART, part));
        }
        BlockPos edge = millstoneCenter.offset(NinePart.LEFT_UP.getPosX(), 0, NinePart.LEFT_UP.getPosY());
        helper.assertFalse(DiggusMaximusCompatTestAccess.excavate(player, helper.absolutePos(edge)),
                "Diggus Maximus excavated one edge of a Cookery multiblock");
        for (NinePart part : NinePart.values()) {
            BlockPos partPos = millstoneCenter.offset(part.getPosX(), 0, part.getPosY());
            helper.assertBlockPresent(ModBlocks.MILLSTONE, partPos);
            helper.assertValueEqual(helper.getBlockState(partPos).getValue(MillstoneBlock.PART), part,
                    "Rejected Diggus Maximus request changed a millstone part");
        }
        helper.succeed();
    }

    @GameTest
    public void wthitPublicApiRegistersAndSynchronizesMinimalServerData(GameTestHelper helper) {
        if (!WthitCompatTestAccess.isLoaded()) {
            helper.succeed();
            return;
        }
        helper.assertValueEqual(WthitCompatTestAccess.registrationSummary(), "13:11",
                "WTHIT common plugin did not register all feature controls and server providers");

        BlockPos basketPos = new BlockPos(2, 1, 2);
        helper.setBlock(basketPos, ModBlocks.FRUIT_BASKET);
        FruitBasketBlockEntity basket = helper.getBlockEntity(basketPos, FruitBasketBlockEntity.class);
        helper.assertTrue(basket.putOn(new ItemStack(Items.APPLE, 3), false),
                "Could not seed WTHIT fruit-basket fixture");
        CompoundTag basketRaw = WthitCompatTestAccess.capture(basket);
        CompoundTag basketData = basketRaw.getCompound("kaleidoscope_cookery")
                .orElseThrow(() -> helper.assertionException("WTHIT did not write namespaced basket data"));
        helper.assertValueEqual(basketData.getString("kind").orElse(""), "storage",
                "WTHIT sent the wrong basket payload type");
        helper.assertValueEqual(basketData.getInt("items_count").orElse(0), 1,
                "WTHIT did not filter the basket to its visible stacks");
        ItemStack basketStack = basketData.read("items_0", ItemStack.CODEC).orElse(ItemStack.EMPTY);
        helper.assertTrue(basketStack.is(Items.APPLE) && basketStack.getCount() == 3,
                "WTHIT changed the visible basket stack");
        helper.assertFalse(basketData.contains(FruitBasketBlockEntity.ITEMS),
                "WTHIT leaked the basket's complete persistence payload");

        BlockPos boardPos = new BlockPos(4, 1, 2);
        helper.setBlock(boardPos, ModBlocks.CHOPPING_BOARD);
        ChoppingBoardBlockEntity board = helper.getBlockEntity(boardPos, ChoppingBoardBlockEntity.class);
        CompoundTag boardFixture = new CompoundTag();
        boardFixture.putString("ModelId", "kaleidoscope_cookery:block/chopping_board/private_model");
        boardFixture.put("CurrentCutStack", legacyStack(Items.SALMON, 2));
        boardFixture.put("ResultItem", legacyStack(Items.COOKED_SALMON, 3));
        boardFixture.putInt("MaxCutCount", 5);
        boardFixture.putInt("CurrentCutCount", 2);
        board.loadCustomOnly(valueInput(helper, boardFixture));
        CompoundTag boardData = WthitCompatTestAccess.capture(board)
                .getCompound("kaleidoscope_cookery")
                .orElseThrow(() -> helper.assertionException("WTHIT did not write namespaced chopping data"));
        helper.assertValueEqual(boardData.getInt("cut_count").orElse(-1), 2,
                "WTHIT changed chopping-board progress");
        helper.assertValueEqual(boardData.getInt("max_cut_count").orElse(-1), 5,
                "WTHIT changed chopping-board maximum progress");
        helper.assertFalse(boardData.contains("ModelId") || boardData.contains("ResultItem"),
                "WTHIT leaked non-display chopping-board NBT");
        helper.succeed();
    }

    @GameTest
    public void optionalCompatibilityTagsPreserveForgeMembership(GameTestHelper helper) {
        helper.assertTrue(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityTypes.COD)
                        .is(TagMod.RICE_GROWTH_BOOSTER),
                "Cod no longer qualifies as a rice growth booster");
        helper.assertFalse(BuiltInRegistries.ENTITY_TYPE.wrapAsHolder(EntityTypes.COW)
                        .is(TagMod.RICE_GROWTH_BOOSTER),
                "Non-aquatic mobs unexpectedly qualify as rice growth boosters");

        TagKey<Block> carryOnBlacklist = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("carryon", "block_blacklist"));
        TagKey<Block> carryOnWhitelist = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("carryon", "block_whitelist"));
        List<Identifier> incorrectlyClassifiedCarryOnBlocks = BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(entry -> entry.getKey().identifier().getNamespace().equals(KaleidoscopeCookery.MOD_ID))
                .filter(entry -> {
                    boolean expectedSafe = CARRY_ON_SAFE_BLOCK_PATHS.contains(entry.getKey().identifier().getPath());
                    BlockState state = entry.getValue().defaultBlockState();
                    return state.is(carryOnWhitelist) != expectedSafe || state.is(carryOnBlacklist) == expectedSafe;
                })
                .map(entry -> entry.getKey().identifier())
                .toList();
        helper.assertTrue(incorrectlyClassifiedCarryOnBlocks.isEmpty(),
                "Carry On safe/unsafe tags misclassified Cookery blocks: %s"
                        .formatted(incorrectlyClassifiedCarryOnBlocks));

        TagKey<Block> relocationNotSupported = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("c", "relocation_not_supported"));
        TagKey<Block> diggusIncluded = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("diggusmaximus", "included_blocks"));
        TagKey<Block> diggusExcluded = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("diggusmaximus", "excluded_blocks"));
        List<Identifier> incorrectlyClassifiedSafetyBlocks = BuiltInRegistries.BLOCK.entrySet().stream()
                .filter(entry -> entry.getKey().identifier().getNamespace().equals(KaleidoscopeCookery.MOD_ID))
                .filter(entry -> {
                    String path = entry.getKey().identifier().getPath();
                    boolean carrySafe = CARRY_ON_SAFE_BLOCK_PATHS.contains(path);
                    boolean diggusSafe = path.equals("oil_block") || path.equals("straw_block");
                    BlockState state = entry.getValue().defaultBlockState();
                    return state.is(relocationNotSupported) == carrySafe
                            || state.is(diggusIncluded) != diggusSafe
                            || state.is(diggusExcluded) == diggusSafe;
                })
                .map(entry -> entry.getKey().identifier())
                .toList();
        helper.assertTrue(incorrectlyClassifiedSafetyBlocks.isEmpty(),
                "Carry On hard-deny or Diggus Maximus safety tags misclassified Cookery blocks: %s"
                        .formatted(incorrectlyClassifiedSafetyBlocks));

        TagKey<Block> springCrops = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("sereneseasons", "spring_crops"));
        TagKey<Block> summerCrops = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("sereneseasons", "summer_crops"));
        TagKey<Block> autumnCrops = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("sereneseasons", "autumn_crops"));
        helper.assertTrue(ModBlocks.LETTUCE_CROP.defaultBlockState().is(springCrops),
                "Serene Seasons spring crops lost lettuce");
        helper.assertTrue(ModBlocks.TOMATO_CROP.defaultBlockState().is(summerCrops)
                        && ModBlocks.CHILI_CROP.defaultBlockState().is(summerCrops)
                        && ModBlocks.RICE_CROP.defaultBlockState().is(summerCrops),
                "Serene Seasons summer crops lost tomato, chili, or rice");
        helper.assertTrue(ModBlocks.TOMATO_CROP.defaultBlockState().is(autumnCrops)
                        && ModBlocks.CHILI_CROP.defaultBlockState().is(autumnCrops)
                        && ModBlocks.RICE_CROP.defaultBlockState().is(autumnCrops)
                        && ModBlocks.LETTUCE_CROP.defaultBlockState().is(autumnCrops),
                "Serene Seasons autumn crops lost a Cookery crop");

        TagKey<Item> eggs = TagKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath("c", "eggs"));
        TagKey<Item> rawMeats = TagKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath("c", "raw_meats"));
        TagKey<Item> seeds = TagKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath("c", "seeds"));
        TagKey<Item> commonKnives = TagKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath("c", "tools/knives"));
        helper.assertTrue(ModItems.FRIED_EGG.getDefaultInstance().is(eggs),
                "The c:eggs migration lost the Forge-baseline fried egg");
        helper.assertTrue(ModItems.RAW_CUT_SMALL_MEATS.getDefaultInstance().is(rawMeats),
                "The c:raw_meats migration lost the Forge-baseline small meat cuts");
        helper.assertTrue(ModItems.WILD_RICE_SEED.getDefaultInstance().is(seeds)
                        && ModItems.RICE_SEED.getDefaultInstance().is(seeds),
                "The c:seeds migration lost wild rice or rice");
        helper.assertTrue(ModItems.IRON_KITCHEN_KNIFE.getDefaultInstance().is(commonKnives)
                        && ModItems.GOLD_KITCHEN_KNIFE.getDefaultInstance().is(commonKnives)
                        && ModItems.DIAMOND_KITCHEN_KNIFE.getDefaultInstance().is(commonKnives)
                        && ModItems.NETHERITE_KITCHEN_KNIFE.getDefaultInstance().is(commonKnives),
                "The c:tools/knives migration lost a Cookery kitchen knife");

        TagKey<Block> ultimineExcluded = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("ftbultimine", "excluded_blocks"));
        TagKey<Block> ultimineSingleCropBlacklist = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath(
                        "ftbultimine", "single_crop_harvesting_blacklist"));
        helper.assertTrue(ModBlocks.RICE_CROP.defaultBlockState().is(ultimineExcluded)
                        && ModBlocks.RICE_CROP.defaultBlockState().is(ultimineSingleCropBlacklist),
                "FTB Ultimine tags no longer protect the multi-block rice crop");

        TagKey<Block> heatSources = TagKey.create(
                Registries.BLOCK, Identifier.fromNamespaceAndPath("farmersdelight", "heat_sources"));
        TagKey<Item> knives = TagKey.create(
                Registries.ITEM, Identifier.fromNamespaceAndPath("farmersdelight", "tools/knives"));
        helper.assertTrue(ModBlocks.STOVE.defaultBlockState().is(heatSources),
                "Farmer's Delight heat sources lost the Cookery stove");
        helper.assertTrue(ModItems.IRON_KITCHEN_KNIFE.getDefaultInstance().is(knives)
                        && ModItems.GOLD_KITCHEN_KNIFE.getDefaultInstance().is(knives)
                        && ModItems.DIAMOND_KITCHEN_KNIFE.getDefaultInstance().is(knives)
                        && ModItems.NETHERITE_KITCHEN_KNIFE.getDefaultInstance().is(knives),
                "Farmer's Delight knife tag lost a Cookery kitchen knife");
        helper.succeed();
    }

    @GameTest
    public void oilBlockPreservesForgeNonStickyPistonBehavior(GameTestHelper helper) {
        try {
            Method isSticky = PistonStructureResolver.class.getDeclaredMethod("isSticky", BlockState.class);
            Method canStick = PistonStructureResolver.class.getDeclaredMethod(
                    "canStickToEachOther", BlockState.class, BlockState.class);
            isSticky.setAccessible(true);
            canStick.setAccessible(true);

            BlockState oil = ModBlocks.OIL_BLOCK.defaultBlockState();
            helper.assertFalse((boolean) isSticky.invoke(null, oil),
                    "Oil block became sticky even though both Forge baselines return false");
            for (BlockState other : List.of(
                    Blocks.STONE.defaultBlockState(),
                    Blocks.SLIME_BLOCK.defaultBlockState(),
                    Blocks.HONEY_BLOCK.defaultBlockState())) {
                helper.assertFalse((boolean) canStick.invoke(null, oil, other),
                        "Oil block stuck to " + other.getBlock());
                helper.assertFalse((boolean) canStick.invoke(null, other, oil),
                        "Block stuck to oil in the reverse argument order: " + other.getBlock());
            }
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not inspect Minecraft 26.2 piston adhesion methods", exception);
        }
        helper.succeed();
    }

    @GameTest
    public void scarecrowOnlyPreventsEntityFarmlandTrampling(GameTestHelper helper) {
        BlockPos farmlandPos = new BlockPos(1, 1, 1);
        BlockPos absoluteFarmlandPos = helper.absolutePos(farmlandPos);
        helper.spawn(ModEntities.SCARECROW, new BlockPos(3, 1, 1));

        helper.setBlock(farmlandPos, Blocks.FARMLAND.defaultBlockState());
        FarmlandBlock.turnToDirt(
                null, helper.getBlockState(farmlandPos), helper.getLevel(), absoluteFarmlandPos);
        helper.assertBlockPresent(Blocks.DIRT, farmlandPos);

        helper.setBlock(farmlandPos, Blocks.FARMLAND.defaultBlockState());
        Player trampler = helper.makeMockPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, trampler, new BlockPos(1, 2, 1));
        FarmlandBlock.turnToDirt(
                trampler, helper.getBlockState(farmlandPos), helper.getLevel(), absoluteFarmlandPos);
        helper.assertBlockPresent(Blocks.FARMLAND, farmlandPos);
        helper.succeed();
    }

    @GameTest
    public void droppedFlourHydratesEveryTenTicksWithoutLosingCount(GameTestHelper helper) {
        BlockPos waterPos = new BlockPos(1, 1, 1);
        BlockPos absoluteWaterPos = helper.absolutePos(waterPos);
        helper.setBlock(waterPos, Blocks.WATER);
        ItemEntity flour = new ItemEntity(
                helper.getLevel(),
                absoluteWaterPos.getX() + 0.5,
                absoluteWaterPos.getY() + 0.2,
                absoluteWaterPos.getZ() + 0.5,
                new ItemStack(ModItems.FLOUR, 7));
        flour.setDeltaMovement(Vec3.ZERO);
        helper.assertTrue(helper.getLevel().addFreshEntity(flour),
                "Flour item entity could not be added to the test world");

        helper.runAfterDelay(12, () -> {
            helper.assertTrue(flour.getItem().is(ModItems.RAW_DOUGH),
                    "Flour did not hydrate after its tenth item-entity tick");
            helper.assertValueEqual(flour.getItem().getCount(), 7,
                    "Flour hydration changed the dropped stack count");
            helper.succeed();
        });
    }

    @GameTest
    public void fallingSteamerTimeoutDropsBothLayersAndDiscardsEntity(GameTestHelper helper) {
        BlockPos sourcePos = new BlockPos(3, 4, 3);
        BlockPos absoluteSourcePos = helper.absolutePos(sourcePos);
        BlockState state = ModBlocks.STEAMER.defaultBlockState().setValue(SteamerBlock.HALF, false);
        helper.setBlock(sourcePos, state);

        FallingBlockEntity falling = FallingBlockEntity.fall(helper.getLevel(), absoluteSourcePos, state);
        CompoundTag data = legacyItemHandler(8, Map.of(
                0, legacyStack(Items.POTATO, 2),
                6, legacyStack(Items.CARROT, 3)));
        data.putIntArray(SteamerBlockEntity.COOKING_PROGRESS_TAG,
                new int[]{11, 0, 0, 0, 0, 0, 17, 0});
        data.putIntArray(SteamerBlockEntity.COOKING_TIME_TAG,
                new int[]{40, 0, 0, 0, 0, 0, 80, 0});
        falling.blockData = data;
        falling.time = 600;
        falling.setNoGravity(true);

        falling.tick();

        helper.assertTrue(falling.isRemoved(),
                "Cancelling the timeout drop left the falling steamer entity alive");
        List<ItemStack> steamerDrops = helper.getLevel().getEntitiesOfClass(
                        ItemEntity.class, new AABB(absoluteSourcePos).inflate(3.0),
                        entity -> entity.getItem().is(ModItems.STEAMER))
                .stream().map(ItemEntity::getItem).toList();
        helper.assertValueEqual(steamerDrops.size(), 2,
                "A full falling steamer did not split into two item drops");

        boolean foundLowerLayer = false;
        boolean foundUpperLayer = false;
        for (ItemStack drop : steamerDrops) {
            TypedEntityData<?> typedData = drop.get(DataComponents.BLOCK_ENTITY_DATA);
            helper.assertTrue(typedData != null, "A non-empty steamer layer lost its block-entity data");
            helper.assertValueEqual(drop.get(DataComponents.MAX_STACK_SIZE), 1,
                    "A filled steamer drop lost its single-stack component");
            CompoundTag dropTag = typedData.copyTagWithoutId();
            NonNullList<ItemStack> items = NonNullList.withSize(4, ItemStack.EMPTY);
            LegacyItemStackCompat.loadAllItems(valueInput(helper, dropTag), items);
            int[] progress = dropTag.getIntArray(SteamerBlockEntity.COOKING_PROGRESS_TAG).orElseThrow();
            int[] times = dropTag.getIntArray(SteamerBlockEntity.COOKING_TIME_TAG).orElseThrow();
            foundLowerLayer |= items.get(0).is(Items.POTATO)
                    && items.get(0).getCount() == 2 && progress[0] == 11 && times[0] == 40;
            foundUpperLayer |= items.get(2).is(Items.CARROT)
                    && items.get(2).getCount() == 3 && progress[2] == 17 && times[2] == 80;
        }
        helper.assertTrue(foundLowerLayer && foundUpperLayer,
                "Falling steamer drops did not preserve both layers and their cooking progress");
        helper.succeed();
    }

    @GameTest
    public void tundraStriderPreservesSpeedAndPowderSnowSemantics(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 2, 2));
        helper.setBlock(new BlockPos(2, 1, 2), Blocks.ICE);
        helper.assertFalse(PowderSnowBlock.canEntityWalkOnPowderSnow(player),
                "Control player unexpectedly walked on powder snow");

        player.getActiveEffectsMap().put(ModEffects.TUNDRA_STRIDER,
                new MobEffectInstance(ModEffects.TUNDRA_STRIDER, 200));
        helper.assertTrue(PowderSnowBlock.canEntityWalkOnPowderSnow(player),
                "Tundra Strider did not grant the Forge powder-snow exception");
        try {
            Method speedFactor = net.minecraft.world.entity.LivingEntity.class
                    .getDeclaredMethod("getBlockSpeedFactor");
            speedFactor.setAccessible(true);
            float actual = (float) speedFactor.invoke(player);
            float friction = Blocks.ICE.getFriction();
            float expected = 1.1F + Math.max(1.0F - friction, 0.0F) * 0.5F;
            helper.assertValueEqual(actual, expected,
                    "Tundra Strider speed factor no longer matches the Forge formula");
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not inspect the Minecraft 26.2 block speed factor", exception);
        }
        helper.succeed();
    }

    @GameTest
    public void projectileDodgeSkipsImpactAndPreservesEffectFlags(GameTestHelper helper) {
        Player target = helper.makeMockPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, target, new BlockPos(3, 1, 3));
        MobEffectInstance dodge = new MobEffectInstance(
                ModEffects.PROJECTILE_DODGE, 500, 2, true, false, false);
        target.getActiveEffectsMap().put(ModEffects.PROJECTILE_DODGE, dodge);
        ThrowableBaoziEntity projectile = new ThrowableBaoziEntity(helper.getLevel(), target);
        projectile.setPos(target.getX(), target.getY(), target.getZ());

        ProjectileDeflection result = invokeProjectileImpact(projectile, new EntityHitResult(target));
        MobEffectInstance remaining = target.getEffect(ModEffects.PROJECTILE_DODGE);
        helper.assertValueEqual(result, ProjectileDeflection.NONE,
                "Projectile dodge did not return the 26.2 skip-impact sentinel");
        helper.assertFalse(projectile.isRemoved(),
                "Projectile dodge still ran the original baozi impact");
        helper.assertTrue(remaining != null && remaining.getDuration() == 300,
                "Projectile dodge did not consume exactly 200 effect ticks");
        helper.assertTrue(remaining.getAmplifier() == 2 && remaining.isAmbient()
                        && !remaining.isVisible() && !remaining.showIcon(),
                "Projectile dodge changed the effect amplifier or display flags");

        Player control = helper.makeMockPlayer(GameType.SURVIVAL);
        ThrowableBaoziEntity controlProjectile = new ThrowableBaoziEntity(helper.getLevel(), control);
        invokeProjectileImpact(controlProjectile, new EntityHitResult(control));
        helper.assertTrue(controlProjectile.isRemoved(),
                "Projectile impact was skipped without the dodge effect");
        helper.succeed();
    }

    @GameTest
    public void villagePoolsContainKitchenStructuresAtForgeWeight(GameTestHelper helper) {
        Map<String, String> kitchens = Map.of(
                "village/plains/houses", "village/houses/plains_kitchen",
                "village/snowy/houses", "village/houses/snowy_kitchen",
                "village/savanna/houses", "village/houses/savanna_kitchen",
                "village/desert/houses", "village/houses/desert_kitchen",
                "village/taiga/houses", "village/houses/taiga_kitchen");
        var templatePools = helper.getLevel().registryAccess().lookupOrThrow(Registries.TEMPLATE_POOL);
        for (Map.Entry<String, String> kitchen : kitchens.entrySet()) {
            StructureTemplatePool pool = templatePools.getValueOrThrow(ResourceKey.create(
                    Registries.TEMPLATE_POOL, vanillaId(kitchen.getKey())));
            Identifier structureId = id(kitchen.getValue());
            long expandedWeight = pool.templates.stream()
                    .filter(element -> element instanceof SinglePoolElement single
                            && single.getTemplateLocation().equals(structureId))
                    .count();
            long rawEntries = pool.rawTemplates.stream()
                    .filter(entry -> entry.getFirst() instanceof SinglePoolElement single
                            && single.getTemplateLocation().equals(structureId)
                            && entry.getSecond() == 4)
                    .count();
            helper.assertValueEqual(expandedWeight, 4L,
                    "Village kitchen expanded weight changed for " + kitchen.getKey());
            helper.assertValueEqual(rawEntries, 1L,
                    "Village kitchen raw pool entry changed for " + kitchen.getKey());
        }
        helper.succeed();
    }

    @GameTest
    public void preservationRemovesOnlyFoodEffectsAfterConsumption(GameTestHelper helper) {
        ItemStack poisonousApple = new ItemStack(Items.APPLE);
        poisonousApple.set(DataComponents.CONSUMABLE, Consumable.builder()
                .consumeSeconds(0.05F)
                .onConsume(new ApplyStatusEffectsConsumeEffect(
                        new MobEffectInstance(MobEffects.POISON, 200)))
                .build());
        helper.assertTrue(!poisonousApple.is(TagMod.PRESERVATION_FOOD),
                "Preservation behavior test unexpectedly uses the narrowed compatibility tag");

        Player protectedPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        protectedPlayer.getActiveEffectsMap().put(ModEffects.PRESERVATION,
                new MobEffectInstance(ModEffects.PRESERVATION, 200));
        protectedPlayer.getActiveEffectsMap().put(MobEffects.HUNGER,
                new MobEffectInstance(MobEffects.HUNGER, 200));
        protectedPlayer.setItemInHand(InteractionHand.MAIN_HAND, poisonousApple.copy());
        protectedPlayer.startUsingItem(InteractionHand.MAIN_HAND);
        invokeCompleteUsingItem(protectedPlayer);

        helper.assertTrue(!protectedPlayer.hasEffect(MobEffects.POISON),
                "Preservation did not remove the harmful effect applied by completed food consumption");
        helper.assertTrue(protectedPlayer.hasEffect(MobEffects.HUNGER),
                "Preservation removed an unrelated pre-existing harmful effect");

        Player control = helper.makeMockPlayer(GameType.SURVIVAL);
        control.setItemInHand(InteractionHand.MAIN_HAND, poisonousApple.copy());
        control.startUsingItem(InteractionHand.MAIN_HAND);
        invokeCompleteUsingItem(control);
        helper.assertTrue(control.hasEffect(MobEffects.POISON),
                "The food control did not apply its harmful effect before Preservation handling");
        helper.succeed();
    }

    @GameTest
    public void farmerArmorUsesLivingEntityTickCadence(GameTestHelper helper) {
        Zombie armoredZombie = createFarmerArmorZombie(helper, new BlockPos(1, 1, 1), true);
        armoredZombie.tickCount = 20;
        armoredZombie.tick();
        MobEffectInstance dolphinsGrace = armoredZombie.getEffect(MobEffects.DOLPHINS_GRACE);
        helper.assertTrue(dolphinsGrace != null && dolphinsGrace.getDuration() == 25,
                "A non-player living entity did not receive the 25-tick full farmer armor effect at tick 20");

        Zombie incompleteZombie = createFarmerArmorZombie(helper, new BlockPos(3, 1, 1), false);
        incompleteZombie.tickCount = 20;
        incompleteZombie.tick();
        helper.assertTrue(!incompleteZombie.hasEffect(MobEffects.DOLPHINS_GRACE),
                "Incomplete farmer armor granted Dolphin's Grace to a non-player living entity");
        helper.succeed();
    }

    @GameTest
    public void trashCanPassengerCannotBecomeMobTarget(GameTestHelper helper) {
        BlockPos seatPos = helper.absolutePos(new BlockPos(1, 1, 1));
        SitEntity trashCanSeat = new SitEntity(helper.getLevel(), seatPos, 0.875, SitEntity.TRASH_CAN);
        helper.assertTrue(helper.getLevel().addFreshEntity(trashCanSeat),
                "Trash-can seat could not be added to the test world");
        Player hiddenPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        hiddenPlayer.setPos(Vec3.atCenterOf(seatPos));
        helper.assertTrue(hiddenPlayer.startRiding(trashCanSeat, true, true),
                "Test player could not mount the trash-can seat");

        Zombie zombie = new Zombie(helper.getLevel());
        zombie.setTarget(hiddenPlayer);
        helper.assertTrue(zombie.getTarget() == null,
                "Mob acquired a player already hiding in a trash can");

        Player visiblePlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        zombie.setTarget(visiblePlayer);
        helper.assertTrue(zombie.getTarget() == visiblePlayer,
                "Trash-can target prevention rejected a visible control player");
        zombie.setTarget(hiddenPlayer);
        helper.assertTrue(zombie.getTarget() == visiblePlayer,
                "Rejected trash-can targeting replaced the mob's existing target");
        helper.succeed();
    }

    @GameTest
    public void enamelBasinReturnsStoredOilAndBasin(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.ENAMEL_BASIN.defaultBlockState()
                .setValue(EnamelBasinBlock.OIL_COUNT, 13));
        BlockPos absolutePos = helper.absolutePos(pos);

        helper.assertTrue(helper.getLevel().destroyBlock(absolutePos, true),
                "Filled enamel basin could not be destroyed");
        List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(
                ItemEntity.class, new AABB(absolutePos).inflate(2.0));
        int oilCount = drops.stream()
                .filter(entity -> entity.getItem().is(ModItems.OIL))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();
        int basinCount = drops.stream()
                .filter(entity -> entity.getItem().is(ModItems.ENAMEL_BASIN))
                .mapToInt(entity -> entity.getItem().getCount())
                .sum();

        helper.assertValueEqual(oilCount, 13,
                "Breaking an enamel basin did not return every stored Forge oil unit");
        helper.assertValueEqual(basinCount, 1,
                "Breaking an enamel basin did not return the basin item");
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
    public void kitchenShovelUsesPotTakeoutResult(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POT);
        PotBlockEntity pot = helper.getBlockEntity(pos, PotBlockEntity.class);
        CompoundTag tag = pot.saveCustomOnly(helper.getLevel().registryAccess());
        tag.putInt("Status", PotBlockEntity.FINISHED);
        pot.loadCustomOnly(TagValueInput.create(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag));

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        player.setShiftKeyDown(true);
        ItemStack shovel = new ItemStack(ModItems.KITCHEN_SHOVEL);
        player.setItemInHand(InteractionHand.MAIN_HAND, shovel);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);

        InteractionResult result = ModItems.KITCHEN_SHOVEL.useOn(
                new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult));

        helper.assertValueEqual(result, InteractionResult.SUCCESS,
                "Kitchen shovel did not propagate the successful pot takeout result");
        helper.assertValueEqual(pot.getStatus(), PotBlockEntity.PUT_INGREDIENT,
                "Kitchen shovel takeout did not reset the finished pot");
        helper.assertTrue(player.getMainHandItem() == shovel && !shovel.isEmpty(),
                "Kitchen shovel takeout consumed or replaced the shovel");
        helper.succeed();
    }

    @GameTest
    public void potSupportsNativeEmptyHandIngredientRemoval(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POT);
        PotBlockEntity pot = helper.getBlockEntity(pos, PotBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack ingredient = Items.APPLE.getDefaultInstance();
        helper.assertTrue(pot.addIngredient(helper.getLevel(), player, ingredient),
                "Pot rejected the test ingredient");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);

        InteractionResult result = ((PotBlock) ModBlocks.POT).useWithoutItem(
                helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(result, InteractionResult.CONSUME,
                "Pot empty-hand ingredient removal did not report success");
        helper.assertTrue(player.getMainHandItem().is(Items.APPLE),
                "Pot empty-hand ingredient removal returned the wrong item");
        helper.assertTrue(pot.getInputs().stream().allMatch(ItemStack::isEmpty),
                "Pot retained the removed ingredient");
        helper.succeed();
    }

    @GameTest
    public void potUsesStackSuppliedToHeldInteraction(GameTestHelper helper) {
        BlockPos heatPos = new BlockPos(1, 1, 1);
        BlockPos potPos = heatPos.above();
        helper.setBlock(heatPos, Blocks.CAMPFIRE);
        helper.setBlock(potPos, ModBlocks.POT);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 2, 1));
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absolutePos = helper.absolutePos(potPos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        ItemStack oil = ModItems.OIL.getDefaultInstance();

        InteractionResult result = ((PotBlock) ModBlocks.POT).useItemOn(
                oil, helper.getBlockState(potPos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(result, InteractionResult.CONSUME,
                "Pot oil insertion did not report a server-side mutation");
        helper.assertTrue(oil.isEmpty() && player.getMainHandItem().is(Items.STONE),
                "Pot reread the player's hand instead of using the supplied stack");
        helper.assertTrue(helper.getBlockState(potPos).getValue(PotBlock.HAS_OIL),
                "Pot did not commit oil from the supplied stack");
        helper.succeed();
    }

    @GameTest
    public void stoveUsesStackSuppliedToHeldInteraction(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.STOVE);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(1, 1, 2));
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        StoveBlock block = (StoveBlock) ModBlocks.STOVE;
        ItemStack fireCharges = new ItemStack(Items.FIRE_CHARGE, 2);

        InteractionResult lightResult = block.useItemOn(
                fireCharges, helper.getBlockState(pos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(lightResult, InteractionResult.CONSUME,
                "Stove ignition did not report a server-side mutation");
        helper.assertTrue(helper.getBlockState(pos).getValue(StoveBlock.LIT)
                        && fireCharges.getCount() == 1
                        && player.getMainHandItem().is(Items.STONE),
                "Stove reread the player's hand instead of consuming the supplied fire charge");
        ItemStack shovel = Items.IRON_SHOVEL.getDefaultInstance();

        InteractionResult extinguishResult = block.useItemOn(
                shovel, helper.getBlockState(pos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(extinguishResult, InteractionResult.CONSUME,
                "Stove extinguishing did not report a server-side mutation");
        helper.assertFalse(helper.getBlockState(pos).getValue(StoveBlock.LIT),
                "Stove did not extinguish from the supplied shovel");
        helper.assertValueEqual(shovel.getDamageValue(), 1,
                "Stove did not damage the supplied extinguishing tool");
        helper.assertTrue(player.getMainHandItem().is(Items.STONE),
                "Stove mutated the player's unrelated held stack");
        helper.succeed();
    }

    @GameTest
    public void stockpotEmptyHandInteractionPrioritizesLidThenIngredient(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.STOCKPOT);
        StockpotBlockEntity stockpot = helper.getBlockEntity(pos, StockpotBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        helper.assertTrue(stockpot.addSoupBase(helper.getLevel(), player, Items.WATER_BUCKET.getDefaultInstance()),
                "Stockpot rejected the test soup base");
        helper.assertTrue(stockpot.addIngredient(helper.getLevel(), player, Items.APPLE.getDefaultInstance()),
                "Stockpot rejected the test ingredient");
        helper.assertTrue(stockpot.onLitClick(helper.getLevel(), player, ModItems.STOCKPOT_LID.getDefaultInstance()),
                "Stockpot rejected the test lid");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        StockpotBlock block = (StockpotBlock) ModBlocks.STOCKPOT;

        InteractionResult lidResult = block.useWithoutItem(
                helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(lidResult, InteractionResult.SUCCESS,
                "Stockpot empty-hand lid removal did not report success");
        helper.assertTrue(player.getMainHandItem().is(ModItems.STOCKPOT_LID),
                "Stockpot empty-hand interaction did not return the lid first");
        helper.assertFalse(stockpot.getInputs().stream().allMatch(ItemStack::isEmpty),
                "Stockpot lid removal also removed an ingredient");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult ingredientResult = block.useWithoutItem(
                helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(ingredientResult, InteractionResult.SUCCESS,
                "Stockpot empty-hand ingredient removal did not report success");
        helper.assertTrue(player.getMainHandItem().is(Items.APPLE),
                "Stockpot empty-hand ingredient removal returned the wrong item");
        helper.assertTrue(stockpot.getInputs().stream().allMatch(ItemStack::isEmpty),
                "Stockpot retained the removed ingredient");
        helper.succeed();
    }

    @GameTest
    public void enamelBasinSupportsNativeEmptyHandLidToggle(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.ENAMEL_BASIN);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        EnamelBasinBlock block = (EnamelBasinBlock) ModBlocks.ENAMEL_BASIN;

        BlockState closedState = helper.getLevel().getBlockState(absolutePos);
        InteractionResult openResult = block.useWithoutItem(
                closedState, helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(openResult, InteractionResult.CONSUME,
                "Enamel basin empty-hand opening did not report success");
        helper.assertFalse(helper.getLevel().getBlockState(absolutePos).getValue(EnamelBasinBlock.HAS_LID),
                "Enamel basin retained its lid after empty-hand opening");

        ItemStack unrelatedItem = Items.APPLE.getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, unrelatedItem);
        BlockState openState = helper.getLevel().getBlockState(absolutePos);
        InteractionResult heldItemResult = block.useItemOn(
                unrelatedItem, openState, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(heldItemResult, InteractionResult.PASS,
                "Enamel basin incorrectly delegated an unrelated held item to empty-hand handling");
        helper.assertFalse(helper.getLevel().getBlockState(absolutePos).getValue(EnamelBasinBlock.HAS_LID),
                "Enamel basin closed its lid while the player held an unrelated item");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult closeResult = block.useWithoutItem(
                openState, helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(closeResult, InteractionResult.CONSUME,
                "Enamel basin empty-hand closing did not report success");
        helper.assertTrue(helper.getLevel().getBlockState(absolutePos).getValue(EnamelBasinBlock.HAS_LID),
                "Enamel basin did not restore its lid after empty-hand closing");
        helper.succeed();
    }

    @GameTest
    public void steamerEmptyHandInteractionPrioritizesLidThenFood(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.STEAMER);
        SteamerBlockEntity steamer = helper.getBlockEntity(pos, SteamerBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack dough = ModItems.RAW_DOUGH.getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, dough);
        helper.assertTrue(steamer.placeFood(helper.getLevel(), player, dough),
                "Steamer rejected the test dough");
        helper.assertTrue(player.getMainHandItem().isEmpty(),
                "Steamer did not consume the inserted test dough");
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        SteamerBlock block = (SteamerBlock) ModBlocks.STEAMER;

        player.setShiftKeyDown(true);
        BlockState openState = helper.getLevel().getBlockState(absolutePos);
        InteractionResult closeResult = block.useWithoutItem(
                openState, helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(closeResult, InteractionResult.CONSUME,
                "Steamer empty-hand lid closing did not report success");
        helper.assertTrue(helper.getLevel().getBlockState(absolutePos).getValue(SteamerBlock.HAS_LID),
                "Steamer did not close its lid during secondary use");
        helper.assertFalse(steamer.getItems().stream().allMatch(ItemStack::isEmpty),
                "Steamer lid interaction removed food from the current layer");

        BlockState closedState = helper.getLevel().getBlockState(absolutePos);
        InteractionResult openResult = block.useWithoutItem(
                closedState, helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(openResult, InteractionResult.CONSUME,
                "Steamer empty-hand lid opening did not report success");
        helper.assertFalse(helper.getLevel().getBlockState(absolutePos).getValue(SteamerBlock.HAS_LID),
                "Steamer retained its lid during secondary use");

        player.setShiftKeyDown(false);
        InteractionResult takeResult = block.useWithoutItem(
                helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(takeResult, InteractionResult.CONSUME,
                "Steamer empty-hand food takeout did not report success");
        helper.assertTrue(player.getMainHandItem().is(ModItems.RAW_DOUGH),
                "Steamer empty-hand food takeout returned the wrong item");
        helper.assertTrue(steamer.getItems().stream().allMatch(ItemStack::isEmpty),
                "Steamer retained the withdrawn food");
        helper.succeed();
    }

    @GameTest
    public void fruitBasketUsesNativeHeldAndEmptyHandInteractions(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.FRUIT_BASKET);
        FruitBasketBlockEntity basket = helper.getBlockEntity(pos, FruitBasketBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack apples = new ItemStack(Items.APPLE, 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, apples);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        FruitBasketBlock block = (FruitBasketBlock) ModBlocks.FRUIT_BASKET;

        InteractionResult insertResult = block.useItemOn(
                apples, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(insertResult, InteractionResult.CONSUME,
                "Fruit basket item insertion did not report success");
        helper.assertTrue(player.getMainHandItem().isEmpty(),
                "Fruit basket did not consume the inserted stack");
        helper.assertValueEqual(basket.getItems().getFirst().getCount(), 3,
                "Fruit basket stored the wrong item count");

        player.setShiftKeyDown(true);
        InteractionResult takeResult = block.useWithoutItem(
                state, helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(takeResult, InteractionResult.CONSUME,
                "Fruit basket empty-hand takeout did not report success");
        helper.assertTrue(player.getMainHandItem().is(Items.APPLE)
                        && player.getMainHandItem().getCount() == 3,
                "Fruit basket empty-hand takeout returned the wrong stack");
        helper.assertTrue(basket.getItems().stream().allMatch(ItemStack::isEmpty),
                "Fruit basket retained the withdrawn stack");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult emptyResult = block.useWithoutItem(
                state, helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(emptyResult, InteractionResult.PASS,
                "Empty fruit basket takeout did not pass interaction handling onward");
        helper.succeed();
    }

    @GameTest
    public void hangingProduceSupportsNativeEmptyHandHarvest(GameTestHelper helper) {
        BlockPos chiliPos = new BlockPos(1, 1, 1);
        BlockPos mushroomPos = new BlockPos(2, 1, 1);
        helper.setBlock(chiliPos.above(), Blocks.STONE);
        helper.setBlock(mushroomPos.above(), Blocks.STONE);
        helper.setBlock(chiliPos, ModBlocks.CHILI_RISTRA);
        helper.setBlock(mushroomPos, ModBlocks.STRUNG_MUSHROOMS);

        ServerPlayer chiliPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, chiliPlayer, new BlockPos(1, 1, 2));
        chiliPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        BlockPos absoluteChiliPos = helper.absolutePos(chiliPos);
        BlockHitResult chiliHit = new BlockHitResult(
                Vec3.atCenterOf(absoluteChiliPos), Direction.UP, absoluteChiliPos, false);
        InteractionResult chiliResult = ((ChiliRistraBlock) ModBlocks.CHILI_RISTRA).useWithoutItem(
                helper.getLevel().getBlockState(absoluteChiliPos), helper.getLevel(), absoluteChiliPos,
                chiliPlayer, chiliHit);

        helper.assertValueEqual(chiliResult, InteractionResult.CONSUME,
                "Chili ristra empty-hand harvest did not report success");
        helper.assertTrue(helper.getLevel().getBlockState(absoluteChiliPos).getValue(ChiliRistraBlock.SHEARED),
                "Chili ristra did not advance to its sheared state");
        helper.assertValueEqual(countItem(chiliPlayer, ModItems.RED_CHILI), 3,
                "Chili ristra empty-hand harvest returned the wrong item count");

        ServerPlayer mushroomPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, mushroomPlayer, new BlockPos(2, 1, 2));
        mushroomPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        BlockPos absoluteMushroomPos = helper.absolutePos(mushroomPos);
        BlockHitResult mushroomHit = new BlockHitResult(
                Vec3.atCenterOf(absoluteMushroomPos), Direction.UP, absoluteMushroomPos, false);
        InteractionResult mushroomResult = ((StrungMushroomsBlock) ModBlocks.STRUNG_MUSHROOMS).useWithoutItem(
                helper.getLevel().getBlockState(absoluteMushroomPos), helper.getLevel(), absoluteMushroomPos,
                mushroomPlayer, mushroomHit);

        helper.assertValueEqual(mushroomResult, InteractionResult.CONSUME,
                "Strung mushrooms empty-hand harvest did not report success");
        helper.assertTrue(helper.getLevel().getBlockState(absoluteMushroomPos).getValue(StrungMushroomsBlock.SHEARED),
                "Strung mushrooms did not advance to their sheared state");
        helper.assertValueEqual(countItem(mushroomPlayer, Items.BROWN_MUSHROOM), 3,
                "Strung mushrooms empty-hand harvest returned the wrong item count");
        helper.succeed();
    }

    @GameTest
    public void cupBlocksUseNativeHeldAndEmptyHandInteractions(GameTestHelper helper) {
        BlockPos emptyCupPos = new BlockPos(1, 1, 1);
        helper.setBlock(emptyCupPos, ModBlocks.EMPTY_CUP);
        ServerPlayer emptyCupPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, emptyCupPlayer, new BlockPos(1, 1, 2));
        ItemStack emptyCups = new ItemStack(ModItems.EMPTY_CUP, 2);
        emptyCupPlayer.setItemInHand(InteractionHand.MAIN_HAND, emptyCups);
        BlockPos absoluteEmptyCupPos = helper.absolutePos(emptyCupPos);
        BlockHitResult emptyCupHit = new BlockHitResult(
                Vec3.atCenterOf(absoluteEmptyCupPos), Direction.UP, absoluteEmptyCupPos, false);
        EmptyCupBlock emptyCupBlock = (EmptyCupBlock) ModBlocks.EMPTY_CUP;

        InteractionResult emptyCupStackResult = emptyCupBlock.useItemOn(
                emptyCups, helper.getLevel().getBlockState(absoluteEmptyCupPos), helper.getLevel(),
                absoluteEmptyCupPos, emptyCupPlayer, InteractionHand.MAIN_HAND, emptyCupHit);

        helper.assertValueEqual(emptyCupStackResult, InteractionResult.CONSUME,
                "Empty cup stacking did not report success");
        helper.assertValueEqual(emptyCups.getCount(), 1,
                "Empty cup stacking consumed the wrong item count");
        helper.assertValueEqual(helper.getLevel().getBlockState(absoluteEmptyCupPos).getValue(EmptyCupBlock.CUP_COUNT),
                2, "Empty cup stacking stored the wrong cup count");

        emptyCupPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult emptyCupTakeResult = emptyCupBlock.useWithoutItem(
                helper.getLevel().getBlockState(absoluteEmptyCupPos), helper.getLevel(),
                absoluteEmptyCupPos, emptyCupPlayer, emptyCupHit);

        helper.assertValueEqual(emptyCupTakeResult, InteractionResult.CONSUME,
                "Empty cup takeout did not report success");
        helper.assertTrue(emptyCupPlayer.getMainHandItem().is(ModItems.EMPTY_CUP),
                "Empty cup takeout returned the wrong item");
        helper.assertValueEqual(helper.getLevel().getBlockState(absoluteEmptyCupPos).getValue(EmptyCupBlock.CUP_COUNT),
                1, "Empty cup takeout retained the wrong cup count");

        BlockPos teaCupPos = new BlockPos(2, 1, 1);
        TeacupBlock teaCupBlock = (TeacupBlock) TeacupRegistry.getBlock(TeacupRegistry.BARLEY_TEA);
        helper.setBlock(teaCupPos, teaCupBlock);
        ServerPlayer teaCupPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, teaCupPlayer, new BlockPos(2, 1, 2));
        ItemStack teaCups = new ItemStack(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA), 2);
        teaCupPlayer.setItemInHand(InteractionHand.MAIN_HAND, teaCups);
        BlockPos absoluteTeaCupPos = helper.absolutePos(teaCupPos);
        BlockHitResult teaCupHit = new BlockHitResult(
                Vec3.atCenterOf(absoluteTeaCupPos), Direction.UP, absoluteTeaCupPos, false);

        InteractionResult teaCupStackResult = teaCupBlock.useItemOn(
                teaCups, helper.getLevel().getBlockState(absoluteTeaCupPos), helper.getLevel(),
                absoluteTeaCupPos, teaCupPlayer, InteractionHand.MAIN_HAND, teaCupHit);

        helper.assertValueEqual(teaCupStackResult, InteractionResult.CONSUME,
                "Filled teacup stacking did not report success");
        helper.assertValueEqual(teaCups.getCount(), 1,
                "Filled teacup stacking consumed the wrong item count");
        BlockState stackedTeaState = helper.getLevel().getBlockState(absoluteTeaCupPos);
        helper.assertValueEqual(stackedTeaState.getValue(TeacupBlock.CUP_COUNT), 2,
                "Filled teacup stacking stored the wrong cup count");
        helper.assertValueEqual(stackedTeaState.getValue(TeacupBlock.TEA_COUNT), 2,
                "Filled teacup stacking stored the wrong tea count");

        teaCupPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult teaCupTakeResult = teaCupBlock.useWithoutItem(
                stackedTeaState, helper.getLevel(), absoluteTeaCupPos, teaCupPlayer, teaCupHit);

        helper.assertValueEqual(teaCupTakeResult, InteractionResult.CONSUME,
                "Filled teacup takeout did not report success");
        helper.assertTrue(teaCupPlayer.getMainHandItem().is(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA)),
                "Filled teacup takeout returned the wrong item");
        BlockState remainingTeaState = helper.getLevel().getBlockState(absoluteTeaCupPos);
        helper.assertValueEqual(remainingTeaState.getValue(TeacupBlock.CUP_COUNT), 1,
                "Filled teacup takeout retained the wrong cup count");
        helper.assertValueEqual(remainingTeaState.getValue(TeacupBlock.TEA_COUNT), 1,
                "Filled teacup takeout retained the wrong tea count");
        helper.succeed();
    }

    @GameTest
    public void teapotUsesSuppliedStackAndNativeEmptyHandTakeout(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.TEAPOT);
        TeapotBlockEntity teapot = helper.getBlockEntity(pos, TeapotBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack waterBucket = Items.WATER_BUCKET.getDefaultInstance();
        player.setItemInHand(InteractionHand.MAIN_HAND, waterBucket);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        TeapotBlock block = (TeapotBlock) ModBlocks.TEAPOT;

        InteractionResult fillResult = block.useItemOn(
                waterBucket, helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(fillResult, InteractionResult.CONSUME,
                "Teapot water insertion did not report success");
        helper.assertValueEqual(teapot.getTeaFluidId(), vanillaId("water"),
                "Teapot stored the wrong fluid");
        helper.assertTrue(player.getMainHandItem().is(Items.BUCKET),
                "Teapot water insertion returned the wrong container");

        ItemStack bucket = player.getMainHandItem();
        InteractionResult drainResult = block.useItemOn(
                bucket, helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(drainResult, InteractionResult.CONSUME,
                "Teapot water removal did not report success");
        helper.assertFalse(teapot.getTeaFluidId().equals(vanillaId("water")),
                "Teapot retained its removed fluid");
        helper.assertTrue(player.getMainHandItem().is(Items.WATER_BUCKET),
                "Teapot water removal returned the wrong filled container");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult takeResult = block.useWithoutItem(
                helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(takeResult, InteractionResult.CONSUME,
                "Teapot empty-hand takeout did not report success");
        helper.assertTrue(helper.getLevel().getBlockState(absolutePos).isAir(),
                "Teapot empty-hand takeout did not remove the block");
        helper.assertTrue(player.getMainHandItem().is(ModItems.TEAPOT),
                "Teapot empty-hand takeout returned the wrong item");
        helper.succeed();
    }

    @GameTest
    public void teapotTransfersOneBucketThroughGenericFabricFluidStorage(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.TEAPOT);
        TeapotBlockEntity teapot = helper.getBlockEntity(pos, TeapotBlockEntity.class);
        Cow user = helper.spawn(EntityTypes.COW, new BlockPos(2, 1, 1));
        SingleVariantStorage<FluidVariant> tank = new SingleVariantStorage<>() {
            @Override
            protected FluidVariant getBlankVariant() {
                return FluidVariant.blank();
            }

            @Override
            protected long getCapacity(FluidVariant variant) {
                return FluidConstants.BUCKET * 2;
            }
        };
        tank.variant = FluidVariant.of(Fluids.WATER);
        tank.amount = FluidConstants.BUCKET - 1;

        helper.assertFalse(teapot.addTeaFluid(helper.getLevel(), user, tank),
                "Teapot accepted less than one Fabric bucket unit");
        helper.assertValueEqual(tank.amount, FluidConstants.BUCKET - 1,
                "Failed teapot transfer changed the source storage");

        tank.amount = FluidConstants.BUCKET;
        helper.assertTrue(teapot.addTeaFluid(helper.getLevel(), user, tank),
                "Teapot rejected a generic Fabric fluid storage");
        helper.assertValueEqual(teapot.getTeaFluidId(), vanillaId("water"),
                "Generic Fabric transfer stored the wrong fluid");
        helper.assertValueEqual(tank.amount, 0L,
                "Teapot did not extract exactly one Fabric bucket unit");

        helper.assertTrue(teapot.removeTeaFluid(helper.getLevel(), user, tank),
                "Teapot could not return fluid to a generic Fabric storage");
        helper.assertValueEqual(tank.getResource(), FluidVariant.of(Fluids.WATER),
                "Teapot returned the wrong fluid variant");
        helper.assertValueEqual(tank.amount, FluidConstants.BUCKET,
                "Teapot did not insert exactly one Fabric bucket unit");
        helper.assertValueEqual(teapot.getTeaFluidId(), TeapotRecipeSerializer.EMPTY_TEA_FLUID,
                "Teapot retained fluid after a generic storage extraction");
        helper.succeed();
    }

    @GameTest
    public void heldTeapotStoresFluidAndSneakAttackClearsIt(GameTestHelper helper) {
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack teapot = new ItemStack(ModItems.TEAPOT);
        player.setItemInHand(InteractionHand.MAIN_HAND, teapot);

        helper.assertTrue(TeapotItem.fillFluid(teapot, Fluids.WATER, player),
                "Held teapot rejected a valid water source");
        TypedEntityData<?> data = teapot.get(DataComponents.BLOCK_ENTITY_DATA);
        helper.assertTrue(data != null, "Held teapot did not store block entity data");
        helper.assertValueEqual(data.copyTagWithoutId().getString(TeapotBlockEntity.TEA_FLUID_ID).orElse(""),
                vanillaId("water").toString(), "Held teapot stored the wrong fluid ID");
        helper.assertValueEqual(teapot.get(DataComponents.MAX_STACK_SIZE), 1,
                "Filled teapot did not retain its single-stack component");
        helper.assertFalse(TeapotItem.fillFluid(teapot, Fluids.LAVA, player),
                "Held teapot accepted a second fluid");

        player.setShiftKeyDown(true);
        BlockPos target = helper.absolutePos(new BlockPos(1, 1, 1));
        InteractionResult clearResult = AttackBlockCallback.EVENT.invoker().interact(
                player, helper.getLevel(), InteractionHand.MAIN_HAND, target, Direction.UP);

        helper.assertValueEqual(clearResult, InteractionResult.SUCCESS,
                "Sneak-attacking a block with a teapot did not cancel the attack");
        helper.assertTrue(teapot.get(DataComponents.BLOCK_ENTITY_DATA) == null,
                "Sneak-attacking a block did not clear the held teapot");
        helper.assertTrue(teapot.get(DataComponents.MAX_STACK_SIZE) == null,
                "Cleared teapot retained its temporary max-stack component");
        helper.succeed();
    }

    @GameTest
    public void finishedHeldTeapotPoursOnEntities(GameTestHelper helper) {
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        Chicken target = helper.spawn(EntityTypes.CHICKEN, new BlockPos(1, 1, 1));
        ItemStack teapot = new ItemStack(ModItems.TEAPOT);
        TagValueOutput output = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        output.putInt(TeapotBlockEntity.STATUS, com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot.FINISHED);
        output.store(TeapotBlockEntity.RESULT, ItemStack.CODEC, new ItemStack(Items.HONEY_BOTTLE, 2));
        net.minecraft.world.item.BlockItem.setBlockEntityData(teapot, ModBlocks.TEAPOT_BE, output);
        player.setItemInHand(InteractionHand.MAIN_HAND, teapot);
        float initialHealth = target.getHealth();

        InteractionResult result = ((TeapotItem) ModItems.TEAPOT).interactLivingEntity(
                teapot, player, target, InteractionHand.MAIN_HAND);

        helper.assertValueEqual(result, InteractionResult.SUCCESS,
                "Finished held teapot did not report a successful entity pour");
        helper.assertValueEqual(target.getHealth(), initialHealth - 3.0F,
                "Finished held teapot dealt the wrong damage");
        helper.assertValueEqual(TeapotItem.getPourOut(teapot, helper.getLevel()).getCount(), 1,
                "Finished held teapot did not consume exactly one result");
        helper.succeed();
    }

    @GameTest
    public void oilPotTransfersCommitBeforeMovingItems(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.OIL_POT);
        OilPotBlockEntity oilPot = helper.getBlockEntity(pos, OilPotBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack oil = new ItemStack(ModItems.OIL, 3);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        OilPotBlock oilPotBlock = (OilPotBlock) ModBlocks.OIL_POT;

        InteractionResult insertResult = oilPotBlock.useItemOn(
                oil, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(insertResult, InteractionResult.CONSUME,
                "Oil pot insertion did not report a committed transfer");
        helper.assertValueEqual(oilPot.getOilCount(), 3,
                "Oil pot insertion stored the wrong oil count");
        helper.assertTrue(oil.isEmpty(),
                "Oil pot insertion did not consume the transferred items");
        helper.assertTrue(player.getMainHandItem().is(Items.STONE),
                "Oil pot reread the player's hand instead of using the supplied stack");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult extractResult = oilPotBlock.useWithoutItem(
                helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(extractResult, InteractionResult.CONSUME,
                "Oil pot extraction did not report a committed transfer");
        helper.assertValueEqual(oilPot.getOilCount(), 0,
                "Oil pot extraction did not remove the stored oil");
        helper.assertTrue(player.getMainHandItem().is(ModItems.OIL)
                        && player.getMainHandItem().getCount() == 3,
                "Oil pot extraction returned the wrong oil stack");
        helper.succeed();
    }

    @GameTest
    public void kitchenwareRackSupportsNativeEmptyHandTakeout(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.KITCHENWARE_RACKS);
        KitchenwareRacksBlockEntity racks = helper.getBlockEntity(pos, KitchenwareRacksBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack tool = new ItemStack(Items.IRON_SWORD);
        player.setItemInHand(InteractionHand.MAIN_HAND, tool);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        KitchenwareRacksBlock block = (KitchenwareRacksBlock) ModBlocks.KITCHENWARE_RACKS;

        InteractionResult insertResult = block.useItemOn(
                tool, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(insertResult, InteractionResult.SUCCESS,
                "Kitchenware rack did not report a successful tool insertion");
        helper.assertTrue(player.getMainHandItem().isEmpty(),
                "Kitchenware rack did not consume the inserted tool");
        helper.assertTrue(racks.getItemLeft().is(Items.IRON_SWORD)
                        || racks.getItemRight().is(Items.IRON_SWORD),
                "Kitchenware rack did not store the inserted tool");

        InteractionResult takeResult = block.useWithoutItem(
                helper.getLevel().getBlockState(absolutePos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(takeResult, InteractionResult.SUCCESS,
                "Kitchenware rack empty-hand takeout did not report success");
        helper.assertTrue(player.getMainHandItem().is(Items.IRON_SWORD),
                "Kitchenware rack empty-hand takeout returned the wrong tool");
        helper.assertTrue(racks.getItemLeft().isEmpty() && racks.getItemRight().isEmpty(),
                "Kitchenware rack retained the withdrawn tool");
        helper.succeed();
    }

    @GameTest
    public void tableSupportsNativeEmptyHandTakeout(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.TABLE_OAK);
        TableBlockEntity table = helper.getBlockEntity(pos, TableBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack apples = new ItemStack(Items.APPLE, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        BlockState state = helper.getLevel().getBlockState(absolutePos);
        TableBlock block = (TableBlock) ModBlocks.TABLE_OAK;

        InteractionResult insertResult = block.useItemOn(
                apples, state, helper.getLevel(), absolutePos, player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(insertResult, InteractionResult.CONSUME,
                "Table did not report a successful item insertion");
        helper.assertValueEqual(apples.getCount(), 1,
                "Table insertion consumed the wrong item count");
        helper.assertTrue(player.getMainHandItem().is(Items.STONE),
                "Table reread the player's hand instead of using the supplied stack");
        helper.assertTrue(table.getLastItem().is(Items.APPLE),
                "Table did not store the inserted item");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        InteractionResult takeResult = block.useWithoutItem(
                state, helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(takeResult, InteractionResult.CONSUME,
                "Table empty-hand takeout did not report success");
        helper.assertTrue(player.getMainHandItem().is(Items.APPLE),
                "Table empty-hand takeout returned the wrong item");
        helper.assertTrue(table.getLastItem().isEmpty(),
                "Table retained the withdrawn item");
        helper.succeed();
    }

    @GameTest
    public void chairUsesStackSuppliedToCarpetInteraction(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.CHAIR_OAK);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(1, 1, 2));
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        ChairBlock block = (ChairBlock) ModBlocks.CHAIR_OAK;
        ItemStack carpet = Items.CARPET.pick(DyeColor.RED).getDefaultInstance();

        InteractionResult result = block.useItemOn(
                carpet, helper.getBlockState(pos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(result, InteractionResult.CONSUME,
                "Chair carpet placement did not report a server-side mutation");
        helper.assertTrue(carpet.isEmpty() && player.getMainHandItem().is(Items.STONE),
                "Chair reread the player's hand instead of using the supplied carpet");
        helper.assertTrue(helper.getBlockState(pos).getValue(ChairBlock.HAS_CARPET),
                "Chair did not commit its carpet block state");
        ChairBlockEntity chair = helper.getBlockEntity(pos, ChairBlockEntity.class);
        helper.assertValueEqual(chair.getColor(), DyeColor.RED,
                "Chair did not store the supplied carpet color");
        helper.succeed();
    }

    @GameTest
    public void shawarmaSpitCopiesUpperWaterlogging(GameTestHelper helper) {
        BlockPos lowerPos = new BlockPos(1, 1, 1);
        BlockPos upperPos = lowerPos.above();
        BlockState lowerState = ModBlocks.SHAWARMA_SPIT.defaultBlockState()
                .setValue(ShawarmaSpitBlock.HALF, DoubleBlockHalf.LOWER)
                .setValue(ShawarmaSpitBlock.WATERLOGGED, false);
        helper.setBlock(upperPos, Blocks.WATER);

        BlockPos absoluteLowerPos = helper.absolutePos(lowerPos);
        helper.getLevel().setBlock(absoluteLowerPos, lowerState, Block.UPDATE_CLIENTS);
        ModBlocks.SHAWARMA_SPIT.setPlacedBy(
                helper.getLevel(),
                absoluteLowerPos,
                lowerState,
                helper.makeMockPlayer(GameType.SURVIVAL),
                ItemStack.EMPTY);

        BlockState upperState = helper.getBlockState(upperPos);
        helper.assertTrue(upperState.is(ModBlocks.SHAWARMA_SPIT),
                "Shawarma spit did not place its upper half");
        helper.assertValueEqual(upperState.getValue(ShawarmaSpitBlock.HALF),
                DoubleBlockHalf.UPPER, "Shawarma spit placed an invalid upper-half state");
        helper.assertTrue(upperState.getValue(ShawarmaSpitBlock.WATERLOGGED),
                "Shawarma spit did not preserve water at its upper position");
        helper.succeed();
    }

    @GameTest
    public void shawarmaSpitUsesSuppliedStackAndNativeEmptyHandTakeout(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.SHAWARMA_SPIT);
        ShawarmaSpitBlockEntity shawarmaSpit = helper.getBlockEntity(pos, ShawarmaSpitBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(1, 1, 2));
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        ShawarmaSpitBlock block = (ShawarmaSpitBlock) ModBlocks.SHAWARMA_SPIT;
        ItemStack beef = Items.BEEF.getDefaultInstance();

        InteractionResult putResult = block.useItemOn(
                beef, helper.getBlockState(pos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(putResult, InteractionResult.CONSUME,
                "Shawarma spit did not accept the stack supplied to useItemOn");
        helper.assertTrue(beef.isEmpty()
                        && player.getMainHandItem().is(Items.STONE)
                        && shawarmaSpit.getStoredItem().is(Items.BEEF),
                "Shawarma spit reread the player's hand instead of using the supplied stack");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        InteractionResult takeResult = block.useWithoutItem(
                helper.getBlockState(pos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(takeResult, InteractionResult.CONSUME,
                "Shawarma spit empty-hand takeout did not report success");
        helper.assertTrue(player.getMainHandItem().is(Items.BEEF) && shawarmaSpit.getStoredItem().isEmpty(),
                "Shawarma spit did not return and clear its uncooked ingredient");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertValueEqual(block.useWithoutItem(
                        helper.getBlockState(pos), helper.getLevel(), absolutePos, player, hitResult),
                InteractionResult.PASS, "Empty shawarma spit did not pass empty-hand handling onward");
        player.setShiftKeyDown(true);
        helper.assertValueEqual(block.useWithoutItem(
                        helper.getBlockState(pos), helper.getLevel(), absolutePos, player, hitResult),
                InteractionResult.PASS, "Shawarma spit did not preserve secondary-use bypass behavior");
        helper.succeed();
    }

    @GameTest
    public void millstoneStorageCommitsThroughFabricTransactions(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.MILLSTONE);
        MillstoneBlockEntity millstone = helper.getBlockEntity(pos, MillstoneBlockEntity.class);
        var storage = millstone.getInputStorage();
        ItemVariant wheat = ItemVariant.of(Items.WHEAT);

        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(storage.insert(wheat, 3, transaction), 3L,
                    "Millstone storage rejected a valid transactional insertion");
            helper.assertTrue(millstone.getInput().isEmpty(),
                    "Millstone storage changed block-entity state before commit");
            helper.assertValueEqual(storage.getAmount(), 3L,
                    "Millstone storage did not expose its pending transaction amount");
        }
        helper.assertTrue(millstone.getInput().isEmpty() && storage.getAmount() == 0,
                "Aborted millstone insertion was not rolled back");

        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(storage.insert(wheat, 3, transaction), 3L,
                    "Millstone storage rejected the committed insertion");
            transaction.commit();
        }
        helper.assertTrue(millstone.getInput().is(Items.WHEAT)
                        && millstone.getInput().getCount() == 3,
                "Committed millstone insertion did not reach the block entity");
        ItemStack inputSnapshot = millstone.getInput();
        inputSnapshot.shrink(1);
        helper.assertValueEqual(millstone.getInput().getCount(), 3,
                "Mutating a millstone input snapshot changed the stored stack");
        helper.succeed();
    }

    @GameTest
    public void millstoneUsesEntityStorageLookupAndDroppedItemFallback(GameTestHelper helper) {
        registerMillstoneEntityStorageTestProvider();

        BlockPos droppedInputPos = new BlockPos(1, 1, 1);
        helper.setBlock(droppedInputPos, ModBlocks.MILLSTONE);
        MillstoneBlockEntity droppedInputMillstone = helper.getBlockEntity(
                droppedInputPos, MillstoneBlockEntity.class);
        Cow rejectedStorageCow = helper.spawn(EntityTypes.COW, new BlockPos(1, 1, 2));
        rejectedStorageCow.tickCount = 10;
        droppedInputMillstone.bindEntity(rejectedStorageCow);
        SingleVariantStorage<ItemVariant> rejectedStorage = testItemStorage(Items.BEDROCK, 4);
        TEST_MILLSTONE_ENTITY_STORAGES.put(rejectedStorageCow.getUUID(), rejectedStorage);

        BlockPos absoluteDroppedInputPos = helper.absolutePos(droppedInputPos);
        ItemEntity droppedWheat = new ItemEntity(
                helper.getLevel(),
                absoluteDroppedInputPos.getX() + 0.5,
                absoluteDroppedInputPos.getY() + 1.25,
                absoluteDroppedInputPos.getZ() + 0.5,
                new ItemStack(Items.WHEAT, 5));
        helper.assertTrue(helper.getLevel().addFreshEntity(droppedWheat),
                "Could not add the millstone dropped-item fixture");
        droppedInputMillstone.tick(helper.getLevel());

        helper.assertTrue(droppedInputMillstone.getInput().is(Items.WHEAT)
                        && droppedInputMillstone.getInput().getCount() == 5,
                "Millstone did not fall back to its Forge dropped-item input path");
        helper.assertValueEqual(rejectedStorage.getAmount(), 4L,
                "Rejected entity storage extraction was not rolled back");
        helper.assertFalse(droppedWheat.isAlive(),
                "Millstone left an empty dropped-item entity behind");

        BlockPos entityInputPos = new BlockPos(4, 1, 1);
        helper.setBlock(entityInputPos, ModBlocks.MILLSTONE);
        MillstoneBlockEntity entityInputMillstone = helper.getBlockEntity(
                entityInputPos, MillstoneBlockEntity.class);
        Cow entityStorageCow = helper.spawn(EntityTypes.COW, new BlockPos(4, 1, 2));
        entityStorageCow.tickCount = 10;
        entityInputMillstone.bindEntity(entityStorageCow);
        SingleVariantStorage<ItemVariant> entityStorage = testItemStorage(Items.WHEAT, 11);
        TEST_MILLSTONE_ENTITY_STORAGES.put(entityStorageCow.getUUID(), entityStorage);

        helper.assertTrue(MillstoneEntityItemStorage.find(entityStorageCow) == entityStorage,
                "Cookery entity item-storage lookup did not discover a registered provider");
        entityInputMillstone.tick(helper.getLevel());

        helper.assertTrue(entityInputMillstone.getInput().is(Items.WHEAT)
                        && entityInputMillstone.getInput().getCount() == MillstoneBlockEntity.MAX_INPUT_COUNT,
                "Millstone did not extract a full batch from generic entity storage");
        helper.assertValueEqual(entityStorage.getAmount(), 3L,
                "Millstone generic entity extraction did not commit exactly eight items");

        var donkey = helper.spawn(EntityTypes.DONKEY, new BlockPos(6, 1, 2));
        donkey.setChest(true);
        invokeCreateHorseInventory(donkey);
        int donkeyStorageSlot = AbstractHorse.INVENTORY_SLOT_OFFSET + 2;
        helper.assertTrue(donkey.getSlot(donkeyStorageSlot).set(new ItemStack(Items.WHEAT, 11)),
                "Could not initialize the chested-horse inventory fixture");
        Storage<ItemVariant> donkeyStorage = MillstoneEntityItemStorage.find(donkey);
        helper.assertTrue(donkeyStorage != null,
                "Vanilla chested horses are not exposed through the millstone entity lookup fallback");
        StorageView<ItemVariant> donkeyWheat = donkeyStorage.nonEmptyViews().iterator().next();
        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(donkeyWheat.extract(
                            ItemVariant.of(Items.WHEAT), MillstoneBlockEntity.MAX_INPUT_COUNT, transaction),
                    8L, "Chested-horse storage rejected transactional extraction");
        }
        helper.assertValueEqual(donkey.getSlot(donkeyStorageSlot).get().getCount(), 11,
                "Aborted chested-horse extraction was not rolled back");
        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(donkeyWheat.extract(
                            ItemVariant.of(Items.WHEAT), MillstoneBlockEntity.MAX_INPUT_COUNT, transaction),
                    8L, "Chested-horse storage rejected committed extraction");
            transaction.commit();
        }
        helper.assertValueEqual(donkey.getSlot(donkeyStorageSlot).get().getCount(), 3,
                "Committed chested-horse extraction did not update the real entity inventory");

        TEST_MILLSTONE_ENTITY_STORAGES.remove(rejectedStorageCow.getUUID());
        TEST_MILLSTONE_ENTITY_STORAGES.remove(entityStorageCow.getUUID());
        helper.succeed();
    }

    @GameTest
    public void oilPotStoragePreservesForgeAutomationSemantics(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.OIL_POT);
        BlockPos absolutePos = helper.absolutePos(pos);
        OilPotBlockEntity oilPot = helper.getBlockEntity(pos, OilPotBlockEntity.class);
        Storage<ItemVariant> discoveredStorage = ItemStorage.SIDED.find(helper.getLevel(), absolutePos, Direction.NORTH);
        helper.assertTrue(discoveredStorage == oilPot.getItemStorage(),
                "Oil pot did not expose its Fabric item storage from the side");
        helper.assertTrue(ItemStorage.SIDED.find(helper.getLevel(), absolutePos, Direction.UP) != null
                        && ItemStorage.SIDED.find(helper.getLevel(), absolutePos, Direction.DOWN) != null,
                "Oil pot storage was not exposed on every Forge-compatible side");
        var storage = oilPot.getItemStorage();
        ItemVariant oil = ItemVariant.of(ModItems.OIL);

        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(storage.insert(oil, 300, transaction), 256L,
                    "Oil pot storage did not preserve its 256-item capacity");
            helper.assertValueEqual(oilPot.getOilCount(), 0,
                    "Oil pot changed block-entity state before transaction commit");
            helper.assertValueEqual(storage.getAmount(), 256L,
                    "Oil pot did not expose its pending transaction amount");
        }
        helper.assertValueEqual(storage.getAmount(), 0L,
                "Aborted oil pot insertion was not rolled back");

        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(storage.insert(ItemVariant.of(Items.WHEAT), 1, transaction), 0L,
                    "Oil pot accepted a non-oil item");
            helper.assertValueEqual(storage.insert(oil, 200, transaction), 200L,
                    "Oil pot rejected a valid committed insertion");
            transaction.commit();
        }
        helper.assertValueEqual(oilPot.getOilCount(), 200,
                "Committed Fabric insertion did not update the oil pot");
        helper.assertTrue(helper.getLevel().getBlockState(absolutePos).getValue(OilPotBlock.HAS_OIL),
                "Committed Fabric insertion did not update HAS_OIL");

        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(storage.extract(oil, 75, transaction), 75L,
                    "Oil pot rejected a valid transactional extraction");
        }
        helper.assertValueEqual(oilPot.getOilCount(), 200,
                "Aborted oil pot extraction changed the stored count");

        try (Transaction transaction = Transaction.openOuter()) {
            helper.assertValueEqual(storage.extract(oil, 200, transaction), 200L,
                    "Oil pot rejected a committed full extraction");
            transaction.commit();
        }
        helper.assertValueEqual(oilPot.getOilCount(), 0,
                "Committed Fabric extraction did not empty the oil pot");
        helper.assertFalse(helper.getLevel().getBlockState(absolutePos).getValue(OilPotBlock.HAS_OIL),
                "Committed Fabric extraction did not clear HAS_OIL");
        helper.succeed();
    }

    @GameTest
    public void chefProfessionUsesCookeryHeroGiftLootTable(GameTestHelper helper) {
        try {
            Field giftsField = GiveGiftToHero.class.getDeclaredField("GIFTS");
            giftsField.setAccessible(true);
            Map<?, ?> gifts = (Map<?, ?>) giftsField.get(null);
            helper.assertValueEqual(gifts.get(ModVillager.CHEF_KEY), ModLootTables.CHEF_GIFT,
                    "Chef profession still falls back to the unemployed hero gift");
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not inspect Minecraft 26.2 villager hero gifts", exception);
        }
        helper.succeed();
    }

    @GameTest
    public void kitchenWorkstationsUseSuppliedStacksAndNativeEmptyHandTakeout(GameTestHelper helper) {
        BlockPos choppingBoardPos = new BlockPos(1, 1, 1);
        helper.setBlock(choppingBoardPos, ModBlocks.CHOPPING_BOARD);
        ChoppingBoardBlockEntity choppingBoard = helper.getBlockEntity(
                choppingBoardPos, ChoppingBoardBlockEntity.class);
        ServerPlayer choppingPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, choppingPlayer, new BlockPos(1, 1, 2));
        choppingPlayer.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absoluteChoppingBoardPos = helper.absolutePos(choppingBoardPos);
        BlockHitResult choppingHit = new BlockHitResult(
                Vec3.atCenterOf(absoluteChoppingBoardPos), Direction.UP, absoluteChoppingBoardPos, false);
        ChoppingBoardBlock choppingBlock = (ChoppingBoardBlock) ModBlocks.CHOPPING_BOARD;
        ItemStack salmon = Items.SALMON.getDefaultInstance();

        InteractionResult choppingPutResult = choppingBlock.useItemOn(
                salmon, helper.getBlockState(choppingBoardPos), helper.getLevel(), absoluteChoppingBoardPos,
                choppingPlayer, InteractionHand.MAIN_HAND, choppingHit);

        helper.assertValueEqual(choppingPutResult, InteractionResult.CONSUME,
                "Chopping board did not accept the stack supplied to useItemOn");
        helper.assertTrue(salmon.isEmpty() && choppingBoard.getCurrentCutStack().is(Items.SALMON),
                "Chopping board reread the player's hand instead of using the supplied stack");
        choppingPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        choppingPlayer.setShiftKeyDown(true);

        InteractionResult choppingTakeResult = choppingBlock.useWithoutItem(
                helper.getBlockState(choppingBoardPos), helper.getLevel(), absoluteChoppingBoardPos,
                choppingPlayer, choppingHit);

        helper.assertValueEqual(choppingTakeResult, InteractionResult.CONSUME,
                "Chopping board empty-hand takeout did not report success");
        helper.assertTrue(choppingPlayer.getMainHandItem().is(Items.SALMON)
                        && choppingBoard.getCurrentCutStack().isEmpty(),
                "Chopping board did not return and clear its uncut ingredient");
        choppingPlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertValueEqual(choppingBlock.useWithoutItem(
                        helper.getBlockState(choppingBoardPos), helper.getLevel(), absoluteChoppingBoardPos,
                        choppingPlayer, choppingHit),
                InteractionResult.PASS, "Empty chopping board did not pass empty-hand handling onward");

        BlockPos millstonePos = new BlockPos(2, 1, 1);
        helper.setBlock(millstonePos, ModBlocks.MILLSTONE);
        MillstoneBlockEntity millstone = helper.getBlockEntity(millstonePos, MillstoneBlockEntity.class);
        ServerPlayer millstonePlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, millstonePlayer, new BlockPos(2, 1, 2));
        millstonePlayer.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absoluteMillstonePos = helper.absolutePos(millstonePos);
        BlockHitResult millstoneHit = new BlockHitResult(
                Vec3.atCenterOf(absoluteMillstonePos), Direction.UP, absoluteMillstonePos, false);
        MillstoneBlock millstoneBlock = (MillstoneBlock) ModBlocks.MILLSTONE;
        ItemStack wheat = Items.WHEAT.getDefaultInstance();

        InteractionResult millstonePutResult = millstoneBlock.useItemOn(
                wheat, helper.getBlockState(millstonePos), helper.getLevel(), absoluteMillstonePos,
                millstonePlayer, InteractionHand.MAIN_HAND, millstoneHit);

        helper.assertValueEqual(millstonePutResult, InteractionResult.CONSUME,
                "Millstone did not accept the stack supplied to useItemOn");
        helper.assertTrue(wheat.isEmpty() && millstone.getInput().is(Items.WHEAT),
                "Millstone reread the player's hand instead of using the supplied stack");
        millstonePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

        InteractionResult millstoneTakeResult = millstoneBlock.useWithoutItem(
                helper.getBlockState(millstonePos), helper.getLevel(), absoluteMillstonePos,
                millstonePlayer, millstoneHit);

        helper.assertValueEqual(millstoneTakeResult, InteractionResult.CONSUME,
                "Millstone empty-hand takeout did not report success");
        helper.assertTrue(millstonePlayer.getMainHandItem().is(Items.WHEAT) && millstone.getInput().isEmpty(),
                "Millstone did not return and clear its input");
        millstonePlayer.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertValueEqual(millstoneBlock.useWithoutItem(
                        helper.getBlockState(millstonePos), helper.getLevel(), absoluteMillstonePos,
                        millstonePlayer, millstoneHit),
                InteractionResult.PASS, "Empty millstone did not pass empty-hand handling onward");
        helper.succeed();
    }

    @GameTest
    public void millstoneRemovalPreservesUnrelatedBlocks(GameTestHelper helper) {
        BlockPos centerPos = new BlockPos(2, 1, 2);
        for (NinePart part : NinePart.values()) {
            helper.setBlock(centerPos.offset(part.getPosX(), 0, part.getPosY()),
                    ModBlocks.MILLSTONE.defaultBlockState().setValue(MillstoneBlock.PART, part));
        }
        BlockPos replacedPartPos = centerPos.offset(NinePart.LEFT_UP.getPosX(), 0, NinePart.LEFT_UP.getPosY());
        helper.setBlock(replacedPartPos, Blocks.DIAMOND_BLOCK);

        BlockState centerState = helper.getBlockState(centerPos);
        centerState.getBlock().playerWillDestroy(helper.getLevel(), helper.absolutePos(centerPos), centerState,
                helper.makeMockPlayer(GameType.SURVIVAL));

        helper.assertBlockPresent(Blocks.DIAMOND_BLOCK, replacedPartPos);
        helper.assertBlockNotPresent(ModBlocks.MILLSTONE, centerPos);
        helper.succeed();
    }

    @GameTest
    public void largeFoodRemovalPreservesUnrelatedBlocks(GameTestHelper helper) {
        BlockPos centerPos = new BlockPos(2, 1, 2);
        for (NinePart part : NinePart.values()) {
            helper.setBlock(centerPos.offset(part.getPosX(), 0, part.getPosY()),
                    ModBlocks.COLD_CUT_HAM_SLICES.defaultBlockState().setValue(FoodBiteThreeByThreeBlock.PART, part));
        }
        BlockPos replacedPartPos = centerPos.offset(NinePart.RIGHT_DOWN.getPosX(), 0, NinePart.RIGHT_DOWN.getPosY());
        helper.setBlock(replacedPartPos, Blocks.GOLD_BLOCK);

        BlockState centerState = helper.getBlockState(centerPos);
        centerState.getBlock().playerWillDestroy(helper.getLevel(), helper.absolutePos(centerPos), centerState,
                helper.makeMockPlayer(GameType.SURVIVAL));

        helper.assertBlockPresent(Blocks.GOLD_BLOCK, replacedPartPos);
        helper.assertBlockNotPresent(ModBlocks.COLD_CUT_HAM_SLICES, centerPos);
        helper.succeed();
    }

    @GameTest
    public void seatBlocksMountPlayersAfterEntityCreation(GameTestHelper helper) {
        BlockPos chairPos = new BlockPos(1, 1, 1);
        BlockPos stoolPos = new BlockPos(3, 1, 1);
        BlockPos trashCanPos = new BlockPos(5, 1, 1);
        helper.setBlock(chairPos, ModBlocks.CHAIR_OAK);
        helper.setBlock(stoolPos, ModBlocks.COOK_STOOL_OAK);
        helper.setBlock(trashCanPos, ModBlocks.TRASH_CAN);

        Player chairPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(chairPos, chairPlayer);
        helper.assertTrue(chairPlayer.getVehicle() instanceof SitEntity,
                "Chair did not mount the player after creating its seat entity");

        Player stoolPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.useBlock(stoolPos, stoolPlayer);
        helper.assertTrue(stoolPlayer.getVehicle() instanceof SitEntity,
                "Cook stool did not mount the player after creating its seat entity");

        Player trashCanPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absoluteTrashCanPos = helper.absolutePos(trashCanPos);
        BlockState trashCanState = helper.getLevel().getBlockState(absoluteTrashCanPos);
        ModBlocks.TRASH_CAN.fallOn(helper.getLevel(), trashCanState, absoluteTrashCanPos, trashCanPlayer, 2.0);
        helper.assertTrue(trashCanPlayer.getVehicle() instanceof SitEntity sitEntity
                        && sitEntity.getSitType() == SitEntity.TRASH_CAN,
                "Trash can did not mount the player on its specialized seat entity");
        helper.succeed();
    }

    @GameTest
    public void seatEntitiesFollowVanillaTickLifecycle(GameTestHelper helper) {
        BlockPos emptySeatPos = helper.absolutePos(new BlockPos(1, 1, 1));
        SitEntity emptySeat = new SitEntity(helper.getLevel(), emptySeatPos);
        helper.assertTrue(helper.getLevel().addFreshEntity(emptySeat),
                "Empty seat entity could not be added to the test world");

        BlockPos occupiedSeatPos = helper.absolutePos(new BlockPos(3, 1, 1));
        SitEntity occupiedSeat = new SitEntity(helper.getLevel(), occupiedSeatPos);
        helper.assertTrue(helper.getLevel().addFreshEntity(occupiedSeat),
                "Occupied seat entity could not be added to the test world");
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(3, 1, 1));
        helper.assertTrue(player.startRiding(occupiedSeat, true, true),
                "Player could not mount the occupied test seat");

        helper.runAfterDelay(12, () -> {
            helper.assertTrue(emptySeat.tickCount > 0,
                    "Seat entity did not run the vanilla entity tick lifecycle");
            helper.assertTrue(emptySeat.isRemoved(),
                    "Empty seat entity remained after its cleanup timeout");
            helper.assertTrue(!occupiedSeat.isRemoved() && player.getVehicle() == occupiedSeat,
                    "Occupied seat entity was removed during passenger cleanup");
            helper.succeed();
        });
    }

    @GameTest
    public void scarecrowPlacementCommitsAfterEntityCreation(GameTestHelper helper) {
        BlockPos floorPos = new BlockPos(1, 1, 1);
        BlockPos scarecrowPos = floorPos.above();
        helper.setBlock(floorPos, Blocks.STONE);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(4, 1, 4));
        ItemStack stack = new ItemStack(ModItems.SCARECROW, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, stack);
        BlockPos absoluteFloorPos = helper.absolutePos(floorPos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absoluteFloorPos), Direction.UP, absoluteFloorPos, false);

        InteractionResult result = ModItems.SCARECROW.useOn(
                new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult));

        helper.assertValueEqual(result, InteractionResult.SUCCESS,
                "Successful scarecrow placement did not use the vanilla placement result");
        helper.assertValueEqual(stack.getCount(), 1,
                "Successful scarecrow placement consumed the wrong item count");
        helper.assertEntityPresent(ModEntities.SCARECROW, scarecrowPos);
        helper.succeed();
    }

    @GameTest
    public void fruitBasketUsesVanillaContainerOperations(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.FRUIT_BASKET);
        FruitBasketBlockEntity basket = helper.getBlockEntity(pos, FruitBasketBlockEntity.class);

        ItemStack firstApples = new ItemStack(Items.APPLE, 63);
        basket.putOn(firstApples, true);
        ItemStack moreApples = new ItemStack(Items.APPLE, 3);
        basket.putOn(moreApples, true);

        helper.assertTrue(firstApples.isEmpty() && moreApples.isEmpty(),
                "Fruit basket did not consume inserted survival stacks");
        helper.assertValueEqual(basket.getItems().get(0).getCount(), 64,
                "Fruit basket did not merge into its matching stack first");
        helper.assertValueEqual(basket.getItems().get(1).getCount(), 2,
                "Fruit basket did not place the insertion remainder into an empty slot");

        ItemStack creativeCarrots = new ItemStack(Items.CARROT, 4);
        basket.putOn(creativeCarrots, false);
        helper.assertValueEqual(creativeCarrots.getCount(), 4,
                "Creative fruit basket insertion mutated the source stack");
        helper.assertValueEqual(basket.getItems().get(2).getCount(), 4,
                "Creative fruit basket insertion did not copy items into storage");

        NonNullList<ItemStack> snapshot = basket.getItems();
        snapshot.get(0).shrink(1);
        snapshot.set(1, ItemStack.EMPTY);
        helper.assertValueEqual(basket.getItems().get(0).getCount(), 64,
                "Mutating a fruit basket item snapshot changed the stored stack");
        helper.assertValueEqual(basket.getItems().get(1).getCount(), 2,
                "Replacing a fruit basket snapshot slot changed the stored slot");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        basket.takeOut(player);
        helper.assertTrue(basket.getItems().get(0).isEmpty(),
                "Fruit basket did not remove the first occupied slot");
        helper.assertValueEqual(countItem(player, Items.APPLE), 64,
                "Fruit basket did not give the extracted stack to the player");
        helper.succeed();
    }

    @GameTest
    public void fruitBasketHeldItemTakeoutPreservesInteractionExceptions(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.FRUIT_BASKET);
        BlockPos absolutePos = helper.absolutePos(pos);
        FruitBasketBlockEntity basket = helper.getBlockEntity(pos, FruitBasketBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        player.setShiftKeyDown(true);

        basket.putOn(new ItemStack(Items.APPLE, 2), false);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.CARROT.getDefaultInstance());
        BlockHitResult topHit = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        InteractionResult ordinaryResult = UseBlockCallback.EVENT.invoker().interact(
                player, helper.getLevel(), InteractionHand.MAIN_HAND, topHit);
        helper.assertValueEqual(ordinaryResult, InteractionResult.CONSUME,
                "Held-item secondary use did not take an item stack out of the fruit basket");
        helper.assertTrue(basket.getItems().stream().allMatch(ItemStack::isEmpty),
                "Held-item secondary use left the extracted fruit-basket stack behind");

        basket.putOn(Items.APPLE.getDefaultInstance(), false);
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockHitResult sideHit = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.NORTH, absolutePos, false);
        InteractionResult sideBlockResult = UseBlockCallback.EVENT.invoker().interact(
                player, helper.getLevel(), InteractionHand.MAIN_HAND, sideHit);
        helper.assertValueEqual(sideBlockResult, InteractionResult.PASS,
                "Fruit basket intercepted a block item used away from its top face");
        helper.assertTrue(basket.getItems().stream().anyMatch(stack -> stack.is(Items.APPLE)),
                "Side-face block-item use removed fruit-basket contents");

        InteractionResult topBlockResult = UseBlockCallback.EVENT.invoker().interact(
                player, helper.getLevel(), InteractionHand.MAIN_HAND, topHit);
        helper.assertValueEqual(topBlockResult, InteractionResult.CONSUME,
                "Fruit basket did not accept top-face takeout while holding a block item");
        helper.assertTrue(basket.getItems().stream().allMatch(ItemStack::isEmpty),
                "Top-face block-item takeout left fruit-basket contents behind");

        basket.putOn(Items.APPLE.getDefaultInstance(), false);
        for (Item excluded : List.of(Items.DEBUG_STICK, Items.FIREWORK_ROCKET)) {
            player.setItemInHand(InteractionHand.MAIN_HAND, excluded.getDefaultInstance());
            InteractionResult excludedResult = UseBlockCallback.EVENT.invoker().interact(
                    player, helper.getLevel(), InteractionHand.MAIN_HAND, topHit);
            helper.assertValueEqual(excludedResult, InteractionResult.PASS,
                    "Fruit basket intercepted excluded held item " + excluded);
            helper.assertTrue(basket.getItems().stream().anyMatch(stack -> stack.is(Items.APPLE)),
                    "Excluded held item removed fruit-basket contents");
        }
        helper.succeed();
    }

    @GameTest
    public void fruitBasketComponentsOmitEmptyContents(GameTestHelper helper) {
        ItemStack basket = ModItems.FRUIT_BASKET.getDefaultInstance();
        ItemStackContainer contents = new ItemStackContainer(8);

        contents.set(0, new ItemStack(Items.APPLE));
        FruitBasketItem.saveItems(basket, contents);
        helper.assertTrue(basket.has(ModDataComponents.FRUIT_BASKET_ITEMS),
                "Non-empty fruit basket contents were not stored");

        contents.extractItem(0, 1);
        FruitBasketItem.saveItems(basket, contents);
        helper.assertFalse(basket.has(ModDataComponents.FRUIT_BASKET_ITEMS),
                "Empty fruit basket contents left a data component behind");

        basket.set(ModDataComponents.FRUIT_BASKET_ITEMS, ItemContainerContents.EMPTY);
        FruitBasketItem.saveItems(basket, NonNullList.withSize(8, ItemStack.EMPTY));
        helper.assertFalse(basket.has(ModDataComponents.FRUIT_BASKET_ITEMS),
                "Empty block-entity contents left a fruit basket data component behind");
        helper.succeed();
    }

    @GameTest
    public void trashCanUsesVanillaContainerOperations(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.TRASH_CAN);
        TrashCanBlockEntity trashCan = helper.getBlockEntity(pos, TrashCanBlockEntity.class);

        ItemStack firstApples = new ItemStack(Items.APPLE, 63);
        helper.assertTrue(trashCan.putItem(firstApples, true),
                "Trash can rejected a valid survival insertion");
        ItemStack moreApples = new ItemStack(Items.APPLE, 3);
        helper.assertTrue(trashCan.putItem(moreApples, true),
                "Trash can rejected a mergeable survival insertion");
        helper.assertTrue(trashCan.putItem(Items.POTATO.getDefaultInstance(), true),
                "Trash can rejected a valid history insertion");

        List<ItemStack> initialItems = trashCan.getStoredItems();
        helper.assertTrue(firstApples.isEmpty() && moreApples.isEmpty(),
                "Trash can did not consume inserted survival stacks");
        helper.assertValueEqual(initialItems.size(), 3,
                "Trash can did not fill all three history slots");
        helper.assertTrue(initialItems.get(0).is(Items.APPLE) && initialItems.get(0).getCount() == 64,
                "Trash can did not merge matching stacks using vanilla rules");
        helper.assertTrue(initialItems.get(1).is(Items.APPLE) && initialItems.get(1).getCount() == 2,
                "Trash can did not preserve the insertion remainder");

        ItemStack creativeCarrot = Items.CARROT.getDefaultInstance();
        helper.assertTrue(trashCan.putItem(creativeCarrot, false),
                "Trash can rejected a valid creative insertion");
        List<ItemStack> rotatedItems = trashCan.getStoredItems();
        helper.assertValueEqual(creativeCarrot.getCount(), 1,
                "Creative trash-can insertion mutated the source stack");
        helper.assertTrue(rotatedItems.get(0).is(Items.APPLE) && rotatedItems.get(0).getCount() == 2
                        && rotatedItems.get(1).is(Items.POTATO)
                        && rotatedItems.get(2).is(Items.CARROT),
                "Full trash can did not rotate out only its oldest stack");

        ItemStack shulkerBox = Items.SHULKER_BOX.getDefaultInstance();
        helper.assertFalse(trashCan.putItem(shulkerBox, true),
                "Trash can accepted an item that cannot be nested in containers");
        helper.assertValueEqual(shulkerBox.getCount(), 1,
                "Rejected trash-can insertion mutated the source stack");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(trashCan.withdrawItem(player),
                "Trash can rejected a valid withdrawal");
        helper.assertTrue(player.getMainHandItem().is(Items.CARROT),
                "Trash can did not withdraw its newest stack first");
        helper.assertValueEqual(trashCan.getStoredItems().size(), 2,
                "Trash can retained the withdrawn stack");
        helper.assertFalse(trashCan.withdrawItem(player),
                "Trash can withdrew another stack into an occupied hand");
        helper.succeed();
    }

    @GameTest
    public void trashCanUsesSuppliedStackAndNativeEmptyHandTakeout(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.TRASH_CAN);
        TrashCanBlockEntity trashCan = helper.getBlockEntity(pos, TrashCanBlockEntity.class);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(1, 1, 2));
        player.setItemInHand(InteractionHand.MAIN_HAND, Items.STONE.getDefaultInstance());
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        TrashCanBlock block = (TrashCanBlock) ModBlocks.TRASH_CAN;
        ItemStack apples = new ItemStack(Items.APPLE, 2);

        InteractionResult putResult = block.useItemOn(
                apples, helper.getBlockState(pos), helper.getLevel(), absolutePos,
                player, InteractionHand.MAIN_HAND, hitResult);

        helper.assertValueEqual(putResult, InteractionResult.CONSUME,
                "Trash can insertion did not report a server-side mutation");
        helper.assertTrue(apples.isEmpty()
                        && player.getMainHandItem().is(Items.STONE)
                        && trashCan.getStoredItems().getFirst().is(Items.APPLE),
                "Trash can reread the player's hand instead of using the supplied stack");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        player.setShiftKeyDown(true);

        InteractionResult takeResult = block.useWithoutItem(
                helper.getBlockState(pos), helper.getLevel(), absolutePos, player, hitResult);

        helper.assertValueEqual(takeResult, InteractionResult.CONSUME,
                "Trash can empty-hand takeout did not report a server-side mutation");
        helper.assertTrue(player.getMainHandItem().is(Items.APPLE)
                        && player.getMainHandItem().getCount() == 2
                        && trashCan.getStoredItems().isEmpty(),
                "Trash can did not return and clear its newest stored stack");
        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        helper.assertValueEqual(block.useWithoutItem(
                        helper.getBlockState(pos), helper.getLevel(), absolutePos, player, hitResult),
                InteractionResult.PASS, "Empty trash can did not pass empty-hand handling onward");
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
    public void recipeInputsFollowVanillaValueSemantics(GameTestHelper helper) {
        List<ItemStack> mutableItems = new ArrayList<>();
        mutableItems.add(new ItemStack(Items.POTATO));
        SimpleInput simpleInput = new SimpleInput(mutableItems);
        StockpotInput stockpotInput = new StockpotInput(mutableItems, vanillaId("water_bucket"));
        mutableItems.clear();

        helper.assertTrue(simpleInput.size() == 1 && simpleInput.getItem(0).is(Items.POTATO),
                "Simple recipe input retained a mutable list structure");
        helper.assertTrue(stockpotInput.size() == 1 && stockpotInput.getItem(0).is(Items.POTATO),
                "Stockpot recipe input retained a mutable list structure");
        helper.assertValueEqual(stockpotInput.soupBase(), vanillaId("water"),
                "Stockpot recipe input did not normalize its soup-base ID");

        TeapotInput teapotInput = new TeapotInput(new ItemStack(Items.APPLE), vanillaId("water"));
        boolean invalidSlotRejected = false;
        try {
            teapotInput.getItem(1);
        } catch (IllegalArgumentException expected) {
            invalidSlotRejected = true;
        }
        helper.assertTrue(invalidSlotRejected,
                "Single-slot teapot input accepted an invalid slot index");
        helper.succeed();
    }

    @GameTest
    public void foodContainersUseVanillaRemainderComponents(GameTestHelper helper) {
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        player.getActiveEffectsMap().put(ModEffects.VITALITY,
                new MobEffectInstance(ModEffects.VITALITY, Integer.MAX_VALUE));

        ItemStack bambooRemainder = new ItemStack(ModItems.BAMBOO_TUBE_RICE)
                .finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(bambooRemainder.is(Items.BAMBOO),
                "Bamboo tube rice did not convert into bamboo through USE_REMAINDER");

        ItemStack tea = new ItemStack(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA), 2);
        helper.assertTrue(tea.has(DataComponents.USE_REMAINDER),
                "Tea did not register a USE_REMAINDER component");
        ItemStack remainingTea = tea.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(remainingTea.is(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA))
                        && remainingTea.getCount() == 1,
                "Drinking stacked tea did not preserve the remaining drink stack");
        helper.assertValueEqual(countItem(player, ModItems.EMPTY_CUP), 1,
                "Drinking stacked tea did not insert the empty cup through vanilla use handling");

        ItemStack lastTea = new ItemStack(TeacupRegistry.getItem(TeacupRegistry.BARLEY_TEA));
        ItemStack emptyCup = lastTea.finishUsingItem(helper.getLevel(), player);
        helper.assertTrue(emptyCup.is(ModItems.EMPTY_CUP),
                "Drinking the last tea did not replace it with an empty cup");

        ItemStack namedBowl = Items.BOWL.getDefaultInstance();
        namedBowl.set(DataComponents.CUSTOM_NAME, Component.literal("Preserved remainder"));
        ItemStack componentCarrier = Items.APPLE.getDefaultInstance();
        componentCarrier.set(DataComponents.USE_REMAINDER,
                new UseRemainder(ItemStackTemplate.fromNonEmptyStack(namedBowl)));
        helper.assertTrue(ItemStack.isSameItemSameComponents(
                        ItemUtils.getContainerStack(componentCarrier), namedBowl),
                "Container lookup discarded remainder stack components");
        helper.assertTrue(ItemUtils.getContainerStack(Items.APPLE.getDefaultInstance()).isEmpty(),
                "Container lookup did not accept an item without a crafting remainder");
        helper.succeed();
    }

    @GameTest
    public void rawDoughTransformationUsesVanillaConsumption(GameTestHelper helper) {
        RawDoughItem rawDough = (RawDoughItem) ModItems.RAW_DOUGH;
        Player survivalPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack survivalDough = new ItemStack(rawDough, 3);
        int survivalTimeLeft = rawDough.getUseDuration(survivalDough, survivalPlayer) - 30;

        helper.assertTrue(rawDough.releaseUsing(
                        survivalDough, helper.getLevel(), survivalPlayer, survivalTimeLeft),
                "Fully pulled survival dough did not transform");
        helper.assertTrue(survivalDough.isEmpty(),
                "Survival dough was not consumed through vanilla item handling");
        helper.assertValueEqual(countItem(survivalPlayer, ModItems.RAW_NOODLES), 3,
                "Survival dough did not preserve its stack count when transformed");

        Player creativePlayer = helper.makeMockPlayer(GameType.CREATIVE);
        creativePlayer.getAbilities().instabuild = true;
        ItemStack creativeDough = new ItemStack(rawDough, 3);
        int creativeTimeLeft = rawDough.getUseDuration(creativeDough, creativePlayer) - 30;

        helper.assertTrue(rawDough.releaseUsing(
                        creativeDough, helper.getLevel(), creativePlayer, creativeTimeLeft),
                "Fully pulled creative dough did not transform");
        helper.assertValueEqual(creativeDough.getCount(), 3,
                "Creative dough was consumed during transformation");
        helper.assertValueEqual(countItem(creativePlayer, ModItems.RAW_NOODLES), 1,
                "Creative dough produced more than one transformed item");
        helper.succeed();
    }

    @GameTest
    public void baoziImpactHealsWolfAndRemovesProjectile(GameTestHelper helper) {
        Wolf wolf = helper.spawn(EntityTypes.WOLF, new BlockPos(3, 1, 1));
        wolf.setNoAi(true);
        wolf.setHealth(1.0F);

        Player owner = helper.makeMockPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, owner, new BlockPos(0, 1, 1));
        ThrowableBaoziEntity baozi = new ThrowableBaoziEntity(helper.getLevel(), owner);
        BlockPos start = helper.absolutePos(new BlockPos(1, 1, 1));
        baozi.setPos(start.getX() + 0.5, start.getY() + 0.8, start.getZ() + 0.5);
        baozi.shoot(1.0, 0.0, 0.0, 1.0F, 0.0F);
        helper.assertTrue(helper.getLevel().addFreshEntity(baozi),
                "Baozi projectile could not be added to the test world");

        helper.runAfterDelay(5, () -> {
            helper.assertValueEqual(wolf.getHealth(), wolf.getMaxHealth(),
                    "Baozi impact did not fully heal the wolf");
            helper.assertTrue(baozi.isRemoved(),
                    "Baozi projectile remained after an entity impact");
            helper.succeed();
        });
    }

    @GameTest
    public void serverboundActionPayloadsRoundTripWithoutClientFields(GameTestHelper helper) {
        RegistryFriendlyByteBuf flatulenceBuffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(), helper.getLevel().registryAccess());
        RegistryFriendlyByteBuf baoziBuffer = new RegistryFriendlyByteBuf(
                Unpooled.buffer(), helper.getLevel().registryAccess());
        try {
            FlatulenceMessage.STREAM_CODEC.encode(flatulenceBuffer, FlatulenceMessage.INSTANCE);
            ThrowBaoziMessage.STREAM_CODEC.encode(baoziBuffer, ThrowBaoziMessage.INSTANCE);
            helper.assertValueEqual(flatulenceBuffer.readableBytes(), 0,
                    "Flatulence payload serialized client-controlled fields");
            helper.assertValueEqual(baoziBuffer.readableBytes(), 0,
                    "Baozi payload serialized client-controlled fields");
            helper.assertTrue(FlatulenceMessage.STREAM_CODEC.decode(flatulenceBuffer) == FlatulenceMessage.INSTANCE,
                    "Flatulence unit payload did not round-trip");
            helper.assertTrue(ThrowBaoziMessage.STREAM_CODEC.decode(baoziBuffer) == ThrowBaoziMessage.INSTANCE,
                    "Baozi unit payload did not round-trip");
        } finally {
            flatulenceBuffer.release();
            baoziBuffer.release();
        }
        helper.succeed();
    }

    @GameTest
    public void networkActionsValidateServerStateAndThrottleSameTick(GameTestHelper helper) {
        ServerPlayer flatulencePlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, flatulencePlayer, new BlockPos(1, 1, 1));
        double initialVelocity = flatulencePlayer.getDeltaMovement().y;
        invokeNetworkHandler("handleFlatulence", flatulencePlayer);
        helper.assertValueEqual(flatulencePlayer.getDeltaMovement().y, initialVelocity,
                "Flatulence action trusted a client without the server-side effect");

        flatulencePlayer.getActiveEffectsMap().put(ModEffects.FLATULENCE,
                new MobEffectInstance(ModEffects.FLATULENCE, 200));
        invokeNetworkHandler("handleFlatulence", flatulencePlayer);
        helper.assertValueEqual(flatulencePlayer.getDeltaMovement().y, initialVelocity + 0.75,
                "Flatulence action did not apply the baseline jump");
        invokeNetworkHandler("handleFlatulence", flatulencePlayer);
        helper.assertValueEqual(flatulencePlayer.getDeltaMovement().y, initialVelocity + 0.75,
                "Flatulence action accepted two packets in the same tick");

        ServerPlayer baoziPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, baoziPlayer, new BlockPos(3, 1, 1));
        replaceMockPlayerCooldowns(baoziPlayer);
        ItemStack baozis = new ItemStack(ModItems.BAOZI, 2);
        baoziPlayer.setItemInHand(InteractionHand.MAIN_HAND, baozis);
        AABB projectileArea = baoziPlayer.getBoundingBox().inflate(16.0);
        invokeNetworkHandler("handleThrowBaozi", baoziPlayer);
        helper.assertValueEqual(baozis.getCount(), 2,
                "Baozi action trusted a client without server-side sneaking");
        helper.assertTrue(helper.getLevel().getEntitiesOfClass(
                        ThrowableBaoziEntity.class, projectileArea).isEmpty(),
                "Baozi action spawned a projectile without server-side sneaking");

        baoziPlayer.setShiftKeyDown(true);
        invokeNetworkHandler("handleThrowBaozi", baoziPlayer);
        helper.assertValueEqual(baozis.getCount(), 1,
                "Baozi action consumed the wrong held-item count");
        helper.assertValueEqual(helper.getLevel().getEntitiesOfClass(
                        ThrowableBaoziEntity.class, projectileArea).size(), 1,
                "Baozi action did not spawn exactly one projectile");
        invokeNetworkHandler("handleThrowBaozi", baoziPlayer);
        helper.assertValueEqual(baozis.getCount(), 1,
                "Baozi action accepted two packets during its one-tick cooldown");
        helper.assertValueEqual(helper.getLevel().getEntitiesOfClass(
                        ThrowableBaoziEntity.class, projectileArea).size(), 1,
                "Baozi cooldown allowed a second projectile in the same tick");
        helper.succeed();
    }

    @GameTest
    public void scarecrowDeathReleasesFreshShoulderEntity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        ScarecrowEntity scarecrow = helper.spawn(ModEntities.SCARECROW, pos);
        CompoundTag parrot = new CompoundTag();
        parrot.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(EntityTypes.PARROT).toString());
        parrot.putInt("Variant", 2);
        scarecrow.setShoulderEntity(parrot);

        scarecrow.kill(helper.getLevel());

        helper.assertTrue(scarecrow.isRemoved(), "Killed scarecrow remained in the world");
        helper.assertTrue(scarecrow.getShoulderEntity().isEmpty(),
                "Killed scarecrow retained its shoulder entity data");
        helper.assertEntityPresent(EntityTypes.PARROT,
                new AABB(pos).inflate(2.0));
        helper.succeed();
    }

    @GameTest
    public void scarecrowEquipmentUsesServerSwingResult(GameTestHelper helper) {
        ScarecrowEntity scarecrow = helper.spawn(ModEntities.SCARECROW, new BlockPos(1, 1, 1));
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack skulls = new ItemStack(Items.SKELETON_SKULL, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, skulls);

        InteractionResult result = scarecrow.interact(
                player, InteractionHand.MAIN_HAND, new Vec3(0.0, 2.0, 0.0));

        helper.assertValueEqual(result, InteractionResult.SUCCESS_SERVER,
                "Scarecrow equipment interaction did not request a server-authoritative swing");
        helper.assertTrue(scarecrow.getItemBySlot(EquipmentSlot.HEAD).is(Items.SKELETON_SKULL),
                "Scarecrow did not equip the interacted skull");
        helper.assertValueEqual(skulls.getCount(), 1,
                "Scarecrow equipment interaction consumed the wrong stack count");
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
    public void itemStackContainersUseVanillaInsertion(GameTestHelper helper) {
        ItemStackContainer items = new ItemStackContainer(2);
        items.set(0, new ItemStack(Items.APPLE, 63));
        ItemStack input = new ItemStack(Items.APPLE, 3);

        ItemStack remainder = items.addItem(input);
        helper.assertTrue(remainder.isEmpty(), "Container did not accept the full input stack");
        helper.assertValueEqual(items.get(0).getCount(), 64,
                "Container did not merge into the matching stack first");
        helper.assertValueEqual(items.get(1).getCount(), 2,
                "Container did not move the remainder into an empty slot");
        helper.assertValueEqual(input.getCount(), 3,
                "Container insertion mutated the caller's input stack");

        ItemStack namedApple = Items.APPLE.getDefaultInstance();
        namedApple.set(DataComponents.CUSTOM_NAME, Component.literal("Separate stack"));
        ItemStackContainer componentSensitive = new ItemStackContainer(1);
        componentSensitive.set(0, namedApple);
        ItemStack componentRemainder = componentSensitive.addItem(Items.APPLE.getDefaultInstance());
        helper.assertValueEqual(componentRemainder.getCount(), 1,
                "Container merged stacks with different components");
        helper.assertTrue(ItemStack.isSameItemSameComponents(componentSensitive.get(0), namedApple),
                "Rejected insertion changed the existing component-bearing stack");
        helper.succeed();
    }

    @GameTest
    public void itemContainerComponentsReadLegacyContents(GameTestHelper helper) {
        ItemStack namedApple = Items.APPLE.getDefaultInstance();
        namedApple.set(DataComponents.CUSTOM_NAME, Component.literal("Legacy contents"));
        List<ItemStack> legacyItems = List.of(
                new ItemStack(Items.CARROT, 2), ItemStack.EMPTY, namedApple);
        var ops = RegistryOps.create(JsonOps.INSTANCE, helper.getLevel().registryAccess());
        var legacyJson = ItemStack.OPTIONAL_CODEC.listOf().encodeStart(ops, legacyItems).getOrThrow();
        var contents = ItemStackContainer.CONTENTS_CODEC.parse(ops, legacyJson).getOrThrow();
        ItemStackContainer decoded = ItemStackContainer.fromContents(contents, 8);

        helper.assertTrue(decoded.get(0).is(Items.CARROT) && decoded.get(0).getCount() == 2,
                "Legacy component contents lost the first stack");
        helper.assertTrue(decoded.get(1).isEmpty(),
                "Legacy component contents lost an empty slot");
        helper.assertTrue(ItemStack.isSameItemSameComponents(decoded.get(2), namedApple),
                "Legacy component contents lost item components");
        helper.succeed();
    }

    @GameTest
    public void lunchBagOmitsEmptyContainerComponents(GameTestHelper helper) {
        ItemStack bag = ModItems.TRANSMUTATION_LUNCH_BAG.getDefaultInstance();
        bag.set(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS,
                ItemContainerContents.EMPTY);
        helper.assertFalse(TransmutationLunchBagItem.hasItems(bag),
                "Empty lunch-bag contents were reported as stored items");

        ItemStackContainer items = new ItemStackContainer(16);
        TransmutationLunchBagItem.setItems(bag, items);
        helper.assertFalse(bag.has(ModDataComponents.TRANSMUTATION_LUNCH_BAG_ITEMS),
                "Empty lunch-bag contents left a data component behind");

        items.set(0, new ItemStack(Items.APPLE));
        TransmutationLunchBagItem.setItems(bag, items);
        helper.assertTrue(TransmutationLunchBagItem.hasItems(bag),
                "Non-empty lunch-bag contents were not stored");
        helper.succeed();
    }

    @GameTest
    public void recipeRecordComponentsExposeDefensiveCopies(GameTestHelper helper) {
        ItemStack sourceInput = new ItemStack(Items.CARROT, 2);
        ItemStack sourceOutput = Items.SUSPICIOUS_STEW.getDefaultInstance();
        sourceOutput.set(DataComponents.CUSTOM_NAME, Component.literal("Recorded result"));
        RecipeItem.RecipeRecord record = new RecipeItem.RecipeRecord(
                List.of(sourceInput), sourceOutput, RecipeItem.POT);

        sourceInput.setCount(1);
        sourceOutput.remove(DataComponents.CUSTOM_NAME);
        List<ItemStack> exposedInputs = record.input();
        ItemStack exposedOutput = record.output();
        exposedInputs.getFirst().setCount(1);
        exposedOutput.remove(DataComponents.CUSTOM_NAME);

        helper.assertValueEqual(record.input().getFirst().getCount(), 2,
                "Recipe record exposed its mutable input stack");
        helper.assertTrue(record.output().has(DataComponents.CUSTOM_NAME),
                "Recipe record exposed its mutable output stack");
        helper.succeed();
    }

    @GameTest
    public void recipeItemConsumesGenericTransferContainerContents(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POT);
        BlockPos absolutePos = helper.absolutePos(pos);
        helper.getLevel().setBlockAndUpdate(absolutePos,
                helper.getLevel().getBlockState(absolutePos).setValue(PotBlock.HAS_OIL, true));
        PotBlockEntity pot = helper.getBlockEntity(pos, PotBlockEntity.class);

        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack recipeItem = ModItems.RECIPE_ITEM.getDefaultInstance();
        RecipeItem.setRecipe(recipeItem, RecipeItem.RecipeRecord.pot(
                Items.SUSPICIOUS_STEW, Items.APPLE, Items.APPLE));
        player.setItemInHand(InteractionHand.MAIN_HAND, recipeItem);

        ItemStack shulkerBox = Items.SHULKER_BOX.getDefaultInstance();
        shulkerBox.set(DataComponents.CONTAINER,
                ItemContainerContents.fromItems(List.of(new ItemStack(Items.APPLE, 3))));
        player.getInventory().setItem(1, shulkerBox);
        BlockHitResult hitResult = new BlockHitResult(
                Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);

        InteractionResult result = ModItems.RECIPE_ITEM.useOn(
                new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult));

        helper.assertValueEqual(result, InteractionResult.CONSUME,
                "Recipe item did not accept ingredients stored in a generic item storage");
        long potApples = pot.getInputs().stream().filter(stack -> stack.is(Items.APPLE)).count();
        helper.assertValueEqual(potApples, 2L,
                "Recipe item did not put both container-supplied ingredients into the pot");
        ItemStack storedShulker = player.getInventory().getItem(1);
        int remainingApples = storedShulker.getOrDefault(DataComponents.CONTAINER,
                        ItemContainerContents.EMPTY)
                .nonEmptyItemCopyStream()
                .filter(stack -> stack.is(Items.APPLE))
                .mapToInt(ItemStack::getCount)
                .sum();
        helper.assertTrue(storedShulker.is(Items.SHULKER_BOX),
                "Generic container deduction replaced the outer shulker box");
        helper.assertValueEqual(remainingApples, 1,
                "Generic container deduction did not commit the shulker contents mutation");
        helper.succeed();
    }

    @GameTest
    public void chefRecipeTradeBuildsRecipeRecordWhenOfferIsGenerated(GameTestHelper helper) {
        ResourceKey<VillagerTrade> tradeKey = ResourceKey.create(
                Registries.VILLAGER_TRADE, id("chef/2/emerald_recipe_braised_beef"));
        VillagerTrade trade = helper.getLevel().registryAccess()
                .lookupOrThrow(Registries.VILLAGER_TRADE)
                .getValueOrThrow(tradeKey);
        Villager villager = helper.spawn(EntityTypes.VILLAGER, new BlockPos(1, 1, 1));
        LootParams lootParams = new LootParams.Builder(helper.getLevel())
                .withParameter(LootContextParams.ORIGIN, villager.position())
                .withParameter(LootContextParams.THIS_ENTITY, villager)
                .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
                .create(LootContextParamSets.VILLAGER_TRADE);
        LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());
        MerchantOffer offer = trade.getOffer(lootContext);

        helper.assertTrue(offer != null, "Chef recipe trade did not generate an offer");
        helper.assertTrue(offer.getBaseCostA().is(Items.EMERALD) && offer.getBaseCostA().getCount() == 3,
                "Chef recipe trade lost its emerald price");
        helper.assertValueEqual(offer.getMaxUses(), 16, "Chef recipe trade max uses changed");
        helper.assertValueEqual(offer.getXp(), 4, "Chef recipe trade XP changed");

        ItemStack result = offer.assemble();
        helper.assertTrue(result.is(ModItems.RECIPE_ITEM), "Chef recipe trade did not give a recipe item");
        RecipeItem.RecipeRecord record = RecipeItem.getRecipe(result);
        helper.assertTrue(record != null, "Chef recipe trade result has no recipe record");
        List<ItemStack> inputs = record.input();
        helper.assertValueEqual(inputs.size(), 4, "Chef recipe trade ingredient count changed");
        helper.assertTrue(inputs.get(0).is(ModItems.RAW_COW_OFFAL)
                        && inputs.get(1).is(ModItems.RAW_COW_OFFAL)
                        && inputs.get(2).is(ModItems.GREEN_CHILI)
                        && inputs.get(3).is(ModItems.GREEN_CHILI),
                "Chef recipe trade ingredient order changed");
        helper.assertTrue(inputs.stream().allMatch(stack -> stack.getCount() == 1),
                "Chef recipe trade ingredient stack counts changed");
        helper.assertTrue(record.output().is(ModItems.BRAISED_BEEF),
                "Chef recipe trade recorded output changed");
        helper.assertValueEqual(record.type(), RecipeItem.POT,
                "Chef recipe trade recorded cooking type changed");
        helper.succeed();
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

        int registeredSoupBases = soupBases.size();
        boolean legacyRegistrationRejected = false;
        try {
            SoupBaseManager.registerFluidSoupBase(vanillaId("water_bucket"), Items.WATER_BUCKET, 0x3F76E4);
        } catch (IllegalArgumentException expected) {
            legacyRegistrationRejected = true;
        }
        helper.assertTrue(legacyRegistrationRejected,
                "Soup-base registry accepted an unreachable legacy alias");
        helper.assertValueEqual(soupBases.size(), registeredSoupBases,
                "Rejected soup-base alias polluted the live registry view");

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
    public void simpleSoupBaseCopiesDisplayStacks(GameTestHelper helper) {
        ItemStack source = new ItemStack(Items.HONEY_BOTTLE, 2);
        SimpleSoupBase soupBase = new SimpleSoupBase(
                id("test_soup_base"), source, id("block/test_soup_base"), 0xFFFFFF,
                stack -> true, stack -> true,
                (level, user, stack) -> ItemStack.EMPTY,
                (level, user, stack) -> ItemStack.EMPTY);

        source.setCount(1);
        ItemStack exposed = soupBase.getDisplayStack();
        exposed.setCount(1);

        helper.assertValueEqual(soupBase.getDisplayStack().getCount(), 2,
                "Simple soup base exposed its mutable display stack");
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
        boolean previousSetting = config.satiatedShieldAbsorbExcessDamage();
        int previousMinFoodLevel = config.satiatedShieldMinFoodLevel();
        boolean damaged;
        try {
            GeneralConfigTestAccess.setSatiatedShieldAbsorbExcessDamage(config, false);
            GeneralConfigTestAccess.setSatiatedShieldMinFoodLevel(config, 1);
            damaged = player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 8.0F);
        } finally {
            GeneralConfigTestAccess.setSatiatedShieldAbsorbExcessDamage(config, previousSetting);
            GeneralConfigTestAccess.setSatiatedShieldMinFoodLevel(config, previousMinFoodLevel);
        }

        helper.assertFalse(damaged, "Satiated shield did not cancel the original damage call");
        helper.assertValueEqual(player.getHealth(), initialHealth - 4.0F,
                "Damage beyond the available hunger shield was not preserved");
        helper.succeed();
    }

    @GameTest
    public void satiatedShieldRespectsForgeActivationGates(GameTestHelper helper) {
        Player lowFoodPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        lowFoodPlayer.getFoodData().setFoodLevel(3);
        lowFoodPlayer.getActiveEffectsMap().put(ModEffects.SATIATED_SHIELD,
                new MobEffectInstance(ModEffects.SATIATED_SHIELD, 200));
        float lowFoodInitialHealth = lowFoodPlayer.getHealth();

        boolean lowFoodDamaged = lowFoodPlayer.hurtServer(
                helper.getLevel(), helper.getLevel().damageSources().generic(), 4.0F);
        helper.assertTrue(lowFoodDamaged, "Satiated shield ignored the Forge minimum-food-level setting");
        helper.assertValueEqual(lowFoodPlayer.getHealth(), lowFoodInitialHealth - 4.0F,
                "Satiated shield absorbed damage below the configured food threshold");

        Player hungryPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        hungryPlayer.getFoodData().setFoodLevel(20);
        hungryPlayer.getActiveEffectsMap().put(ModEffects.SATIATED_SHIELD,
                new MobEffectInstance(ModEffects.SATIATED_SHIELD, 200));
        hungryPlayer.getActiveEffectsMap().put(MobEffects.HUNGER,
                new MobEffectInstance(MobEffects.HUNGER, 200));
        float hungryInitialHealth = hungryPlayer.getHealth();

        boolean hungryDamaged = hungryPlayer.hurtServer(
                helper.getLevel(), helper.getLevel().damageSources().generic(), 4.0F);
        helper.assertTrue(hungryDamaged, "Satiated shield ignored the Forge Hunger-effect setting");
        helper.assertValueEqual(hungryPlayer.getHealth(), hungryInitialHealth - 4.0F,
                "Satiated shield absorbed damage while the Hunger effect disabled it");
        helper.succeed();
    }

    @GameTest
    public void vigorClearsAllExhaustionEachEffectTickWhileSprinting(GameTestHelper helper) {
        float initialExhaustion = 3.95F;
        ServerPlayer vigorPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        vigorPlayer.setSprinting(true);
        vigorPlayer.causeFoodExhaustion(initialExhaustion);

        helper.assertTrue(ModEffects.VIGOR.value().shouldApplyEffectTickThisTick(200, 0),
                "Vigor no longer applies on every effect tick");
        ModEffects.VIGOR.value().applyEffectTick(helper.getLevel(), vigorPlayer, 0);

        float vigorExhaustion = foodDataTag(vigorPlayer).getFloatOr("foodExhaustionLevel", -1.0F);
        helper.assertValueEqual(vigorExhaustion, 0.0F,
                "Vigor did not clear total exhaustion while sprinting");

        ServerPlayer walkingPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        walkingPlayer.setSprinting(false);
        walkingPlayer.causeFoodExhaustion(initialExhaustion);
        ModEffects.VIGOR.value().applyEffectTick(helper.getLevel(), walkingPlayer, 0);
        helper.assertValueEqual(
                foodDataTag(walkingPlayer).getFloatOr("foodExhaustionLevel", -1.0F),
                initialExhaustion,
                "Vigor cleared exhaustion while the player was not sprinting");

        ServerPlayer controlPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        controlPlayer.setOnGround(true);
        controlPlayer.setSprinting(true);
        controlPlayer.causeFoodExhaustion(initialExhaustion);

        controlPlayer.checkMovementStatistics(1.0, 0.0, 0.0);

        float controlExhaustion = foodDataTag(controlPlayer).getFloatOr("foodExhaustionLevel", -1.0F);
        helper.assertTrue(controlExhaustion > initialExhaustion,
                "Sprint movement no longer adds vanilla exhaustion without Vigor");
        helper.succeed();
    }

    @GameTest
    public void warmthEffectFindsHeatSourcesAtScanBoundary(GameTestHelper helper) {
        BlockPos playerPos = new BlockPos(1, 1, 1);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, playerPos);
        player.setHealth(player.getMaxHealth() - 2.0F);
        helper.setBlock(playerPos.offset(2, 1, 2),
                Blocks.FURNACE.defaultBlockState().setValue(BlockStateProperties.LIT, true));

        float initialHealth = player.getHealth();
        ((WarmthEffect) ModEffects.WARMTH.value()).applyEffectTick(helper.getLevel(), player, 0);

        helper.assertValueEqual(player.getHealth(), initialHealth + 1.0F,
                "Warmth effect did not find a lit block at the scan boundary");
        helper.succeed();
    }

    @GameTest
    public void caterpillarFeedUsesServerInteractionResult(GameTestHelper helper) {
        Chicken chicken = helper.spawn(EntityTypes.CHICKEN, new BlockPos(1, 1, 1));
        chicken.setAge(-24000);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack caterpillars = new ItemStack(ModItems.CATERPILLAR, 2);
        player.setItemInHand(InteractionHand.MAIN_HAND, caterpillars);

        InteractionResult result = UseEntityCallback.EVENT.invoker().interact(
                player, helper.getLevel(), InteractionHand.MAIN_HAND, chicken, new EntityHitResult(chicken));

        helper.assertValueEqual(result, InteractionResult.SUCCESS_SERVER,
                "Caterpillar feeding did not request a server-authoritative swing");
        helper.assertFalse(chicken.isBaby(), "Caterpillar feeding did not mature the baby chicken");
        helper.assertValueEqual(caterpillars.getCount(), 1,
                "Caterpillar feeding consumed the wrong stack count");
        helper.assertValueEqual(UseEntityCallback.EVENT.invoker().interact(
                        player, helper.getLevel(), InteractionHand.MAIN_HAND, chicken, new EntityHitResult(chicken)),
                InteractionResult.PASS, "Caterpillar feeding intercepted an adult chicken");
        helper.assertValueEqual(caterpillars.getCount(), 1,
                "Rejected caterpillar feeding mutated the source stack");
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

    @GameTest
    public void sickleHarvestReplantsMatureNetherWart(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, Blocks.NETHER_WART.defaultBlockState().setValue(NetherWartBlock.AGE, 3));
        Player player = helper.makeMockServerPlayer(GameType.SURVIVAL);
        BlockPos absolutePos = helper.absolutePos(pos);
        BlockState matureState = helper.getLevel().getBlockState(absolutePos);

        SickleHarvestCallback.Result result = SickleHarvestCallback.EVENT.invoker().harvest(
                player, ItemStack.EMPTY, absolutePos, matureState);

        helper.assertValueEqual(result, SickleHarvestCallback.Result.HARVESTED,
                "Mature nether wart did not report a completed sickle harvest");
        helper.assertBlockProperty(pos, NetherWartBlock.AGE, 0);
        helper.succeed();
    }

    @GameTest
    public void entityLoadInstallsCookeryGoalsOnlyOnce(GameTestHelper helper) {
        Cat cat = helper.spawn(EntityTypes.CAT, new BlockPos(1, 1, 1));
        var creeper = helper.spawn(EntityTypes.CREEPER, new BlockPos(3, 1, 1));
        String unrelatedTag = "kaleidoscope_cookery.keep_this_test_tag";
        cat.addTag("kaleidoscope_cookery.cat_lie_goal");
        cat.addTag(unrelatedTag);
        creeper.addTag("kaleidoscope_cookery.creeper_mustard_avoid_goal");
        creeper.addTag(unrelatedTag);

        ServerEntityEvents.ENTITY_LOAD.invoker().onLoad(cat, helper.getLevel());
        ServerEntityEvents.ENTITY_LOAD.invoker().onLoad(cat, helper.getLevel());
        ServerEntityEvents.ENTITY_LOAD.invoker().onLoad(creeper, helper.getLevel());
        ServerEntityEvents.ENTITY_LOAD.invoker().onLoad(creeper, helper.getLevel());

        long catGoals = cat.getGoalSelector().getAvailableGoals().stream()
                .filter(goal -> goal.getPriority() == 5 && goal.getGoal() instanceof CatLieOnBlockGoal)
                .count();
        long creeperGoals = creeper.getGoalSelector().getAvailableGoals().stream()
                .filter(goal -> goal.getPriority() == 3
                        && goal.getGoal().getClass().getSimpleName().equals("CreeperMustardAvoidGoal"))
                .count();
        helper.assertValueEqual(catGoals, 1L,
                "Cat entity-load handling did not install exactly one priority-5 Cookery lie goal");
        helper.assertValueEqual(creeperGoals, 1L,
                "Creeper entity-load handling did not install exactly one priority-3 mustard avoidance goal");
        helper.assertFalse(cat.entityTags().contains("kaleidoscope_cookery.cat_lie_goal"),
                "Cat entity-load handling did not remove the legacy persisted AI marker");
        helper.assertFalse(creeper.entityTags().contains("kaleidoscope_cookery.creeper_mustard_avoid_goal"),
                "Creeper entity-load handling did not remove the legacy persisted AI marker");
        helper.assertTrue(cat.entityTags().contains(unrelatedTag),
                "Cat legacy-marker cleanup removed an unrelated entity tag");
        helper.assertTrue(creeper.entityTags().contains(unrelatedTag),
                "Creeper legacy-marker cleanup removed an unrelated entity tag");
        helper.succeed();
    }

    @GameTest
    public void wetFieldHoeCallbackTillsAndDamagesTool(GameTestHelper helper) {
        BlockPos wetDirt = new BlockPos(1, 1, 1);
        helper.setBlock(wetDirt, Blocks.DIRT);
        helper.setBlock(wetDirt.above(), Blocks.WATER);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        ItemStack hoe = new ItemStack(Items.IRON_HOE);
        player.setItemInHand(InteractionHand.MAIN_HAND, hoe);
        BlockPos absoluteWetDirt = helper.absolutePos(wetDirt);

        InteractionResult result = UseBlockCallback.EVENT.invoker().interact(
                player,
                helper.getLevel(),
                InteractionHand.MAIN_HAND,
                new BlockHitResult(Vec3.atCenterOf(absoluteWetDirt), Direction.UP, absoluteWetDirt, false));

        helper.assertValueEqual(result, InteractionResult.CONSUME,
                "Wet-field hoe callback did not consume the server interaction");
        helper.assertBlockPresent(Blocks.FARMLAND, wetDirt);
        helper.assertValueEqual(hoe.getDamageValue(), 1,
                "Wet-field hoe callback did not charge exactly one durability");

        BlockPos dryDirt = new BlockPos(3, 1, 1);
        helper.setBlock(dryDirt, Blocks.DIRT);
        BlockPos absoluteDryDirt = helper.absolutePos(dryDirt);
        helper.assertValueEqual(UseBlockCallback.EVENT.invoker().interact(
                        player,
                        helper.getLevel(),
                        InteractionHand.MAIN_HAND,
                        new BlockHitResult(Vec3.atCenterOf(absoluteDryDirt), Direction.UP, absoluteDryDirt, false)),
                InteractionResult.PASS, "Wet-field hoe callback intercepted dry dirt");
        helper.succeed();
    }

    @GameTest
    public void hinderEffectAppliesSlownessAfterDamage(GameTestHelper helper) {
        Zombie attacker = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(1, 1, 1));
        Zombie target = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(2, 1, 1));
        attacker.addEffect(new MobEffectInstance(ModEffects.HINDER, 200));

        boolean damaged = target.hurtServer(
                helper.getLevel(), helper.getLevel().damageSources().mobAttack(attacker), 2.0F);
        MobEffectInstance slowness = target.getEffect(MobEffects.SLOWNESS);

        helper.assertTrue(damaged, "Hinder test attack did not deal damage");
        helper.assertTrue(slowness != null && slowness.getDuration() == 100 && slowness.getAmplifier() == 1,
                "Hinder damage did not apply 100 ticks of Slowness II");

        Zombie controlAttacker = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(1, 1, 3));
        Zombie controlTarget = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(2, 1, 3));
        controlTarget.hurtServer(
                helper.getLevel(), helper.getLevel().damageSources().mobAttack(controlAttacker), 2.0F);
        helper.assertTrue(!controlTarget.hasEffect(MobEffects.SLOWNESS),
                "Hinder damage handler affected an attacker without the effect");
        helper.succeed();
    }

    @GameTest
    public void vitalityKillSpawnsMatchingBaby(GameTestHelper helper) {
        Zombie killer = helper.spawn(EntityTypes.ZOMBIE, new BlockPos(1, 1, 1));
        killer.addEffect(new MobEffectInstance(ModEffects.VITALITY, 200));
        Cow cow = helper.spawn(EntityTypes.COW, new BlockPos(3, 1, 1));
        cow.setAge(0);
        AABB spawnArea = cow.getBoundingBox().inflate(1.0);

        boolean damaged = cow.hurtServer(
                helper.getLevel(), helper.getLevel().damageSources().mobAttack(killer), 100.0F);
        List<Cow> babies = helper.getLevel().getEntitiesOfClass(Cow.class, spawnArea, Cow::isBaby);

        helper.assertTrue(damaged && cow.isDeadOrDying(), "Vitality test did not kill the adult cow");
        helper.assertValueEqual(babies.size(), 1,
                "Vitality kill did not spawn exactly one matching baby cow");
        helper.succeed();
    }

    @GameTest
    public void instantSmeltingConvertsOnlyEffectLimit(GameTestHelper helper) {
        BlockPos orePos = new BlockPos(2, 1, 2);
        helper.setBlock(orePos, Blocks.COPPER_ORE);
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(2, 1, 1));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.STONE_PICKAXE));
        player.getActiveEffectsMap().put(
                ModEffects.INSTANT_SMELTING, new MobEffectInstance(ModEffects.INSTANT_SMELTING, 200, 0));
        BlockPos absoluteOrePos = helper.absolutePos(orePos);

        helper.assertTrue(player.gameMode.destroyBlock(absoluteOrePos),
                "Instant-smelting test copper ore could not be mined");
        List<ItemEntity> drops = helper.getLevel().getEntitiesOfClass(
                ItemEntity.class, new AABB(absoluteOrePos).inflate(2.0));
        int ingots = drops.stream().filter(entity -> entity.getItem().is(Items.COPPER_INGOT))
                .mapToInt(entity -> entity.getItem().getCount()).sum();
        int rawCopper = drops.stream().filter(entity -> entity.getItem().is(Items.RAW_COPPER))
                .mapToInt(entity -> entity.getItem().getCount()).sum();

        helper.assertValueEqual(ingots, 1,
                "Instant Smelting amplifier 0 did not convert exactly one copper drop");
        helper.assertTrue(rawCopper >= 1,
                "Instant Smelting discarded the raw-copper remainder after reaching its conversion limit");
        helper.succeed();
    }

    @GameTest
    public void extraEntityLootPreservesKnifeConditions(GameTestHelper helper) {
        ServerPlayer player = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        moveIntoTest(helper, player, new BlockPos(1, 1, 1));
        player.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(ModItems.IRON_KITCHEN_KNIFE));

        var donkey = helper.spawn(EntityTypes.DONKEY, new BlockPos(2, 1, 1));
        AABB donkeyDrops = donkey.getBoundingBox().inflate(1.0);
        donkey.hurtServer(helper.getLevel(), helper.getLevel().damageSources().playerAttack(player), 100.0F);
        int donkeyMeat = helper.getLevel().getEntitiesOfClass(ItemEntity.class, donkeyDrops).stream()
                .filter(entity -> entity.getItem().is(ModItems.RAW_DONKEY_MEAT))
                .mapToInt(entity -> entity.getItem().getCount()).sum();
        helper.assertTrue(donkeyMeat >= 2,
                "Knife kill did not receive both baseline donkey-meat rolls");

        var pig = helper.spawn(EntityTypes.PIG, new BlockPos(4, 1, 1));
        AABB pigDrops = pig.getBoundingBox().inflate(1.0);
        pig.hurtServer(helper.getLevel(), helper.getLevel().damageSources().playerAttack(player), 100.0F);
        int oil = helper.getLevel().getEntitiesOfClass(ItemEntity.class, pigDrops).stream()
                .filter(entity -> entity.getItem().is(ModItems.OIL))
                .mapToInt(entity -> entity.getItem().getCount()).sum();
        helper.assertTrue(oil >= 1,
                "Knife kill did not receive the baseline guaranteed pig-oil roll");

        player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        var controlDonkey = helper.spawn(EntityTypes.DONKEY, new BlockPos(6, 1, 1));
        AABB controlDrops = controlDonkey.getBoundingBox().inflate(1.0);
        controlDonkey.hurtServer(
                helper.getLevel(), helper.getLevel().damageSources().playerAttack(player), 100.0F);
        boolean controlMeat = helper.getLevel().getEntitiesOfClass(ItemEntity.class, controlDrops).stream()
                .anyMatch(entity -> entity.getItem().is(ModItems.RAW_DONKEY_MEAT));
        helper.assertFalse(controlMeat, "Donkey meat dropped without a kitchen knife");
        helper.succeed();
    }

    @GameTest
    public void flatulenceMigratesForgeStartAndUsesBlockCoordinates(GameTestHelper helper) {
        ServerPlayer legacyPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        legacyPlayer.getActiveEffectsMap().put(
                ModEffects.FLATULENCE, new MobEffectInstance(ModEffects.FLATULENCE, 200));
        CompoundTag legacyPosition = new CompoundTag();
        legacyPosition.putInt("X", -4);
        legacyPosition.putInt("Y", 71);
        legacyPosition.putInt("Z", 9);
        CompoundTag forgeData = new CompoundTag();
        forgeData.put("FlatulenceEffectStartingPosition", legacyPosition);
        CompoundTag playerData = new CompoundTag();
        playerData.put("ForgeData", forgeData);

        LegacyPlayerDataCompat.loadFlatulenceStartingPosition(
                legacyPlayer, valueInput(helper, playerData));
        helper.assertValueEqual(
                legacyPlayer.getAttached(ModAttachmentType.FLATULENCE_EFFECT_STARTING_POSITION),
                new Vec3(-4, 71, 9),
                "Forge flatulence starting position was not migrated from player ForgeData");
        MobEffectInstance removedEffect = legacyPlayer.getActiveEffectsMap().remove(ModEffects.FLATULENCE);
        ServerMobEffectEvents.AFTER_REMOVE.invoker().afterRemove(removedEffect, legacyPlayer, null);
        helper.assertFalse(legacyPlayer.hasAttached(ModAttachmentType.FLATULENCE_EFFECT_STARTING_POSITION),
                "Flatulence effect removal did not clear the migrated temporary position");

        ServerPlayer currentPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        currentPlayer.setPos(3.75, 72.5, -1.25);
        ((FlatulenceEffect) ModEffects.FLATULENCE.value()).applyEffectTick(
                helper.getLevel(), currentPlayer, 0);
        helper.assertValueEqual(
                currentPlayer.getAttached(ModAttachmentType.FLATULENCE_EFFECT_STARTING_POSITION),
                new Vec3(3, 72, -2),
                "Flatulence first tick no longer records the Forge integer block position");
        helper.succeed();
    }

    private static void prepareCarryOnPlacementArea(GameTestHelper helper, BlockPos pos) {
        helper.setBlock(pos.below(), Blocks.STONE);
        for (Direction direction : Direction.Plane.HORIZONTAL) {
            helper.setBlock(pos.relative(direction), Blocks.STONE);
        }
    }

    private static BlockState carryOnSourceState(Block block) {
        BlockState state = block.defaultBlockState();
        if (block instanceof ChairBlock) {
            return state.setValue(ChairBlock.HAS_CARPET, true);
        }
        if (block instanceof TableBlock) {
            return state.setValue(TableBlock.HAS_CARPET, true);
        }
        if (block == ModBlocks.POT) {
            return state.setValue(PotBlock.HAS_OIL, true).setValue(PotBlock.SHOW_OIL, true);
        }
        if (block == ModBlocks.STOCKPOT) {
            return state.setValue(StockpotBlock.HAS_LID, true);
        }
        return state;
    }

    private static void seedCarryOnPayload(
            GameTestHelper helper, ServerPlayer player, BlockEntity blockEntity) {
        if (blockEntity instanceof PotBlockEntity pot) {
            pot.addAllIngredients(List.of(new ItemStack(Items.BEEF, 2), new ItemStack(Items.CARROT, 3)), player);
        } else if (blockEntity instanceof StockpotBlockEntity stockpot) {
            CompoundTag tag = new CompoundTag();
            tag.put("Inputs", legacyItemHandler(9, Map.of(
                    1, legacyStack(Items.POTATO, 4),
                    7, legacyStack(Items.BEETROOT, 2))));
            tag.put("LidItem", legacyStack(ModItems.STOCKPOT_LID, 1));
            stockpot.loadCustomOnly(valueInput(helper, tag));
        } else if (blockEntity instanceof FruitBasketBlockEntity basket) {
            helper.assertTrue(basket.putOn(new ItemStack(Items.APPLE, 3), false),
                    "Could not seed Carry On fruit-basket fixture");
        } else if (blockEntity instanceof ChoppingBoardBlockEntity board) {
            CompoundTag tag = new CompoundTag();
            tag.putString("ModelId", "kaleidoscope_cookery:block/chopping_board/carry_on_fixture");
            tag.put("CurrentCutStack", legacyStack(Items.SALMON, 2));
            tag.put("ResultItem", legacyStack(Items.COOKED_SALMON, 3));
            tag.putInt("MaxCutCount", 5);
            tag.putInt("CurrentCutCount", 2);
            board.loadCustomOnly(valueInput(helper, tag));
        } else if (blockEntity instanceof KitchenwareRacksBlockEntity racks) {
            helper.assertTrue(racks.onClick(player, new ItemStack(Items.DIAMOND_SWORD), true),
                    "Could not seed Carry On kitchenware-racks fixture");
        } else if (blockEntity instanceof TeapotBlockEntity teapot) {
            CompoundTag tag = new CompoundTag();
            tag.put("Input", legacyStack(Items.WHEAT_SEEDS, 5));
            tag.putString("TeaFluidId", "minecraft:water");
            tag.put("Result", legacyStack(Items.POTION, 2));
            tag.putInt("Status", 2);
            tag.putInt("CurrentTick", 55);
            teapot.loadCustomOnly(valueInput(helper, tag));
        } else if (blockEntity instanceof TrashCanBlockEntity trashCan) {
            helper.assertTrue(trashCan.putItem(new ItemStack(Items.COBBLESTONE, 16), false),
                    "Could not seed Carry On trash-can fixture");
        } else if (blockEntity instanceof RecipeBlockEntity recipeBlock) {
            recipeBlock.setItem(new ItemStack(ModItems.RECIPE_ITEM));
        } else if (blockEntity instanceof OilPotBlockEntity oilPot) {
            helper.assertTrue(oilPot.setOilCount(17), "Could not seed Carry On oil-pot fixture");
        } else if (blockEntity instanceof ChairBlockEntity chair) {
            chair.setColor(DyeColor.RED);
        } else if (blockEntity instanceof TableBlockEntity table) {
            table.setColor(DyeColor.BLUE);
            helper.assertTrue(table.addItem(new ItemStack(Items.CAKE)),
                    "Could not seed Carry On table fixture");
        } else {
            throw new AssertionError("No Carry On payload fixture for " + blockEntity.getType());
        }
        blockEntity.setChanged();
    }

    private static CompoundTag saveCarryOnBlockEntity(GameTestHelper helper, BlockEntity blockEntity) {
        TagValueOutput output = TagValueOutput.createWithContext(
                ProblemReporter.DISCARDING, helper.getLevel().registryAccess());
        blockEntity.saveWithId(output);
        CompoundTag tag = output.buildResult();
        tag.remove("x");
        tag.remove("y");
        tag.remove("z");
        return tag;
    }

    private static void assertCarryOnStateData(
            GameTestHelper helper, BlockState before, BlockState after, Block block) {
        for (var property : before.getProperties()) {
            if (property.getValueClass() == Direction.class
                    || property.getValueClass() == Direction.Axis.class) {
                continue;
            }
            helper.assertValueEqual(after.getValue(property), before.getValue(property),
                    "Carry On changed block-state property %s on %s"
                            .formatted(property.getName(), BuiltInRegistries.BLOCK.getKey(block)));
        }
    }

    private static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(KaleidoscopeCookery.MOD_ID, path);
    }

    private static synchronized void registerMillstoneEntityStorageTestProvider() {
        if (millstoneEntityStorageTestProviderRegistered) {
            return;
        }
        MillstoneEntityItemStorage.SOURCE.registerForType(
                (cow, context) -> TEST_MILLSTONE_ENTITY_STORAGES.get(cow.getUUID()), EntityTypes.COW);
        millstoneEntityStorageTestProviderRegistered = true;
    }

    private static SingleVariantStorage<ItemVariant> testItemStorage(Item item, long amount) {
        SingleVariantStorage<ItemVariant> storage = new SingleVariantStorage<>() {
            @Override
            protected ItemVariant getBlankVariant() {
                return ItemVariant.blank();
            }

            @Override
            protected long getCapacity(ItemVariant variant) {
                return 64;
            }
        };
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(ItemVariant.of(item), amount, transaction);
            if (inserted != amount) {
                throw new AssertionError("Could not initialize millstone entity storage fixture");
            }
            transaction.commit();
        }
        return storage;
    }

    private static void invokeCreateHorseInventory(AbstractHorse horse) {
        try {
            Method method = AbstractHorse.class.getDeclaredMethod("createInventory");
            method.setAccessible(true);
            method.invoke(horse);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not initialize chested-horse inventory", exception);
        }
    }

    private static Identifier vanillaId(String path) {
        return Identifier.fromNamespaceAndPath("minecraft", path);
    }

    private static void fillInventory(Player player, net.minecraft.world.level.ItemLike item) {
        for (int slot = 0; slot < player.getInventory().getNonEquipmentItems().size(); slot++) {
            player.getInventory().setItem(slot, new ItemStack(item, item.asItem().getDefaultMaxStackSize()));
        }
    }

    private static int countItem(Player player, net.minecraft.world.level.ItemLike item) {
        return player.getInventory().countItem(item.asItem());
    }

    private static void moveIntoTest(GameTestHelper helper, Player player, BlockPos relativePos) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        player.setPos(absolutePos.getX() + 0.5, absolutePos.getY(), absolutePos.getZ() + 0.5);
    }

    private static void invokeNetworkHandler(String methodName, ServerPlayer player) {
        try {
            var method = NetworkHandler.class.getDeclaredMethod(methodName, ServerPlayer.class);
            method.setAccessible(true);
            method.invoke(null, player);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not invoke NetworkHandler." + methodName, exception);
        }
    }

    private static void invokeCompleteUsingItem(LivingEntity entity) {
        try {
            Method method = LivingEntity.class.getDeclaredMethod("completeUsingItem");
            method.setAccessible(true);
            method.invoke(entity);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not invoke LivingEntity.completeUsingItem", exception);
        }
    }

    private static Zombie createFarmerArmorZombie(GameTestHelper helper, BlockPos relativePos, boolean complete) {
        helper.setBlock(relativePos, Blocks.WATER);
        Zombie zombie = new Zombie(helper.getLevel());
        zombie.setNoAi(true);
        zombie.setPos(Vec3.atCenterOf(helper.absolutePos(relativePos)));
        zombie.setItemSlot(EquipmentSlot.HEAD, new ItemStack(ModItems.STRAW_HAT));
        zombie.setItemSlot(EquipmentSlot.CHEST, new ItemStack(ModItems.FARMER_CHEST_PLATE));
        zombie.setItemSlot(EquipmentSlot.LEGS, new ItemStack(ModItems.FARMER_LEGGINGS));
        if (complete) {
            zombie.setItemSlot(EquipmentSlot.FEET, new ItemStack(ModItems.FARMER_BOOTS));
        }
        helper.assertTrue(helper.getLevel().addFreshEntity(zombie),
                "Farmer armor test zombie could not be added to the test world");
        return zombie;
    }

    private static void replaceMockPlayerCooldowns(ServerPlayer player) {
        try {
            var field = Player.class.getDeclaredField("cooldowns");
            field.setAccessible(true);
            field.set(player, new net.minecraft.world.item.ItemCooldowns());
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not replace mock player's networked cooldowns", exception);
        }
    }

    private static ProjectileDeflection invokeProjectileImpact(Projectile projectile, EntityHitResult hitResult) {
        try {
            Method method = Projectile.class.getDeclaredMethod("hitTargetOrDeflectSelf",
                    net.minecraft.world.phys.HitResult.class);
            method.setAccessible(true);
            return (ProjectileDeflection) method.invoke(projectile, hitResult);
        } catch (ReflectiveOperationException exception) {
            throw new AssertionError("Could not invoke the Minecraft 26.2 projectile impact method", exception);
        }
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

    private static void assertRichLegacyStack(GameTestHelper helper, ItemStack stack) {
        helper.assertTrue(stack.is(Items.DIAMOND_SWORD), "Rich Forge stack changed its item ID");
        helper.assertValueEqual(stack.getDamageValue(), 17, "Rich Forge stack lost its damage");
        helper.assertValueEqual(stack.getHoverName().getString(), "Migration blade",
                "Rich Forge stack lost its custom name");
        var sharpness = helper.getLevel().registryAccess().lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.SHARPNESS);
        ItemEnchantments enchantments = stack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        helper.assertValueEqual(enchantments.getLevel(sharpness), 3,
                "Rich Forge stack lost its Sharpness level");
        CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
        helper.assertTrue(customData != null
                        && customData.copyTag().getStringOr("migration_marker", "").equals("preserve-me"),
                "Rich Forge stack lost its custom NBT");
    }

    private static ValueInput valueInput(GameTestHelper helper, CompoundTag tag) {
        return TagValueInput.create(ProblemReporter.DISCARDING, helper.getLevel().registryAccess(), tag);
    }

    private static CompoundTag legacyStack(net.minecraft.world.level.ItemLike item, int count) {
        CompoundTag stack = new CompoundTag();
        stack.putString("id", BuiltInRegistries.ITEM.getKey(item.asItem()).toString());
        stack.putByte("Count", (byte) count);
        return stack;
    }

    private static CompoundTag legacyItemHandler(int size, Map<Integer, CompoundTag> entries) {
        CompoundTag handler = new CompoundTag();
        handler.putInt("Size", size);
        ListTag items = new ListTag();
        entries.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(entry -> {
            CompoundTag stack = entry.getValue().copy();
            stack.putByte("Slot", entry.getKey().byteValue());
            items.add(stack);
        });
        handler.put("Items", items);
        return handler;
    }
}
