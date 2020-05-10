package host.kuro.kurobase;

import fr.moribus.imageonmap.ImageOnMap;
import host.kuro.kurobase.commands.*;
import host.kuro.kurobase.commands.Completers.*;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.listeners.BlockListener;
import host.kuro.kurobase.listeners.EntityListener;
import host.kuro.kurobase.listeners.InventoryListener;
import host.kuro.kurobase.listeners.PlayerListener;
import host.kuro.kurobase.shop.GuiShopHandler;
import host.kuro.kurobase.tasks.MinutesTask;
import host.kuro.kurobase.utils.AreaUtils;
import host.kuro.kurobase.utils.DataUtils;
import host.kuro.kurobase.utils.MtRand;
import host.kuro.kurodiscord.KuroDiscord;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;

public class KuroBase extends JavaPlugin {

    public static boolean DEBUG;

    private static DatabaseManager db = null;
    public static DatabaseManager getDB() { return db; }

    private static KuroDiscord kurodiscord = null;
    public static KuroDiscord getDiscord() { return kurodiscord; }

    private static ImageOnMap imageonmap = null;
    public static ImageOnMap getImageOnMap() { return imageonmap; }

    private boolean linux = true;
    public boolean IsLinux() { return linux; }

    private static MtRand random = new MtRand(System.currentTimeMillis());
    public static MtRand GetRand() { return random; }

    private static HashMap<Player, String> click_mode = new HashMap<Player, String>();
    public HashMap<Player, String> GetClickMode() { return click_mode; }

    private static HashMap<Player, Long> afk_status = new HashMap<Player, Long>();
    public HashMap<Player, Long> GetAfkStatus() { return afk_status; }

    private static HashMap<Player, Long> sound_battle = new HashMap<Player, Long>();
    public HashMap<Player, Long> GetSoundBattle() { return sound_battle; }

    private static HashMap<Player, Long> move_message = new HashMap<Player, Long>();
    public HashMap<Player, Long> GetMoveMessage() { return move_message; }

    private static HashMap<Player, AreaData> area_data = new HashMap<Player, AreaData>();
    public HashMap<Player, AreaData> GetAreaData() { return area_data; }

    private static ArrayList<AreaData> protect = new ArrayList<AreaData>();
    public static ArrayList<AreaData> GetProtect() { return protect; }

    private static HashMap<Player, String> frame_name = new HashMap<Player, String>();
    public HashMap<Player, String> GetFrame() { return frame_name; }

    @Override
    public void onEnable() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.toLowerCase().indexOf("windows") >= 0) {
            linux = false;
        }

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
        this.getServer().getPluginManager().registerEvents(new GuiShopHandler(), this);

        // regist command
        getLogger().info(Language.translate("plugin.setup.command"));
        getCommand("list").setExecutor(new ListCommand(this));
        getCommand("info").setExecutor(new InfoCommand(this));
        getCommand("map").setExecutor(new MapCommand(this));
        getCommand("shutdown").setExecutor(new ShutdownCommand(this));
        getCommand("bi").setExecutor(new BiCommand(this));
        getCommand("ri").setExecutor(new RiCommand(this));

        getCommand("name").setExecutor(new NameCommand(this));
        getCommand("name").setTabCompleter(new NameTabCompleter());
        getCommand("tag").setExecutor(new TagCommand(this));
        getCommand("tag").setTabCompleter(new TagTabCompleter());
        getCommand("chest").setExecutor(new ChestCommand(this));
        getCommand("chest").setTabCompleter(new ChestTabCompleter());
        getCommand("price").setExecutor(new PriceCommand(this));
        getCommand("price").setTabCompleter(new PriceTabCompleter());
        getCommand("shop").setExecutor(new ShopCommand(this));
        getCommand("shop").setTabCompleter(new ShopTabCompleter());
        getCommand("pay").setExecutor(new PayCommand(this));
        getCommand("pay").setTabCompleter(new PayTabCompleter());
        getCommand("rand").setExecutor(new RandCommand(this));
        getCommand("rand").setTabCompleter(new RandTabCompleter());
        getCommand("area").setExecutor(new AreaCommand(this));
        getCommand("area").setTabCompleter(new AreaTabCompleter());
        getCommand("home").setExecutor(new HomeCommand(this));
        getCommand("home").setTabCompleter(new HomeTabCompleter());

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

        // task open
        getLogger().info(Language.translate("plugin.setup.task"));
        MinutesTask minutes_task = new MinutesTask(this);
        minutes_task.runTaskTimer(this, 200, 1200);

        // check world data
        if (IsLinux()) {
            getLogger().info(Language.translate("plugin.setup.worlddata"));
            DataUtils.RefreshChestData(db);
        }

        // setup protect data
        getLogger().info(Language.translate("plugin.setup.protectdata"));
        AreaUtils.SetupProtectData();

        // load discord plugin
        if (!LoadDependPluginKuroDiscord()) {
            disablePlugin();
            return;
        }
        kurodiscord = (KuroDiscord)getServer().getPluginManager().getPlugin("KuroDiscord");

        // load imageonmap plugin
        if (!LoadDependPluginImageOnMap()) {
            disablePlugin();
            return;
        }
        imageonmap = (ImageOnMap)getServer().getPluginManager().getPlugin("ImageOnMap");

        if (!linux) {
            kurodiscord.getDiscordMessage().SendDiscordBlueMessage(Language.translate("plugin.test"));
        } else {
            kurodiscord.getDiscordMessage().SendDiscordBlueMessage(Language.translate("plugin.start"));
            kurodiscord.getDiscordMessage().SendDiscordYellowMessage(Language.translate("plugin.information.discord"));
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
        // log_mob
        db.ExecuteUpdate(Language.translate("SQL.CREATE.LOG.MOB"), null);
        // chest
        db.ExecuteUpdate(Language.translate("SQL.CREATE.CHEST"), null);
        // price
        db.ExecuteUpdate(Language.translate("SQL.CREATE.PRICE"), null);
        // command
        db.ExecuteUpdate(Language.translate("SQL.CREATE.COMMAND"), null);
        // area
        db.ExecuteUpdate(Language.translate("SQL.CREATE.AREA"), null);
        // views
        db.ExecuteUpdate(Language.translate("SQL.CREATE.VIEW.ELAPSE"), null);
        db.ExecuteUpdate(Language.translate("SQL.CREATE.VIEW.STATUS"), null);
        // indexes
        db.ExecuteUpdate(Language.translate("SQL.CREATE.INDEX.AREA_NAME"), null);
        db.ExecuteUpdate(Language.translate("SQL.CREATE.INDEX.PRICE_ID_NAME"), null);
        db.ExecuteUpdate(Language.translate("SQL.CREATE.INDEX.PRICE_NAME"), null);
        // UPDATE
        if (IsLinux()) {
            int ret = db.ExecuteUpdate(Language.translate("SQL.LOAD.UPDATE.PLAYER"), null);
        }
    }

    private boolean LoadDependPluginKuroDiscord() {
        RegisteredServiceProvider<KuroDiscord> rsp = getServer().getServicesManager().getRegistration(KuroDiscord.class);
        if (rsp == null) {
            return false;
        }
        kurodiscord = rsp.getProvider();
        return kurodiscord != null;
    }
    private boolean LoadDependPluginImageOnMap() {
        RegisteredServiceProvider<ImageOnMap> rsp = getServer().getServicesManager().getRegistration(ImageOnMap.class);
        if (rsp == null) {
            return false;
        }
        imageonmap = rsp.getProvider();
        return imageonmap != null;
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
