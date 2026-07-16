package com.github.ysbbbbbb.kaleidoscopecookery.entity;

import com.github.ysbbbbbb.kaleidoscopecookery.advancements.criterion.ModEventTriggerType;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModEntities;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModItems;
import com.github.ysbbbbbb.kaleidoscopecookery.init.ModTrigger;
import com.github.ysbbbbbb.kaleidoscopecookery.util.LegacyItemStackCompat;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.animal.wolf.Wolf;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;


public class ThrowableBaoziEntity extends ThrowableItemProjectile {
    @Deprecated(forRemoval = false)
    public static final EntityType<ThrowableBaoziEntity> TYPE = ModEntities.THROWABLE_BAOZI;

    public ThrowableBaoziEntity(EntityType<? extends ThrowableItemProjectile> entityType, Level level) {
        super(entityType, level);
    }

    public ThrowableBaoziEntity(EntityType<? extends ThrowableItemProjectile> entityType, double x, double y, double z, Level level) {
        super(entityType, x, y, z, level, new ItemStack(ModItems.BAOZI));
    }

    public ThrowableBaoziEntity(EntityType<? extends ThrowableItemProjectile> entityType, LivingEntity shooter, Level level) {
        super(entityType, shooter, level, new ItemStack(ModItems.BAOZI));
    }

    public ThrowableBaoziEntity(Level level, LivingEntity shooter) {
        super(ModEntities.THROWABLE_BAOZI, shooter, level, new ItemStack(ModItems.BAOZI));
    }

    @Override
    protected Item getDefaultItem() {
        return ModItems.BAOZI;
    }

    @Override
    public void readAdditionalSaveData(ValueInput input) {
        super.readAdditionalSaveData(input);
        ItemStack legacyItem = LegacyItemStackCompat.readItemStack(input, "Item");
        if (!legacyItem.isEmpty()) {
            this.setItem(legacyItem);
        }
    }

    @Override
    public void handleEntityEvent(byte id) {
        ItemStack entityStack = new ItemStack(this.getDefaultItem());
        if (id == EntityEvent.DEATH) {
            ParticleOptions option = new ItemParticleOption(ParticleTypes.ITEM, ItemStackTemplate.fromNonEmptyStack(entityStack));
            for (int i = 0; i < 12; i++) {
                this.level().addParticle(option, this.getX(), this.getY(), this.getZ(),
                        (this.getRandom().nextFloat() * 2 - 1) * 0.1,
                        (this.getRandom().nextFloat() * 2 - 1) * 0.1 + 0.1,
                        (this.getRandom().nextFloat() * 2 - 1) * 0.1);
            }
        }

        if (id == EntityEvent.LOVE_HEARTS) {
            for (int i = 0; i < 7; i++) {
                double offsetX = (this.getRandom().nextDouble() - 0.5) * 0.5;
                double offsetY = this.getRandom().nextDouble() * 0.5 + 0.5;
                double offsetZ = (this.getRandom().nextDouble() - 0.5) * 0.5;
                this.level().addParticle(ParticleTypes.HEART,
                        this.getX() + offsetX,
                        this.getY() + offsetY,
                        this.getZ() + offsetZ,
                        0.0, 0.0, 0.0);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult hitResult) {
        super.onHitEntity(hitResult);
        if (!(this.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        Entity hitEntity = hitResult.getEntity();
        hitEntity.hurtServer(serverLevel, this.damageSources().thrown(this, this.getOwner()), 0);
        if (hitEntity instanceof Wolf wolf) {
            wolf.heal(wolf.getMaxHealth());
            serverLevel.broadcastEntityEvent(this, EntityEvent.LOVE_HEARTS);
            if (this.getOwner() instanceof ServerPlayer player) {
                ModTrigger.EVENT.trigger(player, ModEventTriggerType.MEAT_BUNS_BEAT_DOGS);
            }
        }
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.broadcastEntityEvent(this, EntityEvent.DEATH);
            this.playSound(SoundEvents.SNOW_HIT, 1.0F, (this.getRandom().nextFloat() - this.getRandom().nextFloat()) * 0.2F + 1.0F);
            this.discard();
        }
    }
}
