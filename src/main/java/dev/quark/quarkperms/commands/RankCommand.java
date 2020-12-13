package dev.quark.quarkperms.commands;

import dev.quark.quarkperms.framework.command.Command;
import dev.quark.quarkperms.framework.command.QuarkFramework;

public class RankCommand {

    public RankCommand(QuarkFramework framework) {
        framework.registerCommands(this);
    }

    @Command(name = "rank")
    public void onRankCmd() {

    }

}
