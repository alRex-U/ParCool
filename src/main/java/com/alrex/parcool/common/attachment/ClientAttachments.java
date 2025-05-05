package com.alrex.parcool.common.attachment;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public class ClientAttachments {
    private static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, ParCool.MOD_ID + ".client");
    public static final Supplier<AttachmentType<LocalStamina>> LOCAL_STAMINA = ATTACHMENT_TYPES.register(
            "local_stamina",
            () -> AttachmentType.builder(LocalStamina::new).build()
    );
    public static final Supplier<AttachmentType<Animation>> ANIMATION = ATTACHMENT_TYPES.register(
            "animation",
            () -> AttachmentType.builder(Animation::new).build()
    );

    public static void registerAll(IEventBus bus) {
        ATTACHMENT_TYPES.register(bus);
    }
}
