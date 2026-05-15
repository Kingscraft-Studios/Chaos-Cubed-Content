package net.kingscraft.chaoscubed.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.kingscraft.chaoscubed.entity.ModEntities;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeModel;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeRenderer;
import net.kingscraft.chaoscubed.friends.api.FriendsApi;
import net.kingscraft.chaoscubed.friends.ui.FriendsScreen;
import net.kingscraft.chaoscubed.particles.ModParticles;
import net.kingscraft.chaoscubed.particles.SulfurGooProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderers;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CompletableFuture;

public class ChaosCubedClient implements ClientModInitializer {
    public static KeyMapping openFriendKey;

    @Override
    public void onInitializeClient() {
        VersionChecker.init();
        EntityModelLayerRegistry.registerModelLayer(SulfurCubeModel.LAYER, SulfurCubeModel::createBodyLayer);
        EntityRenderers.register(ModEntities.SULFUR_CUBE, SulfurCubeRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SULFUR_GOO, SulfurGooProvider::new);

        openFriendKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.chaos_cubed.open_friends",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_O,
                KeyMapping.Category.MULTIPLAYER
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openFriendKey.consumeClick()) {
                client.setScreen(new FriendsScreen());
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player == null) return;

            String uuid = client.player.getUUID().toString();
            String name = client.player.getName().getString();

            System.out.println("[FriendsDebug] Checking database for: " + name);

            java.util.concurrent.CompletableFuture.supplyAsync(() -> FriendsApi.getFriends(uuid))
                    .thenAccept(response -> {
                        // If the user doesn't exist, the Worker's error JSON
                        // causes the 'uuid' or 'friends' fields in our record to be null.
                        if (response == null || response.uuid()  == null) {
                            System.out.println("[FriendsDebug] Profile missing. Registering...");
                            FriendsApi.registerPlayer(uuid, name);
                        } else {
                            System.out.println("[FriendsDebug] Profile found for " + response.name());
                        }
                    }).exceptionally(ex -> {
                        // This catches 404s if your API wrapper throws an exception on non-200 codes
                        System.out.println("[FriendsDebug] Request failed (likely 404). Registering...");
                        FriendsApi.registerPlayer(uuid, name);
                        return null;
                    });
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            CompletableFuture.runAsync(() -> {
                client.execute(() -> {
                    if (client.player != null) {
                        VersionChecker.sendUpdateMessage(client.player);
                    }
                });
            });
        });
    }
}
