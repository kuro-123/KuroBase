package host.kuro.kurobase.tasks;

import com.sun.tools.javac.comp.Check;
import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurodiscord.DiscordMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;

public class WorldEditTask extends BukkitRunnable {

    private final KuroBase plugin;
    private final Player player;
    private final String mode;
    private final int count;
    private final int make_max;
    private BukkitTask task = null;
    private boolean end = false;
    private Material set1 = null;
    private Material set2 = null;
    private final Location loc1;
    private final Location loc2;
    private boolean firstTake = true;

    private int exec_count = 0;
    private final int x1;
    private final int x2;
    private final int y1;
    private final int y2;
    private final int z1;
    private final int z2;

    private int start_x;
    private int start_y;
    private int start_z;

    private ArrayList<Block> blocks;

    public WorldEditTask(KuroBase plugin, Player player, String mode, int count) {
        this.plugin = plugin;
        this.player = player;
        this.mode = mode;
        this.count = count;
        loc1 = plugin.GetSelectDataOne().get(player);
        loc2 = plugin.GetSelectDataTwo().get(player);
        x1 = Math.min(loc1.getBlockX(), loc2.getBlockX());
        x2 = Math.max(loc1.getBlockX(), loc2.getBlockX());
        y1 = Math.min(loc1.getBlockY(), loc2.getBlockY());
        y2 = Math.max(loc1.getBlockY(), loc2.getBlockY());
        z1 = Math.min(loc1.getBlockZ(), loc2.getBlockZ());
        z2 = Math.max(loc1.getBlockZ(), loc2.getBlockZ());
        make_max = plugin.getConfig().getInt("WorldEdit.task_block", 10);
    }

    public void SetTask(BukkitTask task) {
        this.task = task;
    }
    public void SetMaterialOne(Material set1) {
        this.set1 = set1;
    }
    public void SetMaterialTwo(Material set2) {
        this.set2 = set2;
    }
    public void SetStart(int x, int y, int z) {
        this.start_x = x;
        this.start_y = y;
        this.start_z = z;

        blocks = new ArrayList<Block>();
        int i=0,j=0,k=0;
        for (i=x1; i<=x2; i++) {
            for (j=y1; j<=y2; j++) {
                for (k=z1; k<=z2; k++) {
                    Block block = new Location(loc1.getWorld(), i, j, k).getBlock();
                    blocks.add(block);
                }
            }
        }
    }

