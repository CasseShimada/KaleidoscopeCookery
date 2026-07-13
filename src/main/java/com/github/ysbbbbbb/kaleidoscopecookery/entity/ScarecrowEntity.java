package com.github.ysbbbbbb.kaleidoscopecookery.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.util.ItemUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.ProblemReporter;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntitySpawnRequest;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.parrot.ShoulderRidingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.minecart.AbstractMinecart;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.TagValueInput;
import net.minecraft.world.level.storage.TagValueOutput;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LanternBlock;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.OptionalInt;
import java.util.function.Predicate;

public class ScarecrowEntity extends LivingEntity {
    @Deprecated(forRemoval = false)
    public static final EntityType<ScarecrowEntity> TYPE = ModEntities.SCARECROW;

    private static final EntityDataAccessor<OptionalInt> DATA_SHOULDER = SynchedEntityData.defineId(ScarecrowEntity.class, EntityDataSerializers.OPTIONAL_UNSIGNED_INT);
    private static final Predicate<Entity> RIDABLE_MINECARTS = e -> e instanceof AbstractMinecart minecart && minecart.isRideable();
    private static final Predicate<Entity> SHOULDER_RIDING_ENTITY = e -> e instanceof ShoulderRidingEntity entity && !entity.isOrderedToSit() && entity.canSitOnShoulder();
    private static final String HAND_ITEMS_TAG = "HandItems";
    private static final String ARMOR_ITEMS_TAG = "ArmorItems";
    private static final String SHOULDER_ENTITY_TAG = "ShoulderEntity";

    private final NonNullList<ItemStack> handItems = NonNullList.withSize(2, ItemStack.EMPTY);
    private final NonNullList<ItemStack> armorItems = NonNullList.withSize(4, ItemStack.EMPTY);
    private CompoundTag shoulderEntity = new CompoundTag();

    private long lastHit;
    private int cooldown;
    private long timeEntitySatOnShoulder;

    public ScarecrowEntity(EntityType<ScarecrowEntity> type, Level level) {
        super(type, level);
    }

    public ScarecrowEntity(Level level, double pX, double pY, double pZ) {
        this(ModEntities.SCARECROW, level);
        this.setPos(pX, pY, pZ);
    }

    public long getLastHitTime() {
        return this.lastHit;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return createLivingAttributes().add(Attributes.STEP_HEIGHT, 0.0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_SHOULDER, OptionalInt.empty());
    }

    @Override
    public void tick() {
        super.tick();
        if (this.cooldown > 0) {
            this.cooldown--;
        }
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand, Vec3 vec3) {
        ItemStack itemInHand = player.getItemInHand(hand);
        if (itemInHand.is(Items.NAME_TAG)) {
            return InteractionResult.PASS;
        }
        if (player.isSpectator()) {
            return InteractionResult.SUCCESS;
        }
        if (player.level().isClientSide()) {
            return InteractionResult.CONSUME;
        }
        if (hand == InteractionHand.OFF_HAND) {
            return InteractionResult.PASS;
        }
        if (this.cooldown > 0) {
            return InteractionResult.PASS;
        }
        if (isClickHand(vec3)) {
            return handleHandItems(player, itemInHand);
        }
        if (isClickHead(vec3)) {
            return handleHeadItems(player, itemInHand);
        }
        return InteractionResult.PASS;
    }

    private InteractionResult handleHeadItems(Player player, ItemStack itemInHand) {
        this.cooldown = 5;
        ItemStack headItem = this.getItemBySlot(EquipmentSlot.HEAD);
        if (itemInHand.isEmpty() && !headItem.isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, ItemStack.EMPTY);
            ItemUtils.giveItemToPlayer(player, headItem, player.getInventory().getSelectedSlot());
            return InteractionResult.SUCCESS;
        }

        if (!(itemInHand.getItem() instanceof BlockItem blockItem) || !(blockItem.getBlock() instanceof SkullBlock)) {
            return InteractionResult.PASS;
        }

