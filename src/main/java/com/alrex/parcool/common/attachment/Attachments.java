package com.alrex.parcool.common.attachment;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.stamina.ReadonlyStamina;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class Attachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ParCool.MOD_ID);
    public static final Supplier<AttachmentType<ReadonlyStamina>> STAMINA = ATTACHMENT_TYPES.register(
            "stamina",
            () -> AttachmentType
                    .builder(ReadonlyStamina::createDefault)
                    .serialize(ReadonlyStamina.CODEC)
                    .build()
    );

    public static void registerAll(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
