package net.kingscraft.chaoscubed.friends.ui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;

public class FriendEntry extends ObjectSelectionList.Entry<FriendEntry> {
    private final String name;
    private final String uuid;
    private final String status;
    private final boolean request;
    private final String direction;

    public FriendEntry(String name, String uuid, String status, boolean request, String direction) {
        this.name = name;
        this.uuid = uuid;
        this.status = status;
        this.request = request;
        this.direction = direction;
    }

    @Override
    public void renderContent(GuiGraphics graphics, int x, int y, boolean hovered, float tickDelta) {
        int entryWidth = 220;
        int entryHeight = 28;
        int bg = hovered ? 0x993A3A3A : 0x662D2D2D;
        graphics.fill(x, y, x + entryWidth, y + entryHeight - 2, bg);
        graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, name, x + 8, y + 5, 0xFFFFFF, false);
        graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, readableStatus(), x + 8, y + 16, 0xB0D0D0D0, false);

        String badge = request ? "Pending" : readableStatus();
        int color = request ? 0xFFFFAA55 : 0xFF55FF55;
        graphics.drawString(net.minecraft.client.Minecraft.getInstance().font, badge, x + entryWidth - 8 - net.minecraft.client.Minecraft.getInstance().font.width(badge), y + 8, color, false);
    }

    @Override
    public Component getNarration() {
        return Component.literal(name + " " + status);
    }

    public String getUuid() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public boolean isRequest() {
        return request;
    }

    public String getDirection() {
        return direction;
    }

    public String getStatus() {
        return status;
    }

    private String readableStatus() {
        if (status == null) {
            return "Offline";
        }
        return switch (status) {
            case "OFFLINE" -> "Offline";
            case "ONLINE" -> "Online";
            case "IN_WORLD" -> "In a world";
            case "JOINABLE_WORLD" -> "In a joinable world";
            default -> status;
        };
    }
}
