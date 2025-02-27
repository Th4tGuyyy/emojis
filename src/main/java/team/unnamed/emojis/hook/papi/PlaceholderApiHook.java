package team.unnamed.emojis.hook.papi;

import org.bukkit.plugin.Plugin;
import team.unnamed.emojis.EmojiRegistry;
import team.unnamed.emojis.hook.PluginHook;

public class PlaceholderApiHook implements PluginHook {

    private final Plugin plugin;
    private final EmojiRegistry registry;

    public PlaceholderApiHook(
            Plugin plugin,
            EmojiRegistry registry
    ) {
        this.plugin = plugin;
        this.registry = registry;
    }

    @Override
    public String getPluginName() {
        return "PlaceholderAPI";
    }

    @Override
    public void hook(Plugin hook) {
        new EmojiPlaceholderExpansion(plugin, registry)
                .register();
    }

}
