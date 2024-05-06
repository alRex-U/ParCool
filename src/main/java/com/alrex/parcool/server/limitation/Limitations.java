package com.alrex.parcool.server.limitation;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.common.capability.Parkourability;
import com.alrex.parcool.common.info.ServerLimitation;
import com.alrex.parcool.common.network.SyncLimitationMessage;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.server.ServerAboutToStartEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import org.apache.commons.io.FileUtils;

import javax.annotation.Nullable;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class Limitations {
    private static final Limitation.ID GLOBAL_ID = new Limitation.ID(ParCool.MOD_ID, "global");
    public static final Limitation.ID INDIVIDUAL_ID = new Limitation.ID(ParCool.MOD_ID, "individual");
    private static final SortedMap<UUID, SortedMap<Limitation.ID, Limitation>> Loaded = new TreeMap<>();
    private static final SortedSet<Limitation.ID> RegisteredID = new TreeSet<>();
    private static final Limitation GlobalLimitation = new Limitation(GLOBAL_ID);
    @Nullable
    private static Path LimitationFolderRootPath = null;

    private static SortedMap<Limitation.ID, Limitation> getLimitationMapOf(UUID playerID) {
        SortedMap<Limitation.ID, Limitation> map = Loaded.get(playerID);
        if (map == null) {
            map = load(playerID);
        }
        return map;
    }

    public static Collection<Limitation> getLimitationsOf(UUID playerID) {
        return getLimitationMapOf(playerID).values();
    }

    public static Limitation createLimitationOf(UUID playerID, Limitation.ID id) {
        Limitation limitation = getLimitationOf(playerID, id);
        if (limitation != null) return limitation;
        limitation = new Limitation(id);
        RegisteredID.add(id);
        getLimitationMapOf(playerID).put(id, limitation);
        return limitation;
    }

    public static boolean delete(Limitation.ID id) {
        if (LimitationFolderRootPath == null) return false;
        for (SortedMap<Limitation.ID, Limitation> limitationMap : Loaded.values()) {
            limitationMap.remove(id);
        }
        try {
            FileUtils.deleteDirectory(getFolderPath(LimitationFolderRootPath, id).toFile());
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    public static Limitation getGlobalLimitation() {
        return GlobalLimitation;
    }

    public static Collection<Limitation.ID> getRegisteredIDs() {
        return RegisteredID;
    }

    @Nullable
    public static Limitation getLimitationOf(UUID playerID, Limitation.ID id) {
        if (id.equals(GLOBAL_ID)) {
            return GlobalLimitation;
        }
        return getLimitationMapOf(playerID).get(id);
    }

    public static void update(ServerPlayer player) {
        Parkourability parkourability = Parkourability.get(player);
        if (parkourability == null) return;
        parkourability.getActionInfo().setServerLimitation(ServerLimitation.get(player));
        SyncLimitationMessage.sync(player);
    }

    public static SortedMap<Limitation.ID, Limitation> load(UUID playerID) {
        if (LimitationFolderRootPath == null) {
            throw new IllegalStateException(
                    "When loading Limitation Player:" +
                            playerID.toString() +
                            ", Initialization is not completed yet"
            );
        }
        File limitationFolder = LimitationFolderRootPath.toFile();
        File[] directories = limitationFolder.listFiles(File::isDirectory);
        if (directories == null) {
            ParCool.LOGGER.error("Cannot get Limitation folders");
            return null;
        }
        SortedMap<Limitation.ID, Limitation> playerData = Loaded.computeIfAbsent(playerID, k -> new TreeMap<>());
        for (File dir : directories) {
            File[] limitationGroups = dir.listFiles(File::isDirectory);
            if (limitationGroups == null) {
                ParCool.LOGGER.error("Cannot get Limitation folders of '" + dir.getName() + "'");
                continue;
            }
            for (File limitationGroup : limitationGroups) {
                File[] limitationFiles = limitationGroup.listFiles((file) -> file.isFile() && file.canRead() && file.getName().endsWith(".json"));
                if (limitationFiles == null) {
                    ParCool.LOGGER.error("Cannot get Limitation files of '" + dir.getName() + "'");
                    continue;
                }
                Limitation.ID limitationID = new Limitation.ID(dir.getName(), limitationGroup.getName());
                RegisteredID.add(limitationID);
                for (File limitationFile : limitationFiles) {
                    String limitationFilename = limitationFile.getName();
                    String uuidString = limitationFilename.substring(0, limitationFilename.length() - ".json".length());
                    UUID playerUUID = UUID.fromString(uuidString);
                    if (!playerUUID.equals(playerID)) {
                        continue;
                    }
                    try (JsonReader reader =
                                 new JsonReader(
                                         new InputStreamReader(
                                                 new BufferedInputStream(
                                                         new FileInputStream(limitationFile)
                                                 ),
                                                 StandardCharsets.UTF_8
                                         )
                                 )
                    ) {
                        Limitation limitation = new Limitation(limitationID);
                        limitation.loadFrom(reader);
                        playerData.put(limitation.getID(), limitation);
                    } catch (FileNotFoundException e) {
                        ParCool.LOGGER.error("Could not read '" + limitationFile.getAbsolutePath() + "', skipped.");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        ParCool.LOGGER.info("Limitation of " + playerID + " was loaded");
        return playerData;
    }

    public static void unload(UUID playerID) {
        if (LimitationFolderRootPath == null) {
            throw new IllegalStateException(
                    "When loading Limitation Player:" +
                            playerID.toString() +
                            ", Initialization is not completed yet"
            );
        }
        for (Limitation limitation : Loaded.remove(playerID).values()) {
            File limitationFile = getFolderPath(LimitationFolderRootPath, limitation.getID())
                    .resolve(playerID + ".json").toFile();
            if (!limitationFile.getParentFile().exists()) {
                limitationFile.getParentFile().mkdirs();
            }
            try (JsonWriter writer =
                         new JsonWriter(
                                 new OutputStreamWriter(
                                         new BufferedOutputStream(
                                                 Files.newOutputStream(limitationFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                                         ),
                                         StandardCharsets.UTF_8
                                 )
                         )
            ) {
                limitation.saveTo(writer);
            } catch (IOException e) {
                ParCool.LOGGER.error(
                        "IOException during saving limitation : "
                                + e.getMessage()
                );
            }
        }
        ParCool.LOGGER.info("Limitation of " + playerID + " was unloaded");
    }

    public static void init(ServerAboutToStartEvent event) {
        GlobalLimitation.readFromServerConfig();
        Path configPath = getServerConfigPath(event.getServer());
        LimitationFolderRootPath = configPath.resolve("parcool").resolve("limitations");
        File limitationFolder = LimitationFolderRootPath.toFile();
        if (!limitationFolder.exists()) {
            limitationFolder.mkdirs();
        }
    }

    public static void save(ServerStoppingEvent event) {
        Path configPath = getServerConfigPath(event.getServer());
        Path limitationRootPath = configPath.resolve("parcool").resolve("limitations");
        for (Map.Entry<UUID, SortedMap<Limitation.ID, Limitation>> limitationEntry : Loaded.entrySet()) {
            UUID playerID = limitationEntry.getKey();
            for (Limitation limitation : limitationEntry.getValue().values()) {
                File limitationFile = getFolderPath(limitationRootPath, limitation.getID())
                        .resolve(playerID.toString() + ".json").toFile();
                if (!limitationFile.getParentFile().exists()) {
                    limitationFile.getParentFile().mkdirs();
                }
                try (JsonWriter writer =
                             new JsonWriter(
                                     new OutputStreamWriter(
                                             new BufferedOutputStream(
                                                     Files.newOutputStream(limitationFile.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                                             ),
                                             StandardCharsets.UTF_8
                                     )
                             )
                ) {
                    limitation.saveTo(writer);
                } catch (IOException e) {
                    ParCool.LOGGER.error(
                            "IOException during saving limitation : "
                                    + e.getMessage()
                    );
                }
            }
        }
    }

    private static final LevelResource SERVERCONFIG = new LevelResource("serverconfig");

    private static Path getServerConfigPath(final MinecraftServer server) {
        final Path serverConfig = server.getWorldPath(SERVERCONFIG);
        net.minecraftforge.fml.loading.FileUtils.getOrCreateDirectory(serverConfig, "serverconfig");
        return serverConfig;
    }

    public static Path getFolderPath(Path limitationRootPath, Limitation.ID id) {
        return limitationRootPath
                .resolve(id.getGroup())
                .resolve(id.getName());
    }
}
