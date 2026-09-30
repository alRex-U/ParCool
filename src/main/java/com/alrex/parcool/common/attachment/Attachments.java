package com.alrex.parcool.common.attachment;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.attachment.common.Parkourability;
import com.alrex.parcool.common.attachment.common.ReadonlyStamina;
import com.alrex.parcool.fabric.IEventBus;

import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.minecraft.resources.ResourceLocation;

public class Attachments {
    public static final AttachmentType<ReadonlyStamina> STAMINA =
            AttachmentRegistry.<ReadonlyStamina>builder()
                    .initializer(ReadonlyStamina::createDefault)
                    .persistent(ReadonlyStamina.CODEC)
                    .buildAndRegister(
                            ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "stamina"));
    public static final AttachmentType<Parkourability> PARKOURABILITY =
            AttachmentRegistry.createDefaulted(
                    ResourceLocation.fromNamespaceAndPath(ParCool.MOD_ID, "parkourability"),
                    Parkourability::new);

    public static void registerAll(IEventBus bus) {
        // типы регистрируются при инициализации класса, вызов оставлен ради формы апстрима
    }
}
