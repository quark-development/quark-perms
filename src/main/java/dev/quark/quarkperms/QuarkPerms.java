package dev.quark.quarkperms;

import dev.quark.quarkperms.database.MongoManager;
import dev.quark.quarkperms.database.SQLManager;
import dev.quark.quarkperms.framework.command.CommandFramework;
import dev.quark.quarkperms.framework.config.manager.ConfigManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter @Setter
public class QuarkPerms extends JavaPlugin {

    @Getter public static QuarkPerms instance;

    private CommandFramework framework;

    private ConfigManager configManager;
    private SQLManager sqlManager;
    private MongoManager mongoManager;

    public void onEnable() {
        instance = this;
        framework = new CommandFramework(this);

        registerManagers();

        getLogger().info("Plugin enabled successfully!");
    }

    public void onDisable() {
        instance = null;

        getLogger().info("Plugin disabled successfully!");
    }

    protected void registerManagers() {
        configManager = new ConfigManager();
        sqlManager = new SQLManager();
        mongoManager = new MongoManager();
    }

}
