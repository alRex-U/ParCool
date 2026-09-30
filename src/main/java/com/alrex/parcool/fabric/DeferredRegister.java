package com.alrex.parcool.fabric;

import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

/**
 * Аналог net.neoforged.neoforge.registries.DeferredRegister поверх ванильных реестров. Форма
 * вызовов апстрима сохранена целиком, поэтому 12 классов-реестров ParCool едут без правок, кроме
 * импортов. Порядок флаша задаётся порядком registerAll в ParCool: блоки раньше предметов, вкладка
 * позже предметов.
 */
public class DeferredRegister<T> {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final String namespace;
    private final List<Runnable> pending = new ArrayList<>();

    protected DeferredRegister(ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        this.registryKey = registryKey;
        this.namespace = namespace;
    }

    public static <T> DeferredRegister<T> create(
            ResourceKey<? extends Registry<T>> registryKey, String namespace) {
        return new DeferredRegister<>(registryKey, namespace);
    }

    public static DataComponents createDataComponents(
            ResourceKey<? extends Registry<DataComponentType<?>>> registryKey, String namespace) {
        return new DataComponents(registryKey, namespace);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    protected Registry<T> registry() {
        Registry<?> registry =
                (Registry<?>) BuiltInRegistries.REGISTRY.get((ResourceKey) registryKey);
        if (registry == null) {
            throw new IllegalStateException("ParCool: no built-in registry for " + registryKey);
        }
        return (Registry<T>) registry;
    }

    public <U extends T> DeferredHolder<T, U> register(
            String name, Supplier<? extends U> supplier) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(namespace, name);
        DeferredHolder<T, U> holder = new DeferredHolder<>(ResourceKey.create(registryKey, id));
        pending.add(
                () -> {
                    Holder.Reference<T> reference =
                            Registry.registerForHolder(registry(), id, supplier.get());
                    holder.bind(reference);
                });
        return holder;
    }

    public void register(IEventBus bus) {
        pending.forEach(Runnable::run);
        pending.clear();
    }

    public static class DataComponents extends DeferredRegister<DataComponentType<?>> {
        protected DataComponents(
                ResourceKey<? extends Registry<DataComponentType<?>>> registryKey,
                String namespace) {
            super(registryKey, namespace);
        }

        public <D> Supplier<DataComponentType<D>> registerComponentType(
                String name, UnaryOperator<DataComponentType.Builder<D>> builder) {
            DeferredHolder<DataComponentType<?>, DataComponentType<D>> holder =
                    register(name, () -> builder.apply(DataComponentType.builder()).build());
            return holder;
        }
    }
}
