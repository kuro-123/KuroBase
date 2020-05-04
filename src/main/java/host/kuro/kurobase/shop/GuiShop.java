package host.kuro.kurobase.shop;

import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.utils.ErrorUtils;
import host.kuro.kurobase.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class GuiShop implements IGuiScreen {

    private final int size = 6;
    private Player player;
    private int page;
    private GuiItem[] items;

    public GuiShop(Player player, int page) {
        this.player = player;
        this.page = page;
    }

    private String color(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private ItemStack loreItemStack(ItemStack stack, List<String> lore) {
        if(stack != null) {
            ItemMeta meta = stack.getItemMeta();
            for(int i = 0; i < lore.size(); i ++) { lore.set(i, color(lore.get(i))); }
            meta.setLore(lore);
            stack.setItemMeta(meta);
        }
        return stack;
    }

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

    private ItemStack nameItemStack(ItemStack stack, String name) {
        ItemMeta data = stack.getItemMeta();
        data.setDisplayName(color(name));
        stack.setItemMeta(data);
        return stack;
    }

    public void open() {
        try {
            Inventory inv = Bukkit.createInventory(this.player, 9 * size, ChatColor.WHITE + Language.translate("shop.title"));
            ShopItem[] item = ShopHandler.getShopItems();
            ItemStack[] toAdd = new ItemStack[item.length];
            items = new GuiItem[this.size * 9];

            int start = this.page * ((this.size - 1) * 9);
            int listSize = Math.min(9 * (this.size - 1), item.length - start);
            for(int num = 0; num < listSize; num ++) {
                ShopItem currentShopItem = item[start + num];
                toAdd[start + num] = currentShopItem.getStack().clone();

                List<String> lore = new ArrayList<String>();
                lore.add(ChatColor.WHITE+Language.translate("shop.title.buy") + ": " + StringUtils.numFmt.format(currentShopItem.getBuyPrice()) + "p");
                lore.add(ChatColor.GREEN+Language.translate("shop.title.sell") + ": " + StringUtils.numFmt.format(currentShopItem.getSellPrice()) + "p");
                loreItemStack(toAdd[start + num], lore);

                items[num] = new GuiItem(toAdd[start + num], (type) -> {
                    ItemStack toBuy = currentShopItem.getStack().clone();
                    toBuy.setAmount((type.isShiftClick()) ? 64 : 1);
                    if(type.isLeftClick()) {
                        ShopHandler.buyItem(this.player, toBuy, toBuy.getAmount() * currentShopItem.getBuyPrice());
                    } else if(type.isRightClick()) {
                        ShopHandler.sellItem(this.player, toBuy, toBuy.getAmount() * currentShopItem.getSellPrice());
                    }
                });
            }

            for(int i = 0; i < items.length; i ++) {
                GuiItem igi = items[i];
                if(igi != null) {
                    inv.setItem(i, igi.getStack());
                }
            }

            ItemStack backStack = fromString("BARRIER");
            ItemStack nextStack = fromString("ARROW");

            nameItemStack(backStack, Language.translate("shop.title.pageprev"));
            nameItemStack(nextStack, Language.translate("shop.title.pagenext"));

            GuiItem back = new GuiItem(backStack, (type) -> {
                GuiHandler.close(this.player);
                GuiHandler.open(this.player, new GuiShop(this.player, this.page - 1));
            });

            GuiItem forw = new GuiItem(nextStack, (type) -> {
                GuiHandler.close(this.player);
                GuiHandler.open(this.player, new GuiShop(this.player, this.page + 1));
            });

            items[items.length - 3] = back;
            items[items.length - 2] = forw;

            if(this.page > 0) inv.setItem(45, backStack);
            if(this.page < this.numberOfPages()) inv.setItem(53, nextStack);

            player.openInventory(inv);

        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    public int numberOfPages() {
        return (int) Math.ceil(ShopHandler.getShopItems().length / ((this.size - 1) * 9));
    }

    public void click(InventoryClickEvent event) {
        try {
            event.setCancelled(true);
            ItemStack stahck = event.getCurrentItem();
            for(GuiItem item : items) {
                if(item != null && item.getStack().equals(stahck)) {
                    item.click(event.getClick());
                    return;
                }
            }
        } catch (Exception ex) {
            ErrorUtils.GetErrorMessage(ex);
        }
    }

    public GuiItem[] getItems() {
        return this.items;
    }

    public ItemStack atPos(int x, int y) {
        return items[x + 9 * y].getStack();
    }

    public Player getOpener() {
        return player;
    }
}
