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

public class AreaTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> cmds = new ArrayList<String>();
        switch (args.length) {
            case 1:
                cmds.add("add");
                cmds.add("del");
                return StringUtil.copyPartialMatches(args[0], cmds, new ArrayList<String>());
            case 2:
                if (args[0].toLowerCase().equals("del")) {
                    // regist list
                    boolean success = false;
                    try {
                        Player player = (Player)sender;
                        PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.AREA.NAME.LIST"));
                        ArrayList<DatabaseArgs> nargs = new ArrayList<DatabaseArgs>();
                        nargs.add(new DatabaseArgs("c", player.getUniqueId().toString()));
                        ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, nargs);
                        nargs.clear();
                        nargs = null;
                        if (rs != null) {
                            while(rs.next()){
                                cmds.add(rs.getString("name"));
                                success = true;
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
                    }
                    if (!success) {
                        cmds.add("登録されているエリアはありません");
                    }
                } else {
                    cmds.add("名前を入力<3～16文字>");
                }
                return StringUtil.copyPartialMatches(args[1], cmds, new ArrayList<String>());
        }
        return null;
    }
}
