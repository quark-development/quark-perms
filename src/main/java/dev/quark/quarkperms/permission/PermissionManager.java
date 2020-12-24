package dev.quark.quarkperms.permission;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.playerdata.QPlayer;
import dev.quark.quarkperms.rank.Rank;

public class PermissionManager {

    private final QuarkPerms core = QuarkPerms.getInstance();

    public void applyPermission(String permission, QPlayer player) {
        if (!permission.equals("*")) {
            if (permission.startsWith("-")) {
                player.getAttachment().setPermission(permission.substring(1), false);
            } else {
                player.getAttachment().setPermission(permission, true);
            }
        } else {
            // all perms
        }
    }

    public void applyRankPermissions(Rank rank, QPlayer player) {
        for (String permission : rank.getPermissions()) {
            applyPermission(permission, player);
        }
        for (Rank inherit : rank.getInheritance()) {
            applyRankPermissions(inherit, player);
        }
    }

    public void applyAllPermissions(QPlayer player) {
        for (String permission : player.getPermissions()) {
            applyPermission(permission, player);
        }
        for (Rank rank : player.getRanks()) {
            applyRankPermissions(rank, player);
        }
    }

}
