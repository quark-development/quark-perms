package dev.quark.quarkperms.playerdata.manager;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.playerdata.QPlayer;
import dev.quark.quarkperms.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlayerManager {

    private final QuarkPerms core = QuarkPerms.getInstance();
    private final Set<QPlayer> playerData = new HashSet<>();

    private final YamlConfiguration dataFile = core.getConfigManager().getFile("player-data").getConfig();

    public PlayerManager() {
        Bukkit.getScheduler().runTaskAsynchronously(core, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                register(player.getUniqueId());
            }
        });
    }

    public void register(UUID uuid) {

        if (core.isSql()) {
            registerFromSql(uuid);
        } else if (core.isMongo()) {
            registerFromMongo(uuid);
        } else {
            registerFromFile(uuid);
        }

    }

    public void registerFromFile(UUID uuid) {
        QPlayer qp = new QPlayer(uuid);
        qp.setName(Bukkit.getPlayer(uuid).getName());
        if (dataFile.getConfigurationSection("").getKeys(false).contains(uuid.toString())) {
            /* THEY HAVE PLAYER DATA */

            if (dataFile.getStringList(uuid.toString() + ".ranks") != null) {

                for (String rank : dataFile.getStringList(uuid.toString() + ".ranks")) {

                    if ()

                }

            }

            List<String> ranks = dataFile.getStringList(uuid.toString() + ".ranks");

        } else {

        }
    }

    public void registerFromSql(UUID uuid) {

    }

    public void registerFromMongo(UUID uuid) {

    }

}
