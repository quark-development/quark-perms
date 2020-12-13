package dev.quark.quarkperms.playerdata;

import dev.quark.quarkperms.rank.Rank;
import lombok.Data;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import java.util.List;
import java.util.UUID;

@Data
public class QPlayer {

    private final UUID uuid;
    private String name;
    private PermissionAttachment attachment;

    private List<Rank> ranks;
    private List<String> permissions;

    public Player getPlayer() {
        return Bukkit.getPlayer(uuid);
    }

    public void reset() {
        name = null; attachment = null; ranks = null; permissions = null;
    }

}
