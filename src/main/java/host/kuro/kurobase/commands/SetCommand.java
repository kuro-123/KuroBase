package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.tasks.MinutesTask;
import host.kuro.kurobase.tasks.WorldEditTask;
import host.kuro.kurobase.utils.*;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

public class SetCommand implements CommandExecutor {

    private final KuroBase plugin;

    public SetCommand(KuroBase plugin) {
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
        if (args.length != 1) {
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

            // check material choise
            String name = args[0].toUpperCase();
            Material mat = Material.getMaterial(name);
            if (mat == null) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.material.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                ClearMemory(player);
                return false;
            }
            if (mat != Material.AIR) {
                if (!(mat.isBlock() && mat.isSolid())) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.material.error"));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    ClearMemory(player);
                    return false;
                }
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
            int max_block = plugin.getConfig().getInt("WorldEdit.block_max", 40000);
            if (count > max_block) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.over"));
                SoundUtils.PlaySound(player,"cancel5", false);
                ClearMemory(player);
                return false;
            }

            WorldEditTask we_task = new WorldEditTask(plugin, player, "set", count);
            we_task.SetMaterialOne(mat);
            int delay = plugin.getConfig().getInt("WorldEdit.task_delay", 2);
            BukkitTask task = we_task.runTaskTimer(plugin, 0, delay);
            we_task.SetTask(task);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            ClearMemory(player);
            return false;
        }
        ClearMemory(player);
        return true;
    }

    private void ClearMemory(Player player) {
        plugin.GetClickMode().remove(player);
        plugin.GetSelectDataOne().remove(player);
        plugin.GetSelectDataTwo().remove(player);
        plugin.GetSelectStatus().remove(player);
    }
}
