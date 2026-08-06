package com.muhammaddaffa.nextgens.hooks.armor;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.UUID;
import java.util.logging.Level;

public final class ArmorSpeedHook {

    private ArmorSpeedHook() {
    }

    public static double getSpeedBonusPercent(UUID playerId) {
        if (playerId == null) return 0;

        Plugin nextArmor = Bukkit.getPluginManager().getPlugin("NextArmor");
        if (nextArmor == null || !nextArmor.isEnabled()) {
            return 0;
        }

        try {
            Class<?> apiClass = Class.forName("com.nextgens.armor.api.NextArmorAPI");
            Method method = apiClass.getMethod("getSpeedBonusPercent", UUID.class);
            Object result = method.invoke(null, playerId);
            if (result instanceof Number number) {
                return Math.max(0, number.doubleValue());
            }
        } catch (Throwable ex) {
            Bukkit.getLogger().log(Level.FINE, "[NextGens] Gagal membaca bonus speed dari NextArmor.", ex);
        }
        return 0;
    }

}
