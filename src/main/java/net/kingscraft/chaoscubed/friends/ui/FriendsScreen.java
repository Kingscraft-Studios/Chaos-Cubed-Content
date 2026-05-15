package net.kingscraft.chaoscubed.friends.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import net.kingscraft.chaoscubed.friends.api.FriendsApi;
import net.kingscraft.chaoscubed.friends.structure.FriendsModels;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class FriendsScreen extends BaseOwoScreen<FlowLayout> {

    private FlowLayout contentPanel;
    private FlowLayout friendsTab;
    private FlowLayout requestsTab;
    private boolean isFriendsTab = true;
    private FlowLayout friendsListContainer;

    @Override
    protected @NotNull OwoUIAdapter<FlowLayout> createAdapter() {
        return OwoUIAdapter.create(this, (h, v) -> UIContainers.verticalFlow(Sizing.fill(100), Sizing.fill(100)));
    }

    @Override
    protected void build(FlowLayout rootComponent) {
        rootComponent.surface(Surface.VANILLA_TRANSLUCENT);
        rootComponent.horizontalAlignment(HorizontalAlignment.CENTER);
        rootComponent.verticalAlignment(VerticalAlignment.CENTER);

        // --- 1. TABS AREA ---
        var tabs = UIContainers.horizontalFlow(Sizing.fixed(200), Sizing.content());
        tabs.horizontalAlignment(HorizontalAlignment.CENTER);
        tabs.gap(4); // Tight gap like buttons

        friendsTab = createTab("Friends");
        requestsTab = createTab("Requests (0)");

        friendsTab.mouseDown().subscribe((x, y) -> {
            if (!isFriendsTab) { isFriendsTab = true; refreshUI(); loadDataAndRefresh(); }
            return true;
        });

        requestsTab.mouseDown().subscribe((x, y) -> {
            if (isFriendsTab) { isFriendsTab = false; refreshUI(); loadDataAndRefresh(); }
            return true;
        });

        tabs.child(friendsTab);
        tabs.child(requestsTab);
        rootComponent.child(tabs);

        // --- 2. MAIN CONTENT BOX ---
        contentPanel = UIContainers.verticalFlow(Sizing.fixed(200), Sizing.fill(70));
        contentPanel.surface(Surface.flat(0xFF151515).and(Surface.outline(0xFFFFFFFF)));
        contentPanel.padding(Insets.of(16));
        contentPanel.margins(Insets.top(-1)); // Connect to tabs
        contentPanel.horizontalAlignment(HorizontalAlignment.CENTER);

        rootComponent.child(contentPanel);

        refreshUI();
        loadDataAndRefresh();
    }

    private void refreshUI() {
        updateTabVisuals();
        rebuildContent();
    }

    private FlowLayout createTab(String text) {
        var container = UIContainers.verticalFlow(Sizing.fixed(98), Sizing.fixed(22));
        container.horizontalAlignment(HorizontalAlignment.CENTER);
        container.verticalAlignment(VerticalAlignment.CENTER);
        container.cursorStyle(CursorStyle.HAND);

        // This makes the tab look like a Minecraft button background
        container.surface(Surface.PANEL);

        // Child 0: Text
        container.child(UIComponents.label(Component.literal(text)).shadow(true));

        // Child 1: The Active Line slot (absolute positioned at the bottom)
        var line = UIContainers.horizontalFlow(Sizing.fixed(80), Sizing.fixed(1));
        line.positioning(Positioning.relative(50, 100)); // Bottom center
        container.child(line);

        return container;
    }

    private void updateTabVisuals() {
        FlowLayout fLine = (FlowLayout) friendsTab.children().get(1);
        LabelComponent fLabel = (LabelComponent) friendsTab.children().get(0);

        FlowLayout rLine = (FlowLayout) requestsTab.children().get(1);
        LabelComponent rLabel = (LabelComponent) requestsTab.children().get(0);

        fLine.surface(isFriendsTab ? Surface.flat(0xFFFFFFFF) : Surface.BLANK);
        rLine.surface(!isFriendsTab ? Surface.flat(0xFFFFFFFF) : Surface.BLANK);

        fLabel.color(isFriendsTab ? Color.WHITE : Color.ofRgb(0xAAAAAA));
        rLabel.color(!isFriendsTab ? Color.WHITE : Color.ofRgb(0xAAAAAA));
    }

    private void rebuildContent() {
        contentPanel.clearChildren();

        if (isFriendsTab) {
            // --- FRIENDS VIEW: SEARCH + SEND ---
            var searchRow = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.content());
            searchRow.verticalAlignment(VerticalAlignment.CENTER);
            searchRow.gap(4);

            TextBoxComponent searchBar = UIComponents.textBox(Sizing.fill(70));
            searchBar.setMaxLength(16);
            searchBar.setSuggestion("Enter Profile Name");

            // This listener checks every keystroke
            searchBar.onChanged().subscribe(value -> {
                if (!value.isEmpty()) {
                    // Clear the ghost text so it doesn't overlap your typing
                    searchBar.setSuggestion("");
                } else {
                    // Bring it back if the box is empty
                    searchBar.setSuggestion("Enter Profile Name");
                }
            });

            searchRow.child(searchBar);

            var sendBtn = UIComponents.button(Component.literal("Send"), b -> {
                String targetName = searchBar.getValue();
                if (!targetName.isEmpty()) {
                    System.out.println("[FriendsDebug] Attempting to send request to: " + targetName);

                    java.util.concurrent.CompletableFuture.supplyAsync(() ->
                            FriendsApi.sendFriendRequestByName(this.minecraft.player.getUUID().toString(), targetName)
                    ).thenAccept(res -> {
                        System.out.println("[FriendsDebug] Send Request Status: " + (res != null ? "Success" : "Failed"));
                        this.minecraft.execute(() -> {
                            searchBar.text("");
                            loadDataAndRefresh();
                        });
                    });
                }
            }).sizing(Sizing.fixed(40), Sizing.fixed(20));

            sendBtn.margins(Insets.right(2));
            searchRow.child(sendBtn);
            contentPanel.child(searchRow);

            // --- NAME LINE ---
            var nameLine = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
            nameLine.verticalAlignment(VerticalAlignment.CENTER);
            nameLine.margins(Insets.vertical(5));
            nameLine.child(UIComponents.label(Component.literal("My profile: ")).color(Color.ofRgb(0x888888)));

            String playerName = this.minecraft.player != null ? this.minecraft.player.getName().getString() : "Player";
            nameLine.child(UIComponents.label(Component.literal(playerName)));
            contentPanel.child(nameLine);

            // --- THE SEPARATOR (Acting as the list "Edge") ---
            // The separator line acting as a "cap"
            contentPanel.child(UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(1))
                    .surface(Surface.flat(0xFF444444))
                    .margins(Insets.bottom(4)));
        }

        // --- THE SCROLLABLE LIST ---
        var list = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        this.friendsListContainer = list;

        contentPanel.child(UIContainers.verticalScroll(Sizing.fill(100), Sizing.fill(100), list));
    }

    private void loadDataAndRefresh() {
        if (this.minecraft.player == null) return;
        String uuid = this.minecraft.player.getUUID().toString();

        System.out.println("[FriendsDebug] Fetching data for UUID: " + uuid);

        java.util.concurrent.CompletableFuture.supplyAsync(() -> FriendsApi.getFriends(uuid))
                .thenAccept(response -> {
                    if (response != null) {
                        System.out.println("[FriendsDebug] Received response! Badge count: " + response.incomingBadge());
                        this.minecraft.execute(() -> this.renderData(response));
                    } else {
                        System.out.println("[FriendsDebug] API returned NULL (Check your Worker URL or internet)");
                    }
                }).exceptionally(ex -> {
                    System.out.println("[FriendsDebug] API CRASHED: " + ex.getMessage());
                    ex.printStackTrace();
                    return null;
                });
    }

    private void renderData(FriendsModels.FriendsListResponse data) {
        // Existing badge logic...
        LabelComponent rLabel = (LabelComponent) requestsTab.children().get(0);
        rLabel.text(Component.literal("Requests (" + data.incomingBadge() + ")"));

        if (this.friendsListContainer == null) return;
        this.friendsListContainer.clearChildren();

        if (isFriendsTab) {
            int friendCount = (data.friends() != null) ? data.friends().size() : 0;
            System.out.println("[FriendsDebug] Rendering Friends Tab. Count: " + friendCount);

            if (friendCount > 0) {
                for (var friend : data.friends()) {
                    System.out.println("[FriendsDebug] Found Friend: " + friend.name() + " (" + friend.presence() + ")");
                    this.friendsListContainer.child(createFriendRow(friend));
                }
            } else {
                this.addEmptyLabel("No friends yet!");
            }
        } else {
            int reqCount = (data.requests() != null) ? data.requests().size() : 0;
            System.out.println("[FriendsDebug] Rendering Requests Tab. Count: " + reqCount);

            if (reqCount > 0) {
                for (var req : data.requests()) {
                    System.out.println("[FriendsDebug] Found Request from: " + req.name());
                    this.friendsListContainer.child(createRequestRow(req));
                }
            } else {
                this.addEmptyLabel("No pending requests.");
            }
        }
    }

    // Helper to keep renderData clean
    private void addEmptyLabel(String text) {
        LabelComponent emptyLabel = UIComponents.label(Component.literal(text));
        emptyLabel.horizontalSizing(Sizing.fill(100));
        emptyLabel.horizontalTextAlignment(HorizontalAlignment.CENTER);
        emptyLabel.margins(Insets.top(10));
        this.friendsListContainer.child(emptyLabel);
    }

    private FlowLayout createFriendRow(FriendsModels.FriendEntry friend) {
        var row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(25));
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.gap(8);

        // Online/Offline Dot
        int statusColor = friend.presence().equals("OFFLINE") ? 0xFFFF0000 : 0xFF00FF00;
        row.child(UIComponents.label(Component.literal("●")).color(Color.ofArgb(statusColor)));

        row.child(UIComponents.label(Component.literal(friend.name())));

        return row;
    }

    private FlowLayout createRequestRow(FriendsModels.RequestEntry req) {
        var row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(25));
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.padding(Insets.horizontal(8));

        // 1. The Name Label
        // We limit this to 80% so it cannot push the button group off-screen
        LabelComponent nameLabel = UIComponents.label(Component.literal(req.name()));
        nameLabel.horizontalSizing(Sizing.fill(80));
        nameLabel.verticalSizing(Sizing.content());
        row.child(nameLabel);

        // 2. The Button Group
        // This will now sit comfortably in the remaining 20% of the row
        var buttonGroup = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        buttonGroup.verticalAlignment(VerticalAlignment.CENTER);

        var acceptBtn = UIComponents.button(Component.literal("✔"), b -> {
            java.util.concurrent.CompletableFuture.runAsync(() ->
                    FriendsApi.acceptRequest(this.minecraft.player.getUUID().toString(), req.uuid())
            ).thenRun(this::loadDataAndRefresh);
        }).sizing(Sizing.fixed(20));

        buttonGroup.child(acceptBtn);
        row.child(buttonGroup);

        return row;
    }
}