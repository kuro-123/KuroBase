package host.kuro.kurobase;

import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.listeners.BlockListener;
import host.kuro.kurobase.listeners.PlayerLister;
import host.kuro.kurodiscord.KuroDiscord;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;

public class KuroBase extends JavaPlugin {

    public static boolean DEBUG;

    private static DatabaseManager db;
    public static DatabaseManager getDB() { return db; }

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
        getLogger().info(Language.translate("plugin.setup.event"));
        this.getServer().getPluginManager().registerEvents(new PlayerLister(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);

        // database connect
        getLogger().info(Language.translate("plugin.setup.database"));
        db = new DatabaseManager(this);
        if (!db.Connect()) {
            disablePlugin();
            return;
        }
        // table initialize
        getLogger().info(Language.translate("plugin.setup.table"));
        InitTables();

        // load plugin
        if (!LoadDependPlugin()) {
            disablePlugin();
            return;
        }
        kurodiscord = (KuroDiscord) getServer().getPluginManager().getPlugin("KuroDiscord");
        getLogger().info(Language.translate("plugin.loaded"));
    }

    private void InitTables() {
        // player
        db.ExecuteUpdate(Language.translate("SQL.CREATE.PLAYER"), null);
        // UPDATE
        int ret = db.ExecuteUpdate(Language.translate("SQL.LOAD.UPDATE.PLAYER"), null);
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
        if (db != null) {
            getLogger().info(Language.translate("plugin.unloaded.database"));
            db.DisConnect();
        }
        getLogger().info(Language.translate("plugin.unloaded"));
    }
}
