package host.kuro.kurobase.utils;

import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class PlayerUtils {

    public static final String GetDisplayName(DatabaseManager db, Player player) {
        String disp_name = player.getName();
        String disp_tag = "";

        try {
            PreparedStatement ps = db.getConnection().prepareStatement(Language.translate("SQL.DISPLAY.PLAYER.NAME"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getUniqueId().toString()));
            ResultSet rs = db.ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    disp_name = rs.getString("dispname");
                    disp_tag = rs.getString("disptag");
                    break;
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

        if (disp_tag.length() > 0) {
            return disp_name + "\n" + disp_tag;
        } else {
            return disp_name;
        }
    }
}
