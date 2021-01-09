package dev.quark.quarkperms.utils;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.rank.Rank;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class SerializationUtil {

    private final static QuarkPerms core = QuarkPerms.getInstance();

    public static String serializeRanks(List<Rank> ranks) {
        StringBuilder sb = new StringBuilder();

        if (ranks.size() > 0) {
            for (Rank rank : ranks) {
                if (ranks.indexOf(rank) + 1 == ranks.size()) {
                    sb.append(rank.getName());
                } else { sb.append(rank.getName()).append(":"); }
            }
        } else { sb = new StringBuilder("none"); }

        return sb.toString();
    }

    public static String serializePerms(List<String> perms) {
        StringBuilder sb = new StringBuilder();

        if (perms.size() > 0) {
            for (String perm : perms) {
                if (perms.indexOf(perm) + 1 == perms.size()) {
                    sb.append(perm);
                } else { sb.append(perm).append(":"); }
            }
        } else { sb = new StringBuilder("none"); }

        return sb.toString();
    }

    public static List<Rank> deserializeRanks(String string) {
        List<Rank> toReturn = new ArrayList<>();
        String[] data = string.split(":");

        for (String datum : data) {
            if (core.getRankManager().get(datum.toLowerCase()) != null) toReturn.add(core.getRankManager().get(datum.toLowerCase()));
        }

        if (toReturn.isEmpty()) toReturn.add(core.getRankManager().getDefault());

        return toReturn;
    }

    public static List<String> deserializePerms(String string) {
        return new ArrayList<>(Arrays.asList(string.split(":")));
    }

}
