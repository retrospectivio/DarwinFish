package com.darwinfish.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Baby cod. Scales down to 50% in the renderer.
 * Grows into an adult {@link Cod} after {@link BabyFishEntity#GROWTH_TICKS}.
 */
public class BabyCodEntity extends Cod implements BabyFishEntity {

    private int ageTicks;

    public BabyCodEntity(EntityType<? extends BabyCodEntity> type, Level level) {
        super(type, level);
        // Забираем время из настроек при рождении малька
        this.ageTicks = com.darwinfish.config.DarwinFishConfig.BABY_GROWTH_TIME.get() * 20;
    }

    // -------------------------------------------------------------------------
    // Growth
    // -------------------------------------------------------------------------

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
        Cod adult = EntityType.COD.create(sl);
        if (adult != null) {
            adult.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
            adult.setFromBucket(false);
            adult.setHealth(this.getHealth()); // Сохраняем здоровье при взрослении

            // НОВЫЙ КОД: Передача флага постоянства
            if (this.isPersistenceRequired()) {
                adult.setPersistenceRequired();
            }

            sl.addFreshEntity(adult);
        }
        discard();
    }

    // -------------------------------------------------------------------------
    // No bucket pickup for fry
    // -------------------------------------------------------------------------

    @Override
    public ItemStack getBucketItemStack() {
        return ItemStack.EMPTY;
    }

    // -------------------------------------------------------------------------
    // NBT
    // -------------------------------------------------------------------------

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

    // -------------------------------------------------------------------------
    // Sounds (same as adult)
    // -------------------------------------------------------------------------

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.COD_AMBIENT; }
    @Override protected SoundEvent getDeathSound()   { return SoundEvents.COD_DEATH;   }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.COD_HURT; }
    @Override protected SoundEvent getFlopSound()    { return SoundEvents.COD_FLOP;   }
}
