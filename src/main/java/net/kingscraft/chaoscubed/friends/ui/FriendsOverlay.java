package net.kingscraft.chaoscubed.friends.ui;

import net.minecraft.client.Minecraft;

public final class FriendsOverlay {
    private FriendsOverlay() {}

    public static void open() {
        Minecraft client = Minecraft.getInstance();
        if (client != null) {
            client.setScreen(new FriendsScreen());
        }
    }
}
