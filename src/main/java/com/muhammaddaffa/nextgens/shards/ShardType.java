package com.muhammaddaffa.nextgens.shards;

import org.bukkit.Material;

public enum ShardType {

    NETHER("Shard Of Nether", "shard-of-nether", Material.NETHER_STAR),
    END("Shard Of The End", "shard-of-the-end", Material.ECHO_SHARD),
    GREENLAND("Shard Of GreenLand", "shard-of-greenland", Material.RED_DYE);

    private final String defaultName;
    private final String configKey;
    private final Material defaultMaterial;

    ShardType(String defaultName, String configKey, Material defaultMaterial) {
        this.defaultName = defaultName;
        this.configKey = configKey;
        this.defaultMaterial = defaultMaterial;
    }

    public String getDefaultName() {
        return defaultName;
    }

    /** Key used both as the PDC value and as the config.yml section name (shards.<configKey>). */
    public String getConfigKey() {
        return configKey;
    }

    public Material getDefaultMaterial() {
        return defaultMaterial;
    }

}
