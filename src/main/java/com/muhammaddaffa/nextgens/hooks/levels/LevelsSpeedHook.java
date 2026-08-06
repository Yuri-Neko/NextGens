package com.muhammaddaffa.nextgens.hooks.levels;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Optional hook (via reflection, no compile-time dependency) into NextGensLevels,
 * used to read a player's cumulative gens-speed bonus from prestige rewards - same
 * pattern as ArmorSpeedHook for NextArmor. Safe to call even if NextGensLevels isn't
 * installed: always just returns 0.
 */
public final class LevelsSpeedHook {

    private LevelsSpeedHook() {
    }

    public static double getSpeedBonusPercent(UUID playerId) {
        if (playerId == null) return 0;

        Plugin nextGensLevels = Bukkit.getPluginManager().getPlugin("NextGensLevels");
        if (nextGensLevels == null || !nextGensLevels.isEnabled()) {
            return 0;
        }

        try {
            Class<?> hookClass = Class.forName("com.nextgens.levels.hooks.PrestigeSpeedHook");
            Method method = hookClass.getMethod("getSpeedBonusPercent", UUID.class);
            Object result = method.invoke(null, playerId);
            if (result instanceof Number number) {
                return Math.max(0, number.doubleValue());
            }
        } catch (Throwable ex) {
            Bukkit.getLogger().log(Level.FINE, "[NextGens] Gagal membaca bonus speed dari NextGensLevels.", ex);
        }
        return 0;
    }

}
