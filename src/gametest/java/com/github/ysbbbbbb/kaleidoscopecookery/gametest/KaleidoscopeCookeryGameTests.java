package com.github.ysbbbbbb.kaleidoscopecookery.gametest;

import com.github.ysbbbbbb.kaleidoscopecookery.KaleidoscopeCookery;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ActionEventCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.SickleHarvestCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.block.food.FoodBiteThreeByThreeBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.MillstoneBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.NinePart;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration.FruitBasketBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.PotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.MillstoneBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.StockpotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc.TrashCanBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfig;
import com.github.ysbbbbbb.kaleidoscopecookery.config.GeneralConfigTestAccess;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.SimpleInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.PotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SimpleSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ScarecrowEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.ThrowableBaoziEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.effect.WarmthEffect;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModDataComponents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEffects;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEvents;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSoupBases;
import com.github.ysbbbbbb.kaleidoscopecookery.init.registry.TeacupRegistry;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.ItemStackContainer;
import com.github.ysbbbbbb.kaleidoscopecookery.item.FruitBasketItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RawDoughItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.TransmutationLunchBagItem;
import com.github.ysbbbbbb.kaleidoscopecookery.item.RecipeItem;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.DefaultAttributes;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.item.component.UseRemainder;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.NetherWartBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class KaleidoscopeCookeryGameTests {
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
    public void potPlacementCreatesExpectedBlockEntity(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        helper.setBlock(pos, ModBlocks.POT);

        PotBlockEntity blockEntity = helper.getBlockEntity(pos, PotBlockEntity.class);
        helper.assertTrue(blockEntity.getType() == ModBlocks.POT_BE,
                "Placed pot created an unexpected block entity type");
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

        helper.assertValueEqual(result, InteractionResult.CONSUME,
                "Successful scarecrow placement did not consume the interaction");
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
        trashCan.putItem(firstApples, true);
        ItemStack moreApples = new ItemStack(Items.APPLE, 3);
        trashCan.putItem(moreApples, true);
        trashCan.putItem(Items.POTATO.getDefaultInstance(), true);

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
        trashCan.putItem(creativeCarrot, false);
        List<ItemStack> rotatedItems = trashCan.getStoredItems();
        helper.assertValueEqual(creativeCarrot.getCount(), 1,
                "Creative trash-can insertion mutated the source stack");
        helper.assertTrue(rotatedItems.get(0).is(Items.APPLE) && rotatedItems.get(0).getCount() == 2
                        && rotatedItems.get(1).is(Items.POTATO)
                        && rotatedItems.get(2).is(Items.CARROT),
                "Full trash can did not rotate out only its oldest stack");

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        trashCan.withdrawItem(player);
        helper.assertTrue(player.getMainHandItem().is(Items.CARROT),
                "Trash can did not withdraw its newest stack first");
        helper.assertValueEqual(trashCan.getStoredItems().size(), 2,
                "Trash can retained the withdrawn stack");
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
        boolean damaged;
        try {
            GeneralConfigTestAccess.setSatiatedShieldAbsorbExcessDamage(config, false);
            damaged = player.hurtServer(helper.getLevel(), helper.getLevel().damageSources().generic(), 4.0F);
        } finally {
            GeneralConfigTestAccess.setSatiatedShieldAbsorbExcessDamage(config, previousSetting);
        }

        helper.assertFalse(damaged, "Satiated shield did not cancel the original damage call");
        helper.assertValueEqual(player.getHealth(), initialHealth - 2.0F,
                "Damage beyond the available hunger shield was not preserved");
        helper.succeed();
    }

    @GameTest
    public void vigorOnlySuppressesSprintMovementExhaustion(GameTestHelper helper) {
        float initialExhaustion = 3.95F;
        ServerPlayer vigorPlayer = (ServerPlayer) helper.makeMockServerPlayer(GameType.SURVIVAL);
        vigorPlayer.setOnGround(true);
        vigorPlayer.setSprinting(true);
        vigorPlayer.getActiveEffectsMap().put(ModEffects.VIGOR,
                new MobEffectInstance(ModEffects.VIGOR, 200));
        vigorPlayer.causeFoodExhaustion(initialExhaustion);

        vigorPlayer.checkMovementStatistics(1.0, 0.0, 0.0);

        float vigorExhaustion = foodDataTag(vigorPlayer).getFloatOr("foodExhaustionLevel", -1.0F);
        helper.assertValueEqual(vigorExhaustion, initialExhaustion,
                "Vigor did not suppress sprint movement exhaustion");

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

    private static int countItem(Player player, net.minecraft.world.level.ItemLike item) {
        return player.getInventory().countItem(item.asItem());
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
