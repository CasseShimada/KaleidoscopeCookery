package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IStockpot;
import com.github.ysbbbbbb.kaleidoscopecookery.api.recipe.soupbase.ISoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.StockpotBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.compat.farmersdelight.FarmersDelightCompat;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.StockpotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.FlexStockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.StockpotVisuals;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.StockpotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.FluidSoupBase;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseIds;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.soupbase.SoupBaseManager;
import com.github.ysbbbbbb.kaleidoscopecookery.init.*;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.Quality;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityEvaluator;
import com.github.ysbbbbbb.kaleidoscopecookery.item.quality.QualityUtils;
import com.github.ysbbbbbb.kaleidoscopecookery.particle.StockpotParticleOptions;
import com.github.ysbbbbbb.kaleidoscopecookery.util.BlockDrop;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import org.joml.Vector3f;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class StockpotBlockEntity extends BaseBlockEntity implements IStockpot {
    public static final int MAX_TAKEOUT_COUNT = 9;

    private static final String INPUTS = "Inputs";
    private static final String RECIPE_ID = "RecipeId";
    private static final String SOUP_BASE_ID = "SoupBaseId";
    private static final String RESULT = "Result";
    private static final String STATUS = "Status";
    private static final String CURRENT_TICK = "CurrentTick";
    private static final String TAKEOUT_COUNT = "TakeoutCount";
    private static final String LID_ITEM = "LidItem";

    private final RecipeManager.CachedCheck<StockpotInput, StockpotRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.STOCKPOT_RECIPE);
    private final RecipeManager.CachedCheck<StockpotInput, FlexStockpotRecipe> flexQuickCheck = RecipeManager.createCheck(ModRecipes.FLEX_STOCKPOT_RECIPE);

    private NonNullList<ItemStack> inputs = NonNullList.withSize(StockpotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
    private Identifier recipeId = StockpotRecipeSerializer.EMPTY_ID;
    private Identifier soupBaseId = ModSoupBases.WATER;
    private ItemStack result = ItemStack.EMPTY;
    private int status = PUT_SOUP_BASE;
    private int currentTick = -1;
    private int takeoutCount = 0;
    /**
     * 盖子，因为盖子可以当做盾牌，所以会记录很多额外内容，需要专门保存
     */
    private ItemStack lidItem = ItemStack.EMPTY;

    /**
     * 主要用于客户端渲染的字段，recipe 里缓存了数据包中定义的部分客户端渲染需要的东西
     */
    public RecipeHolder<StockpotRecipe> recipe = StockpotRecipeSerializer.getEmptyRecipe();
    public StockpotVisuals visuals = StockpotVisuals.DEFAULT;
    public @Nullable Entity renderEntity = null;

    public StockpotBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.STOCKPOT_BE, pPos, pBlockState);
    }

    public void clientTick() {
        if (this.renderEntity != null) {
            this.renderEntity.tickCount++;
        }
    }

    public StockpotInput getInput() {
        return new StockpotInput(this.inputs, this.soupBaseId);
    }

    @Override
    public boolean hasHeatSource(Level level) {
        BlockState belowState = level.getBlockState(worldPosition.below());
        if (belowState.hasProperty(BlockStateProperties.LIT) && belowState.getValue(BlockStateProperties.LIT)) {
            return true;
        }
        return belowState.is(TagMod.HEAT_SOURCE_BLOCKS_WITHOUT_LIT);
    }

    @Override
    public boolean hasLid() {
        if (level == null) {
            return false;
        }
        BlockState blockState = level.getBlockState(worldPosition);
        return level != null && blockState.hasProperty(StockpotBlock.HAS_LID)
               && blockState.getValue(StockpotBlock.HAS_LID);
    }

    public void tick(Level level) {
        // 没放入汤底时，不进行任何 tick
        if (this.status == PUT_SOUP_BASE) {
            return;
        }
        // 下方没有火源
        if (!this.hasHeatSource(level)) {
            return;
        }

        boolean hasLid = this.hasLid();
        // 音效播放
        if (level.getGameTime() % 15 == 0) {
            float volume = hasLid ? 0.075f : 0.2f;
            float pitch = hasLid ? 0.1f + level.getRandom().nextFloat() * 0.05f : 1f + level.getRandom().nextFloat() * 0.1f;
            level.playSound(null,
                    worldPosition.getX() + 0.5, worldPosition.getY() + 0.5, worldPosition.getZ() + 0.5,
                    ModSounds.BLOCK_STOCKPOT, SoundSource.BLOCKS, volume, pitch);
        }
        // 没有盖子时，不进行任何 tick，只生成粒子
        if (!hasLid) {
            this.spawnParticleWithoutLid(level);
            // 不在进行后续逻辑计算
            return;
        } else {
            // 有盖子时，生成白色粒子
            this.spawnParticleWithLid(level);
        }

        // 如果当前状态是放入素材，且素材不为空
        // 因为 isEmpty() 可能耗时，所以每隔 5 tick 检查一次
        if (status == PUT_INGREDIENT && level.getGameTime() % 5 == 0 && !this.isEmpty()) {
            this.setRecipe(level);
            status = COOKING;
            this.setChangedAndSync();
            return;
        }

        // 如果当前状态是烹饪中，递减当前 tick
        if (status == COOKING) {
            if (currentTick > 0) {
                currentTick--;
                return;
            }
            status = FINISHED;
            currentTick = -1;
            this.inputs = NonNullList.withSize(StockpotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
            this.setChangedAndSync();
        }
    }

    private void spawnParticleWithLid(Level level) {
        if (level instanceof ServerLevel serverLevel && level.getRandom().nextFloat() < 0.05F) {
            RandomSource random = serverLevel.getRandom();
            serverLevel.sendParticles(ModParticles.COOKING,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.375 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 3 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }
    }

    public void addAllIngredients(List<ItemStack> ingredients, LivingEntity user) {
        if (this.level == null) {
            return;
        }
        if (this.hasLid()) {
            return;
        }
        if (this.status != PUT_INGREDIENT) {
            return;
        }
        for (int i = 0; i < Math.min(ingredients.size(), this.inputs.size()); i++) {
            ItemStack stack = ingredients.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            // 如果带有容器，此时返还容器
            ItemStack container = ItemUtils.getContainerStack(stack);
            if (!user.hasInfiniteMaterials() && !container.isEmpty()) {
                ItemUtils.getItemToLivingEntity(user, container);
            }
            this.inputs.set(i, stack.copyWithCount(1));
        }
        level.playSound(null, this.worldPosition,
                SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
        this.setChangedAndSync();
    }

    private void spawnParticleWithoutLid(Level level) {
        if (level instanceof ServerLevel serverLevel && serverLevel.getRandom().nextFloat() < 0.25F) {
            int color = this.getBubbleColor();
            serverLevel.sendParticles(new StockpotParticleOptions(colorToVector(color), 1f),
                    worldPosition.getX() + 0.25 + (level.getRandom().nextFloat() * 0.5F),
                    worldPosition.getY() + 0.375,
                    worldPosition.getZ() + 0.25 + (level.getRandom().nextFloat() * 0.5F),
                    2,
                    (level.getRandom().nextFloat() - 0.5) * 0.1F,
                    0,
                    (level.getRandom().nextFloat() - 0.5) * 0.1F,
                    0);
        }
    }

    private static Vector3f colorToVector(int color) {
        float red = ((color >> 16) & 0xFF) / 255.0F;
        float green = ((color >> 8) & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        return new Vector3f(red, green, blue);
    }

    @Nullable
    private RecipeHolder<StockpotRecipe> getStockpotRecipeById(RecipeManager recipeManager, Level level, Identifier id) {
        Optional<RecipeHolder<?>> holder = recipeManager.byKey(ResourceKey.create(Registries.RECIPE, id));
        if (holder.isEmpty()) {
            return null;
        }
        RecipeHolder<?> rawHolder = holder.get();
        if (!(rawHolder.value() instanceof StockpotRecipe stockpotRecipe)) {
            return FarmersDelightCompat.tryTransformRecipeHolder(rawHolder, level);
        }
        return new RecipeHolder<>(rawHolder.id(), stockpotRecipe);
    }

    @Nullable
    private RecipeHolder<?> getRecipeById(RecipeManager recipeManager, Level level, Identifier id) {
        Optional<RecipeHolder<?>> holder = recipeManager.byKey(ResourceKey.create(Registries.RECIPE, id));
        if (holder.isEmpty()) {
            return null;
        }
        RecipeHolder<?> rawHolder = holder.get();
        if (rawHolder.value() instanceof StockpotRecipe || rawHolder.value() instanceof FlexStockpotRecipe) {
            return rawHolder;
        }
        return FarmersDelightCompat.tryTransformRecipeHolder(rawHolder, level);
    }

    private void refreshVisualsFromRecipeId(RecipeManager recipeManager, Level level) {
        RecipeHolder<?> holder = getRecipeById(recipeManager, level, this.recipeId);
        if (holder == null) {
            this.recipe = StockpotRecipeSerializer.getEmptyRecipe();
            this.visuals = StockpotVisuals.DEFAULT;
            return;
        }
        if (holder.value() instanceof StockpotRecipe stockpotRecipe) {
            this.recipe = new RecipeHolder<>(holder.id(), stockpotRecipe);
            this.visuals = StockpotVisuals.from(stockpotRecipe);
        } else if (holder.value() instanceof FlexStockpotRecipe flexStockpotRecipe) {
            this.recipe = StockpotRecipeSerializer.getEmptyRecipe();
            this.visuals = flexStockpotRecipe.visuals();
        }
    }

    private int getBubbleColor() {
        // 需要检查下 recipe 是否更新
        if (this.level != null && !this.recipeId.equals(StockpotRecipeSerializer.EMPTY_ID)
            && this.visuals.equals(StockpotVisuals.DEFAULT)) {
            if (this.level instanceof ServerLevel serverLevel) {
                if (serverLevel.recipeAccess() instanceof RecipeManager recipeManager) {
                    this.refreshVisualsFromRecipeId(recipeManager, serverLevel);
                }
            } else if (this.level.recipeAccess() instanceof RecipeManager recipeManager) {
                this.refreshVisualsFromRecipeId(recipeManager, this.level);
            }
        }
        if (status == COOKING) {
            return this.visuals.cookingBubbleColor();
        }
        if (status == FINISHED) {
            return this.visuals.finishedBubbleColor();
        }
        ISoupBase soup = this.getSoupBase();
        if (soup != null) {
            return soup.getBubbleColor();
        }
        return 0xffffff;
    }

    @Override
    public boolean onLitClick(Level level, LivingEntity user, ItemStack stack) {
        BlockState blockState = level.getBlockState(worldPosition);
        boolean hasLid = this.hasLid();

        // 第一种情况，放上盖子
        if (!hasLid && stack.is(ModItems.STOCKPOT_LID)) {
            this.setLidItem(user.hasInfiniteMaterials() ? stack.copyWithCount(1) : stack.split(1));
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, true));
            user.playSound(SoundEvents.LANTERN_PLACE, 0.5F, 0.5F);
            ModTrigger.EVENT.trigger(user, ModEventTriggerType.USE_LID_ON_STOCKPOT);
            return true;
        }

        // 第二种情况，取下盖子
        if (hasLid) {
            ItemStack lid = this.getLidItem();
            if (lid.isEmpty()) {
                lid = ModItems.STOCKPOT_LID.getDefaultInstance();
            }
            this.setLidItem(ItemStack.EMPTY);
            if (stack.isEmpty()) {
                user.setItemInHand(InteractionHand.MAIN_HAND, lid);
            } else {
                BlockDrop.popResource(level, worldPosition, 0.5, lid);
            }
            level.setBlockAndUpdate(worldPosition, blockState.setValue(StockpotBlock.HAS_LID, false));
            user.playSound(SoundEvents.LANTERN_BREAK, 0.5F, 0.5F);
            return true;
        }

        return false;
    }

    private void setRecipe(Level levelIn) {
        StockpotInput container = new StockpotInput(this.inputs, this.soupBaseId);
        if (!(levelIn instanceof ServerLevel serverLevel)) {
            this.applySuspiciousRecipe();
            return;
        }
        Optional<RecipeHolder<StockpotRecipe>> stockpotRecipe = this.quickCheck.getRecipeFor(container, serverLevel);
        if (stockpotRecipe.isPresent()) {
            this.applyRecipe(container, stockpotRecipe.get());
            return;
        }

        RecipeHolder<StockpotRecipe> compatRecipe = FarmersDelightCompat.findMatchingRecipe(serverLevel, container);
        if (compatRecipe != null) {
            this.applyRecipe(container, compatRecipe);
            return;
        }

        Optional<RecipeHolder<FlexStockpotRecipe>> flexRecipe = this.flexQuickCheck.getRecipeFor(container, serverLevel);
        if (flexRecipe.isPresent()) {
            this.applyFlexRecipe(serverLevel, container, flexRecipe.get());
            return;
        }

        this.applySuspiciousRecipe();
    }

    private void applySuspiciousRecipe() {
        this.recipeId = StockpotRecipeSerializer.EMPTY_ID;
        this.recipe = StockpotRecipeSerializer.getEmptyRecipe();
        this.visuals = StockpotVisuals.DEFAULT;
        this.result = Items.SUSPICIOUS_STEW.getDefaultInstance();
        this.currentTick = StockpotRecipeSerializer.DEFAULT_TIME;
        this.takeoutCount = 1;
    }

    private void applyRecipe(StockpotInput input, RecipeHolder<StockpotRecipe> recipe) {
        StockpotRecipe value = recipe.value();
        this.recipeId = recipe.id().identifier();
        this.recipe = recipe;
        this.visuals = StockpotVisuals.from(value);
        this.result = value.assemble(input);
        this.currentTick = value.time();
        this.takeoutCount = Math.min(this.result.getCount(), MAX_TAKEOUT_COUNT);
    }

    private void applyFlexRecipe(ServerLevel level, StockpotInput input, RecipeHolder<FlexStockpotRecipe> recipe) {
        FlexStockpotRecipe value = recipe.value();
        this.recipeId = recipe.id().identifier();
        this.recipe = StockpotRecipeSerializer.getEmptyRecipe();
        this.visuals = value.visuals();
        this.result = value.assemble(input);
        this.currentTick = value.time();
        this.takeoutCount = Math.min(this.result.getCount(), MAX_TAKEOUT_COUNT);

        Quality quality = QualityEvaluator.evaluate(this.inputs, value.ingredients(), recipe.id().identifier(), level.getSeed());
        QualityUtils.setQuality(this.result, quality);
    }

    @Override
    public boolean addSoupBase(Level level, LivingEntity user, ItemStack bucket) {
        // 必须打开盖子才能放入汤底
        if (this.hasLid()) {
            return false;
        }
        // 当前状态是放入汤底
        if (this.status != PUT_SOUP_BASE) {
            return false;
        }
        for (var entry : SoupBaseManager.getAllSoupBases().entrySet()) {
            Identifier key = entry.getKey();
            ISoupBase soupBase = entry.getValue();
            if (soupBase.isSoupBase(bucket)) {
                this.soupBaseId = key;
                this.status = PUT_INGREDIENT;
                this.setChangedAndSync();

                ItemStack container = soupBase.getReturnContainer(level, user, bucket);
                if (!user.hasInfiniteMaterials()) {
                    bucket.shrink(1);
                    ItemUtils.getItemToLivingEntity(user, container);
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean removeSoupBase(Level level, LivingEntity user, ItemStack bucket) {
        // 当前是放入食材，但是还没放
        if (this.status == PUT_INGREDIENT && this.isEmpty() && SoupBaseManager.containsSoupBase(this.soupBaseId)) {
            ISoupBase soupBase = this.getSoupBase();
            if (soupBase == null || !soupBase.isContainer(bucket)) {
                return false;
            }
            this.renderEntity = null;
            this.soupBaseId = ModSoupBases.WATER;
            this.status = PUT_SOUP_BASE;
            this.setChangedAndSync();

            ItemStack container = soupBase.getReturnSoupBase(level, user, bucket);
            if (!user.hasInfiniteMaterials()) {
                bucket.shrink(1);
                ItemUtils.getItemToLivingEntity(user, container);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean addIngredient(Level level, LivingEntity user, ItemStack itemStack) {
        if (this.hasLid()) {
            return false;
        }
        if (this.status != PUT_INGREDIENT) {
            return false;
        }
        if (itemStack.getItem() instanceof BucketItem) {
            return false;
        }
        if (itemStack.is(TagMod.INGREDIENT_BLOCKLIST)) {
            return false;
        }
        // 检查是否有足够的空间放入食材
        for (int i = 0; i < this.inputs.size(); i++) {
            if (!this.inputs.get(i).isEmpty()) {
                continue;
            }
            ItemStack container = ItemUtils.getContainerStack(itemStack);
            if (!user.hasInfiniteMaterials() && !container.isEmpty()) {
                ItemUtils.getItemToLivingEntity(user, container);
            }
            this.inputs.set(i, user.hasInfiniteMaterials() ? itemStack.copyWithCount(1) : itemStack.split(1));
            level.playSound(null, user.getX(), user.getY() + 0.5, user.getZ(),
                    SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F,
                    ((level.getRandom().nextFloat() - level.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
            this.setChangedAndSync();
            return true;
        }
        return false;
    }

    @Override
    public boolean removeIngredient(Level level, LivingEntity user) {
        if (hasLid()) {
            return false;
        }
        if (status != PUT_INGREDIENT) {
            return false;
        }
        for (int i = this.inputs.size() - 1; i >= 0; i--) {
            ItemStack stack = this.inputs.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            if (!containerIsMatch(user, stack)) {
                return false;
            }
            ItemUtils.getItemToLivingEntity(user, stack.copy());
            this.inputs.set(i, ItemStack.EMPTY);
            // 如果是流体汤底，且温度过高，玩家会受到伤害
            ISoupBase soupBase = this.getSoupBase();
            if (soupBase instanceof FluidSoupBase fluidSoupBase
                && fluidSoupBase.getFluid().defaultFluidState().typeHolder().is(FluidTags.LAVA)
                && level instanceof ServerLevel serverLevel) {
                user.hurtServer(serverLevel, level.damageSources().inFire(), 1.0F);
                ModTrigger.EVENT.trigger(user, ModEventTriggerType.HURT_WHEN_TAKEOUT_FROM_STOCKPOT);
            }
            this.setChangedAndSync();
            return true;
        }
        return false;
    }

    private boolean containerIsMatch(LivingEntity user, ItemStack stack) {
        ItemStack container = ItemUtils.getContainerStack(stack);
        if (container.isEmpty()) {
            return true;
        }
        if (user.getMainHandItem().is(container.getItem())) {
            if (!user.hasInfiniteMaterials()) {
                user.getMainHandItem().shrink(1);
            }
            return true;
        }
        this.sendActionBarMessage(user, "tip.kaleidoscope_cookery.kitchen.remove_ingredient.need_container",
                container.getHoverName());
        return false;
    }

    @Override
    public boolean takeOutProduct(Level level, LivingEntity user, ItemStack stack) {
        if (this.hasLid()) {
            return false;
        }
        // 如果当前状态是烹饪完成
        if (status != FINISHED || this.result.isEmpty() || this.takeoutCount <= 0) {
            return false;
        }
        Ingredient carrier = this.getCurrentCarrier(level);
        if (!carrier.isEmpty() && !carrier.test(stack)) {
            Component carrierName = ItemUtils.getIngredientName(level, carrier);
            this.sendActionBarMessage(user, "tip.kaleidoscope_cookery.pot.need_carrier", carrierName);
            return false;
        }
        if (!carrier.isEmpty() && !user.hasInfiniteMaterials()) {
            stack.shrink(1);
        }

        ItemStack resultCopy = this.result.copyWithCount(1);
        ItemUtils.getItemToLivingEntity(user, resultCopy);
        this.takeoutCount--;
        if (this.takeoutCount <= 0) {
            this.status = PUT_SOUP_BASE;
            this.inputs = NonNullList.withSize(StockpotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
            this.recipeId = StockpotRecipeSerializer.EMPTY_ID;
            this.recipe = StockpotRecipeSerializer.getEmptyRecipe();
            this.visuals = StockpotVisuals.DEFAULT;
            this.soupBaseId = ModSoupBases.WATER;
            this.result = ItemStack.EMPTY;
            this.currentTick = -1;
            this.renderEntity = null;
        }
        this.setChangedAndSync();
        return true;
    }

    private Ingredient getCurrentCarrier(Level level) {
        if (this.recipeId.equals(StockpotRecipeSerializer.EMPTY_ID)) {
            return StockpotRecipeSerializer.DEFAULT_CARRIER;
        }
        RecipeManager recipeManager = null;
        if (level instanceof ServerLevel serverLevel) {
            recipeManager = serverLevel.recipeAccess();
        } else if (level.recipeAccess() instanceof RecipeManager manager) {
            recipeManager = manager;
        }
        if (recipeManager == null) {
            return StockpotRecipeSerializer.DEFAULT_CARRIER;
        }
        RecipeHolder<?> holder = getRecipeById(recipeManager, level, this.recipeId);
        if (holder == null) {
            return StockpotRecipeSerializer.DEFAULT_CARRIER;
        }
        if (holder.value() instanceof StockpotRecipe stockpotRecipe) {
            return stockpotRecipe.carrier();
        }
        if (holder.value() instanceof FlexStockpotRecipe flexStockpotRecipe) {
            return flexStockpotRecipe.carrier();
        }
        return StockpotRecipeSerializer.DEFAULT_CARRIER;
    }

    private void sendActionBarMessage(LivingEntity user, String key, Object... args) {
        if (user instanceof ServerPlayer serverPlayer) {
            MutableComponent message = Component.translatable(key, args);
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child(INPUTS), this.inputs);
        output.putString(RECIPE_ID, this.recipeId.toString());
        output.putString(SOUP_BASE_ID, this.soupBaseId.toString());
        if (!this.result.isEmpty()) {
            output.store(RESULT, ItemStack.CODEC, this.result);
        }
        output.putInt(STATUS, this.status);
        output.putInt(CURRENT_TICK, this.currentTick);
        output.putInt(TAKEOUT_COUNT, this.takeoutCount);
        if (!this.lidItem.isEmpty()) {
            output.store(LID_ITEM, ItemStack.CODEC, this.lidItem);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.inputs = NonNullList.withSize(StockpotRecipe.RECIPES_SIZE, ItemStack.EMPTY);
        ContainerHelper.loadAllItems(input.childOrEmpty(INPUTS), this.inputs);
        this.recipeId = input.getString(RECIPE_ID)
                .flatMap(value -> Optional.ofNullable(Identifier.tryParse(value)))
                .orElse(StockpotRecipeSerializer.EMPTY_ID);
        if (this.level instanceof ServerLevel serverLevel) {
            if (serverLevel.recipeAccess() instanceof RecipeManager recipeManager) {
                RecipeHolder<StockpotRecipe> stockpotRecipe = getStockpotRecipeById(recipeManager, serverLevel, this.recipeId);
                this.recipe = Objects.requireNonNullElseGet(stockpotRecipe, StockpotRecipeSerializer::getEmptyRecipe);
                this.refreshVisualsFromRecipeId(recipeManager, serverLevel);
            }
        } else if (this.level != null && this.level.recipeAccess() instanceof RecipeManager recipeManager) {
            RecipeHolder<StockpotRecipe> stockpotRecipe = getStockpotRecipeById(recipeManager, this.level, this.recipeId);
            this.recipe = Objects.requireNonNullElseGet(stockpotRecipe, StockpotRecipeSerializer::getEmptyRecipe);
            this.refreshVisualsFromRecipeId(recipeManager, this.level);
        }
        this.soupBaseId = input.getString(SOUP_BASE_ID)
                .flatMap(value -> Optional.ofNullable(Identifier.tryParse(value)))
                .map(SoupBaseIds::normalize)
                .orElse(ModSoupBases.WATER);
        this.result = input.read(RESULT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.status = input.getIntOr(STATUS, PUT_SOUP_BASE);
        this.currentTick = input.getIntOr(CURRENT_TICK, -1);
        this.takeoutCount = input.getIntOr(TAKEOUT_COUNT, 0);
        this.lidItem = input.read(LID_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    public boolean isEmpty() {
        for (ItemStack stack : this.inputs) {
            if (!stack.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public NonNullList<ItemStack> getInputs() {
        return inputs;
    }

    @Override
    public int getStatus() {
        return status;
    }

    public int getTakeoutCount() {
        return takeoutCount;
    }

    public ItemStack getResult() {
        return result;
    }

    public Identifier getSoupBaseId() {
        return soupBaseId;
    }

    @Nullable
    public ISoupBase getSoupBase() {
        return SoupBaseManager.getSoupBase(this.soupBaseId);
    }

    public ItemStack getLidItem() {
        return this.lidItem.copy();
    }

    private void setLidItem(ItemStack lidItem) {
        this.lidItem = lidItem.copyWithCount(1);
        this.setChanged();
    }
}
