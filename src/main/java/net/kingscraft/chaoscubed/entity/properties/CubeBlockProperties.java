package net.kingscraft.chaoscubed.entity.properties;

public record CubeBlockProperties(
        float speedMod,
        float bounceMod,
        float dragMod,
        float knockbackMod,
        boolean isBuoyant,
        Archetype archetype) {

    public enum Archetype {
        REGULAR,
        SLOW_FLAT,
        LIGHT,
        STICKY
    }

    public CubeBlockProperties(float speedMod, float bounceMod, float dragMod, boolean isBuoyant) {
        this(speedMod, bounceMod, dragMod, 1.0f, isBuoyant, Archetype.REGULAR);
    }

    public boolean isSticky() {
        return this.archetype == Archetype.STICKY;
    }

    public float hitResponseScale(float damageAmount) {
        float damageFactor = Math.min(1.0f, Math.max(0.0f, damageAmount / 10.0f));
        return 1.0f + (damageFactor * 0.45f);
    }

    public static final CubeBlockProperties REGULAR =
            new CubeBlockProperties(1.0f, 1.0f, 1.0f, 1.0f, true, Archetype.REGULAR);

    public static final CubeBlockProperties SLOW_FLAT =
            new CubeBlockProperties(0.5f, 0.25f, 1.25f, 0.85f, false, Archetype.SLOW_FLAT);

    public static final CubeBlockProperties LIGHT =
            new CubeBlockProperties(1.75f, 2.25f, 0.75f, 1.45f, true, Archetype.LIGHT);

    public static final CubeBlockProperties STICKY =
            new CubeBlockProperties(0.0f, 0.0f, 0.45f, 1.35f, false, Archetype.STICKY);
}
