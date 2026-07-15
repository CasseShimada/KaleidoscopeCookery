package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IShawarmaSpit;
import com.github.ysbbbbbb.kaleidoscopecookery.block.kitchen.ShawarmaSpitBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

public class ShawarmaSpitBlockEntity extends BaseBlockEntity implements IShawarmaSpit {
    private static final int MAX_ITEMS = 8;

    public static final String COOKING_ITEM = "CookingItem";
    public static final String COOKED_ITEM = "CookedItem";
    public static final String COOK_TIME = "CookTime";

    private final RecipeManager.CachedCheck<SingleRecipeInput, CampfireCookingRecipe> quickCheck = RecipeManager.createCheck(RecipeType.CAMPFIRE_COOKING);
    private ItemStack cookingItem = ItemStack.EMPTY;
    private ItemStack cookedItem = ItemStack.EMPTY;
    private int cookTime;

    public ShawarmaSpitBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.SHAWARMA_SPIT_BE, pPos, pBlockState);
    }

    @Override
    public boolean onPutCookingItem(Level level, ItemStack itemStack) {
        // 先判断能否放入物品
        if (!(level instanceof ServerLevel serverLevel)
                || !this.cookingItem.isEmpty() || !this.cookedItem.isEmpty()) {
            return false;
        }
        // 尝试通过输入的物品寻找营火配方
        SingleRecipeInput singleRecipeInput = new SingleRecipeInput(itemStack);
        return this.quickCheck.getRecipeFor(singleRecipeInput, serverLevel).map(recipe -> {
            // 如果找到了配方，则设置正在烹饪的物品和烹饪时间
            this.cookingItem = itemStack.split(MAX_ITEMS);
            this.cookedItem = recipe.value().assemble(singleRecipeInput);
            this.cookedItem.setCount(this.cookingItem.getCount());
            this.cookTime = recipe.value().cookingTime();
            this.setChangedAndSync();
            level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition, GameEvent.Context.of(this.getBlockState()));
            level.playSound(null,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    SoundEvents.ITEM_FRAME_ADD_ITEM,
                    SoundSource.BLOCKS,
                    0.5F + level.getRandom().nextFloat(),
                    level.getRandom().nextFloat() * 0.7F + 0.6F);
            return true;
        }).orElse(false);
    }

    public boolean canPutCookingItem(Level level, ItemStack itemStack) {
        if (itemStack.isEmpty() || !this.cookingItem.isEmpty() || !this.cookedItem.isEmpty()) {
            return false;
        }
        if (level.recipeAccess() instanceof RecipeManager recipeManager) {
            return recipeManager.getRecipeFor(
                    RecipeType.CAMPFIRE_COOKING, new SingleRecipeInput(itemStack), level).isPresent();
        }
        return false;
    }

    public boolean canTakeCookedItem() {
        return this.cookTime <= 0 && !this.cookedItem.isEmpty()
                || this.cookTime > 0 && !this.cookingItem.isEmpty();
    }

    @Override
    public boolean onTakeCookedItem(Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ItemStack mainHandItem = entity.getMainHandItem();

        // 如果有烹饪完成的物品，则将其取出
        if (this.cookTime <= 0 && !this.cookedItem.isEmpty()) {
            giveItem(level, entity, mainHandItem, this.cookedItem.copy());
            return true;
        }

        // 如果没有烹饪完成，返回原材料并重置
        if (this.cookTime > 0 && !this.cookingItem.isEmpty()) {
            giveItem(level, entity, mainHandItem, this.cookingItem.copy());
            return true;
        }

        return false;
    }

    private void giveItem(Level level, LivingEntity entity, ItemStack mainHandItem, ItemStack copy) {
        this.cookingItem = ItemStack.EMPTY;
        this.cookedItem = ItemStack.EMPTY;
        this.cookTime = 0;
        this.setChangedAndSync();
        level.gameEvent(GameEvent.BLOCK_CHANGE, worldPosition, GameEvent.Context.of(entity, this.getBlockState()));

        if (!mainHandItem.is(TagMod.KITCHEN_KNIFE)
            && this.getBlockState().getValue(ShawarmaSpitBlock.POWERED)
            && level instanceof ServerLevel serverLevel) {
            entity.hurtServer(serverLevel, level.damageSources().inFire(), 1.0F);
        }
        ItemUtils.getItemToLivingEntity(entity, copy);
        if (level instanceof ServerLevel) {
            level.playSound(null,
                    worldPosition.getX() + 0.5,
                    worldPosition.getY() + 0.5,
                    worldPosition.getZ() + 0.5,
                    SoundEvents.ITEM_FRAME_REMOVE_ITEM,
                    SoundSource.BLOCKS,
                    0.5F + level.getRandom().nextFloat(),
                    level.getRandom().nextFloat() * 0.7F + 0.6F);
        }
    }

    public void tick() {
        if (cookingItem.isEmpty()) {
            if (!cookedItem.isEmpty()) {
                this.spawnParticles();
            }
            return;
        }
        this.spawnParticles();
        if (cookTime > 0) {
            cookTime--;
            this.setChanged();
        } else {
            if (level instanceof ServerLevel) {
                level.playSound(null,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5,
                        SoundEvents.FIRE_EXTINGUISH,
                        SoundSource.BLOCKS,
                        0.5F + level.getRandom().nextFloat(),
                        level.getRandom().nextFloat() * 0.7F + 0.6F);
            }
            this.cookingItem = ItemStack.EMPTY;
            this.setChangedAndSync();
        }
    }

    private void spawnParticles() {
        if (level instanceof ServerLevel serverLevel) {
            if (level.getRandom().nextFloat() < 0.25f) {
                serverLevel.sendParticles(ModParticles.COOKING,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5,
                        1,
                        0.25, 0.2, 0.25,
                        0.1f);
            }
            if (level.getRandom().nextInt(20) == 0) {
                serverLevel.playSound(null,
                        worldPosition.getX() + 0.5,
                        worldPosition.getY() + 0.5,
                        worldPosition.getZ() + 0.5,
                        SoundEvents.CAMPFIRE_CRACKLE,
                        SoundSource.BLOCKS,
                        0.5F + level.getRandom().nextFloat(),
                        level.getRandom().nextFloat() * 0.7F + 0.6F);
            }
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!this.cookingItem.isEmpty()) {
            output.store(COOKING_ITEM, ItemStack.CODEC, this.cookingItem);
        }
        if (!this.cookedItem.isEmpty()) {
            output.store(COOKED_ITEM, ItemStack.CODEC, this.cookedItem);
        }
        output.putInt(COOK_TIME, this.cookTime);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.cookingItem = input.read(COOKING_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.cookedItem = input.read(COOKED_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.cookTime = input.getIntOr(COOK_TIME, 0);
    }

    public ItemStack getStoredItem() {
        return (this.cookingItem.isEmpty() ? this.cookedItem : this.cookingItem).copy();
    }

    public ItemStack removeStoredItem() {
        ItemStack storedItem = this.getStoredItem();
        if (!storedItem.isEmpty()) {
            this.cookingItem = ItemStack.EMPTY;
            this.cookedItem = ItemStack.EMPTY;
            this.cookTime = 0;
            this.setChangedAndSync();
        }
        return storedItem;
    }

    public int getCookTime() {
        return this.cookTime;
    }
}
