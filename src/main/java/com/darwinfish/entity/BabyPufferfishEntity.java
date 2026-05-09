package com.darwinfish.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Baby pufferfish. Always stays in STATE_SMALL (0) — cannot puff up.
 */
public class BabyPufferfishEntity extends Pufferfish implements BabyFishEntity {

    private int ageTicks;

    public BabyPufferfishEntity(EntityType<? extends BabyPufferfishEntity> type, Level level) {
        super(type, level);
        this.ageTicks = com.darwinfish.config.DarwinFishConfig.BABY_GROWTH_TIME.get() * 20;
    }

    @Override
    public void tick() {
        super.tick();
        // Force puff state to remain small — override any AI inflation
        if (getPuffState() != STATE_SMALL) {
            setPuffState(STATE_SMALL);
        }
        if (!level().isClientSide && ageTicks > 0) {
            ageTicks--;
            if (ageTicks <= 0) {
                growUp();
            }
        }
    }

    private void growUp() {
        ServerLevel sl = (ServerLevel) level();
        Pufferfish adult = EntityType.PUFFERFISH.create(sl);
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

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.PUFFER_FISH_AMBIENT; }
    @Override protected SoundEvent getDeathSound()   { return SoundEvents.PUFFER_FISH_DEATH;   }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.PUFFER_FISH_HURT; }
    @Override protected SoundEvent getFlopSound()    { return SoundEvents.PUFFER_FISH_FLOP;   }
}
