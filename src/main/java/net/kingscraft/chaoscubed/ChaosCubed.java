package net.kingscraft.chaoscubed;

import net.fabricmc.api.ModInitializer;
import net.kingscraft.chaoscubed.blocks.Blocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChaosCubed implements ModInitializer {
    public static final String MODID = "chaos_cubed";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    @Override
    public void onInitialize() {
        try {
            Blocks.registerModBlocks();
            LOGGER.info("Initialized Chaos Cubed");
        } catch (RuntimeException e) {
            ChaosCubed.LOGGER.error("A Runtime Error occurred while initializing ChaosCubed {}", e.toString());
        }
    }
}
