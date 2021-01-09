package dev.quark.quarkperms.playerdata.manager;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.framework.config.ConfigurationFile;
import dev.quark.quarkperms.playerdata.QPlayer;
import dev.quark.quarkperms.rank.Rank;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static dev.quark.quarkperms.utils.SerializationUtil.*;

public class PlayerManager {

    private final QuarkPerms core = QuarkPerms.getInstance();
    private final Set<QPlayer> playerData = new HashSet<>();
    private MongoCollection<Document> collection;

    private final ConfigurationFile dataFile = core.getConfigManager().getFile("player-data");

    public PlayerManager() {
        if (core.isMongo())
            collection = core.getMongoManager().getMongoDatabase().getCollection("player-data");

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

        if (core.isSql()) {
            saveToSql(qp);
        } else if (core.isMongo()) {
            saveToMongo(qp);
        } else {
            saveToFile(qp);
        }

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

    public void registerFromSql(UUID uuid) {
        QPlayer qp = (get(uuid) != null ? get(uuid) : new QPlayer(uuid));
        qp.setName(Bukkit.getPlayer(uuid).getName());

        PermissionAttachment attachment = Bukkit.getPlayer(uuid).addAttachment(core);
        qp.setAttachment(attachment);

        ResultSet rs = core.getSqlManager().execQuery("SELECT * FROM `player-data` WHERE uuid = '" + uuid.toString() + "'");
        if (rs == null) return;

        try {
            /* THEY HAVE PLAYER DATA */
            if (rs.next()) {

                String ranks = rs.getString("ranks");
                String perms = rs.getString("permissions");

                /* IF THEY HAVE RANKS IN THE FILE */
                if (!ranks.equalsIgnoreCase("none")) {
                    List<Rank> r = new ArrayList<>();
                    for (String s : ranks.split(":")) {
                        if (core.getRankManager().get(s.toLowerCase()) != null) r.add(core.getRankManager().get(s.toLowerCase()));
                    } qp.setRanks(r);
                } else { qp.setRanks(Collections.singletonList(core.getRankManager().getDefault())); }

                /* IF THEY HAVE PERMISSIONS IN THE FILE */
                if (!perms.equalsIgnoreCase("none")) {
                    qp.setPermissions(Arrays.asList(perms.split(":")));
                } else { qp.setPermissions(new ArrayList<>()); }

            } else {

                /* SET DEFAULTS */
                qp.setRanks(Collections.singletonList(core.getRankManager().getDefault()));
                qp.setPermissions(new ArrayList<>());

                /* UPDATE FILE */
                // TODO: caching system to prevent strain on database
                saveToSql(qp);

            }
        } catch (SQLException e) { core.getLogger().severe(e.getMessage()); }

        playerData.add(qp);
    }

    public void registerFromMongo(UUID uuid) {
        QPlayer qp = (get(uuid) != null ? get(uuid) : new QPlayer(uuid));
        qp.setName(Bukkit.getPlayer(uuid).getName());

        PermissionAttachment attachment = Bukkit.getPlayer(uuid).addAttachment(core);
        qp.setAttachment(attachment);

        Document document = collection.find(Filters.eq("uuid", uuid.toString())).first();

        if (document == null) {
            qp.setRanks(Collections.singletonList(core.getRankManager().getDefault()));
            qp.setPermissions(new ArrayList<>());

            this.saveToMongo(qp);

            return;
        }

        qp.setRanks(deserializeRanks(document.getString("ranks")));
        qp.setPermissions(deserializePerms(document.getString("permissions")));

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

    public void saveToSql(QPlayer qp) {
        ResultSet rs = core.getSqlManager().execQuery("SELECT * FROM `player-data` WHERE uuid = '" + qp.getUuid().toString() + "'");
        if (rs == null) return;

        String ranks = serializeRanks(qp.getRanks());
        String permissions = serializePerms(qp.getPermissions());

        try {
            if (rs.next()) {
                core.getSqlManager().execUpdate("UPDATE `player-data` SET ranks = '" + ranks + "', permissions = '" + permissions + "' WHERE uuid = '" + qp.getUuid() + "';");
            } else {
                core.getSqlManager().execUpdate("INSERT INTO `player-data` (uuid, ranks, permissions) VALUES (" + qp.getUuid().toString() + ", " + ranks + ", " + permissions + ");");
            }
        } catch (SQLException e) { core.getLogger().severe(e.getMessage()); }
    }

    public void saveToMongo(QPlayer qp) {
        Document document = new Document();

        document.put("uuid", qp.getUuid().toString());
        document.put("ranks", serializeRanks(qp.getRanks()));
        document.put("permissions", serializePerms(qp.getPermissions()));

        collection.replaceOne(Filters.eq("uuid", qp.getUuid().toString()), document, new ReplaceOptions().upsert(true));
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

    public void reloadPlayer(QPlayer qp) {
        unregisterWithoutSave(qp.getUuid());
        register(qp.getUuid());
    }

}
