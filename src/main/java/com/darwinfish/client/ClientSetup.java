package com.darwinfish.client;

import com.darwinfish.DarwinFish;
import com.darwinfish.client.renderer.*;
import com.darwinfish.registry.ModBlockEntities;
import com.darwinfish.registry.ModEntities;
import com.darwinfish.registry.ModItems;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = DarwinFish.MOD_ID, value = Dist.CLIENT)
public class ClientSetup {

    @SubscribeEvent
    public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
        // Рендеры всех мальков
        event.registerEntityRenderer(ModEntities.BABY_COD.get(),           BabyCodRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_SALMON.get(),        BabySalmonRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_PUFFERFISH.get(),    BabyPufferfishRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_TROPICAL_FISH.get(), BabyTropicalFishRenderer::new);
        event.registerEntityRenderer(ModEntities.BABY_PIKE.get(),          BabyPikeRenderer::new);

        // Рендер взрослой щуки (временно использует стандартный класс лосося)
        event.registerEntityRenderer(ModEntities.PIKE.get(), PikeRenderer::new);

        // Рендер сущностей блоков
        event.registerBlockEntityRenderer(ModBlockEntities.FISH_EGG.get(), FishEggBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            // Слой 0 (бутылочка или ломоть хлеба) возвращаем без покраски
            if (tintIndex == 0) {
                return -1;
            }

            // Дефолтный бежевый цвет (используется, если предмет взят из креатива)
            int defaultBeige = 0xFFE5D1A5;

            CustomData customData = stack.get(DataComponents.CUSTOM_DATA);
            if (customData != null && customData.contains("BaseColorId")) {
                int baseColorId = customData.copyTag().getInt("BaseColorId");
                int geneticTint = DyeColor.byId(baseColorId).getTextureDiffuseColor();

                float genR = ((geneticTint >> 16) & 0xFF) / 255f;
                float genG = ((geneticTint >> 8)  & 0xFF) / 255f;
                float genB = ( geneticTint        & 0xFF) / 255f;

                float beigeR = 0.90f;
                float beigeG = 0.82f;
                float beigeB = 0.65f;
                float geneticInfluence = 0.25f;

                float r = (genR * geneticInfluence) + (beigeR * (1f - geneticInfluence));
                float g = (genG * geneticInfluence) + (beigeG * (1f - geneticInfluence));
                float b = (genB * geneticInfluence) + (beigeB * (1f - geneticInfluence));

                return 0xFF000000 | ((int)(r * 255) << 16) | ((int)(g * 255) << 8) | (int)(b * 255);
            }

            // Если генетических тегов нет, применяем бежевый цвет
            return defaultBeige;

            // ВАЖНО: Список всех предметов, которые должны проходить через этот код:
        }, ModItems.BOTTLED_TROPICAL_FISH_EGGS.get(), ModItems.BREAD_WITH_TROPICAL_CAVIAR.get());
    }
}