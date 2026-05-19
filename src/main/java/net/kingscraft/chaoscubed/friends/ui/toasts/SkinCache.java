package net.kingscraft.chaoscubed.friends.ui.toasts;

import com.mojang.authlib.yggdrasil.ProfileResult;
import net.minecraft.client.Minecraft;
import net.minecraft.util.Util;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class SkinCache {
    private static final Map<String, CompletableFuture<PlayerSkin>> cache = new ConcurrentHashMap<>();

    public static CompletableFuture<PlayerSkin> fetch(String uuid, String name) {
        return cache.computeIfAbsent(uuid, k -> {
            return CompletableFuture.supplyAsync(() -> {
                try {
                    Minecraft mc = Minecraft.getInstance();
                    ProfileResult result = mc.services().sessionService().fetchProfile(UUID.fromString(k), true);
                    if (result == null) return null;
                    PlayerSkin skin = mc.getSkinManager().get(result.profile()).join().orElse(null);
                    return skin;
                } catch (Exception e) {
                    return null;
                }
            }, Util.backgroundExecutor());
        });
    }

    public static PlayerSkin getNow(String uuid) {
        CompletableFuture<PlayerSkin> future = cache.get(uuid);
        return future != null ? future.getNow(null) : null;
    }

    public static void preload(String uuid, String name) {
        fetch(uuid, name);
    }

    public static void clear() {
        cache.clear();
    }
}
