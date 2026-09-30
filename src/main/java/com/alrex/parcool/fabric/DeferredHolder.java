package com.alrex.parcool.fabric;

import com.mojang.datafixers.util.Either;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderOwner;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Аналог net.neoforged.neoforge.registries.DeferredHolder: и Supplier, и Holder. Ссылка появляется
 * при флаше DeferredRegister, до этого обращение к значению -- ошибка.
 */
public class DeferredHolder<R, T extends R> implements Holder<R>, java.util.function.Supplier<T> {
    private final ResourceKey<R> key;
    private Holder.Reference<R> reference = null;

    DeferredHolder(ResourceKey<R> key) {
        this.key = key;
    }

    void bind(Holder.Reference<R> reference) {
        this.reference = reference;
    }

    private Holder.Reference<R> ref() {
        if (reference == null) {
            throw new IllegalStateException(
                    "ParCool: registry entry " + key + " is not registered yet");
        }
        return reference;
    }

    @SuppressWarnings("unchecked")
    @Override
    public T get() {
        return (T) ref().value();
    }

    public ResourceKey<R> getKey() {
        return key;
    }

    public ResourceLocation getId() {
        return key.location();
    }

    @Override
    public R value() {
        return ref().value();
    }

    @Override
    public boolean isBound() {
        return reference != null && reference.isBound();
    }

    @Override
    public boolean is(ResourceLocation id) {
        return key.location().equals(id);
    }

    @Override
    public boolean is(ResourceKey<R> other) {
        return key.equals(other);
    }

    @Override
    public boolean is(Predicate<ResourceKey<R>> predicate) {
        return predicate.test(key);
    }

    @Override
    public boolean is(TagKey<R> tag) {
        return ref().is(tag);
    }

    @Override
    public boolean is(Holder<R> holder) {
        return ref().is(holder);
    }

    @Override
    public Stream<TagKey<R>> tags() {
        return ref().tags();
    }

    @Override
    public Either<ResourceKey<R>, R> unwrap() {
        return Either.left(key);
    }

    @Override
    public Optional<ResourceKey<R>> unwrapKey() {
        return Optional.of(key);
    }

    @Override
    public Kind kind() {
        return Kind.REFERENCE;
    }

    @Override
    public boolean canSerializeIn(HolderOwner<R> owner) {
        return ref().canSerializeIn(owner);
    }
}
