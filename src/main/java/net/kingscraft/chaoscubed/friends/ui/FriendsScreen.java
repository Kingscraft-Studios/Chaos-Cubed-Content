package net.kingscraft.chaoscubed.friends.ui;

import net.kingscraft.chaoscubed.friends.api.FriendRecord;
import net.kingscraft.chaoscubed.friends.api.FriendsResponse;
import net.kingscraft.chaoscubed.friends.client.FriendsService;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.network.chat.Component;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class FriendsScreen extends Screen {
    private FriendListWidget list;
    private EditBox targetBox;
    private Button friendsTab;
    private Button pendingTab;
    private Button refreshButton;
    private Button primaryAction;
    private Button secondaryAction;
    private Button tertiaryAction;
    private boolean showPending;
    private String statusLine = "Loading friends...";
    private long nextRefreshAt;
    private String currentUuidValue;

    public FriendsScreen() {
        super(Component.literal("Friends"));
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int panelWidth = 260;
        int listTop = 88;
        int listHeight = this.height - listTop - 28;

        this.friendsTab = this.addRenderableWidget(Button.builder(Component.literal("Friends"), button -> {
            this.showPending = false;
            this.refreshFriends();
        }).bounds(centerX - 128, 22, 64, 20).build());

        this.pendingTab = this.addRenderableWidget(Button.builder(Component.literal("Pending"), button -> {
            this.showPending = true;
            this.refreshFriends();
        }).bounds(centerX - 60, 22, 64, 20).build());

        this.refreshButton = this.addRenderableWidget(Button.builder(Component.literal("Refresh"), button -> this.refreshFriends())
                .bounds(centerX + 8, 22, 64, 20)
                .build());

        this.targetBox = new EditBox(this.font, centerX - (panelWidth / 2), 52, panelWidth - 60, 20, Component.literal("Profile Name"));
        this.targetBox.setHint(Component.literal("Profile Name"));
        this.addRenderableWidget(this.targetBox);

        this.addRenderableWidget(Button.builder(Component.literal("Send"), button -> sendRequest(this.targetBox.getValue()))
                .bounds(centerX + (panelWidth / 2) - 50, 52, 50, 20)
                .build());

        this.primaryAction = this.addRenderableWidget(Button.builder(Component.literal("Action"), button -> applyPrimaryAction())
                .bounds(centerX + 80, 52, 52, 20).build());
        this.secondaryAction = this.addRenderableWidget(Button.builder(Component.literal("Alt"), button -> applySecondaryAction())
                .bounds(centerX + 136, 52, 42, 20).build());
        this.tertiaryAction = this.addRenderableWidget(Button.builder(Component.literal("More"), button -> applyTertiaryAction())
                .bounds(centerX + 182, 52, 42, 20).build());

        this.list = new FriendListWidget(this.minecraft, panelWidth, listHeight, listTop, 28);
        this.list.setX(centerX - (panelWidth / 2));
        this.addRenderableWidget(this.list);

        this.refreshFriends();
    }

    private String currentUuid() {
        if (this.currentUuidValue != null) {
            return this.currentUuidValue;
        }
        return this.minecraft != null && this.minecraft.player != null ? this.minecraft.player.getStringUUID() : null;
    }

    private FriendRecord selected() {
        return this.list != null && this.list.getSelected() != null ? toRecord(this.list.getSelected()) : null;
    }

    private FriendRecord toRecord(FriendEntry entry) {
        FriendRecord record = new FriendRecord();
        record.uuid = entry.getUuid();
        record.name = entry.getName();
        record.direction = entry.getDirection();
        record.presence = entry.getStatus();
        return record;
    }

    private void sendRequest(String targetProfileName) {
        String from = currentUuid();
        if (from == null || targetProfileName == null || targetProfileName.isBlank()) {
            this.statusLine = "Enter a profile name.";
            return;
        }

        CompletableFuture.runAsync(() -> net.kingscraft.chaoscubed.friends.api.Friends.sendFriendRequestByName(from, targetProfileName.trim()))
                .whenComplete((ignored, error) -> this.minecraft.execute(() -> {
                    this.statusLine = error != null ? "Request failed." : "Friend request sent.";
                    this.targetBox.setValue("");
                    this.refreshFriends();
                }));
    }

    private void applyPrimaryAction() {
        FriendEntry selected = this.list == null ? null : this.list.getSelected();
        if (selected == null) return;

        String uuid = currentUuid();
        if (uuid == null) return;

        CompletableFuture.runAsync(() -> {
            if (this.showPending) {
                if ("incoming".equals(selected.getDirection())) {
                    net.kingscraft.chaoscubed.friends.api.Friends.acceptFriendRequest(uuid, selected.getUuid());
                } else {
                    net.kingscraft.chaoscubed.friends.api.Friends.cancelFriendRequest(uuid, selected.getUuid());
                }
            } else {
                net.kingscraft.chaoscubed.friends.api.Friends.removeFriend(uuid, selected.getUuid());
            }
        }).whenComplete((ignored, error) -> this.minecraft.execute(() -> {
            this.statusLine = error != null ? "Action failed." : "Done.";
            this.refreshFriends();
        }));
    }

    private void applySecondaryAction() {
        FriendEntry selected = this.list == null ? null : this.list.getSelected();
        if (selected == null || !this.showPending || !"incoming".equals(selected.getDirection())) return;
        CompletableFuture.runAsync(() -> net.kingscraft.chaoscubed.friends.api.Friends.declineFriendRequest(currentUuid(), selected.getUuid()))
                .whenComplete((ignored, error) -> this.minecraft.execute(() -> {
                    this.statusLine = error != null ? "Decline failed." : "Request declined.";
                    this.refreshFriends();
                }));
    }

    private void applyTertiaryAction() {
        FriendEntry selected = this.list == null ? null : this.list.getSelected();
        if (selected == null || !this.showPending || !"outgoing".equals(selected.getDirection())) return;
        CompletableFuture.runAsync(() -> net.kingscraft.chaoscubed.friends.api.Friends.cancelFriendRequest(currentUuid(), selected.getUuid()))
                .whenComplete((ignored, error) -> this.minecraft.execute(() -> {
                    this.statusLine = error != null ? "Cancel failed." : "Request canceled.";
                    this.refreshFriends();
                }));
    }

    private void refreshFriends() {
        String uuid = currentUuid();
        if (uuid == null) {
            this.statusLine = "Join a world to load friends.";
            return;
        }
        this.currentUuidValue = uuid;
        this.statusLine = "Refreshing...";
        FriendsService.refresh(uuid).whenComplete((data, error) -> this.minecraft.execute(() -> {
            this.list.clearEntries();
            if (error != null || data == null) {
                this.statusLine = "No friend data available.";
                return;
            }

            List<FriendRecord> rows = this.showPending
                    ? concat(data.requests, data.outgoingRequests)
                    : safe(data.friends);

            for (FriendRecord record : rows) {
                boolean request = "incoming".equals(record.direction);
                this.list.addFriend(record.name, record.uuid, record.presence, request, record.direction);
            }

            this.statusLine = rows.isEmpty()
                    ? (this.showPending ? "No pending requests." : "No friends yet.")
                    : "Ready.";
        }));
    }

    @Override
    public boolean keyPressed(KeyEvent keyEvent) {
        if (this.targetBox != null && this.targetBox.isFocused() && keyEvent.isConfirmation()) {
            this.sendRequest(this.targetBox.getValue());
            return true;
        }
        return super.keyPressed(keyEvent);
    }

    private List<FriendRecord> safe(List<FriendRecord> values) {
        return values == null ? Collections.emptyList() : values;
    }

    private List<FriendRecord> concat(List<FriendRecord> a, List<FriendRecord> b) {
        java.util.ArrayList<FriendRecord> out = new java.util.ArrayList<>();
        out.addAll(safe(a));
        out.addAll(safe(b));
        return out;
    }

    @Override
    public void tick() {
        super.tick();
        long now = System.currentTimeMillis();
        if (this.currentUuidValue != null && now >= this.nextRefreshAt) {
            this.nextRefreshAt = now + (this.minecraft != null && this.minecraft.screen == this ? 60_000L : 300_000L);
            this.refreshFriends();
        }
        if (this.primaryAction != null) {
            this.primaryAction.setMessage(Component.literal(this.showPending ? "Accept" : "Remove"));
        }
        if (this.secondaryAction != null) {
            this.secondaryAction.visible = this.showPending;
            this.secondaryAction.setMessage(Component.literal("Decline"));
        }
        if (this.tertiaryAction != null) {
            this.tertiaryAction.visible = this.showPending;
            this.tertiaryAction.setMessage(Component.literal("Cancel"));
        }
        if (this.showPending && this.list != null && this.list.getSelected() != null) {
            String dir = this.list.getSelected().getDirection();
            this.primaryAction.setMessage(Component.literal("incoming".equals(dir) ? "Accept" : "Cancel"));
            this.secondaryAction.visible = "incoming".equals(dir);
            this.tertiaryAction.visible = "outgoing".equals(dir);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float delta) {
        this.renderBackground(graphics, mouseX, mouseY, delta);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 8, 0xFFFFFF);
        graphics.drawCenteredString(this.font, Component.literal(this.statusLine), this.width / 2, 76, 0xAAAAAA);
        super.render(graphics, mouseX, mouseY, delta);
    }
}
