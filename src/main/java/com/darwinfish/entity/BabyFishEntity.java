package com.darwinfish.entity;

/**
 * Marker interface for baby fish entities.
 * All baby fish extend their vanilla parent class directly (single-inheritance
 * constraint), so shared constants live here.
 */
public interface BabyFishEntity {
    /** 2 in-game days = 48 000 ticks. */
    int GROWTH_TICKS = 24_000;
}
