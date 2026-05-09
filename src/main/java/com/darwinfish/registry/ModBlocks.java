package com.darwinfish.registry;

import com.darwinfish.DarwinFish;
import com.darwinfish.FishType;
import com.darwinfish.block.FishEggBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModBlocks {

    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(DarwinFish.MOD_ID);

    public static final DeferredBlock<FishEggBlock> COD_EGGS = BLOCKS.register(
            "cod_eggs", () -> new FishEggBlock(FishType.COD, BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.SLIME_BLOCK).noOcclusion().noCollission())
    );

    public static final DeferredBlock<FishEggBlock> SALMON_EGGS = BLOCKS.register(
            "salmon_eggs", () -> new FishEggBlock(FishType.SALMON, BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.SLIME_BLOCK).noOcclusion().noCollission())
    );

    public static final DeferredBlock<FishEggBlock> PUFFERFISH_EGGS = BLOCKS.register(
            "pufferfish_eggs", () -> new FishEggBlock(FishType.PUFFERFISH, BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.SLIME_BLOCK).noOcclusion().noCollission())
    );

    public static final DeferredBlock<FishEggBlock> TROPICAL_FISH_EGGS = BLOCKS.register(
            "tropical_fish_eggs", () -> new FishEggBlock(FishType.TROPICAL_FISH, BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.SLIME_BLOCK).noOcclusion().noCollission())
    );

    // НОВОЕ: Регистрируем икру щуки
    public static final DeferredBlock<FishEggBlock> PIKE_EGGS = BLOCKS.register(
            "pike_eggs", () -> new FishEggBlock(FishType.PIKE, BlockBehaviour.Properties.of().strength(0.5f).sound(SoundType.SLIME_BLOCK).noOcclusion().noCollission())
    );

    /** Returns the egg block for the given fish type. */
    public static FishEggBlock getFor(FishType type) {
        return switch (type) {
            case COD           -> COD_EGGS.get();
            case SALMON        -> SALMON_EGGS.get();
            case PUFFERFISH    -> PUFFERFISH_EGGS.get();
            case TROPICAL_FISH -> TROPICAL_FISH_EGGS.get();
            case PIKE          -> PIKE_EGGS.get(); // Добавили возврат икры щуки
        };
    }
}