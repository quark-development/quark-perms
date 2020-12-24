package dev.quark.quarkperms.playerdata.listeners;

import dev.quark.quarkperms.QuarkPerms;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class IOListener implements Listener {

    private QuarkPerms core = QuarkPerms.getInstance();

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        core.getPlayerManager().register(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        core.getPlayerManager().unregister(e.getPlayer().getUniqueId());
    }

}
