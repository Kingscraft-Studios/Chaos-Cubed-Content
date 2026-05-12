package net.kingscraft.chaoscubed.entity.properties;

import net.minecraft.core.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.EmptyBlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class SulfurCubePropertiesRegistry {
    private static final Map<Block, CubeBlockProperties> REGISTRY = new HashMap<>();

    static {
        // Regular
        registerTag(BlockTags.CONCRETE_POWDER, CubeBlockProperties.REGULAR);
        registerTag(BlockTags.DIRT, CubeBlockProperties.REGULAR, Blocks.MYCELIUM, Blocks.MOSS_BLOCK, Blocks.PALE_MOSS_BLOCK);
        register(Blocks.PACKED_MUD, CubeBlockProperties.REGULAR);
        register(Blocks.COAL_BLOCK, CubeBlockProperties.REGULAR);
        register(Blocks.CLAY, CubeBlockProperties.REGULAR);
        register(Blocks.BONE_BLOCK, CubeBlockProperties.REGULAR);

        // Sticky
        register(Blocks.HONEY_BLOCK, CubeBlockProperties.STICKY);
        register(Blocks.HONEYCOMB_BLOCK, CubeBlockProperties.STICKY);

        // Slow Flat
        register(Blocks.IRON_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.GOLD_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.RAW_COPPER_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.RAW_GOLD_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.RAW_IRON_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.GOLD_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.NETHER_GOLD_ORE, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.IRON_ORE, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.COPPER_ORE, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.NETHERITE_BLOCK, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.ANCIENT_DEBRIS, CubeBlockProperties.SLOW_FLAT);
        registerTag(BlockTags.COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.EXPOSED_COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WEATHERED_COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.OXIDIZED_COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_EXPOSED_COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_WEATHERED_COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_OXIDIZED_COPPER_BULB, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.EXPOSED_CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WEATHERED_CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.OXIDIZED_CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_EXPOSED_CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_WEATHERED_CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_OXIDIZED_CUT_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.EXPOSED_CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WEATHERED_CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.OXIDIZED_CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_EXPOSED_CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_WEATHERED_CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);
        register(Blocks.WAXED_OXIDIZED_CHISELED_COPPER, CubeBlockProperties.SLOW_FLAT);

        // Light
        registerTag(BlockTags.WOOL, CubeBlockProperties.LIGHT);

        //TNT
        register(Blocks.TNT, CubeBlockProperties.EXPLOSIVE);

    }

    public static void registerTag(
            TagKey<Block> tag,
            CubeBlockProperties props,
            Block... excludedBlocks
    ) {

        Set<Block> excluded = Set.of(excludedBlocks);

        for (Holder<Block> holder : BuiltInRegistries.BLOCK.getTagOrEmpty(tag)) {
            Block block = holder.value();

            // Skip excluded blocks
            if (excluded.contains(block)) {
                continue;
            }

            BlockState state = block.defaultBlockState();

            // Only real full cube blocks
            if (!Block.isShapeFullBlock(
                    state.getCollisionShape(
                            EmptyBlockGetter.INSTANCE,
                            BlockPos.ZERO
                    )
            )) {
                continue;
            }

            REGISTRY.put(block, props);
        }
    }

    public static void register(Block block, CubeBlockProperties props) {
        REGISTRY.put(block, props);
    }

    public static CubeBlockProperties get(Block block) {
        return REGISTRY.getOrDefault(block, CubeBlockProperties.REGULAR);
    }
}