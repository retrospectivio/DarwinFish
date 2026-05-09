package com.darwinfish.registry;

import com.darwinfish.DarwinFish;
import com.darwinfish.recipe.TropicalBreadRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, DarwinFish.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, SimpleCraftingRecipeSerializer<TropicalBreadRecipe>> TROPICAL_BREAD =
            SERIALIZERS.register("tropical_bread", () -> new SimpleCraftingRecipeSerializer<>(TropicalBreadRecipe::new));
}