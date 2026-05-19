package net.kingscraft.chaoscubed.mixin;

import net.minecraft.SharedConstants;
import net.minecraft.WorldVersion;
import net.minecraft.server.packs.PackType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SharedConstants.class)
public abstract class SharedConstantMixin {

    @Shadow @Mutable
    private static WorldVersion CURRENT_VERSION;

    @Inject(method = "getCurrentVersion", at = @At("HEAD"), cancellable = true)
    private static void onGetCurrentVersion(CallbackInfoReturnable<WorldVersion> cir) {
        WorldVersion vanilla = CURRENT_VERSION;

        if (vanilla != null) {
            cir.setReturnValue(new WorldVersion.Simple(
                    vanilla.id(),
                    "26.2",
                    vanilla.dataVersion(),
                    vanilla.protocolVersion(),
                    vanilla.packVersion(PackType.CLIENT_RESOURCES),
                    vanilla.packVersion(PackType.SERVER_DATA),
                    vanilla.buildTime(),
                    vanilla.stable()
            ));
        }
    }
}