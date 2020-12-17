package dev.quark.quarkperms.framework.config.manager;

import dev.quark.quarkperms.framework.config.ConfigurationFile;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Getter @Setter
public class ConfigManager {

    private final Set<ConfigurationFile> configs = new HashSet<>();

    public ConfigManager() {
        configs.add(new ConfigurationFile("config"));
        configs.add(new ConfigurationFile("db"));
        configs.add(new ConfigurationFile("messages"));
        configs.add(new ConfigurationFile("player-data"));
        configs.add(new ConfigurationFile("ranks"));
    }

    public ConfigurationFile getFile(String name) {
        Optional<ConfigurationFile> opt = configs.stream().filter(config -> config.getName().equalsIgnoreCase(name)).findFirst();
        return opt.orElse(null);
    }

    public void saveAll() {
        for (ConfigurationFile config : this.configs) {
            config.saveConfig();
        }
    }

    public void reloadAll() {
        for (ConfigurationFile config : this.configs) {
            config.reloadConfig();
        }
    }

}
