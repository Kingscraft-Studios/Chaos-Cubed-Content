package net.kingscraft.chaoscubed.terrablender;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.minecraft.resources.Identifier;
import terrablender.api.RegionType;
import terrablender.api.Regions;
import terrablender.api.SurfaceRuleManager;
import terrablender.api.TerraBlenderApi;

public class TerraBlenderInit implements TerraBlenderApi {
    @Override
    public void onTerraBlenderInitialized() {
        Regions.register(new CCRegion(Identifier.fromNamespaceAndPath(ChaosCubed.MODID, "overworld"), RegionType.OVERWORLD, 4));

        SurfaceRuleManager.addSurfaceRules(SurfaceRuleManager.RuleCategory.OVERWORLD, ChaosCubed.MODID, CCSurfaceRuleData.makeRule());
    }
}