        if (player.hasInfiniteMaterials() && headItem.isEmpty()) {
            this.setItemSlot(EquipmentSlot.HEAD, itemInHand.copyWithCount(1));
            this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
            ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_HEAD_ON_SCARECROW);
            return InteractionResult.SUCCESS;
        }
        if (!itemInHand.isEmpty() && itemInHand.getCount() > 1) {
            if (headItem.isEmpty()) {
                this.setItemSlot(EquipmentSlot.HEAD, itemInHand.split(1));
                this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
                ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_HEAD_ON_SCARECROW);
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        this.setItemSlot(EquipmentSlot.HEAD, itemInHand);
        this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
        player.setItemInHand(InteractionHand.MAIN_HAND, headItem);
        ModTrigger.EVENT.trigger(player, ModEventTriggerType.PLACE_HEAD_ON_SCARECROW);
        return InteractionResult.SUCCESS;
    }

    private InteractionResult handleHandItems(Player player, ItemStack itemInHand) {
        this.cooldown = 5;
        if (itemInHand.isEmpty()) {
            ItemStack mainhand = this.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack offhand = this.getItemInHand(InteractionHand.OFF_HAND);
            if (!mainhand.isEmpty()) {
                this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                ItemUtils.giveItemToPlayer(player, mainhand, player.getInventory().getSelectedSlot());
                return InteractionResult.SUCCESS;
            }
            if (!offhand.isEmpty()) {
                this.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                ItemUtils.giveItemToPlayer(player, offhand, player.getInventory().getSelectedSlot());
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }
        if (itemInHand.getItem() instanceof BlockItem blockItem && blockItem.getBlock() instanceof LanternBlock) {
            if (swapHand(InteractionHand.OFF_HAND, player, itemInHand)) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.LANTERN_PLACE, this.getSoundSource());
                return InteractionResult.SUCCESS;
            }
        }
        if (itemInHand.has(DataComponents.DAMAGE)) {
            if (swapHand(InteractionHand.MAIN_HAND, player, itemInHand)) {
                this.level().playSound(null, this.blockPosition(), SoundEvents.ITEM_FRAME_ADD_ITEM, this.getSoundSource());
                return InteractionResult.SUCCESS;
            }
        }
        return InteractionResult.PASS;
    }

    private boolean swapHand(InteractionHand hand, Player player, ItemStack itemInHand) {
        ItemStack scarecrowStack = this.getItemInHand(hand);
        if (player.hasInfiniteMaterials() && scarecrowStack.isEmpty() && !itemInHand.isEmpty()) {
            this.setItemInHand(hand, itemInHand.copyWithCount(1));
            return true;
        }
        if (!itemInHand.isEmpty() && itemInHand.getCount() > 1) {
            if (scarecrowStack.isEmpty()) {
                this.setItemInHand(hand, itemInHand.split(1));
                return true;
            }
            return false;
        }
        this.setItemInHand(hand, itemInHand);
        player.setItemInHand(InteractionHand.MAIN_HAND, scarecrowStack);
        return true;
    }

    private boolean isClickHand(Vec3 vector) {
        return 17 / 16.0 <= vector.y && vector.y <= 27 / 17.0;
    }

    private boolean isClickHead(Vec3 vector) {
        return 27 / 17.0 < vector.y;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        if (this.isRemoved()) {
            return false;
        }

        if (source.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
            this.kill(serverLevel);
            return false;
        }

        if (this.isInvulnerableTo(serverLevel, source)) {
            return false;
        }

        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            this.brokenByAnything(serverLevel, source);
            this.kill(serverLevel);
            return false;
        }

        Entity entity = source.getEntity();
        if (entity instanceof Player player) {
            if (!player.getAbilities().mayBuild) {
                return false;
            }
        }

        if (source.isCreativePlayer()) {
            this.playBrokenSound();
            this.showBreakingParticles();
            this.kill(serverLevel);
            return false;
        }

        long gameTime = this.level().getGameTime();
        if (gameTime - this.lastHit > 5) {
            this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_STAND_HIT, this.getSoundSource(), 0.3F, 1.0F);
            this.level().broadcastEntityEvent(this, (byte) 32);
            this.gameEvent(GameEvent.ENTITY_DAMAGE, source.getEntity());
            this.lastHit = gameTime;
            if (!this.getShoulderEntity().isEmpty()) {
                this.removeEntitiesOnShoulder();
            }
        } else {
            this.brokenByPlayer(serverLevel, source);
            this.showBreakingParticles();
            this.kill(serverLevel);
        }

        return true;
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id == 32) {
            if (this.level().isClientSide()) {
                this.lastHit = this.level().getGameTime();
            }
        } else {
            super.handleEntityEvent(id);
        }
    }

    private void brokenByPlayer(ServerLevel level, DamageSource damageSource) {
        ItemStack stack = new ItemStack(ModItems.SCARECROW);
        if (this.hasCustomName()) {
            stack.set(DataComponents.CUSTOM_NAME, this.getCustomName());
        }
        Block.popResource(this.level(), this.blockPosition(), stack);
        this.brokenByAnything(level, damageSource);
    }

    private void brokenByAnything(ServerLevel level, DamageSource damageSource) {
        this.playBrokenSound();
        this.dropAllDeathLoot(level, damageSource);
        for (int i = 0; i < this.handItems.size(); ++i) {
            ItemStack stack = this.handItems.get(i);
            if (!stack.isEmpty()) {
                Block.popResource(this.level(), this.blockPosition().above(), stack);
                this.handItems.set(i, ItemStack.EMPTY);
            }
        }

        for (int i = 0; i < this.armorItems.size(); ++i) {
            ItemStack stack = this.armorItems.get(i);
            if (!stack.isEmpty()) {
                Block.popResource(this.level(), this.blockPosition().above(), stack);
                this.armorItems.set(i, ItemStack.EMPTY);
            }
        }
    }

    private void playBrokenSound() {
        this.level().playSound(null, this.blockPosition(), SoundEvents.ARMOR_STAND_BREAK, this.getSoundSource(), 1.0F, 1.0F);
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel serverLevel) {
            BlockParticleOption particleOption = new BlockParticleOption(ParticleTypes.BLOCK, Blocks.OAK_PLANKS.defaultBlockState());
            serverLevel.sendParticles(particleOption,
                    this.getX(), this.getY(2 / 3.0),
                    this.getZ(), 10,
                    this.getBbWidth() / 4f,
                    this.getBbHeight() / 4f,
                    this.getBbWidth() / 4f,
                    0.05);
        }
    }

    private boolean setEntityOnShoulder(CompoundTag tag) {
        if (this.canEntityOnShoulder()) {
            this.setShoulderEntity(tag);
            this.timeEntitySatOnShoulder = this.level().getGameTime();
            return true;
        }
        return false;
    }

    private void removeEntitiesOnShoulder() {
        if (this.timeEntitySatOnShoulder + 20 < this.level().getGameTime()) {
            this.releaseShoulderEntity();
        }
    }

    private void releaseShoulderEntity() {
        this.respawnEntityOnShoulder(this.getShoulderEntity());
        this.setShoulderEntity(new CompoundTag());
    }

    private void respawnEntityOnShoulder(CompoundTag tag) {
        if (this.level() instanceof ServerLevel serverLevel && !tag.isEmpty()) {
            ValueInput input = TagValueInput.create(ProblemReporter.DISCARDING, this.level().registryAccess(), tag);
            EntityType.create(input, this.level(), new EntitySpawnRequest(EntitySpawnReason.LOAD, false)).ifPresent(entity -> {
                entity.setPos(this.getX(), this.getY() + 1.675, this.getZ());
                serverLevel.addWithUUID(entity);
            });
        }
    }

    @Override
    public void addAdditionalSaveData(ValueOutput output) {
        super.addAdditionalSaveData(output);
        ContainerHelper.saveAllItems(output.child(HAND_ITEMS_TAG), this.handItems);
        ContainerHelper.saveAllItems(output.child(ARMOR_ITEMS_TAG), this.armorItems);
        if (!this.shoulderEntity.isEmpty()) {
            output.store(SHOULDER_ENTITY_TAG, CompoundTag.CODEC, this.shoulderEntity);
        } else {
            output.discard(SHOULDER_ENTITY_TAG);
        }
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        input.child(HAND_ITEMS_TAG).ifPresent(child -> ContainerHelper.loadAllItems(child, this.handItems));
        input.child(ARMOR_ITEMS_TAG).ifPresent(child -> ContainerHelper.loadAllItems(child, this.armorItems));
        this.setShoulderEntity(input.read(SHOULDER_ENTITY_TAG, CompoundTag.CODEC).orElse(new CompoundTag()));
    }

    protected void tickHeadTurn(float yRot) {
        this.yBodyRotO = this.yRotO;
        this.yBodyRot = this.getYRot();
    }

    @Override
    public void setYBodyRot(float offset) {
        this.yBodyRotO = this.yRotO = offset;
        this.yHeadRotO = this.yHeadRot = offset;
    }

    @Override
    public void setYHeadRot(float rotation) {
        this.yBodyRotO = this.yRotO = rotation;
        this.yHeadRotO = this.yHeadRot = rotation;
    }

    @Override
    public void kill(ServerLevel level) {
        if (!this.getShoulderEntity().isEmpty()) {
            this.releaseShoulderEntity();
        }
        this.remove(RemovalReason.KILLED);
        this.gameEvent(GameEvent.ENTITY_DIE);
    }

    @Override
    public boolean isPushable() {
        return false;
    }

    @Override
    protected void doPush(Entity entity) {
    }

    @Override
    protected void pushEntities() {
        List<Entity> list = this.level().getEntities(this, this.getBoundingBox(), RIDABLE_MINECARTS);
        for (Entity entity : list) {
            if (this.distanceToSqr(entity) <= 0.2) {
                entity.push(this);
                return;
            }
        }

        if (this.canEntityOnShoulder()) {
            list = this.level().getEntities(this, this.getBoundingBox().inflate(2), SHOULDER_RIDING_ENTITY);
            for (Entity entity : list) {
                if (this.distanceToSqr(entity) <= 1.5 && entity instanceof ShoulderRidingEntity shoulderEntity
                    && setEntityOnShoulder(shoulderEntity)) {
                    return;
                }
            }
        }
    }

    private boolean setEntityOnShoulder(ShoulderRidingEntity entity) {
        String id = getSerializableEntityId(entity);
        if (id == null) {
            return false;
        }
        TagValueOutput output = TagValueOutput.createWithContext(ProblemReporter.DISCARDING, this.level().registryAccess());
        output.putString("id", id);
        entity.saveWithoutId(output);
        CompoundTag tag = output.buildResult();
        if (this.setEntityOnShoulder(tag)) {
            entity.discard();
            return true;
        }
        return false;
    }

    @Nullable
    private static String getSerializableEntityId(Entity entity) {
        if (!entity.getType().canSerialize()) {
            return null;
        }
        return entity.typeHolder().unwrapKey()
                .map(key -> key.identifier().toString())
                .orElse(null);
    }

    private boolean canEntityOnShoulder() {
        return !this.isPassenger() && this.onGround() && !this.isInWater() && !this.isInPowderSnow && this.getShoulderEntity().isEmpty();
    }



    @Override
    public ItemStack getItemBySlot(EquipmentSlot slot) {
        return switch (slot.getType()) {
            case HAND -> this.handItems.get(slot.getIndex());
            case HUMANOID_ARMOR -> this.armorItems.get(slot.getIndex());
            default -> ItemStack.EMPTY;
        };
    }

    @Override
    public void setItemSlot(EquipmentSlot slot, ItemStack stack) {
        switch (slot.getType()) {
            case HAND:
                this.onEquipItem(slot, this.handItems.set(slot.getIndex(), stack), stack);
                break;
            case HUMANOID_ARMOR:
                this.onEquipItem(slot, this.armorItems.set(slot.getIndex(), stack), stack);
        }
    }

    @Override
    public boolean skipAttackInteraction(Entity entity) {
        return entity instanceof Player player && !this.level().mayInteract(player, this.blockPosition());
    }

    @Override
    public HumanoidArm getMainArm() {
        return HumanoidArm.RIGHT;
    }

    @Override
    public Fallsounds getFallSounds() {
        return new Fallsounds(SoundEvents.ARMOR_STAND_FALL, SoundEvents.ARMOR_STAND_FALL);
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundEvents.ARMOR_STAND_HIT;
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ARMOR_STAND_BREAK;
    }

    @Override
    public void thunderHit(ServerLevel level, LightningBolt lightningBolt) {
    }

    @Override
    public boolean isAffectedByPotions() {
        return false;
    }

    @Override
    public boolean attackable() {
        return false;
    }

    @Override
    public ItemStack getPickResult() {
        return new ItemStack(ModItems.SCARECROW);
    }

    public CompoundTag getShoulderEntity() {
        if (this.level().isClientSide()) {
            OptionalInt variant = this.entityData.get(DATA_SHOULDER);
            if (variant.isEmpty()) {
                return new CompoundTag();
            }
            CompoundTag tag = new CompoundTag();
            tag.putString("id", EntityType.getKey(EntityTypes.PARROT).toString());
            tag.putInt("Variant", variant.getAsInt());
            return tag;
        }
        return this.shoulderEntity;
    }

    public void setShoulderEntity(CompoundTag tag) {
        this.shoulderEntity = tag;
        OptionalInt variant = OptionalInt.empty();
        String id = tag.getStringOr("id", "");
        if (id.equals(EntityType.getKey(EntityTypes.PARROT).toString())) {
            variant = OptionalInt.of(tag.getIntOr("Variant", 0));
        }
        this.entityData.set(DATA_SHOULDER, variant);
    }
}
