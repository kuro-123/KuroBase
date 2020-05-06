package host.kuro.kurobase.commands.Completers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class PayTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> cmds = new ArrayList<String>();
        switch (args.length) {
            case 1:
                for (Player player : Bukkit.getOnlinePlayers()) {
                    cmds.add(player.getName());
                }
                return StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<String>());
            case 2:
                cmds.add("支払金額を入力");
                return StringUtil.copyPartialMatches(args[1], cmds, new ArrayList<String>());
        }
        return null;
    }
}
