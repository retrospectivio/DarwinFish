package com.darwinfish.handler;

import com.darwinfish.DarwinFish;
import com.darwinfish.FishType;
import com.darwinfish.block.FishEggBlock;
import com.darwinfish.blockentity.FishEggBlockEntity;
import com.darwinfish.data.FishLoveData;
import com.darwinfish.entity.BabyFishEntity;
import com.darwinfish.item.BottledFishEggItem;
import com.darwinfish.registry.ModAttachments;
import com.darwinfish.registry.ModBlocks;
import com.darwinfish.registry.ModItems;
import com.darwinfish.util.ColorMixer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.Cod;
import net.minecraft.world.entity.animal.Pufferfish;
import net.minecraft.world.entity.animal.Salmon;
import net.minecraft.world.entity.animal.TropicalFish;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.EntityJoinLevelEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.tick.EntityTickEvent;

import java.util.Optional;

@EventBusSubscriber(modid = DarwinFish.MOD_ID, bus = EventBusSubscriber.Bus.GAME)
public class FishBreedingHandler {

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        if (event.getLevel().isClientSide()) return;

        if (event.getEntity() instanceof AbstractFish fish && isBreedableFish(fish)) {

            // Приоритет 0: Инстинкт размножения стоит выше любых других действий (и страха, и атаки)
            fish.goalSelector.addGoal(0, new com.darwinfish.entity.goal.FishMateGoal(fish));

            if (!(fish instanceof com.darwinfish.entity.PikeEntity)) {
                // Мирные рыбы боятся щуку
                fish.goalSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.AvoidEntityGoal<>(
                        fish, com.darwinfish.entity.PikeEntity.class, 8.0F, 1.6D, 2.0D
                ));
                // Мирные рыбы едят траву
                fish.goalSelector.addGoal(3, new com.darwinfish.entity.goal.FishEatPlantGoal(fish));
            }

            if (!(fish instanceof com.darwinfish.entity.PikeEntity) && !(fish instanceof Pufferfish)) {
                // Инстинкт каннибализма при голоде (кроме щук и иглобрюхов)
                fish.goalSelector.addGoal(4, new com.darwinfish.entity.goal.FishSurvivalGoal(fish));
            }

