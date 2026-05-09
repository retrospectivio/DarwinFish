package com.darwinfish.blockentity;

import com.darwinfish.FishType;
import com.darwinfish.block.FishEggBlock;
import com.darwinfish.entity.*;
import com.darwinfish.registry.ModBlockEntities;
import com.darwinfish.registry.ModEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class FishEggBlockEntity extends BlockEntity {

    private long hatchTime = Long.MAX_VALUE;
    private int eggCount = 1;

    private int baseColorId    = -1;
    private int patternColorId = -1;

    private int p1BaseColorId = -1;
    private int p1PatternColorId = -1;
    private String p1VariantName = "";

    private int p2BaseColorId = -1;
    private int p2PatternColorId = -1;
    private String p2VariantName = "";

    private boolean isPersistentLine = false;

    public void setPersistentLine(boolean persistent) {
        this.isPersistentLine = persistent;
        this.setChanged();
    }

    public FishEggBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FISH_EGG.get(), pos, state);
    }

    public void setHatchTime(long time)   { this.hatchTime = time; }
    public void setEggCount(int count)    { this.eggCount = count; }

    public void setBaseColor(DyeColor color) { this.baseColorId = color.getId(); }
    public void setPatternColor(DyeColor color) { this.patternColorId = color.getId(); }

    public void setParent1Genetics(TropicalFish tf) {
        this.p1BaseColorId = tf.getBaseColor().getId();
        this.p1PatternColorId = tf.getPatternColor().getId();
        this.p1VariantName = tf.getVariant().name();
    }

    public void setParent2Genetics(TropicalFish tf) {
        this.p2BaseColorId = tf.getBaseColor().getId();
        this.p2PatternColorId = tf.getPatternColor().getId();
        this.p2VariantName = tf.getVariant().name();
    }

    public DyeColor getBaseColor() { return baseColorId >= 0 ? DyeColor.byId(baseColorId) : DyeColor.WHITE; }
    public DyeColor getPatternColor() { return patternColorId >= 0 ? DyeColor.byId(patternColorId) : DyeColor.ORANGE; }
    public boolean hasColorData() { return baseColorId >= 0; }

    public static void serverTick(Level level, BlockPos pos, BlockState state, FishEggBlockEntity be) {
        if (level.getGameTime() >= be.hatchTime) {
            be.hatch(level, pos, state);
        }
    }

    private void hatch(Level level, BlockPos pos, BlockState state) {
        if (!(state.getBlock() instanceof FishEggBlock eggBlock)) return;

        level.removeBlock(pos, false);
        FishType type = eggBlock.getFishType();

        // Получаем взвешенное количество мальков вместо старого eggCount
        int spawnCount = getWeightedSpawnCount(level.random);

        for (int i = 0; i < spawnCount; i++) {
            spawnFry(level, pos, type);
        }
    }

    /**
     * Возвращает от 1 до 5 мальков с заданными вероятностями.
     */
    private int getWeightedSpawnCount(RandomSource random) {
        int chance = random.nextInt(100);
        if (chance < 40) return 2;      // 40%
        if (chance < 70) return 3;      // 30%
        if (chance < 85) return 1;      // 15%
        if (chance < 95) return 4;      // 10%
        return 5;                       // 5%
    }

    private void spawnFry(Level level, BlockPos pos, FishType type) {
        double ox = (level.random.nextDouble() - 0.5) * 0.5;
        double oz = (level.random.nextDouble() - 0.5) * 0.5;
        Vec3 spawnPos = Vec3.atCenterOf(pos).add(ox, 0.1, oz);

        switch (type) {
            case COD -> spawnGenericFry(new BabyCodEntity(ModEntities.BABY_COD.get(), level), level, spawnPos);
            case SALMON -> spawnGenericFry(new BabySalmonEntity(ModEntities.BABY_SALMON.get(), level), level, spawnPos);
            case PUFFERFISH -> spawnGenericFry(new BabyPufferfishEntity(ModEntities.BABY_PUFFERFISH.get(), level), level, spawnPos);
            case TROPICAL_FISH -> spawnTropicalFry(level, spawnPos);
            case PIKE -> spawnGenericFry(new BabyPikeEntity(ModEntities.BABY_PIKE.get(), level), level, spawnPos);
        }
    }

    private void spawnGenericFry(Entity baby, Level level, Vec3 pos) {
        baby.moveTo(pos.x, pos.y, pos.z, level.random.nextFloat() * 360f, 0f);

        // Теперь малек становится бессмертным ТОЛЬКО если икра была "домашней"
        if (this.isPersistentLine && baby instanceof net.minecraft.world.entity.Mob mob) {
            mob.setPersistenceRequired();
        }

        level.addFreshEntity(baby);
    }
    private void spawnTropicalFry(Level level, Vec3 pos) {
        BabyTropicalFishEntity baby = new BabyTropicalFishEntity(ModEntities.BABY_TROPICAL_FISH.get(), level);

        if (hasColorData() && !p1VariantName.isEmpty() && !p2VariantName.isEmpty()) {
            RandomSource rand = level.random;

            DyeColor finalBase = switch (rand.nextInt(3)) {
                case 0 -> DyeColor.byId(p1BaseColorId);
                case 1 -> DyeColor.byId(p2BaseColorId);
                default -> getBaseColor();
            };

            DyeColor finalPatternColor = switch (rand.nextInt(3)) {
                case 0 -> DyeColor.byId(p1PatternColorId);
                case 1 -> DyeColor.byId(p2PatternColorId);
                default -> getPatternColor();
            };

            TropicalFish.Pattern p1Pattern = TropicalFish.Pattern.valueOf(p1VariantName);
            TropicalFish.Pattern p2Pattern = TropicalFish.Pattern.valueOf(p2VariantName);

            TropicalFish.Base p1Size = p1Pattern.base();
            TropicalFish.Base p2Size = p2Pattern.base();
            TropicalFish.Base frySize = rand.nextBoolean() ? p1Size : p2Size;

            TropicalFish.Pattern fryPattern;
            if (rand.nextFloat() < 0.15f) {
                fryPattern = getRandomPatternOfSize(frySize, rand);
            } else {
                TropicalFish.Pattern validP1 = (p1Pattern.base() == frySize) ? p1Pattern : null;
                TropicalFish.Pattern validP2 = (p2Pattern.base() == frySize) ? p2Pattern : null;

                if (validP1 != null && validP2 != null) {
                    fryPattern = rand.nextBoolean() ? validP1 : validP2;
                } else {
                    fryPattern = validP1 != null ? validP1 : validP2;
                }
            }

            baby.initColors(finalBase, finalPatternColor);
            baby.setVariant(fryPattern);
        }

        spawnGenericFry(baby, level, pos);
    }

    private TropicalFish.Pattern getRandomPatternOfSize(TropicalFish.Base size, RandomSource random) {
        List<TropicalFish.Pattern> validPatterns = Arrays.stream(TropicalFish.Pattern.values())
                .filter(p -> p.base() == size)
                .toList();
        return validPatterns.get(random.nextInt(validPatterns.size()));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putLong("HatchTime", hatchTime);
        tag.putInt("EggCount", eggCount);
        tag.putInt("BaseColorId", baseColorId);
        tag.putInt("PatternColorId", patternColorId);

        tag.putInt("P1BaseId", p1BaseColorId);
        tag.putInt("P1PatId", p1PatternColorId);
        tag.putString("P1Var", p1VariantName);

        tag.putInt("P2BaseId", p2BaseColorId);
        tag.putInt("P2PatId", p2PatternColorId);
        tag.putString("P2Var", p2VariantName);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        hatchTime      = tag.getLong("HatchTime");
        eggCount       = tag.getInt("EggCount");
        baseColorId    = tag.contains("BaseColorId") ? tag.getInt("BaseColorId") : -1;
        patternColorId = tag.contains("PatternColorId") ? tag.getInt("PatternColorId") : -1;

        p1BaseColorId    = tag.contains("P1BaseId") ? tag.getInt("P1BaseId") : -1;
        p1PatternColorId = tag.contains("P1PatId") ? tag.getInt("P1PatId") : -1;
        p1VariantName    = tag.getString("P1Var");

        p2BaseColorId    = tag.contains("P2BaseId") ? tag.getInt("P2BaseId") : -1;
        p2PatternColorId = tag.contains("P2PatId") ? tag.getInt("P2PatId") : -1;
        p2VariantName    = tag.getString("P2Var");
    }

    @Nullable
    @Override
    public net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket getUpdatePacket() {
        return net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        this.saveAdditional(tag, registries);
        return tag;
    }
}