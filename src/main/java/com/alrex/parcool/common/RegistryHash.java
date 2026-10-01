package com.alrex.parcool.common;

import com.alrex.parcool.api.action.ActionGroup;
import com.alrex.parcool.common.action.ActionRegistry;
import com.alrex.parcool.common.stamina.StaminaTypeRegistry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Comparator;

public class RegistryHash {
    /// Calculate Hash from constructed registries.
    ///
    /// This value should be used for checking the registries match ones in remote, not for security
    /// so this hash don't have to have high cryptographic strength
    public static String getHash(ActionRegistry actionRegistry, StaminaTypeRegistry staminaTypeRegistry) {
        try {
            // Java docs says MD5 implementation exist in every Java platform
            var hashProvider = MessageDigest.getInstance("MD5");
            var byteArrayStream = new ByteArrayOutputStream();
            byte[] pushedDataArray;
            try (var streamWriter = new OutputStreamWriter(byteArrayStream)) {
                for (var group : actionRegistry.getRegisteredGroups().values().stream().sorted(Comparator.comparing(ActionGroup::namespace)).toList()) {
                    streamWriter.write(group.namespace());
                    streamWriter.write('#');
                    for (var action : group.actions()) {
                        streamWriter.write(action.id().getPath());
                        streamWriter.write('/');
                    }
                }
                streamWriter.write('|');
                for (var staminaType : staminaTypeRegistry.getEntries()) {
                    streamWriter.write(staminaType.id().toString());
                }
                streamWriter.flush();
                pushedDataArray = byteArrayStream.toByteArray();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return Base64.getEncoder().encodeToString(hashProvider.digest(pushedDataArray));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
