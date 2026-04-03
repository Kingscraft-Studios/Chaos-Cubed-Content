package net.kingscraft.chaoscubed.blocks;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.kingscraft.chaoscubed.ChaosCubed;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.StairsBlock;
import net.minecraft.block.WallBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroups;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class Blocks {
    public static final Block SULFUR_BLOCK = registerBlock(
            "sulfur_block",
            new Block(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.DIORITE)
                    .registryKey(blockKey("sulfur_block")))
    );
    public static final Block SULFUR_SLAB = registerBlock(
            "sulfur_slab",
            new SlabBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.DIORITE_SLAB)
                    .registryKey(blockKey("sulfur_slab")))
    );
    public static final Block SULFUR_STAIRS = registerBlock(
            "sulfur_stairs",
            new StairsBlock(
                    SULFUR_BLOCK.getDefaultState(),
                    AbstractBlock.Settings.copy(net.minecraft.block.Blocks.DIORITE_STAIRS)
                            .registryKey(blockKey("sulfur_stairs"))
            )
    );
    public static final Block SULFUR_WALL = registerBlock(
            "sulfur_wall",
            new WallBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.DIORITE_WALL)
                    .registryKey(blockKey("sulfur_wall")))
    );
    public static final Block CHISELED_SULFUR = registerBlock(
            "chiseled_sulfur",
            new Block(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.CHISELED_STONE_BRICKS)
                    .registryKey(blockKey("chiseled_sulfur")))
    );
    public static final Block POLISHED_SULFUR = registerBlock(
            "polished_sulfur",
            new Block(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.POLISHED_DIORITE)
                    .registryKey(blockKey("polished_sulfur")))
    );
    public static final Block POLISHED_SULFUR_SLAB = registerBlock(
            "polished_sulfur_slab",
            new SlabBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.POLISHED_DIORITE_SLAB)
                    .registryKey(blockKey("polished_sulfur_slab")))
    );
    public static final Block POLISHED_SULFUR_STAIRS = registerBlock(
            "polished_sulfur_stairs",
            new StairsBlock(
                    POLISHED_SULFUR.getDefaultState(),
                    AbstractBlock.Settings.copy(net.minecraft.block.Blocks.POLISHED_DIORITE_STAIRS)
                            .registryKey(blockKey("polished_sulfur_stairs"))
            )
    );
    public static final Block POLISHED_SULFUR_WALL = registerBlock(
            "polished_sulfur_wall",
            new WallBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.STONE_BRICK_WALL)
                    .registryKey(blockKey("polished_sulfur_wall")))
    );
    public static final Block SULFUR_BRICKS = registerBlock(
            "sulfur_bricks",
            new Block(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.STONE_BRICKS)
                    .registryKey(blockKey("sulfur_bricks")))
    );
    public static final Block SULFUR_BRICK_SLAB = registerBlock(
            "sulfur_brick_slab",
            new SlabBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.STONE_BRICK_SLAB)
                    .registryKey(blockKey("sulfur_brick_slab")))
    );
    public static final Block SULFUR_BRICK_STAIRS = registerBlock(
            "sulfur_brick_stairs",
            new StairsBlock(
                    SULFUR_BRICKS.getDefaultState(),
                    AbstractBlock.Settings.copy(net.minecraft.block.Blocks.STONE_BRICK_STAIRS)
                            .registryKey(blockKey("sulfur_brick_stairs"))
            )
    );
    public static final Block SULFUR_BRICK_WALL = registerBlock(
            "sulfur_brick_wall",
            new WallBlock(AbstractBlock.Settings.copy(net.minecraft.block.Blocks.STONE_BRICK_WALL)
                    .registryKey(blockKey("sulfur_brick_wall")))
    );

    // Helper Methods
    private static RegistryKey<Block> blockKey(String name) {
        return RegistryKey.of(RegistryKeys.BLOCK, Identifier.of(ChaosCubed.MODID, name));
    }

    private static RegistryKey<Item> itemKey(String name) {
        return RegistryKey.of(RegistryKeys.ITEM, Identifier.of(ChaosCubed.MODID, name));
    }

    private static Block registerBlock(String name, Block block) {
        Registry.register(Registries.BLOCK, blockKey(name), block);
        Registry.register(Registries.ITEM, itemKey(name), new BlockItem(block, new Item.Settings().registryKey(itemKey(name))));
        return block;
    }

    // Public Method to register all Listed Blocks
    public static void registerModBlocks() {
        ChaosCubed.LOGGER.info("Registering blocks for {}", ChaosCubed.MODID);

        ItemGroupEvents.modifyEntriesEvent(ItemGroups.BUILDING_BLOCKS).register(entries -> {
            entries.add(SULFUR_BLOCK);
            entries.add(SULFUR_SLAB);
            entries.add(SULFUR_STAIRS);
            entries.add(SULFUR_WALL);
            entries.add(CHISELED_SULFUR);
            entries.add(POLISHED_SULFUR);
            entries.add(POLISHED_SULFUR_SLAB);
            entries.add(POLISHED_SULFUR_STAIRS);
            entries.add(POLISHED_SULFUR_WALL);
            entries.add(SULFUR_BRICKS);
            entries.add(SULFUR_BRICK_SLAB);
            entries.add(SULFUR_BRICK_STAIRS);
            entries.add(SULFUR_BRICK_WALL);
        });
        ItemGroupEvents.modifyEntriesEvent(ItemGroups.NATURAL).register(entries -> {
            entries.add(SULFUR_BLOCK);
        });
    }
}
