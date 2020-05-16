package host.kuro.kurobase.commands;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class EntityCommand implements CommandExecutor {

    private KuroBase plugin;
    public EntityCommand(KuroBase plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
        if(!(sender instanceof Player)){
            // console
            plugin.getLogger().warning(Language.translate("plugin.console.error"));
            return false;
        }
        Player player = (Player)sender;
        // check city world
        if (!PlayerUtils.IsCityWorld(plugin, player)) {
            int rank = PlayerUtils.GetRank(plugin, player);
            if (rank < PlayerUtils.RANK_NUSHI) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                SoundUtils.PlaySound(player, "cancel5", false);
                return false;
            }
        }
        // check suvival world
        if (!PlayerUtils.IsSurvivalWorld(plugin, player)) {
            int rank = PlayerUtils.GetRank(plugin, player);
            if (rank < PlayerUtils.RANK_NUSHI) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.error.world"));
                SoundUtils.PlaySound(player, "cancel5", false);
                return false;
            }
        }

        switch(args[0].toLowerCase()) {
            case "list": return ActionList(player);
            case "add": return ActionAdd(player, args);
            case "set": return ActionSet(player, args);
            case "url": return ActionUrl(player, args);
            case "del": return ActionDel(player, args);
        }
        return true;
    }

    private boolean ActionList(Player player) {
        return true;
    }

    private boolean ActionAdd(Player player, String[] args) {
        // args check
        if (!(1 <= args.length && args.length <= 4)) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }

        NPC npc = CitizensAPI.getNPCRegistry().createNPC(EntityType.PLAYER, args[1]);
        //npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, new ItemStack(Material.LEATHER_BOOTS, 1));
        npc.spawn(player.getLocation());
        return true;
    }

    private boolean ActionSet(Player player, String[] args) {
        // args check
        if (args.length != 3) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionUrl(Player player, String[] args) {
        // args check
        if (args.length != 3) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }

    private boolean ActionDel(Player player, String[] args) {
        // args check
        if (args.length != 2) {
            player.sendMessage(ChatColor.DARK_RED + Language.translate("plugin.args.error"));
            SoundUtils.PlaySound(player,"cancel5", false);
            return false;
        }
        return true;
    }
}
