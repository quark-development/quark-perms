package dev.quark.quarkperms.rank.manager;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.rank.Rank;
import dev.quark.quarkperms.utils.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.YamlConfiguration;

import java.util.ArrayList;
import java.util.List;

@Getter @Setter
public class RankManager {

    private final QuarkPerms core = QuarkPerms.getInstance();
    private final YamlConfiguration ranksFile = core.getConfigManager().getFile("ranks").getConfig();

    private List<String> rankNames = new ArrayList<>(ranksFile.getConfigurationSection("").getKeys(false));

    public List<Rank> allRanks = new ArrayList<>();

    public RankManager() {
        for (String r : rankNames) {
            registerRank(r, rankNames.indexOf(r));
        }
    }

    private void registerRank(String name, int priority) {
        Rank rank = new Rank(name.toLowerCase());
        rank.setPriority(priority);

        /* SET DEFAULT */
        if (ranksFile.get(name + ".default") != null && ranksFile.getBoolean(name + ".default")) {
            rank.setDefault(true);
        }

        /* SET PREFIX */
        if (ranksFile.get(name + ".prefix") != null) {
            rank.setPrefix(CC.trns(ranksFile.getString(name+".prefix")));
        }

        /* SET PERMS */
        if (ranksFile.get(name + ".permissions") != null) { // set perms
            rank.setPermissions(ranksFile.getStringList(name+".permissions"));
        }

    }

    public Rank get(String name) {
        return null;
    }

}
