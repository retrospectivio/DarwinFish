package com.darwinfish.registry;

import com.darwinfish.DarwinFish;
import com.darwinfish.blockentity.FishEggBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(Registries.BLOCK_ENTITY_TYPE, DarwinFish.MOD_ID);

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<FishEggBlockEntity>> FISH_EGG =
            BLOCK_ENTITIES.register("fish_egg", () ->
                    BlockEntityType.Builder.of(
                            FishEggBlockEntity::new,
                            ModBlocks.COD_EGGS.get(),
                            ModBlocks.SALMON_EGGS.get(),
                            ModBlocks.PUFFERFISH_EGGS.get(),
                            ModBlocks.TROPICAL_FISH_EGGS.get(),
                            ModBlocks.PIKE_EGGS.get()
                    ).build(null)
            );
}