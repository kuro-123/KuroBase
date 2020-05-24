package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.AreaUtils;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Chest;
import org.bukkit.block.DoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

public class InventoryListener implements Listener {

    KuroBase plugin = null;

    private static final int init_val = -999999;

    public InventoryListener(KuroBase plugin) {
        this.plugin = plugin;
    }

    private String chest_mode = "";
    private String chest_owner = "";
    private int cX1 = init_val;
    private int cY1 = init_val;
    private int cZ1 = init_val;
    private int cX2 = init_val;
    private int cY2 = init_val;
    private int cZ2 = init_val;

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        if (!(event.getPlayer() instanceof Player)) {
            return;
        }
        InventoryHolder holder = event.getInventory().getHolder();
        Player player = Player.class.cast(event.getPlayer());

        // check city world
        if (PlayerUtils.IsCityWorld(plugin, player)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
            SoundUtils.PlaySound(player, "cancel5", false);
            event.setCancelled(true);
            return;
        }

        if (holder == null) return;

        Inventory inv = holder.getInventory();
        if (inv == null) return;

        Location loc = inv.getLocation();
        if (loc == null) return;

        // check area
        try {
            AreaData area = AreaUtils.CheckInsideProtect(player, player.getLocation().getWorld().getName()
                    , holder.getInventory().getLocation().getBlockX()
                    , holder.getInventory().getLocation().getBlockY()
                    , holder.getInventory().getLocation().getBlockZ());
            if (area != null) {
                if (!area.name.equals("バディー")) {
                    player.sendMessage(ChatColor.RED + String.format("ここは [ %s さん ] のエリア [ %s ] の敷地内です", area.owner, area.name));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    event.setCancelled(true);
                    return;
                }
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            SoundUtils.PlaySound(player,"cancel5", false);
            event.setCancelled(true);
            return;
        }

        Chest leftChest = null;
        Chest rightChest = null;
        if (holder instanceof Chest) {
            leftChest = Chest.class.cast(holder);

        } else if (holder instanceof DoubleChest) {
            DoubleChest doublechest = DoubleChest.class.cast(holder);
            leftChest = Chest.class.cast(doublechest.getLeftSide());
            rightChest = Chest.class.cast(doublechest.getRightSide());

        } else {
            return;
        }

        // check world
        if (PlayerUtils.IsCityWorld(plugin, player)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
            SoundUtils.PlaySound(player,"cancel5", false);
            event.setCancelled(true);
            return;
        }

        // click mode
        if (plugin.GetClickMode().containsKey(player)) {
            String click_mode = plugin.GetClickMode().get(player);
            // chest lock/unlock
            if (click_mode.equals("chestlock")) {
                if (ActionChest(event, player, leftChest, rightChest)) {
                    plugin.GetClickMode().remove(player);
                }
                return;
            }
        }

        // check lock
        chest_mode = "";
        chest_owner = "";
        if (!GetChestInfo(player, leftChest, rightChest)) {
            return;
        }
        if (chest_owner.length() <= 0) {
            return;
        }
        if (IsSelf(player)) {
            return;
        }
        player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.chest.lock.perm.other") + ChatColor.YELLOW + " [ " + chest_owner + " ]");
        SoundUtils.PlaySound(player, "cancel5", false);
        event.setCancelled(true);
    }

    private String SetLocation(Chest left, Chest right) {
        cX1 = init_val;
        cY1 = init_val;
        cZ1 = init_val;
        cX2 = init_val;
        cY2 = init_val;
        cZ2 = init_val;
        if (left != null) {
            cX1 = left.getX();
            cY1 = left.getY();
            cZ1 = left.getZ();
        }
        if (right != null) {
            cX2 = right.getX();
            cY2 = right.getY();
            cZ2 = right.getZ();
        }
        if ((cX1 == init_val && cY1 == init_val && cZ1 == init_val) && (cX2 != init_val && cY2 != init_val && cZ2 != init_val)) {
            cX1 = cX2;
            cY1 = cY2;
            cZ1 = cZ2;
            cX2 = init_val;
            cY2 = init_val;
            cZ2 = init_val;
        }
        if ((cX1 != init_val && cY1 != init_val && cZ1 != init_val) && (cX2 != init_val && cY2 != init_val && cZ2 != init_val)) {
            return "double";
        }
        return "single";
    }

