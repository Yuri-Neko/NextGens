package com.muhammaddaffa.nextgens.shards;

import com.muhammaddaffa.mdlib.utils.Common;
import com.muhammaddaffa.nextgens.NextGens;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Builds & recognizes "Shard Of Nether" / "Shard Of The End" - the items used to gate
 * crossing from one dimension's generator line into the next (see the shard-cost
 * settings under a generator's "upgrade:" block in generators.yml). Tagged with a PDC
 * marker so a lookalike/renamed vanilla item never counts.
 * <p>
 * Appearance (material/name/lore) is configurable per shard type in config.yml under
 * "shards.<shard-of-nether|shard-of-the-end>".
 */
public class ShardManager {

    public ItemStack build(ShardType type, int amount) {
        var config = NextGens.DEFAULT_CONFIG.getConfig();
        String path = "shards." + type.getConfigKey() + ".";

        Material material;
        try {
            material = Material.valueOf(config.getString(path + "material", type.getDefaultMaterial().name()));
        } catch (IllegalArgumentException ex) {
            material = type.getDefaultMaterial();
        }

        ItemStack item = new ItemStack(material, Math.max(1, Math.min(64, amount)));
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(Common.color(
                    config.getString(path + "display-name", "&d&l" + type.getDefaultName())));

            List<String> lore = new ArrayList<>();
            for (String line : config.getStringList(path + "lore")) {
                lore.add(Common.color(line));
            }
            if (lore.isEmpty()) {
                lore.add(Common.color("&7Used to upgrade generators"));
                lore.add(Common.color("&7into a new dimension."));
            }
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(NextGens.shard_type, PersistentDataType.STRING, type.getConfigKey());
            item.setItemMeta(meta);
        }
        return item;
    }

    public ShardType getType(ItemStack item) {
        if (item == null) return null;
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return null;
        String value = meta.getPersistentDataContainer().get(NextGens.shard_type, PersistentDataType.STRING);
        if (value == null) return null;
        for (ShardType type : ShardType.values()) {
            if (type.getConfigKey().equals(value)) {
                return type;
            }
        }
        return null;
    }

    public boolean isShard(ItemStack item, ShardType type) {
        return this.getType(item) == type;
    }

    public int countInInventory(PlayerInventory inventory, ShardType type) {
        int total = 0;
        for (ItemStack item : inventory.getContents()) {
            if (this.isShard(item, type)) {
                total += item.getAmount();
            }
        }
        return total;
    }

    /**
     * @return how many were actually removed (equal to {@code amount} if there was enough)
     */
    public int removeFromInventory(PlayerInventory inventory, ShardType type, int amount) {
        int remaining = amount;
        ItemStack[] contents = inventory.getContents();
        for (int i = 0; i < contents.length && remaining > 0; i++) {
            ItemStack item = contents[i];
            if (!this.isShard(item, type)) {
                continue;
            }
            int take = Math.min(remaining, item.getAmount());
            int newAmount = item.getAmount() - take;
            if (newAmount <= 0) {
                inventory.setItem(i, null);
            } else {
                item.setAmount(newAmount);
            }
            remaining -= take;
        }
        return amount - remaining;
    }

    public void giveOrDrop(Player player, ShardType type, int amount) {
        ItemStack stack = this.build(type, amount);
        Map<Integer, ItemStack> leftover = player.getInventory().addItem(stack);
        for (ItemStack left : leftover.values()) {
            Location loc = player.getLocation();
            if (loc.getWorld() != null) {
                loc.getWorld().dropItemNaturally(loc, left);
            }
        }
    }

}
