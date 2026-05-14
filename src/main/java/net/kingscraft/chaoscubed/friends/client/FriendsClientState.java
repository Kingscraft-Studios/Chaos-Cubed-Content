package net.kingscraft.chaoscubed.friends.client;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import net.kingscraft.chaoscubed.ChaosCubed;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FriendsClientState {
    private static final Gson GSON = new Gson();
    private static final Path FILE = Path.of("config", ChaosCubed.MODID, "friends.json");

    private static State state = load();

    private FriendsClientState() {}

    public static boolean enabled() {
        return state.enabled;
    }

    public static boolean allowRequests() {
        return state.allowRequests;
    }

    public static boolean firstRun() {
        return !state.seenPrompt;
    }

    public static void acceptPrompt() {
        state.seenPrompt = true;
        save();
    }

    public static void setEnabled(boolean enabled) {
        state.enabled = enabled;
        state.seenPrompt = true;
        save();
    }

    public static void setAllowRequests(boolean allowRequests) {
        state.allowRequests = allowRequests;
        save();
    }

    private static State load() {
        try {
            if (Files.exists(FILE)) {
                return GSON.fromJson(Files.readString(FILE), State.class);
            }
        } catch (Exception ignored) {
        }
        return new State();
    }

    private static void save() {
        try {
            Files.createDirectories(FILE.getParent());
            Files.writeString(FILE, GSON.toJson(state), StandardCharsets.UTF_8);
        } catch (IOException e) {
            ChaosCubed.LOGGER.warn("Failed to save friends state: {}", e.toString());
        }
    }

    private static final class State {
        boolean enabled = true;
        boolean seenPrompt = false;
        boolean allowRequests = true;
        @SerializedName("microsoftSafetyLinkSeen")
        boolean microsoftSafetyLinkSeen = false;
    }
}
