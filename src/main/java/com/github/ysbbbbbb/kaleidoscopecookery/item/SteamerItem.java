package com.github.ysbbbbbb.kaleidoscopecookery.item;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen.SteamerBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

public class SteamerItem extends CookeryTooltipBlockItem {
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
        if (blockEntity instanceof SteamerBlockEntity steamer && stack.is(this)) {
            mergedStack = stack.copyWithCount(1);
            steamer.mergeItem(mergedStack, context.getLevel());
        }
        boolean placed = super.placeBlock(context, state);
        if (placed && mergedStack != null) {
            if (stack.getCount() == 1) {
                stack.applyComponents(mergedStack.getComponents());
            } else {
                BlockItem.updateCustomBlockEntityTag(level, context.getPlayer(), clickedPos, mergedStack);
            }
        }
        return placed;
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

    @Override
    protected void appendCookeryTooltip(ItemStack stack, TooltipContext context, TooltipDisplay display, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable("tooltip.kaleidoscope_cookery.steamer").withStyle(ChatFormatting.GRAY));
    }
}
