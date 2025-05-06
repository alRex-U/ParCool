package com.alrex.parcool.common.capability.capabilities;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class Capabilities {
	public static final Capability<IStamina> STAMINA_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	public static final ResourceLocation STAMINA_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.stamina");
	public static final Capability<Parkourability> PARKOURABILITY_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	public static final ResourceLocation PARKOURABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parkourability");
	public static final Capability<Animation> ANIMATION_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
	});
	public static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.animation");

	@SubscribeEvent
	public static void register(RegisterCapabilitiesEvent event) {
		event.register(Parkourability.class);
		event.register(IStamina.class);
	}

	@SubscribeEvent
	@OnlyIn(Dist.CLIENT)
	public static void registerClient(RegisterCapabilitiesEvent event) {
		event.register(Animation.class);
	}
}
