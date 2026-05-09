package com.darwinfish.entity;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Baby tropical fish.
 *
 * <p>Extends {@link TropicalFish} directly so that all vanilla color accessors
 * ({@link #getBaseColor()}, {@link #getPatternColor()}, {@link #getVariant()})
 * and the {@code TropicalFishPatternLayer} work without special handling.
 *
 * <p>{@code TropicalFish.setPackedVariant(int)} is made accessible via an
 * Access Transformer ({@code META-INF/accesstransformer.cfg}) so we can apply
 * mixed parent colors safely — without the side-effects of an NBT round-trip
 * that would reset HP, effects, and other entity state.
 */
public class BabyTropicalFishEntity extends TropicalFish implements BabyFishEntity {

    private int ageTicks;

    // Stored so we can persist and re-apply colors and patterns after chunk reload
    private DyeColor mixedBase = DyeColor.ORANGE;
    private DyeColor mixedPatternColor = DyeColor.WHITE;
    private Pattern mixedPatternType = Pattern.KOB; // Форм-фактор и узор по умолчанию

    public BabyTropicalFishEntity(EntityType<? extends BabyTropicalFishEntity> type, Level level) {
        super(type, level);
        this.ageTicks = com.darwinfish.config.DarwinFishConfig.BABY_GROWTH_TIME.get() * 20;
    }
    // -------------------------------------------------------------------------
    // Color & Pattern initialisation — called by FishEggBlockEntity on hatch
    // -------------------------------------------------------------------------

    /**
     * Applies mixed parent colors. Call this once after construction and after
     * loading from NBT.
     */
    public void initColors(DyeColor baseColor, DyeColor patternColor) {
        this.mixedBase = baseColor;
        this.mixedPatternColor = patternColor;
        applyMixedColors();
    }

    /**
     * Applies the specific pattern inherited from genetics.
     */
    public void setVariant(Pattern pattern) {
        this.mixedPatternType = pattern;
        applyMixedColors();
    }

    /**
     * Encodes the stored mixed colors and pattern into the vanilla packed-variant integer
     * and pushes it directly via the (AT-unlocked) private setter.
     * No NBT round-trip — no side-effects.
     */
    private void applyMixedColors() {
        int packed = (mixedPatternType.getPackedId() & 0xFFFF)
                | ((mixedBase.getId() & 0xFF) << 16)
                | ((mixedPatternColor.getId() & 0xFF) << 24);

        // setPackedVariant made public via AT in META-INF/accesstransformer.cfg
        setPackedVariant(packed);
    }

    // -------------------------------------------------------------------------
    // Growth tick
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
        TropicalFish adult = EntityType.TROPICAL_FISH.create(sl);
        if (adult != null) {
            adult.moveTo(getX(), getY(), getZ(), getYRot(), getXRot());
            adult.setFromBucket(false);
            adult.setHealth(this.getHealth());

            // Transfer EXACT genetics to the adult using the same AT-unlocked setter
            int packed = (mixedPatternType.getPackedId() & 0xFFFF)
                    | ((mixedBase.getId() & 0xFF) << 16)
                    | ((mixedPatternColor.getId() & 0xFF) << 24);
            adult.setPackedVariant(packed);

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
    // NBT persistence
    // -------------------------------------------------------------------------

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putInt("AgeTicks", ageTicks);
        tag.putInt("MixedBase", mixedBase.getId());
        tag.putInt("MixedPatternColor", mixedPatternColor.getId());
        tag.putString("MixedPatternType", mixedPatternType.name()); // Сохраняем ген узора
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag); // reads vanilla fields (health, etc.) correctly
        if (tag.contains("AgeTicks")) {
            ageTicks = tag.getInt("AgeTicks");
        }
        if (tag.contains("MixedBase")) {
            mixedBase = DyeColor.byId(tag.getInt("MixedBase"));

            // Защита совместимости для старых сохранений
            if (tag.contains("MixedPattern")) {
                mixedPatternColor = DyeColor.byId(tag.getInt("MixedPattern"));
            } else if (tag.contains("MixedPatternColor")) {
                mixedPatternColor = DyeColor.byId(tag.getInt("MixedPatternColor"));
            }
        }
        if (tag.contains("MixedPatternType")) {
            try {
                mixedPatternType = Pattern.valueOf(tag.getString("MixedPatternType"));
            } catch (IllegalArgumentException e) {
                mixedPatternType = Pattern.KOB; // Фолбэк на случай ошибки чтения
            }
        }
        applyMixedColors(); // re-apply: no side-effects on entity state
    }

    // -------------------------------------------------------------------------
    // Sounds
    // -------------------------------------------------------------------------

    @Override protected SoundEvent getAmbientSound() { return SoundEvents.TROPICAL_FISH_AMBIENT; }
    @Override protected SoundEvent getDeathSound()   { return SoundEvents.TROPICAL_FISH_DEATH;   }
    @Override protected SoundEvent getHurtSound(DamageSource src) { return SoundEvents.TROPICAL_FISH_HURT; }
    @Override protected SoundEvent getFlopSound()    { return SoundEvents.TROPICAL_FISH_FLOP;   }
}