package com.muhammaddaffa.nextgens.hooks.levels;

import com.nextgens.levels.api.NextGensLevelsAPI;

import java.util.UUID;

/**
 * NextGensLevels is a hard dependency (see plugin.yml "depend") - direct import, no
 * reflection needed (unlike ArmorSpeedHook for NextArmor, which stays optional). Thin
 * wrapper purely so call sites in this plugin read consistently with the other hooks
 * package, and so a future re-introduction of reflection (if NextGensLevels ever
 * becomes optional again) only touches this one file.
 */
public final class NextGensLevelsHook {

    private NextGensLevelsHook() {
    }

    public static int getLevel(UUID playerId) {
        return NextGensLevelsAPI.getLevel(playerId);
    }

    public static int getPrestige(UUID playerId) {
        return NextGensLevelsAPI.getPrestige(playerId);
    }

    public static void reduceLevel(UUID playerId, int amount) {
        NextGensLevelsAPI.reduceLevel(playerId, amount);
    }

    /** Cumulative gens-speed bonus percent from prestige rewards (see NextGensLevels' prestige.yml). */
    public static double getSpeedBonusPercent(UUID playerId) {
        // NextGensLevelsAPI doesn't need a dedicated method for this - LevelManager
        // itself isn't exposed, so this goes through the same reflection-safe class
        // name NextGensLevels documents for its own PrestigeSpeedHook.
        return com.nextgens.levels.hooks.PrestigeSpeedHook.getSpeedBonusPercent(playerId);
    }

}
