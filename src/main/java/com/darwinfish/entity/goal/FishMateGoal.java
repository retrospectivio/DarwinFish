package com.darwinfish.entity.goal;

import com.darwinfish.handler.FishBreedingHandler;
import com.darwinfish.registry.ModAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;

import java.util.EnumSet;
import java.util.List;

public class FishMateGoal extends Goal {
    private final AbstractFish fish;
    private AbstractFish mate;
    private int delay = 0;

    public FishMateGoal(AbstractFish fish) {
        this.fish = fish;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.fish.getData(ModAttachments.FISH_LOVE.get()).isInLove()) return false;
        if (this.delay > 0) {
            this.delay--;
            return false;
        }
        this.delay = 10;

        List<? extends AbstractFish> mates = fish.level().getEntitiesOfClass(
                fish.getClass(),
                fish.getBoundingBox().inflate(8.0),
                other -> other != fish
                        && other.getData(ModAttachments.FISH_LOVE.get()).isInLove()
        );

        if (mates.isEmpty()) return false;
        this.mate = mates.get(0);
        return true;
    }

    @Override
    public void start() {
        this.fish.getNavigation().moveTo(this.mate, 1.2D);
    }

    @Override
    public void stop() {
        this.mate = null;
        this.fish.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return this.mate != null && this.mate.isAlive()
                && this.mate.getData(ModAttachments.FISH_LOVE.get()).isInLove()
                && this.fish.getData(ModAttachments.FISH_LOVE.get()).isInLove();
    }

    @Override
    public void tick() {
        this.fish.getLookControl().setLookAt(this.mate, 30.0F, 30.0F);

        double dist = this.fish.distanceToSqr(this.mate);

        if (dist < 4.0) {
            FishBreedingHandler.doBreeding((ServerLevel) this.fish.level(), this.fish, this.mate);
            this.mate = null;
        } else if (this.fish.tickCount % 10 == 0) {
            this.fish.getNavigation().moveTo(this.mate, 1.2D);
        }
    }
}