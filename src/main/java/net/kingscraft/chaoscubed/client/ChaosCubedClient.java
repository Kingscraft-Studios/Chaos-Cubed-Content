package net.kingscraft.chaoscubed.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.kingscraft.chaoscubed.entity.ModEntities;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeModel;
import net.kingscraft.chaoscubed.entity.client.sulfurcube.SulfurCubeRenderer;
import net.kingscraft.chaoscubed.particles.ModParticles;
import net.kingscraft.chaoscubed.particles.SulfurGooProvider;
import net.minecraft.client.renderer.entity.EntityRenderers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class ChaosCubedClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        VersionChecker.init();
        EntityModelLayerRegistry.registerModelLayer(SulfurCubeModel.LAYER, SulfurCubeModel::createBodyLayer);
        EntityRenderers.register(ModEntities.SULFUR_CUBE, SulfurCubeRenderer::new);
        ParticleFactoryRegistry.getInstance().register(ModParticles.SULFUR_GOO, SulfurGooProvider::new);

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
