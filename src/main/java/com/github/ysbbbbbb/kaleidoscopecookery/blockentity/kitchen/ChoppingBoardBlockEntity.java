package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IChoppingBoard;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.ChoppingBoardRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class ChoppingBoardBlockEntity extends BaseBlockEntity implements IChoppingBoard {
    private static final String MODEL_ID = "ModelId";
    private static final String CURRENT_CUT_STACK = "CurrentCutStack";
    private static final String RESULT_ITEM = "ResultItem";
    private static final String MAX_CUT_COUNT = "MaxCutCount";
    private static final String CURRENT_CUT_COUNT = "CurrentCutCount";
    /**
     * 仅用于客户端渲染
     */
    public @Nullable Identifier[] cacheModels = null;
    public @Nullable Identifier previousModel = null;
    /**
     * 服务端客户端共通数据
     */
    private @Nullable Identifier modelId = null;
    private int maxCutCount = 0;
    private int currentCutCount = 0;
    private ItemStack currentCutStack = ItemStack.EMPTY;
    private ItemStack result = ItemStack.EMPTY;

    public ChoppingBoardBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.CHOPPING_BOARD_BE, pos, blockState);
    }

    @Override
    public boolean onPutItem(Level level, LivingEntity user, ItemStack putOnItem) {
        if (!this.result.isEmpty()) {
            return false;
        }
        SingleRecipeInput container = new SingleRecipeInput(putOnItem);
        if (!(level.recipeAccess() instanceof RecipeManager recipeManager)) {
            return false;
        }
        Optional<RecipeHolder<ChoppingBoardRecipe>> recipeOptional = recipeManager
                .getRecipeFor(ModRecipes.CHOPPING_BOARD_RECIPE, container, level);
        if (recipeOptional.isPresent()) {
            ChoppingBoardRecipe recipe = recipeOptional.get().value();
            this.modelId = recipe.getModelId();
            this.maxCutCount = recipe.getCutCount();
            this.currentCutCount = 0;
            this.currentCutStack = putOnItem.split(1);
            this.result = recipe.assemble(container, level.registryAccess());
            this.refresh();
            level.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 1.2F);
            return true;
        }
        return false;
    }

    @Override
    public boolean onCutItem(Level level, LivingEntity user, ItemStack cutterItem) {
        if (this.result.isEmpty()) {
            return false;
        }
        // 如果已经切完，执行取出逻辑
        if (this.currentCutCount >= this.maxCutCount) {
            Block.popResource(level, worldPosition, this.result.copy());
            this.resetBoardData();
            level.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 2 + level.random.nextFloat() * 0.2f);
            return true;
        } else if (cutterItem.is(TagMod.KITCHEN_KNIFE)) {
            // 否则，检测是否是刀具，进行切菜逻辑
            this.currentCutCount++;
            this.playParticlesSound();
            this.refresh();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean onTakeOut(Level level, LivingEntity user) {
        if (this.currentCutCount == 0 && !this.currentCutStack.isEmpty()) {
            if (user instanceof Player player) {
                player.getInventory().placeItemBackInInventory(this.currentCutStack);
            } else {
                Block.popResource(level, this.worldPosition, this.currentCutStack);
            }
            this.resetBoardData();
            level.playSound(null, this.worldPosition,
                    SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                    SoundSource.BLOCKS,
                    1, 1.2f + level.random.nextFloat() * 0.2f);
            return true;
        }
        return false;
    }

    @Override
    public void playParticlesSound() {
        if (this.level instanceof ServerLevel serverLevel) {
            RandomSource random = serverLevel.getRandom();
            serverLevel.sendParticles(ParticleTypes.CRIT,
                    this.worldPosition.getX() + 0.25 + random.nextDouble() / 2,
                    this.worldPosition.getY() + 0.25,
                    this.worldPosition.getZ() + 0.25 + random.nextDouble() / 2,
                    2, 0, 0, 0, 0.1);
            serverLevel.playSound(null, this.worldPosition,
                    SoundEvents.WOOD_PLACE,
                    SoundSource.BLOCKS,
                    1, 1.5f + level.random.nextFloat() * 0.4f);
        }
    }

    private void resetBoardData() {
        this.modelId = null;
        this.result = ItemStack.EMPTY;
        this.currentCutStack = ItemStack.EMPTY;
        this.currentCutCount = 0;
        this.maxCutCount = 0;
        this.refresh();
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (this.modelId != null) {
            output.putString(MODEL_ID, this.modelId.toString());
        }
        output.putInt(MAX_CUT_COUNT, this.maxCutCount);
        output.putInt(CURRENT_CUT_COUNT, this.currentCutCount);
        if (!this.currentCutStack.isEmpty()) {
            output.store(CURRENT_CUT_STACK, ItemStack.CODEC, this.currentCutStack);
        }
        if (!this.result.isEmpty()) {
            output.store(RESULT_ITEM, ItemStack.CODEC, this.result);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.modelId = input.getString(MODEL_ID).map(Identifier::parse).orElse(null);
        this.maxCutCount = input.getIntOr(MAX_CUT_COUNT, 0);
        this.currentCutCount = input.getIntOr(CURRENT_CUT_COUNT, 0);
        this.currentCutStack = input.read(CURRENT_CUT_STACK, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.result = input.read(RESULT_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Nullable
    public Identifier getModelId() {
        return modelId;
    }

    public int getMaxCutCount() {
        return maxCutCount;
    }

    public int getCurrentCutCount() {
        return currentCutCount;
    }

    public ItemStack getCurrentCutStack() {
        return currentCutStack;
    }
}
