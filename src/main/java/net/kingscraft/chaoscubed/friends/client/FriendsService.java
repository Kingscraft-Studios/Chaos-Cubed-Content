package net.kingscraft.chaoscubed.friends.client;

import com.google.gson.Gson;
import net.kingscraft.chaoscubed.friends.api.FriendRecord;
import net.kingscraft.chaoscubed.friends.api.Friends;
import net.kingscraft.chaoscubed.friends.api.FriendsResponse;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public final class FriendsService {
    private static final Gson GSON = new Gson();
    private static volatile FriendsResponse cache;
    private static volatile long lastRefreshMs;
    private static final AtomicInteger incomingCount = new AtomicInteger();

    private FriendsService() {}

    public static CompletableFuture<FriendsResponse> refresh(String uuid) {
        return CompletableFuture.supplyAsync(() -> Friends.getFriends(uuid))
                .thenApply(raw -> raw == null || raw.isBlank() ? null : GSON.fromJson(raw, FriendsResponse.class))
                .whenComplete((data, error) -> {
                    if (error == null && data != null) {
                        cache = data;
                        lastRefreshMs = System.currentTimeMillis();
                        incomingCount.set(data.incomingBadge);
                    }
                });
    }

    public static FriendsResponse cache() {
        return cache;
    }

    public static int incomingCount() {
        return incomingCount.get();
    }

    public static long lastRefreshMs() {
        return lastRefreshMs;
    }
}
