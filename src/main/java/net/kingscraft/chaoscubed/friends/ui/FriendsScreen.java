package net.kingscraft.chaoscubed.friends.ui;

import io.wispforest.owo.ui.base.BaseOwoScreen;
import io.wispforest.owo.ui.component.ButtonComponent;
import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.component.UIComponents;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.UIContainers;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.NinePatchTexture;
import net.kingscraft.chaoscubed.client.ChaosCubedClient;
import net.kingscraft.chaoscubed.friends.api.FriendsApi;
import net.kingscraft.chaoscubed.friends.api.FriendsWebSocketManager;
import net.kingscraft.chaoscubed.friends.structure.FriendsModels;
import net.kingscraft.chaoscubed.friends.ui.toasts.SkinCache;
import net.kingscraft.chaoscubed.friends.ui.toasts.ToastManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class FriendsScreen extends BaseOwoScreen<FlowLayout> {

    private FlowLayout contentPanel;
    private FlowLayout friendsTab;
    private FlowLayout requestsTab;
    private boolean isFriendsTab = true;
    private FlowLayout friendsListContainer;

    private FriendsModels.FriendsListResponse friendsData;
    private FriendsModels.RequestsResponse requestsData;

    private static final Identifier ACCEPT_TEX = Identifier.fromNamespaceAndPath("chaos_cubed", "textures/gui/friends/accept.png");
    private static final Identifier ACCEPT_HIGHLIGHTED_TEX = Identifier.fromNamespaceAndPath("chaos_cubed", "textures/gui/friends/accept_highlighted.png");
    private static final Identifier SEND_TEX = Identifier.fromNamespaceAndPath("chaos_cubed", "textures/gui/friends/draft_report.png");

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
        tabs.gap(4);

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
        contentPanel.margins(Insets.top(-1));
        contentPanel.horizontalAlignment(HorizontalAlignment.CENTER);

        rootComponent.child(contentPanel);

        // --- 3. WIRE WEBSOCKET EVENTS TO AUTO-REFRESH + TOASTS ---
        FriendsWebSocketManager.setOnFriendRequest(onFriendRequest);
        FriendsWebSocketManager.setOnFriendAccepted(onFriendAccepted);
        FriendsWebSocketManager.setOnFriendRemoved(onFriendRemoved);

        refreshUI();
        loadDataAndRefresh();
    }

    private final BiConsumer<String, String> onFriendRequest = (uuid, name) -> {
        if (this.minecraft == null) return;
        SkinCache.preload(uuid, name);
        this.minecraft.execute(() -> {
            ToastManager.showToast(uuid, name, name + " sent you a request");
            loadDataAndRefresh();
        });
    };

    private final BiConsumer<String, String> onFriendAccepted = (uuid, name) -> {
        if (this.minecraft == null) return;
        SkinCache.preload(uuid, name);
        this.minecraft.execute(() -> {
            ToastManager.showToast(uuid, name, "You are now friends with " + name);
            loadDataAndRefresh();
        });
    };

    private final BiConsumer<String, String> onFriendRemoved = (uuid, name) -> {
        if (this.minecraft == null) return;
        SkinCache.preload(uuid, name);
        this.minecraft.execute(() -> {
            ToastManager.showToast(uuid, name, name + " is no longer your friend");
            loadDataAndRefresh();
        });
    };

    private void refreshUI() {
        updateTabVisuals();
        rebuildContent();
    }

    private FlowLayout createTab(String text) {
        var container = UIContainers.verticalFlow(Sizing.fixed(98), Sizing.fixed(22));
        container.horizontalAlignment(HorizontalAlignment.CENTER);
        container.verticalAlignment(VerticalAlignment.CENTER);
        container.cursorStyle(CursorStyle.HAND);
        container.surface(Surface.PANEL);
        container.child(UIComponents.label(Component.literal(text)).shadow(true));

        var line = UIContainers.horizontalFlow(Sizing.fixed(80), Sizing.fixed(1));
        line.positioning(Positioning.relative(50, 100));
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

            TextBoxComponent searchBar = UIComponents.textBox(Sizing.expand(100));
            searchBar.setMaxLength(16);
            searchBar.setSuggestion("Enter Profile Name");

            searchBar.onChanged().subscribe(value -> {
                if (!value.isEmpty()) {
                    searchBar.setSuggestion("");
                } else {
                    searchBar.setSuggestion("Enter Profile Name");
                }
            });

            searchRow.child(searchBar);

            var sendBtn = UIComponents.button(Component.literal(""), b -> {
                String targetName = searchBar.getValue();
                if (!targetName.isEmpty()) {
                    String myUuid = Minecraft.getInstance().getUser().getProfileId().toString();
                    CompletableFuture.supplyAsync(() -> FriendsApi.sendFriendRequestByName(myUuid, targetName))
                            .thenAccept(res -> {
                                this.minecraft.execute(() -> {
                                    if (res == null || !res.ok()) {
                                        String err = res != null ? res.error() : "Network";
                                        ChaosCubedClient.LOGGER.error("[FriendsUI] Send request failed: {}", err);
                                        ToastManager.showErrorToast(err);
                                        return;
                                    }
                                    searchBar.text("");
                                    loadDataAndRefresh();
                                    ToastManager.showToast("Sent request to " + targetName);
                                });
                            });
                }
            }).renderer((graphics, button, delta) -> {
                Identifier bg = button.active()
                        ? button.isHoveredOrFocused() ? ButtonComponent.HOVERED_TEXTURE : ButtonComponent.ACTIVE_TEXTURE
                        : ButtonComponent.DISABLED_TEXTURE;
                NinePatchTexture.draw(bg, graphics, button.getX(), button.getY(), button.getWidth(), button.getHeight());
                int pad = (button.getWidth() - 15) / 2;
                graphics.blit(RenderPipelines.GUI_TEXTURED, SEND_TEX,
                        button.getX() + pad, button.getY() + pad,
                        0.0F, 0.0F,
                        15, 15,
                        15, 15,
                        0xFFFFFFFF);
            }).sizing(Sizing.fixed(20));

            sendBtn.margins(Insets.right(2));
            searchRow.child(sendBtn);
            contentPanel.child(searchRow);

            // --- NAME LINE ---
            var nameLine = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
            nameLine.verticalAlignment(VerticalAlignment.CENTER);
            nameLine.margins(Insets.vertical(5));
            nameLine.child(UIComponents.label(Component.literal("My profile: ")).color(Color.ofRgb(0x888888)));

            String playerName = Minecraft.getInstance().getUser().getName();
            nameLine.child(UIComponents.label(Component.literal(playerName)));
            contentPanel.child(nameLine);

            // --- SEPARATOR ---
            contentPanel.child(UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(1))
                    .surface(Surface.flat(0xFF444444))
                    .margins(Insets.bottom(4)));
        }

        // --- SCROLLABLE LIST ---
        var list = UIContainers.verticalFlow(Sizing.fill(100), Sizing.content());
        this.friendsListContainer = list;

        contentPanel.child(UIContainers.verticalScroll(Sizing.fill(100), Sizing.fill(100), list));
    }

    private void loadDataAndRefresh() {
        if (Minecraft.getInstance().getUser().getName() == null) return;
        String uuid = Minecraft.getInstance().getUser().getProfileId().toString();

        CompletableFuture.supplyAsync(() -> FriendsApi.getFriends(uuid))
                .thenAccept(response -> {
                    if (response != null) {
                        this.friendsData = response;
                        for (var f : response.friends()) {
                            SkinCache.preload(f.uuid(), f.name());
                        }
                    }
                });

        CompletableFuture.supplyAsync(() -> FriendsApi.getPendingRequests(uuid))
                .thenAccept(response -> {
                    if (response != null) {
                        this.requestsData = response;
                        for (var r : response.all()) {
                            SkinCache.preload(r.uuid(), r.name());
                        }
                        this.minecraft.execute(() -> {
                            updateBadge();
                            renderData();
                        });
                    }
                });
    }

    private void updateBadge() {
        LabelComponent rLabel = (LabelComponent) requestsTab.children().get(0);
        int count = requestsData != null ? requestsData.incomingBadge() : 0;
        rLabel.text(Component.literal("Requests (" + count + ")"));
    }

    private void renderData() {
        if (this.friendsListContainer == null) return;
        this.friendsListContainer.clearChildren();

        if (isFriendsTab) {
            List<FriendsModels.FriendEntry> friends = friendsData != null ? friendsData.friends() : List.of();

            if (!friends.isEmpty()) {
                for (var friend : friends) {
                    this.friendsListContainer.child(createFriendRow(friend));
                }
            } else {
                this.addEmptyLabel("No friends yet!");
            }
        } else {
            List<FriendsModels.RequestEntry> requests = requestsData != null ? requestsData.all() : List.of();

            if (!requests.isEmpty()) {
                for (var req : requests) {
                    this.friendsListContainer.child(createRequestRow(req));
                }
            } else {
                this.addEmptyLabel("No pending requests.");
            }
        }
    }

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

        int statusColor = friend.presence().equals("OFFLINE") ? 0xFFFF0000 : 0xFF00FF00;
        row.child(UIComponents.label(Component.literal("●")).color(Color.ofArgb(statusColor)));

        row.child(UIComponents.label(Component.literal(friend.name())));

        return row;
    }

    private FlowLayout createRequestRow(FriendsModels.RequestEntry req) {
        var row = UIContainers.horizontalFlow(Sizing.fill(100), Sizing.fixed(25));
        row.verticalAlignment(VerticalAlignment.CENTER);
        row.padding(Insets.horizontal(8));

        LabelComponent nameLabel = UIComponents.label(Component.literal(req.name()));
        nameLabel.horizontalSizing(Sizing.fill(80));
        nameLabel.verticalSizing(Sizing.content());
        row.child(nameLabel);

        var buttonGroup = UIContainers.horizontalFlow(Sizing.content(), Sizing.content());
        buttonGroup.verticalAlignment(VerticalAlignment.CENTER);

        var acceptBtn = UIComponents.button(Component.literal(""), b -> {
            String myUuid = Minecraft.getInstance().getUser().getProfileId().toString();
            CompletableFuture.supplyAsync(() -> FriendsApi.acceptRequest(myUuid, req.uuid()))
                    .thenAccept(res -> this.minecraft.execute(() -> {
                        if (res == null || !res.ok()) {
                            String err = res != null ? res.error() : "Network";
                            ChaosCubedClient.LOGGER.error("[FriendsUI] Accept failed: {}", err);
                            ToastManager.showErrorToast(err);
                            return;
                        }
                        loadDataAndRefresh();
                    }));
        }).renderer((graphics, button, delta) -> {
            Identifier bg = button.active()
                    ? button.isHoveredOrFocused() ? ButtonComponent.HOVERED_TEXTURE : ButtonComponent.ACTIVE_TEXTURE
                    : ButtonComponent.DISABLED_TEXTURE;
            NinePatchTexture.draw(bg, graphics, button.getX(), button.getY(), button.getWidth(), button.getHeight());
            Identifier tex = button.isHoveredOrFocused() ? ACCEPT_HIGHLIGHTED_TEX : ACCEPT_TEX;
            int pad = (button.getWidth() - 18) / 2;
            graphics.blit(RenderPipelines.GUI_TEXTURED, tex,
                    button.getX() + pad, button.getY() + pad,
                    0.0F, 0.0F,
                    18, 18,
                    18, 18,
                    0xFFFFFFFF);
        }).sizing(Sizing.fixed(20));

        buttonGroup.child(acceptBtn);
        row.child(buttonGroup);

        return row;
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        if (this.minecraft != null && this.minecraft.level == null) {
            this.renderPanorama(guiGraphics, delta);
        }
        super.renderBackground(guiGraphics, mouseX, mouseY, delta);
    }
}
