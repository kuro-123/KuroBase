package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BlockListener implements Listener {

    KuroBase plugin = null;

    public BlockListener(KuroBase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        int ret;
        try {
            Player player = e.getPlayer();
            Block block = e.getBlock();

            // check world
            if (PlayerUtils.IsCityWorld(plugin, player)) {
                GameMode mode = player.getGameMode();
                if (mode != GameMode.CREATIVE) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    e.setCancelled(true);
                    return;
                }
                if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
                    AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                    if (area != null) {
                        player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                        SoundUtils.PlaySound(player, "cancel5", false);
                        e.setCancelled(true);
                        return;
                    }
                }
            } else {
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.BREAK.UPDATE.PLAYER"), args);
                args.clear();
                args = null;
            }

            // check area
            if (PlayerUtils.IsSurvivalWorld(plugin, player)) {
                AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                if (area != null) {
                    player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    e.setCancelled(true);
                    return;
                }
            }

            StringBuilder sb = new StringBuilder();
            String name;
            for (ItemStack itemstack: block.getDrops()) {
                if(itemstack.hasItemMeta()){
                    name = itemstack.getItemMeta().getDisplayName();
                }
                else{
                    name = itemstack.getType().toString();
                }
                sb.append(name);
                sb.append(",");
            }
            String result = new String(sb);
            // INSERT
            ArrayList<DatabaseArgs> bargs = new ArrayList<DatabaseArgs>();
            bargs.add(new DatabaseArgs("c", block.getLocation().getWorld().getName())); // world
            bargs.add(new DatabaseArgs("i", ""+block.getLocation().getBlockX())); // x
            bargs.add(new DatabaseArgs("i", ""+block.getLocation().getBlockY())); // y
            bargs.add(new DatabaseArgs("i", ""+block.getLocation().getBlockZ())); // z
            bargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
            bargs.add(new DatabaseArgs("c", block.getBlockData().getMaterial().toString())); // name
            bargs.add(new DatabaseArgs("c", "break")); // action
            bargs.add(new DatabaseArgs("c", result)); // result
            ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.MATERIAL.INSERT"), bargs);
            bargs.clear();
            bargs = null;

            String target = block.getType().toString();
            if (target.toLowerCase().indexOf("chest") >= 0) {
                // check chest data
                DataUtils.RefreshChestData(plugin.getDB());
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        int ret;
        try {
            Player player = e.getPlayer();
            Block block = e.getBlock();

            // check world
            if (PlayerUtils.IsCityWorld(plugin, player)) {
                GameMode mode = player.getGameMode();
                if (mode != GameMode.CREATIVE) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    e.setCancelled(true);
                    return;
                }
                if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
                    AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                    if (area != null) {
                        player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                        SoundUtils.PlaySound(player, "cancel5", false);
                        e.setCancelled(true);
                        return;
                    }
                }
            } else {
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PLACE.UPDATE.PLAYER"), args);
                args.clear();
                args = null;
            }

            // check area
            if (PlayerUtils.IsSurvivalWorld(plugin, player)) {
                AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                if (area != null) {
                    player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    e.setCancelled(true);
                    return;
                }
            }

            // INSERT
            ArrayList<DatabaseArgs> bargs = new ArrayList<DatabaseArgs>();
            bargs.add(new DatabaseArgs("c", block.getLocation().getWorld().getName())); // world
            bargs.add(new DatabaseArgs("i", ""+block.getLocation().getBlockX())); // x
            bargs.add(new DatabaseArgs("i", ""+block.getLocation().getBlockY())); // y
            bargs.add(new DatabaseArgs("i", ""+block.getLocation().getBlockZ())); // z
            bargs.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
            bargs.add(new DatabaseArgs("c", block.getBlockData().getMaterial().toString())); // name
            bargs.add(new DatabaseArgs("c", "place")); // action
            bargs.add(new DatabaseArgs("c", "")); // result
            ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.MATERIAL.INSERT"), bargs);
            bargs.clear();
            bargs = null;

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        try {
            Block block = e.getBlock();
            Player player = e.getPlayer();

            // check world
            if (PlayerUtils.IsCityWorld(plugin, player)) {
                GameMode mode = player.getGameMode();
                if (mode != GameMode.CREATIVE) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    e.setCancelled(true);
                    return;
                }
                if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
                    AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                    if (area != null) {
                        player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                        SoundUtils.PlaySound(player, "cancel5", false);
                        e.setCancelled(true);
                        return;
                    }
                }
            }

            // check area
            AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
            if (area != null) {
                player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                SoundUtils.PlaySound(player,"cancel5", false);
                e.setCancelled(true);
                return;
            }

            String[] lines = e.getLines();
            String line1 = "";
            String line2 = "";
            String line3 = "";
            String line4 = "";
            int i=0;
            for (String line: lines) {
                if (i == 0) {
                    line1 = line;
                } else if (i == 1) {
                    line2 = line;
                } else if (i == 2) {
                    line3 = line;
                } else if (i == 3) {
                    line4 = line;
                }
                i++;
            }

            // UPDATE
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", block.getLocation().getWorld().getName())); // world
            args.add(new DatabaseArgs("i", ""+block.getLocation().getBlockX())); // x
            args.add(new DatabaseArgs("i", ""+block.getLocation().getBlockY())); // y
            args.add(new DatabaseArgs("i", ""+block.getLocation().getBlockZ())); // z
            args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
            args.add(new DatabaseArgs("c", line1)); // line1
            args.add(new DatabaseArgs("c", line2)); // line2
            args.add(new DatabaseArgs("c", line3)); // line3
            args.add(new DatabaseArgs("c", line4)); // line4
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.SIGN.INSERT"), args);
            args.clear();
            args = null;

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }
}