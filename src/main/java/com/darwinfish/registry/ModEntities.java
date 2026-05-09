package com.darwinfish.registry;

import com.darwinfish.DarwinFish;
import com.darwinfish.entity.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModEntities {

    public static final DeferredRegister<EntityType<?>> ENTITIES =
            DeferredRegister.create(Registries.ENTITY_TYPE, DarwinFish.MOD_ID);

    public static final DeferredHolder<EntityType<?>, EntityType<BabyCodEntity>> BABY_COD =
            ENTITIES.register("baby_cod", () ->
                    EntityType.Builder.<BabyCodEntity>of(BabyCodEntity::new, MobCategory.WATER_AMBIENT)
                            .sized(0.35f, 0.2f)
                            .clientTrackingRange(4)
                            .build("baby_cod")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<BabySalmonEntity>> BABY_SALMON =
            ENTITIES.register("baby_salmon", () ->
                    EntityType.Builder.<BabySalmonEntity>of(BabySalmonEntity::new, MobCategory.WATER_AMBIENT)
                            .sized(0.35f, 0.2f)
                            .clientTrackingRange(4)
                            .build("baby_salmon")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<BabyPufferfishEntity>> BABY_PUFFERFISH =
            ENTITIES.register("baby_pufferfish", () ->
                    EntityType.Builder.<BabyPufferfishEntity>of(BabyPufferfishEntity::new, MobCategory.WATER_AMBIENT)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .build("baby_pufferfish")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<BabyTropicalFishEntity>> BABY_TROPICAL_FISH =
            ENTITIES.register("baby_tropical_fish", () ->
                    EntityType.Builder.<BabyTropicalFishEntity>of(BabyTropicalFishEntity::new, MobCategory.WATER_AMBIENT)
                            .sized(0.3f, 0.3f)
                            .clientTrackingRange(4)
                            .build("baby_tropical_fish")
            );

    public static final DeferredHolder<EntityType<?>, EntityType<PikeEntity>> PIKE =
            ENTITIES.register("pike", () ->
                    EntityType.Builder.<PikeEntity>of(PikeEntity::new, MobCategory.WATER_CREATURE)
                            .sized(0.7f, 0.4f)
                            .clientTrackingRange(4)
                            .build("pike")
            );

    public static final net.neoforged.neoforge.registries.DeferredHolder<EntityType<?>, EntityType<BabyPikeEntity>> BABY_PIKE =
            ENTITIES.register("baby_pike", () ->
                    EntityType.Builder.<BabyPikeEntity>of(BabyPikeEntity::new, MobCategory.WATER_AMBIENT)
                            .sized(0.35f, 0.2f)
                            .clientTrackingRange(4)
                            .build("baby_pike")
            );
}