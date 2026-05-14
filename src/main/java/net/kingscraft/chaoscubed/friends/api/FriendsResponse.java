package net.kingscraft.chaoscubed.friends.api;

import java.util.List;

public class FriendsResponse {
    public String uuid;
    public String name;
    public String presence;
    public List<FriendRecord> friends;
    public List<FriendRecord> requests;
    public List<FriendRecord> outgoingRequests;
    public int incomingBadge;
}
