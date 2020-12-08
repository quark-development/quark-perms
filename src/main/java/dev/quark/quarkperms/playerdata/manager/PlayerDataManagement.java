package dev.quark.quarkperms.playerdata.manager;

import dev.quark.quarkperms.QuarkPerms;
import dev.quark.quarkperms.playerdata.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PlayerDataManagement {

    private final QuarkPerms main;
    private final Set<PlayerData> playerData = new HashSet<>();

    public PlayerDataManagement(QuarkPerms main) {
        this.main = main;
        Bukkit.getScheduler().runTaskAsynchronously(main, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                registerPlayerData(player.getUniqueId(), player.getName());
            }
        });
    }

    public void registerPlayerData(UUID uuid, String name) {

    }

}
