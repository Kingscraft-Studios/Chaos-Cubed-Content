package net.kingscraft.chaoscubed.terrablender;

import net.kingscraft.chaoscubed.blocks.ModBlocks;
import net.kingscraft.chaoscubed.worldgen.WorldGeneration;
import net.minecraft.world.level.levelgen.SurfaceRules;

public class CCSurfaceRuleData {
    private static final SurfaceRules.RuleSource SULFUR_FLOOR = SurfaceRules.state(ModBlocks.SULFUR_BLOCK.defaultBlockState());

    public static SurfaceRules.RuleSource makeRule() {
        return SurfaceRules.sequence(
                SurfaceRules.ifTrue(SurfaceRules.isBiome(WorldGeneration.SULFUR_CAVES),
                        SurfaceRules.sequence(
                                SurfaceRules.ifTrue(SurfaceRules.ON_FLOOR, SULFUR_FLOOR)
                        )
                )
        );
    }
}
