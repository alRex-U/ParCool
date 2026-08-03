package com.alrex.parcool.extern.feathers;

import com.alrex.parcool.extern.ModManager;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class FeathersManager extends ModManager {
    public FeathersManager() {
        super("feathers");
    }

    @Override
    public void init() {
        super.init();
        if (!isInstalled()) return;
        FMLJavaModLoadingContext.get().getModEventBus().register(new FeathersModLoadEventHandler());
    }
}
