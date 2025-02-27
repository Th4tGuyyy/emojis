package team.unnamed.emojis.listener;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;

import team.unnamed.emojis.EmojisPlugin;
import team.unnamed.emojis.export.RemoteResource;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ResourcePackApplyListener implements Listener {

    private final Map<UUID, Integer> retries = new HashMap<>();
    private final RemoteResource resource;
    private final FileConfiguration config;

    public ResourcePackApplyListener(EmojisPlugin plugin) {
        this.resource = plugin.getRemoteResource();
        this.config = plugin.getConfig();
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.setResourcePack(resource.getUrl(), resource.getHash());
    }

    @EventHandler
    public void onStatus(PlayerResourcePackStatusEvent event) {
        Player player = event.getPlayer();
        PlayerResourcePackStatusEvent.Status status = event.getStatus();

        switch (status) {
            case SUCCESSFULLY_LOADED -> retries.remove(player.getUniqueId());
            case DECLINED -> handleFailedPack(player);
            case FAILED_DOWNLOAD -> {
                Integer count = retries.get(player.getUniqueId());
                if (count == null) {
                    count = 0;
                } else if (count > 3) {
                    handleFailedPack(player);
                    player.sendMessage(getAndFormat("messages.fail"));
                    retries.remove(player.getUniqueId());
                }

                retries.put(player.getUniqueId(), count + 1);
            }
        }
    }

    private String getAndFormat(String path) {
        String message = config.getString(path);

        if (message == null) {
            return path;
        }

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @SuppressWarnings("deprecation")
    private void handleFailedPack(Player player) {
        if (config.getBoolean("feature.require-pack")) {
            player.kickPlayer(getAndFormat("messages.fail"));
        } else {
            player.sendMessage(getAndFormat("messages.warn"));
        }
    }

}