    private boolean IsSelf(Player player) {
        if (chest_owner.length() <= 0) return true;
        if (chest_owner.toLowerCase().equals(player.getName().toLowerCase())) {
            return true;
        }
        return false;
    }

    private boolean ActionChest(InventoryOpenEvent event, Player player, Chest left, Chest right) {
        chest_mode = "";
        chest_owner = "";
        if (!GetChestInfo(player, left, right)) {
            return false;
        }
        if (chest_mode.length() == 0 || chest_mode.equals("unlock")) {
            return ActionChestLock(event, player, left, right, "lock");
        }
        else if (chest_mode.equals("lock")) {
            return ActionChestLock(event, player, left, right, "unlock");
        }
        return true;
    }

    private boolean ActionChestLock(InventoryOpenEvent event, Player player, Chest left, Chest right, String mode) {
        try {
            String type = SetLocation(left, right);

            if (chest_owner.length() <= 0) {
                // INSERT
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", player.getLocation().getWorld().getName()));
                args.add(new DatabaseArgs("i", ""+cX1));
                args.add(new DatabaseArgs("i", ""+cY1));
                args.add(new DatabaseArgs("i", ""+cZ1));
                args.add(new DatabaseArgs("i", ""+cX2));
                args.add(new DatabaseArgs("i", ""+cY2));
                args.add(new DatabaseArgs("i", ""+cZ2));
                args.add(new DatabaseArgs("c", type)); // type
                args.add(new DatabaseArgs("c", mode)); // mode
                args.add(new DatabaseArgs("c", player.getUniqueId().toString())); // uuid
                args.add(new DatabaseArgs("c", player.getName())); // updater
                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.CHEST.INSERT"), args);
                args.clear();
                args = null;
                if (ret != 1) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.update"));
                    SoundUtils.PlaySound(player,"cancel5", false);
                    return false;
                }
                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.chest.lock." + mode));
                SoundUtils.PlaySound(player,"switch1", false);

            } else {
                if (!IsSelf(player)) {
                    player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.chest.lock.perm.other") + ChatColor.YELLOW + " [ " + chest_owner + " ]");
                    SoundUtils.PlaySound(player, "cancel5", false);
                    event.setCancelled(true);
                    return true;
                }
            }

            // UPDATE
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", mode)); // mode
            args.add(new DatabaseArgs("c", player.getLocation().getWorld().getName()));
            args.add(new DatabaseArgs("i", ""+cX1));
            args.add(new DatabaseArgs("i", ""+cY1));
            args.add(new DatabaseArgs("i", ""+cZ1));
            args.add(new DatabaseArgs("i", ""+cX2));
            args.add(new DatabaseArgs("i", ""+cY2));
            args.add(new DatabaseArgs("i", ""+cZ2));
            int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.CHEST.UPDATE.MODE"), args);
            args.clear();
            args = null;
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.error.update"));
                SoundUtils.PlaySound(player,"cancel5", false);
                return false;
            }

            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.chest.lock." + mode));
            SoundUtils.PlaySound(player,"switch1", false);
            return true;

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return false;
    }

    private boolean GetChestInfo(Player player, Chest left, Chest right) {
        try {
            SetLocation(left, right);
            PreparedStatement ps = plugin.getDB().getConnection().prepareStatement(Language.translate("SQL.CHEST.CHECK"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getLocation().getWorld().getName()));
            args.add(new DatabaseArgs("i", ""+cX1));
            args.add(new DatabaseArgs("i", ""+cY1));
            args.add(new DatabaseArgs("i", ""+cZ1));
            args.add(new DatabaseArgs("i", ""+cX2));
            args.add(new DatabaseArgs("i", ""+cY2));
            args.add(new DatabaseArgs("i", ""+cZ2));
            ResultSet rs = plugin.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    chest_mode = rs.getString("mode");
                    chest_owner = rs.getString("updater");
                    break;
                }
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
        return true;
    }
}
