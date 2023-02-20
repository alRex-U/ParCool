package com.alrex.parcool.common.action;

import com.alrex.parcool.common.action.impl.*;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class ActionList {
	public static final List<ActionRegistry<? extends Action>> ACTION_REGISTRIES = Arrays.asList(
			new ActionRegistry<>(BreakfallReady.class, BreakfallReady::new, 0),
			new ActionRegistry<>(CatLeap.class, CatLeap::new, 150),
			new ActionRegistry<>(ClimbUp.class, ClimbUp::new, 150),
			new ActionRegistry<>(ClingToCliff.class, ClingToCliff::new, 2),
			new ActionRegistry<>(Crawl.class, Crawl::new, 0),
			new ActionRegistry<>(Dive.class, Dive::new, 0),
			new ActionRegistry<>(Dodge.class, Dodge::new, 80),
			new ActionRegistry<>(FastRun.class, FastRun::new, 2),
			new ActionRegistry<>(Flipping.class, Flipping::new, 80),
			new ActionRegistry<>(HangDown.class, HangDown::new, 3),
			new ActionRegistry<>(HorizontalWallRun.class, HorizontalWallRun::new, 2),
			new ActionRegistry<>(JumpFromBar.class, JumpFromBar::new, 100),
			new ActionRegistry<>(QuickTurn.class, QuickTurn::new, 0),
			new ActionRegistry<>(Roll.class, Roll::new, 100),
			new ActionRegistry<>(Slide.class, Slide::new, 0),
			new ActionRegistry<>(Tap.class, Tap::new, 100),
			new ActionRegistry<>(Vault.class, Vault::new, 50),
			new ActionRegistry<>(WallJump.class, WallJump::new, 120),
			new ActionRegistry<>(WallSlide.class, WallSlide::new, 2)
	);
	private static final HashMap<Class<? extends Action>, Short> INDEX_MAP;
	private static final TreeMap<String, Short> NAME_2_INDEX_MAP;
	public static final List<Class<? extends Action>> ACTIONS
			= ACTION_REGISTRIES.stream().map(ActionRegistry::getClassInstance).collect(Collectors.toList());
	public static final List<String> NAMES = ACTIONS.stream().map(Class::getSimpleName).collect(Collectors.toList());

	static {
		INDEX_MAP = new HashMap<>((int) (ACTIONS.size() * 1.5));
		NAME_2_INDEX_MAP = new TreeMap<>();
		for (Class<? extends Action> action : ACTIONS) {
			short index = (short) ACTIONS.indexOf(action);
			INDEX_MAP.put(action, index);
			NAME_2_INDEX_MAP.put(action.getSimpleName(), index);
		}
	}

	public static short getIndexOf(Class<? extends Action> action) {
		return INDEX_MAP.getOrDefault(action, (short) -1);
	}

	public static Class<? extends Action> getByIndex(int index) {
		return ACTIONS.get(index);
	}

	@Nullable
	public static Class<? extends Action> getByName(String name) {
		short index = NAME_2_INDEX_MAP.getOrDefault(name, (short) -1);
		if (index == -1) return null;
		return ACTIONS.get(index);
	}

	public static List<Action> constructActionsList() {
		return ACTION_REGISTRIES.stream().map(ActionRegistry::createInstance).collect(Collectors.toList());
	}

	public static class ActionRegistry<T extends Action> {
		private final Class<T> classInstance;
		private final Supplier<T> factory;
		private final int defaultStaminaConsumption;

		public ActionRegistry(Class<T> action, Supplier<T> constructor, int defaultStaminaConsumption) {
			classInstance = action;
			factory = constructor;
			this.defaultStaminaConsumption = defaultStaminaConsumption;
		}

		public Class<T> getClassInstance() {
			return classInstance;
		}

		public T createInstance() {
			return factory.get();
		}

		public int getDefaultStaminaConsumption() {
			return defaultStaminaConsumption;
		}
	}
}
