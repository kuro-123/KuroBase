package host.kuro.kurobase.shop;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ShopHandler {

    private static final List<ShopItem> shopItems = new ArrayList<ShopItem>();

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

    public static final void loadShop() {
        shopItems.clear();

        String name;
        int price;
        try {
            PreparedStatement ps = KuroBase.getDB().getConnection().prepareStatement(Language.translate("SQL.PRICE.SELECT"));
            ResultSet rs = KuroBase.getDB().ExecuteQuery(ps, null);
            if (rs != null) {
                while(rs.next()){
                    name = rs.getString("org_name");
                    price = rs.getInt("price");

                    Material m = Material.matchMaterial(name);
                    ItemStack stack = new ItemStack(m, 1);

                    double buy = price * 2.0d;
                    double sell = price;
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
        loadShop();
        shopItems.add(item);
    }

    public static final boolean removeItem(ItemStack stack) {
        loadShop();
        for(ShopItem item : shopItems) {
            if(item.getStack().getType().equals(stack.getType())) {
                if(item.getStack().getDurability() == stack.getDurability()) {
                    shopItems.remove(item);
                    return true;
                }
            }
        }
        return false;
    }

    public static final boolean buyItem(Player player, ItemStack stack, double cost) {
        // buy
        String a = "";
        /*
        if(ShopPlugin.getEconomy().getBalance(player) >= cost) {
            ShopPlugin.getEconomy().withdrawPlayer(player, cost);
            player.getInventory().addItem(stack);
            return true;
        }
        Util.chat(player, ShopPlugin.getPlugin().getConfig().getString("langNotEnoughMoney"));
        */
        return false;
    }

    public static final boolean sellItem(Player player, ItemStack stahck, double cost) {
        // sell
        String a = "";
        /*
        if(player.getInventory().containsAtLeast(stahck, stahck.getAmount())) {
            player.getInventory().removeItem(stahck);
            ShopPlugin.getEconomy().depositPlayer(player, cost);
            return true;
        }
        Util.chat(player, ShopPlugin.getPlugin().getConfig().getString("langNotEnoughItems"));
        */
        return false;
    }

    public static final ShopItem[] getShopItems() {
        return shopItems.toArray(new ShopItem[shopItems.size()]);
    }
}