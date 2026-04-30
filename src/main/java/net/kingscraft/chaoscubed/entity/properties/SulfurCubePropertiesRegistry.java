package net.kingscraft.chaoscubed.entity.properties;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.util.HashMap;
import java.util.Map;

public class SulfurCubePropertiesRegistry {
    private static final Map<Block, CubeBlockProperties> REGISTRY = new HashMap<>();

    static {
        // Regular
        register(Blocks.DIRT, CubeBlockProperties.REGULAR);
        register(Blocks.STONE, CubeBlockProperties.REGULAR);
        register(Blocks.COBBLESTONE, CubeBlockProperties.REGULAR);
        register(Blocks.MOSSY_COBBLESTONE, CubeBlockProperties.REGULAR);
        register(Blocks.SMOOTH_STONE, CubeBlockProperties.REGULAR);
        register(Blocks.STONE_BRICKS, CubeBlockProperties.REGULAR);
        register(Blocks.CHISELED_STONE_BRICKS, CubeBlockProperties.REGULAR);
        register(Blocks.CRACKED_STONE_BRICKS, CubeBlockProperties.REGULAR);
        register(Blocks.MOSSY_STONE_BRICKS, CubeBlockProperties.REGULAR);

        // Sticky
        register(Blocks.HONEY_BLOCK, CubeBlockProperties.STICKY);
        register(Blocks.HONEYCOMB_BLOCK, CubeBlockProperties.STICKY);

        // Heavy
        register(Blocks.QUARTZ_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.QUARTZ_BRICKS, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.CHISELED_QUARTZ_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.QUARTZ_PILLAR, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.SMOOTH_QUARTZ, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.COPPER_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.IRON_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.GOLD_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.AMETHYST_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.COAL_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.DIAMOND_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.EMERALD_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.LAPIS_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.NETHERITE_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.REDSTONE_BLOCK, CubeBlockProperties.SLOW_FLAT);

        // Light
        // Oak
        register(Blocks.OAK_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_OAK_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.OAK_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_OAK_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.OAK_PLANKS, CubeBlockProperties.LIGHT);
        // Spruce
        register(Blocks.SPRUCE_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_SPRUCE_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.SPRUCE_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_SPRUCE_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.SPRUCE_PLANKS, CubeBlockProperties.LIGHT);

        // Birch
        register(Blocks.BIRCH_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_BIRCH_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.BIRCH_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_BIRCH_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.BIRCH_PLANKS, CubeBlockProperties.LIGHT);

        // Jungle
        register(Blocks.JUNGLE_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_JUNGLE_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.JUNGLE_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_JUNGLE_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.JUNGLE_PLANKS, CubeBlockProperties.LIGHT);

        // Acacia
        register(Blocks.ACACIA_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_ACACIA_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.ACACIA_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_ACACIA_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.ACACIA_PLANKS, CubeBlockProperties.LIGHT);

        // Dark Oak
        register(Blocks.DARK_OAK_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_DARK_OAK_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.DARK_OAK_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_DARK_OAK_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.DARK_OAK_PLANKS, CubeBlockProperties.LIGHT);

        // Mangrove
        register(Blocks.MANGROVE_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_MANGROVE_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.MANGROVE_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_MANGROVE_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.MANGROVE_PLANKS, CubeBlockProperties.LIGHT);

        // Cherry
        register(Blocks.CHERRY_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_CHERRY_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.CHERRY_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_CHERRY_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.CHERRY_PLANKS, CubeBlockProperties.LIGHT);

        // Pale Oak
        register(Blocks.PALE_OAK_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_PALE_OAK_LOG, CubeBlockProperties.LIGHT);
        register(Blocks.PALE_OAK_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_PALE_OAK_WOOD, CubeBlockProperties.LIGHT);
        register(Blocks.PALE_OAK_PLANKS, CubeBlockProperties.LIGHT);

        // Bamboo
        register(Blocks.BAMBOO_BLOCK, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_BAMBOO_BLOCK, CubeBlockProperties.LIGHT);
        register(Blocks.BAMBOO_MOSAIC, CubeBlockProperties.LIGHT);

        // Crimson
        register(Blocks.CRIMSON_STEM, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_CRIMSON_STEM, CubeBlockProperties.LIGHT);
        register(Blocks.CRIMSON_HYPHAE, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_CRIMSON_HYPHAE, CubeBlockProperties.LIGHT);
        register(Blocks.CRIMSON_PLANKS, CubeBlockProperties.LIGHT);

        // Warped
        register(Blocks.WARPED_STEM, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_WARPED_STEM, CubeBlockProperties.LIGHT);
        register(Blocks.WARPED_HYPHAE, CubeBlockProperties.LIGHT);
        register(Blocks.STRIPPED_WARPED_HYPHAE, CubeBlockProperties.LIGHT);
        register(Blocks.WARPED_PLANKS, CubeBlockProperties.LIGHT);

        //Wool
        register(Blocks.WHITE_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.LIGHT_GRAY_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.GRAY_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.BLACK_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.BROWN_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.RED_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.ORANGE_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.YELLOW_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.GREEN_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.LIME_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.CYAN_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.LIGHT_BLUE_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.BLUE_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.PURPLE_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.MAGENTA_WOOL, CubeBlockProperties.LIGHT);
        register(Blocks.PINK_WOOL, CubeBlockProperties.LIGHT);
    }

    public static void register(Block block, CubeBlockProperties props) {
        REGISTRY.put(block, props);
    }

    public static CubeBlockProperties get(Block block) {
        return REGISTRY.getOrDefault(block, CubeBlockProperties.REGULAR);
    }
}