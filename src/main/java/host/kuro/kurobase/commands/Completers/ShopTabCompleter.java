package host.kuro.kurobase.commands.Completers;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class ShopTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> cmds = new ArrayList<String>();
        switch (args.length) {
            case 1:
                for (Material mat : Material.values()) {
                    cmds.add(mat.name().toLowerCase());
                }
                return StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<String>());
        }
        return null;
    }
}
