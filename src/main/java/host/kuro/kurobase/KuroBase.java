package host.kuro.kurobase;

import host.kuro.kurobase.lang.Language;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public class KuroBase extends JavaPlugin {

    public static boolean DEBUG;

    @Override
    public void onEnable() {
        // language setup
        String os = System.getProperty("os.name").toLowerCase();
        if (os.toLowerCase().indexOf("windows") >= 0) {
            Language.load("SJIS");
        } else {
            Language.load("UTF-8");
        }
        getLogger().info(Language.translate("plgin.setup.language"));

        // directory setup
        getLogger().info(Language.translate("plgin.setup.directory"));
        if(!getDataFolder().exists()) { getDataFolder().mkdir(); }

        // load settings
        getLogger().info(ChatColor.RED + Language.translate("plgin.setup.settings"));
        this.saveDefaultConfig();
        DEBUG = this.getConfig().getBoolean("debug", false);

        getLogger().info(Language.translate("plgin.loaded"));
    }

    @Override
    public void onDisable() {
        getLogger().info(Language.translate("plgin.unloaded"));
    }
}
