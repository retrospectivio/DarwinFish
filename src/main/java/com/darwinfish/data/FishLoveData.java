package com.darwinfish.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Tracks love-mode and breeding cooldown state for a fish entity.
 * Stored as a NeoForge data attachment on the entity.
 *
 * Immutable record; create modified copies via the helper methods.
 */
public record FishLoveData(int loveTicks, int cooldownTicks) {

    /** Default state: not in love, no cooldown. */
    public static final FishLoveData DEFAULT = new FishLoveData(0, 0);

    public static final Codec<FishLoveData> CODEC = RecordCodecBuilder.create(instance ->
        instance.group(
            Codec.INT.fieldOf("love_ticks").forGetter(FishLoveData::loveTicks),
            Codec.INT.fieldOf("cooldown_ticks").forGetter(FishLoveData::cooldownTicks)
        ).apply(instance, FishLoveData::new)
    );

    /** 30 seconds of love mode. */
    private static final int LOVE_DURATION = 600;
    /** 1 in-game day cooldown before breeding again. */
    private static final int BREED_COOLDOWN = 24000;

    public boolean isInLove() {
        return loveTicks > 0;
    }

    public boolean hasCooldown() {
        return cooldownTicks > 0;
    }

    /** Returns a copy with both counters decremented by 1 (min 0). */
    public FishLoveData tick() {
        return new FishLoveData(Math.max(0, loveTicks - 1), Math.max(0, cooldownTicks - 1));
    }

    /** Starts love mode. Does not reset cooldown. */
    public FishLoveData startLove() {
        return new FishLoveData(LOVE_DURATION, cooldownTicks);
    }

    /** Ends love mode and starts breeding cooldown. */
    // Вместо старого метода без аргументов:
    /** Ends love mode and starts breeding cooldown. */
    public FishLoveData startCooldown(int ticks) {
        // Устанавливаем таймер любви на 0 (выключаем режим), а кулдаун на заданное время
        return new FishLoveData(0, ticks);
    }
}
