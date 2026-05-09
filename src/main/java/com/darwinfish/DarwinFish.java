package com.darwinfish;

import com.darwinfish.client.ClientSetup;
import com.darwinfish.config.DarwinFishConfig;
import com.darwinfish.registry.*;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;

@Mod(DarwinFish.MOD_ID)
public class DarwinFish {

    public static final String MOD_ID = "darwinfish";

    public DarwinFish(IEventBus modEventBus, ModContainer modContainer) {
        ModBlocks.BLOCKS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modEventBus);
        ModEntities.ENTITIES.register(modEventBus);
        ModAttachments.ATTACHMENT_TYPES.register(modEventBus);
        ModRecipes.SERIALIZERS.register(modEventBus);

        // Регистрируем наш файл настроек
        modContainer.registerConfig(ModConfig.Type.COMMON, DarwinFishConfig.SPEC);

        if (FMLEnvironment.dist == Dist.CLIENT) {
            modEventBus.addListener(ClientSetup::registerRenderers);
        }
    }
}