package host.kuro.kurobase.utils;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.AreaData;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
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

    public static void ClickArea(KuroBase plugin, PlayerInteractEvent e, Player player, Block block) {
        if (plugin.GetAreaData().containsKey(player)) {
            String message;
            AreaData result = null;
            AreaData area = plugin.GetAreaData().get(player);
            switch (area.status) {
                case 0: // first point
                    area.world = block.getLocation().getWorld().getName();
                    area.x1 = block.getLocation().getBlockX();
                    area.y1 = block.getLocation().getBlockY();
                    area.z1 = block.getLocation().getBlockZ();
                    result = AreaUtils.CheckInsideProtect(null, area.x1, area.y1, area.z1);
                    if (result == null) {
                        area.status = 1;
                        plugin.GetAreaData().put(player, area);
                        message = String.format("1点目を設定しました！ [ %d,%d,%d ]", area.x1, area.y1, area.z1);
                        player.sendMessage(message);
                        SoundUtils.PlaySound(player, "kotsudumi1", false);
                    } else {
                        player.sendMessage(ChatColor.RED + String.format("指定したポイントは [ %s さん ] の保護 [ %s ] の範囲内でした", result.owner, result.name));
                        SoundUtils.PlaySound(player, "cancel5", false);
                        plugin.GetAreaData().remove(player);
                        plugin.GetClickMode().remove(player);
                    }
                    break;
                case 1: // second point
                    area.x2 = block.getLocation().getBlockX();
                    area.y2 = block.getLocation().getBlockY();
                    area.z2 = block.getLocation().getBlockZ();
                    result = AreaUtils.CheckInsideProtect(null, area.x2, area.y2, area.z2);
                    if (result == null) {
                        area.status = 2;
                        plugin.GetAreaData().put(player, area);
                        area = AreaUtils.ReplacePos(area);
                        // check money
                        int count = AreaUtils.GetAreaCount(area);
                        if (count < 512) {
                            player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.minimum.error"));
                            SoundUtils.PlaySound(player, "cancel5", false);
                        } else {
                            boolean money_throw = false;
                            if (PlayerUtils.IsCityWorld(plugin, player)) {
                                if (PlayerUtils.GetRank(plugin, player) >= PlayerUtils.RANK_KANRI) {
                                    money_throw = true;
                                }
                            }

                            int price = count * 2;
                            int money = PlayerUtils.GetMoney(KuroBase.getDB(), player);
                            if (!money_throw && money < price) {
                                player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.pay.monerror"));
                                SoundUtils.PlaySound(player, "cancel5", false);
                            } else {
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
                                args.add(new DatabaseArgs("c", player.getName())); // owner
                                int ret = plugin.getDB().ExecuteUpdate(Language.translate("SQL.AREA.INSERT"), args);
                                args.clear();
                                args = null;
                                if (ret != 1) {
                                    player.sendMessage(ChatColor.DARK_GREEN + Language.translate("commands.area.regist.error"));
                                    SoundUtils.PlaySound(player, "cancel5", false);
                                } else {
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
                                    message = String.format(ChatColor.GREEN + "エリア [ %s ] は保護されました [現在の所持金: %s p]", area.name, StringUtils.numFmt.format(money - price));
                                    player.sendMessage(message);
                                    SoundUtils.PlaySound(player, "kotsudumi1", false);
                                }
                            }
                        }
                    } else {
                        player.sendMessage(ChatColor.RED + String.format("指定したポイントは [ %s さん ] の保護 [ %s ] の範囲内でした", result.owner, result.name));
                        SoundUtils.PlaySound(player, "cancel5", false);
                    }
                    plugin.GetAreaData().remove(player);
                    plugin.GetClickMode().remove(player);
                    break;
            }
        }
        e.setCancelled(true);
    }
}
