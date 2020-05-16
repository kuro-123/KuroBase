package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.tasks.WorldEditTask;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.InteractUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class PasteCommand implements CommandExecutor {

    private final KuroBase plugin;

    public PasteCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console check
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        final Player player = (Player)sender;
        if (args.length != 0) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            ClearMemory(player);
            return false;
        }
        try {
            // check city world
            if (!PlayerUtils.IsCityWorld(plugin, player)) {
                int rank = PlayerUtils.GetRank(plugin, player);
                if (rank < PlayerUtils.RANK_NUSHI) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    return false;
                }
            }
            // check creative
            if (player.getGameMode() != GameMode.CREATIVE) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.creative_only"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }
            // check click mode
            if (plugin.GetClickMode().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                ClearMemory(player);
                return false;
            }
            // check selection one
            if (!plugin.GetSelectDataOne().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                ClearMemory(player);
                return false;
            }
            // check selection two
            if (!plugin.GetSelectDataTwo().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                ClearMemory(player);
                return false;
            }
            Location loc1 = plugin.GetSelectDataOne().get(player);
            Location loc2 = plugin.GetSelectDataTwo().get(player);
            // check location
            if (loc1 == null || loc2 == null) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                ClearMemory(player);
                return false;
            }
            // check exec
            if (plugin.GetExecWE().size() > 0) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.erroe.exec"));
                SoundUtils.PlaySound(player, "cancel5", false);
                ClearMemory(player);
                return false;
            }
            int count = InteractUtils.SelectionBlock(plugin, player);
            if (count <= 0) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                ClearMemory(player);
                return false;
            }

            plugin.GetSelectStatus().remove(player);

            // change mode
            if (plugin.GetClickMode().containsKey(player)) {
                String click_mode = plugin.GetClickMode().get(player);
                if (click_mode.equals("paste")) {
                    plugin.GetClickMode().remove(player);
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.paste.modeoff"));
                    SoundUtils.PlaySound(player, "switch1", false);
                } else {
                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.paste.modeon"));
                    SoundUtils.PlaySound(player, "switch1", false);
                    plugin.GetClickMode().put(player, "paste");
                }
            } else {
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.paste.modeon"));
                SoundUtils.PlaySound(player, "switch1", false);
                plugin.GetClickMode().put(player, "paste");
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            ClearMemory(player);
            return false;
        }
        return true;
    }

    private void ClearMemory(Player player) {
        plugin.GetClickMode().remove(player);
        plugin.GetSelectDataOne().remove(player);
        plugin.GetSelectDataTwo().remove(player);
        plugin.GetSelectStatus().remove(player);
    }
}
