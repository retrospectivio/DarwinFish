package com.darwinfish.entity.goal;

import com.darwinfish.config.DarwinFishConfig;
import com.darwinfish.registry.ModAttachments;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class FishSurvivalGoal extends Goal {
    private final AbstractFish fish;
    private int hungerTicks = 0;
    private int crowdingTicks = 0;
    private LivingEntity target;
    private int checkCooldown = 0;

    public FishSurvivalGoal(AbstractFish fish) {
        this.fish = fish;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    // Вызываем этот метод извне (например, из FishEatPlantGoal), когда рыба поела
    public void resetHunger() {
        this.hungerTicks = 0;
    }

    public boolean isStarving() {
        return this.hungerTicks > (DarwinFishConfig.STARVATION_TIME.get() * 20);
    }

    @Override
    public boolean canUse() {
        if (this.checkCooldown > 0) {
            this.checkCooldown--;
            this.hungerTicks++;
            return false;
        }
        this.checkCooldown = 20; // Проверяем обстановку раз в секунду
        this.hungerTicks += 20;

        List<? extends AbstractFish> neighbors = this.fish.level().getEntitiesOfClass(
                AbstractFish.class,
                this.fish.getBoundingBox().inflate(8.0D)
        );

        boolean isOverpopulated = neighbors.size() > DarwinFishConfig.MAX_FISH_PER_CHUNK.get();

        if (isOverpopulated) {
            this.crowdingTicks += 20;
        } else {
            this.crowdingTicks = Math.max(0, this.crowdingTicks - 20);
        }

        boolean stressedFromCrowd = this.crowdingTicks > (DarwinFishConfig.OVERPOPULATION_TOLERANCE.get() * 20);

        // Если сыты и места много — живем мирно
        if (!isStarving() && !stressedFromCrowd) {
            // Если рыба просто хочет уплыть от толпы, но еще не агрессивна
            if (isOverpopulated && !stressedFromCrowd) {
                fleeFromCrowd(neighbors);
            }
            return false;
        }

        // Ищем жертву среди своих же (но исключаем щук, если сама рыба не щука)
        for (AbstractFish neighbor : neighbors) {
            if (neighbor != this.fish && neighbor.isAlive()) {
                this.target = neighbor;
                return true; // Включаем режим каннибала
            }
        }

        return false;
    }

    @Override
    public void start() {
        this.fish.getNavigation().moveTo(this.target, 1.4D);
    }

    @Override
    public void tick() {
        if (this.target == null || !this.target.isAlive()) {
            this.target = null;
            return;
        }

        this.fish.getLookControl().setLookAt(this.target, 30.0F, 30.0F);

        if (this.fish.distanceToSqr(this.target) < 3.0D) {
            // У ванильных рыб нет атрибута урона, поэтому наносим его напрямую
            this.target.hurt(this.fish.damageSources().mobAttack(this.fish), 1.0F);
            this.checkCooldown = 20; // Задержка между укусами
            this.target = null; // Сбрасываем цель после укуса
        } else if (this.fish.tickCount % 10 == 0) {
            this.fish.getNavigation().moveTo(this.target, 1.4D);
        }
    }

    private void fleeFromCrowd(List<? extends AbstractFish> neighbors) {
        // Простая логика побега: плывем в противоположную сторону от ближайшей рыбы
        if (neighbors.size() > 1) {
            AbstractFish closest = neighbors.get(1); // 0 - это сама рыба
            Vec3 fleeDir = this.fish.position().subtract(closest.position()).normalize().scale(5.0);
            this.fish.getNavigation().moveTo(this.fish.getX() + fleeDir.x, this.fish.getY() + fleeDir.y, this.fish.getZ() + fleeDir.z, 1.2D);
        }
    }
}