            if (fish instanceof Pufferfish) {
                // Иглобрюх: защита от щуки
                fish.targetSelector.addGoal(1, new net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal(fish, com.darwinfish.entity.PikeEntity.class));
                fish.goalSelector.addGoal(2, new net.minecraft.world.entity.ai.goal.MeleeAttackGoal(fish, 1.2D, false));
            }
        }
    }

    @SubscribeEvent
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getLevel().isClientSide()) return;

        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());
        Entity target  = event.getTarget();

        if (!isBreedableFish(target)) return;
        AbstractFish fish = (AbstractFish) target;
        if (!fish.isInWater()) return;

        boolean isPike = fish instanceof com.darwinfish.entity.PikeEntity;
        boolean hasPikeFood = held.is(Items.COD) || held.is(Items.SALMON);
        boolean hasRegularFood = held.is(Items.SEAGRASS) || held.is(Items.KELP);

        if (isPike && !hasPikeFood) return;
        if (!isPike && !hasRegularFood) return;

        FishLoveData data = fish.getData(ModAttachments.FISH_LOVE.get());

        if (data.hasCooldown() || data.isInLove()) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.sidedSuccess(false));
            return;
        }

        fish.setData(ModAttachments.FISH_LOVE.get(), data.startLove());
        fish.setPersistenceRequired();

        // Если это щука, она наедается от руки игрока на полдня
        if (isPike) {
            ((com.darwinfish.entity.PikeEntity) fish).feed();
        }

        if (!player.isCreative()) {
            held.shrink(1);
        }

        ServerLevel serverLevel = (ServerLevel) fish.level();
        serverLevel.sendParticles(ParticleTypes.HEART,
                fish.getX(), fish.getY() + fish.getBbHeight() * 0.8, fish.getZ(),
                5, 0.3, 0.2, 0.3, 0.0);

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(false));
    }

    @SubscribeEvent
    public static void onEntityTick(EntityTickEvent.Post event) {
        Entity entity = event.getEntity();
        if (entity.level().isClientSide() || !isBreedableFish(entity)) return;

        AbstractFish fish = (AbstractFish) entity;
        if (!fish.hasData(ModAttachments.FISH_LOVE.get())) return;

        FishLoveData data = fish.getData(ModAttachments.FISH_LOVE.get());
        if (!data.isInLove() && !data.hasCooldown()) return;

        FishLoveData ticked = data.tick();
        fish.setData(ModAttachments.FISH_LOVE.get(), ticked);

        if (ticked.isInLove() && fish.tickCount % 10 == 0) {
            ServerLevel sl = (ServerLevel) fish.level();
            sl.sendParticles(net.minecraft.core.particles.ParticleTypes.HEART,
                    fish.getX(), fish.getY() + fish.getBbHeight() * 0.8, fish.getZ(),
                    1, 0.2, 0.2, 0.2, 0.0);
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        Player player = event.getEntity();
        ItemStack held = player.getItemInHand(event.getHand());

        if (!held.is(Items.GLASS_BOTTLE)) return;

        BlockPos pos = event.getPos();
        BlockState state = event.getLevel().getBlockState(pos);
        if (!(state.getBlock() instanceof FishEggBlock eggBlock)) return;

        if (event.getLevel().isClientSide()) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
            return;
        }

        ServerLevel level = (ServerLevel) event.getLevel();
        if (!(level.getBlockEntity(pos) instanceof FishEggBlockEntity be)) return;

        // Единая, универсальная логика создания предмета для любого типа рыбы
        BottledFishEggItem bottledItem = ModItems.getBottledEgg(eggBlock.getFishType());
        ItemStack result = bottledItem.createStack(be);

        // Перенос генетики (цветов) исключительно для тропических рыб
        if (eggBlock.getFishType() == FishType.TROPICAL_FISH && be.hasColorData()) {
            net.minecraft.nbt.CompoundTag tag = new net.minecraft.nbt.CompoundTag();
            tag.putInt("BaseColorId", be.getBaseColor().getId());
            tag.put("BlockEntityTag", be.getUpdateTag(level.registryAccess()));
            result.set(net.minecraft.core.component.DataComponents.CUSTOM_DATA,
                    net.minecraft.world.item.component.CustomData.of(tag));
        }

        // Забираем пустую баночку и выдаем полную
        if (!player.isCreative()) {
            held.shrink(1);
        }
        if (!player.getInventory().add(result)) {
            player.drop(result, false);
        }

        // Удаляем блок икры со дна
        if (state.getValue(BlockStateProperties.WATERLOGGED)) {
            level.setBlock(pos, Blocks.WATER.defaultBlockState(), 3);
        } else {
            level.removeBlock(pos, false);
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.CONSUME);
    }

    public static void doBreeding(ServerLevel level, AbstractFish fish, AbstractFish mate) {
        Optional<BlockPos> eggPos = findEggPosition(level, fish, mate);
        if (eggPos.isEmpty()) return;

        placeEgg(level, eggPos.get(), fish, mate);

        int cooldownTicks = com.darwinfish.config.DarwinFishConfig.BREEDING_COOLDOWN.get() * 20;

        FishLoveData data1 = fish.getData(ModAttachments.FISH_LOVE.get());
        fish.setData(ModAttachments.FISH_LOVE.get(), data1.startCooldown(cooldownTicks));

        FishLoveData data2 = mate.getData(ModAttachments.FISH_LOVE.get());
        mate.setData(ModAttachments.FISH_LOVE.get(), data2.startCooldown(cooldownTicks));

        level.sendParticles(ParticleTypes.HAPPY_VILLAGER,
                eggPos.get().getX() + 0.5, eggPos.get().getY() + 0.1, eggPos.get().getZ() + 0.5,
                6, 0.3, 0.1, 0.3, 0.0);
    }

    private static Optional<BlockPos> findEggPosition(ServerLevel level,
                                                      AbstractFish fish1,
                                                      AbstractFish fish2) {
        int cx = (int) ((fish1.getX() + fish2.getX()) / 2);
        int cy = (int) ((fish1.getY() + fish2.getY()) / 2);
        int cz = (int) ((fish1.getZ() + fish2.getZ()) / 2);

        for (int dx = -1; dx <= 1; dx++) {
            for (int dz = -1; dz <= 1; dz++) {
                for (int dy = 0; dy >= -32; dy--) {
                    BlockPos candidate = new BlockPos(cx + dx, cy + dy, cz + dz);
                    BlockPos below     = candidate.below();

                    BlockState candidateState = level.getBlockState(candidate);
                    BlockState belowState    = level.getBlockState(below);

                    boolean isWater = candidateState.is(Blocks.WATER);
                    boolean hasSolidFloor = belowState.isFaceSturdy(level, below, Direction.UP);

                    if (isWater && hasSolidFloor) {
                        return Optional.of(candidate);
                    }
                }
            }
        }

        return Optional.empty();
    }

    private static void placeEgg(ServerLevel level, BlockPos pos, AbstractFish parent1, AbstractFish parent2) {
        FishType type  = getFishType(parent1);
        FishEggBlock block = ModBlocks.getFor(type);

        BlockState newState = block.defaultBlockState()
                .setValue(BlockStateProperties.WATERLOGGED, true);

        level.setBlock(pos, newState, 3);

        if (level.getBlockEntity(pos) instanceof FishEggBlockEntity be) {
            long hatchTicks = com.darwinfish.config.DarwinFishConfig.EGG_HATCH_TIME.get() * 20L;
            be.setHatchTime(level.getGameTime() + hatchTicks);
            be.setEggCount(1 + level.random.nextInt(5));

            if (type == FishType.TROPICAL_FISH
                    && parent1 instanceof TropicalFish tf1
                    && parent2 instanceof TropicalFish tf2) {

                DyeColor mixBase    = ColorMixer.mixDyeColors(tf1.getBaseColor(),    tf2.getBaseColor());
                DyeColor mixPattern = ColorMixer.mixDyeColors(tf1.getPatternColor(), tf2.getPatternColor());
                be.setBaseColor(mixBase);
                be.setPatternColor(mixPattern);

                be.setParent1Genetics(tf1);
                be.setParent2Genetics(tf2);
            }

            boolean isDomesticated = parent1.isPersistenceRequired() || parent2.isPersistenceRequired();
            be.setPersistentLine(isDomesticated);

            be.setChanged();
            level.sendBlockUpdated(pos, newState, newState, net.minecraft.world.level.block.Block.UPDATE_CLIENTS);
        }
    }

    // НОВЫЙ КОД: Обязательно проверяем класс щуки
    private static boolean isBreedableFish(Entity entity) {
        if (entity instanceof BabyFishEntity) return false;
        return entity instanceof com.darwinfish.entity.PikeEntity
                || entity instanceof Cod
                || entity instanceof Salmon
                || entity instanceof Pufferfish
                || entity instanceof TropicalFish;
    }

    // НОВЫЙ КОД: Щука должна определяться первой, до лосося
    private static FishType getFishType(AbstractFish fish) {
        if (fish instanceof com.darwinfish.entity.PikeEntity) return FishType.PIKE;
        if (fish instanceof Cod)          return FishType.COD;
        if (fish instanceof Salmon)       return FishType.SALMON;
        if (fish instanceof Pufferfish)   return FishType.PUFFERFISH;
        return FishType.TROPICAL_FISH;
    }

    @SubscribeEvent
    public static void onFishHuntSuccess(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        if (event.getEntity().level().isClientSide()) return;

        Entity killer = event.getSource().getEntity();
        Entity victim = event.getEntity();

        if (killer instanceof com.darwinfish.entity.PikeEntity pike) {
            if (victim instanceof Cod || victim instanceof Salmon || victim instanceof TropicalFish) {

                // Щука успешно убила рыбу. Насыщаем её на полдня.
                pike.feed();

                // Проверяем конфиг: разрешено ли естественное размножение?
                if (com.darwinfish.config.DarwinFishConfig.NATURAL_BREEDING.get()) {

                    FishLoveData data = pike.getData(ModAttachments.FISH_LOVE.get());

                    if (!data.hasCooldown() && !data.isInLove()) {
                        pike.setData(ModAttachments.FISH_LOVE.get(), data.startLove());

                        ServerLevel serverLevel = (ServerLevel) pike.level();
                        serverLevel.sendParticles(ParticleTypes.HEART,
                                pike.getX(), pike.getY() + pike.getBbHeight() * 0.8, pike.getZ(),
                                5, 0.3, 0.2, 0.3, 0.0);
                    }
                }
            }
        }
    }
}