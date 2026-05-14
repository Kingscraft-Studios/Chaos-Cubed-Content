package net.kingscraft.chaoscubed.friends.ui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.ObjectSelectionList;

public class FriendListWidget extends ObjectSelectionList<FriendEntry> {
    public FriendListWidget(Minecraft minecraft, int width, int height, int y, int entryHeight) {
        super(minecraft, width, height, y, entryHeight);
    }

    public void addFriend(String name, String uuid, String status, boolean request, String direction) {
        this.addEntry(new FriendEntry(name, uuid, status, request, direction));
    }

    public void clearEntries() {
        super.clearEntries();
    }
}
