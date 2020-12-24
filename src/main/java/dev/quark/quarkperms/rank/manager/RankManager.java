package dev.quark.quarkperms.rank.manager;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.playerdata.QPlayer;
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
        if (ranksFile.get(name + ".permissions") != null) {
            rank.setPermissions(ranksFile.getStringList(name+".permissions"));
        }

        /* SET INHERITANCE */
        if (ranksFile.get(name + ".inheritance") != null) {
            List<Rank> ranks = new ArrayList<>();
            for (String str : ranksFile.getStringList(name + ".inheritance")) {
                if (core.getRankManager().get(str) != null) ranks.add(core.getRankManager().get(str));
            }
            rank.setInheritance(ranks);
        }

        allRanks.add(rank);
    }

    public Rank get(String name) {
        for (Rank r : allRanks) {
            if (name.toLowerCase().equals(r.getName())) return r;
        } return null;
    }
    public Rank getDefault() {
        for (Rank r : allRanks) {
            if (r.isDefault()) return r;
        } return null;
    }

    public Rank getHighest(List<Rank> ranks) {
        Rank toReturn = getDefault();
        for (Rank rank : ranks) {
            if (rank.getPriority() > toReturn.getPriority()) toReturn = rank;
        }
        return toReturn;
    }

}
