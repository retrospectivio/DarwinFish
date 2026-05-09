package com.darwinfish.recipe;

import com.darwinfish.registry.ModItems;
import com.darwinfish.registry.ModRecipes;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

public class TropicalBreadRecipe extends CustomRecipe {

    public TropicalBreadRecipe(CraftingBookCategory category) {
        super(category);
    }

    @Override
    public boolean matches(CraftingInput input, Level level) {
        boolean hasBread = false;
        boolean hasEggs = false;

        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (!stack.isEmpty()) {
                if (stack.is(Items.BREAD) && !hasBread) {
                    hasBread = true;
                } else if (stack.is(ModItems.BOTTLED_TROPICAL_FISH_EGGS.get()) && !hasEggs) {
                    hasEggs = true;
                } else {
                    return false; // В сетке лежит что-то лишнее
                }
            }
        }
        return hasBread && hasEggs;
    }

    @Override
    public ItemStack assemble(CraftingInput input, HolderLookup.Provider registries) {
        ItemStack eggs = ItemStack.EMPTY;

        // Ищем банку с икрой, чтобы забрать у нее цвет
        for (int i = 0; i < input.size(); i++) {
            ItemStack stack = input.getItem(i);
            if (stack.is(ModItems.BOTTLED_TROPICAL_FISH_EGGS.get())) {
                eggs = stack;
                break;
            }
        }

        ItemStack result = new ItemStack(ModItems.BREAD_WITH_TROPICAL_CAVIAR.get());

        // Копируем компонент CUSTOM_DATA (наши гены) из банки в бутерброд!
        if (!eggs.isEmpty() && eggs.has(DataComponents.CUSTOM_DATA)) {
            result.set(DataComponents.CUSTOM_DATA, eggs.get(DataComponents.CUSTOM_DATA));
        }
        return result;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= 2;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipes.TROPICAL_BREAD.get();
    }
}