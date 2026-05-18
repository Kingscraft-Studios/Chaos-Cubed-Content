package net.kingscraft.chaoscubed.friends.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;
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

    private static Runnable onFriendRequest;
    private static Runnable onFriendAccepted;
    private static Runnable onFriendRemoved;

    public static void setOnFriendRequest(Runnable r) { onFriendRequest = r; }
    public static void setOnFriendAccepted(Runnable r) { onFriendAccepted = r; }
    public static void setOnFriendRemoved(Runnable r) { onFriendRemoved = r; }

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
        System.out.println("[FriendsWS] Stopped");
    }

    // ──────────────────────────────────────────────
    // connection
    // ──────────────────────────────────────────────

    private static void connect() {
        if (ws != null && !ws.isOutputClosed()) return;
        System.out.println("[FriendsWS] Connecting...");

        HttpClient.newHttpClient().newWebSocketBuilder()
                .buildAsync(URI.create(WS_URL), new GameSocketListener())
                .thenAccept(webSocket -> {
                    ws = webSocket;
                    reconnectAttempts = 0;
                    System.out.println("[FriendsWS] Connected, logging in...");
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
        System.out.println("[FriendsWS] Reconnecting in " + delay + "s (attempt " + reconnectAttempts + ")");
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
            System.out.println("[FriendsWS] << " + msg);
            Minecraft.getInstance().execute(() -> {
                try {
                    JsonObject res = GSON.fromJson(msg, JsonObject.class);
                    String type = res.has("type") ? res.get("type").getAsString() : "";

                    switch (type) {
                        case "connected" -> {
                            // connection acknowledged, login will follow
                        }
                        case "login_ok" -> {
                            System.out.println("[FriendsWS] Login OK for " + playerUUID);
                        }
                        case "friend_request" -> {
                            if (onFriendRequest != null) onFriendRequest.run();
                        }
                        case "friend_accepted" -> {
                            if (onFriendAccepted != null) onFriendAccepted.run();
                        }
                        case "friend_removed" -> {
                            if (onFriendRemoved != null) onFriendRemoved.run();
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
            System.out.println("[FriendsWS] Disconnected" + (reason.isEmpty() ? "" : ": " + reason));
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
