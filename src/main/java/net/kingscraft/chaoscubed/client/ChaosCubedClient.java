package net.kingscraft.chaoscubed.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.kingscraft.chaoscubed.friends.api.Friends;
import net.kingscraft.chaoscubed.entity.ModEntities;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeModel;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeRenderer;
import net.kingscraft.chaoscubed.friends.client.FriendsService;
import net.kingscraft.chaoscubed.friends.ui.FriendsScreen;
import net.kingscraft.chaoscubed.particles.ModParticles;
import net.kingscraft.chaoscubed.particles.SulfurGooProvider;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.network.chat.Component;
import org.lwjgl.glfw.GLFW;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ChaosCubedClient implements ClientModInitializer {
    public static KeyMapping openFriendKey;
    private static boolean registeredThisSession;
    private static long nextFriendsPollMs;

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

            if (client.player == null) {
                return;
            }

            long now = System.currentTimeMillis();
            boolean friendsOpen = client.screen instanceof FriendsScreen;
            long interval = friendsOpen ? 60_000L : 300_000L;
            if (now >= nextFriendsPollMs) {
                nextFriendsPollMs = now + interval;
                FriendsService.refresh(client.player.getStringUUID());
            }
        });

        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) -> {
            if (client.player == null || registeredThisSession) {
                return;
            }

            registeredThisSession = true;
            CompletableFuture.runAsync(() -> {
                Friends.ensurePlayer(
                        client.player.getStringUUID(),
                        client.player.getName().getString()
                );
            }).thenRunAsync(() -> CompletableFuture.delayedExecutor(5, TimeUnit.SECONDS).execute(() ->
                    client.execute(() -> {
                        if (client.player != null) {
                            VersionChecker.sendUpdateMessage(client.player);
                        }
                    })
            ));
        });

        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> registeredThisSession = false);
    }
}
