package host.kuro.kurobase;

import host.kuro.kurobase.lang.Language;
import host.kuro.kurodiscord.KuroDiscord;
import host.kuro.kurodiscord.listeners.PlayerLister;
import org.bukkit.ChatColor;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public class KuroBase extends JavaPlugin {

    public static boolean DEBUG;
    private static KuroDiscord kurodiscord;

    public static KuroDiscord getDiscord() { return kurodiscord; }

    @Override
    public void onEnable() {
        // language setup
        Language.load("UTF-8");
        getLogger().info(Language.translate("plugin.setup.language"));

        // directory setup
        getLogger().info(Language.translate("plugin.setup.directory"));
        if(!getDataFolder().exists()) { getDataFolder().mkdir(); }

        // load settings
        getLogger().info(ChatColor.RED + Language.translate("plugin.setup.settings"));
        this.saveDefaultConfig();
        DEBUG = this.getConfig().getBoolean("debug", false);

        // regist event listener
        getLogger().info(host.kuro.kurodiscord.lang.Language.translate("plugin.setup.event"));
        this.getServer().getPluginManager().registerEvents(new PlayerLister(), this);

        // load plugin
        if (!LoadDependPlugin()) {
            disablePlugin();
            return;
        }
        kurodiscord = (KuroDiscord) getServer().getPluginManager().getPlugin("KuroDiscord");
        getLogger().info(Language.translate("plugin.loaded"));
    }

    private boolean LoadDependPlugin() {
        RegisteredServiceProvider<KuroDiscord> rsp = getServer().getServicesManager().getRegistration(KuroDiscord.class);
        if (rsp == null) {
            return false;
        }
        kurodiscord = rsp.getProvider();
        return kurodiscord != null;
    }

    private void disablePlugin() {
        getServer().getPluginManager().disablePlugin(this);
    }

    private boolean isSpigotServer() {
        return getServer().getVersion().contains("Spigot");
    }

    @Override
    public void onDisable() {
        getLogger().info(Language.translate("plugin.unloaded"));
    }
}
