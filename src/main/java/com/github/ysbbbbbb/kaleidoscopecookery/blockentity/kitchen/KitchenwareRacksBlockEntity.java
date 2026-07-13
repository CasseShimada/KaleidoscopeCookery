package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IKitchenwareRacks;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

public class KitchenwareRacksBlockEntity extends BaseBlockEntity implements IKitchenwareRacks {
    private static final String LEFT_ITEM = "LeftItem";
    private static final String RIGHT_ITEM = "RightItem";

    private ItemStack itemLeft = ItemStack.EMPTY;
    private ItemStack itemRight = ItemStack.EMPTY;

    public KitchenwareRacksBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.KITCHENWARE_RACKS_BE, pPos, pBlockState);
    }

    @Override
    public boolean onClick(LivingEntity user, ItemStack stack, boolean isLeft) {
        if (this.level != null && this.level.isClientSide()) {
            return false;
        }
        ItemStack stackInRacks = isLeft ? itemLeft : itemRight;
        // 取出物品
        if (stack.isEmpty() && !stackInRacks.isEmpty()) {
            if (user instanceof Player player) {
                ItemUtils.giveItemToPlayer(player, stackInRacks.copy(), player.getInventory().getSelectedSlot());
            } else {
                ItemUtils.getItemToLivingEntity(user, stackInRacks.copy());
            }
            if (isLeft) {
                itemLeft = ItemStack.EMPTY;
            } else {
                itemRight = ItemStack.EMPTY;
            }
            user.playSound(SoundEvents.ITEM_FRAME_REMOVE_ITEM, 1.0F, 1.0F);
            this.setChangedAndSync();
            return true;
        }
        // 放入物品
        if (stack.isDamageableItem() && stackInRacks.isEmpty()) {
            ItemStack stored = user.hasInfiniteMaterials()
                    ? stack.copyWithCount(1)
                    : stack.split(1);
            if (isLeft) {
                itemLeft = stored;
            } else {
                itemRight = stored;
            }
            user.playSound(SoundEvents.ITEM_FRAME_ADD_ITEM, 1.0F, 1.0F);
            this.setChangedAndSync();
            return true;
        }
        return false;
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        if (!itemLeft.isEmpty()) {
            output.store(LEFT_ITEM, ItemStack.CODEC, itemLeft);
        }
        if (!itemRight.isEmpty()) {
            output.store(RIGHT_ITEM, ItemStack.CODEC, itemRight);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        this.itemLeft = input.read(LEFT_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.itemRight = input.read(RIGHT_ITEM, ItemStack.CODEC).orElse(ItemStack.EMPTY);
    }

    @Override
    public ItemStack getItemLeft() {
        return this.itemLeft.copy();
    }

    @Override
    public ItemStack getItemRight() {
        return this.itemRight.copy();
    }
}
