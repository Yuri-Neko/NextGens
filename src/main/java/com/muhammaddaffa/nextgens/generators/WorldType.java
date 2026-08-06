package com.muhammaddaffa.nextgens.generators;

import org.bukkit.World;

/**
 * Which dimension a generator is allowed to be placed in. Matched against the
 * world's actual {@link World.Environment} (not its name), so this works
 * correctly no matter what a multiverse/custom world is actually called.
 */
public enum WorldType {

    /** No restriction - can be placed anywhere. */
    ANY,
    OVERWORLD,
    NETHER,
    THE_END;

    public static WorldType fromEnvironment(World.Environment environment) {
        return switch (environment) {
            case NETHER -> NETHER;
            case THE_END -> THE_END;
            default -> OVERWORLD;
        };
    }

    public boolean matches(World world) {
        if (this == ANY) return true;
        return this == fromEnvironment(world.getEnvironment());
    }

    public static WorldType parse(String value) {
        if (value == null) return ANY;
        try {
            return WorldType.valueOf(value.trim().toUpperCase().replace(' ', '_'));
        } catch (IllegalArgumentException ex) {
            return ANY;
        }
    }

}
