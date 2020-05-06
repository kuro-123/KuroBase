package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class AreaCommand implements CommandExecutor {

    private final KuroBase plugin;

    public AreaCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console check
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        Player player = (Player)sender;
        if (args.length != 0 && args.length != 2) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (args.length == 0) {
            // cansel
            plugin.GetClickMode().remove(player);
            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.modeoff"));
            RemoveAreaData(player);
            SoundUtils.PlaySound(player,"switch1", false);
            return true;
        }

        String action = args[0].toLowerCase();
        if (!(action.equals("add") || action.equals("del"))) {
            // check arg 0
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.action.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        String name = args[1];
        if (!(3 <= name.length() && name.length() <= 16)) {
            // check arg 1
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.name.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        if (action.equals("add")) {
            // change mode
            if (plugin.GetClickMode().containsKey(player)) {
                String click_mode = plugin.GetClickMode().get(player);
                if (click_mode.equals("area")) {
                    plugin.GetClickMode().remove(player);
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.modeoff"));
                    RemoveAreaData(player);
                    SoundUtils.PlaySound(player,"switch1", false);
                } else {
                    plugin.GetClickMode().remove(player);
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.modeon"));
                    MakeAreaData(player, name);
                    SoundUtils.PlaySound(player,"switch1", false);
                    plugin.GetClickMode().put(player, "area");
                }
            } else {
                plugin.GetClickMode().remove(player);
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.modeon"));
                MakeAreaData(player, name);
                SoundUtils.PlaySound(player,"switch1", false);
                plugin.GetClickMode().put(player, "area");
            }
        } else {
            // DELETE
            ArrayList<DatabaseArgs> dargs = new ArrayList<DatabaseArgs>();
            dargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
            dargs.add(new DatabaseArgs("c", name)); // name
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.AREA.DELETE.NAME"), dargs);
            dargs.clear();
            dargs = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.delete.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            String message = String.format("保護エリア [ %s ] を削除しました", name);
            player.sendMessage(ChatColor.YELLOW + message);
            SoundUtils.PlaySound(player,"switch1", false);
        }
        return true;
    }

    private void MakeAreaData(Player player, String name) {
        AreaData area = new AreaData();
        area.owner = player.getName();
        area.name = name;
        plugin.GetAreaData().put(player, area);
    }
    private void RemoveAreaData(Player player) {
        plugin.GetAreaData().remove(player);
    }
}
