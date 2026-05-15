package net.kingscraft.chaoscubed.friends.api;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.kingscraft.chaoscubed.friends.structure.FriendsModels;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class FriendsApi {
    private static final String BASE = "https://chaoscubedcontent.vibeverse-social.workers.dev";
    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Gson GSON = new Gson();

    // GET /friends
    public static FriendsModels.FriendsListResponse getFriends(String uuid) {
        return fetchGet(BASE + "/friends?uuid=" + uuid, FriendsModels.FriendsListResponse.class);
    }

    // POST /register
    public static FriendsModels.ActionResponse registerPlayer(String uuid, String name) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid);
        json.addProperty("name", name);
        return fetchPost(BASE + "/register", json, FriendsModels.ActionResponse.class);
    }

    // POST /friend/request/name
    public static FriendsModels.ActionResponse sendFriendRequestByName(String fromUuid, String targetName) {
        JsonObject json = new JsonObject();
        json.addProperty("from", fromUuid);
        json.addProperty("toName", targetName);
        return fetchPost(BASE + "/friend/request/name", json, FriendsModels.ActionResponse.class);
    }

    // POST /friend/accept
    public static FriendsModels.ActionResponse acceptRequest(String uuid, String friendUuid) {
        JsonObject json = new JsonObject();
        json.addProperty("uuid", uuid);
        json.addProperty("friend", friendUuid);
        return fetchPost(BASE + "/friend/accept", json, FriendsModels.ActionResponse.class);
    }

    // --- GENERIC FETCH HELPERS ---

    private static <T> T fetchGet(String url, Class<T> clazz) {
        try {
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return GSON.fromJson(response.body(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static <T> T fetchPost(String url, JsonObject body, Class<T> clazz) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body.toString()))
                    .build();
            HttpResponse<String> response = CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
            return GSON.fromJson(response.body(), clazz);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}