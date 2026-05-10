package com.darwinfish.config;

import net.neoforged.neoforge.common.ModConfigSpec;

public class DarwinFishConfig {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    // Базовые настройки поведения
    public static final ModConfigSpec.BooleanValue NATURAL_BREEDING;
    public static final ModConfigSpec.IntValue EGG_HATCH_TIME;
    public static final ModConfigSpec.IntValue BABY_GROWTH_TIME;
    public static final ModConfigSpec.IntValue BREEDING_COOLDOWN;

    // Настройки экосистемы (Перенаселение и голод)
    public static final ModConfigSpec.IntValue MAX_FISH_PER_CHUNK;
    public static final ModConfigSpec.IntValue OVERPOPULATION_TOLERANCE;

    static {
        BUILDER.push("Behavior");

        NATURAL_BREEDING = BUILDER
                .comment("Разрешить рыбам самостоятельно поедать водоросли для размножения (True - включено, False - выключено)")
                .define("naturalBreeding", false);

        EGG_HATCH_TIME = BUILDER
                .comment("Время созревания икры до вылупления мальков (в секундах). По умолчанию 1200 сек (1 игровой день)")
                .defineInRange("eggHatchTimeSeconds", 1200, 1, 86400);

        BABY_GROWTH_TIME = BUILDER
                .comment("Время взросления малька во взрослую особь (в секундах). По умолчанию 1200 сек (1 игровой день)")
                .defineInRange("babyGrowthTimeSeconds", 1200, 1, 86400);

        BREEDING_COOLDOWN = BUILDER
                .comment("Время отдыха (кулдаун) рыбы после размножения (в секундах). По умолчанию 300 сек (5 минут)")
                .defineInRange("breedingCooldownSeconds", 300, 1, 86400);

        MAX_FISH_PER_CHUNK = BUILDER
                .comment("Максимальное количество рыб в радиусе 16 блоков до начала перенаселения")
                .defineInRange("maxFishPerChunk", 15, 1, 100);

        OVERPOPULATION_TOLERANCE = BUILDER
                .comment("Время (в секундах), которое рыба терпит тесноту перед нападением на сородичей")
                .defineInRange("overpopulationTolerance", 60, 1, 3600);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}