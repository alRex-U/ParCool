package com.alrex.parcool.common.capability.capabilities;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.IStamina;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.capability.storage.AnimationStorage;
import com.alrex.parcool.common.capability.storage.ParkourabilityStorage;
import com.alrex.parcool.common.capability.storage.StaminaStorage;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities {
	@CapabilityInject(IStamina.class)
	public static final Capability<IStamina> STAMINA_CAPABILITY = null;
	public static final ResourceLocation STAMINA_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.stamina");
	@CapabilityInject(Parkourability.class)
	public static final Capability<Parkourability> PARKOURABILITY_CAPABILITY = null;
	public static final ResourceLocation PARKOURABILITY_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.parkourability");
	@CapabilityInject(Animation.class)
	public static final Capability<Animation> ANIMATION_CAPABILITY = null;
	public static final ResourceLocation ANIMATION_LOCATION = new ResourceLocation(ParCool.MOD_ID, "capability.animation");

	public static void register(CapabilityManager manager) {
		manager.register(Parkourability.class, new ParkourabilityStorage(), Parkourability::new);
		manager.register(IStamina.class, new StaminaStorage(), Stamina::new);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerClient(CapabilityManager manager) {
		manager.register(Animation.class, new AnimationStorage(), Animation::new);
	}
}