    @Override
    public void run() {
        if (task == null) return;
        if (end) return;
        switch (mode) {
            case "set": ActionSet(); return;
            case "rep": ActionRep(); return;
            case "paste": ActionPaste(); return;
        }
        exec_count++;
        if (exec_count > 5000) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.we.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            end = true;
            task.cancel();
            plugin.GetExecWE().remove(player);
        }
    }

    private void ActionSet() {
        if (set1 == null) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.we.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            end = true;
            task.cancel();
            plugin.GetExecWE().remove(player);
            return;
        }
        if (firstTake) {
            plugin.GetExecWE().put(player, 1);
            PlayerUtils.BroadcastMessage(ChatColor.YELLOW + String.format("[ %s ] さんが [WorldEdit SET 開始] [ 範囲: %d ﾌﾞﾛｯｸ ]", player.getDisplayName(), count));
            SoundUtils.PlaySound(player, "cork-plug1", false);
            firstTake = false;
        }
        try {
            boolean hit=false;
            int make_cnt=0;
            int i=0,j=0,k=0;
            for (i=x1; i<=x2; i++) {
                for (j=y1; j<=y2; j++) {
                    for (k=z1; k<=z2; k++) {
                        Block block = new Location(loc1.getWorld(), i, j, k).getBlock();
                        if (block.getType().hasGravity()) continue;
                        if (block.getType() != set1) {
                            block.setType(set1);
                            make_cnt++;
                            hit = true;
                        }
                        if (make_cnt >= make_max) break;
                    }
                    if (make_cnt >= make_max) break;
                }
                if (make_cnt >= make_max) break;
            }
            if (!hit) {
                // finish
                end = true;
                task.cancel();
                PlayerUtils.BroadcastMessage(ChatColor.GREEN + String.format("[ %s ] さんの WorldEditが終了", player.getDisplayName()));
                SoundUtils.PlaySound(player,"cork-plug1", false);
                plugin.GetExecWE().remove(player);
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.we.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            end = true;
            task.cancel();
            plugin.GetExecWE().remove(player);
        }
    }

    private void ActionRep() {
        if (set1 == null || set2 == null) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.we.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            end = true;
            task.cancel();
            plugin.GetExecWE().remove(player);
            return;
        }
        if (firstTake) {
            plugin.GetExecWE().put(player, 1);
            PlayerUtils.BroadcastMessage(ChatColor.YELLOW + String.format("[ %s ] さんが [WorldEdit REP 開始] [ 範囲: %d ﾌﾞﾛｯｸ ]", player.getDisplayName(), count));
            SoundUtils.PlaySound(player, "cork-plug1", false);
            firstTake = false;
        }
        try {
            boolean hit=false;
            int make_cnt=0;
            int i=0,j=0,k=0;
            for (i=x1; i<=x2; i++) {
                for (j=y1; j<=y2; j++) {
                    for (k=z1; k<=z2; k++) {
                        Block block = new Location(loc1.getWorld(), i, j, k).getBlock();
                        if (block.getType().hasGravity()) continue;
                        if (block.getType() == set1) {
                            block.setType(set2);
                            make_cnt++;
                            hit = true;
                        }
                        if (make_cnt >= make_max) break;
                    }
                    if (make_cnt >= make_max) break;
                }
                if (make_cnt >= make_max) break;
            }
            if (!hit) {
                // finish
                end = true;
                task.cancel();
                PlayerUtils.BroadcastMessage(ChatColor.GREEN + String.format("[ %s ] さんの WorldEditが終了", player.getDisplayName()));
                SoundUtils.PlaySound(player,"cork-plug1", false);
                plugin.GetExecWE().remove(player);
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.we.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            end = true;
            task.cancel();
            plugin.GetExecWE().remove(player);
        }
    }

    private void ActionPaste() {
        if (set1 == null) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.we.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            end = true;
            task.cancel();
            if (blocks != null) {
                blocks.clear();
                blocks = null;
            }
            plugin.GetExecWE().remove(player);
            return;
        }
        if (firstTake) {
            plugin.GetExecWE().put(player, 1);
            PlayerUtils.BroadcastMessage(ChatColor.YELLOW + String.format("[ %s ] さんが [WorldEdit PASTE 開始] [ 範囲: %d ﾌﾞﾛｯｸ ]", player.getDisplayName(), count));
            SoundUtils.PlaySound(player, "cork-plug1", false);
            firstTake = false;
        }
        try {
            ArrayList<Block> blocks = new ArrayList<Block>();
            boolean hit=false;
            int make_cnt=0;
            int i=0,j=0,k=0;
            int num = 0;
            for (i=x1; i<=x2; i++) {
                for (j=y1; j<=y2; j++) {
                    for (k=z1; k<=z2; k++) {
                        Block block = new Location(loc1.getWorld(), i, j, k).getBlock();
                        Block pasteblock = blocks.get(num);
                        num++;
                        if (block.getType().hasGravity()) continue;
                        if (block.getType() != pasteblock.getType()) {
                            block.setType(pasteblock.getType());
                            make_cnt++;
                            hit = true;
                        }
                        if (make_cnt >= make_max) break;
                    }
                    if (make_cnt >= make_max) break;
                }
                if (make_cnt >= make_max) break;
            }
            if (!hit) {
                // finish
                end = true;
                task.cancel();
                if (blocks != null) {
                    blocks.clear();
                    blocks = null;
                }
                PlayerUtils.BroadcastMessage(ChatColor.GREEN + String.format("[ %s ] さんの WorldEditが終了", player.getDisplayName()));
                SoundUtils.PlaySound(player,"cork-plug1", false);
                plugin.GetExecWE().remove(player);
            }

        } catch (Exception ex) {
            plugin.getLogger().warning(ErrorUtils.GetErrorMessage(ex));
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.we.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            end = true;
            task.cancel();
            if (blocks != null) {
                blocks.clear();
                blocks = null;
            }
            plugin.GetExecWE().remove(player);
        }
    }
}
