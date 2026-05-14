package net.kingscraft.chaoscubed.friends.ui;

import net.kingscraft.chaoscubed.friends.client.FriendsClientState;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class FriendsConfirmScreen extends Screen {
    private final Screen parent;

    public FriendsConfirmScreen(Screen parent) {
        super(Component.literal("Friends List"));
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        this.addRenderableWidget(Button.builder(Component.literal("Enable Friends List"), button -> {
            FriendsClientState.setEnabled(true);
            this.minecraft.setScreen(this.parent);
        }).bounds(centerX - 90, centerY - 10, 180, 20).build());

        this.addRenderableWidget(Button.builder(Component.literal("Not Now"), button -> {
            FriendsClientState.setEnabled(false);
            this.minecraft.setScreen(this.parent);
        }).bounds(centerX - 90, centerY + 16, 180, 20).build());
    }
}
