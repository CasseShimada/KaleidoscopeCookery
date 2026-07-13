package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.misc;

import com.github.ysbbbbbb.kaleidoscopecookery.block.misc.TrashCanBlock;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.entity.SitEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TrashCanBlockEntity extends BaseBlockEntity {
    public static final int EVENT_PUT = 1;
    public static final int EVENT_WITHDRAW = 2;
    public static final int EVENT_ENTER = 3;
    private static final String STORAGE = "Storage";
    private final SimpleContainer storage = new SimpleContainer(3);

    public final AnimationState putState = new AnimationState();
    public final AnimationState withdrawState = new AnimationState();
    public final AnimationState player1State = new AnimationState();
    public final AnimationState player2State = new AnimationState();
    public final AnimationState enterState = new AnimationState();

    public TrashCanBlockEntity(BlockPos pos, BlockState blockState) {
        super(ModBlocks.TRASH_CAN_BE, pos, blockState);
    }

    public void clientTick(Level level) {
        long offset = level.getGameTime() + worldPosition.hashCode();
        if (Math.floorMod(offset, 61) == 0 && hasTrashCanSeat(level)) {
            startRandomPlayerAnimation(level);
        }
        if (Math.floorMod(offset, 5) == 0 && !hasTrashCanSeat(level)) {
            stopPlayerAnimations();
        }
    }

    private boolean hasTrashCanSeat(Level level) {
        return !level.getEntitiesOfClass(SitEntity.class, new AABB(this.worldPosition)).isEmpty();
    }

    private void startRandomPlayerAnimation(Level level) {
        if (level.getRandom().nextBoolean()) {
            this.player2State.stop();
            this.player1State.start((int) level.getGameTime());
        } else {
            this.player1State.stop();
            this.player2State.start((int) level.getGameTime());
        }
    }

    private void stopPlayerAnimations() {
        this.player1State.stop();
        this.player2State.stop();
    }

    public void entityInside(Level level, BlockPos pos, Entity entity) {
        if (!(level instanceof ServerLevel serverLevel) || !(entity instanceof ItemEntity itemEntity)) {
            return;
        }
        AABB entityBox = entity.getBoundingBox().move(-pos.getX(), -pos.getY(), -pos.getZ());
        VoxelShape shape = Shapes.create(entityBox);
        if (!Shapes.joinIsNotEmpty(shape, TrashCanBlock.SUCK_ZONE, BooleanOp.AND)) {
            return;
        }
        if (!absorbMatchingItem(itemEntity.getItem())) {
            return;
        }
        serverLevel.sendParticles(ParticleTypes.CLOUD, entity.getX(), entity.getY() + entity.getEyeHeight(), entity.getZ(),
                1, 0.1, 0.1, 0.1, 0.01);
        if (itemEntity.getItem().isEmpty()) {
            entity.discard();
        }
    }

    public void putItem(ItemStack stack, boolean consumeSourceStack) {
        if (stack.isEmpty() || !stack.getItem().canFitInsideContainerItems()) {
            return;
        }
        int storedCount = storeItem(stack);
        if (consumeSourceStack) {
            stack.shrink(storedCount);
        }
        playActionEffects(SoundEvents.BARREL_OPEN, 0.5F);
        if (level != null) {
            level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), EVENT_PUT, 0);
        }
        this.setChangedAndSync();
    }

    private int storeItem(ItemStack stack) {
        int before = stack.getCount();
        ItemStack remainder = this.storage.addItem(stack);
        if (remainder.getCount() < before) {
            return before - remainder.getCount();
        }
        rotateInNewestItem(stack);
        return before;
    }

    private void rotateInNewestItem(ItemStack stack) {
        this.storage.setItem(0, this.storage.getItem(1).copy());
        this.storage.setItem(1, this.storage.getItem(2).copy());
        this.storage.setItem(2, stack.copy());
    }

    public void withdrawItem(LivingEntity user) {
        if (!user.getItemInHand(InteractionHand.MAIN_HAND).isEmpty()) {
            return;
        }
        for (int i = storage.getContainerSize() - 1; i >= 0; i--) {
            ItemStack stack = storage.removeItemNoUpdate(i);
            if (!stack.isEmpty()) {
                user.setItemInHand(InteractionHand.MAIN_HAND, stack);
                playActionEffects(SoundEvents.BARREL_CLOSE, 0.8F);
                if (level != null) {
                    level.blockEvent(this.worldPosition, this.getBlockState().getBlock(), EVENT_WITHDRAW, 0);
                }
                this.setChangedAndSync();
                return;
            }
        }
    }

    private boolean absorbMatchingItem(ItemStack itemStack) {
        for (int i = 0; i < storage.getContainerSize(); i++) {
            ItemStack stored = storage.getItem(i);
            if (!stored.isEmpty() && ItemStack.isSameItemSameComponents(stored, itemStack)) {
                ItemStack remainder = this.storage.addItem(itemStack);
                int inserted = itemStack.getCount() - remainder.getCount();
                if (inserted <= 0) {
                    return false;
                }
                itemStack.shrink(inserted);
                this.setChangedAndSync();
                return true;
            }
        }
        return false;
    }

    private void playActionEffects(net.minecraft.sounds.SoundEvent sound, float pitch) {
        if (level instanceof ServerLevel serverLevel) {
            BlockPos pos = this.getBlockPos();
            serverLevel.sendParticles(ParticleTypes.SMOKE, pos.getX() + 0.5, pos.getY() + 1.25, pos.getZ() + 0.5,
                    3, 0.25, 0.05, 0.25, 0.01);
            serverLevel.playSound(null, pos, sound, SoundSource.BLOCKS, 1.0F, pitch);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        ContainerHelper.saveAllItems(output.child(STORAGE), this.storage.getItems());
    }

    @Override
    public void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        ContainerHelper.loadAllItems(input.childOrEmpty(STORAGE), this.storage.getItems());
    }

    public NonNullList<ItemStack> getStoredItems() {
        NonNullList<ItemStack> items = NonNullList.create();
        for (ItemStack stack : this.storage.getItems()) {
            if (!stack.isEmpty()) {
                items.add(stack.copy());
            }
        }
        return items;
    }

    @Override
    public boolean triggerEvent(int id, int type) {
        if (this.level == null) {
            return false;
        }
        int tick = (int) this.level.getGameTime();
        switch (id) {
            case EVENT_PUT -> this.putState.start(tick);
            case EVENT_WITHDRAW -> this.withdrawState.start(tick);
            case EVENT_ENTER -> this.enterState.start(tick);
            default -> {
                return super.triggerEvent(id, type);
            }
        }
        return true;
    }
}
