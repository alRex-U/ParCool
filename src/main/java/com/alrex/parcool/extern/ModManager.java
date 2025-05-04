package com.alrex.parcool.extern;

import net.minecraftforge.fml.ModList;

import javax.annotation.Nullable;

public abstract class ModManager {
    private boolean installed = false;
    private final String modId;

    public ModManager(String modId) {
        this.modId = modId;
    }

    public void init() {
        @Nullable
        var mod = ModList.get().getModFileById(modId);
        installed = mod != null;
    }

    public boolean isInstalled() {
        return installed;
    }

    public String getModID() {
        return modId;
    }
}
