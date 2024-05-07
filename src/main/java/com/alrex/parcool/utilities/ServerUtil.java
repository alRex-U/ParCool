package com.alrex.parcool.utilities;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.FolderName;
import net.minecraftforge.fml.loading.FileUtils;

import java.nio.file.Path;

public class ServerUtil {
    private static final FolderName SERVERCONFIG = new FolderName("serverconfig");

    public static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
        return serverConfig;
    }
}
