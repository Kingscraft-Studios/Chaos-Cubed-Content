package net.kingscraft.chaoscubed.friends.ui.toasts;

import net.kingscraft.chaoscubed.client.ChaosCubedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.PlayerSkin;

public class ToastManager {
    public static void showToast(String friendUUID, String friendName, String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.getToastManager() == null) return;

        PlayerSkin skin = SkinCache.getNow(friendUUID);
        if (skin != null) {
            client.getToastManager().addToast(new FriendToast(skin, Component.literal(message), 5000L));
        } else {
            SkinCache.fetch(friendUUID, friendName);
            client.getToastManager().addToast(new FriendToast(Component.literal(message), 5000L));
        }
    }

    public static void showToast(String message) {
        Minecraft client = Minecraft.getInstance();
        if (client.getToastManager() == null) return;

        client.getToastManager().addToast(new FriendToast(Component.literal(message), 5000L));
    }

    public static void showErrorToast(String rawError) {
        Minecraft client = Minecraft.getInstance();
        if (client.getToastManager() == null) return;

        ChaosCubedClient.LOGGER.error("[FriendsToast] Error: {}", rawError);

        String display = switch (rawError != null ? rawError.toLowerCase() : "") {
            case "player not found" -> "Error: Player Doesn\u2019t Exist";
            case "request already exists" -> "Error: Request Already Exists";
            case "already friends" -> "Error: Already Friends";
            default -> "Error: " + (rawError != null ? rawError : "Unknown") + ". See Logs for Detailed Error";
        };

        client.getToastManager().addToast(new FriendToast(Component.literal(display), 7000L));
    }
}
