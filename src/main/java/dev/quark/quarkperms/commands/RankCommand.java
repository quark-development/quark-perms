package dev.quark.quarkperms.commands;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.framework.command.Command;
import dev.quark.quarkperms.framework.command.CommandArgs;
import dev.quark.quarkperms.framework.command.QuarkFramework;
import dev.quark.quarkperms.playerdata.QPlayer;
import dev.quark.quarkperms.rank.Rank;
import dev.quark.quarkperms.rank.manager.RankManager;
import dev.quark.quarkperms.utils.CC;
import org.bukkit.entity.Player;

import java.util.HashMap;

public class RankCommand {

    private final QuarkPerms core = QuarkPerms.getInstance();

    public RankCommand(QuarkFramework framework) {
        framework.registerCommands(this);
    }

    @Command(name = "rank")
    public void onRankCmd(CommandArgs cmd) {
        String[] args = cmd.getArgs();
        Player player = cmd.getPlayer();

        QPlayer qp = core.getPlayerManager().get(player.getUniqueId());

        RankManager rankManager = core.getRankManager();

        if (rankManager == null) core.getLogger().info("AAAAAAAAAAAAAAAA");

        rankManager.allRanks.forEach(r -> player.sendMessage(CC.trns(r.getPrefix())));

        if (qp == null) player.sendMessage("null");
        qp.getRanks().forEach(r -> player.sendMessage(r.getName()));

        Rank rank = rankManager.getHighest(qp.getRanks());

        player.sendMessage(CC.trns("&fRank: " + rank.getPrefix()));

    }



}
