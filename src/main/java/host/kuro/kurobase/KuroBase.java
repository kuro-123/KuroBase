package host.kuro.kurobase;

import host.kuro.kurobase.commands.*;
import host.kuro.kurobase.commands.Completers.*;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.listeners.*;
import host.kuro.kurobase.shop.GuiShopHandler;
import host.kuro.kurobase.tasks.MinutesTask;
import host.kuro.kurobase.trait.*;
import host.kuro.kurobase.utils.AreaUtils;
import host.kuro.kurobase.utils.DataUtils;
import host.kuro.kurobase.utils.MtRand;
import host.kuro.kurodiscord.KuroDiscord;
import net.citizensnpcs.Citizens;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.trait.TraitInfo;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;

public class KuroBase extends JavaPlugin {

    public static boolean DEBUG;

    private static KuroBase instance = null;
    public static KuroBase GetInstance() { return instance; }

    private static DatabaseManager db = null;
    public static DatabaseManager getDB() { return db; }

    private static KuroDiscord kurodiscord = null;
    public static KuroDiscord getDiscord() { return kurodiscord; }

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
    private static HashMap<Player, String> frame_name = new HashMap<Player, String>();
    public HashMap<Player, String> GetFrame() { return frame_name; }
    private static HashMap<Player, Integer> rank = new HashMap<Player, Integer>();
    public HashMap<Player, Integer> GetRank() { return rank; }
    private static HashMap<Player, Integer> select_status = new HashMap<Player, Integer>();
    public HashMap<Player, Integer> GetSelectStatus() { return select_status; }
    private static HashMap<Player, Location> select_data_one = new HashMap<Player, Location>();
    public HashMap<Player, Location> GetSelectDataOne() { return select_data_one; }
    private static HashMap<Player, Location> select_data_two = new HashMap<Player, Location>();
    public HashMap<Player, Location> GetSelectDataTwo() { return select_data_two; }
    private static HashMap<Player, Long> interact_wait = new HashMap<Player, Long>();
    public HashMap<Player, Long> GetInteractWait() { return interact_wait; }
    private static HashMap<Player, Integer> exec_we = new HashMap<Player, Integer>();
    public HashMap<Player, Integer> GetExecWE() { return exec_we; }
    private static HashMap<Player, Boolean> pvp = new HashMap<Player, Boolean>();
    public HashMap<Player, Boolean> GetPvp() { return pvp; }

    private static ArrayList<AreaData> protect = new ArrayList<AreaData>();
    public static ArrayList<AreaData> GetProtect() { return protect; }

    private static Citizens citizen_plugin = null;
    public static Citizens GetCitizens() { return citizen_plugin; }
    private static CitizenListener citizen_listener = null;
    private static TraitInfo citizen_type_trait;
    private static TraitInfo citizen_trait;
    private static TraitInfo citizen_master_trait;
    private static TraitInfo citizen_text_trait;
    private static TraitInfo citizen_explaner_trait;


