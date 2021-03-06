package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.*;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class BlockListener implements Listener {

    KuroBase plugin = null;

    public BlockListener(KuroBase plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent e) {
        // check rename
        HumanEntity entity = e.getView().getPlayer();
        AnvilInventory inv = e.getInventory();
        String rename = inv.getRenameText();
        if (rename.length() > 0) {
            entity.closeInventory();
        }
    }

    @EventHandler
    public void onBreak(BlockIgniteEvent e) {
        e.setCancelled(true);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        int ret;
        try {
            Player player = e.getPlayer();
            Block block = e.getBlock();

            if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
                // check area
                AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                if (area != null) {
                    player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    e.setCancelled(true);
                    return;
                }
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PLACE.UPDATE.PLAYER"), args);
                args.clear();
                args = null;
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
            if (block.getType() == Material.COAL_ORE ||
                block.getType() == Material.DIAMOND_ORE ||
                block.getType() == Material.EMERALD_ORE ||
                block.getType() == Material.GOLD_ORE ||
                block.getType() == Material.IRON_ORE ||
                block.getType() == Material.LAPIS_ORE ||
                block.getType() == Material.NETHER_QUARTZ_ORE ||
                block.getType() == Material.REDSTONE_ORE) {
                int val = KuroBase.GetRand().Next(1, 100);
                if (val == 1) {
                    RandomDrop(player);
                }
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    private void RandomDrop(Player player) {
        int amount = KuroBase.GetRand().Next(1, 3);
        ItemStack item = new ItemStack(Material.DIAMOND, amount);
        int x = KuroBase.GetRand().Next(-2, 2);
        int z = KuroBase.GetRand().Next(-2, 2);
        player.getWorld().dropItem(player.getLocation().add(x,1,z), item);
        PlayerUtils.BroadcastMessage(ChatColor.AQUA + String.format("%sさんは採掘中にダイアモンド%d個を発見した！", player.getDisplayName(), amount), true);
        SoundUtils.BroadcastSound("correct2", false);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        int ret;
        try {
            Player player = e.getPlayer();
            Block block = e.getBlock();

            if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
                // check area
                AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                if (area != null) {
                    player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    e.setCancelled(true);
                    return;
                }
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PLACE.UPDATE.PLAYER"), args);
                args.clear();
                args = null;
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

            if (PlayerUtils.GetRank(plugin, player) < PlayerUtils.RANK_KANRI) {
                // check area
                AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName(), block.getX(), block.getY(), block.getZ());
                if (area != null) {
                    player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                    SoundUtils.PlaySound(player, "cancel5", false);
                    e.setCancelled(true);
                    return;
                }
                // UPDATE
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // UUID
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.PLACE.UPDATE.PLAYER"), args);
                args.clear();
                args = null;
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