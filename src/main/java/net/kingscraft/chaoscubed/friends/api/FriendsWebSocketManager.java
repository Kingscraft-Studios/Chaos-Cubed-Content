package net.kingscraft.chaoscubed.friends.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.kingscraft.chaoscubed.client.ChaosCubedClient;
import net.minecraft.client.Minecraft;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
import java.util.function.BiConsumer;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FriendsWebSocketManager {
    private static final String WS_URL = "wss://chaoscubedcontent.vibeverse-social.workers.dev/ws";
    private static final Gson GSON = new Gson();
    private static final int MAX_RECONNECT_DELAY = 30;

    private static WebSocket ws;
    private static ScheduledExecutorService pingExecutor;
    private static ScheduledExecutorService reconnectExecutor;
    private static String playerUUID;
    private static boolean shouldReconnect = false;
    private static int reconnectAttempts = 0;

    private static BiConsumer<String, String> onFriendRequest;
    private static BiConsumer<String, String> onFriendAccepted;
    private static BiConsumer<String, String> onFriendRemoved;
    private static BiConsumer<String, String> onFriendRejected;

    public static void setOnFriendRequest(BiConsumer<String, String> c) { onFriendRequest = c; }
    public static void setOnFriendAccepted(BiConsumer<String, String> c) { onFriendAccepted = c; }
    public static void setOnFriendRemoved(BiConsumer<String, String> c) { onFriendRemoved = c; }
    public static void setOnFriendRejected(BiConsumer<String, String> c) { onFriendRejected = c; }

    /** Start or re-login the WebSocket. Safe to call repeatedly. */
    public static void start(String uuid) {
        playerUUID = uuid;
        shouldReconnect = true;

        if (ws != null && !ws.isOutputClosed()) {
            // Already connected — just re-login with new UUID
            sendLoginPacket();
            return;
        }

        reconnectAttempts = 0;
        connect();
    }

    /** Full stop — closes socket, cancels reconnection. */
    public static void stop() {
        shouldReconnect = false;
        cancelReconnect();
        shutdownPing();
        if (ws != null && !ws.isOutputClosed()) {
            ws.sendClose(WebSocket.NORMAL_CLOSURE, "Client stopping");
        }
        ws = null;
        playerUUID = null;
    }

    // ──────────────────────────────────────────────
    // connection
    // ──────────────────────────────────────────────

    private static void connect() {
        if (ws != null && !ws.isOutputClosed()) return;

        HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), new GameSocketListener())
                .thenAccept(webSocket -> {
                    ws = webSocket;
                    reconnectAttempts = 0;
                    sendLoginPacket();
                    startPingLoop();
                }).exceptionally(throwable -> {
                    System.err.println("[FriendsWS] Connect failed: " + throwable.getMessage());
                    scheduleReconnect();
                    return null;
                });
    }

    // ──────────────────────────────────────────────
    // protocol
    // ──────────────────────────────────────────────

    private static void sendLoginPacket() {
        if (ws == null || ws.isOutputClosed() || playerUUID == null) return;
        JsonObject login = new JsonObject();
        login.addProperty("type", "login");
        login.addProperty("uuid", playerUUID);
        ws.sendText(GSON.toJson(login), true);
    }

    // ──────────────────────────────────────────────
    // ping
    // ──────────────────────────────────────────────

    private static void startPingLoop() {
        shutdownPing();
        pingExecutor = Executors.newSingleThreadScheduledExecutor();
        pingExecutor.scheduleAtFixedRate(() -> {
            if (ws != null && !ws.isOutputClosed() && playerUUID != null) {
                JsonObject ping = new JsonObject();
                ping.addProperty("type", "ping");
                ping.addProperty("uuid", playerUUID);
                ws.sendText(GSON.toJson(ping), true);
            }
        }, 10, 10, TimeUnit.SECONDS);
    }

    private static void shutdownPing() {
        if (pingExecutor != null) {
            pingExecutor.shutdownNow();
            pingExecutor = null;
        }
    }

    // ──────────────────────────────────────────────
    // reconnection (exponential backoff)
    // ──────────────────────────────────────────────

    private static void scheduleReconnect() {
        if (!shouldReconnect) return;
        if (reconnectExecutor == null) {
            reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
        }
        int delay = Math.min(1 << reconnectAttempts, MAX_RECONNECT_DELAY);
        reconnectAttempts++;
        ChaosCubedClient.LOGGER.info("[FriendsWS] Reconnecting in {}s (attempt {})", delay, reconnectAttempts);
        reconnectExecutor.schedule(FriendsWebSocketManager::connect, delay, TimeUnit.SECONDS);
    }

    private static void cancelReconnect() {
        if (reconnectExecutor != null) {
            reconnectExecutor.shutdownNow();
            reconnectExecutor = null;
        }
    }

    // ──────────────────────────────────────────────
    // listener
    // ──────────────────────────────────────────────

    private static class GameSocketListener implements WebSocket.Listener {
        @Override
        public CompletionStage<?> onText(WebSocket ws, CharSequence data, boolean last) {
            String msg = data.toString();
            Minecraft.getInstance().execute(() -> {
                try {
                    JsonObject res = GSON.fromJson(msg, JsonObject.class);
                    String type = res.has("type") ? res.get("type").getAsString() : "";

                    switch (type) {
                        case "connected" -> {
                        }
                        case "login_ok" -> {
                        }
                        case "friend_request" -> {
                            String from = res.has("from") ? res.get("from").getAsString() : "";
                            String name = res.has("name") ? res.get("name").getAsString() : from;
                            if (onFriendRequest != null) onFriendRequest.accept(from, name);
                        }
                        case "friend_accepted" -> {
                            String from = res.has("from") ? res.get("from").getAsString() : "";
                            String name = res.has("name") ? res.get("name").getAsString() : from;
                            if (onFriendAccepted != null) onFriendAccepted.accept(from, name);
                        }
                        case "friend_removed" -> {
                            String from = res.has("from") ? res.get("from").getAsString() : "";
                            String name = res.has("name") ? res.get("name").getAsString() : from;
                            if (onFriendRemoved != null) onFriendRemoved.accept(from, name);
                        }
                        case "friend_rejected" -> {
                            String from = res.has("from") ? res.get("from").getAsString() : "";
                            String name = res.has("name") ? res.get("name").getAsString() : from;
                            if (onFriendRejected != null) onFriendRejected.accept(from, name);
                        }
                    }
                } catch (Exception e) {
                    System.err.println("[FriendsWS] Failed to parse message: " + e.getMessage());
                }
            });
            return WebSocket.Listener.super.onText(ws, data, last);
        }

        @Override
        public CompletionStage<?> onClose(WebSocket ws, int statusCode, String reason) {
            ChaosCubedClient.LOGGER.info("[FriendsWS] Disconnected{}", reason.isEmpty() ? "" : ": " + reason);
            shutdownPing();
            scheduleReconnect();
            return WebSocket.Listener.super.onClose(ws, statusCode, reason);
        }

        @Override
        public void onError(WebSocket ws, Throwable error) {
            System.err.println("[FriendsWS] Error: " + error.getMessage());
            shutdownPing();
            scheduleReconnect();
            WebSocket.Listener.super.onError(ws, error);
        }
    }
}
