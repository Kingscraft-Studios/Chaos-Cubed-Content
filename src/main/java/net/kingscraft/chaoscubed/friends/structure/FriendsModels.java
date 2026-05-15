package net.kingscraft.chaoscubed.friends.structure;

import java.util.List;

public class FriendsModels {

    // Matches the /friends GET response
    public record FriendsListResponse(
            String uuid,
            String name,
            String presence,
            List<FriendEntry> friends,
            List<RequestEntry> requests, // incoming
            List<RequestEntry> outgoingRequests,
            int incomingBadge
    ) {}

    // Used for friends list
    public record FriendEntry(
            String uuid,
            String name,
            String presence // "OFFLINE", "ONLINE", "IN_WORLD", "JOINABLE_WORLD"
    ) {}

    // Used for incoming/outgoing requests
    public record RequestEntry(
            String uuid,
            String name,
            String direction, // "incoming" or "outgoing"
            String presence
    ) {}

    // Matches the { "ok": true } or { "created": true } responses
    public record ActionResponse(
            boolean ok,
            boolean created,
            String error // for 400/404/409 errors
    ) {}
}