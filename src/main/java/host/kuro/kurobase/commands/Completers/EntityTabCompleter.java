package host.kuro.kurobase.commands.Completers;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.SoundUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class EntityTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> cmds = new ArrayList<String>();
        switch (args.length) {
            case 1:
                cmds.add("アクションを選択");
                cmds.add("list");
                cmds.add("add");
                cmds.add("set");
                cmds.add("url");
                cmds.add("del");
                return StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<String>());
            case 2:
                if (args[0].toLowerCase().equals("add")) {
                    cmds.add("名前を入力");
                    return StringUtil.copyPartialMatches(args[1], cmds, new ArrayList<String>());
                }
                else if (args[0].toLowerCase().equals("set")) {
                    cmds.add("エンティティを選択");
                    return StringUtil.copyPartialMatches(args[1], cmds, new ArrayList<String>());
                }
                else if (args[0].toLowerCase().equals("url")) {
                    cmds.add("エンティティを選択");
                    return StringUtil.copyPartialMatches(args[1], cmds, new ArrayList<String>());
                }
                else if (args[0].toLowerCase().equals("del")) {
                    cmds.add("エンティティを選択");
                    return StringUtil.copyPartialMatches(args[1], cmds, new ArrayList<String>());
                }
            case 3:
                if (args[0].toLowerCase().equals("add")) {
                    cmds.add("タイプを選択");
                    return StringUtil.copyPartialMatches(args[2], cmds, new ArrayList<String>());
                }
                else if (args[0].toLowerCase().equals("set")) {
                    cmds.add("タイプを選択");
                    return StringUtil.copyPartialMatches(args[2], cmds, new ArrayList<String>());
                }
                else if (args[0].toLowerCase().equals("url")) {
                    cmds.add("スキンURLを入力");
                    return StringUtil.copyPartialMatches(args[2], cmds, new ArrayList<String>());
                }

            case 4:
                if (args[0].toLowerCase().equals("add")) {
                    cmds.add("スキンURLを入力");
                    return StringUtil.copyPartialMatches(args[3], cmds, new ArrayList<String>());
                }
        }
        return null;
    }
}