    @Override
    public void onEnable() {
        instance = this;

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
        this.getServer().getPluginManager().registerEvents(new WetherListener(this), this);
        this.getServer().getPluginManager().registerEvents(new GuiShopHandler(), this);

        // regist command
        getLogger().info(Language.translate("plugin.setup.command"));
        getCommand("list").setExecutor(new ListCommand(this));
        getCommand("info").setExecutor(new InfoCommand(this));
        getCommand("map").setExecutor(new MapCommand(this));
        getCommand("shutdown").setExecutor(new ShutdownCommand(this));
        getCommand("bi").setExecutor(new BiCommand(this));
        getCommand("ri").setExecutor(new RiCommand(this));
        getCommand("creative").setExecutor(new CreativeCommand(this));
        getCommand("survival").setExecutor(new SurvivalCommand(this));
        getCommand("sel").setExecutor(new SelCommand(this));
        getCommand("paste").setExecutor(new PasteCommand(this));

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
        getCommand("set").setExecutor(new SetCommand(this));
        getCommand("set").setTabCompleter(new SetTabCompleter());
        getCommand("rep").setExecutor(new RepCommand(this));
        getCommand("rep").setTabCompleter(new RepTabCompleter());
        getCommand("buddy").setExecutor(new BuddyCommand(this));
        getCommand("buddy").setTabCompleter(new BuddyTabCompleter());
        getCommand("ai").setExecutor(new AiCommand(this));
        getCommand("ai").setTabCompleter(new AiTabCompleter());
        getCommand("rule").setExecutor(new RuleCommand(this));
        getCommand("rule").setTabCompleter(new RuleTabCompleter());
        getCommand("pvp").setExecutor(new PvpCommand(this));
        getCommand("pvp").setTabCompleter(new PvpTabCompleter());

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

        // load citizens plugin
        getLogger().info(Language.translate("plugin.setup.citizens"));
        citizen_plugin = (Citizens)getServer().getPluginManager().getPlugin("Citizens");
        if(citizen_plugin == null || citizen_plugin.isEnabled() == false) {
            disablePlugin();
            return;
        }
        if (!EnableTrait()) {
            disablePlugin();
            return;
        }
        citizen_listener = new CitizenListener();
        this.getServer().getPluginManager().registerEvents(citizen_listener, this);

        // load discord plugin
        if (linux) {
            if (!LoadDependPluginKuroDiscord()) {
                disablePlugin();
                return;
            }
            kurodiscord = (KuroDiscord)getServer().getPluginManager().getPlugin("KuroDiscord");
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
        // entity
        db.ExecuteUpdate(Language.translate("SQL.CREATE.ENTITY"), null);

        // views
        db.ExecuteUpdate(Language.translate("SQL.CREATE.VIEW.ELAPSE"), null);
        db.ExecuteUpdate(Language.translate("SQL.CREATE.VIEW.STATUS"), null);

        // indexes
        db.ExecuteUpdate(Language.translate("SQL.CREATE.INDEX.AREA_NAME"), null);
        db.ExecuteUpdate(Language.translate("SQL.CREATE.INDEX.PRICE_ID_NAME"), null);
        db.ExecuteUpdate(Language.translate("SQL.CREATE.INDEX.PRICE_NAME"), null);
        db.ExecuteUpdate(Language.translate("SQL.CREATE.INDEX.ENTITY_NAME"), null);

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

    private boolean EnableTrait() {
        try {
            // original trait
            TraitInfo citizen_type_trait = TraitInfo.create(BaseTypeTrait.class).withName("BaseTypeTrait");
            CitizensAPI.getTraitFactory().registerTrait(citizen_type_trait);
            TraitInfo citizen_trait = TraitInfo.create(KuroTrait.class).withName("KuroTrait");
            CitizensAPI.getTraitFactory().registerTrait(citizen_trait);
            TraitInfo citizen_master_trait = TraitInfo.create(BuddyMasterTrait.class).withName("BuddyMasterTrait");
            CitizensAPI.getTraitFactory().registerTrait(citizen_master_trait);
            TraitInfo citizen_cmaster_trait = TraitInfo.create(CreativeMasterTrait.class).withName("CreativeMasterTrait");
            CitizensAPI.getTraitFactory().registerTrait(citizen_cmaster_trait);
            TraitInfo citizen_text_trait = TraitInfo.create(SendTextTrait.class).withName("SendTextTrait");
            CitizensAPI.getTraitFactory().registerTrait(citizen_text_trait);
            TraitInfo citizen_explaner_trait = TraitInfo.create(ExplanerTrait.class).withName("ExplanerTrait");
            CitizensAPI.getTraitFactory().registerTrait(citizen_explaner_trait);
        } catch (Exception ex) {
            return false;
        }
        return true;
    }

    private void DisableTrait() {
        if (citizen_trait != null) {
            try {
                CitizensAPI.getTraitFactory().deregisterTrait(citizen_trait);
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                citizen_trait = null;
            }
        }
        if (citizen_type_trait != null) {
            try {
                CitizensAPI.getTraitFactory().deregisterTrait(citizen_type_trait);
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                citizen_type_trait = null;
            }
        }
        if (citizen_master_trait != null) {
            try {
                CitizensAPI.getTraitFactory().deregisterTrait(citizen_master_trait);
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                citizen_master_trait = null;
            }
        }
        if (citizen_text_trait != null) {
            try {
                CitizensAPI.getTraitFactory().deregisterTrait(citizen_text_trait);
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                citizen_text_trait = null;
            }
        }
        if (citizen_explaner_trait != null) {
            try {
                CitizensAPI.getTraitFactory().deregisterTrait(citizen_explaner_trait);
            } catch (Throwable ex) {
                ex.printStackTrace();
            } finally {
                citizen_explaner_trait = null;
            }
        }
    }

    private void disablePlugin() {
        if (citizen_plugin != null) {
            DisableTrait();
        }
        if (citizen_listener != null) {
            HandlerList.unregisterAll(citizen_listener);
            citizen_listener = null;
        }
        getServer().getPluginManager().disablePlugin(this);
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
