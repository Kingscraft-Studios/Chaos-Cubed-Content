package net.kingscraft.chaoscubed.friends.structure;

import java.util.ArrayList;
import java.util.List;

public class FriendsModels {

    // Matches GET /friends response
    public record FriendsListResponse(
            String uuid,
            List<FriendEntry> friends
    ) {
        public FriendsListResponse {
            if (friends == null) friends = new ArrayList<>();
        }

        public int incomingBadge() {
            return 0;
        }
    }

    // Matches GET /friend/requests response
    public record RequestsResponse(
            String uuid,
            List<RequestEntry> incoming,
            List<RequestEntry> outgoing
    ) {
        public RequestsResponse {
            if (incoming == null) incoming = new ArrayList<>();
            if (outgoing == null) outgoing = new ArrayList<>();
        }

        public int incomingBadge() {
            return incoming.size();
        }

        /** Shorthand to merge both directions for UI display */
        public List<RequestEntry> all() {
            var all = new ArrayList<RequestEntry>();
            for (var i : incoming) {
                all.add(new RequestEntry(i.uuid(), i.name(), "incoming", i.presence()));
            }
            for (var o : outgoing) {
                all.add(new RequestEntry(o.uuid(), o.name(), "outgoing", o.presence()));
            }
            return all;
        }
    }

    // Used for friends list rendering
    public record FriendEntry(
            String uuid,
            String name,
            String presence // "OFFLINE", "ONLINE", "IN_WORLD", "JOINABLE_WORLD"
    ) {}

    // Used for incoming/outgoing request objects
    public record RequestEntry(
            String uuid,
            String name,
            String direction, // "incoming" or "outgoing"
            String presence
    ) {}

    // Matches { "ok": true } or { "ok": false, "error": "msg" }
    public record ActionResponse(
            boolean ok,
            boolean created,
            String error
    ) {}
}
