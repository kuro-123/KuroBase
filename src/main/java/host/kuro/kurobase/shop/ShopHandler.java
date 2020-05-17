package host.kuro.kurobase.shop;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.database.DatabaseManager;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurobase.utils.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ShopHandler {

    private static final List<ShopItem> shopItems = new ArrayList<ShopItem>();
    private static HashMap<Player, Long> shopUsed = new HashMap<Player, Long>();
    private static String keyword = "";

    private ItemStack fromString(String s) {
        int data = 0;
        String mat = s.trim();
        if(mat.contains(":")) {
            String[] spl = mat.split(":");
            mat = spl[0].trim();
            data = Integer.parseInt(spl[1].trim());
        }
        return new ItemStack(Material.getMaterial(mat), 1, (byte) data);
    }

    public static final void loadShop(String key) {
        keyword = key;
        shopItems.clear();

        String name;
        int price;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.PRICE.SELECT"));
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", "%" + keyword + "%"));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, args);
            args.clear();
            args = null;
            if (rs != null) {
                while(rs.next()){
                    name = rs.getString("org_name");
                    price = rs.getInt("price");

                    Material m;
                    ItemStack stack;
                    if (name.equals("復活の書")) {
                        m = Material.getMaterial("PAPER");
                        stack = new ItemStack(m, 1);
                        ItemMeta data = stack.getItemMeta();
                        data.setDisplayName("復活の書");
                        stack.setItemMeta(data);
                    } else {
                        m = Material.matchMaterial(name);
                        stack = new ItemStack(m, 1);
                    }

                    int buy = price;
                    int sell = price / 2;
                    ShopItem item = new ShopItem(stack, buy, sell);
                    if(item.init()) {
                        shopItems.add(item);
                    }
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

        } catch(Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    public static final void addItem(ShopItem item) {
        loadShop(keyword);
        shopItems.add(item);
    }

    public static final boolean removeItem(ItemStack stack) {
        try {
            loadShop(keyword);
            for(ShopItem item : shopItems) {
                if(item.getStack().getType().equals(stack.getType())) {
                    if(item.getStack().getDurability() == stack.getDurability()) {
                        shopItems.remove(item);
                        return true;
                    }
                }
            }
        } catch(Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return false;
    }

    public static final boolean buyItem(Player player, ItemStack stack, int cost) {
        // Countermeasure against repeated hits
        try {
            if (!shopUsed.containsKey(player)) {
                shopUsed.put(player, System.currentTimeMillis());
            }
            long used_time = shopUsed.get(player);
            long now_time = System.currentTimeMillis();
            if ((now_time - used_time) < 500) return false;
            shopUsed.put(player, System.currentTimeMillis());

            if (cost <= 0) return false;
            int suryo = stack.getAmount();
            if (suryo <= 0) return false;
            int money = PlayerUtils.GetMoney(KuroBase.getDB(), player);
            int price = cost * suryo;
            if (money < price) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("shop.error.shortage"));
                SoundUtils.PlaySound(player,"incorrect2", false);
                return false;
            }
            // pay
            int ret = PlayerUtils.PayMoney(KuroBase.getDB(), player, price);
            if (ret != 1) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("shop.buy.fail"));
                SoundUtils.PlaySound(player,"incorrect2", false);
                return false;
            }
            AddLogPay(player, "BUY", 0-price, stack.getType().name());

            // add item
            player.getInventory().addItem(stack);
            String str_suryo = StringUtils.numFmt.format(suryo);
            String str_zankin = StringUtils.numFmt.format(money-price);
            String message = String.format(Language.translate("shop.buy.success"), stack.getType().name(), str_suryo, str_zankin);
            player.sendMessage(ChatColor.BLUE + message);
            SoundUtils.PlaySound(player,"amount-display1", false);

        } catch(Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return false;
    }

    public static final boolean sellItem(Player player, ItemStack stack, int cost) {
        try {
            // Countermeasure against repeated hits
            if (!shopUsed.containsKey(player)) {
                shopUsed.put(player, System.currentTimeMillis());
            }
            long used_time = shopUsed.get(player);
            long now_time = System.currentTimeMillis();
            if ((now_time - used_time) < 500) return false;
            shopUsed.put(player, System.currentTimeMillis());

            if (cost <= 0) return false;
            int money = PlayerUtils.GetMoney(KuroBase.getDB(), player);
            String search = stack.getType().name().toLowerCase();

            int ret = 0;
            int total_price = 0;
            int total_amount = 0;
            PlayerInventory inv = player.getInventory();
            for(int i = 0; i < 36; i++){
                ItemStack item = inv.getItem(i);
                if (item == null) continue;
                if(item.getType()!=null) {
                    String itemName = inv.getItem(i).getType().name();
                    if (itemName.toLowerCase().equals(search)) {
                        int suryo = item.getAmount();
                        int price = cost * suryo;
                        ret = PlayerUtils.AddMoney(KuroBase.getDB(), player, price);
                        if (ret == 1) {
                            total_amount += suryo;
                            total_price += price;
                            inv.setItem(i, new ItemStack(Material.AIR, 1));
                        }
                    }
                }
            }
            if (total_price <= 0) {
                player.sendMessage(ChatColor.DARK_RED + Language.translate("shop.sell.none"));
                SoundUtils.PlaySound(player,"incorrect2", false);
                return false;
            }
            AddLogPay(player, "SELL", total_price, search);

            String str_suryo = StringUtils.numFmt.format(total_amount);
            String str_price = StringUtils.numFmt.format(total_price);
            String str_zankin = StringUtils.numFmt.format(money+total_price);
            String message = String.format(Language.translate("shop.sell.success"), search, str_suryo, str_price, str_zankin);
            player.sendMessage(ChatColor.BLUE + message);
            SoundUtils.PlaySound(player,"amount-display1", false);

        } catch(Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return false;
    }

    public static final int AddLogPay(Player player, String kind, int pay, String item_name) {
        int ret = 0;
        try {
            ArrayList<DatabaseArgs> args = new ArrayList<DatabaseArgs>();
            args.add(new DatabaseArgs("c", player.getName())); // src
            args.add(new DatabaseArgs("c", "SHOP")); // dst
            args.add(new DatabaseArgs("c", kind)); // kind
            args.add(new DatabaseArgs("i", ""+pay)); // price
            args.add(new DatabaseArgs("c", item_name)); // result
            ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.INSERT.LOG.PAY"), args);
            args.clear();
            args = null;
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
        return ret;
    }

    public static final ShopItem[] getShopItems() {
        return shopItems.toArray(new ShopItem[shopItems.size()]);
    }
}