package host.kuro.kurobase.shop;

import org.bukkit.inventory.ItemStack;

public class ShopItem {

    private ItemStack stack;
    private int buy;
    private int sell;
    private boolean init = false;

    public ShopItem(ItemStack stack, int buyPrice, int sellPrice) {
        this.stack = stack;
        this.stack.setAmount(1);
        if(this.stack != null) {
            this.buy = buyPrice;
            this.sell = sellPrice;
            init = true;
        }
    }

    public ItemStack getStack() { return this.stack; }
    public int getBuyPrice() { return this.buy; }
    public int getSellPrice() { return this.sell; }
    public boolean init() { return this.init; }
}