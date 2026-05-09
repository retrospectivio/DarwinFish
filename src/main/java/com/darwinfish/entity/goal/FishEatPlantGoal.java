package com.darwinfish.entity.goal;

import com.darwinfish.config.DarwinFishConfig;
import com.darwinfish.data.FishLoveData;
import com.darwinfish.registry.ModAttachments;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.TallSeagrassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class FishEatPlantGoal extends Goal {
    private final AbstractFish fish;
    private BlockPos targetPos;
    private int checkCooldown;

    public FishEatPlantGoal(AbstractFish fish) {
        this.fish = fish;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!DarwinFishConfig.NATURAL_BREEDING.get()) return false;

        if (this.checkCooldown > 0) {
            this.checkCooldown--;
            return false;
        }
        this.checkCooldown = 20 + this.fish.getRandom().nextInt(20);

        // УБРАЛИ hasData! Просто берем данные. Если их нет, выдастся дефолт (не влюблена)
        FishLoveData data = this.fish.getData(ModAttachments.FISH_LOVE.get());

        if (data.isInLove() || data.hasCooldown()) return false;

        this.targetPos = findFood(this.fish.level(), this.fish.blockPosition(), 6);
        return this.targetPos != null;
    }

    @Override
    public void start() {
        this.fish.getNavigation().moveTo(this.targetPos.getX() + 0.5, this.targetPos.getY() + 0.5, this.targetPos.getZ() + 0.5, 1.2D);
    }

    @Override
    public void stop() {
        this.targetPos = null;
        this.fish.getNavigation().stop();
    }

    @Override
    public boolean canContinueToUse() {
        return this.targetPos != null
                && this.fish.distanceToSqr(Vec3.atCenterOf(this.targetPos)) < 64.0
                && isEdible(this.fish.level().getBlockState(this.targetPos));
    }

    @Override
    public void tick() {
        if (this.targetPos == null) return;

        this.fish.getLookControl().setLookAt(this.targetPos.getX() + 0.5, this.targetPos.getY() + 0.5, this.targetPos.getZ() + 0.5, 30.0F, 30.0F);

        double dist = this.fish.distanceToSqr(Vec3.atCenterOf(this.targetPos));

        if (dist < 4.0) {
            eatAndEnterLoveMode();
            this.targetPos = null;
        } else if (this.fish.tickCount % 10 == 0) {
            this.fish.getNavigation().moveTo(this.targetPos.getX() + 0.5, this.targetPos.getY() + 0.5, this.targetPos.getZ() + 0.5, 1.2D);
        }
    }

    private BlockPos findFood(Level level, BlockPos start, int radius) {
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = start.offset(x, y, z);
                    if (isEdible(level.getBlockState(pos))) return pos;
                }
            }
        }
        return null;
    }

    private boolean isEdible(BlockState state) {
        return state.is(Blocks.SEAGRASS) || state.is(Blocks.TALL_SEAGRASS) || state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT);
    }

    private void eatAndEnterLoveMode() {
        Level level = this.fish.level();
        BlockState state = level.getBlockState(this.targetPos);

        if (state.is(Blocks.SEAGRASS)) {
            level.setBlock(this.targetPos, Blocks.WATER.defaultBlockState(), 3);
        } else if (state.is(Blocks.TALL_SEAGRASS)) {
            DoubleBlockHalf half = state.getValue(TallSeagrassBlock.HALF);
            BlockPos lowerPos = half == DoubleBlockHalf.LOWER ? this.targetPos : this.targetPos.below();
            BlockPos upperPos = half == DoubleBlockHalf.UPPER ? this.targetPos : this.targetPos.above();
            level.setBlock(lowerPos, Blocks.SEAGRASS.defaultBlockState(), 3);
            level.setBlock(upperPos, Blocks.WATER.defaultBlockState(), 3);
        } else if (state.is(Blocks.KELP) || state.is(Blocks.KELP_PLANT)) {
            BlockPos topPos = this.targetPos;
            while (level.getBlockState(topPos.above()).is(Blocks.KELP) || level.getBlockState(topPos.above()).is(Blocks.KELP_PLANT)) {
                topPos = topPos.above();
            }
            level.destroyBlock(topPos, false);
        }

        FishLoveData data = this.fish.getData(ModAttachments.FISH_LOVE.get());
        this.fish.setData(ModAttachments.FISH_LOVE.get(), data.startLove());

        if (level instanceof ServerLevel sl) {
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART,
                    this.fish.getX(), this.fish.getY() + this.fish.getBbHeight() * 0.8, this.fish.getZ(),
                    5, 0.3, 0.2, 0.3, 0.0);
        }
    }
}