package host.kuro.kurobase.database;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;

public class DatabaseManager {
    private KuroBase plugin = null;
    private String sJdbc;
    private String shost;
    private String sPort;
    private String sDb;
    private String sUser;
    private String sPass;
    private Connection conn = null;

    public static final int ERR = -1;
    public static final int DUPLICATE = -2;

    public DatabaseManager(KuroBase plugin) {
        this.plugin = plugin;
        sJdbc = plugin.getConfig().getString("Database.Jdbc");
        shost = plugin.getConfig().getString("Database.Host");
        sPort = plugin.getConfig().getString("Database.Port");
        sDb   = plugin.getConfig().getString("Database.Database");
        sUser = plugin.getConfig().getString("Database.User");
        sPass = plugin.getConfig().getString("Database.Pass");
    }

    public Connection getConnection() {
        return conn;
    }

    public boolean Connect() {
        try{
            Class.forName("org.postgresql.Driver");
            conn = DriverManager.getConnection("jdbc:" + sJdbc + "://" + shost + ":" + sPort + "/" + sDb, sUser, sPass);
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        catch (Exception e){
            return false;
        }
        plugin.getLogger().info(Language.translate("plugin.db.connect"));
        return true;
    }

    public boolean DisConnect() {
        try{
            if(conn != null) {
                conn.close();
                conn = null;
            }
        }
        catch (SQLException e){
            e.printStackTrace();
            return false;
        }
        plugin.getLogger().info(Language.translate("plugin.db.disconnect"));
        return true;
    }

    public int ExecuteUpdate(String sql, ArrayList<DatabaseArgs> args) {
        int num = ERR;
        PreparedStatement ps = null;
        try {
            conn.setAutoCommit(false);
            ps = conn.prepareStatement(sql);
            if (args != null) {
                int i = 1;
                for(Iterator<DatabaseArgs> itr = args.iterator(); itr.hasNext();){
                    DatabaseArgs arg = itr.next();
                    switch (arg.type) {
                        case "c":
                            ps.setString(i, arg.value);
                            break;
                        case "i":
                            ps.setInt(i, Integer.parseInt(arg.value));
                            break;
                        case"t":
                            long long_buff = Long.parseLong(arg.value);
                            ps.setTimestamp(i, new Timestamp(long_buff));
                            break;
                        case"d":
                            ps.setDouble(i, Double.parseDouble(arg.value));
                            break;
                        case "b":
                            if (arg.value.toLowerCase().equals("true")) {
                                ps.setBoolean(i, true);
                            } else {
                                ps.setBoolean(i, false);
                            }
                            break;
                    }
                    i++;
                }
            }
            num = ps.executeUpdate();
            conn.commit();
        } catch (SQLException e) {
            if (e.getSQLState().equalsIgnoreCase("23505")) {
                // 重複エラー
                num = DUPLICATE;
            } else {
                plugin.getLogger().warning(Language.translate("plugin.db.error"));
                org.postgresql.jdbc.PgStatement stmt = (org.postgresql.jdbc.PgStatement)ps;
                plugin.getLogger().warning("SQL : " + stmt.toString());
                plugin.getLogger().warning("ERR_CD : " + e.getSQLState());
                plugin.getLogger().warning("ERR : " + e.getMessage());
                e.printStackTrace();
            }
            try {
                conn.rollback();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } catch (Exception e) {
            plugin.getLogger().warning(Language.translate("plugin.db.error"));
            org.postgresql.jdbc.PgStatement stmt = (org.postgresql.jdbc.PgStatement)ps;
            plugin.getLogger().warning("SQL : " + stmt.toString());
            e.printStackTrace();
            try {
                conn.rollback();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } finally {
            try {
                if (ps != null) {
                    ps.close();
                    ps = null;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return num;
    }

    public ResultSet ExecuteQuery(PreparedStatement ps, ArrayList<DatabaseArgs> args) {
        ResultSet rs = null;
        try {
            int i = 1;
            if (args != null) {
                for(Iterator<DatabaseArgs> itr = args.iterator(); itr.hasNext();){
                    DatabaseArgs arg = itr.next();
                    switch (arg.type) {
                        case "c":
                            ps.setString(i, arg.value);
                            break;
                        case "i":
                            ps.setInt(i, Integer.parseInt(arg.value));
                            break;
                        case"t":
                            long long_buff = Long.parseLong(arg.value);
                            ps.setTimestamp(i, new Timestamp(long_buff));
                            break;
                        case"d":
                            ps.setDouble(i, Double.parseDouble(arg.value));
                            break;
                        case "b":
                            if (arg.value.toLowerCase().equals("true")) {
                                ps.setBoolean(i, true);
                            } else {
                                ps.setBoolean(i, false);
                            }
                            break;
                    }
                    i++;
                }
            }
            rs = ps.executeQuery();
        } catch (SQLException e) {
            plugin.getLogger().warning(Language.translate("plugin.db.error"));
            org.postgresql.jdbc.PgStatement stmt = (org.postgresql.jdbc.PgStatement)ps;
            plugin.getLogger().warning("SQL : " + stmt.toString());
            plugin.getLogger().warning("ERR_CD : " + e.getSQLState());
            plugin.getLogger().warning("ERR : " + e.getMessage());
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            plugin.getLogger().warning(Language.translate("plugin.db.error"));
            org.postgresql.jdbc.PgStatement stmt = (org.postgresql.jdbc.PgStatement)ps;
            plugin.getLogger().warning("SQL : " + stmt.toString());
            e.printStackTrace();
            return null;
        }
        return rs;
    }
}
