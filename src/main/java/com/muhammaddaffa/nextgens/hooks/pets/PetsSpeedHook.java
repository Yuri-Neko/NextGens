package com.muhammaddaffa.nextgens.hooks.pets;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

/**
 * Hook opsional (lewat reflection, tanpa compile-time dependency) ke plugin NextPets,
 * dipakai untuk membaca persentase bonus speed generator dari pet SPEED yang sedang
 * aktif/dipakai player - persis pola ArmorSpeedHook (NextArmor) / NextGensLevelsHook
 * (prestige). Aman dipanggil meskipun NextPets tidak terpasang: akan selalu
 * mengembalikan 0.
 */
public final class PetsSpeedHook {

    private PetsSpeedHook() {
    }

    public static double getSpeedBonusPercent(UUID playerId) {
        if (playerId == null) return 0;

        Plugin nextPets = Bukkit.getPluginManager().getPlugin("NextPets");
        if (nextPets == null || !nextPets.isEnabled()) {
            return 0;
        }

        try {
            Class<?> apiClass = Class.forName("com.nextgens.pets.api.NextPetsAPI");
            Method method = apiClass.getMethod("getSpeedBonusPercent", UUID.class);
            Object result = method.invoke(null, playerId);
            if (result instanceof Number number) {
                return Math.max(0, number.doubleValue());
            }
        } catch (Throwable ex) {
            Bukkit.getLogger().log(Level.FINE, "[NextGens] Gagal membaca bonus speed dari NextPets.", ex);
        }
        return 0;
    }

}
