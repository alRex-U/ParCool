package com.alrex.parcool.extern.epicfight;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.extern.ModManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import yesman.epicfight.world.capabilities.EpicFightCapabilities;
import yesman.epicfight.world.capabilities.entitypatch.player.PlayerPatch;

import javax.annotation.Nullable;

public class EpicFightManager extends ModManager {
    public EpicFightManager() {
        super("epicfight");
    }

    @Nullable
    private SimpleChannel connection;

    @Override
    public void init() {
        super.init();
        if (!isInstalled()) return;
        var protocolVersion = "1";
        connection = NetworkRegistry.newSimpleChannel(
                new ResourceLocation(ParCool.MOD_ID, "epicfight"),
                () -> protocolVersion,
                protocolVersion::equals,
                protocolVersion::equals
        );
        FMLJavaModLoadingContext.get().getModEventBus().register(new EpicFightModLoadEventHandler());
    }

    @Override
    public void initInClient() {
        if (!isInstalled()) return;
        if (connection == null) return;
        connection.messageBuilder(EpicFightStaminaConsumePacket.class, 0)
                .noResponse()
                .encoder(EpicFightStaminaConsumePacket.HANDLER::encode)
                .decoder(EpicFightStaminaConsumePacket.HANDLER::decode)
                .consumerMainThread(EpicFightStaminaConsumePacket.HANDLER::handleInPhysicalClient)
                .add();
    }

    @Override
    public void initInDedicatedServer() {
        if (!isInstalled()) return;
        if (connection == null) return;
        connection.messageBuilder(EpicFightStaminaConsumePacket.class, 0)
                .noResponse()
                .encoder(EpicFightStaminaConsumePacket.HANDLER::encode)
                .decoder(EpicFightStaminaConsumePacket.HANDLER::decode)
                .consumerMainThread(EpicFightStaminaConsumePacket.HANDLER::handleInPhysicalServer)
                .add();
    }

    @Nullable
    PlayerPatch<?> getPlayerPatch(Player player) {
        if (!isInstalled()) return null;

        return EpicFightCapabilities.getEntityPatch(player, PlayerPatch.class);
    }

    public boolean isBattleMode(Player player) {
        if (!isInstalled()) return false;
        PlayerPatch<?> patch = getPlayerPatch(player);
        if (patch == null) return false;
        return patch.isBattleMode();
    }

    @Nullable
    public SimpleChannel getConnection() {
        return connection;
    }
}
