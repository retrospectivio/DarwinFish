package com.darwinfish.entity.goal;

import com.darwinfish.config.DarwinFishConfig;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;
import java.util.List;

public class FishSurvivalGoal extends Goal {
    private final AbstractFish fish;
    private int checkCooldown = 0;

    public FishSurvivalGoal(AbstractFish fish) {
        this.fish = fish;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE));
    }

    @Override
    public boolean canUse() {
        if (this.checkCooldown > 0) {
            this.checkCooldown--;
            return false;
        }
        this.checkCooldown = 20;

        List<? extends AbstractFish> neighbors = this.fish.level().getEntitiesOfClass(
                AbstractFish.class,
                this.fish.getBoundingBox().inflate(8.0D)
        );

        if (neighbors.size() > DarwinFishConfig.MAX_FISH_PER_CHUNK.get()) {
            fleeFromCrowd(neighbors);
        }

        return false;
    }

    private void fleeFromCrowd(List<? extends AbstractFish> neighbors) {
        for (AbstractFish neighbor : neighbors) {
            if (neighbor != this.fish) {
                Vec3 fleeDir = this.fish.position().subtract(neighbor.position()).normalize().scale(5.0);
                this.fish.getNavigation().moveTo(this.fish.getX() + fleeDir.x, this.fish.getY() + fleeDir.y, this.fish.getZ() + fleeDir.z, 1.2D);
                break;
            }
        }
    }
}