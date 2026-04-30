package net.kingscraft.chaoscubed.sounds;

import net.kingscraft.chaoscubed.ChaosCubed;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvent;

public class ModSounds {

    public static final SoundEvent SULFUR_CUBE_DEATH = registerSoundEvent("sulfur_cube_death");
    public static final SoundEvent SULFUR_CUBE_JUMP = registerSoundEvent("sulfur_cube_jump");
    public static final SoundEvent SULFUR_CUBE_SQUISH = registerSoundEvent("sulfur_cube_squish");
    public static final SoundEvent SULFUR_CUBE_HURT = registerSoundEvent("sulfur_cube_hit");
    public static final SoundEvent SULFUR_CUBE_ABSORB = registerSoundEvent("sulfur_cube_absorb");
    public static final SoundEvent SULFUR_CUBE_EJECT = registerSoundEvent("sulfur_cube_eject");

    private static SoundEvent registerSoundEvent(String name) {
        Identifier id = Identifier.fromNamespaceAndPath(ChaosCubed.MODID, name);
        return Registry.register(BuiltInRegistries.SOUND_EVENT, id, SoundEvent.createVariableRangeEvent(id));
    }

    public static void registerSounds() {

    }
}
