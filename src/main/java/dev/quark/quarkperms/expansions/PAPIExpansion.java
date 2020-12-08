package dev.quark.quarkperms.expansions;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;

public class PAPIExpansion extends PlaceholderExpansion {

    @Override
    public boolean canRegister() { return true; }

    @Override
    public String getIdentifier() { return "QuarkPerms"; }

    @Override
    public String getAuthor() { return "iHells"; }

    @Override
    public String getVersion() { return "DEV-1.0"; }

    @Override
    public String onPlaceholderRequest(Player player, String id) {
        switch (id) {
            case "placeholder":
                return "item";
        }
        return null;
    }

}
