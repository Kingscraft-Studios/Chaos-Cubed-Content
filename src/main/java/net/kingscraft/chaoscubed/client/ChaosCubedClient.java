package net.kingscraft.chaoscubed.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.kingscraft.chaoscubed.entity.ModEntities;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeModel;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeRenderer;
import net.kingscraft.chaoscubed.friends.api.FriendsApi;
import net.kingscraft.chaoscubed.friends.api.FriendsWebSocketManager;
import net.kingscraft.chaoscubed.friends.ui.FriendsScreen;
import net.kingscraft.chaoscubed.particles.ModParticles;
import net.kingscraft.chaoscubed.particles.SulfurGooProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaosCubedClient implements ClientModInitializer {
    public static final String MODID = "chaos_cubed";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);
    public static KeyMapping openFriendKey;
    public static final KeyMapping.Category FRIENDS_CATEGORY = KeyMapping.Category.register(
            Identifier.fromNamespaceAndPath(MODID, "friends")
    );

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
                FRIENDS_CATEGORY
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openFriendKey.consumeClick()) {
                client.setScreen(new FriendsScreen());
            }
        });

        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            Minecraft mc = Minecraft.getInstance();

            User session = mc.getUser();
            if (session != null) {
                String name = session.getName();
                String UUID = session.getProfileId().toString();

                java.util.concurrent.CompletableFuture.supplyAsync(() -> FriendsApi.registerPlayer(UUID, name))
                        .thenAccept(res -> {
                            LOGGER.info("[FriendsWS] Registered: {}", name);
                            FriendsWebSocketManager.start(UUID);
                        }).exceptionally(ex -> {
                            LOGGER.error("[FriendsWS] Registration failed: {}", ex.getMessage());
                            return null;
                        });
            }
        });

        // Version check on join
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            java.util.concurrent.CompletableFuture.runAsync(() -> {
                client.execute(() -> {
                    if (client.player != null) {
                        VersionChecker.sendUpdateMessage(client.player);
                    }
                });
            });
        });

        // Stop WS when the game closes (not on world leave)
        ClientLifecycleEvents.CLIENT_STOPPING.register(client -> {
            FriendsWebSocketManager.stop();
        });
    }
}
