package com.darwinfish;

public enum FishType {
    COD,
    SALMON,
    PUFFERFISH,
    TROPICAL_FISH,
    PIKE; // Добавили щуку

    public String getId() {
        return name().toLowerCase();
    }
}