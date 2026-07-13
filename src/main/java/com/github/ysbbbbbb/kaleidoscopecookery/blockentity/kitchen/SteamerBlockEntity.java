package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ISteamer;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.SteamerBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.SteamerRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class SteamerBlockEntity extends BaseBlockEntity implements ISteamer {
    // 最大火力等级为 4，往上继续叠蒸笼，火力等级逐步降低
    public static final int MAX_LIT_LEVEL = 4;
    public static final String COOKING_PROGRESS_TAG = "CookingProgress";
    public static final String COOKING_TIME_TAG = "CookingTime";
    public static final String ITEMS_TAG = "Items";

    // 合成表
    private final RecipeManager.CachedCheck<SingleRecipeInput, SteamerRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.STEAMER_RECIPE);

    // 0-3 是下层蒸笼，4-7 是上层蒸笼
    private final NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
    private final int[] cookingProgress = new int[8];
    private final int[] cookingTime = new int[8];

    private int litLevel = 0;

    public SteamerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.STEAMER_BE, pos, state);
    }

    public void tick(Level level) {
        // 蒸笼每火力每 5 tick 更新一次
        if (level.getGameTime() % 5 == 0) {
            updateLitLevel(level);

            // 如果有蒸熟的，且进度为 -1，那么释放蒸汽粒子
            for (int i = 0; i < this.items.size(); i++) {
                if (this.cookingTime[i] == -1) {
                    this.makeRipeParticles(level, worldPosition);
                    break;
                }
            }
        }

        if (this.litLevel > 0) {
            cookingTick(level, worldPosition, getBlockState(), this);
        } else {
            cooldownTick(level, worldPosition, getBlockState(), this);
        }
    }

    // 合并物品，仅在放置时调用
    public void mergeItem(ItemStack stack, Level level) {
        TypedEntityData<?> typedData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        CompoundTag data = typedData == null ? new CompoundTag() : typedData.copyTagWithoutId();

        NonNullList<ItemStack> merge = NonNullList.withSize(8, ItemStack.EMPTY);
        int[] mergeCookingProgress = new int[8];
        int[] mergeCookingTime = new int[8];

        // 先尝试把物品里的数据 0-3 取出，放到 4-7
        NonNullList<ItemStack> itemsInStack = NonNullList.withSize(4, ItemStack.EMPTY);
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data);
        ContainerHelper.loadAllItems(input, itemsInStack);
        for (int i = 0; i < 4; i++) {
            merge.set(i + 4, itemsInStack.get(i));
        }
        data.getIntArray(COOKING_PROGRESS_TAG).ifPresent(times -> {
            int length = Math.min(mergeCookingProgress.length - 4, times.length);
            System.arraycopy(times, 0, mergeCookingProgress, 4, length);
        });
        data.getIntArray(COOKING_TIME_TAG).ifPresent(times -> {
            int length = Math.min(mergeCookingTime.length - 4, times.length);
            System.arraycopy(times, 0, mergeCookingTime, 4, length);
        });

        // 接着把自己的数据放到 0-3
        for (int i = 0; i < 4; i++) {
            merge.set(i, this.items.get(i));
            mergeCookingProgress[i] = this.cookingProgress[i];
            mergeCookingTime[i] = this.cookingTime[i];
        }

        // 写进 data
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        ContainerHelper.saveAllItems(output, merge, false);
        output.putIntArray(COOKING_PROGRESS_TAG, mergeCookingProgress);
        output.putIntArray(COOKING_TIME_TAG, mergeCookingTime);

        // 写回物品
        BlockItem.setBlockEntityData(stack, this.getType(), output);
        stack.set(DataComponents.MAX_STACK_SIZE, 1);
    }

    public List<ItemStack> dropAsItem(Level level) {
        return createSteamerDrops(this.getBlockState(), this.items, this.cookingProgress, this.cookingTime, this.getType(), level);
    }

    public static List<ItemStack> dropFallingSteamerAsItem(BlockState blockState, CompoundTag steamerTag, Level level) {
        NonNullList<ItemStack> items = NonNullList.withSize(8, ItemStack.EMPTY);
        int[] cookingProgress = new int[8];
        int[] cookingTime = new int[8];
        if (steamerTag != null) {
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), steamerTag);
            ContainerHelper.loadAllItems(input, items);
            steamerTag.getIntArray(COOKING_PROGRESS_TAG).ifPresent(value -> {
                int length = Math.min(cookingProgress.length, value.length);
                System.arraycopy(value, 0, cookingProgress, 0, length);
            });
            steamerTag.getIntArray(COOKING_TIME_TAG).ifPresent(value -> {
                int length = Math.min(cookingTime.length, value.length);
                System.arraycopy(value, 0, cookingTime, 0, length);
            });
        }

        return createSteamerDrops(blockState, items, cookingProgress, cookingTime, ModBlocks.STEAMER_BE, level);
    }

    private static List<ItemStack> createSteamerDrops(BlockState blockState, NonNullList<ItemStack> items,
                                                      int[] cookingProgress, int[] cookingTime,
                                                      BlockEntityType<?> blockEntityType, Level level) {
        List<ItemStack> drops = new ArrayList<>();
        boolean half = blockState.getValue(SteamerBlock.HALF);
        ItemStack first = ModItems.STEAMER.getDefaultInstance();
        if (items.stream().allMatch(ItemStack::isEmpty)) {
            drops.add(first);
            if (!half) {
                drops.add(ModItems.STEAMER.getDefaultInstance());
            }
            return drops;
        }

        TagValueOutput tag1 = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        TagValueOutput tag2 = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        saveSplit(tag1, tag2, level, items, cookingProgress, cookingTime);

        setSteamerData(first, blockEntityType, tag1);
        drops.add(first);

        if (!half) {
            ItemStack second = ModItems.STEAMER.getDefaultInstance();
            setSteamerData(second, blockEntityType, tag2);
            drops.add(second);
        }
        return drops;
    }

    public static void setSteamerData(ItemStack stack, BlockEntityType<?> type, TagValueOutput data) {
        if (data.isEmpty()) {
            return;
        }
        BlockItem.setBlockEntityData(stack, type, data);
        stack.set(DataComponents.MAX_STACK_SIZE, 1);
    }

    @Override
    public void updateLitLevel(Level level) {
        BlockPos pos = this.getBlockPos();
        if (this.hasHeatSource(level)) {
            this.litLevel = MAX_LIT_LEVEL;
        } else if (level.getBlockEntity(pos.below()) instanceof SteamerBlockEntity steamer) {
            // 下层是蒸笼，且需要是双层的才能传递火力
            if (steamer.getBlockState().getValue(SteamerBlock.HALF)) {
                this.litLevel = 0;
            } else {
                this.litLevel = Math.max(steamer.litLevel - 1, 0);
            }
        } else {
            this.litLevel = 0;
        }
    }

    @Override
    public boolean hasHeatSource(Level level) {
        BlockState belowState = level.getBlockState(worldPosition.below());
        if (belowState.hasProperty(BlockStateProperties.LIT) && belowState.getValue(BlockStateProperties.LIT)) {
            return true;
        }
        return belowState.is(TagMod.HEAT_SOURCE_BLOCKS_WITHOUT_LIT);
    }

    private void cookingTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        // 先检查上面是否是蒸笼
        BlockState aboveState = level.getBlockState(pos.above());
        boolean aboveIsSteamer = aboveState.is(ModBlocks.STEAMER);
        if (!aboveIsSteamer) {
            // 上面不是蒸笼，释放蒸汽粒子
            this.makeCookingParticles(level, pos);
            // 既没有上层蒸笼，也没有盖子，不能蒸
            if (!state.getValue(SteamerBlock.HAS_LID)) {
                return;
            }
        }

        // 开始蒸
        boolean hasCooking = false;
        boolean completedCooking = false;
        for (int i = 0; i < steamer.items.size(); i++) {
            ItemStack stack = steamer.items.get(i);
            if (stack.isEmpty() || steamer.cookingTime[i] < 0) {
                continue;
            }
            hasCooking = true;
            int progress = steamer.cookingProgress[i]++;
            if (progress < steamer.cookingTime[i]) {
                continue;
            }
            if (!(level instanceof ServerLevel serverLevel)) {
                continue;
            }
            SingleRecipeInput container = new SingleRecipeInput(stack);
            ItemStack resultStack = steamer.quickCheck.getRecipeFor(container, serverLevel)
                    .map(r -> r.value().assemble(container))
                    .orElse(stack);
            if (!resultStack.isEmpty()) {
                steamer.items.set(i, resultStack);
                // 设置为 -1 代表已经蒸熟
                steamer.cookingTime[i] = -1;
                completedCooking = true;
            }
        }
        if (completedCooking) {
            steamer.setChangedAndSync();
        } else if (hasCooking) {
            steamer.setChanged();
        }
    }

    private void cooldownTick(Level level, BlockPos pos, BlockState state, SteamerBlockEntity steamer) {
        boolean hasCooking = false;

        for (int i = 0; i < steamer.items.size(); i++) {
            if (steamer.cookingTime[i] >= 0 && steamer.cookingProgress[i] > 0) {
                hasCooking = true;
                steamer.cookingProgress[i] = Mth.clamp(steamer.cookingProgress[i] - 2, 0, steamer.cookingTime[i]);
            }
        }

        if (hasCooking) {
            steamer.setChanged();
        }
    }

    public void makeCookingParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && level.getRandom().nextFloat() < 0.1F) {
            RandomSource random = serverLevel.getRandom();
            boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
            double yOffset = half ? 0.5 : 1;
            serverLevel.sendParticles(ModParticles.COOKING,
                    pos.getX() + 0.5 + random.nextDouble() / 2 * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + yOffset + random.nextDouble() / 2,
                    pos.getZ() + 0.5 + random.nextDouble() / 2 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }
    }

    public void makeRipeParticles(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel && level.getRandom().nextFloat() < 0.5F) {
            RandomSource random = serverLevel.getRandom();
            boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
            double yOffset = half ? 0.25 : 0.75;
            serverLevel.sendParticles(ModParticles.COOKING,
                    pos.getX() + 0.5 + random.nextDouble() / 1.25 * (random.nextBoolean() ? 1 : -1),
                    pos.getY() + yOffset + random.nextDouble() / 2,
                    pos.getZ() + 0.5 + random.nextDouble() / 1.25 * (random.nextBoolean() ? 1 : -1),
                    1, 0, 0, 0, 0.05);
        }
    }

    public Optional<RecipeHolder<SteamerRecipe>> getSteamerRecipe(Level level, ItemStack stack) {
        if (this.items.stream().noneMatch(ItemStack::isEmpty)) {
            return Optional.empty();
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return Optional.empty();
        }
        return this.quickCheck.getRecipeFor(new SingleRecipeInput(stack), serverLevel);
    }

    public boolean canPlaceFood(Level level, ItemStack food) {
        if (food.isEmpty() || !canInteractWithOpenLayer(level) || !hasOpenSlotInCurrentLayer()) {
            return false;
        }
        if (level.recipeAccess() instanceof RecipeManager recipeManager) {
            return recipeManager.getRecipeFor(ModRecipes.STEAMER_RECIPE, new SingleRecipeInput(food), level)
                    .map(holder -> holder.value().getCookTick() > 0)
                    .orElse(false);
        }
        return true;
    }

    private boolean canInteractWithOpenLayer(Level level) {
        BlockPos above = this.getBlockPos().above();
        return !level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)
                && !this.getBlockState().getValue(SteamerBlock.HAS_LID);
    }

    private boolean hasOpenSlotInCurrentLayer() {
        boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
        int startIndex = half ? 0 : 4;
        for (int i = startIndex; i < startIndex + 4; i++) {
            if (this.items.get(i).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean placeFood(Level level, LivingEntity user, ItemStack food) {
        if (level.isClientSide()) {
            return false;
        }
        // 先检查这层是否是能交互的
        // 上层不能阻拦交互
        BlockPos above = this.getBlockPos().above();
        if (level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) {
            return false;
        }
        // 且自己必须开着盖子
        if (this.getBlockState().getValue(SteamerBlock.HAS_LID)) {
            return false;
        }
        // 然后检查配方
        Optional<RecipeHolder<SteamerRecipe>> steamerRecipe = getSteamerRecipe(level, food);
        if (steamerRecipe.isEmpty()) {
            return false;
        }
        int cookTime = steamerRecipe.get().value().getCookTick();
        if (cookTime <= 0) {
            return false;
        }
        boolean half = this.getBlockState().getValue(SteamerBlock.HALF);
        int startIndex = half ? 0 : 4;
        boolean added = false;
        // 一次性放入一层的
        for (int i = startIndex; i < startIndex + 4; i++) {
            ItemStack itemstack = this.items.get(i);
            if (itemstack.isEmpty() && !food.isEmpty()) {
                this.cookingTime[i] = cookTime;
                this.cookingProgress[i] = 0;
                this.items.set(i, user.hasInfiniteMaterials() ? food.copyWithCount(1) : food.split(1));
                added = true;
            }
        }
        if (!added) {
            return false;
        }
        this.setChangedAndSync();
        return true;
    }

    @Override
    public boolean takeFood(Level level, LivingEntity user, InteractionHand hand) {
        if (level.isClientSide()) {
            return false;
        }
        // 先检查这层是否是能交互的
        // 上层不能阻拦交互
        BlockPos above = this.getBlockPos().above();
        if (level.getBlockState(above).isFaceSturdy(level, above, Direction.DOWN)) {
            return false;
        }
        BlockState blockState = this.getBlockState();
        // 且自己必须开着盖子
        if (blockState.getValue(SteamerBlock.HAS_LID)) {
            return false;
        }
        boolean isAllEmpty = true;
        boolean half = blockState.getValue(SteamerBlock.HALF);
        int preferredSlot = user instanceof Player player ? player.getInventory().getSelectedSlot() : -1;
        int startIndex = half ? 4 : 8;
        // 一次性取出一层的
        for (int i = startIndex - 1; i >= (startIndex - 4); i--) {
            ItemStack stack = this.items.get(i);
            if (stack.isEmpty()) {
                continue;
            }
            isAllEmpty = false;
            ItemUtils.getItemToLivingEntity(user, stack, preferredSlot);
            this.items.set(i, ItemStack.EMPTY);
            this.cookingTime[i] = 0;
            this.cookingProgress[i] = 0;
        }
        boolean isAboveSteamer = level.getBlockState(this.getBlockPos().above()).is(blockState.getBlock());
        // 全为空，且上面没有蒸笼阻挡时，拆掉当前可交互的一层
        if (isAllEmpty && !isAboveSteamer) {
            ItemUtils.getItemToLivingEntity(user, ModItems.STEAMER.getDefaultInstance(), preferredSlot);
            // 把 4-8 全部清空
            for (int i = 4; i < 8; i++) {
                this.items.set(i, ItemStack.EMPTY);
                this.cookingTime[i] = 0;
                this.cookingProgress[i] = 0;
            }
            // 释放粒子效果
            level.playSound(null, this.getBlockPos(), blockState.getSoundType().getBreakSound(), SoundSource.BLOCKS);
            if (half) {
                level.setBlockAndUpdate(this.getBlockPos(), Blocks.AIR.defaultBlockState());
            } else {
                setChanged();
                level.setBlockAndUpdate(this.getBlockPos(), blockState.setValue(SteamerBlock.HALF, true));
            }
            return true;
        } else {
            this.setChangedAndSync();
        }
        return !isAllEmpty;
    }

    public NonNullList<ItemStack> getItems() {
        return items;
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        for (int i = 0; i < this.items.size(); i++) {
            this.items.set(i, ItemStack.EMPTY);
        }
        Arrays.fill(this.cookingProgress, 0);
        Arrays.fill(this.cookingTime, 0);
        ContainerHelper.loadAllItems(input, this.items);
        input.getIntArray(COOKING_PROGRESS_TAG).ifPresent(times -> {
            int length = Math.min(this.cookingTime.length, times.length);
            System.arraycopy(times, 0, this.cookingProgress, 0, length);
        });
        input.getIntArray(COOKING_TIME_TAG).ifPresent(times -> {
            int length = Math.min(this.cookingTime.length, times.length);
            System.arraycopy(times, 0, this.cookingTime, 0, length);
        });
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output, this.items, true);
        output.putIntArray(COOKING_PROGRESS_TAG, this.cookingProgress);
        output.putIntArray(COOKING_TIME_TAG, this.cookingTime);
    }

    // 将蒸笼数据一分为二，分别保存到两个 tag 里
    public static void saveSplit(TagValueOutput tag1, TagValueOutput tag2,
                                 Level level,
                                 NonNullList<ItemStack> items,
                                 int[] cookingProgress,
                                 int[] cookingTime) {
        // 保存两部分
        NonNullList<ItemStack> first = NonNullList.withSize(4, ItemStack.EMPTY);
        NonNullList<ItemStack> second = NonNullList.withSize(4, ItemStack.EMPTY);
        for (int i = 0; i < 4; i++) {
            first.set(i, items.get(i));
            second.set(i, items.get(i + 4));
        }

        int[] firstCookingProgress = new int[4];
        int[] secondCookingProgress = new int[4];

        int[] firstCookingTime = new int[4];
        int[] secondCookingTime = new int[4];

        System.arraycopy(cookingProgress, 0, firstCookingProgress, 0, 4);
        System.arraycopy(cookingProgress, 4, secondCookingProgress, 0, 4);

        System.arraycopy(cookingTime, 0, firstCookingTime, 0, 4);
        System.arraycopy(cookingTime, 4, secondCookingTime, 0, 4);

        ContainerHelper.saveAllItems(tag1, first, false);
        if (!tag1.isEmpty()) {
            tag1.putIntArray(COOKING_PROGRESS_TAG, firstCookingProgress);
            tag1.putIntArray(COOKING_TIME_TAG, firstCookingTime);
        }

        ContainerHelper.saveAllItems(tag2, second, false);
        if (!tag2.isEmpty()) {
            tag2.putIntArray(COOKING_PROGRESS_TAG, secondCookingProgress);
            tag2.putIntArray(COOKING_TIME_TAG, secondCookingTime);
        }
    }

    public int[] getCookingProgress() {
        return cookingProgress;
    }

    public int[] getCookingTime() {
        return cookingTime;
    }
}
