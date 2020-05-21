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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

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
        player.sendMessage(ChatColor.YELLOW + String.format("Lv %d [EXP: %d] <HP: %.2f / %.2f>", level, exp, health, maxhealth));
        SoundUtils.PlaySound(player,"switch1", false);
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    void onNPCRightClick(NPCRightClickEvent event) {
        NPC npc = event.getNPC();
        Player player = event.getClicker();
        if (npc.getTrait(BaseTypeTrait.class).getType().equals("BUDDYMASTER")) {
            int rank = PlayerUtils.GetRank(KuroBase.GetInstance(), player);
            if (rank < PlayerUtils.RANK_NUSHI) {
                if (player.getLevel() >= 200) {
                    ShopHandler.loadShop("の書", "npc");
                    GuiHandler.open(player, new GuiShop(player, 0));
                } else {
                    player.sendMessage(ChatColor.YELLOW + "ﾊﾞﾃﾞｨｰ取引をするにはLv200以上必要です！狩りをしましょう！");
                    SoundUtils.PlaySound(player, "cancel5", false);
                }
            } else {
                ShopHandler.loadShop("の書", "npc");
                GuiHandler.open(player, new GuiShop(player, 0));
            }
        } else if (npc.getTrait(BaseTypeTrait.class).getType().equals("CREATIVEMASTER")) {
            ShopHandler.loadShop("ｸﾘｴﾁｹｯﾄ", "npc");
            GuiHandler.open(player, new GuiShop(player, 0));
        } else if (npc.getTrait(BaseTypeTrait.class).getType().equals("EXPLANER")) {
            npc.getTrait(SendTextTrait.class).SendText(player, false);
        } else {
            double dhealth = npc.getTrait(KuroTrait.class).getHealth();
            dhealth = (Math.ceil(dhealth * 100.00D)) / 100.00D;
            double dmaxhealth = npc.getTrait(KuroTrait.class).getMaxHealth();
            dmaxhealth = (Math.ceil(dmaxhealth * 100.00D)) / 100.00D;
            if (dhealth < dmaxhealth) {
                ItemStack main_item = player.getInventory().getItemInMainHand();
                if (main_item != null) {
                    if (main_item.getType().isEdible()) {
                        int amount = main_item.getAmount();
                        amount--;
                        if (amount <= 0) {
                            player.getInventory().setItemInMainHand(new ItemStack(Material.AIR, 1));
                        } else {
                            main_item.setAmount(amount);
                            player.getInventory().setItemInMainHand(main_item);
                        }
                        // particle
                        ParticleUtils.CrownParticle(npc.getEntity(), Particle.HEART, 1); // particle
                        SoundUtils.BroadcastSound("menu2", false);
                        dhealth = dhealth + 1.0D;
                        if (dhealth > dmaxhealth) {
                            dhealth = dmaxhealth;
                        }
                        npc.getTrait(KuroTrait.class).setHealth(dhealth);
                        SendNpcInfo(npc, player);
                    } else {
                        player.sendMessage(ChatColor.YELLOW+"ﾊﾞﾃﾞｨｰに食べ物を渡すときは手に持ってください");
                        SoundUtils.PlaySound(player,"cancel5", false);
                    }
                } else {
                    player.sendMessage(ChatColor.YELLOW+"ﾊﾞﾃﾞｨｰに食べ物を渡すときは手に持ってください");
                    SoundUtils.PlaySound(player,"cancel5", false);
                }
            } else {
                player.sendMessage(ChatColor.YELLOW+"ﾊﾞﾃﾞｨｰの体力は満タンです");
                SoundUtils.PlaySound(player,"cancel5", false);
            }
        }
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
    }
}
