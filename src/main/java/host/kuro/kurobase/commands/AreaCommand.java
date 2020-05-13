package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
        final Player player = (Player)sender;

        if (args.length != 0 && args.length != 2) {
            // args check
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        plugin.GetClickMode().remove(player);

        if (args.length == 0) {
            // list
            return ActionList(player);
        }
        String action = args[0].toLowerCase();
        boolean ret;
        switch (action) {
            case "add":
                ret = ActionAdd(player, args);
                break;
            case "del":
                ret =ActionDel(player, args);
                break;
            default:
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.action.error"));
                ret = false;
        }
        if (!ret) {
            SoundUtils.PlaySound(player,"cancel5", false);
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

    private boolean ActionAdd(Player player, String[] in_args) {
        String name = in_args[1];
        if (!(3 <= name.length() && name.length() <= 16)) {
            // check arg 1
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.name.error"));
            return false;
        }

        try {
            boolean money_throw = false;

            // check survival world
            if (!PlayerUtils.IsSurvivalWorld(plugin, player)) {
                if (PlayerUtils.IsCityWorld(plugin, player)) {
                    if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
                        player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                        return false;
                    } else {
                        money_throw = true;
                    }
                } else {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                    return false;
                }
            } else {
                // check creative
                if (player.getGameMode() == GameMode.CREATIVE) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.creative"));
                    return false;
                }
            }
            // check click mode
            if (plugin.GetClickMode().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.select.error"));
                return false;
            }
            // check selection one
            if (!plugin.GetSelectDataOne().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                return false;
            }
            // check selection two
            if (!plugin.GetSelectDataTwo().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                return false;
            }
            Location loc1 = plugin.GetSelectDataOne().get(player);
            Location loc2 = plugin.GetSelectDataTwo().get(player);
            // check location
            if (loc1 == null || loc2 == null) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                return false;
            }
            // check area
            AreaData temp_area = AreaUtils.CheckInsideProtect(null, loc1.getWorld().getName(), loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ());
            if (temp_area != null) {
                player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", temp_area.owner, temp_area.name));
                return false;
            }
            temp_area = AreaUtils.CheckInsideProtect(null, loc2.getWorld().getName(), loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());
            if (temp_area != null) {
                player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", temp_area.owner, temp_area.name));
                return false;
            }
            // area count
            int count = InteractUtils.SelectionBlock(plugin, player);
            if (count <= 512) {
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.minimum.error"));
                return false;
            }
            int price = count * 2;
            int money = PlayerUtils.GetMoney(KuroBase.getDB(), player);
            if (!money_throw && money < price) {
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.pay.monerror"));
                return false;
            }

            AreaData area = new AreaData();
            area.world = loc1.getWorld().getName();
            area.name = name;
            area.owner = player.getName();
            area.x1 = loc1.getBlockX();
            area.y1 = loc1.getBlockY();
            area.z1 = loc1.getBlockZ();
            area.x2 = loc2.getBlockX();
            area.y2 = loc2.getBlockY();
            area.z2 = loc2.getBlockZ();

            // INSERT
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", area.world)); // world
            args.add(new DatabaseArgs("i", "" + area.x1)); // x1
            args.add(new DatabaseArgs("i", "" + area.y1)); // y1
            args.add(new DatabaseArgs("i", "" + area.z1)); // z1
            args.add(new DatabaseArgs("i", "" + area.x2)); // x2
            args.add(new DatabaseArgs("i", "" + area.y2)); // y2
            args.add(new DatabaseArgs("i", "" + area.z2)); // z2
            args.add(new DatabaseArgs("c", area.name)); // name
            args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
            args.add(new DatabaseArgs("c", area.owner)); // owner
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.AREA.INSERT"), args);
            args.clear();
            args = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.regist.error"));
                return false;
            }
            if (!money_throw) {
                // pay
                PlayerUtils.PayMoney(KuroBase.getDB(), player, price);
                // pay log
                PlayerUtils.AddLogAreaPay(player, "AREA", price);
            }

            // data resetup
            AreaUtils.SetupProtectData();

            new Location(player.getWorld(), area.x1, area.y1, area.z1).getBlock().setType(Material.BLUE_TERRACOTTA);
            new Location(player.getWorld(), area.x2, area.y2, area.z2).getBlock().setType(Material.BLUE_TERRACOTTA);
            String message = String.format(ChatColor.GREEN + "エリア [ %s ] は保護されました [現在の所持金: %s p]", area.name, StringUtils.numFmt.format(money - price));
            player.sendMessage(message);
            SoundUtils.PlaySound(player, "kotsudumi1", false);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        return true;
    }

    private boolean ActionDel(Player player, String[] args) {
        String name = args[1];
        if (!(3 <= name.length() && name.length() <= 16)) {
            // check arg 1
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.name.error"));
            return false;
        }

        // DELETE
        ArrayList<DatabaseArgs> dargs = new ArrayList<DatabaseArgs>();
        dargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
        dargs.add(new DatabaseArgs("c", name)); // name
        int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.AREA.DELETE.NAME"), dargs);
        dargs.clear();
        dargs = null;
        if (ret != 1) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.area.delete.error"));
            return false;
        }
        AreaUtils.DeleteAreaData(player.getName(), name);

        String message = String.format("保護エリア [ %s ] を削除しました", name);
        player.sendMessage(ChatColor.YELLOW + message);
        SoundUtils.PlaySound(player,"switch1", false);
        return true;
    }

    private boolean ActionList(Player player) {
        try {
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.AREAS.OWN"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                int cnt = 0;
                StringBuilder sb = new StringBuilder();
                while(rs.next()){
                    sb.append(ChatColor.DARK_GREEN);
                    sb.append(String.format("[ エリア:%s ワールド: %s 位置: %d,%d,%d - %d,%d,%d ]"
                            , rs.getString("name")
                            , rs.getString("world")
                            , rs.getInt("x1")
                            , rs.getInt("y1")
                            , rs.getInt("z1")
                            , rs.getInt("x2")
                            , rs.getInt("y2")
                            , rs.getInt("z2")));
                    sb.append("\n");
                    sb.append(ChatColor.DARK_GREEN);
                    cnt++;
                }
                sb.append(String.format("エリア数 : %d\n", cnt));
                player.sendMessage(new String(sb));
                SoundUtils.PlaySound(player,"switch1", false);
            }
            if (ps != null) {
                ps.close();
                ps = null;
            }
            if (rs != null) {
                rs.close();
                rs = null;
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            return false;
        }
        SoundUtils.PlaySound(player,"switch1", false);
        return true;
    }
}
