package com.darwinfish.registry;

import com.darwinfish.DarwinFish;
import com.darwinfish.FishType;
import com.darwinfish.item.BottledFishEggItem;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {

    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(DarwinFish.MOD_ID);

    // --- БАНОЧКИ С ИКРОЙ (Добавлено возвращение бутылочки при крафте) ---
    public static final DeferredHolder<Item, BottledFishEggItem> BOTTLED_COD_EGGS =
            ITEMS.register("bottled_cod_eggs",
                    () -> new BottledFishEggItem(FishType.COD, new Item.Properties()
                            .stacksTo(16)
                            .craftRemainder(Items.GLASS_BOTTLE)
                            .food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build()))
            );

    public static final DeferredHolder<Item, BottledFishEggItem> BOTTLED_SALMON_EGGS =
            ITEMS.register("bottled_salmon_eggs",
                    () -> new BottledFishEggItem(FishType.SALMON, new Item.Properties()
                            .stacksTo(16)
                            .craftRemainder(Items.GLASS_BOTTLE)
                            .food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build()))
            );

    public static final DeferredHolder<Item, BottledFishEggItem> BOTTLED_PUFFERFISH_EGGS =
            ITEMS.register("bottled_pufferfish_eggs",
                    () -> new BottledFishEggItem(FishType.PUFFERFISH, new Item.Properties()
                            .stacksTo(16)
                            .craftRemainder(Items.GLASS_BOTTLE)
                            .food(new FoodProperties.Builder()
                                    .nutrition(2)
                                    .saturationModifier(0.1f)
                                    .effect(new MobEffectInstance(MobEffects.POISON, 200, 1), 1.0f)
                                    .build()))
            );

    public static final DeferredHolder<Item, BottledFishEggItem> BOTTLED_TROPICAL_FISH_EGGS =
            ITEMS.register("bottled_tropical_fish_eggs",
                    () -> new BottledFishEggItem(FishType.TROPICAL_FISH, new Item.Properties()
                            .stacksTo(16)
                            .craftRemainder(Items.GLASS_BOTTLE)
                            .food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build()))
            );

    // --- НОВОЕ: ПРОМЫТАЯ ИКРА ИГЛОБРЮХА ---
    public static final DeferredItem<Item> WASHED_PUFFERFISH_EGGS = ITEMS.register("washed_pufferfish_eggs",
            () -> new Item(new Item.Properties()
                    .stacksTo(16)
                    .craftRemainder(Items.GLASS_BOTTLE)
                    .food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build())) {
                @Override
                public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
                    ItemStack result = super.finishUsingItem(stack, level, entity);
                    if (entity instanceof Player player && player.getAbilities().instabuild) return result;
                    if (result.isEmpty()) return new ItemStack(Items.GLASS_BOTTLE);
                    if (entity instanceof Player player) {
                        ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                        if (!player.getInventory().add(bottle)) player.drop(bottle, false);
                    }
                    return result;
                }
            });

    // --- НОВОЕ: БУТЕРБРОДЫ С ИКРОЙ ---
    // Питательность: Хлеб (5) + Икра (2) = 7. Насыщение: 0.6
    public static final DeferredItem<Item> BREAD_WITH_COD_CAVIAR = ITEMS.register("bread_with_cod_caviar",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> BREAD_WITH_SALMON_CAVIAR = ITEMS.register("bread_with_salmon_caviar",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> BREAD_WITH_TROPICAL_CAVIAR = ITEMS.register("bread_with_tropical_caviar",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> BREAD_WITH_PUFFERFISH_CAVIAR = ITEMS.register("bread_with_pufferfish_caviar",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(7).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> TOXIC_BREAD_WITH_PUFFERFISH_CAVIAR = ITEMS.register("toxic_bread_with_pufferfish_caviar",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder()
                    .nutrition(7)
                    .saturationModifier(0.6f)
                    .effect(new MobEffectInstance(MobEffects.POISON, 200, 1), 1.0f)
                    .build())));

    public static BottledFishEggItem getBottledEgg(FishType type) {
        return switch (type) {
            case COD           -> BOTTLED_COD_EGGS.get();
            case SALMON        -> BOTTLED_SALMON_EGGS.get();
            case PUFFERFISH    -> BOTTLED_PUFFERFISH_EGGS.get();
            case TROPICAL_FISH -> BOTTLED_TROPICAL_FISH_EGGS.get();
            case PIKE          -> BOTTLED_PIKE_EGGS.get();
        };
    }

    // Регистрация жареной рыбы (осталась без изменений)
    public static final DeferredItem<Item> ROASTED_TROPICAL_FISH = ITEMS.register("roasted_tropical_fish",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f).build())));

    public static final DeferredItem<Item> CLEANED_PUFFERFISH = ITEMS.register("cleaned_pufferfish",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.05f).build())));

    public static final DeferredItem<Item> TOXIC_ROASTED_PUFFERFISH = ITEMS.register("toxic_roasted_pufferfish",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationModifier(0.5f)
                    .effect(new MobEffectInstance(MobEffects.POISON, 600, 0), 0.7f)
                    .effect(new MobEffectInstance(MobEffects.CONFUSION, 200, 0), 0.7f)
                    .effect(new MobEffectInstance(MobEffects.HUNGER, 300, 1), 0.7f).build())));

    public static final java.util.function.Supplier<net.minecraft.world.item.Item> PIKE_SPAWN_EGG =
            ITEMS.register("pike_spawn_egg",
                    () -> new net.neoforged.neoforge.common.DeferredSpawnEggItem(
                            com.darwinfish.registry.ModEntities.PIKE,
                            0x0F545C,
                            0x802120,
                            new net.minecraft.world.item.Item.Properties()
                    ));

    public static final DeferredHolder<Item, BottledFishEggItem> BOTTLED_PIKE_EGGS =
            ITEMS.register("bottled_pike_eggs",
                    () -> new BottledFishEggItem(FishType.PIKE, new Item.Properties()
                            .stacksTo(16)
                            .craftRemainder(Items.GLASS_BOTTLE)
                            .food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build()))
            );

    public static final net.neoforged.neoforge.registries.DeferredHolder<net.minecraft.world.item.Item, net.minecraft.world.item.Item> BREAD_WITH_PIKE_CAVIAR =
            ITEMS.register("bread_with_pike_caviar",
                    () -> new net.minecraft.world.item.Item(new net.minecraft.world.item.Item.Properties()
                            .food(new net.minecraft.world.food.FoodProperties.Builder().nutrition(5).saturationModifier(0.6f).build())));

    public static final DeferredItem<Item> ROASTED_PUFFERFISH = ITEMS.register("roasted_pufferfish",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(6).saturationModifier(1.0f).build())));

    public static final DeferredItem<Item> RAW_PIKE = ITEMS.register("raw_pike",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(2).saturationModifier(0.1f).build())));

    public static final DeferredItem<Item> COOKED_PIKE = ITEMS.register("cooked_pike",
            () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(5).saturationModifier(0.6f).build())));
}