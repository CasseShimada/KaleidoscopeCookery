package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.container.TeapotInput;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModParticles;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.ProblemReporter;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeapotBlockEntity extends BaseBlockEntity implements ITeapot {
    public static final int INGREDIENT_TIME = 200;
    public static final String TEA_FLUID_ID = "TeaFluidId";
    public static final String RESULT = "Result";
    public static final String STATUS = "Status";

    private static final String INPUT = "Input";
    private static final String CURRENT_TICK = "CurrentTick";

    private final RecipeManager.CachedCheck<TeapotInput, TeapotRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.TEAPOT_RECIPE);

    private ItemStack input = ItemStack.EMPTY;
    private Identifier teaFluidId = TeapotRecipeSerializer.EMPTY_TEA_FLUID;
    private ItemStack result = ItemStack.EMPTY;
    private int status = PUT_INGREDIENT;
    private int currentTick = -1;

    public TeapotBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TEAPOT_BE, pos, state);
    }

    public void tick(Level level) {
        long offset = level.getGameTime() + worldPosition.hashCode();
        if (this.status == PUT_INGREDIENT && Math.floorMod(offset, 23) == 0) {
            if (this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID) || !hasHeatSource(level)) {
                return;
            }
            this.onProcessingEffects(level);
            if (this.input.isEmpty()) {
                return;
            }
            if (this.currentTick > 0) {
                this.currentTick = Math.max(-1, this.currentTick - 23);
                this.setChanged();
                return;
            }
            if (!(level instanceof ServerLevel serverLevel)) {
                return;
            }
            TeapotInput container = new TeapotInput(this.input, this.teaFluidId);
            Optional<RecipeHolder<TeapotRecipe>> recipeOpt = this.quickCheck.getRecipeFor(container, serverLevel);
            if (recipeOpt.isPresent()) {
                TeapotRecipe recipe = recipeOpt.get().value();
                this.result = recipe.assemble(container);
                this.currentTick = recipe.time();
                this.status = PROCESSING;
                this.setChangedAndSync();
                return;
            }
            Block.popResource(level, worldPosition, this.input);
            this.input = ItemStack.EMPTY;
            this.result = ItemStack.EMPTY;
            this.currentTick = -1;
            this.setChangedAndSync();
            return;
        }
        if (this.status == PROCESSING && Math.floorMod(offset, 23) == 0) {
            if (!hasHeatSource(level)) {
                return;
            }
            this.onProcessingEffects(level);
            if (this.currentTick > 0) {
                this.currentTick = Math.max(-1, this.currentTick - 23);
                this.setChanged();
                return;
            }
            this.status = FINISHED;
            this.currentTick = -1;
            this.setChangedAndSync();
            return;
        }
        if (this.status == FINISHED && Math.floorMod(offset, 11) == 0) {
            if (!hasHeatSource(level)) {
                this.onFinishEffects(level);
            } else {
                this.onBoilingEffects(level);
            }
        }
    }

    @Override
    public boolean hasHeatSource(Level level) {
        BlockState belowState = level.getBlockState(worldPosition.below());
        if (belowState.hasProperty(BlockStateProperties.LIT)) {
            return belowState.getValue(BlockStateProperties.LIT);
        }
        return belowState.is(TagMod.HEAT_SOURCE_BLOCKS_WITHOUT_LIT);
    }

    @Override
    public boolean addTeaFluid(Level level, LivingEntity user, ItemStack stack) {
        if (this.status != PUT_INGREDIENT) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_tea_fluid.state_incorrect", this.getStatusText());
            return false;
        }
        if (!this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_tea_fluid.has_fluid");
            return false;
        }
        Fluid fluid = null;
        ItemStack remainder = ItemStack.EMPTY;
        if (stack.is(Items.WATER_BUCKET)) {
            fluid = Fluids.WATER;
            remainder = new ItemStack(Items.BUCKET);
        } else if (stack.is(Items.LAVA_BUCKET)) {
            fluid = Fluids.LAVA;
            remainder = new ItemStack(Items.BUCKET);
        }
        if (fluid == null) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        this.teaFluidId = BuiltInRegistries.FLUID.getKey(fluid);
        if (!user.hasInfiniteMaterials()) {
            stack.shrink(1);
            ItemUtils.getItemToLivingEntity(user, remainder);
        }
        level.playSound(null, worldPosition, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
        this.setChangedAndSync();
        return true;
    }

    @Override
    public boolean removeTeaFluid(Level level, LivingEntity user, ItemStack stack) {
        if (this.status != PUT_INGREDIENT || this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID) || !this.input.isEmpty()) {
            return false;
        }
        if (!stack.is(Items.BUCKET)) {
            return false;
        }
        Fluid fluid = BuiltInRegistries.FLUID.getValue(this.teaFluidId);
        ItemStack filled = ItemStack.EMPTY;
        if (fluid == Fluids.WATER) {
            filled = new ItemStack(Items.WATER_BUCKET);
        } else if (fluid == Fluids.LAVA) {
            filled = new ItemStack(Items.LAVA_BUCKET);
        }
        if (filled.isEmpty()) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        if (!user.hasInfiniteMaterials()) {
            stack.shrink(1);
            ItemUtils.getItemToLivingEntity(user, filled);
        }
        level.playSound(null, worldPosition, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
        this.teaFluidId = TeapotRecipeSerializer.EMPTY_TEA_FLUID;
        this.currentTick = -1;
        this.setChangedAndSync();
        return true;
    }

    @Override
    public boolean addIngredient(Level level, LivingEntity user, ItemStack stack) {
        if (this.status != PUT_INGREDIENT) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_ingredient.state_incorrect", this.getStatusText());
            return false;
        }
        if (this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_ingredient.no_fluid");
            return false;
        }
        if (!this.input.isEmpty()) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_ingredient.has_ingredient");
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return true;
        }
        TeapotInput container = new TeapotInput(stack, this.teaFluidId);
        Optional<RecipeHolder<TeapotRecipe>> recipeOpt = this.quickCheck.getRecipeFor(container, serverLevel);
        if (recipeOpt.isPresent()) {
            int count = recipeOpt.get().value().ingredientCount();
            this.input = stack.copyWithCount(count);
            this.currentTick = INGREDIENT_TIME;
            if (!user.hasInfiniteMaterials()) {
                stack.shrink(count);
            }
            this.setChangedAndSync();
            return true;
        }
        this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.add_ingredient.recipe_incorrect");
        return false;
    }

    @Override
    public boolean removeIngredient(Level level, LivingEntity user) {
        if (this.status != PUT_INGREDIENT || this.input.isEmpty()) {
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        ItemUtils.getItemToLivingEntity(user, this.input.copyAndClear());
        this.setChangedAndSync();
        return true;
    }

    @Override
    public boolean takeTeapot(Level level, LivingEntity user) {
        if (this.status == PROCESSING) {
            this.sendActionBarMessage(user, "tooltip.kaleidoscope_cookery.teapot.take_teapot.state_incorrect");
            return false;
        }
        if (level.isClientSide()) {
            return true;
        }
        for (ItemStack drop : getDrops()) {
            ItemUtils.getItemToLivingEntity(user, drop);
        }
        level.playSound(null, worldPosition, SoundEvents.LANTERN_BREAK, SoundSource.BLOCKS, 0.6F,
                0.8F + level.getRandom().nextFloat() * 0.2F);
        level.setBlock(worldPosition, Blocks.AIR.defaultBlockState(), Block.UPDATE_ALL);
        return true;
    }

    public List<ItemStack> getDrops() {
        List<ItemStack> drops = new ArrayList<>();
        ItemStack teapot = ModItems.TEAPOT.getDefaultInstance();
        if (this.status != FINISHED) {
            if (!this.input.isEmpty()) {
                drops.add(this.input.copy());
            }
            if (this.level != null && !this.teaFluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
                TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.level.registryAccess());
                output.putString(TEA_FLUID_ID, this.teaFluidId.toString());
                BlockItem.setBlockEntityData(teapot, ModBlocks.TEAPOT_BE, output);
                teapot.set(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE, 1);
            }
            drops.add(teapot);
            return drops;
        }
        if (this.status == FINISHED && this.level != null) {
            TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.level.registryAccess());
            output.store(RESULT, ItemStack.CODEC, this.result);
            output.putInt(STATUS, this.status);
            BlockItem.setBlockEntityData(teapot, ModBlocks.TEAPOT_BE, output);
            teapot.set(net.minecraft.core.component.DataComponents.MAX_STACK_SIZE, 1);
        }
        drops.add(teapot);
        return drops;
    }

    private void onProcessingEffects(Level level) {
        RandomSource random = level.getRandom();
        level.playSound(null, worldPosition, ModSounds.BLOCK_TEAPOT_PROCESSING, SoundSource.BLOCKS, 0.6F,
                0.8F + random.nextFloat() * 0.2F);
        this.onFinishEffects(level);
    }

    private void onBoilingEffects(Level level) {
        RandomSource random = level.getRandom();
        level.playSound(null, worldPosition, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.4F,
                0.8F + random.nextFloat() * 0.2F);
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.COOKING,
                    worldPosition.getX() + 0.5 + (random.nextFloat() - 0.5F),
                    worldPosition.getY() + 0.6 + random.nextDouble() / 5,
                    worldPosition.getZ() + 0.5 + (random.nextFloat() - 0.5F),
                    3, (random.nextFloat() - 0.5) * 0.05F, 0.1, (random.nextFloat() - 0.5) * 0.05F, 0.02);
        }
    }

    private void onFinishEffects(Level level) {
        RandomSource random = level.getRandom();
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(ModParticles.COOKING,
                    worldPosition.getX() + 0.5 + random.nextDouble() / 4 * (random.nextBoolean() ? 1 : -1),
                    worldPosition.getY() + 0.8 + random.nextDouble() / 3,
                    worldPosition.getZ() + 0.5 + random.nextDouble() / 4 * (random.nextBoolean() ? 1 : -1),
                    1, (random.nextFloat() - 0.5) * 0.05F, 0.1, (random.nextFloat() - 0.5) * 0.05F, 0.02);
        }
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
        output.store(INPUT, ItemStack.CODEC, this.input);
        output.putString(TEA_FLUID_ID, this.teaFluidId.toString());
        output.store(RESULT, ItemStack.CODEC, this.result);
        output.putInt(STATUS, this.status);
        output.putInt(CURRENT_TICK, this.currentTick);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.input = input.read(INPUT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.teaFluidId = input.getString(TEA_FLUID_ID)
                .map(Identifier::tryParse)
                .orElse(TeapotRecipeSerializer.EMPTY_TEA_FLUID);
        this.result = input.read(RESULT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.status = input.getIntOr(STATUS, PUT_INGREDIENT);
        this.currentTick = input.getIntOr(CURRENT_TICK, -1);
    }

    public static ValueInput inputFromTag(Level level, CompoundTag tag) {
        return TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), tag);
    }

    @Override
    public int getStatus() {
        return this.status;
    }

    public Component getStatusText() {
        return switch (this.status) {
            case PUT_INGREDIENT -> Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.put_ingredient");
            case PROCESSING -> Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.processing");
            case FINISHED -> Component.translatable("tooltip.kaleidoscope_cookery.teapot.statue.finished");
            default -> Component.empty();
        };
    }

    public ItemStack getInput() {
        return this.input.copy();
    }

    public Identifier getTeaFluidId() {
        return teaFluidId;
    }

    public ItemStack getResult() {
        return this.result.copy();
    }

    public int getCurrentTick() {
        return currentTick;
    }
}
