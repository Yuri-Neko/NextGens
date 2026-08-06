package com.muhammaddaffa.nextgens.commands.subcommands;

import com.muhammaddaffa.mdlib.commands.args.ArgSuggester;
import com.muhammaddaffa.mdlib.commands.args.builtin.IntArg;
import com.muhammaddaffa.mdlib.commands.args.builtin.OnlinePlayerArg;
import com.muhammaddaffa.mdlib.commands.args.builtin.StringArg;
import com.muhammaddaffa.mdlib.commands.commands.RoutedCommand;
import com.muhammaddaffa.mdlib.utils.Placeholder;
import com.muhammaddaffa.nextgens.NextGens;
import com.muhammaddaffa.nextgens.shards.ShardManager;
import com.muhammaddaffa.nextgens.shards.ShardType;
import org.bukkit.entity.Player;

import java.util.List;

public class GiveShardCommand {

    public static void handle(RoutedCommand.CommandPlan plan, ShardManager shardManager) {
        plan.perm("nextgens.admin")
                .arg("target", new OnlinePlayerArg())
                .arg("type", new StringArg(), ArgSuggester.ofDynamic((sender, prefix) -> List.of("nether", "end")))
                .argOptional("amount", new IntArg())
                .exec((sender, ctx) -> {
                    Player target = ctx.get("target", Player.class);
                    String typeArg = ctx.get("type", String.class);
                    Integer amount = ctx.get("amount", Integer.class);

                    ShardType type = "end".equalsIgnoreCase(typeArg) ? ShardType.END
                            : "nether".equalsIgnoreCase(typeArg) ? ShardType.NETHER : null;
                    if (type == null) {
                        NextGens.DEFAULT_CONFIG.sendMessage(sender, "messages.invalid-shard-type");
                        return;
                    }
                    int actualAmount = amount != null ? Math.max(1, amount) : 1;

                    shardManager.giveOrDrop(target, type, actualAmount);

                    NextGens.DEFAULT_CONFIG.sendMessage(sender, "messages.give-shard", new Placeholder()
                            .add("{amount}", actualAmount)
                            .add("{shard}", type.getDefaultName())
                            .add("{player}", target.getName()));
                    NextGens.DEFAULT_CONFIG.sendMessage(target, "messages.receive-shard", new Placeholder()
                            .add("{amount}", actualAmount)
                            .add("{shard}", type.getDefaultName()));
                });
    }

}
