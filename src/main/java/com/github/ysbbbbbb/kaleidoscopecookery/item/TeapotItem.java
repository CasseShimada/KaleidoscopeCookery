package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.ITeapot;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.TeapotBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.TeapotRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.serializer.TeapotRecipeSerializer;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;

import java.util.function.Consumer;

public class TeapotItem extends CookeryTooltipBlockItem {
    public TeapotItem(Properties properties) {
        super(ModBlocks.TEAPOT, properties);
    }

    public static ItemStack getPourOut(ItemStack stack, Level level) {
        ValueInput input = readData(stack, level);
        if (input == null || input.getIntOr(TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT) != ITeapot.FINISHED) {
            return ItemStack.EMPTY;
        }
        return input.read(TeapotBlockEntity.RESULT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    public static void pourOut(ItemStack stack, Level level) {
        pourOut(stack, level, true);
    }

    public static void pourOut(ItemStack stack, Level level, LivingEntity user) {
        pourOut(stack, level, user == null || !user.hasInfiniteMaterials());
    }

    private static void pourOut(ItemStack stack, Level level, boolean consumeResult) {
        if (level.isClientSide()) {
            return;
        }
        ValueInput input = readData(stack, level);
        if (input == null || input.getIntOr(TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT) != ITeapot.FINISHED) {
            return;
        }
        ItemStack result = input.read(TeapotBlockEntity.RESULT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        if (result.isEmpty()) {
            return;
        }
        if (!consumeResult) {
            return;
        }
        result.shrink(1);
        if (result.isEmpty()) {
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            stack.remove(DataComponents.MAX_STACK_SIZE);
            return;
        }
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, level.registryAccess());
        output.store(TeapotBlockEntity.RESULT, ItemStack.CODEC, result);
        output.putInt(TeapotBlockEntity.STATUS, ITeapot.FINISHED);
        BlockItem.setBlockEntityData(stack, ModBlocks.TEAPOT_BE, output);
        stack.set(DataComponents.MAX_STACK_SIZE, 1);
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Player player = context.getPlayer();
        if (player == null || player.isSecondaryUseActive()) {
            return super.useOn(context);
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity target, InteractionHand hand) {
        ValueInput input = readData(stack, player.level());
        if (input == null) {
            return InteractionResult.PASS;
        }
        int status = input.getIntOr(TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);
        Identifier fluidId = input.getString(TeapotBlockEntity.TEA_FLUID_ID)
                .map(Identifier::tryParse)
                .orElse(TeapotRecipeSerializer.EMPTY_TEA_FLUID);
        if (status != ITeapot.PUT_INGREDIENT || !Identifier.withDefaultNamespace("lava").equals(fluidId)) {
            return InteractionResult.PASS;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.SUCCESS;
        }
        if (player.getRandom().nextFloat() < 0.3F) {
            stack.remove(DataComponents.BLOCK_ENTITY_DATA);
            stack.remove(DataComponents.MAX_STACK_SIZE);
            player.playSound(SoundEvents.BUCKET_EMPTY, 1.0F, 1.0F);
        }
        if (player.level() instanceof ServerLevel serverLevel) {
            target.hurtServer(serverLevel, player.level().damageSources().inFire(), 3.0F);
        }
        player.playSound(SoundEvents.FIRE_EXTINGUISH, 1.0F, 1.0F);
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean isBarVisible(ItemStack stack) {
        TypedEntityData<?> typedData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return typedData != null;
    }

    @Override
    public int getBarColor(ItemStack stack) {
        CompoundTag tag = getBlockEntityTag(stack);
        String fluidId = tag.getString(TeapotBlockEntity.TEA_FLUID_ID).orElse("");
        return Identifier.withDefaultNamespace("lava").toString().equals(fluidId) ? 0xFBA800 : 0x9DF7FF;
    }

    @Override
    public int getBarWidth(ItemStack stack) {
        CompoundTag tag = getBlockEntityTag(stack);
        if (tag.isEmpty()) {
            return 0;
        }
        int status = tag.getInt(TeapotBlockEntity.STATUS).orElse(ITeapot.PUT_INGREDIENT);
        if (status == ITeapot.PUT_INGREDIENT) {
            String fluidId = tag.getString(TeapotBlockEntity.TEA_FLUID_ID)
                    .orElse(TeapotRecipeSerializer.EMPTY_TEA_FLUID.toString());
            return fluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID.toString()) ? 0 : 13;
        }
        if (status == ITeapot.FINISHED) {
            int count = tag.getCompound(TeapotBlockEntity.RESULT)
                    .flatMap(resultTag -> resultTag.getInt("count"))
                    .orElse(0);
            return count <= 0 ? 0 : Math.round(13.0F * count / TeapotRecipe.OUTPUT_COUNT);
        }
        return 0;
    }

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display,
                                Consumer<Component> tooltip, TooltipFlag flag) {
        ValueInput input = readData(stack, context.registries());
        if (input == null) {
            return;
        }
        int status = input.getIntOr(TeapotBlockEntity.STATUS, ITeapot.PUT_INGREDIENT);
        if (status == ITeapot.PUT_INGREDIENT) {
            Identifier fluidId = input.getString(TeapotBlockEntity.TEA_FLUID_ID)
                    .map(Identifier::tryParse)
                    .orElse(TeapotRecipeSerializer.EMPTY_TEA_FLUID);
            if (!fluidId.equals(TeapotRecipeSerializer.EMPTY_TEA_FLUID)) {
                tooltip.accept(Component.translatable("block.%s.%s".formatted(fluidId.getNamespace(), fluidId.getPath()))
                        .withStyle(ChatFormatting.GRAY));
            }
        } else if (status == ITeapot.FINISHED) {
            ItemStack result = input.read(TeapotBlockEntity.RESULT, ItemStack.CODEC).orElse(ItemStack.EMPTY);
            if (!result.isEmpty()) {
                tooltip.accept(result.getHoverName().copy()
                        .append(CommonComponents.SPACE)
                        .append(Component.literal("x%d".formatted(result.getCount())))
                        .withStyle(ChatFormatting.GRAY));
            }
        }
    }

    private static ValueInput readData(ItemStack stack, Level level) {
        return level == null ? null : readData(stack, level.registryAccess());
    }

    private static ValueInput readData(ItemStack stack, net.minecraft.core.HolderLookup.Provider provider) {
        TypedEntityData<?> typedData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (typedData == null) {
            return null;
        }
        CompoundTag tag = typedData.copyTagWithoutId();
        return TagValueInput.create(ProblemReporter.DISCARDING, provider, tag);
    }

    private static CompoundTag getBlockEntityTag(ItemStack stack) {
        TypedEntityData<?> typedData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        return typedData == null ? new CompoundTag() : typedData.copyTagWithoutId();
    }
}
