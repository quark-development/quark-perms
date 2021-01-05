package dev.quark.quarkperms.playerdata.manager;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.framework.config.ConfigurationFile;
import dev.quark.quarkperms.playerdata.QPlayer;
import dev.quark.quarkperms.rank.Rank;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.*;

public class PlayerManager {

    private final QuarkPerms core = QuarkPerms.getInstance();
    private final Set<QPlayer> playerData = new HashSet<>();

    private final ConfigurationFile dataFile = core.getConfigManager().getFile("player-data");

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

        core.getPermissionManager().applyAllPermissions(get(uuid));

    }

    public void unregister(UUID uuid) {
        QPlayer qp = get(uuid);
        saveToFile(qp);

        playerData.remove(qp);
        if (qp.getAttachment() != null) qp.getPlayer().removeAttachment(qp.getAttachment());
        qp.reset();
    }

    public void unregisterWithoutSave(UUID uuid) {
        QPlayer qp = get(uuid);
        playerData.remove(qp);
        if (qp.getAttachment() != null) qp.getPlayer().removeAttachment(qp.getAttachment());
        qp.reset();
    }

    public void registerFromFile(UUID uuid) {
        QPlayer qp = (get(uuid) != null ? get(uuid) : new QPlayer(uuid));
        qp.setName(Bukkit.getPlayer(uuid).getName());

        PermissionAttachment attachment = Bukkit.getPlayer(uuid).addAttachment(core);
        qp.setAttachment(attachment);

        /* THEY HAVE PLAYER DATA */
        if (dataFile.getConfig().getConfigurationSection("").getKeys(false).contains(uuid.toString())) {

            /* IF THEY HAVE RANKS IN THE FILE */
            if (dataFile.getConfig().getStringList(uuid.toString() + ".ranks") != null) {
                List<Rank> ranks = new ArrayList<>();
                for (String rank : dataFile.getConfig().getStringList(uuid.toString() + ".ranks")) {
                    if (core.getRankManager().get(rank.toLowerCase()) != null) ranks.add(core.getRankManager().get(rank.toLowerCase()));
                } qp.setRanks(ranks);
            } else { qp.setRanks(Collections.singletonList(core.getRankManager().getDefault())); }

            /* IF THEY HAVE PERMISSIONS IN THE FILE */
            if (dataFile.getConfig().get(uuid.toString() + ".permissions") != null) {
                qp.setPermissions(dataFile.getConfig().getStringList(uuid.toString() + ".permissions"));
            } else { qp.setPermissions(new ArrayList<>()); }

        } else {

            /* SET DEFAULTS */
            qp.setRanks(Collections.singletonList(core.getRankManager().getDefault()));
            qp.setPermissions(new ArrayList<>());

            /* UPDATE FILE */
            saveToFile(qp);

        }

        playerData.add(qp);
    }

    public void saveToFile(QPlayer qp) {
        List<String> ranks = new ArrayList<>();
        for (Rank rank : qp.getRanks()) { ranks.add(rank.getName()); }
        dataFile.getConfig().set(qp.getUuid().toString() + ".ranks", ranks);
        dataFile.getConfig().set(qp.getUuid().toString() + ".permissions", qp.getPermissions());

        dataFile.saveConfig();
        dataFile.reloadConfig();
    }

    public QPlayer get(Player player) {
        for (QPlayer qp : playerData) {
            if (qp.getUuid().equals(player.getUniqueId())) return qp;
        } return null;
    }

    public QPlayer get(UUID uuid) {
        for (QPlayer qp : playerData) {
            if (qp.getUuid().equals(uuid)) return qp;
        } return null;
    }

    public void registerFromSql(UUID uuid) {

    }

    public void registerFromMongo(UUID uuid) {

    }

    public void reloadPlayer(QPlayer qp) {
        unregisterWithoutSave(qp.getUuid());
        register(qp.getUuid());
    }

}
