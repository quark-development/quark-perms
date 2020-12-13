package dev.quark.quarkperms;

import dev.quark.quarkperms.commands.RankCommand;
import dev.quark.quarkperms.database.MongoManager;
import dev.quark.quarkperms.database.SQLManager;
import dev.quark.quarkperms.expansions.PAPIExpansion;
import dev.quark.quarkperms.framework.command.QuarkFramework;
import dev.quark.quarkperms.framework.config.manager.ConfigManager;
import dev.quark.quarkperms.playerdata.manager.PlayerManager;
import dev.quark.quarkperms.rank.manager.RankManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class QuarkPerms extends JavaPlugin {

    @Getter public static QuarkPerms instance;

    private QuarkFramework framework;

    private ConfigManager configManager;
    private SQLManager sqlManager;
    private MongoManager mongoManager;

    private PlayerManager playerManager;
    private RankManager rankManager;

    private boolean sql = false;
    private boolean mongo = false;

    public void onEnable() {
        instance = this;
        framework = new QuarkFramework(this);

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            PAPIExpansion papiExpansion = new PAPIExpansion();

            if (papiExpansion.canRegister()) {
                papiExpansion.register();
            }
        }
        registerManagers();
        registerCommands();

        getLogger().info("Plugin enabled successfully!");
    }

    public void onDisable() {
        instance = null;

        getLogger().info("Plugin disabled successfully!");
    }

    protected void registerManagers() {
        configManager = new ConfigManager();

        if (configManager.getFile("config").getConfig().getString("data").equalsIgnoreCase("SQL")) {
            sql = true;
        } else if (configManager.getFile("config").getConfig().getString("data").equalsIgnoreCase("MONGO")) {
            mongo = true;
        }

        if (sql) sqlManager = new SQLManager();
        if (mongo) mongoManager = new MongoManager();

        rankManager = new RankManager();
        playerManager = new PlayerManager();
    }

    protected void registerCommands() {
        new RankCommand(framework);
    }

}
