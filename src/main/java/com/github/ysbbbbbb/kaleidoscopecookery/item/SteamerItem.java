package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TypedEntityData;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.ValueInput;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public class SteamerItem extends BlockItem {
    private static final int NONE = 0;
    private static final int HAS = 1;

    public SteamerItem(Properties properties) {
        super(ModBlocks.STEAMER, properties);
    }

    @Override
    protected boolean placeBlock(BlockPlaceContext context, BlockState state) {
        Level level = context.getLevel();
        Direction face = context.getClickedFace();
        BlockPos clickedPos = context.getClickedPos();
        // 点击顶部才能放置
        if (face != Direction.UP) {
            return false;
        }
        BlockEntity blockEntity = level.getBlockEntity(clickedPos);
        ItemStack stack = context.getItemInHand();
        ItemStack mergedStack = null;
        if (blockEntity instanceof SteamerBlockEntity steamer && stack.is(this) && stack.getCount() == 1) {
            mergedStack = stack.copy();
            steamer.mergeItem(mergedStack, context.getLevel());
        }
        boolean placed = super.placeBlock(context, state);
        if (placed && mergedStack != null && !level.isClientSide()) {
            BlockEntity placedEntity = level.getBlockEntity(clickedPos);
            if (placedEntity instanceof SteamerBlockEntity steamer) {
                applyMergedData(mergedStack, steamer, level);
            }
        }
        return placed;
    }

    private static void applyMergedData(ItemStack stack, SteamerBlockEntity steamer, Level level) {
        TypedEntityData<?> typedData = stack.get(DataComponents.BLOCK_ENTITY_DATA);
        if (typedData == null) {
            return;
        }
        CompoundTag data = typedData.copyTagWithoutId();
        ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, level.registryAccess(), data);
        steamer.loadAdditional(input);
        steamer.refresh();
    }

    @Override
    public int getDefaultMaxStackSize() {
        if (this.getDefaultInstance().has(DataComponents.BLOCK_ENTITY_DATA)) {
            return 1;
        }
        return super.getDefaultMaxStackSize();
    }

    @Override
    public void onCraftedBy(ItemStack stack, Player player) {
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            stack.set(DataComponents.MAX_STACK_SIZE, 1);
        }
        super.onCraftedBy(stack, player);
    }

    @Environment(EnvType.CLIENT)
    public static float getTexture(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
        if (stack.has(DataComponents.BLOCK_ENTITY_DATA)) {
            return HAS;
        }
        return NONE;
    }

    @Override
        public void appendHoverText(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.steamer").withStyle(ChatFormatting.GRAY));
    }
}
