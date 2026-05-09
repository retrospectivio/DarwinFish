package com.darwinfish.handler;

import com.darwinfish.DarwinFish;
import com.darwinfish.registry.ModItems;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

@EventBusSubscriber(modid = DarwinFish.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class CreativeTabHandler {

    @SubscribeEvent
    public static void buildContents(BuildCreativeModeTabContentsEvent event) {

        // --- ВКЛАДКА: ЯЙЦА ПРИЗЫВА ---
        if (event.getTabKey() == CreativeModeTabs.SPAWN_EGGS) {
            // Добавляем яйцо призыва щуки
            event.accept(ModItems.PIKE_SPAWN_EGG.get());
        }

        // --- ВКЛАДКА: ЕДА И НАПИТКИ ---
        if (event.getTabKey() == CreativeModeTabs.FOOD_AND_DRINKS) {

            // 1. Икра выстраивается перед бутылочкой мёда (и после молока)
            event.insertBefore(
                    new ItemStack(Items.HONEY_BOTTLE),
                    new ItemStack(ModItems.BOTTLED_COD_EGGS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertBefore(
                    new ItemStack(Items.HONEY_BOTTLE),
                    new ItemStack(ModItems.BOTTLED_SALMON_EGGS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertBefore(
                    new ItemStack(Items.HONEY_BOTTLE),
                    new ItemStack(ModItems.BOTTLED_PUFFERFISH_EGGS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertBefore(
                    new ItemStack(Items.HONEY_BOTTLE),
                    new ItemStack(ModItems.WASHED_PUFFERFISH_EGGS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertBefore(
                    new ItemStack(Items.HONEY_BOTTLE),
                    new ItemStack(ModItems.BOTTLED_TROPICAL_FISH_EGGS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            // Добавляем икру щуки сюда же
            event.insertBefore(
                    new ItemStack(Items.HONEY_BOTTLE),
                    new ItemStack(ModItems.BOTTLED_PIKE_EGGS.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );

            // 2. Бутерброды выстраиваются цепочкой сразу после ванильного хлеба
            event.insertAfter(
                    new ItemStack(Items.BREAD),
                    new ItemStack(ModItems.BREAD_WITH_COD_CAVIAR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                    new ItemStack(ModItems.BREAD_WITH_COD_CAVIAR.get()),
                    new ItemStack(ModItems.BREAD_WITH_SALMON_CAVIAR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                    new ItemStack(ModItems.BREAD_WITH_SALMON_CAVIAR.get()),
                    new ItemStack(ModItems.BREAD_WITH_TROPICAL_CAVIAR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertAfter(
                    new ItemStack(ModItems.BREAD_WITH_TROPICAL_CAVIAR.get()),
                    new ItemStack(ModItems.BREAD_WITH_PUFFERFISH_CAVIAR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            // Добавляем бутерброд со щукой в конец цепочки бутербродов
            event.insertAfter(
                    new ItemStack(ModItems.BREAD_WITH_PUFFERFISH_CAVIAR.get()),
                    new ItemStack(ModItems.BREAD_WITH_PIKE_CAVIAR.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );

            // 3. Жареная тропическая рыба ставится перед ванильной сырой
            event.insertBefore(
                    new ItemStack(Items.TROPICAL_FISH),
                    new ItemStack(ModItems.ROASTED_TROPICAL_FISH.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );

            // 4. Очищенный и жареный иглобрюхи ставятся перед ванильным
            event.insertBefore(
                    new ItemStack(Items.PUFFERFISH),
                    new ItemStack(ModItems.CLEANED_PUFFERFISH.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
            event.insertBefore(
                    new ItemStack(Items.PUFFERFISH),
                    new ItemStack(ModItems.ROASTED_PUFFERFISH.get()),
                    CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS
            );
        }
    }
}