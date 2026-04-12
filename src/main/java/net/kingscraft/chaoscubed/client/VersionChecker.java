package net.kingscraft.chaoscubed.client;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.fabricmc.loader.api.FabricLoader;
import java.io.InputStream;
import java.net.URL;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class VersionChecker {
    private static final String VERSION_URL = "https://github.com/Kingscraft-Studios/Chaos-Cubed-Content/raw/master/version/1.21.11.json";
    private static String latestVersion = null;
    private static boolean updateAvailable = false;

    private static final String CURRENT_VERSION = FabricLoader.getInstance()
            .getModContainer("chaos_cubed")
            .map(c -> c.getMetadata().getVersion().getFriendlyString())
            .orElse("1.0.0");

    // Run this ONCE in onInitializeClient
    public static void init() {
        CompletableFuture.runAsync(() -> {
            try (InputStream in = new URL(VERSION_URL).openStream();
                 Scanner s = new Scanner(in).useDelimiter("\\A")) {
                String result = s.hasNext() ? s.next() : "";
                if (result.contains("\"latest\":")) {
                    latestVersion = result.split("\"latest\":")[1].split("\"")[1];
                    updateAvailable = !CURRENT_VERSION.equals(latestVersion);
                }
            } catch (Exception e) {
                System.err.println("[Chaos Cubed] Failed to fetch version info: " + e.getMessage());
            }
        });
    }

    public static void sendUpdateMessage(LocalPlayer player) {
        if (updateAvailable && latestVersion != null) {
            player.displayClientMessage(Component.literal("§6[Chaos Cubed] §eNew version available: §a" + latestVersion), false);
            player.displayClientMessage(Component.literal("§7Current: " + CURRENT_VERSION), false);
        }
    }
}