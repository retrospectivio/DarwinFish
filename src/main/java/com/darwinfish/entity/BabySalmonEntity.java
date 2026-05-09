package com.darwinfish.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BabySalmonEntity extends Salmon implements BabyFishEntity {

    private int ageTicks;

    public BabySalmonEntity(EntityType<? extends BabySalmonEntity> type, Level level) {
        super(type, level);
        this.ageTicks = com.darwinfish.config.DarwinFishConfig.BABY_GROWTH_TIME.get() * 20;
    }

    @Override
    public void tick() {
        super.tick();
        if (!level().isClientSide && ageTicks > 0) {
            ageTicks--;
            if (ageTicks <= 0) {
                growUp();
            }
        }
    }

    private void growUp() {
        ServerLevel sl = (ServerLevel) level();
        Salmon adult = EntityType.SALMON.create(sl);
        if (adult != null) {
            adult.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
            adult.setFromBucket(false);
            adult.setHealth(this.getHealth());

            // НОВЫЙ КОД: Передача флага постоянства
            if (this.isPersistenceRequired()) {
                adult.setPersistenceRequired();
            }

            sl.addFreshEntity(adult);
        }
        discard();
    }

    @Override
    public ItemStack getBucketItemStack() {
        return ItemStack.EMPTY;
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AgeTicks", ageTicks);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.contains("AgeTicks")) ageTicks = tag.getInt("AgeTicks");
    }

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.SALMON_AMBIENT; }
    @Override protected SoundEvent getDeathSound()   { return SoundEvents.SALMON_DEATH;   }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.SALMON_HURT; }
    @Override protected SoundEvent getFlopSound()    { return SoundEvents.SALMON_FLOP;   }
}
