package org.com.dynamiclantern.client;

import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.ModList;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public final class ShaderHeldLightMode {
    private static final String HELD_BLOCK_LIGHT_VALUE = "heldblocklightvalue";
    private static final String HELD_BLOCK_LIGHT_VALUE_2 = "heldblocklightvalue2";
    private static final String HELD_ITEM_ID_2 = "helditemid2";

    private static boolean cachedMainHandOnly;

    private ShaderHeldLightMode() {
    }

    public static synchronized boolean usesMainHandOnlyHeldLight() {
        return cachedMainHandOnly;
    }

    public static synchronized void refresh() {
        String shaderPack = readActiveShaderPack();
        cachedMainHandOnly = !shaderPack.isEmpty() && scanMainHandOnly(shaderPack);
    }

    private static String readActiveShaderPack() {
        Path configDir = Minecraft.getInstance().gameDirectory.toPath().resolve("config");
        if (ModList.get() != null && ModList.get().isLoaded("oculus") && !ModList.get().isLoaded("iris")) {
            String oculusPack = readShaderPack(configDir.resolve("oculus.properties"));
            if (!oculusPack.isEmpty()) {
                return oculusPack;
            }
        }

        String irisPack = readShaderPack(configDir.resolve("iris.properties"));
        if (!irisPack.isEmpty()) {
            return irisPack;
        }

        return readShaderPack(configDir.resolve("oculus.properties"));
    }

    private static String readShaderPack(Path propertiesPath) {
        if (!Files.isRegularFile(propertiesPath)) {
            return "";
        }

        Properties properties = new Properties();
        try (InputStream input = Files.newInputStream(propertiesPath)) {
            properties.load(input);
        } catch (IOException ignored) {
            return "";
        }

        String shaderPack = properties.getProperty("shaderPack", "");
        return "OFF".equalsIgnoreCase(shaderPack) ? "" : shaderPack;
    }

    private static boolean scanMainHandOnly(String shaderPack) {
        Path shaderPackPath = Minecraft.getInstance().gameDirectory.toPath().resolve("shaderpacks").resolve(shaderPack);
        try {
            if (Files.isDirectory(shaderPackPath)) {
                return scanDirectory(shaderPackPath);
            }
            if (Files.isRegularFile(shaderPackPath)) {
                return scanZip(shaderPackPath);
            }
        } catch (IOException ignored) {
            return false;
        }
        return false;
    }

    private static boolean scanDirectory(Path shaderPackPath) throws IOException {
        ScanResult result = new ScanResult();
        try (var files = Files.walk(shaderPackPath)) {
            for (Path file : files.filter(Files::isRegularFile).toList()) {
                if (isShaderSource(file.toString())) {
                    scanText(Files.readString(file, StandardCharsets.UTF_8), result);
                }
            }
        }
        return result.mainHandOnly();
    }

    private static boolean scanZip(Path shaderPackPath) throws IOException {
        ScanResult result = new ScanResult();
        try (ZipInputStream zip = new ZipInputStream(Files.newInputStream(shaderPackPath))) {
            ZipEntry entry;
            while ((entry = zip.getNextEntry()) != null) {
                if (!entry.isDirectory() && isShaderSource(entry.getName())) {
                    scanText(new String(zip.readAllBytes(), StandardCharsets.UTF_8), result);
                }
            }
        }
        return result.mainHandOnly();
    }

    private static boolean isShaderSource(String path) {
        String lower = path.toLowerCase(Locale.ROOT);
        return lower.endsWith(".fsh")
                || lower.endsWith(".vsh")
                || lower.endsWith(".glsl")
                || lower.endsWith(".properties");
    }

    private static void scanText(String text, ScanResult result) {
        String lower = text.toLowerCase(Locale.ROOT);
        result.hasMainHeldLight |= lower.contains(HELD_BLOCK_LIGHT_VALUE);
        result.hasSecondHeldLight |= lower.contains(HELD_BLOCK_LIGHT_VALUE_2) || lower.contains(HELD_ITEM_ID_2);
    }

    private static final class ScanResult {
        private boolean hasMainHeldLight;
        private boolean hasSecondHeldLight;

        private boolean mainHandOnly() {
            return hasMainHeldLight && !hasSecondHeldLight;
        }
    }
}
