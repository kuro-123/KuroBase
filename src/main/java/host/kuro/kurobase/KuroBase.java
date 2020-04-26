package host.kuro.kurobase;

import org.bukkit.plugin.java.JavaPlugin;

public class KuroBase extends JavaPlugin {

    public static boolean DEBUG;

    @Override
    public void onEnable() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        this.saveDefaultConfig();
        DEBUG = this.getConfig().getBoolean("debug", false);

        //Config config = new Config(PlayerCapture.getInstance().getDataFolder() + "\\Recordings/" + name + ".yml");
        getLogger().info("Hello, world!");
    }
}
