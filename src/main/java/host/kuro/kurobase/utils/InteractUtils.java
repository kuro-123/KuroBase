package host.kuro.kurobase.utils;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.tasks.WorldEditTask;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class InteractUtils {

    private static BlockFace[] allowedBlockFaces = {BlockFace.EAST, BlockFace.WEST, BlockFace.SOUTH, BlockFace.NORTH};

    public static void ClickBlockId(KuroBase plugin, PlayerInteractEvent e, Player player, Block block) {
        if (block != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(ChatColor.GREEN);
            sb.append(block.getBlockData().getMaterial().toString() + "  ");
            sb.append(ChatColor.YELLOW);
            sb.append(block.getLocation().getWorld().getName());
            sb.append(" (");
            sb.append(block.getLocation().getBlockX());
            sb.append(", ");
            sb.append(block.getLocation().getBlockY());
            sb.append(", ");
            sb.append(block.getLocation().getBlockZ());
            sb.append(")");
            PlayerUtils.SendActionBar(player, new String(sb));
        }
        e.setCancelled(true);
    }

    public static void ClickSelect(KuroBase plugin, PlayerInteractEvent e, Player player, Block block) {
        String message;
        try {
            int status = 0;
            if (plugin.GetSelectStatus().containsKey(player)) {
                status = plugin.GetSelectStatus().get(player);
            }
            if (status == 0) {
                // point one
                plugin.GetSelectDataOne().put(player, block.getLocation());
                plugin.GetSelectStatus().put(player, 1);
                message = String.format(ChatColor.YELLOW + "1点目を設定しました！ [ %d,%d,%d ]", block.getLocation().getBlockX(), block.getLocation().getBlockY(), block.getLocation().getBlockZ());
                player.sendMessage(message);
                SoundUtils.PlaySound(player, "kotsudumi1", false);

            } else if (status == 1) {
                // point two
                plugin.GetSelectDataTwo().put(player, block.getLocation());
                plugin.GetSelectStatus().put(player, 2);
                int count = SelectionBlock(plugin, player);
                message = String.format(ChatColor.YELLOW + "2点目を設定しました！ [ %d,%d,%d ] [選択範囲: %sﾏｽ] [土地価格はﾏｽ数x2です]"
                        , block.getLocation().getBlockX()
                        , block.getLocation().getBlockY()
                        , block.getLocation().getBlockZ()
                        , StringUtils.numFmt.format(count));
                player.sendMessage(message);
                SoundUtils.PlaySound(player, "kotsudumi1", false);
                plugin.GetClickMode().remove(player);
                plugin.GetSelectStatus().remove(player);
            }

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        e.setCancelled(true);
    }

    public static void ClickPaste(KuroBase plugin, PlayerInteractEvent e, Player player, Block block) {
        String message;
        try {
            // check selection one
            if (!plugin.GetSelectDataOne().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                e.setCancelled(true);
                return;
            }
            // check selection two
            if (!plugin.GetSelectDataTwo().containsKey(player)) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                e.setCancelled(true);
                return;
            }
            Location loc1 = plugin.GetSelectDataOne().get(player);
            Location loc2 = plugin.GetSelectDataTwo().get(player);
            // check location
            if (loc1 == null || loc2 == null) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.error"));
                SoundUtils.PlaySound(player,"cancel5", false);
                e.setCancelled(true);
                plugin.GetClickMode().remove(player);
                return;
            }
            // check exec
            if (plugin.GetExecWE().size() > 0) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.erroe.exec"));
                SoundUtils.PlaySound(player, "cancel5", false);
                e.setCancelled(true);
                plugin.GetClickMode().remove(player);
                return;
            }
            int count = 0;
            int max_block = plugin.getConfig().getInt("WorldEdit.block_max", 40000);
            if (count > max_block) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("commands.set.over"));
                SoundUtils.PlaySound(player,"cancel5", false);
                e.setCancelled(true);
                plugin.GetClickMode().remove(player);
                return;
            }

            WorldEditTask we_task = new WorldEditTask(plugin, player, "paste", count);
            we_task.SetStart(block.getX(), block.getY(), block.getZ());
            int delay = plugin.getConfig().getInt("WorldEdit.task_delay", 2);
            BukkitTask task = we_task.runTaskTimer(plugin, 0, delay);
            we_task.SetTask(task);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
            plugin.GetClickMode().remove(player);
            e.setCancelled(true);
            return;
        }
        plugin.GetClickMode().remove(player);
        e.setCancelled(true);
    }

    public static final int SelectionBlock(KuroBase plugin, Player player) {
        int count = 0;
        Location loc1 = plugin.GetSelectDataOne().get(player);
        Location loc2 = plugin.GetSelectDataTwo().get(player);

        try {
            if (loc1 == null || loc2 == null) return count;

            int lx1 = loc1.getBlockX();
            int ly1 = loc1.getBlockY();
            int lz1 = loc1.getBlockZ();
            int lx2 = loc2.getBlockX();
            int ly2 = loc2.getBlockY();
            int lz2 = loc2.getBlockZ();

            boolean replace = true;
            if (lx1 < lx2) {
                replace = false;
            } else {
                if (lx1 == lx2) {
                    if (ly1 < ly2) {
                        replace = false;
                    } else {
                        if (ly1 == ly2) {
                            if (lz1 < lz2) {
                                replace = false;
                            }
                        }
                    }
                }
            }
            if (replace) {
                int temp_x = lx1;
                int temp_y = ly1;
                int temp_z = lz1;
                lx1 = lx2;
                ly1 = ly2;
                lz1 = lz2;
                lx2 = temp_x;
                ly2 = temp_y;
                lz2 = temp_z;
            }

            int x1 = Math.min(lx1, lx2);
            int x2 = Math.max(lx1, lx2);
            int y1 = Math.min(ly1, ly2);
            int y2 = Math.max(ly1, ly2);
            int z1 = Math.min(lz1, lz2);
            int z2 = Math.max(lz1, lz2);
            for (int i=x1; i<=x2; i++) {
                for (int j=y1; j<=y2; j++) {
                    for (int k=z1; k<=z2; k++) {
                        count++;
                    }
                }
            }

            loc1.setX(lx1);
            loc1.setY(ly1);
            loc1.setZ(lz1);
            plugin.GetSelectDataOne().put(player, loc1);

            loc2.setX(lx2);
            loc2.setY(ly2);
            loc2.setZ(lz2);
            plugin.GetSelectDataTwo().put(player, loc2);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return count;
    }

}
