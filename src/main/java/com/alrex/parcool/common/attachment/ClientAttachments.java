package com.alrex.parcool.common.attachment;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.client.Animation;
import com.alrex.parcool.common.attachment.client.LocalStamina;
import com.alrex.parcool.fabric.IEventBus;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;

@Environment(EnvType.CLIENT)
public class ClientAttachments {
    public static final AttachmentType<LocalStamina> LOCAL_STAMINA =
            AttachmentRegistry.createDefaulted(
                    ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "client_local_stamina"),
                    LocalStamina::new);
    public static final AttachmentType<Animation> ANIMATION =
            AttachmentRegistry.createDefaulted(
                    ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "client_animation"),
                    Animation::new);

    public static void registerAll(IEventBus bus) {}
}
