package com.alrex.parcool.common.capability.capabilities;

import com.alrex.parcool.common.capability.Animation;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.capability.Stamina;
import com.alrex.parcool.common.capability.storage.AnimationStorage;
import com.alrex.parcool.common.capability.storage.ParkourabilityStorage;
import com.alrex.parcool.common.capability.storage.StaminaStorage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.CapabilityManager;

public class Capabilities {
	@CapabilityInject(Stamina.class)
	public static final Capability<Stamina> STAMINA_CAPABILITY = null;
	@CapabilityInject(Parkourability.class)
	public static final Capability<Parkourability> PARKOURABILITY_CAPABILITY = null;
	@CapabilityInject(Animation.class)
	public static final Capability<Animation> ANIMATION_CAPABILITY = null;

	public static void register(CapabilityManager manager) {
		manager.register(Parkourability.class, new ParkourabilityStorage(), Parkourability::new);
		manager.register(Stamina.class, new StaminaStorage(), Stamina::new);
	}

	@OnlyIn(Dist.CLIENT)
	public static void registerClient(CapabilityManager manager) {
		manager.register(Animation.class, new AnimationStorage(), Animation::new);
	}
}
