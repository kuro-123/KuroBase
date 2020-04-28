package host.kuro.kurobase.utils;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class ErrorUtils {

    public static String GetErrorMessage(Exception ex) {
        StackTraceElement[] ste = ex.getStackTrace();
        String buff = "";
        StringBuilder sb = new StringBuilder();
        for (StackTraceElement element: ste) {
            sb.append("[");
            sb.append(element);
            sb.append("]\n");
        }
        buff = (ex.getClass().getName() + ": "+ ex.getMessage() + " -> " + new String(sb));
        if (buff.length() > 1024) {
            buff = buff.substring(0, 1024);
        }
        String className = new Object(){}.getClass().getEnclosingClass().getName();
        String methodName = new Object(){}.getClass().getEnclosingMethod().getName();
        DatabaseManager db = KuroBase.getDB();
        if (db != null) {
            try {
                // INSERT
                ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
                args.add(new DatabaseArgs("c", className + ":" + methodName)); // place
                args.add(new DatabaseArgs("c", ex.getMessage())); // err_msg
                args.add(new DatabaseArgs("c", buff)); // result
                db.ExecuteUpdate(Language.translate("SQL.ERROR.INSERT"), args);
                args.clear();
                args = null;
            } catch (Exception err) {
            }
        }
        String message = (className + ":" + methodName + " -> " + buff);
        Bukkit.getLogger().warning(message);
        return message;
    }
}
