package com.darwinfish.registry;

import com.darwinfish.DarwinFish;
import com.darwinfish.data.FishLoveData;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModAttachments {

    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
        DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, DarwinFish.MOD_ID);

    /**
     * Tracks love-mode and breeding cooldown for vanilla fish entities.
     * Serialized so it survives chunk unload.
     */
    public static final Supplier<AttachmentType<FishLoveData>> FISH_LOVE =
        ATTACHMENT_TYPES.register("fish_love", () ->
            AttachmentType.builder(() -> FishLoveData.DEFAULT)
                .serialize(FishLoveData.CODEC)
                .build()
        );
}
