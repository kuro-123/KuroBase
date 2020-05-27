package host.kuro.kurobase.commands.Completers;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AiTabCompleter implements TabCompleter {

    List<String> cmds = new ArrayList<String>();

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        cmds.clear();
        Player player = (Player)sender;
        switch (args.length) {
            case 1:
                cmds.add("<アクションを選択>");
                cmds.add("buddymaster");
                cmds.add("explaner");
                cmds.add("weaponshop");
                cmds.add("armorshop");
                cmds.add("itemshop");
                cmds.add("specialshop");
                return StringUtil.copyPartialMatches(args[args.length-1], cmds, new ArrayList<String>());
        }
        return null;
    }
}