package host.kuro.kurobase;

import host.kuro.kurobase.commands.ChestCommand;
import host.kuro.kurobase.commands.ListCommand;
import host.kuro.kurobase.commands.NameCommand;
import host.kuro.kurobase.commands.TagCommand;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.listeners.BlockListener;
import host.kuro.kurobase.listeners.EntityListener;
import host.kuro.kurobase.listeners.InventoryListener;
import host.kuro.kurobase.listeners.PlayerListener;
import host.kuro.kurodiscord.KuroDiscord;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public class KuroBase extends JavaPlugin {

    public static boolean DEBUG;

    private static DatabaseManager db = null;
    public static DatabaseManager getDB() { return db; }
    private static KuroDiscord kurodiscord = null;
    public static KuroDiscord getDiscord() { return kurodiscord; }
    private boolean linux = true;
    public boolean IsLinux() { return linux; }

    private static HashMap<Player, String> click_mode = new HashMap<Player, String>();
    public HashMap<Player, String> GetClickMode() { return click_mode; }

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
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        this.getServer().getPluginManager().registerEvents(new EntityListener(this), this);
        this.getServer().getPluginManager().registerEvents(new InventoryListener(this), this);

        // regist command
        getLogger().info(Language.translate("plugin.setup.command"));
        getCommand("list").setExecutor(new ListCommand(this));
        getCommand("name").setExecutor(new NameCommand(this));
        getCommand("tag").setExecutor(new TagCommand(this));
        getCommand("chest").setExecutor(new ChestCommand(this));

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
        kurodiscord = (KuroDiscord)getServer().getPluginManager().getPlugin("KuroDiscord");
        String os = System.getProperty("os.name").toLowerCase();
        if (os.toLowerCase().indexOf("windows") >= 0) {
            linux = false;
            kurodiscord.getDiscordMessage().SendDiscordBlueMessage(Language.translate("plugin.test"));
        } else {
            kurodiscord.getDiscordMessage().SendDiscordBlueMessage(Language.translate("plugin.start"));
            kurodiscord.getDiscordMessage().SendDiscordYellowMessage(Language.translate("plugin.information"));
        }
        getLogger().info(Language.translate("plugin.loaded"));
    }

    private void InitTables() {
        // player
        db.ExecuteUpdate(Language.translate("SQL.CREATE.PLAYER"), null);
        // log_err
        db.ExecuteUpdate(Language.translate("SQL.CREATE.LOG.ERROR"), null);
        // log_mat
        db.ExecuteUpdate(Language.translate("SQL.CREATE.LOG.MATERIAL"), null);
        // log_cmd
        db.ExecuteUpdate(Language.translate("SQL.CREATE.LOG.COMMAND"), null);
        // log_sign
        db.ExecuteUpdate(Language.translate("SQL.CREATE.LOG.SIGN"), null);
        // log_pay
        db.ExecuteUpdate(Language.translate("SQL.CREATE.LOG.PAY"), null);
        // chest
        db.ExecuteUpdate(Language.translate("SQL.CREATE.CHEST"), null);
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
