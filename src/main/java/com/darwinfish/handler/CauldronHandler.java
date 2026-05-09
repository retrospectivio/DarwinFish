package com.darwinfish.handler;

import com.darwinfish.DarwinFish;
import com.darwinfish.registry.ModItems;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.particles.ColorParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;

@EventBusSubscriber(modid = DarwinFish.MOD_ID)
public class CauldronHandler {

    @SubscribeEvent
    public static void onSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {

            // 1. Обработчик для сырой рыбы (иглобрюха)
            CauldronInteraction.WATER.map().put(Items.PUFFERFISH, (state, level, pos, player, hand, stack) -> {
                if (!level.isClientSide) {
                    int waterLevel = state.getValue(LayeredCauldronBlock.LEVEL);

                    float poisonChance = switch (waterLevel) {
                        case 1 -> 0.25f;
                        case 2 -> 0.12f;
                        case 3 -> 0.04f;
                        default -> 0.0f;
                    };

                    if (level.random.nextFloat() < poisonChance) {
                        ((ServerLevel) level).sendParticles(
                                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0x4E9331),
                                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                                15, 0.2, 0.2, 0.2, 1.0
                        );
                        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    ItemStack cleanedFish = new ItemStack(ModItems.CLEANED_PUFFERFISH.get());
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }

                    if (stack.isEmpty()) {
                        player.setItemInHand(hand, cleanedFish);
                    } else if (!player.getInventory().add(cleanedFish)) {
                        player.drop(cleanedFish, false);
                    }

                    LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            });

            // 2. Обработчик для сырой икры иглобрюха
            CauldronInteraction.WATER.map().put(ModItems.BOTTLED_PUFFERFISH_EGGS.get(), (state, level, pos, player, hand, stack) -> {
                if (!level.isClientSide) {
                    int waterLevel = state.getValue(LayeredCauldronBlock.LEVEL);

                    float poisonChance = switch (waterLevel) {
                        case 1 -> 0.25f;
                        case 2 -> 0.12f;
                        case 3 -> 0.04f;
                        default -> 0.0f;
                    };

                    if (level.random.nextFloat() < poisonChance) {
                        ((ServerLevel) level).sendParticles(
                                ColorParticleOption.create(ParticleTypes.ENTITY_EFFECT, 0x4E9331),
                                pos.getX() + 0.5, pos.getY() + 0.8, pos.getZ() + 0.5,
                                15, 0.2, 0.2, 0.2, 1.0
                        );
                        level.playSound(null, pos, SoundEvents.BREWING_STAND_BREW, SoundSource.BLOCKS, 1.0F, 1.0F);
                    }

                    ItemStack cleanedEggs = new ItemStack(ModItems.WASHED_PUFFERFISH_EGGS.get());
                    if (!player.isCreative()) {
                        stack.shrink(1);
                    }

                    if (stack.isEmpty()) {
                        player.setItemInHand(hand, cleanedEggs);
                    } else if (!player.getInventory().add(cleanedEggs)) {
                        player.drop(cleanedEggs, false);
                    }

                    LayeredCauldronBlock.lowerFillLevel(state, level, pos);
                    level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
                }
                return ItemInteractionResult.sidedSuccess(level.isClientSide);
            });

        });
    }
}