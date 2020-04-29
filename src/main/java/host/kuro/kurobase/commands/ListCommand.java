package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class ListCommand implements CommandExecutor {

    private KuroBase plugin;

    public ListCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }

        final Player player = (Player)sender;
        new BukkitRunnable(){
            @Override
            public void run() {
                StringBuilder sb = new StringBuilder();
                int cnt=0;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sb.append("\n");
                    sb.append(ChatColor.DARK_GREEN);
                    sb.append(String.format("[ %s ワールド: %s 位置: %d,%d,%d ]", player.getName(), player.getLocation().getWorld().getName(), player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ()));
                    cnt++;
                }
                sb.append("\n");
                sb.append(ChatColor.DARK_GREEN);
                sb.append(String.format("オンライン : %d人\n", cnt));
                player.sendMessage(new String(sb));
            }
        }.runTaskAsynchronously(plugin);
        return true;
    }
}
