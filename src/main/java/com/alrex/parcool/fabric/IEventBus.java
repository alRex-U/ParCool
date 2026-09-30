package com.alrex.parcool.fabric;

/**
 * Заглушка вместо шины NeoForge: под Fabric регистрации разложены по колбэкам, но форма вызова
 * `Xxx.registerAll(bus)` из ParCool.java сохранена, чтобы не трогать 12 файлов-реестров.
 */
public interface IEventBus {
    IEventBus INSTANCE = new IEventBus() {};
}
