package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.decoration;

import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.state.BlockState;

public class FruitBasketBlockEntity extends BaseBlockEntity {
    public static final String ITEMS = "BasketItems";
    private final SimpleContainer items = new SimpleContainer(8);

    public FruitBasketBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlocks.FRUIT_BASKET_BE, pPos, pBlockState);
    }

    public void putOn(ItemStack stack, boolean consumeSourceStack) {
        if (!stack.getItem().canFitInsideContainerItems()) {
            return;
        }
        ItemStack remainder = this.items.addItem(stack);
        int inserted = stack.getCount() - remainder.getCount();
        if (inserted > 0) {
            if (consumeSourceStack) {
                stack.shrink(inserted);
            }
            if (this.level != null) {
                this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_ADD_ITEM, SoundSource.BLOCKS);
            }
            this.setChangedAndSync();
        }
    }

    public void takeOut(Player player) {
        for (int i = 0; i < items.getContainerSize(); i++) {
            ItemStack stack = items.getItem(i);
            if (stack.isEmpty()) {
                continue;
            }
            ItemStack extracted = this.items.removeItem(i, stack.getCount());
            if (!extracted.isEmpty()) {
                ItemUtils.giveItemToPlayer(player, extracted);
                if (this.level != null) {
                    this.level.playSound(null, this.worldPosition, SoundEvents.ITEM_FRAME_REMOVE_ITEM, SoundSource.BLOCKS);
                }
                this.setChangedAndSync();
            }
            return;
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child(ITEMS), this.items.items);
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input.childOrEmpty(ITEMS), this.items.items);
    }

    public NonNullList<ItemStack> getItems() {
        return items.items;
    }

    public void setItems(NonNullList<ItemStack> items) {
        this.items.clearContent();
        int maxSize = Math.min(items.size(), this.items.getContainerSize());
        for (int i = 0; i < maxSize; i++) {
            this.items.setItem(i, items.get(i).copy());
        }
        this.setChangedAndSync();
    }
}
