package net.kingscraft.chaoscubed.friends.api;

import com.google.gson.JsonObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Friends {

    private static final String BASE =
            "https://chaoscubedcontent.vibeverse-social.workers.dev";

    private static final HttpClient client = HttpClient.newHttpClient();

    public static String getFriends(String uuid) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/friends?uuid=" + uuid))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String registerPlayer(String uuid, String name) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", uuid);
            json.addProperty("name", name);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/register"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String ensurePlayer(String uuid, String name) {
        return registerPlayer(uuid, name);
    }

    public static String ping() {
        try {

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sendFriendRequest(String from, String to) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("from", from);
            json.addProperty("to", to);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/friend/request"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String sendFriendRequestByName(String fromUuid, String targetProfileName) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("from", fromUuid);
            json.addProperty("toName", targetProfileName);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/friend/request/name"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String acceptFriendRequest(String uuid, String friend) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", uuid);
            json.addProperty("friend", friend);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/friend/accept"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String declineFriendRequest(String uuid, String friend) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", uuid);
            json.addProperty("friend", friend);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/friend/decline"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String cancelFriendRequest(String uuid, String friend) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", uuid);
            json.addProperty("friend", friend);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/friend/cancel"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String removeFriend(String uuid, String friend) {
        try {
            JsonObject json = new JsonObject();
            json.addProperty("uuid", uuid);
            json.addProperty("friend", friend);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE + "/friend/remove"))
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json.toString()))
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
