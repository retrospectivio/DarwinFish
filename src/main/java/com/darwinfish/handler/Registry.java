package com.darwinfish.handler;

import com.darwinfish.DarwinFish;
import com.darwinfish.registry.ModEntities;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.minecraft.world.entity.SpawnPlacementTypes;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.entity.animal.WaterAnimal;

@EventBusSubscriber(modid = DarwinFish.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Registry {

    @SubscribeEvent
    public static void registerAttributes(EntityAttributeCreationEvent event) {
        // Базовые атрибуты для мирных мальков
        event.put(ModEntities.BABY_COD.get(), Cod.createAttributes().build());
        event.put(ModEntities.BABY_SALMON.get(), Salmon.createAttributes().build());
        event.put(ModEntities.BABY_PUFFERFISH.get(), Pufferfish.createAttributes().build());
        event.put(ModEntities.BABY_TROPICAL_FISH.get(), TropicalFish.createAttributes().build());

        // Атрибуты для ВЗРОСЛОЙ ЩУКИ (Урон: 1.5, Скорость: 2.5)
        event.put(ModEntities.PIKE.get(), Salmon.createAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 1.5D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 2.5D)
                .build());

        // Атрибуты для МАЛЬКА ЩУКИ (Урон: 1.0, Скорость: 2.0)
        event.put(ModEntities.BABY_PIKE.get(), Salmon.createAttributes()
                .add(net.minecraft.world.entity.ai.attributes.Attributes.ATTACK_DAMAGE, 1.0D)
                .add(net.minecraft.world.entity.ai.attributes.Attributes.MOVEMENT_SPEED, 2.0D)
                .build());
    }

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(
                ModEntities.PIKE.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WaterAnimal::checkSurfaceWaterAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
        event.register(
                ModEntities.BABY_PIKE.get(),
                SpawnPlacementTypes.IN_WATER,
                Heightmap.Types.MOTION_BLOCKING_NO_LEAVES,
                WaterAnimal::checkSurfaceWaterAnimalSpawnRules,
                RegisterSpawnPlacementsEvent.Operation.REPLACE
        );
    }
}