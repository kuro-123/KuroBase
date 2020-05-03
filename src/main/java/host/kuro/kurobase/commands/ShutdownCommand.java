package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.tasks.ShutdownTask;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ShutdownCommand implements CommandExecutor {

    private final KuroBase plugin;

    public ShutdownCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        boolean exec_ok = false;
        if(sender instanceof Player){
            // console
            plugin.getLogger().warning(Language.translate("plugin.player.error"));
            return false;
        }
        ShutdownTask task = new ShutdownTask(plugin);
        task.runTaskTimer(plugin, 0, 200);
        return true;
    }
}
