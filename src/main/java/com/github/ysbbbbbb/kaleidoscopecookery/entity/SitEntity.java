package com.github.ysbbbbbb.kaleidoscopecookery.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;

public class SitEntity extends Entity {
    public static final int DEFAULT = 0;
    public static final int TRASH_CAN = 1;
    private static final String SIT_TYPE_KEY = "SitType";
    private static final EntityDataAccessor<Integer> SIT_TYPE = SynchedEntityData.defineId(SitEntity.class, EntityDataSerializers.INT);
    @Deprecated(forRemoval = false)
    public static final EntityType<SitEntity> TYPE = ModEntities.SIT;
    private int passengerTick = 0;

    public SitEntity(EntityType<?> entityTypeIn, Level worldIn) {
        super(entityTypeIn, worldIn);
    }

    public SitEntity(Level worldIn, BlockPos pos) {
        this(ModEntities.SIT, worldIn);
        this.setPos(pos.getX() + 0.5, pos.getY() + 0.4375, pos.getZ() + 0.5);
    }

    public SitEntity(Level worldIn, BlockPos pos, double y) {
        this(ModEntities.SIT, worldIn);
        this.setPos(pos.getX() + 0.5, pos.getY() + y, pos.getZ() + 0.5);
    }

    public SitEntity(Level worldIn, BlockPos pos, double y, int sitType) {
        this(worldIn, pos, y);
        this.setSitType(sitType);
    }

    @Override
    public Vec3 getPassengerRidingPosition(Entity entity) {
        return super.getPassengerRidingPosition(entity).add(0, -0.0625, 0);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(SIT_TYPE, DEFAULT);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.setSitType(input.getIntOr(SIT_TYPE_KEY, DEFAULT));
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putInt(SIT_TYPE_KEY, this.getSitType());
    }

    public int getSitType() {
        return this.entityData.get(SIT_TYPE);
    }

    public void setSitType(int sitType) {
        this.entityData.set(SIT_TYPE, sitType);
    }

    @Override
    public void tick() {
        super.tick();
        if (this.level() instanceof ServerLevel) {
            this.checkPassengers();
        }
    }

    private void checkPassengers() {
        if (this.getPassengers().isEmpty()) {
            passengerTick++;
        } else {
            passengerTick = 0;
        }
        if (passengerTick > 10) {
            this.discard();
        }
    }

    @Override
    protected void removePassenger(Entity passenger) {
        if (this.getSitType() == TRASH_CAN && passenger instanceof Player player) {
            player.playSound(ModSounds.TRASH_CAN);
        }
        super.removePassenger(passenger);
    }

    @Override
    public boolean skipAttackInteraction(Entity targetEntity) {
        return true;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource damageSource, float damageAmount) {
        return false;
    }

    @Override
    public void move(MoverType moverType, Vec3 movement) {
    }

    @Override
    public void push(Entity pushedEntity) {
    }

    @Override
    public void push(double x, double y, double z) {
    }

    @Override
    protected boolean repositionEntityAfterLoad() {
        return false;
    }

    @Override
    public void thunderHit(ServerLevel serverLevel, LightningBolt lightningBolt) {
    }

    @Override
    public void refreshDimensions() {
    }

    @Override
    public boolean canCollideWith(Entity entity) {
        return false;
    }

    @Override
    public boolean isPickable() {
        return false;
    }
}
