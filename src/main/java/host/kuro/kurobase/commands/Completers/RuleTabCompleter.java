package host.kuro.kurobase.commands.Completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class RuleTabCompleter implements TabCompleter {

    List<String> cmds = new ArrayList<String>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        cmds.clear();
        Player player = (Player)sender;
        switch (args.length) {
            case 1:
                cmds.add("<ルールの種別を選択>");
                cmds.add("stoptime");
                cmds.add("firetick");
                return StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<String>());
            case 2:
                if (args[0].toLowerCase().equals("stoptime")) {
                    cmds.add("ON");
                    cmds.add("OFF");
                    return StringUtil.copyPartialMatches(args[1], cmds, new ArrayList<String>());
                }
        }
        return null;
    }
}