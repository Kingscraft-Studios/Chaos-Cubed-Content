package net.kingscraft.chaoscubed.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ChaosCubedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VersionChecker.init();

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player != null) {
                // Schedule the task to run after 5 seconds
                CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() -> {
                    // We must use client.execute() to ensure the message
                    // is sent on the main game thread to prevent crashes
                    client.execute(() -> {
                        if (client.player != null) {
                            VersionChecker.sendUpdateMessage(client.player);
                        }
                    });
                });
            }
        });
    }
}
