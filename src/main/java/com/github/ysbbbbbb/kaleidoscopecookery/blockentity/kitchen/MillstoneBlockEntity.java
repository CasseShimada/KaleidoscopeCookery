package com.github.ysbbbbbb.kaleidoscopecookery.blockentity.kitchen;

import com.github.ysbbbbbb.kaleidoscopecookery.api.blockentity.IMillstone;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.ActionEventCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.api.event.MillstoneTakeItemCallback;
import com.github.ysbbbbbb.kaleidoscopecookery.blockentity.BaseBlockEntity;
import com.github.ysbbbbbb.kaleidoscopecookery.crafting.recipe.MillstoneRecipe;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.MillstoneBindableData;
import com.github.ysbbbbbb.kaleidoscopecookery.datamap.resources.MillstoneBindableDataReloadListener;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModBlocks;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModRecipes;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import com.github.ysbbbbbb.kaleidoscopecookery.init.tag.TagMod;
import com.github.ysbbbbbb.kaleidoscopecookery.inventory.transfer.MillstoneInputStorage;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.protocol.game.ClientboundSetActionBarTextPacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.equine.AbstractChestedHorse;
import net.minecraft.world.entity.animal.equine.AbstractHorse;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class MillstoneBlockEntity extends BaseBlockEntity implements IMillstone {
    public static final int MAX_INPUT_COUNT = 8;
    private static final String ENTITY_ID_KEY = "EntityId";
    private static final String CACHE_ROT_KEY = "CacheRot";
    private static final String ROT_SPEED_TICK_KEY = "RotSpeedTick";
    private static final String LIFT_ANGLE_KEY = "LiftAngle";
    private static final String INPUT_ITEM_KEY = "InputItem";
    private static final String OUTPUT_ITEM_KEY = "OutputItem";
    private static final String CARRIER_INGREDIENT_KEY = "CarrierIngredient";
    private static final String PROGRESS_KEY = "Progress";

    private final RecipeManager.CachedCheck<SingleRecipeInput, MillstoneRecipe> quickCheck = RecipeManager.createCheck(ModRecipes.MILLSTONE_RECIPE);

    private UUID entityId = Util.NIL_UUID;
    // 缓存的角度，避免动画突兀的跳动变化
    private float cacheRot = 0f;
    private float rotSpeedTick = 200f;
    private float liftAngle = 5f;
    private ItemStack input = ItemStack.EMPTY;
    private ItemStack output = ItemStack.EMPTY;
    private Optional<Ingredient> carrier = Optional.empty();
    private int progress = 0;
    private final MillstoneInputStorage inputStorage = new MillstoneInputStorage(this);

    private @Nullable Mob bindEntity;
    private Vec3 offset = Vec3.ZERO;

    public MillstoneBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.MILLSTONE_BE, pos, state);
    }

    public float getRotation(Level level, float partialTick) {
        float degPerTick = 360f / Math.max(this.rotSpeedTick, 1);
        float gameTime = level.getGameTime() + partialTick;
        return (this.cacheRot + gameTime * degPerTick) % 360;
    }

    public void tick(Level level) {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return;
        }
        if (Util.NIL_UUID.equals(this.entityId)) {
            return;
        }

        // 每秒额外检查一次输出，强制触发磨盘完成回调
        if (serverLevel.getGameTime() % 20 == 0 && this.input.isEmpty() && !this.output.isEmpty()) {
            ActionEventCallback.MillstoneFinish.EVENT.invoker().onMillstoneFinish(this, this.bindEntity);
        }

        // 旋转一圈的时间 (ticks)
        float rot = this.getRotation(level, 0);
        Vec3 center = Vec3.atBottomCenterOf(this.getBlockPos());
        double maxDistanceSqr = 5 * 5;
        // 服务器端检查实体是否还存在
        if (bindEntity == null) {
            // 必须距离磨盘足够近才可以（5 格）
            if (serverLevel.getEntity(entityId) instanceof Mob mob
                && mob.isAlive() && mob.distanceToSqr(center) < maxDistanceSqr
                && this.canBindEntity(mob)) {
                this.bindEntity(mob);
            } else {
                this.entityId = Util.NIL_UUID;
                this.cacheRot = 0f;
                this.liftAngle = 0f;
                this.setChangedAndSync();
                return;
            }
        } else if (!bindEntity.isAlive()
                   || bindEntity.distanceToSqr(center) >= maxDistanceSqr
                   || bindEntity.fallDistance > 0.5f
                   || bindEntity.isInWall()
                   || this.saddleEntityIsControlling(bindEntity)) {
            this.entityId = Util.NIL_UUID;
            this.bindEntity = null;
            this.cacheRot = rot;
            this.liftAngle = 0f;
            this.setChangedAndSync();
            return;
        }

        // 如果实体存在，检查是否需要更新位置
        Vec3 pos = new Vec3(0, 0, 2)
                .add(this.offset)
                .yRot(rot * Mth.DEG_TO_RAD)
                .add(center);
        this.bindEntity.setPos(pos.x, pos.y, pos.z);
        this.bindEntity.setYRot(-rot - 90);
        this.bindEntity.setXRot(0);

        // 如果实体带有库存，那么可以尝试往磨盘里放物品
        if (this.bindEntity.tickCount % 10 == 0 && this.output.isEmpty() && this.input.isEmpty() && this.progress <= 0) {
            if (bindEntity instanceof AbstractChestedHorse chestedHorse) {
                int slotCount = chestedHorse.getInventorySize();
                for (int i = 0; i < slotCount; i++) {
                    SlotAccess slot = chestedHorse.getSlot(i);
                    ItemStack stackInSlot = slot.get();
                    if (stackInSlot.isEmpty()) {
                        continue;
                    }
                    int extractCount = Math.min(MAX_INPUT_COUNT, stackInSlot.getCount());
                    ItemStack stack = stackInSlot.copyWithCount(extractCount);
                    if (this.onPutItem(level, stack)) {
                        int remaining = stackInSlot.getCount() - extractCount;
                        slot.set(remaining > 0 ? stackInSlot.copyWithCount(remaining) : ItemStack.EMPTY);
                        return;
                    }
                }
            }
        }

        // 释放粒子效果
        if (serverLevel.getGameTime() % 5 == 2) {
            Item item = !this.output.isEmpty() ? this.output.getItem() : (!this.input.isEmpty() ? this.input.getItem() : Items.AIR);
            if (item != Items.AIR) {
                Vec3 particlePos = new Vec3(0, 1, 1)
                        .yRot(rot * Mth.DEG_TO_RAD)
                        .add(center);
                if (item instanceof BlockItem blockItem) {
                    BlockState block = blockItem.getBlock().defaultBlockState();
                    BlockParticleOption option = new BlockParticleOption(ParticleTypes.BLOCK, block);
                    serverLevel.sendParticles(option,
                            particlePos.x, particlePos.y, particlePos.z,
                            5, 0.1, 0.1, 0.1,
                            0.05);
                } else {
                    ItemParticleOption option = new ItemParticleOption(ParticleTypes.ITEM, net.minecraft.world.item.ItemStackTemplate.fromNonEmptyStack(item.getDefaultInstance()));
                    serverLevel.sendParticles(option,
                            particlePos.x, particlePos.y, particlePos.z,
                            5, 0.1, 0.1, 0.1,
                            0.05);
                }
            }
        }

        // 播放音频
        if (serverLevel.getGameTime() % 25 == 0) {
            float pitch = level.getRandom().nextFloat() * 0.2f + 0.9f;
            serverLevel.playSound(null, this.worldPosition,
                    ModSounds.BLOCK_MILLSTONE, SoundSource.BLOCKS, 0.5f, pitch);
        }

        // 输出栏为空才能进行研磨
        if (this.progress > 0 && this.output.isEmpty()) {
            this.progress--;
            this.setChanged();
            // 每 10 tick 同步一次客户端进度
            if (this.progress % 10 == 0) {
                this.syncToClient();
            }
        }

        // 当进度为 0 时，检查输入输出
        if (this.progress <= 0 && !this.input.isEmpty() && this.output.isEmpty()) {
            SingleRecipeInput container = new SingleRecipeInput(this.input);
            this.quickCheck.getRecipeFor(container, serverLevel).ifPresentOrElse(recipe -> {
                this.output = recipe.value().assemble(container);
                // 依据输入数量决定输出数量
                this.output.setCount(this.output.getCount() * this.input.getCount());
                this.input = ItemStack.EMPTY;
                this.carrier = recipe.value().getCarrier();
                this.setChangedAndSync();
            }, () -> {
                // 几乎不太可能，但是此时把输入转向输出
                this.output = this.input.copyAndClear();
                this.input = ItemStack.EMPTY;
                this.carrier = Optional.empty();
                this.setChangedAndSync();
            });

            // 触发完成事件，用于特殊情况判断（比如油壶自动化）
            ActionEventCallback.MillstoneFinish.EVENT.invoker().onMillstoneFinish(this, this.bindEntity);

        }
    }

    @Override
    public boolean onPutItem(Level level, ItemStack putOnItem) {
        return this.putItem(level, putOnItem, false);
    }

    public boolean onPutItem(Level level, LivingEntity user, ItemStack putOnItem) {
        return this.putItem(level, putOnItem, user.hasInfiniteMaterials());
    }

    private boolean putItem(Level level, ItemStack putOnItem, boolean keepSourceStack) {
        if (level.isClientSide()) {
            return false;
        }
        // 先清空输出槽才可以
        if (!this.output.isEmpty()) {
            return false;
        }
        // 正在工作中，不能放入
        if (this.progress > 0 && !this.input.isEmpty()) {
            return false;
        }
        if (!(level instanceof ServerLevel serverLevel)) {
            return false;
        }
        SingleRecipeInput container = new SingleRecipeInput(putOnItem);
        return this.quickCheck.getRecipeFor(container, serverLevel).map(recipe -> {
            this.input = keepSourceStack
                    ? putOnItem.copyWithCount(Math.min(MAX_INPUT_COUNT, putOnItem.getCount()))
                    : putOnItem.split(MAX_INPUT_COUNT);
            this.progress = Math.max(Math.round(this.rotSpeedTick), 1);
            this.setChangedAndSync();
            level.playSound(null, this.worldPosition,
                    SoundEvents.STONE_HIT, SoundSource.BLOCKS, 0.8f,
                    level.getRandom().nextFloat() * 0.2f + 0.9f);
            return true;
        }).orElse(false);
    }

    public boolean canPutItem(Level level, ItemStack putOnItem) {
        if (putOnItem.isEmpty() || !this.output.isEmpty() || this.progress > 0 && !this.input.isEmpty()) {
            return false;
        }
        if (level.recipeAccess() instanceof RecipeManager recipeManager) {
            return recipeManager.getRecipeFor(ModRecipes.MILLSTONE_RECIPE, new SingleRecipeInput(putOnItem), level).isPresent();
        }
        return true;
    }

    public boolean canAutomationInsert() {
        return this.output.isEmpty() && this.input.isEmpty() && this.progress <= 0;
    }

    public boolean canAutomationInsert(ItemStack stack) {
        if (!this.canAutomationInsert() || !(this.level instanceof ServerLevel serverLevel)) {
            return false;
        }
        return this.quickCheck.getRecipeFor(new SingleRecipeInput(stack), serverLevel).isPresent();
    }

    @Override
    public boolean onTakeItem(LivingEntity user, ItemStack heldItem) {
        if (this.level != null && this.level.isClientSide()) {
            return false;
        }
        // 先尝试取出输出槽
        if (!this.output.isEmpty()) {
            // 事件系统处理特殊情况
            MillstoneTakeItemCallback.Result callbackResult =
                    MillstoneTakeItemCallback.EVENT.invoker().takeItem(user, heldItem, this);
            if (callbackResult.handled()) {
                return callbackResult.succeeds();
            }
            // 兼容容器是否正确
            int consumeCount = this.output.getCount();
            // 返回容器
            if (carrier.isPresent()) {
                Ingredient carrierIngredient = carrier.get();
                if (!carrierIngredient.test(heldItem)) {
                    Component carrierName = ItemUtils.getIngredientName(user.level(), carrierIngredient);
                    this.sendActionBarMessage(user, "tip.kaleidoscope_cookery.pot.need_carrier", carrierName);
                    return false;
                }
                // 依据容器数量消耗
                if (!user.hasInfiniteMaterials()) {
                    consumeCount = Math.min(consumeCount, heldItem.getCount());
                    ItemStack container = ItemUtils.getContainerStack(heldItem.split(consumeCount));
                    if (!container.isEmpty()) {
                        ItemUtils.getItemToLivingEntity(user, container);
                    }
                }
            }
            ItemUtils.getItemToLivingEntity(user, this.output.split(consumeCount));
            if (this.output.isEmpty()) {
                this.resetWhenTakeout();
            }
            return true;
        }
        // 如果没有输出，则尝试取出输入槽
        if (!this.input.isEmpty()) {
            ItemUtils.getItemToLivingEntity(user, this.input.copyAndClear());
            this.input = ItemStack.EMPTY;
            this.progress = 0;
            this.setChangedAndSync();
            return true;
        }
        return false;
    }

    public boolean canTakeItem(ItemStack heldItem) {
        if (!this.output.isEmpty()) {
            return this.carrier.isEmpty() || this.carrier.get().test(heldItem);
        }
        return !this.input.isEmpty();
    }

    public void resetWhenTakeout() {
        this.output = ItemStack.EMPTY;
        this.carrier = Optional.empty();
        this.progress = 0;
        this.setChangedAndSync();
    }

    public boolean saddleEntityIsControlling(Mob mob) {
        // 骑乘的生物不能被绑定
        return mob.getControllingPassenger() != null;
    }

    public boolean canBindEntity(Mob mob) {
        if (!mob.is(TagMod.MILLSTONE_BINDABLE)) {
            return false;
        }
        if (mob.getVehicle() != null) {
            // 骑乘的生物不能被绑定
            return false;
        }
        // 禁止童工！
        if (mob.isBaby()) {
            return false;
        }
        // 已经被骑乘的生物不能被绑定
        if (this.saddleEntityIsControlling(mob)) {
            return false;
        }
        // 如果是可驯服生物，必须已经被驯服才行
        if (mob instanceof AbstractHorse horse) {
            return horse.isTamed();
        }
        if (mob instanceof TamableAnimal tamableAnimal) {
            return tamableAnimal.isTame();
        }
        if (mob instanceof OwnableEntity ownable) {
            return ownable.getOwner() != null;
        }
        return true;
    }

    public void bindEntity(Mob mob) {
        if (this.level == null || this.level.isClientSide()) {
            // 仅在服务器端绑定实体
            return;
        }
        if (!mob.isAlive()) {
            return;
        }
        this.entityId = mob.getUUID();
        this.bindEntity = mob;
        // 缓存角度纠正
        float rot = this.getRotation(this.level, 0);
        this.cacheRot = this.cacheRot - (rot - this.cacheRot);

        // 读取数据地图，获取抬升角度
        MillstoneBindableData data = MillstoneBindableDataReloadListener.getData(mob.getType());
        this.rotSpeedTick = data.rotSpeedTick();
        this.liftAngle = data.liftAngle();
        this.offset = data.offset();
        this.setChangedAndSync();
    }

    public void sendActionBarMessage(LivingEntity user, String key, Object... args) {
        if (user instanceof ServerPlayer serverPlayer) {
            MutableComponent message = Component.translatable(key, args);
            serverPlayer.connection.send(new ClientboundSetActionBarTextPacket(message));
        }
    }

    @Override
    protected void saveAdditional(ValueOutput outputTag) {
        super.saveAdditional(outputTag);
        outputTag.putString(ENTITY_ID_KEY, entityId.toString());
        outputTag.putFloat(CACHE_ROT_KEY, cacheRot);
        outputTag.putFloat(ROT_SPEED_TICK_KEY, rotSpeedTick);
        outputTag.putFloat(LIFT_ANGLE_KEY, liftAngle);
        if (!input.isEmpty()) {
            outputTag.store(INPUT_ITEM_KEY, ItemStack.CODEC, input);
        }
        if (!output.isEmpty()) {
            outputTag.store(OUTPUT_ITEM_KEY, ItemStack.CODEC, output);
        }
        this.carrier.ifPresent(ingredient -> outputTag.store(CARRIER_INGREDIENT_KEY, Ingredient.CODEC, ingredient));
        outputTag.putInt(PROGRESS_KEY, this.progress);
    }

    @Override
    public void loadAdditional(ValueInput inputTag) {
        super.loadAdditional(inputTag);
        this.entityId = inputTag.getString(ENTITY_ID_KEY).map(UUID::fromString).orElse(Util.NIL_UUID);
        this.cacheRot = inputTag.getFloatOr(CACHE_ROT_KEY, 0.0F);
        this.rotSpeedTick = inputTag.getFloatOr(ROT_SPEED_TICK_KEY, 0.0F);
        this.liftAngle = inputTag.getFloatOr(LIFT_ANGLE_KEY, 0.0F);
        this.input = inputTag.read(INPUT_ITEM_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.output = inputTag.read(OUTPUT_ITEM_KEY, ItemStack.CODEC).orElse(ItemStack.EMPTY);
        this.carrier = inputTag.read(CARRIER_INGREDIENT_KEY, Ingredient.CODEC);
        this.progress = inputTag.getIntOr(PROGRESS_KEY, 0);
    }


    public boolean hasEntity() {
        return !Util.NIL_UUID.equals(this.entityId);
    }

    public float getCacheRot() {
        return this.cacheRot;
    }

    public float getLiftAngle() {
        return this.liftAngle;
    }

    public ItemStack getInput() {
        return this.input.copy();
    }

    public MillstoneInputStorage getInputStorage() {
        return this.inputStorage;
    }

    public ItemStack getOutput() {
        return this.output.copy();
    }

    public Optional<Ingredient> getCarrier() {
        return this.carrier;
    }

    public float getProgressPercent() {
        float total = Math.max(this.rotSpeedTick, 1);
        return (total - this.progress) / total;
    }
}
