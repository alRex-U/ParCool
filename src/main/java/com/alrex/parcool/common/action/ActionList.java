package com.alrex.parcool.common.action;

import com.alrex.parcool.common.action.impl.*;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ActionList {
	private static final List<ActionRegistry<? extends Action>> ACTION_REGISTRIES = Arrays.asList(
			new ActionRegistry<>(AdditionalProperties.class, AdditionalProperties::new),
			new ActionRegistry<>(BreakfallReady.class, BreakfallReady::new),
			new ActionRegistry<>(CatLeap.class, CatLeap::new),
			new ActionRegistry<>(ClimbUp.class, ClimbUp::new),
			new ActionRegistry<>(ClingToCliff.class, ClingToCliff::new),
			new ActionRegistry<>(Crawl.class, Crawl::new),
			new ActionRegistry<>(Dive.class, Dive::new),
			new ActionRegistry<>(Dodge.class, Dodge::new),
			new ActionRegistry<>(FastRun.class, FastRun::new),
			new ActionRegistry<>(Flipping.class, Flipping::new),
			new ActionRegistry<>(HorizontalWallRun.class, HorizontalWallRun::new),
			new ActionRegistry<>(QuickTurn.class, QuickTurn::new),
			new ActionRegistry<>(Roll.class, Roll::new),
			new ActionRegistry<>(Slide.class, Slide::new),
			new ActionRegistry<>(Tap.class, Tap::new),
			new ActionRegistry<>(Vault.class, Vault::new),
			new ActionRegistry<>(WallJump.class, WallJump::new),
			new ActionRegistry<>(WallSlide.class, WallSlide::new)
	);
	public static final List<Class<? extends Action>> ACTIONS
			= ACTION_REGISTRIES.stream().map(ActionRegistry::getClassInstance).collect(Collectors.toList());

	public static List<Action> constructActionsList() {
		return ACTION_REGISTRIES.stream().map(ActionRegistry::createInstance).collect(Collectors.toList());
	}

	private static class ActionRegistry<T extends Action> {
		private final Class<T> classInstance;
		private final Supplier<T> factory;

		public ActionRegistry(Class<T> action, Supplier<T> constructor) {
			classInstance = action;
			factory = constructor;
		}

		public Class<T> getClassInstance() {
			return classInstance;
		}

		public T createInstance() {
			return factory.get();
		}
	}
}
