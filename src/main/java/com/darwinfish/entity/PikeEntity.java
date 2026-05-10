package com.darwinfish.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.level.Level;

public class PikeEntity extends Salmon {

    // Таймер сытости. Если он <= 0, щука голодна. 12000 тиков = половина игрового дня.
    private int ticksUntilHungry = 0;

    public PikeEntity(EntityType<? extends Salmon> type, Level level) {
        super(type, level);
    }

    @Override
    protected void registerGoals() {
        super.registerGoals();

        // 1. Защита: Щука всегда даст сдачи, даже если сыта
        this.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(this));

        // 2. Охота: Щука ищет рыбу ТОЛЬКО если она голодна. Не ест других щук и СВОИХ МАЛЬКОВ.
        this.targetSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal<>(
                this, net.minecraft.world.entity.animal.AbstractFish.class, 10, true, false,
                e -> !(e instanceof com.darwinfish.entity.PikeEntity) &&
                        !(e instanceof com.darwinfish.entity.BabyPikeEntity) &&
                        this.isHungry()
        ));

        // 3. Атака
        this.goalSelector.addGoal(3, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(this, 1.2D, true));
    }

    @Override
    public void tick() {
        super.tick();

        // Постепенно перевариваем пищу (работает только на сервере)
        if (!this.level().isClientSide() && this.ticksUntilHungry > 0) {
            this.ticksUntilHungry--;
        }

        // Если щука задыхается, она забывает про врагов
        if (!this.isInWater() && this.getTarget() != null) {
            this.setTarget(null);
        }
    }

    // --- СИСТЕМА ГОЛОДА ---

    public boolean isHungry() {
        return this.ticksUntilHungry <= 0;
    }

    public void feed() {
        this.ticksUntilHungry = 6000;
        this.setTarget(null);          // Мгновенно теряет интерес к недоеденным жертвам
    }

    // Сохраняем уровень сытости при выходе из игры
    @Override
    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("TicksUntilHungry", this.ticksUntilHungry);
    }

    // Загружаем уровень сытости при входе в игру
    @Override
    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.ticksUntilHungry = compound.getInt("TicksUntilHungry");
    }

    @Override
    public boolean doHurtTarget(net.minecraft.world.entity.Entity target) {
        if (!this.isInWater()) {
            return false;
        }

        float damage = (float) this.getAttributeValue(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE);

        if (target instanceof net.minecraft.world.entity.player.Player) {
            damage = switch (this.level().getDifficulty()) {
                case EASY -> 2.0F;
                case NORMAL -> 4.0F;
                case HARD -> 7.0F;
                default -> 2.0F;
            };
        }

        return target.hurt(this.damageSources().mobAttack(this), damage);
    }
}