package net.kingscraft.chaoscubed;

import net.fabricmc.api.ModInitializer;
import net.kingscraft.chaoscubed.blocks.ModBlocks;
import net.kingscraft.chaoscubed.entity.ModEntities;
import net.kingscraft.chaoscubed.items.ModItems;
import net.kingscraft.chaoscubed.particles.ModParticles;
import net.kingscraft.chaoscubed.sounds.ModSounds;
import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaosCubed implements ModInitializer {
    public static final String MODID = "chaos_cubed";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        try {
            ModBlocks.registerModBlocks();
            ModItems.registerModItems();
            ModEntities.registerModEntities();
            WorldGeneration.register();
            ModParticles.registerParticles();
            ModSounds.registerSounds();
            LOGGER.info("Initialized Chaos Cubed");
        } catch (RuntimeException e) {
            ChaosCubed.LOGGER.error("A Runtime Error occurred while initializing ChaosCubed {}", e.toString());
        }
    }
}
