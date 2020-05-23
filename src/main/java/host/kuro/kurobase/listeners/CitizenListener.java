package host.kuro.kurobase.listeners;

import host.kuro.kurobase.KuroBase;
import host.kuro.kurobase.database.DatabaseArgs;
import host.kuro.kurobase.lang.Language;
import host.kuro.kurobase.shop.GuiHandler;
import host.kuro.kurobase.shop.GuiShop;
import host.kuro.kurobase.shop.ShopHandler;
import host.kuro.kurobase.trait.BaseTypeTrait;
import host.kuro.kurobase.trait.KuroTrait;
import host.kuro.kurobase.trait.SendTextTrait;
import host.kuro.kurobase.utils.ParticleUtils;
import host.kuro.kurobase.utils.PlayerUtils;
import host.kuro.kurobase.utils.SoundUtils;
import host.kuro.kurodiscord.DiscordMessage;
import net.citizensnpcs.api.event.*;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class CitizenListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCRemoved(NPCRemoveEvent event) {
        NPC npc = event.getNPC();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onTraitRemoved(NPCRemoveTraitEvent event) {
        NPC npc = event.getNPC();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCLeftClick(NPCLeftClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        if (npc.getTrait(BaseTypeTrait.class).getType().equals("BUDDY")) {
            SendNpcInfo(npc, player);
        }
    }

    private void SendNpcInfo(NPC npc, Player player) {
        int level = npc.getTrait(KuroTrait.class).getLevel();
        int exp = npc.getTrait(KuroTrait.class).getExp();
        double health = npc.getTrait(KuroTrait.class).getHealth();
        double maxhealth = npc.getTrait(KuroTrait.class).getMaxHealth();
        double mphealth = npc.getTrait(KuroTrait.class).getMpHealth();
        double mpmaxhealth = npc.getTrait(KuroTrait.class).getMaxMpHealth();
        player.sendMessage(ChatColor.YELLOW + String.format("Lv %d [EXP: %d] <HP: %.2f / %.2f> <MP: %.2f / %.2f>", level, exp, health, maxhealth, mphealth, mpmaxhealth));
        SoundUtils.PlaySound(player,"switch1", false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        if (npc.getTrait(BaseTypeTrait.class).getType().equals("BUDDYMASTER")) {
            ShopHandler.loadShop("", "npc");
            GuiHandler.open(player, new GuiShop(player, 0));

        } else if (npc.getTrait(BaseTypeTrait.class).getType().equals("EXPLANER")) {
            npc.getTrait(SendTextTrait.class).SendText(player, false);

        } else if (npc.getTrait(BaseTypeTrait.class).getType().equals("BUDDY")) {
            BuddyItem(npc, player);
        }
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCDeath(NPCDeathEvent event) {
        NPC npc = event.getNPC();
        if (!npc.getTrait(BaseTypeTrait.class).getType().equals("BUDDY")) return;

        // particle
        ParticleUtils.CrownParticle(npc.getEntity(), Particle.LAVA, 50); // particle
        SoundUtils.BroadcastSound("don-1", false);
        // message
        String message = String.format("[ %sさん ] の %s が死亡しました", npc.getTrait(KuroTrait.class).getOwner().getDisplayName(), npc.getName());
        PlayerUtils.BroadcastMessage(message, false);
        DiscordMessage dm = KuroBase.getDiscord().getDiscordMessage();
        if (dm != null) {
            dm.SendDiscordRedMessage(message);
        }

        Player owner = npc.getTrait(KuroTrait.class).getOwner();
        if (owner != null) {
            int rank = PlayerUtils.GetRank(KuroBase.GetInstance(), owner);
            if (rank == PlayerUtils.RANK_NUSHI) {
                // UPDATE
                ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
                eargs.add(new DatabaseArgs("c", npc.getUniqueId().toString())); // uuid
                int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.QUIT.ENTITY"), eargs);
                eargs.clear();
                eargs = null;
                return;
            }
        }
        // UPDATE
        ArrayList<DatabaseArgs> eargs = new ArrayList<DatabaseArgs>();
        eargs.add(new DatabaseArgs("c", npc.getUniqueId().toString())); // uuid
        int ret = KuroBase.getDB().ExecuteUpdate(Language.translate("SQL.UPDATE.DEATH.ENTITY"), eargs);
        eargs.clear();
        eargs = null;

        // NPC CLOSE
        npc.getTrait(KuroTrait.class).Close();
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCDamageByEntity(NPCDamageByEntityEvent event) {
        NPC npc = event.getNPC();
        // owner no damage
        Entity damager = event.getDamager();

        if (damager instanceof  Player) {
            Player p = (Player)damager;
            Player owner = npc.getTrait(KuroTrait.class).getOwner();
            if (owner != null) {
                if (p.getEntityId() == owner.getEntityId()) {
                    event.setCancelled(true);
                    return;
                }
            }
        }

        // emergency
        Player player = npc.getTrait(KuroTrait.class).getOwner();
        if (player != null) {
            double dhealth = npc.getTrait(KuroTrait.class).getHealth();
            dhealth = (Math.ceil(dhealth * 100.00D)) / 100.00D;

            double dmaxhealth = npc.getTrait(KuroTrait.class).getMaxHealth();
            dmaxhealth = (Math.ceil(dmaxhealth * 100.00D)) / 100.00D;

            double value = (dhealth / dmaxhealth) * 100.00D;
            if (value < 20) {
                SoundUtils.PlaySound(player,"emergency-alert1", false);
            }
        }

        // score update
        npc.getTrait(KuroTrait.class).setHealth(player.getHealth());
    }

    private boolean BuddyItem(NPC npc, Player player) {
        ItemStack stack = player.getInventory().getItemInMainHand();
        if (stack == null) return false;

        ItemMeta data = stack.getItemMeta();
        if (data == null) return false;

        String display = data.getDisplayName();
        if (display.length() <= 0) return false;

        ItemStack item = null;
        if (display.equals(Language.translate("shop.item.sword.wood"))) {
            item = new ItemStack(Material.WOODEN_SWORD, 1);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.sword.chain"))) {
            item = new ItemStack(Material.STONE_SWORD, 1);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 2);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.sword.iron"))) {
            item = new ItemStack(Material.IRON_SWORD, 1);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 3);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.sword.gold"))) {
            item = new ItemStack(Material.GOLDEN_SWORD, 1);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 4);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.sword.dia"))) {
            item = new ItemStack(Material.DIAMOND_SWORD, 1);
            item.addEnchantment(Enchantment.DAMAGE_ALL, 5);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HAND, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);

        } else if (display.equals(Language.translate("shop.item.helmet.wood"))) {
            item = new ItemStack(Material.LEATHER_HELMET, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.helmet.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_HELMET, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.helmet.iron"))) {
            item = new ItemStack(Material.IRON_HELMET, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.helmet.gold"))) {
            item = new ItemStack(Material.GOLDEN_HELMET, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.helmet.dia"))) {
            item = new ItemStack(Material.DIAMOND_HELMET, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.HELMET, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);

        } else if (display.equals(Language.translate("shop.item.chestplate.wood"))) {
            item = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.chestplate.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_CHESTPLATE, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.chestplate.iron"))) {
            item = new ItemStack(Material.IRON_CHESTPLATE, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.chestplate.gold"))) {
            item = new ItemStack(Material.GOLDEN_CHESTPLATE, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.chestplate.dia"))) {
            item = new ItemStack(Material.DIAMOND_CHESTPLATE, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.CHESTPLATE, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);

        } else if (display.equals(Language.translate("shop.item.leggins.wood"))) {
            item = new ItemStack(Material.LEATHER_LEGGINGS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.leggins.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_LEGGINGS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.leggins.iron"))) {
            item = new ItemStack(Material.IRON_LEGGINGS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.leggins.gold"))) {
            item = new ItemStack(Material.GOLDEN_LEGGINGS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.leggins.dia"))) {
            item = new ItemStack(Material.DIAMOND_LEGGINGS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.LEGGINGS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);

        } else if (display.equals(Language.translate("shop.item.boots.wood"))) {
            item = new ItemStack(Material.LEATHER_BOOTS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.boots.chain"))) {
            item = new ItemStack(Material.CHAINMAIL_BOOTS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.boots.iron"))) {
            item = new ItemStack(Material.IRON_BOOTS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.boots.gold"))) {
            item = new ItemStack(Material.GOLDEN_BOOTS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.boots.dia"))) {
            item = new ItemStack(Material.DIAMOND_BOOTS, 1);
            npc.getTrait(Equipment.class).set(Equipment.EquipmentSlot.BOOTS, item);
            // particle
            ParticleUtils.CrownParticle(npc.getEntity(), Particle.FALLING_WATER, 1); // particle
            SoundUtils.BroadcastSound("menu2", false);
        } else if (display.equals(Language.translate("shop.item.skill.heal.hp"))) {
            double dhealth = npc.getTrait(KuroTrait.class).getHealth();
            dhealth = (Math.ceil(dhealth * 100.00D)) / 100.00D;
            double dmaxhealth = npc.getTrait(KuroTrait.class).getMaxHealth();
            dmaxhealth = (Math.ceil(dmaxhealth * 100.00D)) / 100.00D;
            if (dhealth < dmaxhealth) {
                dhealth = dhealth + 1.0D;
                if (dhealth > dmaxhealth) {
                    dhealth = dmaxhealth;
                }
                npc.getTrait(KuroTrait.class).setHealth(dhealth);
                // particle
                ParticleUtils.CrownParticle(npc.getEntity(), Particle.HEART, 1); // particle
                SoundUtils.BroadcastSound("menu2", false);
            } else {
                player.sendMessage(ChatColor.YELLOW + "ﾊﾞﾃﾞｨｰのHPは満タンです");
                SoundUtils.PlaySound(player, "cancel5", false);
            }
            SendNpcInfo(npc, player);
        } else if (display.equals(Language.translate("shop.item.skill.heal.mp"))) {
            double dhealth = npc.getTrait(KuroTrait.class).getMpHealth();
            dhealth = (Math.ceil(dhealth * 100.00D)) / 100.00D;
            double dmaxhealth = npc.getTrait(KuroTrait.class).getMaxMpHealth();
            dmaxhealth = (Math.ceil(dmaxhealth * 100.00D)) / 100.00D;
            if (dhealth < dmaxhealth) {
                dhealth = dhealth + 1.0D;
                if (dhealth > dmaxhealth) {
                    dhealth = dmaxhealth;
                }
                npc.getTrait(KuroTrait.class).setMpHealth(dhealth);
                // particle
                ParticleUtils.CrownParticle(npc.getEntity(), Particle.HEART, 1); // particle
                SoundUtils.BroadcastSound("menu2", false);
            } else {
                player.sendMessage(ChatColor.YELLOW + "ﾊﾞﾃﾞｨｰのMPは満タンです");
                SoundUtils.PlaySound(player, "cancel5", false);
            }
            SendNpcInfo(npc, player);
        } else {
            SoundUtils.PlaySound(player, "cancel5", false);
            return false;
        }

        int amount = stack.getAmount();
        amount--;
        if (amount <= 0) {
            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
        } else {
            stack.setAmount(amount);
            player.getInventory().setItemInMainHand(stack);
        }
        return true;
    }
}
