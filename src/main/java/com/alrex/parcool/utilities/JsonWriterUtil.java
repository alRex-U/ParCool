package com.alrex.parcool.utilities;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import com.alrex.parcool.ParCool;
import com.alrex.parcool.server.limitation.Limitation;
import com.google.gson.stream.JsonWriter;

public class JsonWriterUtil {
    public static JsonWriter GetJsonWriter(File file) throws IOException {
        return new JsonWriter(
            new OutputStreamWriter(
                new BufferedOutputStream(
                    Files.newOutputStream(file.toPath(), StandardOpenOption.CREATE, StandardOpenOption.WRITE)
                ),
                StandardCharsets.UTF_8
            )
            );
    }

    public static void Save(Limitation limitation, File limitationFile) {
        try (JsonWriter writer = JsonWriterUtil.GetJsonWriter(limitationFile)
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
