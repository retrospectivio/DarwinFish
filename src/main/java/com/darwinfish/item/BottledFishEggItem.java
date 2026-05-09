package com.darwinfish.item;

import com.darwinfish.FishType;
import com.darwinfish.blockentity.FishEggBlockEntity;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

/**
 * A glass bottle filled with fish eggs.
 * All types are now edible (chewing animation).
 * Eating them returns an empty glass bottle.
 */
public class BottledFishEggItem extends Item {

    private final FishType fishType;

    public BottledFishEggItem(FishType fishType, Item.Properties properties) {
        super(properties);
        this.fishType = fishType;
    }

    public FishType getFishType() {
        return fishType;
    }

    /**
     * Creates a stack from a FishEggBlockEntity. Encodes tropical-fish color
     * data into the stack's CUSTOM_DATA component.
     */
    public ItemStack createStack(FishEggBlockEntity be) {
        ItemStack stack = new ItemStack(this);

        if (fishType == FishType.TROPICAL_FISH && be.hasColorData()) {
            CompoundTag tag = new CompoundTag();
            tag.putInt("BaseColorId",    be.getBaseColor().getId());
            tag.putInt("PatternColorId", be.getPatternColor().getId());
            stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        }

        return stack;
    }

    // -------------------------------------------------------------------------
    // Eating logic — Returns empty bottle and sets eating time
    // -------------------------------------------------------------------------

    // Устанавливаем время поедания (40 тиков = 2 секунды, как у бутылочки меда)
    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 40;
    }

    // Явно указываем анимацию жевания
    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.EAT;
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        // Базовый метод съест 1 баночку из стака и применит эффекты еды
        ItemStack result = super.finishUsingItem(stack, level, entity);

        // Игрокам в креативе бутылочки не возвращаем
        if (entity instanceof Player player && player.getAbilities().instabuild) {
            return result;
        }

        // Выдаем пустую бутылочку
        if (result.isEmpty()) {
            return new ItemStack(Items.GLASS_BOTTLE);
        } else {
            if (entity instanceof Player player) {
                ItemStack bottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(bottle)) {
                    player.drop(bottle, false);
                }
            }
            return result;
        }
    }
}