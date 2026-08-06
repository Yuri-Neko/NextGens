package com.muhammaddaffa.nextgens.generators.listeners.helpers;

import com.muhammaddaffa.mdlib.hooks.VaultEconomy;
import com.muhammaddaffa.mdlib.task.ExecutorManager;
import com.muhammaddaffa.mdlib.utils.Common;
import com.muhammaddaffa.mdlib.utils.Executor;
import com.muhammaddaffa.mdlib.utils.Placeholder;
import com.muhammaddaffa.mdlib.xseries.particles.XParticle;
import com.muhammaddaffa.nextgens.NextGens;
import com.muhammaddaffa.nextgens.api.events.generators.GeneratorUpgradeEvent;
import com.muhammaddaffa.nextgens.generators.ActiveGenerator;
import com.muhammaddaffa.nextgens.generators.Generator;
import com.muhammaddaffa.nextgens.shards.ShardManager;
import com.muhammaddaffa.nextgens.shards.ShardType;
import com.muhammaddaffa.nextgens.utils.Utils;
import com.muhammaddaffa.nextgens.utils.VisualAction;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Panda;
import org.bukkit.entity.Player;

import java.util.List;

public class GeneratorUpdateHelper {

    public static boolean upgradeGenerator(Player player, ActiveGenerator active) {
        return upgradeGenerator(player, active, false);
    }

    public static boolean upgradeGenerator(Player player, ActiveGenerator active, boolean silent) {
        Generator generator = active.getGenerator();
        Generator nextGenerator = NextGens.getInstance().getGeneratorManager().getGenerator(generator.nextTier());
        // Try to upgrade
        return upgradeGenerator(player, active, generator, nextGenerator, silent);
    }

    public static boolean upgradeGenerator(Player player, ActiveGenerator active, Generator generator, Generator nextGenerator) {
        return upgradeGenerator(player, active, generator, nextGenerator, false);
    }

    public static boolean upgradeGenerator(Player player, ActiveGenerator active, Generator generator, Generator nextGenerator, boolean silent) {
        Block block = active.getLocation().getBlock();
        if (nextGenerator == null) {
            NextGens.DEFAULT_CONFIG.sendMessage(player, "messages.no-upgrade");
            // play bass sound
            Utils.bassSound(player);
            return false;
        }
        // money check
        if (VaultEconomy.getBalance(player) < generator.cost()) {
            NextGens.DEFAULT_CONFIG.sendMessage(player, "messages.not-enough-money", new Placeholder()
                    .add("{money}", Common.digits(VaultEconomy.getBalance(player)))
                    .add("{upgradecost}", Common.digits(generator.cost()))
                    .add("{remaining}", Common.digits(VaultEconomy.getBalance(player) - generator.cost())));
            // play bass sound
            Utils.bassSound(player);
            return false;
        }
        // Check requirements
        List<String> requirementsNotPassed = generator.checkRequirements(player, generator.upgradeRequirements());
        if (!requirementsNotPassed.isEmpty()) {
            Common.sendMessage(player, requirementsNotPassed);
            Utils.bassSound(player);
            return false;
        }
        // Shard cost check (cross-dimension upgrades, e.g. max Overworld -> first Nether tier)
        ShardManager shardManager = NextGens.getInstance().getShardManager();
        if (generator.netherShardCost() > 0
                && !hasEnoughShard(player, shardManager, ShardType.NETHER, generator.netherShardCost())) {
            return false;
        }
        if (generator.endShardCost() > 0
                && !hasEnoughShard(player, shardManager, ShardType.END, generator.endShardCost())) {
            return false;
        }
        // call the custom events
        GeneratorUpgradeEvent upgradeEvent = new GeneratorUpgradeEvent(generator, player, nextGenerator);
        Bukkit.getPluginManager().callEvent(upgradeEvent);
        if (upgradeEvent.isCancelled()) {
            return false;
        }
        // take the money from player
        VaultEconomy.withdraw(player, generator.cost());
        // take the shards from player, if any were required
        if (generator.netherShardCost() > 0) {
            shardManager.removeFromInventory(player.getInventory(), ShardType.NETHER, generator.netherShardCost());
        }
        if (generator.endShardCost() > 0) {
            shardManager.removeFromInventory(player.getInventory(), ShardType.END, generator.endShardCost());
        }
        // If the next tier can't be placed in this world (e.g. upgrading a maxed-out
        // Overworld generator into the first Nether-only tier), it can't just replace
        // the block in place - remove the old generator and hand the new one's item
        // to the player instead, same as if they'd broken it.
        boolean crossDimension = !nextGenerator.worldType().matches(block.getWorld());
        if (crossDimension) {
            NextGens.getInstance().getGeneratorManager().unregisterGenerator(block);
            NextGens.getInstance().getApi().giveGenerator(player, nextGenerator.id());
        } else {
            // register the generator again
            NextGens.getInstance().getGeneratorManager().registerGenerator(player, nextGenerator, block);
        }
        // visual actions
        FileConfiguration config = NextGens.DEFAULT_CONFIG.getConfig();
        if (!silent) {
            if (crossDimension) {
                NextGens.DEFAULT_CONFIG.sendMessage(player, "messages.generator-given-inventory", new Placeholder()
                        .add("{previous}", generator.displayName())
                        .add("{current}", nextGenerator.displayName())
                        .add("{dimension}", nextGenerator.worldType().name()));
            } else {
                VisualAction.send(player, config, "generator-upgrade-options", new Placeholder()
                        .add("{previous}", generator.displayName())
                        .add("{current}", nextGenerator.displayName())
                        .add("{cost}", Common.digits(generator.cost())));
            }
        }
        // play particle
        ExecutorManager.getProvider().async(() -> {
            if (NextGens.DEFAULT_CONFIG.getConfig().getBoolean("generator-upgrade-options.particles")) {
                GeneratorParticle.successParticle(block, generator);
            }
        });
        // give cashback to the player
        Utils.performCashback(player, NextGens.getInstance().getUserManager(), generator.cost());
        return true;
    }

    private static boolean hasEnoughShard(Player player, ShardManager shardManager, ShardType type, int required) {
        int owned = shardManager.countInInventory(player.getInventory(), type);
        if (owned >= required) {
            return true;
        }
        NextGens.DEFAULT_CONFIG.sendMessage(player, "messages.not-enough-shard", new Placeholder()
                .add("{shard}", type.getDefaultName())
                .add("{required}", required)
                .add("{owned}", owned));
        Utils.bassSound(player);
        return false;
    }

}
